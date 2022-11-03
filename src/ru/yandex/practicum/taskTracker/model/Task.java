package ru.yandex.practicum.taskTracker.model;

import ru.yandex.practicum.taskTracker.service.InMemoryTaskManager;

import java.util.Objects;

public class Task {
    private String taskName;
    private String description;
    private Status status;
    private final int id;

    public Task(String taskName, String description) {
        this.taskName = taskName;
        this.description = description;
        this.id = InMemoryTaskManager.setId();
        this.status = Status.NEW;
    }

    public Task(String taskName, String description, Status status) {
        this.taskName = taskName;
        this.description = description;
        this.id = InMemoryTaskManager.setId();
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
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
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}