package taskTracker.model;

import java.util.ArrayList;

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

    public void setCounterSubtask() {
        counterSubtask++;
    }

    public ArrayList<SubTask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<SubTask> subtasks) {
        this.subtasks = subtasks;
    }

    public Epic(String nameSubtask, String epicName) {
        super(nameSubtask);
        this.epicName = epicName;
        setId(super.getIdValue(Hash.EPIC.hashCode()));
        subtasks.add(new SubTask(nameSubtask));
        counterSubtask = subtasks.size();
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
