package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() throws URISyntaxException {
        assertEquals(HttpTaskManager.class, Managers.getDefault().getClass(), "Объект не соответствует классу.");
    }

    @Test
    void getDefaultHistory() {
        assertEquals(InMemoryHistoryManager.class, Managers.getDefaultHistory().getClass(), "Объект не соответствует классу.");
    }
}