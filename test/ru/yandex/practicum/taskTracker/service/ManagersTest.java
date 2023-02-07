package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        assertEquals(FileBackedTasksManager.class, Managers.getDefault().getClass(), "Объект не соответствует калссу.");
    }

    @Test
    void getDefaultHistory() {
        assertEquals(InMemoryHistoryManager.class, Managers.getDefaultHistory().getClass(), "Объект не соответствует калссу.");
    }
}