package taskTracker.model;

public enum Status {
    NEW("Задача открыта"),
    IN_PROGRESS("Задача выполняется"),
    DONE("Задача выполнена");

    final String name;

    Status(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
