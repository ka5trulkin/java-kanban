package ru.yandex.practicum.taskTracker.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    final Task task = new Task(
            "Задача 1",
            "Описание задачи 1",
            LocalDateTime.of(1978, 7, 28, 1, 21),
            Duration.ofMinutes(15),
            1);

    @Test
    void getType() {
        Type type = Type.TASK;

        assertEquals(type, task.getType());
    }

    @Test
    void getTaskName() {
        String taskName = "Задача 1";

        assertEquals(taskName, task.getTaskName());
    }

    @Test
    void getDescription() {
        String taskDescription = "Описание задачи 1";

        assertEquals(taskDescription, task.getDescription());
    }

    @Test
    void getStatus() {
        Status taskStatus = Status.NEW;

        assertEquals(taskStatus, task.getStatus());
    }

    @Test
    void getEndTime() {
        LocalDateTime expectedEndDateTime = LocalDateTime.parse("1978-07-28T01:36");
        assertEquals(expectedEndDateTime, task.getEndTime());
    }

    @Test
    void getId() {
        int taskId = 1;

        assertEquals(taskId, task.getId());
    }

    @Test
    void setTaskName() {
        String newTaskName = "Новое имя задачи 1";

        task.setTaskName(newTaskName);
        assertEquals(newTaskName, task.getTaskName());
    }

    @Test
    void setDescription() {
        String newTaskDescription = "Новое описание задачи 1";

        task.setDescription(newTaskDescription);
        assertEquals(newTaskDescription, task.getDescription());
    }

    @Test
    void setStatus() {
        Status newTaskStatus = Status.IN_PROGRESS;

        task.setStatus(newTaskStatus);
        assertEquals(newTaskStatus, task.getStatus());
    }

    @Test
    void getParentEpicID() {
        assertNull(task.getParentEpicID());
    }
}