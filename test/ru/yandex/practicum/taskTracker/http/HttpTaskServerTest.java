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
    private final URI serverUri = URI.create("http://localhost:8080/tasks");
    private final String taskEndpoint = "/task";
    private final String epicEndpoint = "/epic";
    private final String subtaskEndpoint = "/subtask";
    private final String parameterId = "?id=";

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
                .uri(URI.create(serverUri + taskEndpoint + parameterId + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Task.class);
    }

    private Epic getEpicById(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUri + epicEndpoint + parameterId + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Epic.class);
    }

    private Subtask getSubtaskById(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUri + subtaskEndpoint + parameterId + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Subtask.class);
    }

    private void deleteObjectById(URI uri, Task task) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + parameterId + task.getId()))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void handleGetPrioritizedTasks() throws IOException, InterruptedException {
        // пустой список
        List<Task> prioritizedList = getTaskList(serverUri);
        assertNull(prioritizedList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addObjectOnServer(firstTask, URI.create(serverUri + taskEndpoint));
        addObjectOnServer(secondTask, URI.create(serverUri + taskEndpoint));
        addObjectOnServer(firstEpic, URI.create(serverUri + epicEndpoint));
        addObjectOnServer(secondEpic, URI.create(serverUri + epicEndpoint));
        addObjectOnServer(firstSubtask, URI.create(serverUri + subtaskEndpoint));
        addObjectOnServer(secondSubtask, URI.create(serverUri + subtaskEndpoint));
        int expectedListSize = 4;
        prioritizedList = getTaskList(serverUri);
        assertEquals(expectedListSize, prioritizedList.size(), "Неверная длинна списка.");
        assertTrue(prioritizedList.contains(firstTask), "Задача в списке получена некорректно.");
        // удаление Tasks
        deleteObjectsOfType(URI.create(serverUri + taskEndpoint));
        prioritizedList = getTaskList(serverUri);
        expectedListSize = 2;
        assertEquals(expectedListSize, prioritizedList.size(), "Списки не совпадают.");
        assertFalse(prioritizedList.contains(firstTask), "Список не должен содержать " + firstTask);
        // удаление всех задач
        deleteObjectsOfType(URI.create(serverUri + subtaskEndpoint));
        prioritizedList = getTaskList(serverUri);
        assertNull(prioritizedList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetHistory() throws IOException, InterruptedException {
        URI uriHistory = URI.create(serverUri + "/history");
        // пустой список
        List<Task> historyList = getTaskList(uriHistory);
        assertNull(historyList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addObjectOnServer(firstTask, URI.create(serverUri + taskEndpoint));
        addObjectOnServer(firstEpic, URI.create(serverUri + epicEndpoint));
        addObjectOnServer(secondSubtask, URI.create(serverUri + subtaskEndpoint));
        List<Task> expectedList = new ArrayList<>();
        expectedList.add(getSubtaskById(secondSubtask.getId()));
        expectedList.add(getTaskById(firstTask.getId()));
        expectedList.add(getEpicById(firstEpic.getId()));
        historyList = getTaskList(uriHistory);
        assertEquals(expectedList.size(), historyList.size(), "Списки не совпадают.");
        assertEquals(expectedList.get(1), historyList.get(1), "Порядок задач не совпадает.");
        // удаление подзадачи
        deleteObjectsOfType(URI.create(serverUri + subtaskEndpoint));
        expectedList.remove(secondSubtask);
        historyList = getTaskList(uriHistory);
        assertEquals(expectedList.size(), historyList.size(), "Списки не совпадают.");
        assertEquals(expectedList.get(0), historyList.get(0), "Порядок задач не совпадает.");
    }

    @Test
    void handleGetSubtasksFromEpic() throws IOException, InterruptedException {
        URI uriSubtasksFromEpic = URI.create(serverUri + "/subtask/epic?id=" + firstEpic.getId());
        // пустой список
        List<Subtask> subtaskList = getSubtaskList(uriSubtasksFromEpic);
        List<Epic> epicList = getEpicList(URI.create(serverUri + epicEndpoint));
        assertNull(epicList);
        assertNull(subtaskList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addObjectOnServer(firstEpic, URI.create(serverUri + epicEndpoint));
        addObjectOnServer(firstSubtask, URI.create(serverUri + subtaskEndpoint));
        addObjectOnServer(secondSubtask, URI.create(serverUri + subtaskEndpoint));
        subtaskList = getSubtaskList(uriSubtasksFromEpic);
        int expectedListSize = 2;
        assertEquals(expectedListSize, subtaskList.size(), "Неверная длинна списка.");
        assertTrue(subtaskList.contains(firstSubtask), "Задача в списке получена некорректно.");
        Thread.sleep(100);
        // удаление подзадачи
        deleteObjectById(URI.create(serverUri + subtaskEndpoint), firstSubtask);
        expectedListSize = 1;
        subtaskList = getSubtaskList(uriSubtasksFromEpic);
        assertEquals(expectedListSize, subtaskList.size(), "Списки не совпадают.");
        assertFalse(subtaskList.contains(firstSubtask), "Список не должен содержать " + firstSubtask);
        Thread.sleep(100); // Тест без задержки не проходит (видимо из-за пинга)
        // удаление эпика
        deleteObjectsOfType(URI.create(serverUri + epicEndpoint));
        subtaskList = getSubtaskList(uriSubtasksFromEpic);
        assertNull(subtaskList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetAllTasks() throws IOException, InterruptedException {
        // пустой список
        List<Task> taskList = getTaskList(URI.create(serverUri + taskEndpoint));
        assertNull(taskList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addObjectOnServer(firstTask, URI.create(serverUri + taskEndpoint));
        addObjectOnServer(secondTask, URI.create(serverUri + taskEndpoint));
        int expectedListSize = 2;
        taskList = getTaskList(URI.create(serverUri + taskEndpoint));
        assertEquals(expectedListSize, taskList.size(), "Неверная длинна списка.");
        assertTrue(taskList.contains(firstTask), "Задача в списке получена некорректно.");
        assertTrue(taskList.contains(secondTask), "Задача в списке получена некорректно.");
        // удаление задач
        deleteObjectsOfType(URI.create(serverUri + taskEndpoint));
        taskList = getTaskList(URI.create(serverUri + taskEndpoint));
        assertNull(taskList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetTaskById() throws IOException, InterruptedException {
        // задача не существует
        Task task = getTaskById(firstTask.getId());
        assertNull(task, "Задача не должна существовать.");
        // получение задачи
        addObjectOnServer(firstTask, URI.create(serverUri + taskEndpoint));
        task = getTaskById(firstTask.getId());
        assertEquals(firstTask, task, "Задачи не совпадают.");
        // удаление задачи
        deleteObjectById(URI.create(serverUri + taskEndpoint), firstTask);
        task = getTaskById(firstTask.getId());
        assertNull(task, "Задача не удалена.");
    }

    @Test
    void handlePostTaskById() throws IOException, InterruptedException {
        // отправка задачи на сервер
        addObjectOnServer(firstTask, URI.create(serverUri + taskEndpoint));
        Task task = getTaskById(firstTask.getId());
        assertEquals(firstTask, task, "Задача создана некорректно");
        // обновление задачи
        final Task updateTask = new Task(
                secondTask.getTaskName(),
                secondTask.getDescription(),
                firstTask.getId());
        addObjectOnServer(updateTask, URI.create(serverUri + taskEndpoint));
        assertNotEquals(task, getTaskById(firstTask.getId()), "Задача не обновлена.");
        assertEquals(updateTask, getTaskById(firstTask.getId()), "Задача не обновлена.");
        // удаление задачи
        deleteObjectById(URI.create(serverUri + taskEndpoint), firstTask);
        assertNull(getTaskById(firstTask.getId()), "Задача не удалена.");
    }

    @Test
    void handleDeleteAllTasks() throws IOException, InterruptedException {
        // нет задач
        assertNull(getTaskList(URI.create(serverUri + taskEndpoint)), "Список задач должен быть пустым.");
        // удаление задач
        addObjectOnServer(firstTask, URI.create(serverUri + taskEndpoint));
        addObjectOnServer(secondTask, URI.create(serverUri + taskEndpoint));
        final int expectedSize = 2;
        assertEquals(expectedSize, getTaskList(URI.create(serverUri + taskEndpoint)).size(), "Списки задач не совпадают.");
        deleteObjectsOfType(URI.create(serverUri + taskEndpoint));
        assertNull(getTaskList(URI.create(serverUri + taskEndpoint)), "Задачи не удалены.");
    }

    @Test
    void handleDeleteTaskById() throws IOException, InterruptedException {
        // нет задач
        assertNull(getTaskList(URI.create(serverUri + taskEndpoint)), "Список задач должен быть пустым.");
        // удаление задачи по Id
        addObjectOnServer(firstTask, URI.create(serverUri + taskEndpoint));
        addObjectOnServer(secondTask, URI.create(serverUri + taskEndpoint));
        assertTrue(getTaskList(URI.create(serverUri + taskEndpoint)).contains(firstTask), "Задача не добавлена.");
        deleteObjectById(URI.create(serverUri + taskEndpoint), firstTask);
        assertFalse(getTaskList(URI.create(serverUri + taskEndpoint)).contains(firstTask), "Задача не удалена.");
        assertTrue(getTaskList(URI.create(serverUri + taskEndpoint)).contains(secondTask), "Задача не должна быть удалена.");
        deleteObjectById(URI.create(serverUri + taskEndpoint), secondTask);
        assertNull(getTaskList(URI.create(serverUri + taskEndpoint)), "Задача не удалена.");
    }

    @Test
    void handleGetAllEpics() throws IOException, InterruptedException {
        // пустой список
        List<Epic> epicList = getEpicList(URI.create(serverUri + epicEndpoint));
        assertNull(epicList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addObjectOnServer(firstEpic, URI.create(serverUri + epicEndpoint));
        addObjectOnServer(secondEpic, URI.create(serverUri + epicEndpoint));
        int expectedListSize = 2;
        epicList = getEpicList(URI.create(serverUri + epicEndpoint));
        assertEquals(expectedListSize, epicList.size(), "Неверная длинна списка.");
        assertTrue(epicList.contains(firstEpic), "Задача в списке получена некорректно.");
        assertTrue(epicList.contains(secondEpic), "Задача в списке получена некорректно.");
        // удаление задач
        deleteObjectsOfType(URI.create(serverUri + epicEndpoint));
        epicList = getEpicList(URI.create(serverUri + epicEndpoint));
        assertNull(epicList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetEpicById() throws IOException, InterruptedException {
        // задача не существует
        Epic epic = getEpicById(firstEpic.getId());
        assertNull(epic, "Задача не должна существовать.");
        // получение задачи
        addObjectOnServer(firstEpic, URI.create(serverUri + epicEndpoint));
        epic = getEpicById(firstEpic.getId());
        assertEquals(firstEpic, epic, "Задачи не совпадают.");
        // удаление задачи
        deleteObjectById(URI.create(serverUri + epicEndpoint), firstEpic);
        epic = getEpicById(firstEpic.getId());
        assertNull(epic, "Задача не удалена.");
    }

    @Test
    void handlePostEpicById() throws IOException, InterruptedException {
        // отправка задачи на сервер
        addObjectOnServer(firstEpic, URI.create(serverUri + epicEndpoint));
        Epic epic = getEpicById(firstEpic.getId());
        assertEquals(firstEpic, epic, "Задача создана некорректно");
        // обновление задачи
        final Epic updateEpic = new Epic(
                secondEpic.getTaskName(),
                secondEpic.getDescription(),
                firstEpic.getId());
        addObjectOnServer(updateEpic, URI.create(serverUri + epicEndpoint));
        assertNotEquals(epic, getEpicById(firstEpic.getId()), "Задача не обновлена.");
        assertEquals(updateEpic, getEpicById(firstEpic.getId()), "Задача не обновлена.");
        // удаление задачи
        deleteObjectById(URI.create(serverUri + epicEndpoint), firstEpic);
        assertNull(getEpicById(firstEpic.getId()), "Задача не удалена.");
    }

    @Test
    void handleDeleteAllEpics() throws IOException, InterruptedException {
        // нет задач
        assertNull(getEpicList(URI.create(serverUri + epicEndpoint)), "Список задач должен быть пустым.");
        // удаление задач
        addObjectOnServer(firstEpic, URI.create(serverUri + epicEndpoint));
        addObjectOnServer(secondEpic, URI.create(serverUri + epicEndpoint));
        final int expectedSize = 2;
        assertEquals(expectedSize, getEpicList(URI.create(serverUri + epicEndpoint)).size(), "Списки задач не совпадают.");
        deleteObjectsOfType(URI.create(serverUri + epicEndpoint));
        assertNull(getEpicList(URI.create(serverUri + epicEndpoint)), "Задачи не удалены.");
    }

    @Test
    void handleDeleteEpicById() throws IOException, InterruptedException {
        // нет задач
        assertNull(getEpicList(URI.create(serverUri + epicEndpoint)), "Список задач должен быть пустым.");
        // удаление задачи по Id
        addObjectOnServer(firstEpic, URI.create(serverUri + epicEndpoint));
        addObjectOnServer(secondEpic, URI.create(serverUri + epicEndpoint));
        assertTrue(getEpicList(URI.create(serverUri + epicEndpoint)).contains(firstEpic), "Задача не добавлена.");
        deleteObjectById(URI.create(serverUri + epicEndpoint), firstEpic);
        assertFalse(getEpicList(URI.create(serverUri + epicEndpoint)).contains(firstEpic), "Задача не удалена.");
        assertTrue(getEpicList(URI.create(serverUri + epicEndpoint)).contains(secondEpic), "Задача не должна быть удалена.");
        deleteObjectById(URI.create(serverUri + epicEndpoint), secondEpic);
        assertNull(getEpicList(URI.create(serverUri + epicEndpoint)), "Задача не удалена.");
    }

    @Test
    void handleGetAllSubtasks() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, URI.create(serverUri + epicEndpoint));
        // пустой список
        List<Subtask> subtaskList = getSubtaskList(URI.create(serverUri + subtaskEndpoint));
        assertNull(subtaskList, "Список не должен быть возвращен.");
        // проверка состояния списка
        addObjectOnServer(firstSubtask, URI.create(serverUri + subtaskEndpoint));
        addObjectOnServer(secondSubtask, URI.create(serverUri + subtaskEndpoint));
        int expectedListSize = 2;
        subtaskList = getSubtaskList(URI.create(serverUri + subtaskEndpoint));
        assertEquals(expectedListSize, subtaskList.size(), "Неверная длинна списка.");
        assertTrue(subtaskList.contains(firstSubtask), "Задача в списке получена некорректно.");
        assertTrue(subtaskList.contains(secondSubtask), "Задача в списке получена некорректно.");
        // удаление задач
        deleteObjectsOfType(URI.create(serverUri + subtaskEndpoint));
        subtaskList = getSubtaskList(URI.create(serverUri + subtaskEndpoint));
        assertNull(subtaskList, "Список не должен быть возвращен.");
    }

    @Test
    void handleGetSubtaskById() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, URI.create(serverUri + epicEndpoint));
        // задача не существует
        Subtask subtask = getSubtaskById(firstSubtask.getId());
        assertNull(subtask, "Задача не должна существовать.");
        // получение задачи
        addObjectOnServer(firstSubtask, URI.create(serverUri + subtaskEndpoint));
        subtask = getSubtaskById(firstSubtask.getId());
        assertEquals(firstSubtask, subtask, "Задачи не совпадают.");
        // удаление задачи
        deleteObjectById(URI.create(serverUri + subtaskEndpoint), firstSubtask);
        subtask = getSubtaskById(firstSubtask.getId());
        assertNull(subtask, "Задача не удалена.");
    }

    @Test
    void handlePostSubtaskById() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, URI.create(serverUri + epicEndpoint));
        // отправка задачи на сервер
        addObjectOnServer(firstSubtask, URI.create(serverUri + subtaskEndpoint));
        Subtask subtask = getSubtaskById(firstSubtask.getId());
        assertEquals(firstSubtask, subtask, "Задача создана некорректно");
        // обновление задачи
        final Subtask updateSubtask = new Subtask(
                secondSubtask.getTaskName(),
                secondSubtask.getDescription(),
                firstSubtask.getId(),
                firstEpic.getId());
        addObjectOnServer(updateSubtask, URI.create(serverUri + subtaskEndpoint));
        assertNotEquals(subtask, getSubtaskById(firstSubtask.getId()), "Задача не обновлена.");
        assertEquals(updateSubtask, getSubtaskById(firstSubtask.getId()), "Задача не обновлена.");
        // удаление задачи
        deleteObjectById(URI.create(serverUri + subtaskEndpoint), firstSubtask);
        assertNull(getSubtaskById(firstSubtask.getId()), "Задача не удалена.");
    }

    @Test
    void handleDeleteAllSubtasks() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, URI.create(serverUri + epicEndpoint));
        // нет задач
        assertNull(getSubtaskList(URI.create(serverUri + subtaskEndpoint)), "Список задач должен быть пустым.");
        // удаление задач
        addObjectOnServer(firstSubtask, URI.create(serverUri + subtaskEndpoint));
        addObjectOnServer(secondSubtask, URI.create(serverUri + subtaskEndpoint));
        final int expectedSize = 2;
        assertEquals(expectedSize, getSubtaskList(URI.create(serverUri + subtaskEndpoint)).size(), "Списки задач не совпадают.");
        deleteObjectsOfType(URI.create(serverUri + subtaskEndpoint));
        assertNull(getSubtaskList(URI.create(serverUri + subtaskEndpoint)), "Задачи не удалены.");
    }

    @Test
    void handleDeleteSubtaskById() throws IOException, InterruptedException {
        addObjectOnServer(firstEpic, URI.create(serverUri + epicEndpoint));
        // нет задач
        assertNull(getSubtaskList(URI.create(serverUri + subtaskEndpoint)), "Список задач должен быть пустым.");
        // удаление задачи по Id
        addObjectOnServer(firstSubtask, URI.create(serverUri + subtaskEndpoint));
        addObjectOnServer(secondSubtask, URI.create(serverUri + subtaskEndpoint));
        assertTrue(getSubtaskList(URI.create(serverUri + subtaskEndpoint)).contains(firstSubtask), "Задача не добавлена.");
        deleteObjectById(URI.create(serverUri + subtaskEndpoint), firstSubtask);
        assertFalse(getSubtaskList(URI.create(serverUri + subtaskEndpoint)).contains(firstSubtask), "Задача не удалена.");
        assertTrue(getSubtaskList(URI.create(serverUri + subtaskEndpoint)).contains(secondSubtask), "Задача не должна быть удалена.");
        deleteObjectById(URI.create(serverUri + subtaskEndpoint), secondSubtask);
        assertNull(getSubtaskList(URI.create(serverUri + subtaskEndpoint)), "Задача не удалена.");
    }
}