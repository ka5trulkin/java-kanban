package ru.yandex.practicum.taskTracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private final TaskManager manager = new InMemoryTaskManager();
    private final List<Subtask> subtaskList = new ArrayList<>();
    private Epic epicTest;
    private Subtask subtaskTest;

    @BeforeEach
    void beforeEach() {
        LocalDateTime dateTime = LocalDateTime.of(2022, 7, 28, 1, 21);

        manager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1", manager.setId()));
        manager.addNewEpic(new Epic("Эпик 2", "Описание эпика 2", manager.setId(), Status.NEW));
        subtaskList.addAll(
                Arrays.asList(
                        new Subtask(
                                "Подзадача 1",
                                "Описание подзадачи 1",
                                dateTime.plusDays(2),
                                Duration.ofMinutes(15),
                                manager.setId(),
                                manager.getEpics().get(0).getId()),
                        new Subtask(
                                "Подзадача 2",
                                "Описание подзадачи 2",
                                dateTime,
                                Duration.ofMinutes(30),
                                manager.setId(),
                                manager.getEpics().get(0).getId()),
                        new Subtask(
                                "Подзадача 3",
                                "Описание подзадачи 3",
                                manager.setId(),
                                manager.getEpics().get(0).getId(),
                                Status.NEW)
                )
        );
        epicTest = manager.getEpics().get(0);
        subtaskTest = subtaskList.get(0);
    }

    @Test
    void epicStatusTest() {
        assertEquals(Status.NEW, epicTest.getStatus());

        subtaskList.forEach(manager::addNewSubtask);
        assertEquals(Status.IN_PROGRESS, epicTest.getStatus());

        manager.getSubtasks().forEach(subtask -> subtask.setStatus(Status.DONE));
        manager.updateSubtask(subtaskTest);
        assertEquals(Status.DONE, epicTest.getStatus());

        subtaskTest.setStatus(Status.NEW);
        manager.updateSubtask(subtaskTest);
        assertEquals(Status.IN_PROGRESS, epicTest.getStatus());

        manager.getSubtasks().forEach(subtask -> subtask.setStatus(Status.IN_PROGRESS));
        manager.updateSubtask(subtaskTest);
        assertEquals(Status.IN_PROGRESS, epicTest.getStatus());
    }

    @Test
    void getSubtasksId() {
        assertEquals(Collections.emptyList(), manager.getEpics().get(0).getSubtasksId());
        subtaskList.forEach(manager::addNewSubtask);

        List<Integer> subtaskIdList = subtaskList.stream().map(Task::getId).collect(Collectors.toList());

        assertEquals(subtaskIdList, manager.getEpics().get(0).getSubtasksId());
    }

    @Test
    void addSubtask() {
        assertEquals(Collections.emptyList(), epicTest.getSubtasksId());

        manager.getEpics().get(0).addSubtask(subtaskTest.getId());
        assertEquals(Collections.singletonList(subtaskTest.getId()), epicTest.getSubtasksId());
    }

    @Test
    void removeSubtask() {
        assertEquals(Collections.emptyList(), epicTest.getSubtasksId());

        List<Integer> subtaskIdList = subtaskList.stream().map(Task::getId).collect(Collectors.toList());

        subtaskList.forEach(manager::addNewSubtask);
        assertEquals(subtaskIdList, epicTest.getSubtasksId());

        subtaskList.remove(subtaskTest);
        epicTest.removeSubtask(subtaskTest.getId());
        subtaskIdList = subtaskList.stream().map(Task::getId).collect(Collectors.toList());
        assertEquals(subtaskIdList, epicTest.getSubtasksId());
    }

    @Test
    void clearSubtasks() {
        assertEquals(Collections.emptyList(), epicTest.getSubtasksId());

        subtaskList.forEach(manager::addNewSubtask);
        assertNotEquals(Collections.emptyList(), epicTest.getSubtasksId());

        epicTest.clearSubtasks();
        assertEquals(Collections.emptyList(), epicTest.getSubtasksId());
        assertEquals(Status.NEW, epicTest.getStatus());
        assertNull(epicTest.getStartTime());
        assertNull(epicTest.getEndTime());
        assertNull(epicTest.getEndTime());
    }

    @Test
    void getEpicEndTime() {
        LocalDateTime expectedEndTime = subtaskList.stream()
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElseThrow();

        assertNull(epicTest.getEndTime());
        subtaskList.forEach(manager::addNewSubtask);
        assertEquals(expectedEndTime, epicTest.getEndTime());

        manager.clearAllSubtasks();
        assertNull(epicTest.getEndTime());
    }
}