import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Конечный клиент с графическим интерфейсом
 * Подключается к промежуточному клиенту по TCP и получает последние 5 сообщений
 */
public class EndClient extends JFrame {
    private static final String INTERMEDIATE_HOST = "localhost";
    private static final int TCP_PORT = 1503;
    
    private JTextArea messagesArea;
    private JButton refreshButton;
    private JLabel statusLabel;
    
    public EndClient() {
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Клиент - Сообщения из файла");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        // Создаем компоненты
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Панель статуса
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Готов к подключению");
        statusPanel.add(statusLabel);
        mainPanel.add(statusPanel, BorderLayout.NORTH);
        
        // Область для сообщений
        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        messagesArea.setBackground(Color.BLACK);
        messagesArea.setForeground(Color.GREEN);
        JScrollPane scrollPane = new JScrollPane(messagesArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Последние сообщения из файла"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout());
        refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> fetchMessages());
        
        JButton exitButton = new JButton("Выход");
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(exitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Автоматически загружаем сообщения при запуске
        SwingUtilities.invokeLater(() -> fetchMessages());
        
        // Автоматическое обновление каждые 5 секунд
        Timer timer = new Timer(5000, e -> fetchMessages());
        timer.start();
    }
    
    /**
     * Получает сообщения от промежуточного клиента
     */
    private void fetchMessages() {
        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Подключение к промежуточному клиенту...");
                    refreshButton.setEnabled(false);
                });
                
                Socket socket = new Socket(INTERMEDIATE_HOST, TCP_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                
                // Читаем количество сообщений
                int messageCount = Integer.parseInt(in.readLine());
                
                List<String> messages = new ArrayList<>();
                for (int i = 0; i < messageCount; i++) {
                    String message = in.readLine();
                    if (message != null) {
                        messages.add(message);
                    }
                }
                
                socket.close();
                
                // Обновляем GUI
                SwingUtilities.invokeLater(() -> {
                    updateMessagesDisplay(messages);
                    statusLabel.setText("Получено сообщений: " + messages.size() + " | Последнее обновление: " + new java.util.Date());
                    refreshButton.setEnabled(true);
                });
                
            } catch (ConnectException e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Ошибка: Промежуточный клиент не доступен. Убедитесь, что он запущен.");
                    messagesArea.setText("Не удалось подключиться к промежуточному клиенту.\n" +
                                       "Убедитесь, что IntermediateClient запущен на " + INTERMEDIATE_HOST + ":" + TCP_PORT);
                    refreshButton.setEnabled(true);
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Ошибка: " + e.getMessage());
                    messagesArea.setText("Ошибка при получении сообщений: " + e.getMessage());
                    refreshButton.setEnabled(true);
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Обновляет отображение сообщений
     */
    private void updateMessagesDisplay(List<String> messages) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("         ПОСЛЕДНИЕ СООБЩЕНИЯ ИЗ ФАЙЛА\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        
        if (messages.isEmpty()) {
            sb.append("Нет доступных сообщений.\n");
            sb.append("Промежуточный клиент еще не получил сообщения от сервера.\n");
        } else {
            for (int i = 0; i < messages.size(); i++) {
                sb.append("[").append(i + 1).append("] ").append(messages.get(i)).append("\n");
            }
        }
        
        sb.append("\n═══════════════════════════════════════════════════════\n");
        sb.append("Обновлено: ").append(new java.util.Date()).append("\n");
        
        messagesArea.setText(sb.toString());
    }
    
    public static void main(String[] args) {
        // Устанавливаем Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            EndClient client = new EndClient();
            client.setVisible(true);
        });
    }
}

