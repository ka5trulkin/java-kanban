package ru.yandex.practicum.taskTracker.model;

import java.util.Objects;

public class Task {
    private String taskName;
    private String description;
    private Status status = Status.NEW;
    private boolean isDone = false;
    private int id;

    public Task(String taskName, String description, int id) {
        this.taskName = taskName;
        this.description = description;
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        if (taskName != null) {
            this.taskName = taskName;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null) {
            this.description = description;
        }
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status != null) {
            this.status = status;
            setDone(status == Status.DONE && !(isDone));
        }
    }

    public boolean isDone() {
        return isDone;
    }

    private void setDone(boolean done) {
        isDone = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return isDone == task.isDone
                && id == task.id
                && Objects.equals(taskName, task.taskName)
                && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, description, status, isDone, id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", isDone=" + isDone +
                ", id=" + id +
                '}';
    }
}