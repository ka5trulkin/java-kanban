package taskTracker.model;

import java.util.Objects;

public class Task {
    private int id;
    private String taskName;
    private Status status;
    private boolean isDone;

    public Task(String taskName) {
        this.taskName = taskName;
        status = Status.NEW;
        id = getIdValue(Hash.TASK.hashCode());
        isDone = false;
    }

    public void changeStatus(Status status) {
        this.setStatus(status);
        if (status.equals(Status.DONE)) {
            setDone();
        }
    }

    protected int getIdValue(int hashClass) {
        int hashCount = 31;
        int hash = hashClass;
        if (taskName != null) {
            hash += taskName.hashCode();
        }
        hash *= hashCount;
        return hash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Status getStatus() {
        return status;
    }

    private void setStatus(Status status) {
        if (status != null) {
            this.status = status;
        }
    }

    public void setStatusInProgress() {
        this.status = Status.IN_PROGRESS;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone() {
        isDone = true;
        status = Status.DONE;
        System.out.println(Status.DONE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && isDone == task.isDone && Objects.equals(taskName, task.taskName) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, status, isDone);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", status=" + status +
                ", isDone=" + isDone +
                '}';
    }
}
