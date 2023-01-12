package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    FileBackedTasksManager manager = new FileBackedTasksManager(new File("resource/backup-task-manager.csv"));

    @Test
    void loadFromFile() {
    }

    @Test
    void historyFromString() {
    }

    @Test
    void historyToString() {
    }

    @Test
    void getTasks() {
    }

    @Test
    void getEpics() {
    }

    @Test
    void getSubtasks() {
    }

    @Test
    void clearAllTasks() {
    }

    @Test
    void clearAllEpics() {
    }

    @Test
    void clearAllSubtasks() {
    }

    @Test
    void getTaskById() {
    }

    @Test
    void getEpicById() {
    }

    @Test
    void getSubTaskById() {
    }

    @Test
    void addNewTask() {
    }

    @Test
    void addNewEpic() {
    }

    @Test
    void addNewSubtask() {
    }

    @Test
    void updateTask() {
    }

    @Test
    void updateEpic() {
    }

    @Test
    void updateSubtask() {
    }

    @Test
    void removeTaskById() {
    }

    @Test
    void removeEpicById() {
    }

    @Test
    void removeSubtaskById() {
    }
}