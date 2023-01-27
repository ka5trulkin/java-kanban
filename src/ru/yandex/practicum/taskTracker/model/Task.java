package ru.yandex.practicum.taskTracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class Task {
    private String taskName;
    private String description;
    private final Duration duration;
    private final LocalDateTime startTime;
    private final int id;
    private Status status;

    public Task(String taskName,
                String description,
                Duration duration,
                LocalDateTime startTime,
                int id) {
        this.taskName = taskName;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.id = id;
        this.status = Status.NEW;
    }

    public Task(String taskName,
                String description,
                Duration duration,
                LocalDateTime startTime,
                int id,
                Status status) {
        this(taskName, description, duration, startTime, id);
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

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime endTime(LocalDateTime startTime, Duration duration) {
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
        return id == task.id && Objects.equals(taskName, task.taskName) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, description, status, id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status.getName() +
                ", id=" + id +
                '}';
    }
}