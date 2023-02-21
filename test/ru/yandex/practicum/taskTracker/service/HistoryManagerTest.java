package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    HistoryManager historyManager;
    TaskManager taskManager;
    final List<Task> taskList = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager();
        LocalDateTime dateTime = LocalDateTime.of(2021, 7, 28, 1, 21);
        final int epicId = 10;
        taskList.addAll(
                Arrays.asList(
                        new Task(
                                "Задача 1",
                                "Описание задачи 1",
                                dateTime.plusMinutes(15),
                                Duration.ofMinutes(15),
                                taskManager.assignID()),
                        new Task(
                                "Задача 2",
                                "Описание задачи 2",
                                dateTime,
                                Duration.ofMinutes(15),
                                taskManager.assignID()),
                        new Task(
                                "Задача 3",
                                "Описание задачи 3",
                                taskManager.assignID(),
                                Status.IN_PROGRESS),
                        new Epic("Эпик 1", "Описание эпика 1", epicId),
                        new Epic("Эпик 2", "Описание эпика 2", epicId + 1, Status.NEW),
                        new Subtask(
                                "Подзадача 1",
                                "Описание подзадачи 1",
                                dateTime.plusMinutes(20),
                                Duration.ofMinutes(15),
                                taskManager.assignID(),
                                epicId),
                        new Subtask(
                                "Подзадача 2",
                                "Описание подзадачи 2",
                                dateTime.minusMinutes(115),
                                Duration.ofMinutes(15),
                                taskManager.assignID(),
                                epicId),
                        new Subtask(
                                "Подзадача 3",
                                "Описание подзадачи 3",
                                taskManager.assignID(),
                                epicId,
                                Status.NEW)));
    }

    @Test
    void historyManagerTest() {
        assertEquals(Collections.emptyList(), historyManager.getHistory(), "Список должен быть пустым.");
        taskList.forEach(historyManager::add);
        assertEquals(taskList.size(), historyManager.getHistory().size(), "Неверное количество задач.");
        assertEquals(taskList, historyManager.getHistory(), "Неверный порядок добавленных задач.");

        historyManager.add(taskList.get(0));
        historyManager.add(taskList.get(taskList.size() - 1));
        Set<Task> elements = new HashSet<>();
        Set<Task> duplicates = historyManager.getHistory().stream()
                .filter(e -> !elements.add(e))
                .collect(Collectors.toSet());
        assertEquals(0, duplicates.size(), "История содержит дубликаты.");

        final List<Task> expectedHistoryList = historyManager.getHistory();
        final Task firstTask = historyManager.getHistory().get(0);
        historyManager.remove(firstTask.getId());
        assertNotEquals(expectedHistoryList, historyManager.getHistory(), "Количество задач не изменилось.");
        expectedHistoryList.remove(firstTask);
        assertEquals(expectedHistoryList, historyManager.getHistory(), "Порядок задач не совпадает.");

        final Task lastTask = historyManager.getHistory().get(historyManager.getHistory().size() - 1);
        historyManager.remove(lastTask.getId());
        assertNotEquals(expectedHistoryList, historyManager.getHistory(), "Количество задач не изменилось.");
        expectedHistoryList.remove(lastTask);
        assertEquals(expectedHistoryList, historyManager.getHistory(), "Порядок задач не совпадает.");

        final Task averageTask = historyManager.getHistory().get(historyManager.getHistory().size() / 2);
        historyManager.remove(averageTask.getId());
        assertNotEquals(expectedHistoryList, historyManager.getHistory(), "Количество задач не изменилось.");
        expectedHistoryList.remove(averageTask);
        assertEquals(expectedHistoryList, historyManager.getHistory(), "Порядок задач не совпадает.");
    }
}