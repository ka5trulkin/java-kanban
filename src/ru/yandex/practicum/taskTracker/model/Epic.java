package ru.yandex.practicum.taskTracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task{
    private final List<Integer> subtasksId = new ArrayList<>();

    public Epic(String taskName, String description) {
        super(taskName, description);
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubtask(int subtaskId) {
        if (!(subtasksId.contains(subtaskId))) {
            subtasksId.add(subtaskId);
        }
    }

    public void removeSubtask(int subtaskId) {
        if (subtasksId.remove(Integer.valueOf(subtaskId))) {
            return;
        }
    }

    public void setSubtask(int subtaskId) {
        if (subtasksId.contains(subtaskId)) {
            subtasksId.set(subtasksId.indexOf(subtaskId), subtaskId);
        }
    }

    public void clearSubtasks() {
        subtasksId.clear();
        setStatus(Status.NEW);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksId, epic.subtasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasksId=" + subtasksId +
                "} " + super.toString();
    }
}