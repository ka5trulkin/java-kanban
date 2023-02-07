package ru.yandex.practicum.taskTracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    Task task;

    @BeforeEach
    void beforeEach() {
        task = new Task(
                "Задача 1",
                "Описание задачи 1",
                LocalDateTime.of(1978, 7, 28, 1, 21),
                Duration.ofMinutes(15),
                1);
    }

    @Test
    void getType() {
        Type type = Type.TASK;

        assertEquals(type, task.getType(), "Тип задачи не совпадает.");
    }

    @Test
    void getTaskName() {
        String taskName = "Задача 1";

        assertEquals(taskName, task.getTaskName(), "Имя задачи не совпадает.");
    }

    @Test
    void getDescription() {
        String taskDescription = "Описание задачи 1";

        assertEquals(taskDescription, task.getDescription(), "Описание задачи не совпадает.");
    }

    @Test
    void getStatus() {
        Status taskStatus = Status.NEW;

        assertEquals(taskStatus, task.getStatus(), "Статус задачи не совпадает.");
    }

    @Test
    void getStartTime() {
        LocalDateTime expectedStartDateTime = LocalDateTime.of(1978, 7, 28, 1, 21);
        assertEquals(expectedStartDateTime, task.getStartTime(), "Дата начала задачи не совпадает.");
    }

    @Test
    void getEndTime() {
        LocalDateTime expectedEndDateTime = LocalDateTime.of(1978, 7, 28, 1, 36);
        assertEquals(expectedEndDateTime, task.getEndTime(), "Дата окончания задачи не совпадает.");
    }

    @Test
    void getId() {
        int taskId = 1;

        assertEquals(taskId, task.getId(), "ID задачи не совпадает.");
    }

    @Test
    void setTaskName() {
        String newTaskName = "Новое имя задачи 1";

        task.setTaskName(newTaskName);
        assertEquals(newTaskName, task.getTaskName(), "Имя задачи не совпадает.");
    }

    @Test
    void setDescription() {
        String newTaskDescription = "Новое описание задачи 1";

        task.setDescription(newTaskDescription);
        assertEquals(newTaskDescription, task.getDescription(), "Описание задачи не совпадает.");
    }

    @Test
    void setStartTime() {
        LocalDateTime expectedStartDateTime = LocalDateTime.of(1978, 7, 28, 1, 41);

        task.setStartTime(task.getStartTime().plusMinutes(20));
        assertEquals(expectedStartDateTime, task.getStartTime(), "Дата начала задачи не совпадает.");
    }

    @Test
    void setDuration() {
        int expectedDuration = 50;

        task.setDuration(Duration.ofMinutes(50));
        assertEquals(expectedDuration, task.getDuration().toMinutes(), "Продолжительность задачи не совпадает.");
    }

    @Test
    void setStatus() {
        Status newTaskStatus = Status.IN_PROGRESS;

        task.setStatus(newTaskStatus);
        assertEquals(newTaskStatus, task.getStatus(), "Статус задачи не совпадает.");
    }

    @Test
    void getParentEpicID() {
        assertNull(task.getParentEpicID(), "ID эпика должно быть пустым.");
    }
}