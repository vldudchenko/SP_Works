import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Сервис для управления задачами
 * Хранит задачи в памяти
 */
public class TaskService {
    private static TaskService instance;
    private Map<Integer, Task> tasks;
    private AtomicInteger nextId;
    
    private TaskService() {
        tasks = new ConcurrentHashMap<>();
        nextId = new AtomicInteger(1);
        
        // Добавляем несколько примеров задач
        addTask(new Task(1, "Изучить REST API", "Изучить основы REST API и HTTP методы", false));
        addTask(new Task(2, "Создать проект", "Создать новый проект для лабораторной работы", false));
        addTask(new Task(3, "Написать документацию", "Написать README для проекта", true));
    }
    
    public static TaskService getInstance() {
        if (instance == null) {
            instance = new TaskService();
        }
        return instance;
    }
    
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    
    public Task getTaskById(int id) {
        return tasks.get(id);
    }
    
    public Task addTask(Task task) {
        if (task.getId() == 0) {
            task.setId(nextId.getAndIncrement());
        }
        tasks.put(task.getId(), task);
        return task;
    }
    
    public Task updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            task.setId(id);
            tasks.put(id, task);
            return task;
        }
        return null;
    }
    
    public boolean deleteTask(int id) {
        return tasks.remove(id) != null;
    }
    
    public boolean taskExists(int id) {
        return tasks.containsKey(id);
    }
}

