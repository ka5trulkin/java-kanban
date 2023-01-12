package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest {
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
        manager.clearAllSubtasks();
        assertEquals(Status.NEW, manager.getEpicById(idEpic).getStatus());
    }
}