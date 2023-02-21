package ru.yandex.practicum.taskTracker.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.taskTracker.http.KVTaskClient;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.taskTracker.model.Type.*;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson = Managers.getGson();

    public HttpTaskManager(URI uri) {
        this.client = new KVTaskClient(uri);
        this.loadDataFromServer();
    }

    private void checkId(int id) {
        if (idCounter < id) {
            idCounter = id;
        }
    }

    private List<Integer> historyToInteger() {
        return getHistory().stream().map(Task::getId).collect(Collectors.toList());
    }

    private void loadDataFromServer() {
        loadTasksFromServer();
        loadEpicsFromServer();
        loadSubtasksFromServer();
        loadHistoryFromServer();
    }

    private void loadTasksFromServer() {
        List<Task> tasksList
                = gson.fromJson(client.load(TASK.toLowerCase()), new TypeToken<ArrayList<Task>>() {}.getType());
        if (tasksList != null) {
            tasksList.forEach(task -> {
                tasks.put(task.getId(), task);
                checkId(task.getId());
            });
        }
    }

    private void loadEpicsFromServer() {
        List<Epic> epicsList
                = gson.fromJson(client.load(EPIC.toLowerCase()), new TypeToken<ArrayList<Epic>>() {}.getType());
        if (epicsList != null) {
            epicsList.forEach(epic -> {
                epics.put(epic.getId(), epic);
                checkId(epic.getId());
            });
        }
    }

    private void loadSubtasksFromServer() {
        List<Subtask> subtasksList
                = gson.fromJson(client.load(SUBTASK.toLowerCase()), new TypeToken<ArrayList<Subtask>>() {}.getType());
        if (subtasksList != null) {
            subtasksList.forEach(subtask -> {
                subtasks.put(subtask.getId(), subtask);
                checkId(subtask.getId());
            });
        }
    }

    private void loadHistoryFromServer() {
        List<Integer> historyList = gson.fromJson(client.load(HISTORY.toLowerCase()), new TypeToken<>() {});
        if (historyList != null) {
            this.fillHistoryManager(historyList);
        }
    }

    protected void save() {
        String tasksGson = gson.toJson(new ArrayList<>(tasks.values()));
        client.save(TASK.toLowerCase(), tasksGson);
        String epicsGson = gson.toJson(new ArrayList<>(epics.values()));
        client.save(EPIC.toLowerCase(), epicsGson);
        String subtaskGson = gson.toJson(new ArrayList<>(subtasks.values()));
        client.save(SUBTASK.toLowerCase(), subtaskGson);
        String historyGson = gson.toJson(new ArrayList<>(historyToInteger()));
        client.save(HISTORY.toLowerCase(), historyGson);
    }
}