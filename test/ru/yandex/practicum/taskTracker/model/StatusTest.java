package ru.yandex.practicum.taskTracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void getName() {
        String statusNew = "Задача открыта";
        String statusInProgress = "Задача выполняется";
        String stsusDone = "Задача выполнена";

        assertEquals(statusNew, Status.NEW.getName());
        assertEquals(statusInProgress, Status.IN_PROGRESS.getName());
        assertEquals(stsusDone, Status.DONE.getName());
    }
}