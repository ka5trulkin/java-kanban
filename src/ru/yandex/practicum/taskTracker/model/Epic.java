package ru.yandex.practicum.taskTracker.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task{
    private final List<Integer> subtasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String taskName,
                String description,
                int id) {
        super(taskName,description,id);
        this.endTime = null;
    }

    public Epic(String taskName,
                String description,
                int id,
                Status status) {
        this(taskName,description,id);
        this.setStatus(status);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
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
        subtasksId.remove(Integer.valueOf(subtaskId));
    }

    public void clearSubtasks() {
        subtasksId.clear();
        setStatus(Status.NEW);
        setStartTime(null);
        setEndTime(null);
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