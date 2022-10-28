package ru.yandex.practicum.taskTracker.model;

import java.util.Objects;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String taskName, String description, int id) {
        super(taskName, description, id);
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return idEpic == subtask.idEpic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idEpic);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "idEpic=" + idEpic +
                "} " + super.toString();
    }
}