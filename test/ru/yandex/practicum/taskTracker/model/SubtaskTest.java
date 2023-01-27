package ru.yandex.practicum.taskTracker.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    final Subtask subtask = new Subtask(
            "Подзадача",
            "Описание подзадачи",
            LocalDateTime.now(),
            Duration.ofMinutes(15),
            2,
            1);
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