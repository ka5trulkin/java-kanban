package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    public InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }

    @Test
    void checkEpicStatus() {
        manager.addNewEpic(epicTest);
        assertEquals(Status.NEW, epicTest.getStatus(), "Статус не совпадает.");
        subtaskList.forEach(manager::addNewSubtask);
        assertEquals(Status.IN_PROGRESS, epicTest.getStatus(), "Статус не совпадает.");
        manager.getSubtasks().forEach(subtask -> subtask.setStatus(Status.DONE));
        manager.updateEpic(epicTest);
        assertEquals(Status.DONE, epicTest.getStatus(), "Статус не совпадает.");
        manager.getSubTaskById(subtaskTest.getId()).setStatus(Status.NEW);
        manager.updateEpic(epicTest);
        assertEquals(Status.IN_PROGRESS, epicTest.getStatus(), "Статус не совпадает.");
        manager.getSubtasks().forEach(subtask -> subtask.setStatus(Status.IN_PROGRESS));
        manager.updateEpic(epicTest);
        assertEquals(Status.IN_PROGRESS, epicTest.getStatus(), "Статус не совпадает.");
        manager.deleteAllSubtasks();
        assertEquals(Status.NEW, epicTest.getStatus(), "Статус не совпадает.");
    }

    @Test
    void checkTasksToCrossByStartTime() {
        final Task task = new Task(
                "Новая задача",
                "Новое описание",
                taskTest.getStartTime(),
                Duration.ofMinutes(10),
                idNonexistent
        );
        final Subtask subtask = new Subtask(
                "Новая подзадача",
                "Новое описание",
                subtaskTest.getStartTime(),
                Duration.ofMinutes(25),
                idNonexistent,
                epicTest.getId()
        );
        String errorByCrossTask = "Задача ID:" + task.getId() + " пересекается с другой задачей по времени: " + task.getStartTime();
        String errorByCrossSubtask = "Задача ID:" + subtask.getId() + " пересекается с другой задачей по времени: " + subtask.getStartTime();

        taskList.forEach(manager::addNewTask);
        epicList.forEach(manager::addNewEpic);
        subtaskList.forEach(manager::addNewSubtask);
        IllegalArgumentException taskException = assertThrows(
                IllegalArgumentException.class,
                () -> manager.addNewTask(task),
                "Задачи не пересекаются по времени."
        );
        assertEquals(errorByCrossTask, taskException.getMessage());
        IllegalArgumentException subtaskException = assertThrows(
                IllegalArgumentException.class,
                () -> manager.addNewTask(subtask),
                "Задачи не пересекаются по времени."
        );
        assertEquals(errorByCrossSubtask, subtaskException.getMessage());
    }
}