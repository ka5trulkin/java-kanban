package taskTracker.model;

import java.util.ArrayList;

public class Epic extends Task{
    private String nameEpicTask;
    private int counter;
    private ArrayList<SubTask> subTasks = new ArrayList<>();


    public String getNameEpicTask() {
        return nameEpicTask;
    }

    public void setNameEpicTask(String nameEpicTask) {
        this.nameEpicTask = nameEpicTask;
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public Epic(String nameEpicTask, String task) {
        super(task);
        this.nameEpicTask = nameEpicTask;
        setId(super.getIdValue(Hash.EPIC.hashCode()));
        counter = subTasks.size();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", counter=" + counter +
                '}';
    }
}
