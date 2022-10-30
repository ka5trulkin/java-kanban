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

    // Получение списка всех задач
    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();

        for (Task value : tasks.values()) {
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    // Получение списка всех эпиков
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> result = new ArrayList<>();

        for (Epic value : epics.values()) {
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    // Получение списка всех подзадач
    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> result = new ArrayList<>();

        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtasks().values()) {
                if (subtask != null) {
                    result.add(subtask);
                }
            }
        }
        return result;
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
        }
        for (Epic value : epics.values()) {
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
            epics.get(epicId).putSubtask(subtaskId, subtask);
            checkEpicStatus(epicId);
        }
    }

    // Обновление задачи
    public void updateTasks(Task task) {
        if ((task != null) && (tasks.containsKey(task.getId()))) {
            tasks.put(task.getId(), task);
        }
    }

    // Обновление эпика
    public void updateEpics(Epic epic) {
        if ((epic != null) && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    // Обновление подзадачи
    public void updateSubtasks(Subtask subtask) {
        if ((subtask != null) && (subtasks.containsKey(subtask.getId()))) {
            int subtaskId = subtask.getId();
            int epicId = subtask.getEpicId();

            subtasks.put(subtaskId, subtask);
            epics.get(epicId).putSubtask(subtaskId, subtask);
            checkEpicStatus(epicId);
        }
    }

    // Удаление задачи по идентификатору
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }

    // Удаление эпика по идентификатору
    public void removeEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            for (Subtask value : subtasks.values()) {
                if (value.getEpicId() == epicId) {
                    subtasks.remove(value.getId());
                }
            }
            epics.remove(epicId);
        }
    }

    // Удаление подзадачи по идентификатору
    public void removeSubtaskById(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            int epicId = subtasks.get(subtaskId).getEpicId();

            subtasks.remove(subtaskId);
            if (epics.containsKey(epicId)) {
                epics.get(epicId).removeSubtask(subtaskId);
                checkEpicStatus(epicId);
            }
        }
    }

    // Получение списка всех подзадач определённого эпика
    public ArrayList<Subtask> getSubtasksFromEpic(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();

        if (epics.containsKey(epicId)) {
            for (Subtask value : epics.get(epicId).getSubtasks().values()) {
                if (value != null) {
                    result.add(value);
                }
            }
        }
        return result;
    }

    // Проверка статуса эпика
    public void checkEpicStatus (int epicId) {
        int counter = 0;
        Epic epic = epics.get(epicId);

        if (epic != null) {
            for (Subtask value : epic.getSubtasks().values()) {
                if (value.getStatus() == Status.DONE) {
                    counter++;
                }
            }
            if (epic.getSubtasks().size() == counter) {
                epic.setStatus(Status.DONE);
            } else if (epic.getSubtasks().size() > 0) {
                epic.setStatus(Status.IN_PROGRESS);
            } else {
                epic.setStatus(Status.NEW);
            }
        }
    }

    public int setId() {
        idCounter++;
        return idCounter;
    }
}