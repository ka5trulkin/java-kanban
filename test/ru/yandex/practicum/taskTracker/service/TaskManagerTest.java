package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

abstract class TaskManagerTest<T extends TaskManager> {
    String newDescription = "Новое описание задачи";
    List<Task> tasksList = Arrays.asList(
            new Task("Задача 1", "Описание задачи 1", 1),
            new Task("Задача 2", "Описание задачи 2", 2)
    );
    List<Epic> epicsList = Arrays.asList(
            new Epic("Эпик 1", "Описание эпика 1", 3),
            new Epic("Эпик 2", "Описание эпика 2", 4)
    );
    List<Subtask> subtasksList = Arrays.asList(
            new Subtask("Подзадача 1", "Описание подзадачи 1", 5, 3),
            new Subtask("Подзадача 2", "Описание подзадачи 2", 6, 3)
    );
    final int firstTaskInList = 0;
    Task task = tasksList.get(firstTaskInList);
    Epic epic = epicsList.get(firstTaskInList);
    Subtask subtask = subtasksList.get(firstTaskInList);
    final int idTask = task.getId();
    final int idEpic = epic.getId();
    final int idSubtask = subtask.getId();
    final int idNonexistent = 777;

    void fillManager(T manager) {
        tasksList.forEach(manager :: addNewTask);
        epicsList.forEach(manager :: addNewEpic);
        subtasksList.forEach(manager :: addNewSubtask);
    }

    void setId(T manager) {
        int idCounterMostBe = 1;

        assertEquals(idCounterMostBe, manager.setId());
        idCounterMostBe = 2;
        assertEquals(idCounterMostBe, manager.setId());
    }

    void getTasks(T manager) {
        assertEquals(Collections.emptyList(), manager.getTasks());
        fillManager(manager);
        assertEquals(tasksList, manager.getTasks());
    }

    void getEpics(T manager) {
        assertEquals(Collections.emptyList(), manager.getEpics());
        fillManager(manager);
        assertEquals(epicsList, manager.getEpics());
    }

    void getSubtasks(T manager) {
        assertEquals(Collections.emptyList(), manager.getSubtasks());
        fillManager(manager);
        assertEquals(subtasksList, manager.getSubtasks());
    }

    void clearAllTasks(T manager) {
        fillManager(manager);
        assertEquals(tasksList, manager.getTasks());
        manager.clearAllTasks();
        assertEquals(Collections.emptyList(), manager.getTasks());
    }

    void clearAllEpics(T manager) {
        fillManager(manager);
        assertEquals(epicsList, manager.getEpics());
        manager.clearAllEpics();
        assertEquals(Collections.emptyList(), manager.getEpics());
    }

    void clearAllSubtasks(T manager) {
        fillManager(manager);
        assertEquals(subtasksList, manager.getSubtasks());
        manager.clearAllSubtasks();
        assertEquals(Collections.emptyList(), manager.getSubtasks());
    }

    void getTaskById(T manager) {
        fillManager(manager);
        assertEquals(task, manager.getTaskById(idTask));
        assertNull(manager.getTaskById(idNonexistent));
    }

    void getEpicById(T manager) {
        fillManager(manager);
        assertEquals(epic, manager.getEpicById(idEpic));
        assertNull(manager.getEpicById(idNonexistent));
    }

    void getSubTaskById(T manager) {
        fillManager(manager);
        assertEquals(subtask, manager.getSubTaskById(idSubtask));
        assertNull(manager.getSubTaskById(idNonexistent));
    }

    void addNewTask(T manager) {
        manager.addNewTask(task);
        assertEquals(Collections.singletonList(task), manager.getTasks());
    }

    void addNewEpic(T manager) {
        manager.addNewEpic(epic);
        assertEquals(Collections.singletonList(epic), manager.getEpics());
    }

    void addNewSubtask(T manager) {
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtask);
        assertEquals(Collections.singletonList(subtask), manager.getSubtasks());
    }

    void updateTask(T manager) {
        fillManager(manager);
        task = new Task(task.getTaskName(), newDescription, idTask);
        manager.updateTask(task);
        assertEquals(newDescription, manager.getTaskById(idTask).getDescription());
    }

    void updateEpic(T manager) {
        fillManager(manager);
        epic = new Epic(epic.getTaskName(), newDescription, idEpic);
        manager.updateEpic(epic);
        assertEquals(newDescription, manager.getEpicById(idEpic).getDescription());
    }

    void updateSubtask(T manager) {
        fillManager(manager);
        subtask = new Subtask(subtask.getTaskName(), newDescription, idSubtask, subtask.getEpicId());
        manager.updateSubtask(subtask);
        assertEquals(newDescription, manager.getSubTaskById(idSubtask).getDescription());
    }

    void removeTaskById(T manager) {
        fillManager(manager);
        assertEquals(task, manager.getTaskById(idTask));
        manager.removeTaskById(idTask);
        assertNull(manager.getTaskById(idTask));
    }

    void removeEpicById(T manager) {
        fillManager(manager);
        assertEquals(epic, manager.getEpicById(idEpic));
        manager.removeEpicById(idEpic);
        assertNull(manager.getEpicById(idEpic));
    }

    void removeSubtaskById(T manager) {
        fillManager(manager);
        assertEquals(subtask, manager.getSubTaskById(idSubtask));
        manager.removeSubtaskById(idSubtask);
        assertNull(manager.getSubTaskById(idSubtask));
    }

    void getSubtasksFromEpic(T manager) {
        fillManager(manager);
        assertEquals(subtasksList, manager.getSubtasksFromEpic(idEpic));
        assertEquals(Collections.emptyList(), manager.getSubtasksFromEpic(idNonexistent));
    }
}