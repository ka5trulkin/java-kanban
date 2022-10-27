package ru.yandex.practicum.taskTracker.service;

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
            int newId = setId();

            subtasks.put(subtask.getId(), subtask);
            epics.get(idEpic).getEpicSubtasks().put(newId, subtasks.get(newId));
        }
    }

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра
    public void updateTasks(Task task) {
        if (task != null) {
            tasks.remove(task.getId());
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpics(Epic epic) {
        if (epic != null) {
            int key = epic.getId();
            int counter = 0;

            epics.remove(key);
            epics.put(key, epic);
            for (Subtask value : epic.getEpicSubtasks().values()) {
                if (value.isDone()) {
                    counter++;
                }
            }
            if (epic.getEpicSubtasks().size() == counter) {
                epics.get(key).setDone(true);
            }
        }
    }

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра
    public void updateSubtasks(Subtask subtask) {
        if (subtask != null) {
            int idSubtask = subtask.getId();

            subtasks.remove(idSubtask);
            subtasks.put(idSubtask, subtask);
        }
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                ", TASKS=" + tasks + System.lineSeparator() +
                ", EPICS=" + epics +
                '}';
    }
}