package ru.yandex.practicum.taskTracker.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void afterEach() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    private void addObjectOnServer(Task task, URI url) throws IOException, InterruptedException {
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
        return gson.fromJson(response.body(), new TypeToken<>() {
        });
    }

    private List<Epic> getEpicList(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), new TypeToken<>() {
        });
    }

    private List<Subtask> getSubtaskList(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), new TypeToken<>() {
        });
    }

    private void deleteObjectsOfType(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Task getTaskById(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriTask + "?id=" + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Task.class);
    }

    private Epic getEpicById(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriEpic + "?id=" + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Epic.class);
    }

    private Subtask getSubtaskById(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriSubtask + "?id=" + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Subtask.class);
    }


    private void deleteObjectById(URI uri, Task task) throws IOException, InterruptedException {
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
        addObjectOnServer(firstTask, uriTask);
        addObjectOnServer(secondTask, uriTask);
        addObjectOnServer(firstEpic, uriEpic);
        addObjectOnServer(secondEpic, uriEpic);
        addObjectOnServer(firstSubtask, uriSubtask);
        addObjectOnServer(secondSubtask, uriSubtask);
        int expectedListSize = 4;
        prioritizedList = getTaskList(uriPrioritizedTasks);
        assertEquals(expectedListSize, prioritizedList.size(), "Неверная длинна списка.");
        assertTrue(prioritizedList.contains(firstTask), "Задача в списке получена некорректно.");
        // удаление Tasks
        deleteObjectsOfType(uriTask);
        prioritizedList = getTaskList(uriPrioritizedTasks);
        expectedListSize = 2;
        assertEquals(expectedListSize, prioritizedList.size(), "Списки не совпадают.");
        assertFalse(prioritizedList.contains(firstTask), "Список не должен содержать " + firstTask);
        // удаление всех задач
        deleteObjectsOfType(uriSubtask);
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
        addObjectOnServer(firstTask, uriTask);
        addObjectOnServer(firstEpic, uriEpic);
        addObjectOnServer(secondSubtask, uriSubtask);
        List<Task> expectedList = new ArrayList<>();
        expectedList.add(getSubtaskById(secondSubtask.getId()));
        expectedList.add(getTaskById(firstTask.getId()));
        expectedList.add(getEpicById(firstEpic.getId()));
        historyList = getTaskList(uriHistory);
        assertEquals(expectedList.size(), historyList.size(), "Списки не совпадают.");
        assertEquals(expectedList.get(1), historyList.get(1), "Порядок задач не совпадает.");
        // удаление подзадачи
        deleteObjectsOfType(uriSubtask);
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
        addObjectOnServer(firstEpic, uriEpic);
        addObjectOnServer(firstSubtask, uriSubtask);
        addObjectOnServer(secondSubtask, uriSubtask);
        subtaskList = getSubtaskList(uriSubtasksFromEpic);
        int expectedListSize = 2;
        assertEquals(expectedListSize, subtaskList.size(), "Неверная длинна списка.");
        assertTrue(subtaskList.contains(firstSubtask), "Задача в списке получена некорректно.");
        Thread.sleep(100);
        // удаление подзадачи
        deleteObjectById(uriSubtask, firstSubtask);
        expectedListSize = 1;
        subtaskList = getSubtaskList(uriSubtasksFromEpic);
        assertEquals(expectedListSize, subtaskList.size(), "Списки не совпадают.");
        assertFalse(subtaskList.contains(firstSubtask), "Список не должен содержать " + firstSubtask);
        Thread.sleep(100); // Тест без задержки не проходит (видимо из-за пинга)
        // удаление эпика
        deleteObjectsOfType(uriEpic);
        subtaskList = getSubtaskList(uriSubtasksFromEpic);
        assertNull(subtaskList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetAllTasks() throws IOException, InterruptedException {
        // пустой список
        List<Task> taskList = getTaskList(uriTask);
        assertNull(taskList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addObjectOnServer(firstTask, uriTask);
        addObjectOnServer(secondTask, uriTask);
        int expectedListSize = 2;
        taskList = getTaskList(uriTask);
        assertEquals(expectedListSize, taskList.size(), "Неверная длинна списка.");
        assertTrue(taskList.contains(firstTask), "Задача в списке получена некорректно.");
        assertTrue(taskList.contains(secondTask), "Задача в списке получена некорректно.");
        // удаление задач
        deleteObjectsOfType(uriTask);
        taskList = getTaskList(uriTask);
        assertNull(taskList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetTaskById() throws IOException, InterruptedException {
        // задача не существует
        Task task = getTaskById(firstTask.getId());
        assertNull(task, "Задача не должна существовать.");
        // получение задачи
        addObjectOnServer(firstTask, uriTask);
        task = getTaskById(firstTask.getId());
        assertEquals(firstTask, task, "Задачи не совпадают.");
        // удаление задачи
        deleteObjectById(uriTask, firstTask);
        task = getTaskById(firstTask.getId());
        assertNull(task, "Задача не удалена.");
    }

    @Test
    void handlePostTaskById() throws IOException, InterruptedException {
        // отправка задачи на сервер
        addObjectOnServer(firstTask, uriTask);
        Task task = getTaskById(firstTask.getId());
        assertEquals(firstTask, task, "Задача создана некорректно");
        // обновление задачи
        final Task updateTask = new Task(
                secondTask.getTaskName(),
                secondTask.getDescription(),
                firstTask.getId());
        addObjectOnServer(updateTask, uriTask);
        assertNotEquals(task, getTaskById(firstTask.getId()), "Задача не обновлена.");
        assertEquals(updateTask, getTaskById(firstTask.getId()), "Задача не обновлена.");
        // удаление задачи
        deleteObjectById(uriTask, firstTask);
        assertNull(getTaskById(firstTask.getId()), "Задача не удалена.");
    }

    @Test
    void handleDeleteAllTasks() throws IOException, InterruptedException {
        // нет задач
        assertNull(getTaskList(uriTask), "Список задач должен быть пустым.");
        // удаление задач
        addObjectOnServer(firstTask, uriTask);
        addObjectOnServer(secondTask, uriTask);
        final int expectedSize = 2;
        assertEquals(expectedSize, getTaskList(uriTask).size(), "Списки задач не совпадают.");
        deleteObjectsOfType(uriTask);
        assertNull(getTaskList(uriTask), "Задачи не удалены.");
    }

    @Test
    void handleDeleteTaskById() throws IOException, InterruptedException {
        // нет задач
        assertNull(getTaskList(uriTask), "Список задач должен быть пустым.");
        // удаление задачи по Id
        addObjectOnServer(firstTask, uriTask);
        addObjectOnServer(secondTask, uriTask);
        assertTrue(getTaskList(uriTask).contains(firstTask), "Задача не добавлена.");
        deleteObjectById(uriTask, firstTask);
        assertFalse(getTaskList(uriTask).contains(firstTask), "Задача не удалена.");
        assertTrue(getTaskList(uriTask).contains(secondTask), "Задача не должна быть удалена.");
        deleteObjectById(uriTask, secondTask);
        assertNull(getTaskList(uriTask), "Задача не удалена.");
    }

    @Test
    void handleGetAllEpics() throws IOException, InterruptedException {
        // пустой список
        List<Epic> epicList = getEpicList(uriEpic);
        assertNull(epicList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addObjectOnServer(firstEpic, uriEpic);
        addObjectOnServer(secondEpic, uriEpic);
        int expectedListSize = 2;
        epicList = getEpicList(uriEpic);
        assertEquals(expectedListSize, epicList.size(), "Неверная длинна списка.");
        assertTrue(epicList.contains(firstEpic), "Задача в списке получена некорректно.");
        assertTrue(epicList.contains(secondEpic), "Задача в списке получена некорректно.");
        // удаление задач
        deleteObjectsOfType(uriEpic);
        epicList = getEpicList(uriEpic);
        assertNull(epicList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetEpicById() throws IOException, InterruptedException {
        // задача не существует
        Epic epic = getEpicById(firstEpic.getId());
        assertNull(epic, "Задача не должна существовать.");
        // получение задачи
        addObjectOnServer(firstEpic, uriEpic);
        epic = getEpicById(firstEpic.getId());
        assertEquals(firstEpic, epic, "Задачи не совпадают.");
        // удаление задачи
        deleteObjectById(uriEpic, firstEpic);
        epic = getEpicById(firstEpic.getId());
        assertNull(epic, "Задача не удалена.");
    }

    @Test
    void handlePostEpicById() throws IOException, InterruptedException {
        // отправка задачи на сервер
        addObjectOnServer(firstEpic, uriEpic);
        Epic epic = getEpicById(firstEpic.getId());
        assertEquals(firstEpic, epic, "Задача создана некорректно");
        // обновление задачи
        final Epic updateEpic = new Epic(
                secondEpic.getTaskName(),
                secondEpic.getDescription(),
                firstEpic.getId());
        addObjectOnServer(updateEpic, uriEpic);
        assertNotEquals(epic, getEpicById(firstEpic.getId()), "Задача не обновлена.");
        assertEquals(updateEpic, getEpicById(firstEpic.getId()), "Задача не обновлена.");
        // удаление задачи
        deleteObjectById(uriEpic, firstEpic);
        assertNull(getEpicById(firstEpic.getId()), "Задача не удалена.");
    }

    @Test
    void handleDeleteAllEpics() throws IOException, InterruptedException {
        // нет задач
        assertNull(getEpicList(uriEpic), "Список задач должен быть пустым.");
        // удаление задач
        addObjectOnServer(firstEpic, uriEpic);
        addObjectOnServer(secondEpic, uriEpic);
        final int expectedSize = 2;
        assertEquals(expectedSize, getEpicList(uriEpic).size(), "Списки задач не совпадают.");
        deleteObjectsOfType(uriEpic);
        assertNull(getEpicList(uriEpic), "Задачи не удалены.");
    }

    @Test
    void handleDeleteEpicById() throws IOException, InterruptedException {
        // нет задач
        assertNull(getEpicList(uriEpic), "Список задач должен быть пустым.");
        // удаление задачи по Id
        addObjectOnServer(firstEpic, uriEpic);
        addObjectOnServer(secondEpic, uriEpic);
        assertTrue(getEpicList(uriEpic).contains(firstEpic), "Задача не добавлена.");
        deleteObjectById(uriEpic, firstEpic);
        assertFalse(getEpicList(uriEpic).contains(firstEpic), "Задача не удалена.");
        assertTrue(getEpicList(uriEpic).contains(secondEpic), "Задача не должна быть удалена.");
        deleteObjectById(uriEpic, secondEpic);
        assertNull(getEpicList(uriEpic), "Задача не удалена.");
    }

    @Test
    void handleGetAllSubtasks() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, uriEpic);
        // пустой список
        List<Subtask> subtaskList = getSubtaskList(uriSubtask);
        assertNull(subtaskList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addObjectOnServer(firstSubtask, uriSubtask);
        addObjectOnServer(secondSubtask, uriSubtask);
        int expectedListSize = 2;
        subtaskList = getSubtaskList(uriSubtask);
        assertEquals(expectedListSize, subtaskList.size(), "Неверная длинна списка.");
        assertTrue(subtaskList.contains(firstSubtask), "Задача в списке получена некорректно.");
        assertTrue(subtaskList.contains(secondSubtask), "Задача в списке получена некорректно.");
        // удаление задач
        deleteObjectsOfType(uriSubtask);
        subtaskList = getSubtaskList(uriSubtask);
        assertNull(subtaskList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetSubtaskById() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, uriEpic);
        // задача не существует
        Subtask subtask = getSubtaskById(firstSubtask.getId());
        assertNull(subtask, "Задача не должна существовать.");
        // получение задачи
        addObjectOnServer(firstSubtask, uriSubtask);
        subtask = getSubtaskById(firstSubtask.getId());
        assertEquals(firstSubtask, subtask, "Задачи не совпадают.");
        // удаление задачи
        deleteObjectById(uriSubtask, firstSubtask);
        subtask = getSubtaskById(firstSubtask.getId());
        assertNull(subtask, "Задача не удалена.");
    }

    @Test
    void handlePostSubtaskById() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, uriEpic);
        // отправка задачи на сервер
        addObjectOnServer(firstSubtask, uriSubtask);
        Subtask subtask = getSubtaskById(firstSubtask.getId());
        assertEquals(firstSubtask, subtask, "Задача создана некорректно");
        // обновление задачи
        final Subtask updateSubtask = new Subtask(
                secondSubtask.getTaskName(),
                secondSubtask.getDescription(),
                firstSubtask.getId(),
                firstEpic.getId());
        addObjectOnServer(updateSubtask, uriSubtask);
        assertNotEquals(subtask, getSubtaskById(firstSubtask.getId()), "Задача не обновлена.");
        assertEquals(updateSubtask, getSubtaskById(firstSubtask.getId()), "Задача не обновлена.");
        // удаление задачи
        deleteObjectById(uriSubtask, firstSubtask);
        assertNull(getSubtaskById(firstSubtask.getId()), "Задача не удалена.");
    }

    @Test
    void handleDeleteAllSubtasks() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, uriEpic);
        // нет задач
        assertNull(getSubtaskList(uriSubtask), "Список задач должен быть пустым.");
        // удаление задач
        addObjectOnServer(firstSubtask, uriSubtask);
        addObjectOnServer(secondSubtask, uriSubtask);
        final int expectedSize = 2;
        assertEquals(expectedSize, getSubtaskList(uriSubtask).size(), "Списки задач не совпадают.");
        deleteObjectsOfType(uriSubtask);
        assertNull(getSubtaskList(uriSubtask), "Задачи не удалены.");
    }

    @Test
    void handleDeleteSubtaskById() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, uriEpic);
        // нет задач
        assertNull(getSubtaskList(uriSubtask), "Список задач должен быть пустым.");
        // удаление задачи по Id
        addObjectOnServer(firstSubtask, uriSubtask);
        addObjectOnServer(secondSubtask, uriSubtask);
        assertTrue(getSubtaskList(uriSubtask).contains(firstSubtask), "Задача не добавлена.");
        deleteObjectById(uriSubtask, firstSubtask);
        assertFalse(getSubtaskList(uriSubtask).contains(firstSubtask), "Задача не удалена.");
        assertTrue(getSubtaskList(uriSubtask).contains(secondSubtask), "Задача не должна быть удалена.");
        deleteObjectById(uriSubtask, secondSubtask);
        assertNull(getSubtaskList(uriSubtask), "Задача не удалена.");
    }
}