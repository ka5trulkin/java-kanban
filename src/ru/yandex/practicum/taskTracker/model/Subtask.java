package ru.yandex.practicum.taskTracker.model;

public class Subtask extends Task {
    private final int idEpic;

    public Subtask(String taskName, String description, int idSubtask, int idEpic) {
        super(taskName, description, idSubtask);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "idEpic=" + idEpic +
                "} " + super.toString();
    }
}