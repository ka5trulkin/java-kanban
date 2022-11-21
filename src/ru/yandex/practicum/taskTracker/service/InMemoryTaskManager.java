package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.model.Epic;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static int idCounter = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private void checkEpicStatus (int epicId) {
        int counter = 0;
        Epic epic = epics.get(epicId);

        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasksId()) {
                if (subtasks.get(subtaskId).getStatus() == Status.DONE) {
                    counter++;
                }
            }
            if (epic.getSubtasksId().size() == counter) {
                epic.setStatus(Status.DONE);
            } else if (epic.getSubtasksId().size() > 0) {
                epic.setStatus(Status.IN_PROGRESS);
            } else {
                epic.setStatus(Status.NEW);
            }
        }
    }

    public static int setId() {
        idCounter++;
        return idCounter;
    }

    @Override
    public List<Task> getHistoryFromManager() { // Метод для проверки работоспособности HistoryManager
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public Subtask getSubTaskById(int subtaskId) {
        historyManager.add(subtasks.get(subtaskId));
        return subtasks.get(subtaskId);
    }

    @Override
    public void addNewTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void addNewEpic(Epic epic) {
        if (epic != null) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        if (subtask != null) {
            int subtaskId = subtask.getId();
            int epicId = subtask.getEpicId();

            subtasks.put(subtaskId, subtask);
            epics.get(epicId).addSubtask(subtaskId);
            checkEpicStatus(epicId);
        }
    }

    @Override
    public void updateTask(Task task) {
        if ((task != null) && (tasks.containsKey(task.getId()))) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if ((epic != null) && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            checkEpicStatus(epic.getId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if ((subtask != null) && (subtasks.containsKey(subtask.getId()))) {
            int subtaskId = subtask.getId();
            int epicId = subtask.getEpicId();

            subtasks.put(subtaskId, subtask);
            epics.get(epicId).setSubtask(subtaskId);
            checkEpicStatus(epicId);
        }
    }

    @Override
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        if (epics.get(epicId) != null) {
            for (Integer subtaskId : epics.get(epicId).getSubtasksId()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(epicId);
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        if (subtasks.get(subtaskId) != null) {
            int epicId = subtasks.get(subtaskId).getEpicId();

            subtasks.remove(subtaskId);
            epics.get(epicId).removeSubtask(subtaskId);
            checkEpicStatus(epicId);
            historyManager.remove(subtaskId);
        }
    }

    @Override
    public List<Subtask> getSubtasksFromEpic(int epicId) {
        List<Subtask> result = new ArrayList<>();

        if (epics.containsKey(epicId)) {
            for (Integer subtaskId : epics.get(epicId).getSubtasksId()) {
                result.add(subtasks.get(subtaskId));
            }
        }
        return result;
    }
}