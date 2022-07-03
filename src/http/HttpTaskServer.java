package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;
import alltasks.Epic;
import alltasks.Subtask;
import alltasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .create();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks/task/", new TasksHandler());
        server.createContext("/tasks/subtask/", new SubTasksHandler());
        server.createContext("/tasks/epic/", new EpicHandler());
        server.createContext("/tasks/tasks/", new GetAllTasksHandler());
        server.createContext("/tasks/", new GetPrioritizedTasksHandler());
        server.createContext("/tasks/history/", new GetHistoryHandler());
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void start() {
        System.out.println("Запускаем сервер на порту http://localhost: " + PORT);
        server.start();
    }

    private class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getRawQuery();
            String response;

            switch (method) {
                case "GET": {
                    if (!(query == null)) {
                        int idRequest = Integer.parseInt(query.substring(3));
                        if (manager.getTaskStorage().containsKey(idRequest)) {
                            Task requestedTask = manager.getTaskByID(idRequest);
                            response = gson.toJson(requestedTask);
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            exchange.getResponseHeaders().add("Content-Type", "application/text");
                            exchange.sendResponseHeaders(404, 0);
                            response = "Задача " + idRequest + " не найдена.";
                        }
                    } else {
                        List<Task> tasks = new ArrayList<>();
                        for (Map.Entry<Integer, Task> entry : manager.getTaskStorage().entrySet()) {
                            tasks.add(entry.getValue());
                        }
                        response = gson.toJson(tasks);
                        exchange.sendResponseHeaders(200, 0);
                    }
                    writeResponse(exchange, response);
                    break;
                }

                case "POST": {
                    System.out.println("Пришел запрос POST");
                    String json = readText(exchange);
                    if (json.isEmpty()) {
                        System.out.println("Body c задачей  пустой. указывается в теле запроса");
                        exchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    final Task taskFromRequest = gson.fromJson(json, Task.class);
                    final Integer idFromRequest = taskFromRequest.getId();
                    System.out.println(taskFromRequest);
                    if (idFromRequest != null) {
                        if (manager.getTaskStorage().containsKey(idFromRequest)) {
                            manager.updateTask(taskFromRequest);
                            response = "Обновлена задача: " + taskFromRequest;
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            exchange.getResponseHeaders().add("Content-Type", "application/text");
                            exchange.sendResponseHeaders(404, 0);
                            response = "Задача " + idFromRequest + " не найдена.";
                        }
                    } else {
                        try {
                            manager.putTask(taskFromRequest);
                            response = "Добавлена задача: " + taskFromRequest;
                            exchange.sendResponseHeaders(200, 0);
                        } catch (IllegalArgumentException e) {
                            response = "Обнаружено пересечение добавляемой задачи, задача не была добавлена";
                            exchange.sendResponseHeaders(200, 0);
                        }
                    }
                    writeResponse(exchange, response);
                    break;
                }

                case "DELETE": {
                    if (!(query == null)) {
                        int idRequest = Integer.parseInt(query.substring(3));
                        if (manager.getTaskStorage().containsKey(idRequest)) {
                            Task requestedTask = manager.getTaskByID(idRequest);
                            manager.deleteTask(requestedTask);
                            response = "Удалена task с id " + idRequest;
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            exchange.getResponseHeaders().add("Content-Type", "application/text");
                            exchange.sendResponseHeaders(404, 0);
                            response = "Задача " + idRequest + " не найдена.";
                        }
                    } else {
                        manager.deleteTaskStorage();
                        response = "Удален список task";
                        exchange.sendResponseHeaders(200, 0);
                    }
                    writeResponse(exchange, response);
                    break;
                }

                default:
                    response = "Использован неизвестный метод";
                    exchange.sendResponseHeaders(405, 0);
                    writeResponse(exchange, response);
                    break;
            }
        }
    }

    private class SubTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getRawQuery();
            String response;

            switch (method) {
                case "GET": {
                    if (!(query == null)) {
                        int idRequest = Integer.parseInt(query.substring(3));
                        if (manager.getSubTaskStorage().containsKey(idRequest)) {
                            Task requestedTask = manager.getTaskByID(idRequest);
                            response = gson.toJson(requestedTask);
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            exchange.getResponseHeaders().add("Content-Type", "application/text");
                            exchange.sendResponseHeaders(404, 0);
                            response = "Задача " + idRequest + " не найдена.";
                        }
                    } else {
                        List<Subtask> subtasks = new ArrayList<>();
                        for (Map.Entry<Integer, Subtask> entry : manager.getSubTaskStorage().entrySet()) {
                            subtasks.add(entry.getValue());
                        }
                        response = gson.toJson(subtasks);
                        exchange.sendResponseHeaders(200, 0);
                    }
                    writeResponse(exchange, response);
                    break;
                }

                case "POST": {
                    System.out.println("Пришел запрос POST");
                    String json = readText(exchange);
                    if (json.isEmpty()) {
                        System.out.println("Body c задачей  пустой. указывается в теле запроса");
                        exchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    final Subtask subTaskFromRequest = gson.fromJson(json, Subtask.class);
                    final Integer idFromRequest = subTaskFromRequest.getId();
                    System.out.println(subTaskFromRequest);
                    if (idFromRequest != null) {
                        if (manager.getSubTaskStorage().containsKey(idFromRequest)) {
                            manager.updateSubTask(subTaskFromRequest);
                            response = "Обновлена задача: " + subTaskFromRequest;
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            exchange.getResponseHeaders().add("Content-Type", "application/text");
                            exchange.sendResponseHeaders(404, 0);
                            response = "Задача " + idFromRequest + " не найдена.";
                        }
                    } else {
                        try {
                            System.out.println(manager.getEpicTaskStorage().get(subTaskFromRequest.getEpicTaskId()));
                            System.out.println(subTaskFromRequest);
                            manager.putSubTask(manager.getEpicTaskStorage().get(subTaskFromRequest.getEpicTaskId()), subTaskFromRequest);
                            response = "Добавлена задача: " + subTaskFromRequest;
                            exchange.sendResponseHeaders(200, 0);
                        } catch (IllegalArgumentException e) {
                            response = "Обнаружено пересечение добавляемой задачи, задача не была добавлена";
                            exchange.sendResponseHeaders(200, 0);
                        }
                    }
                    writeResponse(exchange, response);
                    break;
                }

                case "DELETE": {
                    if (!(query == null)) {
                        int idRequest = Integer.parseInt(query.substring(3));
                        if (manager.getSubTaskStorage().containsKey(idRequest)) {
                            Task requestedTask = manager.getTaskByID(idRequest);
                            manager.deleteSubTask((Subtask) requestedTask);
                            response = "Удалена subtask с id " + idRequest;
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            exchange.getResponseHeaders().add("Content-Type", "application/text");
                            exchange.sendResponseHeaders(404, 0);
                            response = "Задача " + idRequest + " не найдена.";
                        }
                    } else {
                        manager.deleteSubTaskStorage();
                        response = "Удален список subtask";
                        exchange.sendResponseHeaders(200, 0);
                    }
                    writeResponse(exchange, response);
                    break;
                }

                default:
                    response = "Использован неизвестный метод";
                    exchange.sendResponseHeaders(405, 0);
                    writeResponse(exchange, response);
                    break;
            }
        }
    }

    private class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getRawQuery();
            String response;

            switch (method) {
                case "GET": {
                    if (!(query == null)) {
                        int idRequest = Integer.parseInt(query.substring(3));
                        if (manager.getEpicTaskStorage().containsKey(idRequest)) {
                            Task requestedTask = manager.getTaskByID(idRequest);
                            response = gson.toJson(requestedTask);
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            exchange.getResponseHeaders().add("Content-Type", "application/text");
                            exchange.sendResponseHeaders(404, 0);
                            response = "Задача " + idRequest + " не найдена.";
                        }
                    } else {
                        List<Epic> epics = new ArrayList<>();
                        for (Map.Entry<Integer, Epic> entry : manager.getEpicTaskStorage().entrySet()) {
                            epics.add(entry.getValue());
                        }
                        response = gson.toJson(epics);
                        exchange.sendResponseHeaders(200, 0);
                    }
                    writeResponse(exchange, response);
                    break;
                }

                case "POST": {
                    System.out.println("Пришел запрос POST");
                    String json = readText(exchange);
                    if (json.isEmpty()) {
                        System.out.println("Body c задачей  пустой. указывается в теле запроса");
                        exchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    final Epic epicFromRequest = gson.fromJson(json, Epic.class);
                    final Integer idFromRequest = epicFromRequest.getId();
                    System.out.println(epicFromRequest);
                    if (idFromRequest != null) {
                        if (manager.getEpicTaskStorage().containsKey(idFromRequest)) {
                            manager.updateEpicTask(epicFromRequest);
                            response = "Обновлена задача: " + epicFromRequest;
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            exchange.getResponseHeaders().add("Content-Type", "application/text");
                            exchange.sendResponseHeaders(404, 0);
                            response = "Задача " + idFromRequest + " не найдена.";
                        }
                    } else {
                        try {
                            manager.putEpicTask(epicFromRequest);
                            response = "Добавлена задача: " + epicFromRequest;
                            exchange.sendResponseHeaders(200, 0);
                        } catch (IllegalArgumentException e) {
                            response = "Обнаружено пересечение добавляемой задачи, задача не была добавлена";
                            exchange.sendResponseHeaders(200, 0);
                        }
                    }
                    writeResponse(exchange, response);
                    break;
                }

                case "DELETE": {
                    if (!(query == null)) {
                        int idRequest = Integer.parseInt(query.substring(3));
                        if (manager.getEpicTaskStorage().containsKey(idRequest)) {
                            Task requestedTask = manager.getTaskByID(idRequest);
                            manager.deleteEpicTask((Epic) requestedTask);
                            response = "Удалена epic с id " + idRequest;
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            exchange.getResponseHeaders().add("Content-Type", "application/text");
                            exchange.sendResponseHeaders(404, 0);
                            response = "Задача " + idRequest + " не найдена.";
                        }
                    } else {
                        manager.deleteEpicTaskStorage();
                        response = "Удален список epic";
                        exchange.sendResponseHeaders(200, 0);
                    }
                    writeResponse(exchange, response);
                    break;
                }

                default:
                    response = "Использован неизвестный метод";
                    exchange.sendResponseHeaders(405, 0);
                    writeResponse(exchange, response);
                    break;
            }
        }
    }

    private class GetAllTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response;
            List<Task> allTasks = new ArrayList<>();
            if (method.equals("GET")) {
                for (Map.Entry<Integer, Task> entry : manager.getAllTasks().entrySet()) {
                    allTasks.add(entry.getValue());
                }
                response = gson.toJson(allTasks);
                exchange.sendResponseHeaders(200, 0);
                writeResponse(exchange, response);
            } else {
                response = "Использован неизвестный метод";
                exchange.sendResponseHeaders(405, 0);
                writeResponse(exchange, response);
            }
        }
    }

    private class GetPrioritizedTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response;
            if (method.equals("GET")) {
                response = gson.toJson(manager.getPrioritizedTasks());
                exchange.sendResponseHeaders(200, 0);
                writeResponse(exchange, response);
            } else {
                response = "Использован неизвестный метод";
                exchange.sendResponseHeaders(405, 0);
                writeResponse(exchange, response);
            }
        }
    }

    private class GetHistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response;
            if (method.equals("GET")) {
                if (!manager.getHistory().isEmpty()) {
                    response = gson.toJson(manager.getHistory());
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    exchange.getResponseHeaders().add("Content-Type", "application/text");
                    exchange.sendResponseHeaders(200, 0);
                    response = "История просмотров пуста.";
                }
                writeResponse(exchange, response);
            } else {
                response = "Использован неизвестный метод";
                exchange.sendResponseHeaders(405, 0);
                writeResponse(exchange, response);
            }
        }
    }

    private void writeResponse(HttpExchange exchange, String response) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    public void stop() {
        server.stop(1);
    }
}
