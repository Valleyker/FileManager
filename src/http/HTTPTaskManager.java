package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import manager.HistoryManager;
import alltasks.Epic;
import alltasks.Subtask;
import alltasks.Task;
import manager.Managers;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.isNull;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient taskClient;
    private static final String KEY_TASKS = "tasks";
    private static final String KEY_SUBTASKS = "subtasks";
    private static final String KEY_EPICS = "epics";
    private static final String KEY_HISTORY = "history";

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HTTPTaskManager(Boolean checkLoad) {
        super("/Users/valleyker/Desktop/httpTasks.csv");
        taskClient = new KVTaskClient(KVServer.getServerURL());
        if (checkLoad) {
            load();
        }
    }

    @Override
    public void save() {
        if (!isNull(getTaskStorage())) {
            taskClient.put(KEY_TASKS, gson.toJson(getTaskStorage(), new TypeToken<Map<Integer,
                    Task>>() {
            }.getType()));
        }
        if (!isNull(getEpicTaskStorage())) {
            taskClient.put(KEY_EPICS, gson.toJson(getEpicTaskStorage(), new TypeToken<Map<Integer,
                    Epic>>() {
            }.getType()));
        }
        if (!isNull(getSubTaskStorage())) {
            taskClient.put(KEY_SUBTASKS, gson.toJson(getSubTaskStorage(), new TypeToken<Map<Integer,
                    Subtask>>() {
            }.getType()));
        }
        if (!getHistory().isEmpty()) {
            taskClient.put(KEY_HISTORY, historyToString(Managers.getDefaultHistory()));
        }
    }

    public void load() {
        Map<Integer, Task> tasks = gson.fromJson(taskClient.load(KEY_TASKS), new TypeToken<Map<Integer, Task>>() {
        }.getType());
        for (Task task : tasks.values()) {
            taskStorage.put(task.getId(), task);
        }
        Map<Integer, Epic> epics = gson.fromJson(taskClient.load(KEY_EPICS), new TypeToken<Map<Integer, Epic>>() {
        }.getType());
        for (Epic epic : epics.values()) {
            epicTaskStorage.put(epic.getId(), epic);
        }
        Map<Integer, Subtask> subtasks = gson.fromJson(taskClient.load(KEY_SUBTASKS), new TypeToken<Map<Integer,
                Subtask>>() {
        }.getType());
        for (Subtask subtask : subtasks.values()) {
            subTaskStorage.put(subtask.getId(), subtask);
        }

        String[] historyFromServer = taskClient.load(KEY_HISTORY).trim().split(",");
        for (String number : historyFromServer) {
            if (!number.isBlank()) {
                getTaskByID(Integer.parseInt(number));
            }
        }

        for (Map.Entry<Integer, Task> entry : getTaskStorage().entrySet()) {
            getPrioritizedTasks().add(entry.getValue());
        }
        for (Map.Entry<Integer, Subtask> entry : getSubTaskStorage().entrySet()) {
            getPrioritizedTasks().add(entry.getValue());
        }
        id = 0;
        List<Integer> idCount = new ArrayList<>();
        Map<Integer, Task> allTasks = getAllTasks();
        for (Map.Entry<Integer, Task> entry : allTasks.entrySet()) {
            idCount.add(entry.getKey());
        }
        for (Integer elem : idCount) {
            if (elem > id) {
                id = elem;
            }
        }
    }

    static String historyToString(HistoryManager manager) {
        List<String> list = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            list.add(Integer.toString(task.getId()));
        }
        return String.join(",", list);
    }
}