package ru.yandex.practicum.taskTracker.model;

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
    public String toString() {
        return "Subtask{" +
                "idEpic=" + idEpic +
                "} " + super.toString();
    }
}