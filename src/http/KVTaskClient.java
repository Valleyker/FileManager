package http;

import manager.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String serverUrl;
    private final String apiToken;
    private final HttpClient client = HttpClient.newHttpClient();
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

    public KVTaskClient(final String serverUrl) {
        this.serverUrl = serverUrl;
        apiToken = register(serverUrl);
    }

    private String register(String serverUrl) {
        try {
            final HttpRequest request = requestBuilder
                    .uri(URI.create(serverUrl + "/register"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "text/html")
                    .GET()
                    .build();
            final HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() != 200) {
                throw new RuntimeException("Не удается обработать запрос /register");
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Обработка запроса /register прервана", e.getCause());
        }
    }

    public String load(String key) {
        try {
            final HttpRequest request = requestBuilder
                    .uri(URI.create(serverUrl + "/load/" + key + "?API_TOKEN=" + apiToken))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "text/json")
                    .GET()
                    .build();
            final HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() != 200) {
                throw new RuntimeException("Не удается обработать запрос");
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Обработка запроса прервана", e.getCause());
        }
    }

    public void put(String key, String value) {
        try {
            final HttpRequest request = requestBuilder
                    .uri(URI.create(serverUrl + "/save/" + key + "?API_TOKEN=" + apiToken))
                    .version(HttpClient.Version.HTTP_1_1)
                    .POST(HttpRequest.BodyPublishers.ofString(value))
                    .build();
            final HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Не удается сохранить данные");
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Обработка запроса прервана", e.getCause());
        }
    }
}
