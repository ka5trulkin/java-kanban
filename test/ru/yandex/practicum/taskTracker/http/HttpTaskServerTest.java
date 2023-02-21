package ru.yandex.practicum.taskTracker.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.*;
import ru.yandex.practicum.taskTracker.service.InMemoryTaskManager;
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
    private HttpTaskServer httpTaskServer;
    private HttpClient client;
    private Gson gson;
    private Task firstTask;
    private Task secondTask;
    private Epic firstEpic;
    private Epic secondEpic;
    private Subtask firstSubtask;
    private Subtask secondSubtask;
    private final URI SERVER_URI = URI.create("http://localhost:8080/tasks");
    private final String TASK_ENDPOINT = "/task";
    private final String EPIC_ENDPOINT = "/epic";
    private final String SUBTASK_ENDPOINT = "/subtask";
    private final String PARAMETER_ID = "?id=";

    @BeforeEach
    void BeforeEach() throws IOException, URISyntaxException {
        TaskManager taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
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
        httpTaskServer.stop();
    }

    private void addObjectOnServer(Task task, URI uri) throws IOException, InterruptedException {
        String json = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
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
                .uri(URI.create(SERVER_URI + TASK_ENDPOINT + PARAMETER_ID + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Task.class);
    }

    private Epic getEpicById(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URI + EPIC_ENDPOINT + PARAMETER_ID + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Epic.class);
    }

    private Subtask getSubtaskById(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URI + SUBTASK_ENDPOINT + PARAMETER_ID + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Subtask.class);
    }

    private void deleteObjectById(URI uri, Task task) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + PARAMETER_ID + task.getId()))
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
        addObjectOnServer(firstTask, URI.create(SERVER_URI + TASK_ENDPOINT));
        addObjectOnServer(secondTask, URI.create(SERVER_URI + TASK_ENDPOINT));
        addObjectOnServer(firstEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        addObjectOnServer(secondEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        addObjectOnServer(firstSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        addObjectOnServer(secondSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        int expectedListSize = 4;
        prioritizedList = getTaskList(uriPrioritizedTasks);
        assertEquals(expectedListSize, prioritizedList.size(), "Неверная длинна списка.");
        assertTrue(prioritizedList.contains(firstTask), "Задача в списке получена некорректно.");
        // удаление Tasks
        deleteObjectsOfType(URI.create(SERVER_URI + TASK_ENDPOINT));
        prioritizedList = getTaskList(uriPrioritizedTasks);
        expectedListSize = 2;
        assertEquals(expectedListSize, prioritizedList.size(), "Списки не совпадают.");
        assertFalse(prioritizedList.contains(firstTask), "Список не должен содержать " + firstTask);
        // удаление всех задач
        deleteObjectsOfType(URI.create(SERVER_URI + SUBTASK_ENDPOINT));
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
        addObjectOnServer(firstTask, URI.create(SERVER_URI + TASK_ENDPOINT));
        addObjectOnServer(firstEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        addObjectOnServer(secondSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        List<Task> expectedList = new ArrayList<>();
        expectedList.add(getSubtaskById(secondSubtask.getId()));
        expectedList.add(getTaskById(firstTask.getId()));
        expectedList.add(getEpicById(firstEpic.getId()));
        historyList = getTaskList(uriHistory);
        assertEquals(expectedList.size(), historyList.size(), "Списки не совпадают.");
        assertEquals(expectedList.get(1), historyList.get(1), "Порядок задач не совпадает.");
        // удаление подзадачи
        deleteObjectsOfType(URI.create(SERVER_URI + SUBTASK_ENDPOINT));
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
        List<Epic> epicList = getEpicList(URI.create(SERVER_URI + EPIC_ENDPOINT));
        assertNull(epicList);
        assertNull(subtaskList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addObjectOnServer(firstEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        addObjectOnServer(firstSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        addObjectOnServer(secondSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        subtaskList = getSubtaskList(uriSubtasksFromEpic);
        int expectedListSize = 2;
        assertEquals(expectedListSize, subtaskList.size(), "Неверная длинна списка.");
        assertTrue(subtaskList.contains(firstSubtask), "Задача в списке получена некорректно.");
        Thread.sleep(100);
        // удаление подзадачи
        deleteObjectById(URI.create(SERVER_URI + SUBTASK_ENDPOINT), firstSubtask);
        expectedListSize = 1;
        subtaskList = getSubtaskList(uriSubtasksFromEpic);
        assertEquals(expectedListSize, subtaskList.size(), "Списки не совпадают.");
        assertFalse(subtaskList.contains(firstSubtask), "Список не должен содержать " + firstSubtask);
        Thread.sleep(100); // Тест без задержки не проходит (видимо из-за пинга)
        // удаление эпика
        deleteObjectsOfType(URI.create(SERVER_URI + EPIC_ENDPOINT));
        subtaskList = getSubtaskList(uriSubtasksFromEpic);
        assertNull(subtaskList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetAllTasks() throws IOException, InterruptedException {
        // пустой список
        List<Task> taskList = getTaskList(URI.create(SERVER_URI + TASK_ENDPOINT));
        assertNull(taskList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addObjectOnServer(firstTask, URI.create(SERVER_URI + TASK_ENDPOINT));
        addObjectOnServer(secondTask, URI.create(SERVER_URI + TASK_ENDPOINT));
        int expectedListSize = 2;
        taskList = getTaskList(URI.create(SERVER_URI + TASK_ENDPOINT));
        assertEquals(expectedListSize, taskList.size(), "Неверная длинна списка.");
        assertTrue(taskList.contains(firstTask), "Задача в списке получена некорректно.");
        assertTrue(taskList.contains(secondTask), "Задача в списке получена некорректно.");
        // удаление задач
        deleteObjectsOfType(URI.create(SERVER_URI + TASK_ENDPOINT));
        taskList = getTaskList(URI.create(SERVER_URI + TASK_ENDPOINT));
        assertNull(taskList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetTaskById() throws IOException, InterruptedException {
        // задача не существует
        Task task = getTaskById(firstTask.getId());
        assertNull(task, "Задача не должна существовать.");
        // получение задачи
        addObjectOnServer(firstTask, URI.create(SERVER_URI + TASK_ENDPOINT));
        task = getTaskById(firstTask.getId());
        assertEquals(firstTask, task, "Задачи не совпадают.");
        // удаление задачи
        deleteObjectById(URI.create(SERVER_URI + TASK_ENDPOINT), firstTask);
        task = getTaskById(firstTask.getId());
        assertNull(task, "Задача не удалена.");
    }

    @Test
    void handlePostTaskById() throws IOException, InterruptedException {
        // отправка задачи на сервер
        addObjectOnServer(firstTask, URI.create(SERVER_URI + TASK_ENDPOINT));
        Task task = getTaskById(firstTask.getId());
        assertEquals(firstTask, task, "Задача создана некорректно");
        // обновление задачи
        final Task updateTask = new Task(
                secondTask.getTaskName(),
                secondTask.getDescription(),
                firstTask.getId());
        addObjectOnServer(updateTask, URI.create(SERVER_URI + TASK_ENDPOINT));
        assertNotEquals(task, getTaskById(firstTask.getId()), "Задача не обновлена.");
        assertEquals(updateTask, getTaskById(firstTask.getId()), "Задача не обновлена.");
        // удаление задачи
        deleteObjectById(URI.create(SERVER_URI + TASK_ENDPOINT), firstTask);
        assertNull(getTaskById(firstTask.getId()), "Задача не удалена.");
    }

    @Test
    void handleDeleteAllTasks() throws IOException, InterruptedException {
        // нет задач
        assertNull(getTaskList(URI.create(SERVER_URI + TASK_ENDPOINT)), "Список задач должен быть пустым.");
        // удаление задач
        addObjectOnServer(firstTask, URI.create(SERVER_URI + TASK_ENDPOINT));
        addObjectOnServer(secondTask, URI.create(SERVER_URI + TASK_ENDPOINT));
        final int expectedSize = 2;
        assertEquals(expectedSize, getTaskList(URI.create(SERVER_URI + TASK_ENDPOINT)).size(), "Списки задач не совпадают.");
        deleteObjectsOfType(URI.create(SERVER_URI + TASK_ENDPOINT));
        assertNull(getTaskList(URI.create(SERVER_URI + TASK_ENDPOINT)), "Задачи не удалены.");
    }

    @Test
    void handleDeleteTaskById() throws IOException, InterruptedException {
        // нет задач
        assertNull(getTaskList(URI.create(SERVER_URI + TASK_ENDPOINT)), "Список задач должен быть пустым.");
        // удаление задачи по Id
        addObjectOnServer(firstTask, URI.create(SERVER_URI + TASK_ENDPOINT));
        addObjectOnServer(secondTask, URI.create(SERVER_URI + TASK_ENDPOINT));
        assertTrue(getTaskList(URI.create(SERVER_URI + TASK_ENDPOINT)).contains(firstTask), "Задача не добавлена.");
        deleteObjectById(URI.create(SERVER_URI + TASK_ENDPOINT), firstTask);
        assertFalse(getTaskList(URI.create(SERVER_URI + TASK_ENDPOINT)).contains(firstTask), "Задача не удалена.");
        assertTrue(getTaskList(URI.create(SERVER_URI + TASK_ENDPOINT)).contains(secondTask), "Задача не должна быть удалена.");
        deleteObjectById(URI.create(SERVER_URI + TASK_ENDPOINT), secondTask);
        assertNull(getTaskList(URI.create(SERVER_URI + TASK_ENDPOINT)), "Задача не удалена.");
    }

    @Test
    void handleGetAllEpics() throws IOException, InterruptedException {
        // пустой список
        List<Epic> epicList = getEpicList(URI.create(SERVER_URI + EPIC_ENDPOINT));
        assertNull(epicList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addObjectOnServer(firstEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        addObjectOnServer(secondEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        int expectedListSize = 2;
        epicList = getEpicList(URI.create(SERVER_URI + EPIC_ENDPOINT));
        assertEquals(expectedListSize, epicList.size(), "Неверная длинна списка.");
        assertTrue(epicList.contains(firstEpic), "Задача в списке получена некорректно.");
        assertTrue(epicList.contains(secondEpic), "Задача в списке получена некорректно.");
        // удаление задач
        deleteObjectsOfType(URI.create(SERVER_URI + EPIC_ENDPOINT));
        epicList = getEpicList(URI.create(SERVER_URI + EPIC_ENDPOINT));
        assertNull(epicList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetEpicById() throws IOException, InterruptedException {
        // задача не существует
        Epic epic = getEpicById(firstEpic.getId());
        assertNull(epic, "Задача не должна существовать.");
        // получение задачи
        addObjectOnServer(firstEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        epic = getEpicById(firstEpic.getId());
        assertEquals(firstEpic, epic, "Задачи не совпадают.");
        // удаление задачи
        deleteObjectById(URI.create(SERVER_URI + EPIC_ENDPOINT), firstEpic);
        epic = getEpicById(firstEpic.getId());
        assertNull(epic, "Задача не удалена.");
    }

    @Test
    void handlePostEpicById() throws IOException, InterruptedException {
        // отправка задачи на сервер
        addObjectOnServer(firstEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        Epic epic = getEpicById(firstEpic.getId());
        assertEquals(firstEpic, epic, "Задача создана некорректно");
        // обновление задачи
        final Epic updateEpic = new Epic(
                secondEpic.getTaskName(),
                secondEpic.getDescription(),
                firstEpic.getId());
        addObjectOnServer(updateEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        assertNotEquals(epic, getEpicById(firstEpic.getId()), "Задача не обновлена.");
        assertEquals(updateEpic, getEpicById(firstEpic.getId()), "Задача не обновлена.");
        // удаление задачи
        deleteObjectById(URI.create(SERVER_URI + EPIC_ENDPOINT), firstEpic);
        assertNull(getEpicById(firstEpic.getId()), "Задача не удалена.");
    }

    @Test
    void handleDeleteAllEpics() throws IOException, InterruptedException {
        // нет задач
        assertNull(getEpicList(URI.create(SERVER_URI + EPIC_ENDPOINT)), "Список задач должен быть пустым.");
        // удаление задач
        addObjectOnServer(firstEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        addObjectOnServer(secondEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        final int expectedSize = 2;
        assertEquals(expectedSize, getEpicList(URI.create(SERVER_URI + EPIC_ENDPOINT)).size(), "Списки задач не совпадают.");
        deleteObjectsOfType(URI.create(SERVER_URI + EPIC_ENDPOINT));
        assertNull(getEpicList(URI.create(SERVER_URI + EPIC_ENDPOINT)), "Задачи не удалены.");
    }

    @Test
    void handleDeleteEpicById() throws IOException, InterruptedException {
        // нет задач
        assertNull(getEpicList(URI.create(SERVER_URI + EPIC_ENDPOINT)), "Список задач должен быть пустым.");
        // удаление задачи по Id
        addObjectOnServer(firstEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        addObjectOnServer(secondEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        assertTrue(getEpicList(URI.create(SERVER_URI + EPIC_ENDPOINT)).contains(firstEpic), "Задача не добавлена.");
        deleteObjectById(URI.create(SERVER_URI + EPIC_ENDPOINT), firstEpic);
        assertFalse(getEpicList(URI.create(SERVER_URI + EPIC_ENDPOINT)).contains(firstEpic), "Задача не удалена.");
        assertTrue(getEpicList(URI.create(SERVER_URI + EPIC_ENDPOINT)).contains(secondEpic), "Задача не должна быть удалена.");
        deleteObjectById(URI.create(SERVER_URI + EPIC_ENDPOINT), secondEpic);
        assertNull(getEpicList(URI.create(SERVER_URI + EPIC_ENDPOINT)), "Задача не удалена.");
    }

    @Test
    void handleGetAllSubtasks() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        // пустой список
        List<Subtask> subtaskList = getSubtaskList(URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        assertNull(subtaskList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addObjectOnServer(firstSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        addObjectOnServer(secondSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        int expectedListSize = 2;
        subtaskList = getSubtaskList(URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        assertEquals(expectedListSize, subtaskList.size(), "Неверная длинна списка.");
        assertTrue(subtaskList.contains(firstSubtask), "Задача в списке получена некорректно.");
        assertTrue(subtaskList.contains(secondSubtask), "Задача в списке получена некорректно.");
        // удаление задач
        deleteObjectsOfType(URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        subtaskList = getSubtaskList(URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        assertNull(subtaskList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetSubtaskById() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        // задача не существует
        Subtask subtask = getSubtaskById(firstSubtask.getId());
        assertNull(subtask, "Задача не должна существовать.");
        // получение задачи
        addObjectOnServer(firstSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        subtask = getSubtaskById(firstSubtask.getId());
        assertEquals(firstSubtask, subtask, "Задачи не совпадают.");
        // удаление задачи
        deleteObjectById(URI.create(SERVER_URI + SUBTASK_ENDPOINT), firstSubtask);
        subtask = getSubtaskById(firstSubtask.getId());
        assertNull(subtask, "Задача не удалена.");
    }

    @Test
    void handlePostSubtaskById() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        // отправка задачи на сервер
        addObjectOnServer(firstSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        Subtask subtask = getSubtaskById(firstSubtask.getId());
        assertEquals(firstSubtask, subtask, "Задача создана некорректно");
        // обновление задачи
        final Subtask updateSubtask = new Subtask(
                secondSubtask.getTaskName(),
                secondSubtask.getDescription(),
                firstSubtask.getId(),
                firstEpic.getId());
        addObjectOnServer(updateSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        assertNotEquals(subtask, getSubtaskById(firstSubtask.getId()), "Задача не обновлена.");
        assertEquals(updateSubtask, getSubtaskById(firstSubtask.getId()), "Задача не обновлена.");
        // удаление задачи
        deleteObjectById(URI.create(SERVER_URI + SUBTASK_ENDPOINT), firstSubtask);
        assertNull(getSubtaskById(firstSubtask.getId()), "Задача не удалена.");
    }

    @Test
    void handleDeleteAllSubtasks() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        // нет задач
        assertNull(getSubtaskList(URI.create(SERVER_URI + SUBTASK_ENDPOINT)), "Список задач должен быть пустым.");
        // удаление задач
        addObjectOnServer(firstSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        addObjectOnServer(secondSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        final int expectedSize = 2;
        assertEquals(expectedSize, getSubtaskList(URI.create(SERVER_URI + SUBTASK_ENDPOINT)).size(), "Списки задач не совпадают.");
        deleteObjectsOfType(URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        assertNull(getSubtaskList(URI.create(SERVER_URI + SUBTASK_ENDPOINT)), "Задачи не удалены.");
    }

    @Test
    void handleDeleteSubtaskById() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, URI.create(SERVER_URI + EPIC_ENDPOINT));
        // нет задач
        assertNull(getSubtaskList(URI.create(SERVER_URI + SUBTASK_ENDPOINT)), "Список задач должен быть пустым.");
        // удаление задачи по Id
        addObjectOnServer(firstSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        addObjectOnServer(secondSubtask, URI.create(SERVER_URI + SUBTASK_ENDPOINT));
        assertTrue(getSubtaskList(URI.create(SERVER_URI + SUBTASK_ENDPOINT)).contains(firstSubtask), "Задача не добавлена.");
        deleteObjectById(URI.create(SERVER_URI + SUBTASK_ENDPOINT), firstSubtask);
        assertFalse(getSubtaskList(URI.create(SERVER_URI + SUBTASK_ENDPOINT)).contains(firstSubtask), "Задача не удалена.");
        assertTrue(getSubtaskList(URI.create(SERVER_URI + SUBTASK_ENDPOINT)).contains(secondSubtask), "Задача не должна быть удалена.");
        deleteObjectById(URI.create(SERVER_URI + SUBTASK_ENDPOINT), secondSubtask);
        assertNull(getSubtaskList(URI.create(SERVER_URI + SUBTASK_ENDPOINT)), "Задача не удалена.");
    }
}