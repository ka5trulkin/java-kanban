package ru.yandex.practicum.taskTracker.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.taskTracker.http.KVTaskClient;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.model.Type;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.taskTracker.model.Type.*;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final static Gson gson = Managers.getGson();

    public HttpTaskManager(URI uri) {
        this.client = new KVTaskClient(uri);
    }

    private void save(Type type) {
        switch (type) {
            case TASK:
                String tasksGson = gson.toJson(new ArrayList<>(tasks.values()));
                client.save("tasks", tasksGson);
                break;
            case EPIC:
                String epicsGson = gson.toJson(new ArrayList<>(epics.values()));
                client.save("epics", epicsGson);
                break;
            case SUBTASK:
                String subtaskGson = gson.toJson(new ArrayList<>(subtasks.values()));
                client.save("subtasks", subtaskGson);
                break;
            case HISTORY:
                String historyGson = gson.toJson(new ArrayList<>(historyToInteger()));
                client.save("history", historyGson);
        }
    }

    private List<Integer> historyToInteger() {
        return getHistory().stream().map(Task::getId).collect(Collectors.toList());
    }

    public static TaskManager loadFromURI(URI uri) {
        final HttpTaskManager manager = new HttpTaskManager(uri);
        final KVTaskClient loadClient = new KVTaskClient(uri);
        List<Task> tasksList = gson.fromJson(loadClient.load("tasks"), new TypeToken<ArrayList<Task>>() {}.getType());
        if (tasksList != null) {
            tasksList.forEach(manager::addNewTask);
        }
        List<Epic> epicsList = gson.fromJson(loadClient.load("epics"), new TypeToken<ArrayList<Epic>>() {}.getType());
        if (epicsList != null) {
            epicsList.forEach(manager::addNewEpic);
        }
        List<Subtask> subtasksList = gson.fromJson(loadClient.load("subtasks"), new TypeToken<ArrayList<Subtask>>() {}.getType());
        if (subtasksList != null) {
            subtasksList.forEach(manager::addNewSubtask);
        }
        List<Integer> historyList = gson.fromJson(loadClient.load("history"), new TypeToken<>() {});
        if (historyList != null) {
            manager.fillHistoryManager(historyList);
        }
        return manager;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> result = super.getTasks();
        save(HISTORY);
        return result;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> result = super.getEpics();
        save(HISTORY);
        return result;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> result = super.getSubtasks();
        save(HISTORY);
        return result;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save(TASK);
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save(EPIC);
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save(SUBTASK);
    }

    @Override
    public Task getTaskById(int taskId) {
        Task result = super.getTaskById(taskId);
        save(HISTORY);
        return result;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic result = super.getEpicById(epicId);
        save(HISTORY);
        return result;
    }

    @Override
    public Subtask getSubTaskById(int subtaskId) {
        Subtask result = super.getSubTaskById(subtaskId);
        save(HISTORY);
        return result;
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save(TASK);
        checkIdCounter(task.getId());
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save(EPIC);
        checkIdCounter(epic.getId());
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save(SUBTASK);
        checkIdCounter(subtask.getId());
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save(TASK);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save(EPIC);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save(SUBTASK);
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save(TASK);
        save(HISTORY);
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save(EPIC);
        save(HISTORY);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save(SUBTASK);
        save(HISTORY);
    }
}