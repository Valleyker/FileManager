package alltasks;

import info.Status;
import org.junit.jupiter.api.AfterEach;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;


class EpicTest {

    TaskManager taskManager = new InMemoryTaskManager();

    @AfterEach
    void deleteAllTaskAfterEachTest() {
        taskManager.deleteAllTasks();
    }

    @Test
    @DisplayName("Create new epic without subtasks")
    void checkEpicWithoutSubtasks() {
        Epic epic = new Epic(taskManager.calcId(), "epicTest1", "epicDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30),0);
        taskManager.putEpicTask(epic);
        Assertions.assertEquals(Status.NEW, epic.getStatus(), "Subtask отсутствуют: " +
                "Статус Epic без Subtask должен быть NEW");
    }

    @Test
    @DisplayName("Create new epic, all subtask are NEW status")
    void checkEpicAllSubtasksAreNew() {
        Epic epic = new Epic(taskManager.calcId(), "epicTest1", "epicDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Subtask subtask1 = new Subtask(taskManager.calcId(), "subTest1", "subDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Subtask subtask2 = new Subtask(taskManager.calcId(), "subTest2", "subDescr2",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        taskManager.putEpicTask(epic);
        taskManager.putSubTask(epic, subtask1);
        taskManager.putSubTask(epic, subtask2);
        Assertions.assertEquals(Status.NEW, epic.getStatus(), "Все Subtask имеют статус NEW: " +
                "Статус Epic должен быть NEW");
    }

    @Test
    @DisplayName("Create new epic, all subtask are DONE status")
    void checkEpicAllSubtasksAreDone() {
        Epic epic = new Epic(taskManager.calcId(), "epicTest1", "epicDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Subtask subtask1 = new Subtask(taskManager.calcId(), "subTest1", "subDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Subtask subtask2 = new Subtask(taskManager.calcId(), "subTest2", "subDescr2",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        taskManager.putEpicTask(epic);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.putSubTask(epic, subtask1);
        taskManager.putSubTask(epic, subtask2);
        Assertions.assertEquals(Status.DONE, epic.getStatus(), "Все Subtask имеют статус DONE: " +
                "Статус Epic должен быть DONE");
    }

    @Test
    @DisplayName("Create new epic, all subtask are DONE or NEW or IN_PROGRESS status")
    void checkEpicAllSubtasksAreDoneOrNew() {
        Epic epic = new Epic(taskManager.calcId(), "epicTest1", "epicDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Subtask subtask1 = new Subtask(taskManager.calcId(), "subTest1", "subDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Subtask subtask2 = new Subtask(taskManager.calcId(), "subTest2", "subDescr2",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        Subtask subtask3 = new Subtask(taskManager.calcId(), "subTest2", "subDescr2",
                LocalDateTime.of(2022, 5, 17, 19, 30), 120);
        taskManager.putEpicTask(epic);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.putSubTask(epic, subtask1);
        taskManager.putSubTask(epic, subtask2);
        taskManager.putSubTask(epic, subtask3);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Subtask имеют статус DONE и NEW: " +
                "Статус Epic должен быть IN_PROGRESS");
    }

    @Test
    @DisplayName("Create new epic, all subtask are IN_PROGRESS status")
    void checkEpicAllSubtasksAreInProgress() {
        Epic epic = new Epic(taskManager.calcId(), "epicTest1", "epicDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Subtask subtask1 = new Subtask(taskManager.calcId(), "subTest1", "subDescr1",
                LocalDateTime.of(2022, 5, 15, 19, 30), 120);
        Subtask subtask2 = new Subtask(taskManager.calcId(), "subTest2", "subDescr2",
                LocalDateTime.of(2022, 5, 16, 19, 30), 120);
        taskManager.putEpicTask(epic);
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.putSubTask(epic, subtask1);
        taskManager.putSubTask(epic, subtask2);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Subtask имеют статус IN_PROGRESS: " +
                "Статус Epic должен быть IN_PROGRESS");
    }
}