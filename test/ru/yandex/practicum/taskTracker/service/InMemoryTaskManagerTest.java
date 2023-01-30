package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    final InMemoryTaskManager manager = new InMemoryTaskManager();

    void changeSubtaskStatus(Subtask subtaskFromManager, Status status) {
        manager.updateSubtask(
                new Subtask(
                        subtaskFromManager.getTaskName(),
                        subtaskFromManager.getDescription(),
                        subtaskFromManager.getStartTime(),
                        subtaskFromManager.getDuration(),
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
        assertEquals(Status.NEW, manager.getEpicById(idEpic).getStatus());
        assertEquals(Collections.emptyList(), manager.getSubtasks());
        fillManager(manager);
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
        manager.clearAllSubtasks();
        assertEquals(Status.NEW, manager.getEpicById(idEpic).getStatus());
    }

    @Test
    void getEpicEndTime() {
        super.getEpicEndTime(manager);
    }

    @Test
    void getPrioritizedTasks() {
        super.getPrioritizedTasks(manager);
    }

    @Test
    void setId() {
        super.setId(manager);
    }

    @Test
    void getTasks() {
        super.getTasks(manager);
    }

    @Test
    void getEpics() {
        super.getEpics(manager);
    }

    @Test
    void getSubtasks() {
        super.getSubtasks(manager);
    }

    @Test
    void clearAllTasks() {
        super.clearAllTasks(manager);
    }

    @Test
    void clearAllEpics() {
        super.clearAllEpics(manager);
    }

    @Test
    void clearAllSubtasks() {
        super.clearAllSubtasks(manager);
    }

    @Test
    void getTaskById() {
        super.getTaskById(manager);
    }

    @Test
    void getEpicById() {
        super.getEpicById(manager);
    }

    @Test
    void getSubTaskById() {
        super.getSubTaskById(manager);
    }

    @Test
    void addNewTask() {
        super.addNewTask(manager);
    }

    @Test
    void addNewEpic() {
        super.addNewEpic(manager);
    }

    @Test
    void addNewSubtask() {
        super.addNewSubtask(manager);
    }

    @Test
    void updateTask() {
        super.updateTask(manager);
    }

    @Test
    void updateEpic() {
        super.updateEpic(manager);
    }

    @Test
    void updateSubtask() {
        super.updateSubtask(manager);
    }

    @Test
    void removeTaskById() {
        super.removeTaskById(manager);
    }

    @Test
    void removeEpicById() {
        super.removeEpicById(manager);
    }

    @Test
    void removeSubtaskById() {
        super.removeSubtaskById(manager);
    }

    @Test
    void getSubtasksFromEpic() {
        super.getSubtasksFromEpic(manager);
    }
}