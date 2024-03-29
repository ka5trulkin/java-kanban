package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.taskTracker.http.KVServer;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static KVServer kvServer;

    public HttpTaskManagerTest() throws URISyntaxException {
        super(new HttpTaskManager(new URI("http://localhost:8078")));
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterEach
    void afterEach() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
    }

    @AfterAll
    static void afterAll() {
        kvServer.stop();
    }

    @Test
    void loadFromServer() throws URISyntaxException {
        TaskManager loadedManager = Managers.getDefault();
        // loadedManager изначально должен быть пустым
        assertEquals(Collections.emptyList(), loadedManager.getTasks(), "Список должен быть пустым.");
        assertEquals(Collections.emptyList(), loadedManager.getEpics(), "Список должен быть пустым.");
        assertEquals(Collections.emptyList(), loadedManager.getSubtasks(), "Список должен быть пустым.");
        // загрузка задач в loadedManager с сервера
        taskList.forEach(manager::addNewTask);
        loadedManager = Managers.getDefault();
        assertEquals(manager.getTasks(), loadedManager.getTasks(), "Задачи не совпадают.");
        // все задачи должны быть удалены
        manager.deleteAllTasks();
        loadedManager = Managers.getDefault();
        assertEquals(Collections.emptyList(), loadedManager.getTasks(), "Задачи не удалены.");
        // загрузка эпиков в loadedManager с сервера
        epicList.forEach(manager::addNewEpic);
        loadedManager = Managers.getDefault();
        assertEquals(manager.getEpics(), loadedManager.getEpics(), "Списки эпиков не совпадает.");
        assertEquals(Collections.emptyList(), loadedManager.getSubtasks(), "Список должен быть пустым.");
        subtaskList.forEach(manager::addNewSubtask);
        loadedManager = Managers.getDefault();
        assertEquals(manager.getEpics(), loadedManager.getEpics(), "Список эпиков не совпадает.");
        assertEquals(manager.getSubtasks(), loadedManager.getSubtasks(), "Списки подзадач не совпадают.");
        manager.updateEpic(epicTest);
        // все эпики и подзадачи должны быть удалены
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
        loadedManager = Managers.getDefault();
        assertEquals(Collections.emptyList(), loadedManager.getEpics(), "Эпики не удалены.");
        assertEquals(Collections.emptyList(), loadedManager.getSubtasks(), "Подзадачи не удалены.");
        // добавление всех задач, эпиков и подзадач
        taskList.forEach(manager::addNewTask);
        epicList.forEach(manager::addNewEpic);
        subtaskList.forEach(manager::addNewSubtask);
        loadedManager = Managers.getDefault();
        assertEquals(manager.getTasks(), loadedManager.getTasks(), "Задачи не совпадают.");
        assertEquals(manager.getEpics(), loadedManager.getEpics(), "Эпики не совпадают.");
        assertEquals(manager.getSubtasks(), loadedManager.getSubtasks(), "Подзадачи не совпадают.");
        // проверка восстановления очередности присвоения ID
        final int expectedId = manager.getTasks().size()
                + manager.getEpics().size()
                + manager.getSubtasks().size();
        assertEquals(expectedId, loadedManager.assignID() - 1, "Восстановлен не верный ID.");
        // проверка восстановления истории задач
        assertEquals(manager.getHistory(), loadedManager.getHistory(), "Список истории не совпадает.");
        manager.getEpics();
        assertNotEquals(manager.getHistory(), loadedManager.getHistory(), "Списки истории не должны совпадать.");
        loadedManager = Managers.getDefault();
        assertEquals(manager.getHistory(), loadedManager.getHistory(), "Список истории не совпадает.");
        manager.deleteTaskById(taskTest.getId());
        assertNotEquals(manager.getHistory(), loadedManager.getHistory(), "Списки истории не должны совпадать.");
        loadedManager = Managers.getDefault();
        assertEquals(manager.getHistory(), loadedManager.getHistory(), "Список истории не совпадает.");
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
        loadedManager = Managers.getDefault();
        assertEquals(Collections.emptyList(), loadedManager.getHistory(), "Список истории должен быть пуст.");
    }
}