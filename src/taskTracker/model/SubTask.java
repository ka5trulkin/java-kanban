package taskTracker.model;

public class SubTask extends Epic {

    public SubTask(String nameEpicTask, String taskName) {
        super(nameEpicTask, taskName);
        setId(super.getIdValue(Hash.SUB_TASK.hashCode()));
    }
}
