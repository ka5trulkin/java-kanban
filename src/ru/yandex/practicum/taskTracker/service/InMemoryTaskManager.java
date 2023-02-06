package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int idCounter = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasksByStartTime = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getId)
    );

    private void checkEpic(Epic epic) {
        List<Subtask> subtaskList = getSubtasksFromEpic(epic);

        setEpicStatus(subtaskList, epic);
        setEpicStartTime(subtaskList, epic);
        setEpicEndTime(subtaskList, epic);
        setEpicDuration(subtaskList, epic);
    }

    private void setEpicStatus(List<Subtask> subtaskList, Epic epic) {
        List<Status> statusList = subtaskList
                .stream()
                .map(Task::getStatus)
                .collect(Collectors.toList());

        if (statusList.contains(Status.IN_PROGRESS) || statusList.contains(Status.NEW)) {
            epic.setStatus(Status.IN_PROGRESS);
        } else if (statusList.contains(Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else epic.setStatus(Status.NEW);
    }

    private void setEpicStartTime(List<Subtask> subtaskList, Epic epic) {
        LocalDateTime epicStartTime = subtaskList
                .stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);

        epic.setStartTime(epicStartTime);
    }

    private void setEpicEndTime(List<Subtask> subtaskList, Epic epic) {
        LocalDateTime epicEndTime = subtaskList
                .stream()
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        epic.setEndTime(epicEndTime);
    }

    private void setEpicDuration(List<Subtask> subtaskList, Epic epic) {
        Duration epicDuration = subtaskList
                .stream()
                .map(Task::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration::plus)
                .orElse(Duration.ZERO);

        epic.setDuration(epicDuration);
    }

    private void updatePrioritizedTasksByStartTime(Task oldTask, Task newTask) {
        prioritizedTasksByStartTime.remove(oldTask);
        prioritizedTasksByStartTime.add(newTask);
    }

    private void checkTaskToCrossByStartTime(Task task) {
        if ((task.getStartTime() != null) && (task.getType() != Type.EPIC)) {
            boolean isCross = prioritizedTasksByStartTime
                    .stream()
                    .filter(prioritizedTask -> prioritizedTask.getStartTime() != null)
                    .anyMatch(prioritizedTask -> prioritizedTask.getStartTime().equals(task.getStartTime()));
            if (isCross) {
                throw new IllegalArgumentException(
                        "Задача ID:"
                                + task.getId()
                                + " пересекается с другой задачей по времени: "
                                + task.getStartTime());
            }
        }
    }

    @Override
    public int setId() {
        return ++idCounter;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasksByStartTime);
    }

    @Override
    public List<Task> getTasks() {
        tasks.values().forEach(historyManager::add);
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        epics.values().forEach(historyManager::add);
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        subtasks.values().forEach(historyManager::add);
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.values().forEach(prioritizedTasksByStartTime::remove);
        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(historyManager::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearAllSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.values().forEach(prioritizedTasksByStartTime::remove);
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            historyManager.add(epic);
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            historyManager.add(tasks.get(taskId));
            return tasks.get(taskId);
        } else throw new IllegalArgumentException("Задача с ID:" + taskId + " не найдена.");
    }

    @Override
    public Epic getEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            historyManager.add(epics.get(epicId));
            return epics.get(epicId);
        } else throw new IllegalArgumentException("Эпик с ID:" + epicId + " не найден.");
    }

    @Override
    public Subtask getSubTaskById(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            historyManager.add(subtasks.get(subtaskId));
            return subtasks.get(subtaskId);
        } else throw new IllegalArgumentException("Подзадача с ID:" + subtaskId + " не найдена.");
    }

    @Override
    public void addNewTask(Task task) {
        if (task != null) {
            checkTaskToCrossByStartTime(task);
            tasks.put(task.getId(), task);
            prioritizedTasksByStartTime.add(task);
        }
    }

    @Override
    public void addNewEpic(Epic epic) {
        if (epic != null) {
            checkTaskToCrossByStartTime(epic);
            epics.put(epic.getId(), epic);
            checkEpic(epic);
        }
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        if (subtask != null) {
            int subtaskId = subtask.getId();

            if (!epics.containsKey(subtask.getEpicId())) {
                throw new IllegalArgumentException(
                        "Эпика ID:" + subtask.getEpicId()
                                + " для Подзадачи ID:" + subtaskId + " не существует"
                );
            }
            Epic epic = epics.get(subtask.getEpicId());

            checkTaskToCrossByStartTime(subtask);
            subtasks.put(subtaskId, subtask);
            prioritizedTasksByStartTime.add(subtask);
            epic.addSubtask(subtaskId);
            checkEpic(epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        if ((task != null) && (tasks.containsKey(task.getId()))) {
            int taskId = task.getId();

            checkTaskToCrossByStartTime(task);
            updatePrioritizedTasksByStartTime(tasks.get(taskId), task);
            tasks.put(taskId, task);
        } else throw new IllegalArgumentException("Ошибка добавления " + task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if ((epic != null) && epics.containsKey(epic.getId())) {
            int epicId = epic.getId();

            checkTaskToCrossByStartTime(epic);
            updatePrioritizedTasksByStartTime(epics.get(epicId), epic);
            epics.put(epicId, epic);
            checkEpic(epic);
        } else throw new IllegalArgumentException("Ошибка добавления " + epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if ((subtask != null) && (subtasks.containsKey(subtask.getId()))) {
            int subtaskId = subtask.getId();

            if (!epics.containsKey(subtask.getEpicId())) {
                throw new IllegalArgumentException(
                        "Подзадача ID:" + subtaskId
                                + " не принадлежит эпику ID:" + subtask.getEpicId()
                );
            }
            Epic epic = epics.get(subtask.getEpicId());

            if (subtasks.get(subtaskId).getStartTime() != null
                    && subtask.getStartTime() != null
                    && !subtasks.get(subtaskId).getStartTime().isEqual(subtask.getStartTime())) {
                checkTaskToCrossByStartTime(subtask);
            }
            updatePrioritizedTasksByStartTime(subtasks.get(subtaskId), subtask);
            subtasks.put(subtaskId, subtask);
            checkEpic(epic);
        } else throw new IllegalArgumentException("Ошибка добавления " + subtask);
    }

    @Override
    public void removeTaskById(int taskId) {
        if (!tasks.containsKey(taskId)) {
            throw new IllegalArgumentException("Задача ID:" + taskId + " не найдена");
        }
        prioritizedTasksByStartTime.remove(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Эпик ID:" + epicId + " не найден");
        }
        for (Integer subtaskId : epics.get(epicId).getSubtasksId()) {
            prioritizedTasksByStartTime.remove(subtasks.get(subtaskId));
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        historyManager.remove(epicId);
        epics.remove(epicId);
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) {
            throw new IllegalArgumentException("Подзадача ID:" + subtaskId + " не найдена");
        }
        Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());

        prioritizedTasksByStartTime.remove(subtasks.get(subtaskId));
        subtasks.remove(subtaskId);
        historyManager.remove(subtaskId);
        epic.removeSubtask(subtaskId);
        historyManager.add(epic);
        checkEpic(epic);
    }

    @Override
    public List<Subtask> getSubtasksFromEpic(Epic epic) {
        return epic.getSubtasksId()
                .stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }
}