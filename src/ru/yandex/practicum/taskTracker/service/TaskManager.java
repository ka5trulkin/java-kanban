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
    public ArrayList<Epic> getSubtasks() {
        ArrayList<Epic> result = new ArrayList<>();

        for (Epic value : epics.values()) {
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    // Удаление всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic value : epics.values()) {
            value.deleteSubtasks();
        }
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
//
//    // Обновление подзадачи
//    public void updateSubtasks(Subtask subtask) {
//        if (subtask != null) {
//            int subtaskId = subtask.getId();
//            HashMap<Integer, Status> getEpicSubtasks = epics.get(subtask.getIdEpic()).getSubtasks();
//
//            subtasks.remove(subtaskId);
//            subtasks.put(subtaskId, subtask);
//            getEpicSubtasks.remove(subtaskId);
//            getEpicSubtasks.put(subtaskId, subtasks.get(subtaskId).getStatus());
//            checkEpicStatus(subtask.getIdEpic());
//        }
//    }
//
//    // Удаление задачи по идентификатору
//    public void deleteTaskById(int taskId) {
//        tasks.remove(taskId);
//    }
//
//    // Удаление эпика по идентификатору
//    public void deleteEpicById(int epicId) {
//        epics.remove(epicId);
//    }
//
//    // Удаление подзадачи по идентификатору
//    public void deleteSubtaskById(int subtaskId) {
//        int epicId = subtasks.get(subtaskId).getIdEpic();
//
//        epics.get(epicId).getSubtasks().remove(subtaskId);
//        subtasks.remove(subtaskId);
//        checkEpicStatus(epicId);
//    }
//
//    // Получение списка всех подзадач определённого эпика
//    public HashMap<Integer, Subtask> getSubtasksFromEpic(int epicId) {
//        HashMap<Integer, Subtask> result = new HashMap<>();
//
//        for (Integer subtaskId : epics.get(epicId).getSubtasks().keySet()) {
//            result.put(subtaskId,subtasks.get(subtaskId));
//        }
//        return result;
//    }
//
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