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
            epics.get(idEpic).getEpicSubtaskInfo().put(idSubtask, subtasks.get(idSubtask).getStatus());
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

            epics.remove(epicId);
            epics.put(epicId, epic);
            checkEpicStatus(epicId);
            for (Subtask value : subtasks.values()) {
                Status subtaskStatusInEpic = epics.get(value.getIdEpic()).getEpicSubtaskInfo().get(value.getId());

                if (value.getStatus() != subtaskStatusInEpic) {
                    value.setStatus(subtaskStatusInEpic);
                }
            }
        }
    }

    // Обновление подзадачи
    public void updateSubtasks(Subtask subtask) {
        if (subtask != null) {
            int subtaskId = subtask.getId();
            HashMap<Integer, Status> getEpicSubtasks = epics.get(subtask.getIdEpic()).getEpicSubtaskInfo();

            subtasks.remove(subtaskId);
            subtasks.put(subtaskId, subtask);
            getEpicSubtasks.remove(subtaskId);
            getEpicSubtasks.put(subtaskId, subtasks.get(subtaskId).getStatus());
            checkEpicStatus(subtask.getIdEpic());
        }
    }

    public void checkEpicStatus (int epicId) {
        int counter = 0;
        Epic epic = epics.get(epicId);

        for (Status value : epic.getEpicSubtaskInfo().values()) {
            if (value == Status.DONE) {
                counter++;
            }
        }
        if (epic.getEpicSubtaskInfo().size() == counter) {
            epic.setStatus(Status.DONE);
        } else if (epic.getEpicSubtaskInfo().size() > 0) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteEpicById(int epicId) {
        epics.remove(epicId);
    }

    public void deleteSubtaskById(int subtaskId) {
        int epicId = subtasks.get(subtaskId).getIdEpic();
        subtasks.remove(subtaskId);
        checkEpicStatus(epicId);
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