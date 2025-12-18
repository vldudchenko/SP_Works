import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Простой REST API сервер
 * Реализует методы: GET, POST, PUT, DELETE
 * Домен: Управление задачами (To-Do List)
 */
public class RestApiServer {
    private static final int PORT = 8080;
    private static final String CONTEXT = "/api/tasks";
    private TaskService taskService;
    
    public RestApiServer() {
        taskService = TaskService.getInstance();
    }
    
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Создаем контекст для всех методов
        server.createContext(CONTEXT, this::handleRequest);
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("========================================");
        System.out.println("  REST API Server запущен");
        System.out.println("========================================");
        System.out.println("URL: http://localhost:" + PORT + CONTEXT);
        System.out.println("Методы: GET, POST, PUT, DELETE");
        System.out.println("========================================\n");
    }
    
    private void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        
        // Обработка OPTIONS для CORS
        if ("OPTIONS".equals(method)) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            return;
        }
        
        String path = exchange.getRequestURI().getPath();
        
        // Извлекаем ID из пути, если есть
        String[] pathParts = path.split("/");
        Integer id = null;
        if (pathParts.length > 3) {
            try {
                id = Integer.parseInt(pathParts[3]);
            } catch (NumberFormatException e) {
                // ID не является числом
            }
        }
        
        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, id);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "PUT":
                    handlePut(exchange, id);
                    break;
                case "DELETE":
                    handleDelete(exchange, id);
                    break;
                default:
                    sendResponse(exchange, 405, JsonUtils.errorJson("Method Not Allowed"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, JsonUtils.errorJson("Internal Server Error: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/tasks - получить все задачи
     * GET /api/tasks/{id} - получить задачу по ID
     */
    private void handleGet(HttpExchange exchange, Integer id) throws IOException {
        if (id == null) {
            // Получить все задачи
            List<Task> tasks = taskService.getAllTasks();
            String response = JsonUtils.toJsonArray(tasks);
            sendResponse(exchange, 200, response);
        } else {
            // Получить задачу по ID
            Task task = taskService.getTaskById(id);
            if (task != null) {
                String response = JsonUtils.toJson(task);
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 404, JsonUtils.errorJson("Task not found"));
            }
        }
    }
    
    /**
     * POST /api/tasks - создать новую задачу
     */
    private void handlePost(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange);
        
        try {
            Task task = JsonUtils.fromJson(requestBody);
            
            if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
                sendResponse(exchange, 400, JsonUtils.errorJson("Title is required"));
                return;
            }
            
            Task createdTask = taskService.addTask(task);
            String response = JsonUtils.toJson(createdTask);
            sendResponse(exchange, 201, response);
        } catch (Exception e) {
            sendResponse(exchange, 400, JsonUtils.errorJson("Invalid JSON: " + e.getMessage()));
        }
    }
    
    /**
     * PUT /api/tasks/{id} - обновить задачу
     */
    private void handlePut(HttpExchange exchange, Integer id) throws IOException {
        if (id == null) {
            sendResponse(exchange, 400, JsonUtils.errorJson("Task ID is required"));
            return;
        }
        
        if (!taskService.taskExists(id)) {
            sendResponse(exchange, 404, JsonUtils.errorJson("Task not found"));
            return;
        }
        
        String requestBody = readRequestBody(exchange);
        
        try {
            Task task = JsonUtils.fromJson(requestBody);
            
            if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
                sendResponse(exchange, 400, JsonUtils.errorJson("Title is required"));
                return;
            }
            
            Task updatedTask = taskService.updateTask(id, task);
            String response = JsonUtils.toJson(updatedTask);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 400, JsonUtils.errorJson("Invalid JSON: " + e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/tasks/{id} - удалить задачу
     */
    private void handleDelete(HttpExchange exchange, Integer id) throws IOException {
        if (id == null) {
            sendResponse(exchange, 400, JsonUtils.errorJson("Task ID is required"));
            return;
        }
        
        if (taskService.deleteTask(id)) {
            sendResponse(exchange, 200, JsonUtils.messageJson("Task deleted successfully"));
        } else {
            sendResponse(exchange, 404, JsonUtils.errorJson("Task not found"));
        }
    }
    
    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
    
    public static void main(String[] args) {
        try {
            RestApiServer server = new RestApiServer();
            server.start();
            
            System.out.println("Сервер работает. Нажмите Enter для остановки...");
            System.in.read();
            
        } catch (IOException e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

