package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.model.Epic;

import java.util.HashMap;

public class TaskManager {
    private int idCounter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public int setId() {
        idCounter++;
        return idCounter;
    }

    // Получение списка всех задач
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    // Получение списка всех эпиков
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    // Получение списка всех подзадач
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    // Удаление всех задач
    public void clearAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    // Получение задания по идентификатору
    public Task getTaskById(int idTask) {
        return tasks.get(idTask);
    }

    // Получение эпика по идентификатору
    public Epic getEpicById(int idEpic) {
        return epics.get(idEpic);
    }

    // Получение подзадачи по идентификатору
    public Subtask getSubTaskById(int idSubTask) {
        return subtasks.get(idSubTask);
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
    public void addNewSubtask(Subtask subtask, int idEpic) {
        if (subtask != null) {
            int idSubtask = subtask.getId();

            subtask.setIdEpic(idEpic);
            subtasks.put(idSubtask, subtask);
            epics.get(idEpic).getEpicSubtasks().put(idSubtask, subtasks.get(idSubtask));
        }
    }

    // Обновление задачи
    public void updateTasks(Task task) {
        if (task != null) {
            tasks.remove(task.getId());
            tasks.put(task.getId(), task);
        }
    }

    // Обновление эпика
    public void updateEpics(Epic epic) {
        if (epic != null) {
            int epicId = epic.getId();
            int counter = 0;

            epics.remove(epicId);
            epics.put(epicId, epic);
            for (Subtask value : epic.getEpicSubtasks().values()) {
                if (value.isDone()) {
                    counter++;
                }
            }
            if (epic.getEpicSubtasks().size() == counter) {
                epics.get(epicId).setStatus(Status.DONE);
            } else if (epics.get(epicId).getEpicSubtasks().size() > 0) {
                epics.get(epicId).setStatus(Status.IN_PROGRESS);
            } else {
                epics.get(epicId).setStatus(Status.NEW);
            }
        }
    }

    // Обновление подзадачи
    public void updateSubtasks(Subtask subtask) {
        if (subtask != null) {
            int counter = 0;
            int subtaskId = subtask.getId();
            HashMap<Integer, Subtask> getEpicSubtasks = epics.get(subtask.getIdEpic()).getEpicSubtasks();

            subtasks.remove(subtaskId);
            subtasks.put(subtaskId, subtask);
            getEpicSubtasks.remove(subtaskId);
            getEpicSubtasks.put(subtaskId, subtask);
            for (Subtask value : getEpicSubtasks.values()) {
                if (value.isDone()) {
                    counter++;
                }
            }
            if (getEpicSubtasks.size() == counter) {
                epics.get(subtask.getIdEpic()).setStatus(Status.DONE);
            } else if (getEpicSubtasks.size() > 0) {
                epics.get(subtask.getIdEpic()).setStatus(Status.IN_PROGRESS);
            } else {
                epics.get(subtask.getIdEpic()).setStatus(Status.NEW);
            }
        }
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "tasks=" + tasks +
                ", epics=" + epics +
                ", subtasks=" + subtasks +
                '}';
    }
}