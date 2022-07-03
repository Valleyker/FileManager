package manager;

import alltasks.Epic;
import info.Status;
import alltasks.Subtask;
import alltasks.Task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Integer id = 0;
    protected final HashMap<Integer, Task> taskStorage = new HashMap<>();
    protected final HashMap<Integer, Epic> epicTaskStorage = new HashMap<>();
    protected final HashMap<Integer, Subtask> subTaskStorage = new HashMap<>();
    protected final HistoryManager history;
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

    public InMemoryTaskManager() {
        this.history = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public Integer calcId() {
        id = id + 1;
        return id;
    }

    @Override
    public void putTask(Task task) {
        if (task != null) {
            TreeSet<Task> prioritizedTasks = getPrioritizedTasks();
            boolean isNotOverlapping = checkTimeOverlapping(task);
            if (isNotOverlapping || prioritizedTasks.isEmpty()) {
                Integer id = calcId();
                task.setId(id);
                taskStorage.put(id, task);
                prioritizedTasks.add(task);
            } else {
                throw new IllegalArgumentException("Обнаружено пересечение задачи: " + task.getName() +
                        ". Задача не была добавлена.");
            }
        } else {
            System.out.println("Ошибка ввода task");
        }
    }

    @Override
    public void putEpicTask(Epic epic) {
        if (epic != null) {
            Integer id = calcId();
            epic.setId(id);
            epicTaskStorage.put(id, epic);
        } else {
            System.out.println("Ошибка ввода epictask");
        }
    }

    @Override
    public void putSubTask(Epic epic, Subtask subtask) {
        if (epic != null && subtask != null) {
            TreeSet<Task> prioritizedTasks = getPrioritizedTasks();
            boolean isNotOverlapping = checkTimeOverlapping(subtask);
            if (isNotOverlapping || prioritizedTasks.isEmpty()) {
                Integer id = calcId();
                subtask.setId(id);
                subTaskStorage.put(id, subtask);
                ArrayList<Integer> subTaskListId;
                subTaskListId = epic.getSubTaskListId();
                subTaskListId.add(subtask.getId());
                epic.setSubTaskListId(subTaskListId);
                subtask.setEpicTaskId(epic.getId());
                prioritizedTasks.add(subtask);
                checkEpicStatusStartEndTimeDuration(epic);
            } else {
                throw new IllegalArgumentException("Обнаружено пересечение задачи: " + subtask.getName() +
                        ". Задача не была добавлена.");
            }
        } else {
            System.out.println("Ошибка ввода epictask и subtask");
        }
    }

    @Override
    public void restoreSubTask(Epic epic, Subtask subtask) {
        if (epic != null && subtask != null) {
            TreeSet<Task> prioritizedTasks = getPrioritizedTasks();
            boolean isNotOverlapping = checkTimeOverlapping(subtask);
            if (isNotOverlapping || prioritizedTasks.isEmpty()) {
                subTaskStorage.put(subtask.getId(), subtask);
                ArrayList<Integer> subTaskListId;
                subTaskListId = epic.getSubTaskListId();
                subTaskListId.add(subtask.getId());
                epic.setSubTaskListId(subTaskListId);
                subtask.setEpicTaskId(epic.getId());
                prioritizedTasks.add(subtask);
                checkEpicStatusStartEndTimeDuration(epic);
            } else {
                throw new IllegalArgumentException("Обнаружено пересечение задачи: " + subtask.getName() +
                        ". Задача не была добавлена.");
            }
        } else {
            System.out.println("Ошибка ввода epictask и subtask");
        }
    }

    boolean checkTimeOverlapping(Task task) {
        boolean isNotOverlapping = false;
        TreeSet<Task> prioritizedTasks = getPrioritizedTasks();
        if (task.getStartTime() == null) {
            isNotOverlapping = true;
        } else {
            for (Task elem : prioritizedTasks) {
                if (elem.getStartTime() == null) {
                    continue;
                }
                if (elem.getId().equals(task.getId())) {
                    isNotOverlapping = true;
                    continue;
                }
                if (task.getStartTime().equals(elem.getStartTime())) {
                    isNotOverlapping = false;
                    break;
                }
                if (!task.getStartTime().isBefore(elem.getEndTime()) ||
                        !task.getEndTime().isAfter(elem.getStartTime())) {
                    isNotOverlapping = true;
                } else {
                    isNotOverlapping = false;
                    break;
                }
            }
        }
        return isNotOverlapping;
    }

    private void checkEpicStatusStartEndTimeDuration(Epic epic) {
        if (checkEpicStatus(epic.getId()) != null) {
            epic.setStatus(checkEpicStatus(epic.getId()));
            epic.setStartTime(setEpicStartTime(epic.getId()));
            epic.setEndTime(setEpicEndTime(epic.getId()));
            epic.setDuration(setEpicDuration(epic.getId()));
        } else {
            System.out.println("Невозможно обновить статус epic, epic отсутствует в epicTaskStorage");
        }
    }

    @Override
    public Status checkEpicStatus(int id) {
        Status status = null;
        if (epicTaskStorage.containsKey(id)) {
            ArrayList<Integer> subTaskIdList = epicTaskStorage.get(id).getSubTaskListId(); // определяем список subtask
            if (subTaskIdList.isEmpty()) { // проверка на наличие subtask у объекта epic
                status = Status.NEW;
            } else {
                int countNewStatus = 0;
                int countDoneStatus = 0;
                for (int idSubtask : subTaskIdList) {
                    if (subTaskStorage.get(idSubtask).getStatus().equals(Status.NEW)) {
                        countNewStatus++;
                    } else if (subTaskStorage.get(idSubtask).getStatus().equals(Status.DONE)) {
                        countDoneStatus++;
                    }
                }
                if (countNewStatus == subTaskIdList.size()) {  // проверка если все subtask имеют статус new
                    status = Status.NEW;
                } else if (countDoneStatus == subTaskIdList.size()) { // проверка если все subtask имеют статус done
                    status = Status.DONE;
                } else {
                    status = Status.IN_PROGRESS;
                }
            }
        }
        return status;
    }

    @Override
    public LocalDateTime setEpicStartTime(int id) {
        LocalDateTime earliestSubtaskStart = LocalDateTime.of(3000, 1, 1, 12, 30);
        if (epicTaskStorage.containsKey(id)) {
            ArrayList<Integer> subTaskIdList = epicTaskStorage.get(id).getSubTaskListId();
            if (!subTaskIdList.isEmpty()) {
                if (subTaskIdList.size() == 1 && subTaskStorage.get(subTaskIdList.get(0)).getStartTime() == null) {
                    earliestSubtaskStart = null;
                } else {
                    for (int idSubtask : subTaskIdList) {
                        if (subTaskStorage.get(idSubtask).getStartTime() == null) {
                            continue;
                        }
                        if (subTaskStorage.get(idSubtask).getStartTime().isBefore(earliestSubtaskStart)) {
                            earliestSubtaskStart = subTaskStorage.get(idSubtask).getStartTime();
                        }
                    }
                }
            }
        }
        return earliestSubtaskStart;
    }

    @Override
    public LocalDateTime setEpicEndTime(int id) {
        LocalDateTime latestSubtaskEnd = LocalDateTime.of(1900, 1, 1, 12, 30);
        if (epicTaskStorage.containsKey(id)) {
            ArrayList<Integer> subTaskIdList = epicTaskStorage.get(id).getSubTaskListId();
            if (!subTaskIdList.isEmpty()) {
                if (subTaskIdList.size() == 1 && subTaskStorage.get(subTaskIdList.get(0)).getStartTime() == null) {
                    latestSubtaskEnd = null;
                } else {
                    for (int idSubtask : subTaskIdList) {
                        if (subTaskStorage.get(idSubtask).getStartTime() == null) {
                            continue;
                        }
                        if (subTaskStorage.get(idSubtask).getStartTime().isAfter(latestSubtaskEnd)) {
                            latestSubtaskEnd = subTaskStorage.get(idSubtask).getEndTime();
                        }
                    }
                }
            }
        }
        return latestSubtaskEnd;
    }

    @Override
    public long setEpicDuration(int id) {
        if (epicTaskStorage.get(id).getStartTime() == null || epicTaskStorage.get(id).getEndTime() == null) {
            return 0;
        } else {
            LocalDateTime start = epicTaskStorage.get(id).getStartTime();
            LocalDateTime end = epicTaskStorage.get(id).getEndTime();
            return ChronoUnit.MINUTES.between(start, end);
        }
    }

    @Override
    public Task getTaskByID(int id) {
        Task searchedTask = null;
        if (taskStorage.containsKey(id)) {
            searchedTask = taskStorage.get(id);
            history.add(taskStorage.get(id));
        } else if (subTaskStorage.containsKey(id)) {
            searchedTask = subTaskStorage.get(id);
            history.add(subTaskStorage.get(id));
        } else if (epicTaskStorage.containsKey(id)) {
            searchedTask = epicTaskStorage.get(id);
            history.add(epicTaskStorage.get(id));
        }
        return searchedTask;
    }

    @Override
    public HashMap<Integer, Task> getSubTasksByEpicID(int id) {
        HashMap<Integer, Task> searchedHashMap = new HashMap<>();
        ArrayList<Integer> listSubTasks;
        if (epicTaskStorage.containsKey(id)) {
            listSubTasks = epicTaskStorage.get(id).getSubTaskListId(); // получаем список subtask для нужного epictask ID
            for (int key : subTaskStorage.keySet()) { // последовательно проходимся по мапе subtask в поисках ключей
                if (listSubTasks.contains(key)) {
                    searchedHashMap.put(key, subTaskStorage.get(key));
                }
            }
        }
        return searchedHashMap;
    }

    @Override
    public void updateTask(Task task) {
        if (task != null) {
            TreeSet<Task> prioritizedTasks = getPrioritizedTasks();
            boolean isNotOverlapping = checkTimeOverlapping(task);
            if (isNotOverlapping || prioritizedTasks.isEmpty()) {
                prioritizedTasks.remove(task);
                taskStorage.put(task.getId(), task);
                prioritizedTasks.add(task);
            } else {
                throw new IllegalArgumentException("Обнаружено пересечение задачи: " + task.getName() +
                        ". Задача не была добавлена.");
            }
        } else {
            System.out.println("Ошибка ввода");
        }
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        if (subtask != null) {
            TreeSet<Task> prioritizedTasks = getPrioritizedTasks();
            boolean isNotOverlapping = checkTimeOverlapping(subtask);
            if (isNotOverlapping || prioritizedTasks.isEmpty()) {
                prioritizedTasks.remove(subtask);
                int epicTaskId = subTaskStorage.get(subtask.getId()).getEpicTaskId(); // определяем id эпика изменяемого сабтаска
                subTaskStorage.put(subtask.getId(), subtask); // добавляем в мапу subtask (перезаписывая старый subtask)
                subtask.setEpicTaskId(epicTaskId); // устанавливаем ID для epic, к которому относится subtask
                prioritizedTasks.add(subtask);
                checkEpicStatusStartEndTimeDuration(epicTaskStorage.get(subtask.getEpicTaskId()));
            } else {
                throw new IllegalArgumentException("Обнаружено пересечение задачи: " + subtask.getName() +
                        ". Задача не была добавлена.");
            }
        } else {
            System.out.println("Ошибка ввода");
        }
    }

    @Override
    public void updateEpicTask(Epic epic) {
        if (epic != null) {
            ArrayList<Integer> idTransfer = epicTaskStorage.get(epic.getId()).getSubTaskListId();
            epic.setSubTaskListId(idTransfer);
            epicTaskStorage.put(epic.getId(), epic);
            for (Map.Entry<Integer, Subtask> entry : subTaskStorage.entrySet()) {
                for (Integer idSub : idTransfer) {
                    if (entry.getKey().equals(idSub)) {
                        entry.getValue().setEpicTaskId(epic.getId());
                    }
                }
            }
            if (checkEpicStatus(epic.getId()) != null) {
                epicTaskStorage.get(epic.getId()).setStatus(checkEpicStatus(id));
            } else {
                System.out.println("Невозможно обновить статус epic, epic отсутствует в epicTaskStorage");
            }
        } else {
            System.out.println("Ошибка ввода");
        }
    }

    @Override
    public void deleteTask(Task task) {
        if (task != null) {
            removeFromHistory(task.getId());
            taskStorage.remove(task.getId());
            prioritizedTasks.remove(task);
        } else {
            System.out.println("Ошибка ввода");
        }
    }

    @Override
    public void deleteSubTask(Subtask subtask) {
        if (subtask != null) {
            int idEpic = subTaskStorage.get(subtask.getId()).getEpicTaskId();
            epicTaskStorage.get(idEpic).getSubTaskListId().removeIf(n -> (n.equals(subtask.getId())));
            removeFromHistory(subtask.getId());
            subTaskStorage.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
            checkEpicStatusStartEndTimeDuration( epicTaskStorage.get(idEpic));
        } else {
            System.out.println("Ошибка ввода");
        }
    }

    @Override
    public void deleteEpicTask(Epic epic) {
        if (epic != null) {
            ArrayList<Integer> subtaskListDelete = epic.getSubTaskListId();
            for (int idSubtask : subtaskListDelete) {
                removeFromHistory(idSubtask);
                prioritizedTasks.remove(subTaskStorage.get(idSubtask));
                subTaskStorage.remove(idSubtask);
            }
            removeFromHistory(epic.getId());
            epic.getSubTaskListId().clear();
            epicTaskStorage.remove(epic.getId());
        } else {
            System.out.println("Ошибка ввода");
        }
    }


    @Override
    public void deleteTaskStorage() {
        for (Map.Entry<Integer, Task> entry : taskStorage.entrySet()) {
            removeFromHistory(entry.getKey());
            prioritizedTasks.remove(entry.getValue());
        }
        taskStorage.clear();
    }

    @Override
    public void deleteEpicTaskStorage() {
        for (Map.Entry<Integer, Subtask> entry : subTaskStorage.entrySet()) {
            removeFromHistory(entry.getKey());
            prioritizedTasks.remove(entry.getValue());
        }
        for (Map.Entry<Integer, Epic> entry : epicTaskStorage.entrySet()) {
            removeFromHistory(entry.getKey());
        }
        subTaskStorage.clear();
        for (Map.Entry<Integer, Epic> entry : epicTaskStorage.entrySet()) {
            entry.getValue().getSubTaskListId().clear();
            prioritizedTasks.remove(entry.getValue());
        }
        epicTaskStorage.clear();
    }


    @Override
    public void deleteSubTaskStorage() {
        for (Map.Entry<Integer, Subtask> entry : subTaskStorage.entrySet()) {
            removeFromHistory(entry.getKey());
            prioritizedTasks.remove(entry.getValue());
        }
        for (Map.Entry<Integer, Epic> entry : epicTaskStorage.entrySet()) {
            entry.getValue().getSubTaskListId().clear();
            entry.getValue().setDuration(0);
            entry.getValue().setStartTime(null);
            entry.getValue().setEndTime(null);
            entry.getValue().setStatus(checkEpicStatus(entry.getKey()));
        }
        subTaskStorage.clear();
    }

    @Override
    public void deleteAllTasks() {
        removeAllHistory();
        deleteTaskStorage();
        deleteSubTaskStorage();
        deleteEpicTaskStorage();
        prioritizedTasks.clear();
        id = 0;
    }


    @Override
    public LinkedHashMap<Integer, Task> getAllTasks() {
        LinkedHashMap<Integer, Task> combinedMap = new LinkedHashMap<>();
        combinedMap.putAll(taskStorage);
        combinedMap.putAll(epicTaskStorage);
        combinedMap.putAll(subTaskStorage);
        return combinedMap;
    }

    @Override
    public HashMap<Integer, Task> getTaskStorage() {
        return taskStorage;
    }

    @Override
    public HashMap<Integer, Subtask> getSubTaskStorage() {
        return subTaskStorage;
    }

    @Override
    public HashMap<Integer, Epic> getEpicTaskStorage() {
        return epicTaskStorage;
    }

    @Override
    public void removeFromHistory(int id) {
        history.remove(id);
    }

    @Override
    public void removeAllHistory() {
        HashMap<Integer, Task> combinedMap = getAllTasks();
        for (Map.Entry<Integer, Task> entry : combinedMap.entrySet()) {
            history.remove(entry.getKey());
        }
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }
}
