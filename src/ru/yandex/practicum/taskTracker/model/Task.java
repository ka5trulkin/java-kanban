package ru.yandex.practicum.taskTracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String taskName;
    private String description;
    private LocalDateTime startTime;
    private Duration duration;
    private final int id;
    private Status status;

    protected Task(String taskName, String description, int id) {
        this.taskName = taskName;
        this.description = description;
        this.id = id;
        this.status = Status.NEW;
    }

    public Task(String taskName,
                String description,
                LocalDateTime startTime,
                Duration duration,
                int id) {
        this(taskName,description,id);
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String taskName,
                String description,
                LocalDateTime startTime,
                Duration duration,
                int id,
                Status status) {
        this(taskName, description, startTime, duration, id);
        this.status = status;
    }

    public Type getType() {
        return Type.TASK;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Integer getParentEpicID() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id
                && Objects.equals(taskName, task.taskName)
                && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, description, id, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status.getName() +
                '}';
    }
}