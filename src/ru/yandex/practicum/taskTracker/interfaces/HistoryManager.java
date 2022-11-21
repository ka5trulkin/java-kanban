package ru.yandex.practicum.taskTracker.interfaces;

import ru.yandex.practicum.taskTracker.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    void clearAll(List<Integer> idList);

    List<Task> getHistory();
}