import java.util.List;

/**
 * Утилиты для работы с JSON (простая реализация без внешних библиотек)
 */
public class JsonUtils {
    
    public static String toJson(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(task.getId()).append(",");
        sb.append("\"title\":\"").append(escapeJson(task.getTitle())).append("\",");
        sb.append("\"description\":\"").append(escapeJson(task.getDescription())).append("\",");
        sb.append("\"completed\":").append(task.isCompleted());
        sb.append("}");
        return sb.toString();
    }
    
    public static String toJsonArray(List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(tasks.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }
    
    public static Task fromJson(String json) {
        Task task = new Task();
        
        // Простой парсинг JSON
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON format");
        }
        
        // Удаляем фигурные скобки
        json = json.substring(1, json.length() - 1).trim();
        
        // Парсим поля более аккуратно
        int pos = 0;
        while (pos < json.length()) {
            // Находим ключ
            int keyStart = json.indexOf('"', pos);
            if (keyStart == -1) break;
            int keyEnd = json.indexOf('"', keyStart + 1);
            if (keyEnd == -1) break;
            String key = json.substring(keyStart + 1, keyEnd);
            
            // Находим двоеточие
            int colonPos = json.indexOf(':', keyEnd);
            if (colonPos == -1) break;
            
            // Находим значение
            int valueStart = colonPos + 1;
            while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
                valueStart++;
            }
            
            String value;
            if (valueStart < json.length() && json.charAt(valueStart) == '"') {
                // Строковое значение
                int valueEnd = valueStart + 1;
                while (valueEnd < json.length()) {
                    if (json.charAt(valueEnd) == '"' && json.charAt(valueEnd - 1) != '\\') {
                        break;
                    }
                    valueEnd++;
                }
                value = unescapeJson(json.substring(valueStart + 1, valueEnd));
            } else {
                // Числовое или булево значение
                int valueEnd = valueStart;
                while (valueEnd < json.length() && json.charAt(valueEnd) != ',' && json.charAt(valueEnd) != '}') {
                    valueEnd++;
                }
                value = json.substring(valueStart, valueEnd).trim();
            }
            
            // Устанавливаем значение
            if (key.equals("id")) {
                try {
                    task.setId(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    // Игнорируем, если не число
                }
            } else if (key.equals("title")) {
                task.setTitle(value);
            } else if (key.equals("description")) {
                task.setDescription(value);
            } else if (key.equals("completed")) {
                task.setCompleted(Boolean.parseBoolean(value));
            }
            
            // Переходим к следующему полю
            pos = json.indexOf(',', valueStart);
            if (pos == -1) break;
            pos++;
        }
        
        return task;
    }
    
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    private static String unescapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\\"", "\"")
                  .replace("\\\\", "\\")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t");
    }
    
    public static String errorJson(String message) {
        return "{\"error\":\"" + escapeJson(message) + "\"}";
    }
    
    public static String messageJson(String message) {
        return "{\"message\":\"" + escapeJson(message) + "\"}";
    }
}

