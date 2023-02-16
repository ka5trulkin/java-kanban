package ru.yandex.practicum.taskTracker.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
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
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
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

//    @BeforeAll
//    static void beforeAll() throws IOException, URISyntaxException {
//        new KVServer().start();
//        new HttpTaskServer().start();
//    }

    @AfterEach
    void afterEach() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @BeforeEach
    void BeforeEach() throws IOException, URISyntaxException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

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

    private void addTaskOnServer(Task task, URI url) throws IOException, InterruptedException {
        String json = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
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
        System.out.println("Тело getTaskList: " + response.body());
        return gson.fromJson(response.body(), new TypeToken<>(){});
    }

    private List<Epic> getEpicList(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Тело getTaskList: " + response.body());
        return gson.fromJson(response.body(), new TypeToken<>(){});
    }

    private List<Subtask> getSubtaskList(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Тело getTaskList: " + response.body());
        return gson.fromJson(response.body(), new TypeToken<>(){});

    }


    private void deleteTasks(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Task getTaskById(URI uri, Task task) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri.toString() + "?id=" + task.getId()))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), task.getClass());
    }

    private void deleteTaskById(URI uri, Task task) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri.toString() + "?id=" + task.getId()))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void handleGetPrioritizedTasks() throws IOException, InterruptedException {
        URI uriPrioritizedTasks = URI.create("http://localhost:8080/tasks");
        // пустой список
        List<Task> prioritizedList = getTaskList(uriPrioritizedTasks);
        assertNull(prioritizedList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addTaskOnServer(firstTask, uriTask);
        addTaskOnServer(secondTask, uriTask);
        addTaskOnServer(firstEpic, uriEpic);
        addTaskOnServer(secondEpic, uriEpic);
        addTaskOnServer(firstSubtask, uriSubtask);
        addTaskOnServer(secondSubtask, uriSubtask);
        int expectedListSize = 4;
        prioritizedList = getTaskList(uriPrioritizedTasks);
        assertEquals(expectedListSize, prioritizedList.size(), "Неверная длинна списка.");
        assertTrue(prioritizedList.contains(firstTask), "Задача в списке получена некорректно.");
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
        addTaskOnServer(firstTask, uriTask);
        addTaskOnServer(firstEpic, uriEpic);
        addTaskOnServer(secondSubtask, uriSubtask);
        List<Task> expectedList = new ArrayList<>();
        expectedList.add(getTaskById(uriSubtask, secondSubtask));
        expectedList.add(getTaskById(uriTask, firstTask));
        expectedList.add(getTaskById(uriEpic, firstEpic));
        historyList = getTaskList(uriHistory);
        assertEquals(expectedList.size(), historyList.size(), "Списки не совпадают.");
        assertEquals(expectedList.get(1), historyList.get(1), "Порядок задач не совпадает.");
        // удаление подзадачи
        deleteTasks(uriSubtask);
        expectedList.remove(secondSubtask);
        historyList = getTaskList(uriHistory);
        assertEquals(expectedList.size(), historyList.size(), "Списки не совпадают.");
        assertEquals(expectedList.get(0), historyList.get(0), "Порядок задач не совпадает.");
    }


    @Test
    void handleGetSubtasksFromEpic() throws IOException, InterruptedException {
        URI uriSubtasksFromEpic = URI.create("http://localhost:8080/tasks/subtask/epic?id=" + firstEpic.getId());
        // пустой список
        List<Subtask> subtaskList = getSubtaskList(uriSubtasksFromEpic);
        List<Epic> epicList = getEpicList(uriEpic);
        assertNull(epicList);
        assertNull(subtaskList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addTaskOnServer(firstEpic, uriEpic);
        addTaskOnServer(firstSubtask, uriSubtask);
        addTaskOnServer(secondSubtask, uriSubtask);
        subtaskList = getSubtaskList(uriSubtasksFromEpic);
        int expectedListSize = 2;
        assertEquals(expectedListSize, subtaskList.size(), "Неверная длинна списка.");
        assertTrue(subtaskList.contains(firstSubtask), "Задача в списке получена некорректно.");
        // удаление подзадачи
        System.out.println("проверка списка: " + getSubtaskList(uriSubtask));
        deleteTaskById(uriSubtask, firstSubtask);
        expectedListSize = 1;
        subtaskList = getSubtaskList(uriSubtasksFromEpic);
        assertEquals(expectedListSize, subtaskList.size(), "Списки не совпадают.");
        assertFalse(subtaskList.contains(firstSubtask), "Список не должен содержать " + firstSubtask);
        Thread.sleep(100); // Тест без задержки не проходит (видимо из-за пинга)
        // удаление эпика
        deleteTasks(uriEpic);
        subtaskList = getSubtaskList(uriSubtasksFromEpic);
        assertNull(subtaskList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetAllTasks() throws IOException, InterruptedException {
        // пустой список
        List<Task> taskList = getTaskList(uriTask);
        assertNull(taskList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addTaskOnServer(firstTask, uriTask);
        addTaskOnServer(secondTask, uriTask);
        int expectedListSize = 2;
        taskList = getTaskList(uriTask);
        assertEquals(expectedListSize, taskList.size(), "Неверная длинна списка.");
        assertTrue(taskList.contains(firstTask), "Задача в списке получена некорректно.");
        assertTrue(taskList.contains(secondTask), "Задача в списке получена некорректно.");
        // удаление задач
        deleteTasks(uriTask);
        taskList = getTaskList(uriTask);
        assertNull(taskList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetTaskById() throws IOException, InterruptedException {
        // задача не существует
        Task task = getTaskById(uriTask, firstTask);
        assertNull(task, "Задача не должна существовать.");
        // получение задачи
        addTaskOnServer(firstTask, uriTask);
        task = getTaskById(uriTask, firstTask);
        assertEquals(firstTask, task, "Задачи не совпадают.");
        // удаление задачи
        deleteTaskById(uriTask, firstTask);
        task = getTaskById(uriTask, firstTask);
        assertNull(task, "Задача не удалена.");
    }
}