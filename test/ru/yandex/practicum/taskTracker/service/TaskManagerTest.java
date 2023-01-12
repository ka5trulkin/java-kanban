package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    InMemoryTaskManager manager = new InMemoryTaskManager();
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
    int idTask = task.getId();
    int idEpic = epic.getId();
    int idSubtask = subtask.getId();

    void fillManager() {
        tasksList.forEach(manager :: addNewTask);
        epicsList.forEach(manager :: addNewEpic);
        subtasksList.forEach(manager :: addNewSubtask);
    }

    @Test
    void setId() {
        int idCounterMostBe = 1;

        assertEquals(idCounterMostBe, manager.setId());
        idCounterMostBe = 2;
        assertEquals(idCounterMostBe, manager.setId());
    }

    @Test
    void getTasks() {
        assertEquals(Collections.emptyList(), manager.getTasks());
        fillManager();
        assertEquals(tasksList, manager.getTasks());
    }

    @Test
    void getEpics() {
        assertEquals(Collections.emptyList(), manager.getEpics());
        fillManager();
        assertEquals(epicsList, manager.getEpics());
    }

    @Test
    void getSubtasks() {
        assertEquals(Collections.emptyList(), manager.getSubtasks());
        fillManager();
        assertEquals(subtasksList, manager.getSubtasks());
    }

    @Test
    void clearAllTasks() {
        fillManager();
        assertEquals(tasksList, manager.getTasks());
        manager.clearAllTasks();
        assertEquals(Collections.emptyList(), manager.getTasks());
    }

    @Test
    void clearAllEpics() {
        fillManager();
        assertEquals(epicsList, manager.getEpics());
        manager.clearAllEpics();
        assertEquals(Collections.emptyList(), manager.getEpics());
    }

    @Test
    void clearAllSubtasks() {
        fillManager();
        assertEquals(subtasksList, manager.getSubtasks());
        manager.clearAllSubtasks();
        assertEquals(Collections.emptyList(), manager.getSubtasks());
    }

    @Test
    void getTaskById() {
        fillManager();
        assertEquals(task, manager.getTaskById(idTask));
    }

    @Test
    void getEpicById() {
        fillManager();
        assertEquals(epic, manager.getEpicById(idEpic));
    }

    @Test
    void getSubTaskById() {
        fillManager();
        assertEquals(subtask, manager.getSubTaskById(idSubtask));
    }

    @Test
    void addNewTask() {
        manager.addNewTask(task);
        assertEquals(Collections.singletonList(task), manager.getTasks());
    }

    @Test
    void addNewEpic() {
        manager.addNewEpic(epic);
        assertEquals(Collections.singletonList(epic), manager.getEpics());
    }

    @Test
    void addNewSubtask() {
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtask);
        assertEquals(Collections.singletonList(subtask), manager.getSubtasks());
    }

    @Test
    void updateTask() {
        fillManager();
        task = new Task(task.getTaskName(), newDescription, idTask);
        manager.updateTask(task);
        assertEquals(newDescription, manager.getTaskById(idTask).getDescription());
    }

    @Test
    void updateEpic() {
        fillManager();
        epic = new Epic(epic.getTaskName(), newDescription, idEpic);
        manager.updateEpic(epic);
        assertEquals(newDescription, manager.getEpicById(idEpic).getDescription());
    }

    @Test
    void updateSubtask() {
        fillManager();
        subtask = new Subtask(subtask.getTaskName(), newDescription, idSubtask, subtask.getEpicId());
        manager.updateSubtask(subtask);
    }

    @Test
    void removeTaskById() {
        fillManager();
        assertEquals(task, manager.getTaskById(idTask));
        manager.removeTaskById(idTask);
        assertNull(manager.getTaskById(idTask));
    }

    @Test
    void removeEpicById() {
        fillManager();
        assertEquals(epic, manager.getEpicById(idEpic));
        manager.removeEpicById(idEpic);
        assertNull(manager.getEpicById(idEpic));
    }

    @Test
    void removeSubtaskById() {
        fillManager();
        assertEquals(subtask, manager.getSubTaskById(idSubtask));
        manager.removeSubtaskById(idSubtask);
        assertNull(manager.getSubTaskById(idSubtask));
    }

    @Test
    void getSubtasksFromEpic() {
        fillManager();
        assertEquals(subtasksList, manager.getSubtasksFromEpic(idEpic));
    }
}