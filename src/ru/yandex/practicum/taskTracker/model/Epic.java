package ru.yandex.practicum.taskTracker.model;

import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task{
    private final HashMap<Integer, Status> epicSubtaskInfo = new HashMap<>();

    public Epic(String taskName, String description, int id) {
        super(taskName, description, id);
    }

    public HashMap<Integer, Status> getEpicSubtaskInfo() {
        return epicSubtaskInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(epicSubtaskInfo, epic.epicSubtaskInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicSubtaskInfo);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "epicSubtaskInfo=" + epicSubtaskInfo +
                "} " + super.toString();
    }
}