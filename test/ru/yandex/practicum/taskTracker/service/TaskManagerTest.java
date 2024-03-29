package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

abstract class TaskManagerTest<T extends TaskManager> {
    public TaskManagerTest(T manager) {
        this.manager = manager;
    }

    protected T manager;
    protected final List<Task> taskList = new ArrayList<>();
    protected final List<Epic> epicList = new ArrayList<>();
    protected final List<Subtask> subtaskList = new ArrayList<>();
    protected int idNonexistent;
    protected Task taskTest;
    protected Epic epicTest;
    protected Subtask subtaskTest;

    @BeforeEach
    void beforeEach() {
        LocalDateTime dateTime = LocalDateTime.of(2022, 7, 28, 1, 21);
        taskList.addAll(
                Arrays.asList(
                        new Task(
                                "Задача 1",
                                "Описание задачи 1",
                                dateTime.plusMinutes(15),
                                Duration.ofMinutes(15),
                                manager.assignID()),
                        new Task(
                                "Задача 2",
                                "Описание задачи 2",
                                dateTime,
                                Duration.ofMinutes(15),
                                manager.assignID()),
                        new Task(
                                "Задача 3",
                                "Описание задачи 3",
                                manager.assignID(),
                                Status.IN_PROGRESS)));
        taskTest = taskList.get(0);
        epicList.add(new Epic("Эпик 1", "Описание эпика 1", manager.assignID()));
        epicList.add(new Epic("Эпик 2", "Описание эпика 2", manager.assignID(), Status.NEW));
        epicTest = epicList.get(0);
        subtaskList.addAll(
                Arrays.asList(
                        new Subtask(
                                "Подзадача 1",
                                "Описание подзадачи 1",
                                dateTime.plusMinutes(20),
                                Duration.ofMinutes(15),
                                manager.assignID(),
                                epicTest.getId()),
                        new Subtask(
                                "Подзадача 2",
                                "Описание подзадачи 2",
                                dateTime.minusMinutes(115),
                                Duration.ofMinutes(15),
                                manager.assignID(),
                                epicTest.getId()),
                        new Subtask(
                                "Подзадача 3",
                                "Описание подзадачи 3",
                                manager.assignID(),
                                epicTest.getId(),
                                Status.NEW)));
        subtaskTest = subtaskList.get(0);
        idNonexistent = 777;
    }

