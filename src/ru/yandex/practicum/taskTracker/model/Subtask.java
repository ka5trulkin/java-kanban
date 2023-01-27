package ru.yandex.practicum.taskTracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String taskName,
                   String description,
                   LocalDateTime startTime,
                   Duration duration,
                   int id,
                   int epicId) {
        super(taskName, description,  startTime, duration, id);
        this.epicId = epicId;
    }

    public Subtask(String taskName,
                   String description,
                   LocalDateTime startTime,
                   Duration duration,
                   int id,
                   Status status,
                   int epicId) {
        super(taskName, description, startTime, duration, id, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public Integer getParentEpicID() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                "} " + super.toString();
    }
}