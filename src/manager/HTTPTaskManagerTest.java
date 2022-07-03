package manager;

import com.google.gson.reflect.TypeToken;
import http.*;
import com.google.gson.*;
import org.junit.jupiter.api.*;
import alltasks.Epic;
import alltasks.Subtask;
import alltasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskManagerTest {
    private static final int PORT = 8080;
    private String url = "http://localhost:" + PORT;
    private HTTPTaskManager taskManager;
    private HttpClient client;
    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @BeforeEach
    void startServers() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HTTPTaskManager(false);
        client = HttpClient.newHttpClient();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        taskManager.deleteAllTasks();
    }

    @AfterEach
    void stopServers() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    @DisplayName("EndPoint - Get All Tasks")
    void getTasks() throws IOException, InterruptedException {
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);
        Task task2 = new Task(2,"taskName_2", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 16, 17, 30), 60);
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);

        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask11);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/tasks/task/"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        List<Task> searchedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        HashMap<Integer, Task> searchedTaskStorage = new HashMap<>();
        for (Task elem : searchedTasks) {
            searchedTaskStorage.put(elem.getId(), elem);
        }

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertEquals(searchedTaskStorage, taskManager.getTaskStorage(), "Списки задач не совпадают");
    }

    @Test
    @DisplayName("EndPoint - Get All Epics")
    void getEpics() throws IOException, InterruptedException {
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);
        Task task2 = new Task(2,"taskName_2", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 16, 17, 30), 60);
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);

        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask11);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/tasks/epic/"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        List<Epic> searchedTasks = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        HashMap<Integer, Epic> searchedEpicStorage = new HashMap<>();
        for (Epic elem : searchedTasks) {
            searchedEpicStorage.put(elem.getId(), elem);
        }

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertEquals(searchedEpicStorage, taskManager.getEpicTaskStorage(), "Списки задач не совпадают");
    }

    @Test
    @DisplayName("EndPoint - Get All SubTasks")
    void getSubTasks() throws IOException, InterruptedException {
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);
        Task task2 = new Task(2,"taskName_2", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 16, 17, 30), 60);
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);


        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask11);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/tasks/subtask/"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        List<Subtask> searchedTasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());

        HashMap<Integer, Subtask> searchedSubTaskStorage = new HashMap<>();
        for (Subtask elem : searchedTasks) {
            searchedSubTaskStorage.put(elem.getId(), elem);
        }

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertEquals(searchedSubTaskStorage, taskManager.getSubTaskStorage(), "Списки задач не совпадают");
    }

    @Test
    @DisplayName("EndPoint - Get Task By Id")
    void getTaskById() throws IOException, InterruptedException {
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);
        Task task2 = new Task(2,"taskName_2", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 16, 17, 30), 60);
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);

        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask11);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/tasks/task/?id=" + task1.getId()))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Task searchedTask = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertEquals(searchedTask, task1, "Возвращается неверная задача");
    }

    @Test
    @DisplayName("EndPoint - Get Epic By Id")
    void getEpicById() throws IOException, InterruptedException {
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);
        Task task2 = new Task(2,"taskName_2", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 16, 17, 30), 60);
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);

        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask11);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/tasks/epic/?id=" + epic1.getId()))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Epic searchedTask = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertEquals(searchedTask, epic1, "Возвращается неверная задача");
    }

    @Test
    @DisplayName("EndPoint - Get SubTask By Id")
    void getSubTaskById() throws IOException, InterruptedException {
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);
        Task task2 = new Task(2,"taskName_2", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 16, 17, 30), 60);
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);

        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask11);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/tasks/subtask/?id=" + subtask11.getId()))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Subtask searchedTask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertEquals(searchedTask, subtask11, "Возвращается неверная задача");
    }

    @Test
    @DisplayName("EndPoint - Delete All Tasks")
    void deleteAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);
        Task task2 = new Task(2,"taskName_2", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 16, 17, 30), 60);
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);

        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask11);

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(url + "/tasks/task/"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertEquals(taskManager.getTaskStorage().size(), 0, "Список task не был очищен");
    }

    @Test
    @DisplayName("EndPoint - Delete All Epics")
    void deleteAllEpics() throws IOException, InterruptedException {
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);
        Task task2 = new Task(2,"taskName_2", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 16, 17, 30), 60);
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);

        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask11);

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(url + "/tasks/epic/"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertEquals(taskManager.getEpicTaskStorage().size(), 0, "Список task не был очищен");
    }

    @Test
    @DisplayName("EndPoint - Delete All SubTasks")
    void deleteAllSubTasks() throws IOException, InterruptedException {
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);
        Task task2 = new Task(2,"taskName_2", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 16, 17, 30), 60);
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);

        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask11);

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(url + "/tasks/subtask/"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertEquals(taskManager.getSubTaskStorage().size(), 0, "Список task не был очищен");
    }

    @Test
    @DisplayName("EndPoint - Delete Task By Id")
    void deleteTaskById() throws IOException, InterruptedException {
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);
        Task task2 = new Task(2,"taskName_2", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 16, 17, 30), 60);
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);

        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask11);

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(url + "/tasks/task/?id=" + task1.getId()))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertFalse(taskManager.getTaskStorage().containsValue(task1), "Задача task не была удалена");
    }

    @Test
    @DisplayName("EndPoint - Delete Epic By Id")
    void deleteEpicById() throws IOException, InterruptedException {
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);
        Task task2 = new Task(2,"taskName_2", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 16, 17, 30), 60);
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);

        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask11);

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(url + "/tasks/epic/?id=" + epic1.getId()))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertFalse(taskManager.getEpicTaskStorage().containsValue(epic1), "Задача epic не была удалена");
    }

    @Test
    @DisplayName("EndPoint - Delete SubTask By Id")
    void deleteSubTaskById() throws IOException, InterruptedException {
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);
        Task task2 = new Task(2,"taskName_2", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 16, 17, 30), 60);
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);

        taskManager.putTask(task1);
        taskManager.putTask(task2);
        taskManager.putEpicTask(epic1);
        taskManager.putSubTask(epic1, subtask11);

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(url + "/tasks/subtask/?id=" + subtask11.getId()))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertFalse(taskManager.getSubTaskStorage().containsValue(subtask11), "Задача subtask не была удалена");
    }

    @Test
    @DisplayName("EndPoint - post Task")
    void putTask() throws IOException, InterruptedException {
        Task task1 = new Task(1,"taskName_1", "Проверка на JSON",
                LocalDateTime.of(2022, 5, 15, 16, 30), 60);

        URI urlTask = URI.create(url + "/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlTask)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertEquals(task1.getName(), taskManager.getTaskStorage().get(1).getName(),
                "Ошибка. Задача не добавлена.");
        assertEquals(task1.getInfo(), taskManager.getTaskStorage().get(1).getInfo(),
                "Ошибка. Задача не добавлена.");
    }

    @Test
    @DisplayName("EndPoint - post Epic")
    void putEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);

        URI urlEpic = URI.create(url + "/tasks/epic/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlEpic)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertEquals(epic1.getName(), taskManager.getEpicTaskStorage().get(1).getName(),
                "Ошибка. Задача не добавлена.");
        assertEquals(epic1.getInfo(), taskManager.getEpicTaskStorage().get(1).getInfo(),
                "Ошибка. Задача не добавлена.");
    }

    @Test
    @DisplayName("EndPoint - post Subtask")
    void putSubTask() throws IOException, InterruptedException {
        Epic epic1 = new Epic(3,"epicTaskName_1", "epicTaskDescription_1",
                null, 0);
        Subtask subtask11 = new Subtask(4,"subTaskName_11", "subTaskDescription_11",
                LocalDateTime.of(2022, 5, 11, 19, 30), 60);

        URI urlEpic = URI.create(url + "/tasks/epic/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlEpic)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка. Код ответа не 200.");
        assertEquals(epic1.getName(), taskManager.getEpicTaskStorage().get(1).getName(),
                "Ошибка. Задача не добавлена.");
        assertEquals(epic1.getInfo(), taskManager.getEpicTaskStorage().get(1).getInfo(),
                "Ошибка. Задача не добавлена.");

        subtask11.setEpicTaskId(taskManager.getEpicTaskStorage().get(1).getId());
        URI urlSubTask = URI.create(url + "/tasks/subtask/");
        HttpRequest.BodyPublisher bodySub = HttpRequest.BodyPublishers.ofString(gson.toJson(subtask11));
        HttpRequest requestSub = HttpRequest.newBuilder()
                .uri(urlSubTask)
                .POST(bodySub)
                .build();
        HttpResponse<String> responseSub = client.send(requestSub, HttpResponse.BodyHandlers.ofString());
        System.out.println(taskManager.getSubTaskStorage());

        assertEquals(200, responseSub.statusCode(), "Ошибка. Код ответа не 200.");
        assertEquals(subtask11.getName(), taskManager.getSubTaskStorage().get(2).getName(),
                "Ошибка. Задача не добавлена.");
        assertEquals(subtask11.getInfo(), taskManager.getSubTaskStorage().get(2).getInfo(),
                "Ошибка. Задача не добавлена.");
    }
}