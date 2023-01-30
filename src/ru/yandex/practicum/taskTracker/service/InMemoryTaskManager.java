package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.model.Epic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected static int idCounter = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasksByStartTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));



    private void checkEpicStatus(Epic epic) {
        int counter = 0;

        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasksId()) {
                if (subtasks.get(subtaskId).getStatus() == Status.DONE) {
                    counter++;
                }
            }
            if ((epic.getSubtasksId().size() == counter) && (counter > 0)) {
                epic.setStatus(Status.DONE);
            } else if (epic.getSubtasksId().size() > 0) {
                epic.setStatus(Status.IN_PROGRESS);
            } else {
                epic.setStatus(Status.NEW);
            }
        }
    }

    private void checkEpicTimes(Epic epic) {
        setEpicStartTime(epic);
        setEpicDuration(epic);
        setEpicEndTime(epic);
    }

    private void setEpicStartTime(Epic epic) {
        if (!epic.getSubtasksId().equals(Collections.emptyList())) {
            LocalDateTime epicStartTime = null;

            for (Integer subtaskId : epic.getSubtasksId()) {
                LocalDateTime subtaskStartTime = subtasks.get(subtaskId).getStartTime();

                if ((epicStartTime == null) && (subtaskStartTime != null)) {
                    epicStartTime = subtaskStartTime;
                } else if (subtaskStartTime != null && epicStartTime.isAfter(subtaskStartTime)) {
                    epicStartTime = subtaskStartTime;
                }
            }
            epic.setStartTime(epicStartTime);
        }
    }

    private void setEpicEndTime(Epic epic) {
        if (!epic.getSubtasksId().equals(Collections.emptyList())) {
            LocalDateTime epicEndTime = null;

            for (Integer subtaskId : epic.getSubtasksId()) {
                LocalDateTime subtaskEndTime = subtasks.get(subtaskId).getEndTime();

                if ((epicEndTime == null) && (subtaskEndTime != null)) {
                    epicEndTime = subtaskEndTime;
                } else if ((subtaskEndTime != null) && (epicEndTime.isBefore(subtaskEndTime))) {
                    epicEndTime = subtaskEndTime;
                }
            }
            epic.setEndTime(epicEndTime);
        }
    }

    private void setEpicDuration(Epic epic) {
        if (!epic.getSubtasksId().equals(Collections.emptyList())) {
            Duration duration = null;

            for (Integer subtaskId : epic.getSubtasksId()) {
                Duration subtaskDuration = subtasks.get(subtaskId).getDuration();

                if ((duration == null) && (subtaskDuration != null)) {
                    duration = subtaskDuration;
                } else if (subtaskDuration != null) {
                    duration = duration.plus(subtaskDuration);
                }
            }
            epic.setDuration(duration);
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
        tasks.values().forEach(historyManager :: add);
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        epics.values().forEach(historyManager :: add);
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        subtasks.values().forEach(historyManager :: add);
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearAllTasks() {
        tasks.keySet().forEach(historyManager :: remove);
        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        epics.keySet().forEach(historyManager :: remove);
        subtasks.keySet().forEach(historyManager :: remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearAllSubtasks() {
        subtasks.keySet().forEach(historyManager :: remove);
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            historyManager.add(epic);
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public Subtask getSubTaskById(int subtaskId) {
        historyManager.add(subtasks.get(subtaskId));
        return subtasks.get(subtaskId);
    }

    @Override
    public void addNewTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void addNewEpic(Epic epic) {
        if (epic != null) {
            epics.put(epic.getId(), epic);
            checkEpicStatus(epic);
            checkEpicTimes(epic);
        }
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        if (subtask != null) {
            int subtaskId = subtask.getId();
            Epic epic = epics.get(subtask.getEpicId());

            subtasks.put(subtaskId, subtask);
            epic.addSubtask(subtaskId);
            checkEpicStatus(epic);
            checkEpicTimes(epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        if ((task != null) && (tasks.containsKey(task.getId()))) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if ((epic != null) && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            checkEpicStatus(epic);
            checkEpicTimes(epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if ((subtask != null) && (subtasks.containsKey(subtask.getId()))) {
            int subtaskId = subtask.getId();
            Epic epic = epics.get(subtask.getEpicId());

            subtasks.put(subtaskId, subtask);
            epic.setSubtask(subtaskId);
            checkEpicStatus(epic);
            checkEpicTimes(epic);
        }
    }

    @Override
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        if (epics.get(epicId) != null) {
            for (Integer subtaskId : epics.get(epicId).getSubtasksId()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(epicId);
            epics.remove(epicId);
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        if (subtasks.get(subtaskId) != null) {
            Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());

            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            epic.removeSubtask(subtaskId);
            historyManager.add(epic);
            checkEpicStatus(epic);
            checkEpicTimes(epic);
        }
    }

    @Override
    public List<Subtask> getSubtasksFromEpic(int epicId) {
        List<Subtask> result = new ArrayList<>();

        if (epics.containsKey(epicId)) {
            for (Integer subtaskId : epics.get(epicId).getSubtasksId()) {
                result.add(subtasks.get(subtaskId));
            }
        }
        return result;
    }
}