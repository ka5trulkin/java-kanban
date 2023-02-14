package ru.yandex.practicum.taskTracker.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.yandex.practicum.taskTracker.service.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = Managers.getGson();
    private final URI serverURL;
    private final long token;

    public KVTaskClient(URI serverURI) {
        this.serverURL = serverURI;
        this.token = registrationOnKVServer();
        System.out.println("Клиент получил токен: " + token);
    }

    private long registrationOnKVServer() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(this.serverURL.resolve("/register"))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return Long.parseLong(response.body());
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return - 1;
    }

    public void save(String key, String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(this.serverURL.resolve("/save/" + key + "?API_TOKEN=" + this.token))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Сохранение со стороны клиента прошло успешно");
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
}
