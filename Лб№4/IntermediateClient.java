import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Промежуточный клиент
 * - Получает UDP multicast сообщения от сервера
 * - Фильтрует сообщения (показывает только если изменилось)
 * - Хранит последние 5 сообщений
 * - Работает как TCP сервер для конечных клиентов
 */
public class IntermediateClient {
    private static final String MULTICAST_GROUP = "233.0.0.1";
    private static final int UDP_PORT = 1502;
    private static final int TCP_PORT = 1503;
    private static final int MAX_MESSAGES = 5;
    
    private List<String> lastMessages = new ArrayList<>();
    private String lastReceivedMessage = "";
    private boolean running = true;
    
    public static void main(String[] args) {
        IntermediateClient client = new IntermediateClient();
        client.start();
    }
    
    public void start() {
        // Запускаем TCP сервер в отдельном потоке
        Thread tcpServerThread = new Thread(this::startTCPServer);
        tcpServerThread.setDaemon(true);
        tcpServerThread.start();
        
        System.out.println("Промежуточный клиент запущен");
        System.out.println("UDP Multicast: " + MULTICAST_GROUP + ":" + UDP_PORT);
        System.out.println("TCP Server: localhost:" + TCP_PORT);
        System.out.println("Ожидание сообщений...\n");
        
        // Получаем UDP multicast сообщения
        receiveUDPMessages();
    }
    
    /**
     * Получает UDP multicast сообщения от сервера
     */
    private void receiveUDPMessages() {
        try {
            MulticastSocket socket = new MulticastSocket(UDP_PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
            socket.joinGroup(group);
            
            byte[] buffer = new byte[1024];
            
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                String message = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                
                // Фильтруем: показываем только если сообщение изменилось
                if (!message.equals(lastReceivedMessage)) {
                    lastReceivedMessage = message;
                    
                    // Добавляем в список последних сообщений
                    synchronized (lastMessages) {
                        lastMessages.add(message);
                        // Оставляем только последние MAX_MESSAGES сообщений
                        if (lastMessages.size() > MAX_MESSAGES) {
                            lastMessages.remove(0);
                        }
                    }
                    
                    System.out.println("[" + new Date() + "] Получено новое сообщение: " + message);
                    System.out.println("Всего сохранено сообщений: " + lastMessages.size());
                } else {
                    System.out.println("[" + new Date() + "] Получено повторное сообщение (отфильтровано): " + message);
                }
            }
            
            socket.leaveGroup(group);
            socket.close();
            
        } catch (IOException e) {
            System.err.println("Ошибка при получении UDP сообщений: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Запускает TCP сервер для конечных клиентов
     */
    private void startTCPServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(TCP_PORT);
            System.out.println("TCP сервер запущен на порту " + TCP_PORT);
            
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Подключен конечный клиент: " + clientSocket.getRemoteSocketAddress());
                
                // Обрабатываем клиента в отдельном потоке
                new Thread(() -> handleEndClient(clientSocket)).start();
            }
            
        } catch (IOException e) {
            System.err.println("Ошибка TCP сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Обрабатывает запрос от конечного клиента
     */
    private void handleEndClient(Socket clientSocket) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            // Отправляем последние 5 сообщений
            synchronized (lastMessages) {
                out.println(lastMessages.size()); // Количество сообщений
                
                for (String message : lastMessages) {
                    out.println(message);
                }
            }
            
            System.out.println("Отправлено " + lastMessages.size() + " сообщений конечному клиенту");
            
            clientSocket.close();
            
        } catch (IOException e) {
            System.err.println("Ошибка при обработке конечного клиента: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

