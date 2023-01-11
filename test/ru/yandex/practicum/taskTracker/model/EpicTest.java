package ru.yandex.practicum.taskTracker.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    final Epic epic = new Epic("Эпик для теста", "Протестировать Эпик", 1);
    final int listSize = 0;
    final int subtaskId = 2;

    @Test
    void getType() {
        Type epicType = Type.EPIC;

        assertEquals(epicType, epic.getType());
    }

    @Test
    void getSubtasksId() {
        assertEquals(listSize, epic.getSubtasksId().size());
        epic.addSubtask(subtaskId);
        assertEquals(Collections.singletonList(subtaskId), epic.getSubtasksId());
    }

    @Test
    void addSubtask() {
       for (int numberOfAddedTasks = 1; numberOfAddedTasks <= 3; numberOfAddedTasks++) {
            epic.addSubtask(subtaskId);
            assertEquals(Collections.singletonList(subtaskId), epic.getSubtasksId());
       }
    }

    @Test
    void removeSubtask() {
        epic.addSubtask(subtaskId);
        epic.removeSubtask(subtaskId);
        assertEquals(listSize, epic.getSubtasksId().size());
    }

    @Test
    void setSubtask() {
        epic.setSubtask(subtaskId);
        assertEquals(listSize, epic.getSubtasksId().size());
        epic.addSubtask(subtaskId);
        epic.setSubtask(subtaskId);
        assertEquals(Collections.singletonList(subtaskId), epic.getSubtasksId());
    }

    @Test
    void clearSubtasks() {
        epic.addSubtask(subtaskId);
        epic.clearSubtasks();
        assertEquals(listSize, epic.getSubtasksId().size());
    }
}