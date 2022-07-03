package http;

import manager.TaskManager;
import alltasks.Epic;
import alltasks.Subtask;
import alltasks.Task;
import manager.Managers;

import java.io.IOException;
import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Запуск KVServer");
        new KVServer().start();
        System.out.println();
        System.out.println("Запуск httpTaskServer");
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);
        Task task2 = new Task(2,"taskName_2", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 16, 17, 30), 60);
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null,0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);
        Subtask subtask12 = new Subtask(5,"subTaskName_12", "subTaskDescription_12",
                LocalDateTime.of(2022, 5, 12, 20, 30), 60);
        Task task3 = new Task(6,"taskName_3", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 13, 21, 30), 120);
        Task task4 = new Task(7,"taskName_4", "Проверка на JSON", null, 60);
        Epic epic2 = new Epic(8,"epicTaskName_2", "epicTaskDescription_2",
                null,0);

        manager.putTask(task1);
        manager.putTask(task2);
        manager.putEpicTask(epic1);
        manager.putSubTask(epic1, subtask11);
        manager.putSubTask(epic1, subtask12);
        manager.putTask(task3);
        manager.putTask(task4);
        manager.putEpicTask(epic2);
        int[] numbers = {1, 2, 3, 4, 5};
        for (int number : numbers) {
            manager.getTaskByID(number);
        }
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
        System.out.println("Создаем новый экземпляр HTTPTaskManager taskManager");
        HTTPTaskManager taskManager = new HTTPTaskManager(true);
        System.out.println("Печать восстановленных данных");
        taskManager.getAllTasks().forEach((key, value) -> System.out.println(key + " " + value));
        System.out.println("Печать восстановленной истории");
        System.out.println(taskManager.getHistory());
        Task task5 = new Task(6,"Проверка на ID", "Проверка на JSON",
                LocalDateTime.of(2025, 5, 15, 16, 30), 60);
        taskManager.putTask(task5);
        System.out.println(task5);
        System.out.println(taskManager.getPrioritizedTasks());
    }
}
