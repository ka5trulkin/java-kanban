package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.model.Epic;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int idCounter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    // Проверка статуса эпика
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

    // Получение списка всех задач
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Получение списка всех эпиков
    public ArrayList<Epic> getEpics() {
        return new  ArrayList<>(epics.values());
    }

    // Получение списка всех подзадач
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Удаление всех задач
    public void clearAllTasks() {
        tasks.clear();
    }

    // Удаление всех эпиков
    public void clearAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    // Удаление всех подзадач
    public void clearAllSubtasks() {
        subtasks.clear();
        for (Epic value : epics.values()) {
            value.clearSubtasks();
            value.setStatus(Status.NEW);
        }
    }

    // Получение задания по идентификатору
    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    // Получение эпика по идентификатору
    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    // Получение подзадачи по идентификатору
    public Subtask getSubTaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    // Создание задачи
    public void addNewTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        }
    }

    // Создание эпика
    public void addNewEpic(Epic epic) {
        if (epic != null) {
            epics.put(epic.getId(), epic);
        }
    }

    // Создание подзадачи
    public void addNewSubtask(Subtask subtask) {
        if (subtask != null) {
            int subtaskId = subtask.getId();
            int epicId = subtask.getEpicId();

            subtasks.put(subtaskId, subtask);
            epics.get(epicId).addSubtask(subtaskId);
            checkEpicStatus(epicId);
        }
    }

    // Обновление задачи
    public void updateTask(Task task) {
        if ((task != null) && (tasks.containsKey(task.getId()))) {
            tasks.put(task.getId(), task);
        }
    }

    // Обновление эпика
    public void updateEpic(Epic epic) {
        if ((epic != null) && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            checkEpicStatus(epic.getId());
        }
    }

    // Обновление подзадачи
    public void updateSubtask(Subtask subtask) {
        if ((subtask != null) && (subtasks.containsKey(subtask.getId()))) {
            int subtaskId = subtask.getId();
            int epicId = subtask.getEpicId();

            subtasks.put(subtaskId, subtask);
            epics.get(epicId).removeSubtask(subtaskId);
            epics.get(epicId).addSubtask(subtaskId);
            checkEpicStatus(epicId);
        }
    }

    // Удаление задачи по идентификатору
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }

    // Удаление эпика по идентификатору
    public void removeEpicById(int epicId) {
        if (epics.get(epicId) != null) {
            for (Integer subtaskId : epics.get(epicId).getSubtasksId()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(epicId);
        }
    }

    // Удаление подзадачи по идентификатору
    public void removeSubtaskById(int subtaskId) {
        if (subtasks.get(subtaskId) != null) {
            int epicId = subtasks.get(subtaskId).getEpicId();

            subtasks.remove(subtaskId);
            epics.get(epicId).removeSubtask(subtaskId);
            checkEpicStatus(epicId);
        }
    }

    // Получение списка всех подзадач определённого эпика
    public ArrayList<Subtask> getSubtasksFromEpic(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();

        if (epics.containsKey(epicId)) {
            for (Integer subtaskId : epics.get(epicId).getSubtasksId()) {
                result.add(subtasks.get(subtaskId));
            }
        }
        return result;
    }

    public int setId() {
        idCounter++;
        return idCounter;
    }
}