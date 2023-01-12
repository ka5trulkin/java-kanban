package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    void changeSubtaskStatus(Subtask subtaskFromManager, Status status) {
        manager.updateSubtask(
                new Subtask(
                        subtaskFromManager.getTaskName(),
                        subtaskFromManager.getDescription(),
                        subtaskFromManager.getId(),
                        status,
                        subtaskFromManager.getEpicId()
                )
        );
    }

    @Test
    void checkEpicStatus() {
        manager.addNewEpic(epic);
        manager.updateEpic(epic);
        System.out.println(manager.getEpics());
        assertEquals(Status.NEW, manager.getEpicById(idEpic).getStatus());
        assertEquals(Collections.emptyList(), manager.getSubtasks());
        fillManager();
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(idEpic).getStatus());
        for (Subtask subtaskFromManager : manager.getSubtasks()) {
            changeSubtaskStatus(subtaskFromManager, Status.DONE);
        }
        assertEquals(Status.DONE, manager.getEpicById(idEpic).getStatus());
        changeSubtaskStatus(manager.getSubTaskById(idSubtask), Status.NEW);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(idEpic).getStatus());
        for (Subtask subtaskFromManager : manager.getSubtasks()) {
            changeSubtaskStatus(subtaskFromManager, Status.IN_PROGRESS);
        }
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(idEpic).getStatus());
        System.out.println(manager.getEpics());
        manager.clearAllSubtasks();
        assertEquals(Status.NEW, manager.getEpicById(idEpic).getStatus());
    }

    @Override
    @Test
    void setId() {
        int idCounterMostBe = 1;

        assertEquals(idCounterMostBe, manager.setId());
        idCounterMostBe = 2;
        assertEquals(idCounterMostBe, manager.setId());
    }

    @Override
    @Test
    void getTasks() {
        assertEquals(Collections.emptyList(), manager.getTasks());
        fillManager();
        assertEquals(tasksList, manager.getTasks());
    }

    @Override
    @Test
    void getEpics() {
        assertEquals(Collections.emptyList(), manager.getEpics());
        fillManager();
        assertEquals(epicsList, manager.getEpics());
    }

    @Override
    @Test
    void getSubtasks() {
        assertEquals(Collections.emptyList(), manager.getSubtasks());
        fillManager();
        assertEquals(subtasksList, manager.getSubtasks());
    }

    @Override
    @Test
    void clearAllTasks() {
        fillManager();
        assertEquals(tasksList, manager.getTasks());
        manager.clearAllTasks();
        assertEquals(Collections.emptyList(), manager.getTasks());
    }

    @Override
    @Test
    void clearAllEpics() {
        fillManager();
        assertEquals(epicsList, manager.getEpics());
        manager.clearAllEpics();
        assertEquals(Collections.emptyList(), manager.getEpics());
    }

    @Override
    @Test
    void clearAllSubtasks() {
        fillManager();
        assertEquals(subtasksList, manager.getSubtasks());
        manager.clearAllSubtasks();
        assertEquals(Collections.emptyList(), manager.getSubtasks());
    }

    @Override
    @Test
    void getTaskById() {
        fillManager();
        assertEquals(task, manager.getTaskById(idTask));
    }

    @Override
    @Test
    void getEpicById() {
        fillManager();
        assertEquals(epic, manager.getEpicById(idEpic));
    }

    @Override
    @Test
    void getSubTaskById() {
        fillManager();
        assertEquals(subtask, manager.getSubTaskById(idSubtask));
    }

    @Override
    @Test
    void addNewTask() {
        manager.addNewTask(task);
        assertEquals(Collections.singletonList(task), manager.getTasks());
    }

    @Override
    @Test
    void addNewEpic() {
        manager.addNewEpic(epic);
        assertEquals(Collections.singletonList(epic), manager.getEpics());
    }

    @Override
    @Test
    void addNewSubtask() {
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtask);
        assertEquals(Collections.singletonList(subtask), manager.getSubtasks());
    }

    @Override
    @Test
    void updateTask() {
        fillManager();
        task = new Task(task.getTaskName(), newDescription, idTask);
        manager.updateTask(task);
        assertEquals(newDescription, manager.getTaskById(idTask).getDescription());
    }

    @Override
    @Test
    void updateEpic() {
        fillManager();
        epic = new Epic(epic.getTaskName(), newDescription, idEpic);
        manager.updateEpic(epic);
        assertEquals(newDescription, manager.getEpicById(idEpic).getDescription());
    }

    @Override
    @Test
    void updateSubtask() {
        fillManager();
        subtask = new Subtask(subtask.getTaskName(), newDescription, idSubtask, subtask.getEpicId());
        manager.updateSubtask(subtask);
    }

    @Override
    @Test
    void removeTaskById() {
        fillManager();
        assertEquals(task, manager.getTaskById(idTask));
        manager.removeTaskById(idTask);
        assertNull(manager.getTaskById(idTask));
    }

    @Override
    @Test
    void removeEpicById() {
        fillManager();
        assertEquals(epic, manager.getEpicById(idEpic));
        manager.removeEpicById(idEpic);
        assertNull(manager.getEpicById(idEpic));
    }

    @Override
    @Test
    void removeSubtaskById() {
        fillManager();
        assertEquals(subtask, manager.getSubTaskById(idSubtask));
        manager.removeSubtaskById(idSubtask);
        assertNull(manager.getSubTaskById(idSubtask));
    }

    @Override
    @Test
    void getSubtasksFromEpic() {
        fillManager();
        assertEquals(subtasksList, manager.getSubtasksFromEpic(idEpic));
    }
}