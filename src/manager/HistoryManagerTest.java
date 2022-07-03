package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import alltasks.Task;

import java.time.LocalDateTime;
import java.util.List;

class HistoryManagerTest {

    private TaskManager taskManager = new InMemoryTaskManager();

    @AfterEach
    void deleteHistory() {
        taskManager.removeAllHistory();
    }

    @Test
    @DisplayName("Check empty history")
    void checkNewHistoryIsEmpty_TEST() {
        Task task1 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        taskManager.putTask(task1);
        List<Task> history = taskManager.getHistory();
        Assertions.assertNotNull(history, "История задач null");
        Assertions.assertEquals(history.size(), 0, "Список не пустой");
    }

    @Test
    @DisplayName("Check history is not empty")
    void checkNewHistoryIsNotEmpty_TEST() {
        Task task1 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        taskManager.putTask(task1);
        taskManager.getTaskByID(task1.getId());
        List<Task> history = taskManager.getHistory();
        Assertions.assertNotNull(history, "История задач null");
        Assertions.assertEquals(history.size(), 1, "Размер списка некорретен");
        Assertions.assertEquals(history.get(0).getId(), task1.getId(), "В истории некорретная задача");
    }

    @Test
    @DisplayName("Check duplications in history")
    void checkDuplicationsInNewHistory_TEST() {
        Task task1 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Task task2 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        Task task3 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 17, 19, 30), 120);
        Task task4 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 18, 19, 30), 120);
        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putTask(task3);
        taskManager.putTask(task4);
        taskManager.getTaskByID(task1.getId());
        taskManager.getTaskByID(task2.getId());
        taskManager.getTaskByID(task3.getId());
        taskManager.getTaskByID(task4.getId());
        taskManager.getTaskByID(task1.getId());
        taskManager.getTaskByID(task2.getId());
        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(history.size(), 4, "Размер списка некорретен");
        Assertions.assertEquals(history.get(history.size() - 1).getId(), task2.getId(),
                "Последняя задача в истории некорретная");
        Assertions.assertEquals(history.get(0).getId(), task3.getId(),
                "Первая задача в истории некорретная");
    }

    @Test
    @DisplayName("Check removal from the beginning of the history")
    void checkRemovalFirstTaskInNewHistory_TEST() {
        Task task1 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Task task2 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        Task task3 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 17, 19, 30), 120);
        Task task4 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 18, 19, 30), 120);
        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putTask(task3);
        taskManager.putTask(task4);
        taskManager.getTaskByID(task1.getId());
        taskManager.getTaskByID(task2.getId());
        taskManager.getTaskByID(task3.getId());
        taskManager.getTaskByID(task4.getId());
        taskManager.removeFromHistory(task1.getId());
        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(history.size(), 3, "Размер списка некорретен");
        Assertions.assertEquals(history.get(0).getId(), task2.getId(),
                "Первая задача в истории некорретная");
    }

    @Test
    @DisplayName("Check removal from the end of the history")
    void checkRemovalLastTaskInNewHistory_TEST() {
        Task task1 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Task task2 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        Task task3 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 17, 19, 30), 120);
        Task task4 = new Task(taskManager.calcId(), "taskTest1", "taskDescr1",
                LocalDateTime.of(2022, 5, 18, 19, 30), 120);
        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putTask(task3);
        taskManager.putTask(task4);
        taskManager.getTaskByID(task1.getId());
        taskManager.getTaskByID(task2.getId());
        taskManager.getTaskByID(task3.getId());
        taskManager.getTaskByID(task4.getId());
        taskManager.removeFromHistory(task4.getId());
        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(history.size(), 3, "Размер списка некорретен");
        Assertions.assertEquals(history.get(2).getId(), task3.getId(),
                "Первая задача в истории некорретная");
    }
}