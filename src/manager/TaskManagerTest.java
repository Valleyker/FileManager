package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import alltasks.Epic;
import info.Status;
import alltasks.Subtask;
import alltasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

abstract class TaskManagerTest<T extends TaskManager> {
    private final T taskManager;

    TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }

    @Test
    @DisplayName("Add new task")
    void addNewTask_TEST() {
        Task task = new Task(1,"testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        taskManager.putTask(task);
        Assertions.assertNotNull(task, "Задача не найдена");
        Assertions.assertEquals(task, taskManager.getTaskStorage().get(task.getId()), "Задачи не совпадают");
        final HashMap<Integer, Task> tasks = taskManager.getTaskStorage();
        Assertions.assertNotNull(tasks, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач");
        taskManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Add new subtask")
    void addNewSubTask_TEST() {
        Epic epic = new Epic(2, "testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Subtask subtask = new Subtask(3,"testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        taskManager.putEpicTask(epic);
        taskManager.putSubTask(epic, subtask);
        Assertions.assertNotNull(subtask, "Задача не найдена");
        Assertions.assertNotNull(epic, "Задача не найдена");
        Assertions.assertEquals(subtask, taskManager.getSubTaskStorage().get(subtask.getId()), "Задачи не совпадают");
        Assertions.assertEquals(epic, taskManager.getEpicTaskStorage().get(epic.getId()), "Задачи не совпадают");
        final HashMap<Integer, Subtask> tasks = taskManager.getSubTaskStorage();
        final HashMap<Integer, Epic> epics = taskManager.getEpicTaskStorage();
        Assertions.assertNotNull(subtask, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач");
        Assertions.assertNotNull(epic, "Задачи не возвращаются");
        Assertions.assertEquals(1, epics.size(), "Неверное количество задач");
        taskManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Get task by id")
    void getTaskById_TEST() {
        Task task = new Task(taskManager.calcId(), "testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Epic epic = new Epic(taskManager.calcId(), "testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        Subtask subtask = new Subtask(taskManager.calcId(), "testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 17, 19, 30), 120);
        taskManager.putTask(task);
        taskManager.putEpicTask(epic);
        taskManager.putSubTask(epic, subtask);
        Assertions.assertEquals(taskManager.getTaskByID(task.getId()), task, "Неверный возврат task");
        Assertions.assertEquals(taskManager.getTaskByID(epic.getId()), epic, "Неверный возврат epic");
        Assertions.assertEquals(taskManager.getTaskByID(subtask.getId()), subtask, "Неверный возврат subtask");
        taskManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Get SubTasks By Epic ID")
    void getSubTasksByEpicID_TEST() {
        Epic epic = new Epic(taskManager.calcId(), "testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        taskManager.putEpicTask(epic);
        HashMap<Integer, Task> subtasks1 = taskManager.getSubTasksByEpicID(epic.getId());
        Assertions.assertEquals(subtasks1.size(), 0, "Список сабтасков не пустой");
        Subtask subtask = new Subtask(taskManager.calcId(), "testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 17, 19, 30), 120);
        taskManager.putSubTask(epic, subtask);
        HashMap<Integer, Task> subtasks2 = taskManager.getSubTasksByEpicID(epic.getId());
        Assertions.assertEquals(subtasks2.size(), 1, "Список сабтасков не пустой");
        Assertions.assertEquals(subtasks2.get(subtask.getId()), subtask, "В списке неверный subtask");
        taskManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Update Task by ID")
    void updateTask_TEST() {
        Task task1 = new Task(1,"testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        taskManager.putTask(task1);
        Task task2 = new Task(task1.getId(),"testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        task2.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task2);
        Assertions.assertEquals(taskManager.getTaskStorage().get(task1.getId()).getStatus(),
                Status.IN_PROGRESS, "Неправильно обновляется статус");
        taskManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Update SubTask by ID")
    void updateSubTask_TEST() {
        Epic epic = new Epic(2,"testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        Subtask subtask1 = new Subtask(3, "testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        taskManager.putEpicTask(epic);
        taskManager.putSubTask(epic, subtask1);
        Subtask subtask2 = new Subtask(subtask1.getId(), "testName2", "testDescr2",
                LocalDateTime.of(2022, 5, 16, 19, 30), 200);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subtask2);
        Assertions.assertEquals(taskManager.getSubTaskStorage().get(subtask1.getId()).getStatus(),
                Status.IN_PROGRESS, "Неправильно обновляется статус subtask");
        Assertions.assertEquals(taskManager.getEpicTaskStorage().get(epic.getId()).getStatus(),
                Status.IN_PROGRESS, "Неправильно обновляется статус epic");
        taskManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Update Epic by ID")
    void updateEpicTask_TEST() {
        Epic epic1 = new Epic(2, "Epic1", "EpicDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        Subtask subtask1 = new Subtask(3, "Sub1", "SubDescr1",
                LocalDateTime.of(2022, 5, 17, 19, 30), 120);
        Subtask subtask2 = new Subtask(4, "Sub2", "SubDescr2",
                LocalDateTime.of(2022, 5, 18, 19, 30), 120);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask1);
        taskManager.putSubTask(epic1, subtask2);
        Epic epic2 = new Epic(epic1.getId(), "Epic2", "EpicDescr2",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        taskManager.updateEpicTask(epic2);
        Assertions.assertEquals(taskManager.getEpicTaskStorage().get(epic2.getId()).getName(), epic2.getName(),
                "Неправильно обновляется название epic");
        Assertions.assertEquals(taskManager.getEpicTaskStorage().get(epic2.getId()).getInfo(), epic2.getInfo(),
                "Неправильно обновляется описание epic");
        Assertions.assertEquals(taskManager.getEpicTaskStorage().get(epic2.getId()).getSubTaskListId().size(), 2,
                "Неправильно обновляется список subtask");
        Assertions.assertEquals(taskManager.getSubTaskStorage().get(taskManager.getEpicTaskStorage().get(epic2.getId()).
                getSubTaskListId().get(0)), subtask1, "Неверная привязка subtask");
        taskManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Delete Task")
    void deleteTask_TEST() {
        Task task1 = new Task(taskManager.calcId(), "testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        taskManager.putTask(task1);
        taskManager.deleteTask(task1);
        Assertions.assertEquals(taskManager.getTaskStorage().size(), 0, "Задача task не удалилась");
        taskManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Delete Epic")
    void deleteEpic_TEST() {
        Epic epic1 = new Epic(taskManager.calcId(), "Epic1", "EpicDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        Subtask subtask1 = new Subtask(taskManager.calcId(), "Sub1", "SubDescr1",
                LocalDateTime.of(2022, 5, 17, 19, 30), 120);
        Subtask subtask2 = new Subtask(taskManager.calcId(), "Sub2", "SubDescr2",
                LocalDateTime.of(2022, 5, 18, 19, 30), 120);
        Assertions.assertEquals(taskManager.getTaskStorage().size(), 0, "Задача task не удалилась");
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask1);
        taskManager.putSubTask(epic1, subtask2);
        taskManager.deleteEpicTask(epic1);
        Assertions.assertEquals(taskManager.getEpicTaskStorage().size(), 0, "Задача epic не удалилась");
        Assertions.assertEquals(taskManager.getSubTaskStorage().size(), 0, "Задачи subtask не удалились");
        taskManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Delete Subtask")
    void deleteSubtask_TEST() {
        Epic epic1 = new Epic(taskManager.calcId(), "Epic1", "EpicDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        Subtask subtask1 = new Subtask(taskManager.calcId(), "Sub1", "SubDescr1",
                LocalDateTime.of(2022, 5, 17, 19, 30), 120);
        Assertions.assertEquals(taskManager.getTaskStorage().size(), 0, "Задача task не удалилась");
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask1);
        taskManager.deleteSubTask(subtask1);
        Assertions.assertEquals(taskManager.getSubTaskStorage().size(), 0, "Задача subtask не удалились");
        Assertions.assertEquals(taskManager.getEpicTaskStorage().get(epic1.getId()).getSubTaskListId().size(), 0,
                "Задача subtask не удалилась в epic");
        taskManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Get prioritized tasks")
    void getPrioritizedTasks_2_TEST() {
        Task task1 = new Task(taskManager.calcId(), "testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Epic epic1 = new Epic(taskManager.calcId(), "Epic1", "EpicDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        Subtask subtask1 = new Subtask(taskManager.calcId(), "Sub1", "SubDescr1",
                LocalDateTime.of(2022, 5, 17, 19, 30), 120);
        Subtask subtask2 = new Subtask(taskManager.calcId(), "Sub2", "SubDescr2",
                LocalDateTime.of(2022, 5, 18, 19, 30), 120);
        taskManager.putTask(task1);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask1);
        taskManager.putSubTask(epic1, subtask2);
        Assertions.assertEquals(taskManager.getPrioritizedTasks().size(), 3, "В список добавляются epic");
        Task firstTask = taskManager.getPrioritizedTasks().first();
        Task lastTask = taskManager.getPrioritizedTasks().last();
        Assertions.assertEquals(firstTask, task1, "Некорретный первый элемент");
        Assertions.assertEquals(lastTask, subtask2, "Некорретный последний элемент (в задачах установлено время)");
        Task task3 = new Task(taskManager.calcId(), "testName2", "testDescr2", null, 0);
        taskManager.putTask(task3);
        lastTask = taskManager.getPrioritizedTasks().last();
        Assertions.assertEquals(taskManager.getPrioritizedTasks().size(), 4,
                "В список не добавилась задача со временем начала null");
        Assertions.assertEquals(lastTask, task3, "Некорретный последний элемент (должна быть задача с null)");
        taskManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Task startTime overlapping")
    void startTimeOverlapping_TEST() {
        Task task1 = new Task(taskManager.calcId(), "testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Task task2 = new Task(taskManager.calcId(), "testName2", "testDescr2",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Epic epic1 = new Epic(taskManager.calcId(), "Epic1", "EpicDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        Subtask subtask1 = new Subtask(taskManager.calcId(), "Sub1", "SubDescr1",
                LocalDateTime.of(2022, 5, 17, 19, 30), 120);
        Subtask subtask2 = new Subtask(taskManager.calcId(), "Sub2", "SubDescr2",
                LocalDateTime.of(2022, 5, 17, 19, 30), 120);
        taskManager.putTask(task1);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> taskManager.putSubTask(epic1, subtask2));
        Assertions.assertThrows(IllegalArgumentException.class, () -> taskManager.putTask(task2));
        Assertions.assertEquals(taskManager.getTaskStorage().size(), 1, "Ошибка добавления task");
        Assertions.assertEquals(taskManager.getSubTaskStorage().size(), 1, "Добавилась subtask с пересечением");
        Assertions.assertEquals(taskManager.getSubTaskStorage().get(subtask1.getId()), subtask1, "Сохранилась неверная subtask");
        taskManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Test file")
    void testFileOneTaskWithoutHistory_TEST() throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager("/Users/valleyker/Desktop/Tasks.csv");
        Task task1 = new Task(manager.calcId(), "testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        manager.putTask(task1);
        FileBackedTasksManager restoredManager = manager.loadFromFile(new File("/Users/valleyker/Desktop/Tasks.csv"));
        Assertions.assertEquals(restoredManager.getTaskStorage().size(), 1,
                "Некорректное восстановление менеджера");
        manager.deleteAllTasks();
        restoredManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Test file")
    void testFileFewTasksWithoutHistory_TEST() throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager("/Users/valleyker/Desktop/Tasks.csv");
        Task task1 = new Task(manager.calcId(), "testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Task task2 = new Task(manager.calcId(), "testName2", "testDescr2",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        Task task3 = new Task(manager.calcId(), "testName3", "testDescr3",
                LocalDateTime.of(2022, 5, 17, 19, 30), 120);
        manager.putTask(task1);
        manager.putTask(task2);
        manager.putTask(task3);
        FileBackedTasksManager restoredManager =                 manager.loadFromFile(new File("/Users/valleyker/Desktop/Tasks.csv"));
        Assertions.assertEquals(restoredManager.getTaskStorage().size(), 3,
                "Некорректное восстановление менеджера");
        manager.deleteAllTasks();
        restoredManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Test file")
    void testFileOneTaskWithHistory_TEST() throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager("/Users/valleyker/Desktop/Tasks.csv");
        Task task1 = new Task(manager.calcId(), "testName1", "testDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        manager.putTask(task1);
        manager.getTaskByID(task1.getId());
        FileBackedTasksManager restoredManager = manager.loadFromFile(new File("/Users/valleyker/Desktop/Tasks.csv"));
        Assertions.assertEquals(restoredManager.getTaskStorage().size(), 1,
                "Некорректное восстановление менеджера");
        Assertions.assertEquals(restoredManager.history.getHistory().size(), 1,
                "Некорректное восстановление истории");
        manager.deleteAllTasks();
        restoredManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Test file")
    void testFileOneEpicWithoutSubtasks_TEST() throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager("/Users/valleyker/Desktop/Tasks.csv");
        Epic epic1 = new Epic(manager.calcId(), "Epic1", "EpicDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        manager.putTask(epic1);
        FileBackedTasksManager restoredManager = manager.loadFromFile(new File("/Users/valleyker/Desktop/Tasks.csv"));
        Assertions.assertEquals(restoredManager.getEpicTaskStorage().size(), 1,
                "Некорректное восстановление менеджера");
        Assertions.assertEquals(restoredManager.getEpicTaskStorage().get(epic1.getId()), epic1,
                "Некорректное восстановление задачи");
        manager.deleteAllTasks();
        restoredManager.deleteAllTasks();
    }
}
