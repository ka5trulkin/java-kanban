package ru.yandex.practicum.taskTracker.model;

import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task{
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(String taskName, String description, int id) {
        super(taskName, description, id);
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void putSubtask(int idSubtask, Subtask subtask) {
        subtasks.put(idSubtask, subtask);
    }

    public void deleteSubtask(int idSubtask) {
        subtasks.remove(idSubtask);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "epicSubtaskInfo=" + subtasks +
                "} " + super.toString();
    }
}