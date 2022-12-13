package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;

import java.io.File;

public final class Managers {
    public static TaskManager getDefault() {
        return FileBackedTasksManager
                .loadFromFile(new File("src/ru/yandex/practicum/taskTracker/files/backup-task-manager.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}