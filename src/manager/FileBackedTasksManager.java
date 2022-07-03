package manager;


import alltasks.*;
import info.Status;
import info.Type;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static info.Type.SUBTASK;
import static info.Type.valueOf;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path pathToSave;
    private static final String HEADER = "ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC,START TIME,DURATION, END TIME\n";

    public FileBackedTasksManager(String file) {
        this.pathToSave = Paths.get(file);
    }

    @Override
    public void putTask(Task task) {
        super.putTask(task);
        save();
    }

    @Override
    public void putEpicTask(Epic epic) {
        super.putEpicTask(epic);
        save();
    }

    @Override
    public void putSubTask(Epic epic, Subtask subtask) {
        super.putSubTask(epic, subtask);
        save();
    }

    @Override
    public void restoreSubTask(Epic epic, Subtask subtask) {
        super.restoreSubTask(epic, subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public Task getTaskByID(int id) {
        HashMap<Integer, Task> searchedHashMap = new HashMap<>();
        if (taskStorage.containsKey(id)) {
            searchedHashMap.put(id, taskStorage.get(id));
            history.add(taskStorage.get(id));
        } else if (subTaskStorage.containsKey(id)) {
            searchedHashMap.put(id, subTaskStorage.get(id));
            history.add(subTaskStorage.get(id));
        } else if (epicTaskStorage.containsKey(id)) {
            searchedHashMap.put(id, epicTaskStorage.get(id));
            history.add(epicTaskStorage.get(id));
        }
        save();
        return searchedHashMap.get(id);
    }

    @Override
    public void removeFromHistory(int id) {
        super.removeFromHistory(id);
        save();
    }

    @Override
    public void updateEpicTask(Epic epic) {
        super.updateEpicTask(epic);
        save();
    }

    @Override
    public void deleteTask(Task task) {
        super.deleteTask(task);
        save();
    }

    @Override
    public void deleteSubTask(Subtask subtask) {
        super.deleteSubTask(subtask);
        save();
    }

    @Override
    public void deleteEpicTask(Epic epic) {
        super.deleteEpicTask(epic);
        save();
    }

    @Override
    public void deleteTaskStorage() {
        super.deleteTaskStorage();
        save();
    }

    @Override
    public void deleteEpicTaskStorage() {
        super.deleteEpicTaskStorage();
        save();
    }

    @Override
    public void deleteSubTaskStorage() {
        super.deleteSubTaskStorage();
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }


    public void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(pathToSave), false))) {
            writer.write(HEADER);
            Map<Integer, Task> allTasks = getAllTasks();
            allTasks.forEach(((integer, task) -> {
                try {
                    writer.write(toString(task));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
            writer.newLine();
            writer.write(toString(history));
        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно сохранить файл", e.getCause());
        }
    }

    private String toString(Task task) {
        String epicOfSubtask = "";
        if (task.getTaskType() == SUBTASK) {
            epicOfSubtask += ((Subtask) task).getEpicTaskId();
        }
        Stream<Object> fields = Stream.builder()
                .add(task.getId())
                .add(task.getTaskType())
                .add(task.getName())
                .add(task.getStatus())
                .add(task.getInfo())
                .add(epicOfSubtask)
                .add(task.getStartTime())
                .add(task.getDuration())
                .add(task.getEndTime())
                .build();
        return fields.map(String::valueOf).collect(Collectors.joining(",", "", "\n"));
    }

    FileBackedTasksManager loadFromFile(File file) throws IOException {
        FileBackedTasksManager restoredManager = new FileBackedTasksManager(file.getPath());
        List<String> fileLines = Files.readAllLines(Path.of(file.getPath()));
        if (fileLines.size() == 3) {
            addTask(restoredManager, fileLines.get(1));
        } else {
            for (int i = 1; i < (fileLines.size()); i++) {
                if (i <= (fileLines.size() - 2)) {
                    if (!fileLines.get(i).isEmpty()) {
                        addTask(restoredManager, fileLines.get(i));
                    }
                } else if (i == (fileLines.size() - 1)) {
                    if (!fileLines.get(i).isEmpty()) {
                        List<Integer> history = fromStringHistory(fileLines.get(i));
                        for (Integer elem : history) {
                            restoredManager.getTaskByID(elem);
                        }
                    }
                }
            }
        }
        id = 0;
        for (int i = 1; i < (fileLines.size()); i++) {
            if (i < fileLines.size()-2) {
                String[] line = fileLines.get(i).trim().split(",");
                if (Integer.parseInt(line[0]) > id) {
                    id = Integer.parseInt(line[0]);
                }
            }
        }
        return restoredManager;
    }

    private static void addTask(FileBackedTasksManager manager, String str) {
        String[] line = str.trim().split(",");
        Task task = taskFromString(str.trim());
        Type type = Objects.requireNonNull(task).getTaskType();
        int id = task.getId();
        switch (type) {
            case TASK -> {
                manager.taskStorage.put(Integer.parseInt(line[0].trim()), task);
                manager.taskStorage.get(id).setStatus(setStatus(line[3].trim()));
            }
            case EPIC -> manager.epicTaskStorage.put(Integer.parseInt(line[0].trim()), (Epic) task);
            case SUBTASK -> {
                Epic epic = manager.epicTaskStorage.get(Integer.parseInt(line[5].trim()));
                task.setStatus(setStatus(line[3].trim()));
                manager.restoreSubTask(epic, (Subtask) task);
            }
        }
    }

    public static Task taskFromString(String value) {
        String[] line = value.trim().split(",");
        Type type = valueOf(line[1].trim());
        Task task = null;
        int id = Integer.parseInt(line[0].trim());
        String name = line[2].trim();
        String description = line[4].trim();
        LocalDateTime startTime;
        if (line[6].equals("null")) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(line[6]);
        }
        long duration = Long.parseLong(line[7]);
        switch (type) {
            case TASK -> task = new Task(id, name, description, startTime, duration);
            case EPIC -> task = new Epic(id, name, description, startTime, duration);
            case SUBTASK -> task = new Subtask(id, name, description, startTime, duration);
            default -> System.out.println("Отсутствует тип задачи TASK, SUBTASK, EPIC");
        }
        return task;
    }

    private static Status setStatus(String status) {
        if (status.equals("NEW")) {
            return Status.NEW;
        } else if (status.equals("IN_PROGRESS")) {
            return Status.IN_PROGRESS;
        } else {
            return Status.DONE;
        }
    }

    private static String toString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        ArrayList<String> historyId = new ArrayList<>();
        for (Task elem : history) {
            historyId.add(String.valueOf(elem.getId()));
        }
        return String.join(",", historyId);
    }

    private static List<Integer> fromStringHistory(String value) {
        String[] id = value.trim().split(",");
        List<Integer> historyId = new ArrayList<>();
        for (String elem : id) {
            historyId.add(Integer.parseInt(elem));
        }
        return historyId;
    }
}