    @Test
    void addNewTask() {
        manager.addNewTask(
                new Task(
                        taskTest.getTaskName(),
                        taskTest.getDescription(),
                        taskTest.getStartTime(),
                        taskTest.getDuration(),
                        taskTest.getId()));

        final int taskId = taskTest.getId();
        final Task task = manager.getTaskById(taskId);

        assertNotNull(task, "Задача не найдена.");
        assertEquals(taskTest, task, "Задачи не совпадают.");

        final List<Task> taskList = manager.getTasks();

        assertNotNull(taskList, "Задачи на возвращаются.");
        assertEquals(1, taskList.size(), "Неверное количество задач.");
        assertEquals(taskTest, taskList.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        manager.addNewEpic(new Epic(epicTest.getTaskName(), epicTest.getDescription(), epicTest.getId()));

        final int epicId = epicTest.getId();
        final Epic epic = manager.getEpicById(epicId);
        assertNotNull(epic, "Эпик не найден.");
        assertEquals(epicTest, epic, "Эпики не совпадают.");
        final List<Epic> epicTestList = manager.getEpics();
        assertNotNull(epicTestList, "Эпики на возвращаются.");
        assertEquals(1, epicTestList.size(), "Неверное количество эпиков.");
        assertEquals(epicTest, epicTestList.get(0), "Эпики не совпадают.");
        assertEquals(Status.NEW, epic.getStatus());
        subtaskList.forEach(manager::addNewSubtask);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус Эпика не совпадает.");
        manager.getSubtasks().forEach(subtask -> subtask.setStatus(Status.DONE));
        manager.updateEpic(epic);
        assertEquals(Status.DONE, epic.getStatus(), "Статус Эпика не совпадает.");
        manager.deleteAllSubtasks();
        assertEquals(Status.NEW, epic.getStatus(), "Статус Эпика не совпадает.");
    }

    @Test
    void addNewSubtask() {
        IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> manager.addNewSubtask(subtaskTest));
        String expectedException = "Подзадача ID:" + subtaskTest.getId()
                + " не принадлежит эпику ID:" + subtaskTest.getEpicId();
        assertEquals(expectedException, exception1.getMessage(), "Подзадача добавлена в несуществующий эпик");
        manager.addNewEpic(epicTest);
        manager.addNewSubtask(
                new Subtask(
                        subtaskTest.getTaskName(),
                        subtaskTest.getDescription(),
                        subtaskTest.getStartTime(),
                        subtaskTest.getDuration(),
                        subtaskTest.getId(),
                        subtaskTest.getEpicId()));

        final int subtaskId = subtaskTest.getId();
        final Subtask subtask = manager.getSubTaskById(subtaskId);

        assertNotNull(subtask, "Подзадача не найдена.");
        assertEquals(subtaskTest, subtask, "Подзадачи не совпадают.");

        final List<Subtask> subtaskTestList = manager.getSubtasks();

        assertNotNull(subtaskTestList, "Подзадачи на возвращаются.");
        assertEquals(1, subtaskTestList.size(), "Неверное количество подзадач.");
        assertEquals(subtaskTest, subtaskTestList.get(0), "Подзадачи не совпадают.");

        manager.deleteAllEpics();
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> manager.addNewSubtask(subtask));
        assertEquals(expectedException, exception2.getMessage(), "Подзадача добавлена в несуществующий эпик");
    }

    @Test
    void getTasks() {
        taskList.forEach(manager::addNewTask);
        assertEquals(taskList, manager.getTasks(), "Задачи не совпадают.");
        assertEquals(taskList.size(), manager.getTasks().size(), "Неверное количество задач.");

        manager.deleteAllTasks();
        assertEquals(Collections.emptyList(), manager.getTasks(), "Список должен быть пуст.");
    }

    @Test
    void getEpics() {
        epicList.forEach(manager::addNewEpic);
        assertEquals(epicList, manager.getEpics(), "Эпики не совпадают.");
        assertEquals(epicList.size(), manager.getEpics().size(), "Неверное количество эпиков.");

        manager.deleteAllEpics();
        assertEquals(Collections.emptyList(), manager.getEpics(), "Список должен быть пуст.");
    }

    @Test
    void getSubtasks() {
        manager.addNewEpic(epicTest);
        subtaskList.forEach(manager::addNewSubtask);
        assertEquals(subtaskList, manager.getSubtasks(), "Подзадачи не совпадают.");
        assertEquals(subtaskList.size(), manager.getSubtasks().size(), "Неверное количество подзадач.");

        manager.deleteAllSubtasks();
        assertEquals(Collections.emptyList(), manager.getSubtasks(), "Список должен быть пуст.");
    }

    @Test
    void getTaskById() {
        final Task task = taskTest;
        int taskId = task.getId();

        manager.addNewTask(taskTest);
        assertEquals(task, manager.getTaskById(taskId), "Задачи не совпадают.");

        String expectedException = "Задача с ID:" + idNonexistent + " не найдена.";

        IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> manager.getTaskById(idNonexistent));
        assertEquals(expectedException, exception1.getMessage(), "Задача не должна быть получена.");

        manager.deleteAllTasks();
        expectedException = "Задача с ID:" + taskId + " не найдена.";

        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> manager.getTaskById(taskId));
        assertEquals(expectedException, exception2.getMessage(), "Задача не должна быть получена.");

    }

    @Test
    void getEpicById() {
        final Epic epic = epicTest;
        int epicId = epic.getId();

        manager.addNewEpic(epicTest);
        assertEquals(epic, manager.getEpicById(epicId), "Эпики не совпадают.");

        String expectedException = "Эпик с ID:" + idNonexistent + " не найден.";

        IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> manager.getEpicById(idNonexistent));
        assertEquals(expectedException, exception1.getMessage(), "Эпик не должен быть получен.");

        manager.deleteAllEpics();
        expectedException = "Эпик с ID:" + epicId + " не найден.";
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> manager.getEpicById(epicId));
        assertEquals(expectedException, exception2.getMessage(), "Эпик не должен быть получен.");
    }

    @Test
    void getSubTaskById() {
        final Subtask subtask = subtaskTest;
        int subtaskId = subtask.getId();

        manager.addNewEpic(epicTest);
        manager.addNewSubtask(subtask);
        assertEquals(subtask, manager.getSubTaskById(subtaskId), "Подзадачи не совпадают.");

        String expectedException = "Подзадача с ID:" + idNonexistent + " не найдена.";

        IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> manager.getSubTaskById(idNonexistent));
        assertEquals(expectedException, exception1.getMessage(), "Подзадача не должна быть получена.");

        manager.deleteAllSubtasks();
        expectedException = "Подзадача с ID:" + subtaskId + " не найдена.";
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> manager.getSubTaskById(subtaskId));
        assertEquals(expectedException, exception2.getMessage(), "Подзадача не должна быть получена.");
    }

    @Test
    void clearAllTasks() {
        taskList.forEach(manager::addNewTask);
        assertEquals(taskList, manager.getTasks(), "Задачи не совпадают.");
        manager.deleteAllTasks();

        String expectedException = "Задача с ID:" + taskTest.getId() + " не найдена.";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> manager.getTaskById(taskTest.getId()));
        assertEquals(expectedException, exception.getMessage(), "Задача не удалена.");
        assertEquals(Collections.emptyList(), manager.getTasks(), "Задачи не удалены.");
    }

    @Test
    void clearAllEpics() {
        epicList.forEach(manager::addNewEpic);
        subtaskList.forEach(manager::addNewSubtask);
        assertEquals(epicList, manager.getEpics(), "Эпики не совпадают.");
        assertEquals(subtaskList, manager.getSubtasks(), "Подзадачи не совпадают.");
        manager.deleteAllEpics();

        String expectedException = "Эпик с ID:" + epicTest.getId() + " не найден.";

        IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> manager.getEpicById(epicTest.getId()),
                "Эпик не удален.");
        assertEquals(expectedException, exception1.getMessage(), "Эпик не удален.");

        expectedException = "Подзадача с ID:" + subtaskTest.getId() + " не найдена.";
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> manager.getSubTaskById(subtaskTest.getId()),
                "Подзадача не удалена.");
        assertEquals(expectedException, exception2.getMessage(), "Подзадача не удалена.");
        assertEquals(Collections.emptyList(), manager.getEpics(), "Эпики не удалены.");
        assertEquals(Collections.emptyList(), manager.getSubtasks(), "Подзадачи не удалены.");
    }

    @Test
    void clearAllSubtasks() {
        epicList.forEach(manager::addNewEpic);
        subtaskList.forEach(manager::addNewSubtask);
        assertEquals(epicList, manager.getEpics(), "Эпики не совпадают.");
        assertEquals(subtaskList, manager.getSubtasks(), "Подзадачи не совпадают.");
        assertEquals(Status.IN_PROGRESS, epicTest.getStatus(), "Статус не совпадает.");
        manager.deleteAllSubtasks();

        String expectedException = "Подзадача с ID:" + subtaskTest.getId() + " не найдена.";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> manager.getSubTaskById(subtaskTest.getId()),
                "Подзадача не удалена.");
        assertEquals(expectedException, exception.getMessage(), "Подзадача не удалена.");
        assertEquals(epicTest, manager.getEpicById(epicTest.getId()), "Эпик не должен быть удален.");
        assertEquals(Collections.emptyList(), manager.getSubtasks(), "Подзадачи не удалены.");
        assertEquals(epicList, manager.getEpics(), "Эпики не должны быть удалены.");
        assertEquals(Status.NEW, epicTest.getStatus(), "Статус не совпадает.");
    }

    @Test
    void updateTask() {
        final Task task = new Task(
                "Новая задача",
                "Новое описание",
                LocalDateTime.of(2000, 10, 10, 10, 10),
                Duration.ofDays(23),
                taskTest.getId());
        final int taskId = task.getId();

        taskList.forEach(manager::addNewTask);
        assertNotEquals(task, manager.getTaskById(taskId), "Задачи совпадают.");
        manager.updateTask(task);
        assertEquals(task, manager.getTaskById(taskId), "Задачи не совпадают.");
        assertEquals(taskList.size(), manager.getTasks().size(), "Неверное количество задач.");

        final Task taskNonexistent = new Task(task.getTaskName(), task.getDescription(), idNonexistent);
        String expectedException = "Ошибка добавления " + taskNonexistent;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> manager.updateTask(taskNonexistent));
        assertEquals(expectedException, exception.getMessage(), "Задача не должна быть обновлена.");
    }

    @Test
    void updateEpic() {
        final Epic epic = new Epic("Новый эпик", "Новое описание", epicTest.getId());
        final int epicId = epic.getId();

        epicList.forEach(manager::addNewEpic);
        assertNotEquals(epic, manager.getEpicById(epicId), "Эпики совпадают.");
        manager.updateEpic(epic);
        assertEquals(epic, manager.getEpicById(epicId), "Эпики не совпадают.");
        assertEquals(epicList.size(), manager.getEpics().size(), "Неверное количество задач.");
        assertEquals(Status.NEW, manager.getEpicById(epicId).getStatus(), "Статус не совпадает.");
        subtaskList.forEach(manager::addNewSubtask);
        manager.getSubtasks().forEach(subtask -> subtask.setStatus(Status.DONE));
        manager.updateEpic(epic);
        assertEquals(Status.DONE, manager.getEpicById(epicId).getStatus(), "Статус не совпадает.");
        manager.getSubTaskById(subtaskTest.getId()).setStatus(Status.IN_PROGRESS);
        manager.updateEpic(epic);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epicId).getStatus(), "Статус не совпадает.");
        manager.deleteAllSubtasks();
        assertEquals(Status.NEW, manager.getEpicById(epicId).getStatus(), "Статус не совпадает.");

        final Epic epicNonexistent = new Epic(epicTest.getTaskName(), epicTest.getDescription(), idNonexistent);
        String expectedException = "Ошибка добавления " + epicNonexistent;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> manager.updateEpic(epicNonexistent));
        assertEquals(expectedException, exception.getMessage(), "Эпик не должен быть обновлен.");
    }

    @Test
    void updateSubtask() {
        final Subtask subtask = new Subtask(
                "Новая подзадача",
                "Новое описание",
                LocalDateTime.of(2077, 11, 11, 12, 12),
                Duration.ofHours(16),
                subtaskTest.getId(),
                subtaskTest.getEpicId());
        final int subtaskId = subtask.getId();

        String expectedException = "Ошибка добавления " + subtask;

        IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> manager.updateSubtask(subtask));
        assertEquals(expectedException, exception1.getMessage(), "Подзадача не должна существовать.");

        epicList.forEach(manager::addNewEpic);
        subtaskList.forEach(manager::addNewSubtask);
        assertNotEquals(subtask, manager.getSubTaskById(subtaskId), "Подзадачи совпадают.");
        manager.updateSubtask(subtask);
        assertEquals(subtask, manager.getSubTaskById(subtaskId), "Подзадачи не совпадают.");
        assertEquals(subtaskList.size(), manager.getSubtasks().size(), "Неверное количество подзадач.");

        final Subtask subtaskWithNonexistentEpicId = new Subtask(
                "Новая подзадача",
                "Новое описание",
                LocalDateTime.of(2077, 11, 11, 12, 12),
                Duration.ofHours(16),
                subtaskId,
                idNonexistent);
        expectedException = "Подзадача ID:" + subtaskId + " не принадлежит эпику ID:" + idNonexistent;
        IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> manager.updateSubtask(subtaskWithNonexistentEpicId));
        assertEquals(expectedException, exception2.getMessage(), "Подзадача добавлена в несуществующий эпик.");
    }

    @Test
    void removeTaskById() {
        final int taskId = taskTest.getId();
        String expectedException = "Задача ID:" + idNonexistent + " не найдена";

        assertThrows(
                IllegalArgumentException.class,
                () -> manager.deleteTaskById(taskId),
                "Задача не должна существовать.");
        taskList.forEach(manager::addNewTask);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> manager.deleteTaskById(idNonexistent));
        assertEquals(expectedException, exception.getMessage(), "Задача не должна существовать.");
        manager.deleteTaskById(taskId);
        assertEquals(taskList.size() - 1, manager.getTasks().size(), "Неверное количество задач.");
        assertThrows(
                IllegalArgumentException.class,
                () -> manager.getTaskById(taskId),
                "Задача не удалена.");
    }

    @Test
    void removeEpicById() {
        final int epicId = epicTest.getId();
        String expectedException = "Эпик ID:" + idNonexistent + " не найден";

        assertThrows(
                IllegalArgumentException.class,
                () -> manager.deleteEpicById(epicId),
                "Задача не должна существовать.");
        epicList.forEach(manager::addNewEpic);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> manager.deleteEpicById(idNonexistent));
        assertEquals(expectedException, exception.getMessage(), "Эпик не должен существовать.");
        subtaskList.forEach(manager::addNewSubtask);
        manager.deleteEpicById(epicId);
        assertEquals(Collections.emptyList(), manager.getSubtasks(), "Список должен быть пустым.");
        assertThrows(
                IllegalArgumentException.class,
                () -> manager.getEpicById(epicId),
                "Эпик не удален.");
        manager.deleteAllSubtasks();
    }

    @Test
    void removeSubtaskById() {
        final int subtaskId = subtaskTest.getId();
        String expectedException = "Подзадача ID:" + idNonexistent + " не найдена";

        manager.addNewEpic(epicTest);
        assertThrows(
                IllegalArgumentException.class,
                () -> manager.deleteSubtaskById(subtaskId),
                "Подзадача не должна существовать.");
        manager.addNewSubtask(subtaskTest);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> manager.deleteSubtaskById(idNonexistent));
        assertEquals(expectedException, exception.getMessage(), "Подзадача не должна существовать.");
        assertEquals(Status.IN_PROGRESS, epicTest.getStatus(), "Статусы не совпадают.");
        manager.deleteSubtaskById(subtaskId);
        assertEquals(Status.NEW, epicTest.getStatus(), "Статусы не совпадают.");
        assertThrows(
                IllegalArgumentException.class,
                () -> manager.deleteSubtaskById(subtaskId),
                "Подзадача не удалена.");
        subtaskList.forEach(manager::addNewSubtask);
        manager.deleteSubtaskById(subtaskId);
        assertEquals(subtaskList.size() - 1, manager.getSubtasks().size(), "Неверное количество задач.");
    }

    @Test
    void getSubtasksFromEpic() {
        assertEquals(Collections.emptyList(), manager.getSubtasksFromEpic(epicTest), "Список должен быть пустым.");
        manager.addNewEpic(epicTest);
        assertEquals(Collections.emptyList(), manager.getSubtasksFromEpic(epicTest), "Список должен быть пустым.");
        subtaskList.forEach(manager::addNewSubtask);
        assertEquals(subtaskList.size(), manager.getSubtasksFromEpic(epicTest).size(), "Неверное количество задач.");
        manager.deleteSubtaskById(subtaskTest.getId());
        assertEquals(subtaskList.size() - 1, manager.getSubtasksFromEpic(epicTest).size(), "Неверное количество задач.");
        manager.deleteAllSubtasks();
        assertEquals(Collections.emptyList(), manager.getSubtasksFromEpic(epicTest), "Список должен быть пустым.");
    }

    @Test
    void getPrioritizedTasks() {
        List<String> list = new ArrayList<>(
                List.of("Подзадача 2", "Задача 2", "Задача 1", "Подзадача 1", "Задача 3", "Подзадача 3"));
        assertEquals(Collections.emptyList(), manager.getPrioritizedTasks(), "Список должен быть пустым.");
        taskList.forEach(manager::addNewTask);
        epicList.forEach(manager::addNewEpic);
        subtaskList.forEach(manager::addNewSubtask);
        assertFalse(manager.getPrioritizedTasks().contains(epicTest), "Список не должен содержать эпик.");
        assertEquals(
                list,
                manager.getPrioritizedTasks().stream().map(Task::getTaskName).collect(Collectors.toList()),
                "Задачи не совпадают.");
        manager.deleteSubtaskById(subtaskTest.getId());
        list.remove("Подзадача 1");
        assertEquals(
                list,
                manager.getPrioritizedTasks().stream().map(Task::getTaskName).collect(Collectors.toList()),
                "Задачи не совпадают.");
        manager.deleteTaskById(taskTest.getId());
        list.remove("Задача 1");
        assertEquals(
                list,
                manager.getPrioritizedTasks().stream().map(Task::getTaskName).collect(Collectors.toList()),
                "Задачи не совпадают.");
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        assertEquals(Collections.emptyList(), manager.getPrioritizedTasks(), "Список должен быть пустым.");
    }
}