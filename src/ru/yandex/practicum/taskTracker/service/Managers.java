package ru.yandex.practicum.taskTracker.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.taskTracker.gson.DurationAdapter;
import ru.yandex.practicum.taskTracker.gson.LocalDateTimeAdapter;
import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public final class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTasksManager(new File("resource/backup-task-manager.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }
}