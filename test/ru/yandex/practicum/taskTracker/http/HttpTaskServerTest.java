package ru.yandex.practicum.taskTracker.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.service.Managers;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpClient client;
    private Gson gson;
    private Task firstTask;
    private Task secondTask;
    private Epic firstEpic;
    private Epic secondEpic;
    private Subtask firstSubtask;
    private Subtask secondSubtask;
    private final URI uriTask = URI.create("http://localhost:8080/tasks/task");
    private final URI uriEpic = URI.create("http://localhost:8080/tasks/epic");
    private final URI uriSubtask = URI.create("http://localhost:8080/tasks/subtask");


    @BeforeAll
    static void beforeAll() throws IOException, URISyntaxException {
        new KVServer().start();
        new HttpTaskServer().start();
    }

    @BeforeEach
    void BeforeEach() {
        client = HttpClient.newHttpClient();
        gson = Managers.getGson();
        LocalDateTime dateTime = LocalDateTime.now();
        int idCounter = 0;
        firstTask = new Task(
                "Задача 1",
                "Описание задачи 1",
                dateTime.plusMinutes(15),
                Duration.ofMinutes(15),
                ++idCounter);
        secondTask = new Task(
                "Задача 2",
                "Описание задачи 2",
                dateTime,
                Duration.ofMinutes(15),
                ++idCounter);
        firstEpic = new Epic("Эпик 1", "Описание эпика 1", ++idCounter);
        secondEpic = new Epic("Эпик 2", "Описание эпика 2", ++idCounter, Status.NEW);
        firstSubtask = new Subtask(
                "Подзадача 1",
                "Описание подзадачи 1",
                dateTime.plusMinutes(20),
                Duration.ofMinutes(15),
                ++idCounter,
                firstEpic.getId());
        secondSubtask = new Subtask(
                "Подзадача 2",
                "Описание подзадачи 2",
                dateTime.minusMinutes(115),
                Duration.ofMinutes(15),
                ++idCounter,
                firstEpic.getId());
    }

    private void pushTaskOnServer(Task task, URI url) throws IOException, InterruptedException {
        String json = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("pushTaskOnServer");
        if (response.statusCode() != 201) {
            System.out.println("Произошла ошибка отправки задачи на сервер.");
        }
    }

    private List<Task> getPrioritizedList() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        TypeToken<List<Task>> typeToken = new TypeToken<>() {
        };
        return gson.fromJson(response.body(), typeToken);
    }

    private void deleteTasks(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

    }

    @Test
    void handleGetPrioritizedTasks() throws IOException, InterruptedException {
        // пустой список
        List<Task> prioritizedList = getPrioritizedList();
        assertNull(prioritizedList, "Список не должен быть возвращен.");
        // проверка состояния списка
        pushTaskOnServer(firstTask, uriTask);
        pushTaskOnServer(secondTask, uriTask);
        pushTaskOnServer(firstEpic, uriEpic);
        pushTaskOnServer(secondEpic, uriEpic);
        pushTaskOnServer(firstSubtask, uriSubtask);
        pushTaskOnServer(secondSubtask, uriSubtask);
        int expectedListSize = 4;
        prioritizedList = getPrioritizedList();
        assertEquals(expectedListSize, prioritizedList.size(), "Неверная длинна списка.");
        assertTrue(prioritizedList.contains(firstTask), "Задача в списке восстановлена некорректно.");
        // удаление Tasks
        deleteTasks(uriTask);
        prioritizedList = getPrioritizedList();
        System.out.println(prioritizedList);
        expectedListSize = 2;
        assertEquals(expectedListSize, prioritizedList.size(), "Списки не совпадают.");
        assertFalse(prioritizedList.contains(firstTask), "Список не должен содержать " + firstTask);
        // удаление всех задач
        deleteTasks(uriSubtask);
        prioritizedList = getPrioritizedList();
        assertNull(prioritizedList, "Список не должен быть возвращен.");
    }


}