package ru.yandex.practicum.taskTracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String taskName;
    protected String description;
    protected LocalDateTime startTime;
    protected Duration duration;
    protected int id;
    protected Status status;

    public Task() {
    }

    public Task(
            String taskName,
            String description,
            int id) {
        this.taskName = taskName;
        this.description = description;
        this.startTime = null;
        this.duration = Duration.ZERO;
        this.id = id;
        this.status = Status.NEW;
    }

    public Task(
            String taskName,
            String description,
            LocalDateTime startTime,
            Duration duration,
            int id) {
        this(taskName, description, id);
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(
            String taskName,
            String description,
            int id,
            Status status) {
        this(taskName, description, id);
        this.status = status;
    }

    public Task(
            String taskName,
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
        if (startTime != null) {
            return LocalDateTime.of(
                    startTime.getYear(),
                    startTime.getMonth(),
                    startTime.getDayOfMonth(),
                    startTime.getHour(),
                    startTime.getMinute());
        } else return null;
    }

    public void setId(int id) {
        this.id = id;
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
        if (startTime == null || duration == null) {
            return null;
        }
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
                && Objects.equals(this.getStartTime(), task.getStartTime())
                && Objects.equals(duration, task.duration)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, description, this.getStartTime(), duration, id, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}