package ru.yandex.practicum.taskTracker.model;

import ru.yandex.practicum.taskTracker.service.Type;

import java.util.Objects;

public class Task {
    private String taskName;
    private String description;
    private Status status;
    private final int id;

    public Task(String taskName, String description, int id) {
        this.taskName = taskName;
        this.description = description;
        this.id = id;
        this.status = Status.NEW;
    }

    public Task(String taskName, String description, int id, Status status) {
        this(taskName, description, id);
        this.status = status;
    }

    public Type getType() {
        return Type.TASK;
    }

//    private static int setId() {
//        return ++idCounter;
//    }

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