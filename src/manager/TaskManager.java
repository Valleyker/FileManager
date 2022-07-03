package manager;

import alltasks.Epic;
import info.Status;
import alltasks.Subtask;
import alltasks.Task;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    List<Task> getHistory();

    Integer calcId();

    void putTask(Task task);

    void putEpicTask(Epic epic);

    void putSubTask(Epic epic, Subtask subtask);

    Task getTaskByID(int id);

    HashMap<Integer, Task> getSubTasksByEpicID(int id);

    void updateTask(Task task);

    void updateSubTask(Subtask subtask);

    void updateEpicTask(Epic epic);

    void restoreSubTask(Epic epic, Subtask subtask);

    Status checkEpicStatus(int id);

    LocalDateTime setEpicStartTime(int id);

    LocalDateTime setEpicEndTime(int id);

    long setEpicDuration(int id);

    void deleteTask(Task task);

    void deleteSubTask(Subtask subtask);

    void deleteEpicTask(Epic epic);

    void deleteTaskStorage();

    void deleteEpicTaskStorage();

    void deleteSubTaskStorage();

    void deleteAllTasks();

    HashMap<Integer, Task> getAllTasks();

    HashMap<Integer, Task> getTaskStorage();

    HashMap<Integer, Subtask> getSubTaskStorage();

    HashMap<Integer, Epic> getEpicTaskStorage();

    void removeFromHistory(int id);

    void removeAllHistory();

    TreeSet<Task> getPrioritizedTasks();
}
