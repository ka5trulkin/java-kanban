package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.http.HttpTaskServer;
import ru.yandex.practicum.taskTracker.http.KVServer;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    public HttpTaskManagerTest(HttpTaskManager manager) throws URISyntaxException {
        super(new HttpTaskManager(new URI("http://localhost:8078")));
    }

    @BeforeAll
    static void beforeAll() throws IOException, URISyntaxException {
        new KVServer().start();
        new HttpTaskServer().start();
    }

    @Test
    void loadFromServer() throws URISyntaxException {
        TaskManager loadedManager = new HttpTaskManager(new URI("http://localhost:8078"));

        assertEquals(Collections.emptyList(), loadedManager.getTasks(), "Списки не совпадают.");
        assertEquals(Collections.emptyList(), loadedManager.getEpics(), "Списки не совпадают.");
        assertEquals(Collections.emptyList(), loadedManager.getSubtasks(), "Списки не совпадают.");
//        taskList.forEach(manager::addNewTask);
//        loadedManager = FileBackedTasksManager.loadFromFile(new File("resource/backup-task-manager-test.csv"));
//        assertEquals(manager.getTasks(), loadedManager.getTasks(), "Списки не совпадают.");
//        manager.deleteAllTasks();
//        epicList.forEach(manager::addNewEpic);
//        loadedManager = FileBackedTasksManager.loadFromFile(new File("resource/backup-task-manager-test.csv"));
//        assertEquals(manager.getEpics(), loadedManager.getEpics(), "Списки не совпадают.");
//        assertEquals(manager.getSubtasks(), loadedManager.getSubtasks(), "Списки не совпадают.");
//        manager.deleteAllEpics();
//        loadedManager = FileBackedTasksManager.loadFromFile(new File("resource/backup-task-manager-test.csv"));
//        assertEquals(manager.getEpics(), loadedManager.getEpics(), "Списки не совпадают.");
//        taskList.forEach(manager::addNewTask);
//        epicList.forEach(manager::addNewEpic);
//        subtaskList.forEach(manager::addNewSubtask);
//        loadedManager = FileBackedTasksManager.loadFromFile(new File("resource/backup-task-manager-test.csv"));
//        assertEquals(manager.getTasks(), loadedManager.getTasks(), "Списки не совпадают.");
//        assertEquals(manager.getEpics(), loadedManager.getEpics(), "Списки не совпадают.");
//        assertEquals(manager.getSubtasks(), loadedManager.getSubtasks(), "Списки не совпадают.");
//
//        final int expectedId = manager.getTasks().size()
//                + manager.getEpics().size()
//                + manager.getSubtasks().size();
//        assertEquals(expectedId, loadedManager.assignID() - 1, "Восстановлен не верный ID.");
//
//        TaskManager mustBeRecoveredFromHistory = FileBackedTasksManager.loadFromFile(new File("resource/backup-task-manager-test.csv"));
//        assertEquals(loadedManager.getTasks(), mustBeRecoveredFromHistory.getTasks(), "Списки не совпадают.");
//        assertEquals(loadedManager.getEpics(), mustBeRecoveredFromHistory.getEpics(), "Списки не совпадают.");
//        assertEquals(loadedManager.getSubtasks(), mustBeRecoveredFromHistory.getSubtasks(), "Списки не совпадают.");
//
//        assertNotEquals(Collections.emptyList(), mustBeRecoveredFromHistory.getPrioritizedTasks(), "Список не должен быть пустым.");
    }
}