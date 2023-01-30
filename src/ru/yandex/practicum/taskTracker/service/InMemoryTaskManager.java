package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected static int idCounter = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasksByStartTime = new TreeSet<>(
            (o1, o2) -> {
                if (o1.getStartTime() != null && o2.getStartTime() != null) {
                    return o1.getStartTime().compareTo(o2.getStartTime());
                } else return 1;
            });

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
        setEpicEndTime(epic);
        setEpicDuration(epic);
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
        if ((epic.getStartTime() != null) || (epic.getEndTime() != null)) {
            epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()));
        }
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
                        "Задача '"
                        + task.getTaskName()
                        + "' пересекается с другой задачей по времени начала задачи: "
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
        tasks.values().forEach(prioritizedTasksByStartTime::remove);
        tasks.clear();
    }

    @Override
    public void clearAllEpics() {
        epics.keySet().forEach(historyManager :: remove);
        subtasks.keySet().forEach(historyManager :: remove);
        epics.values().forEach(prioritizedTasksByStartTime::remove);
        subtasks.values().forEach(prioritizedTasksByStartTime :: remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearAllSubtasks() {
        subtasks.keySet().forEach(historyManager :: remove);
        subtasks.values().forEach(prioritizedTasksByStartTime :: remove);
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
            prioritizedTasksByStartTime.add(epic);
            checkEpicStatus(epic);
            checkEpicTimes(epic);
        }
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        if (subtask != null) {
            int subtaskId = subtask.getId();
            Epic epic = epics.get(subtask.getEpicId());

            checkTaskToCrossByStartTime(subtask);
            subtasks.put(subtaskId, subtask);
            prioritizedTasksByStartTime.add(subtask);
            epic.addSubtask(subtaskId);
            checkEpicStatus(epic);
            checkEpicTimes(epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        if ((task != null) && (tasks.containsKey(task.getId()))) {
            int taskId = task.getId();

            checkTaskToCrossByStartTime(task);
            updatePrioritizedTasksByStartTime(tasks.get(taskId), task);
            tasks.put(taskId, task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if ((epic != null) && epics.containsKey(epic.getId())) {
            int epicId = epic.getId();

            checkTaskToCrossByStartTime(epic);
            updatePrioritizedTasksByStartTime(epics.get(epicId), epic);
            epics.put(epicId, epic);
            checkEpicStatus(epic);
            checkEpicTimes(epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if ((subtask != null) && (subtasks.containsKey(subtask.getId()))) {
            int subtaskId = subtask.getId();
            Epic epic = epics.get(subtask.getEpicId());

            checkTaskToCrossByStartTime(subtask);
            updatePrioritizedTasksByStartTime(subtasks.get(subtaskId), subtask);
            subtasks.put(subtaskId, subtask);
            epic.setSubtask(subtaskId);
            checkEpicStatus(epic);
            checkEpicTimes(epic);
        }
    }

    @Override
    public void removeTaskById(int taskId) {
        prioritizedTasksByStartTime.remove(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        if (epics.get(epicId) != null) {
            for (Integer subtaskId : epics.get(epicId).getSubtasksId()) {
                prioritizedTasksByStartTime.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            prioritizedTasksByStartTime.remove(epics.get(epicId));
            historyManager.remove(epicId);
            epics.remove(epicId);
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        if (subtasks.get(subtaskId) != null) {
            Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());

            prioritizedTasksByStartTime.remove(subtasks.get(subtaskId));
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