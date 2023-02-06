package ru.yandex.practicum.taskTracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private Subtask subtask;
    private int epicId;

    @BeforeEach
    void beforeEach() {
        subtask = new Subtask(
                "Подзадача",
                "Описание подзадачи",
                LocalDateTime.now(),
                Duration.ofMinutes(15),
                2,
                1);
        epicId = 1;
    }


    @Test
    void getEpicId() {
        assertEquals(epicId, subtask.getEpicId(), "ID Эпика не совпадают.");
    }

    @Test
    void getType() {
        Type subtaskType = Type.SUBTASK;

        assertEquals(subtaskType, subtask.getType(), "Статусы не совпадают.");
    }

    @Test
    void getParentEpicID() {
        assertEquals(epicId, subtask.getParentEpicID(), "ID эпика не совпадает.");
    }
}