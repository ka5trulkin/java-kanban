package taskTracker.model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task{
    private String epicName;
    private int counterSubtask;
    private ArrayList<SubTask> subtasks = new ArrayList<>();


    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public int getCounterSubtask() {
        return counterSubtask;
    }

    public void setCounterSubtaskUp(boolean isTrue) {
        if (isTrue) {
            counterSubtask++;
        } else {
            counterSubtask--;
        }
    }

    public ArrayList<SubTask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<SubTask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    protected int getIdValue(int hashClass) {
        int hashCount = 31;
        int hash = hashClass;
        if (epicName != null) {
            hash += epicName.hashCode();
        }
        hash *= hashCount;
        return hash;
    }

    public Epic(String nameSubtask, String epicName) {
        super(nameSubtask);
        this.epicName = epicName;
        setId(super.getIdValue(Hash.EPIC.hashCode()));
        subtasks.add(new SubTask(nameSubtask));
        counterSubtask = subtasks.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return counterSubtask == epic.counterSubtask && Objects.equals(epicName, epic.epicName) && Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicName, counterSubtask, subtasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "nameEpic='" + epicName + '\'' +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", isDone=" + isDone() +
                ", counterSubtask=" + counterSubtask +
                ", subtasks=" + subtasks.size() +
                '}';
    }
}
