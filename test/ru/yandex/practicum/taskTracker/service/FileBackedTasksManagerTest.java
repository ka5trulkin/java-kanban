package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;

import java.io.File;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    public FileBackedTasksManagerTest() {
        super(new FileBackedTasksManager(new File("resource/backup-task-manager-test.csv")));
    }

    @Test
    void loadFromFile() {
        TaskManager loadedManager = new FileBackedTasksManager(new File("resource/backup-task-manager-test.csv"));

        assertEquals(Collections.emptyList(), loadedManager.getTasks(), "Списки не совпадают.");
        assertEquals(Collections.emptyList(), loadedManager.getEpics(), "Списки не совпадают.");
        assertEquals(Collections.emptyList(), loadedManager.getSubtasks(), "Списки не совпадают.");
        tasksList.forEach(manager::addNewTask);
        loadedManager = FileBackedTasksManager.loadFromFile(new File("resource/backup-task-manager-test.csv"));
        assertEquals(manager.getTasks(), loadedManager.getTasks(), "Списки не совпадают.");
        manager.clearAllTasks();
        epicList.forEach(manager::addNewEpic);
        loadedManager = FileBackedTasksManager.loadFromFile(new File("resource/backup-task-manager-test.csv"));
        assertEquals(manager.getEpics(), loadedManager.getEpics(), "Списки не совпадают.");
        assertEquals(manager.getSubtasks(), loadedManager.getSubtasks(), "Списки не совпадают.");
        manager.clearAllEpics();
        loadedManager = FileBackedTasksManager.loadFromFile(new File("resource/backup-task-manager-test.csv"));
        assertEquals(manager.getEpics(), loadedManager.getEpics(), "Списки не совпадают.");
        tasksList.forEach(manager::addNewTask);
        epicList.forEach(manager::addNewEpic);
        subtaskList.forEach(manager::addNewSubtask);
        loadedManager = FileBackedTasksManager.loadFromFile(new File("resource/backup-task-manager-test.csv"));
        assertEquals(manager.getTasks(), loadedManager.getTasks(), "Списки не совпадают.");
        assertEquals(manager.getEpics(), loadedManager.getEpics(), "Списки не совпадают.");
        assertEquals(manager.getSubtasks(), loadedManager.getSubtasks(), "Списки не совпадают.");
    }
}