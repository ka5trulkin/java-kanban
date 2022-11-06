package ru.yandex.practicum.taskTracker.interfaces;

import ru.yandex.practicum.taskTracker.model.Task;

import java.util.List;

public interface HistoryManager {
    int MAX_TASKS_IN_HISTORY = 10;

    void add(Task task);

    List<Task> getHistory();
}