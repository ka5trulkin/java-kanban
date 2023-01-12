package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.util.Arrays;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {
    final InMemoryTaskManager manager = new InMemoryTaskManager();
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
    Task task = tasksList.get(0);
    Epic epic = epicsList.get(0);
    Subtask subtask = subtasksList.get(0);
    final int idTask = task.getId();
    final int idEpic = epic.getId();
    final int idSubtask = subtask.getId();
    final int idNonexistent = 777;

    void fillManager() {
        tasksList.forEach(manager :: addNewTask);
        epicsList.forEach(manager :: addNewEpic);
        subtasksList.forEach(manager :: addNewSubtask);
    }

    @Test
    abstract void setId();

    @Test
    abstract void getTasks();

    @Test
    abstract void getEpics();

    @Test
    abstract void getSubtasks();

    @Test
    abstract void clearAllTasks();

    @Test
    abstract void clearAllEpics();

    @Test
    abstract void clearAllSubtasks();

    @Test
    abstract void getTaskById();

    @Test
    abstract void getEpicById();

    @Test
    abstract void getSubTaskById();

    @Test
    abstract void addNewTask();

    @Test
    abstract void addNewEpic();

    @Test
    abstract void addNewSubtask();

    @Test
    abstract void updateTask();

    @Test
    abstract void updateEpic();

    @Test
    abstract void updateSubtask();

    @Test
    abstract void removeTaskById();

    @Test
    abstract void removeEpicById();

    @Test
    abstract void removeSubtaskById();

    @Test
    abstract void getSubtasksFromEpic();
}