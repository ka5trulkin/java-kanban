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

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isDone() {

        return isDone;
    }

    public void setDone() {
        isDone = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task1 = (Task) o;
        return id == task1.id
                && isDone == task1.isDone
                && Objects.equals(taskName, task1.taskName)
                && status == task1.status;
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
