package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;

import java.io.File;

public final class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTasksManager(new File("resource/backup-task-manager.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}