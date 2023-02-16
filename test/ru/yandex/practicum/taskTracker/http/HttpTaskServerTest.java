package ru.yandex.practicum.taskTracker.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.model.*;
import ru.yandex.practicum.taskTracker.service.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    void BeforeEach() throws IOException, URISyntaxException {
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

    private List<Task> getTaskList(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), new TypeToken<>(){});

    }

    private void deleteTasks(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Task getTask(URI uri, Task task) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri.toString() + "?id=" + task.getId()))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), task.getClass());
    }

    @Test
    void handleGetPrioritizedTasks() throws IOException, InterruptedException {
        URI uriPrioritizedTasks = URI.create("http://localhost:8080/tasks");
        // пустой список
        List<Task> prioritizedList = getTaskList(uriPrioritizedTasks);
        assertNull(prioritizedList, "Список не должен быть возвращен.");
        // проверка состояния списка
        pushTaskOnServer(firstTask, uriTask);
        pushTaskOnServer(secondTask, uriTask);
        pushTaskOnServer(firstEpic, uriEpic);
        pushTaskOnServer(secondEpic, uriEpic);
        pushTaskOnServer(firstSubtask, uriSubtask);
        pushTaskOnServer(secondSubtask, uriSubtask);
        int expectedListSize = 4;
        prioritizedList = getTaskList(uriPrioritizedTasks);
        System.out.println("Длинна " + prioritizedList.size());
        System.out.println("List " + prioritizedList);
        for (Task task : prioritizedList) {
            System.out.println(task);
            System.out.println(task.getType());
        }
        assertEquals(expectedListSize, prioritizedList.size(), "Неверная длинна списка.");
        assertTrue(prioritizedList.contains(firstTask), "Задача в списке восстановлена некорректно.");
        // удаление Tasks
        deleteTasks(uriTask);
        prioritizedList = getTaskList(uriPrioritizedTasks);
        expectedListSize = 2;
        assertEquals(expectedListSize, prioritizedList.size(), "Списки не совпадают.");
        assertFalse(prioritizedList.contains(firstTask), "Список не должен содержать " + firstTask);
        // удаление всех задач
        deleteTasks(uriSubtask);
        prioritizedList = getTaskList(uriPrioritizedTasks);
        assertNull(prioritizedList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetHistory() throws IOException, InterruptedException {
        URI uriHistory = URI.create("http://localhost:8080/tasks/history");
        // пустой список
        List<Task> historyList = getTaskList(uriHistory);
        assertNull(historyList, "Список не должен быть возвращен.");
        // проверка состояния списка
        pushTaskOnServer(firstTask, uriTask);
        pushTaskOnServer(firstEpic, uriEpic);
        pushTaskOnServer(secondSubtask, uriSubtask);
        List<Task> expectedList = new ArrayList<>();
        expectedList.add(getTask(uriSubtask, secondSubtask));
        expectedList.add(getTask(uriTask, firstTask));
        expectedList.add(getTask(uriEpic, firstEpic));
        historyList = getTaskList(uriHistory);
        assertEquals(expectedList.size(), historyList.size(), "Списки не совпадают.");
        assertEquals(expectedList.get(1), historyList.get(1), "Порядок задач не совпадает.");
        // удаление подзадачи
        deleteTasks(uriSubtask);
        expectedList.remove(secondSubtask);
        historyList = getTaskList(uriHistory);
        assertEquals(expectedList.size(), historyList.size(), "Списки не совпадают.");
        assertEquals(expectedList.get(0), historyList.get(0), "Порядок задач не совпадает.");
        deleteTasks(uriTask);
        deleteTasks(uriEpic);
    }
}