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

    private int setId() {
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
    public void addNewTask(String taskName, String description) {
        if ((taskName != null) && (description != null)) {
            Task task = new Task(taskName, description, setId());

            tasks.put(task.getId(), task);
        }
    }

    // Создание эпика
    public void addNewEpic(String taskName, String description) {
        if ((taskName != null) && (description != null)) {
            Epic epic = new Epic(taskName, description, setId());

            epics.put(epic.getId(), epic);
        }
    }

    // Создание подзадачи
    public void addNewSubtask(String taskName, String description, int idEpic) {
        if ((taskName != null) && (description != null)) {
            int newId = setId();
            Subtask subTask = new Subtask(taskName, description, newId, idEpic);

            subtasks.put(newId, subTask);
            epics.get(idEpic).getEpicSubtasks().put(newId, subtasks.get(newId));
        }
    }

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра
    public void updateTasks(Subtask subtask) {
        if (subtask != null) {
            subtasks.remove(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
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