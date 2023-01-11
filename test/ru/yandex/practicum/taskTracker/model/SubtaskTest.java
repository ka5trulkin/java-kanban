package ru.yandex.practicum.taskTracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    final Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 2, 1);
    final int epicId = 1;

    @Test
    void getEpicId() {
        assertEquals(epicId, subtask.getEpicId());
    }

    @Test
    void getType() {
        Type subtaskType = Type.SUBTASK;

        assertEquals(subtaskType, subtask.getType());
    }

    @Test
    void getParentEpicID() {
        assertEquals(epicId, subtask.getParentEpicID());
    }
}