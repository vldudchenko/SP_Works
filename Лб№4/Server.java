import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

/**
 * UDP Multicast Server
 * Отправляет сообщения о погоде каждые 10 секунд всем клиентам в группе 233.0.0.1:1502
 */
public class Server {
    private static final String MULTICAST_GROUP = "233.0.0.1";
    private static final int PORT = 1502;
    private static final int INTERVAL = 10000; // 10 секунд
    private static final String FILE = "text.txt";
    
    public static void main(String[] args) {
        try {
            // Создаем multicast socket
            MulticastSocket socket = new MulticastSocket();
            InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
            
            System.out.println("Сервер запущен. Группа: " + MULTICAST_GROUP + ":" + PORT);
            System.out.println("Чтение сообщений из файла: " + FILE);
            
            // Читаем сообщения из файла
            List<String> messages = readMessagesFromFile(FILE);
            if (messages.isEmpty()) {
                System.out.println("Ошибка: файл " + FILE + " пуст или не найден!");
                System.exit(1);
            }
            
            System.out.println("Загружено " + messages.size() + " сообщений");
            System.out.println("Начинаю отправку сообщений каждые 10 секунд...\n");
            
            int messageIndex = 0;
            
            // Отправляем сообщения в цикле
            while (true) {
                String message = messages.get(messageIndex);
                byte[] buffer = message.getBytes("UTF-8");
                
                DatagramPacket packet = new DatagramPacket(
                    buffer, 
                    buffer.length, 
                    group, 
                    PORT
                );
                
                socket.send(packet);
                
                System.out.println("[" + new Date() + "] Отправлено: " + message);
                
                // Переходим к следующему сообщению (циклически)
                messageIndex = (messageIndex + 1) % messages.size();
                
                // Ждем 10 секунд
                Thread.sleep(INTERVAL);
            }
            
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Поток прерван: " + e.getMessage());
        }
    }
    
    /**
     * Читает сообщения из файла
     * Каждая строка - отдельное сообщение
     */
    private static List<String> readMessagesFromFile(String filename) {
        List<String> messages = new ArrayList<>();
        
        try {
            Path filePath = Paths.get(filename);
            if (Files.exists(filePath)) {
                messages = Files.readAllLines(filePath, java.nio.charset.StandardCharsets.UTF_8);
                // Удаляем пустые строки
                messages.removeIf(String::isEmpty);
            } else {
                System.out.println("Файл " + filename + " не найден.");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
        
        return messages;
    }
}

