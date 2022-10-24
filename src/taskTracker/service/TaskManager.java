package taskTracker.service;

import taskTracker.model.Task;

import java.util.ArrayList;

public class TaskManager {

    public ArrayList<String> getTaskList(ArrayList<Task> object) { // Получение списка всех задач
        ArrayList<String> result = new ArrayList<>();
        for (Task task : object) {
            if (task != null) {
                result.add(task.getTaskName());
            }
        }
        if (result.size() == 0) {
            result.add("Список задач пуст");
        }
        return result;
    }
    
    public void deleteTaskList(ArrayList<Task> object) { // Удаление всех задач
        System.out.println("Все задачи удалены");
        object.clear();
    }

    public Task getTaskById(ArrayList<Task> object, int id) { // Получение по идентификатору
        Task result = null;
        for (Task task : object) {
            if (task.getId() == id) {
                result = task;
                System.out.println("ID " + id + ": " + result.getTaskName());
            }
        }
        return result;
    }

    public void setStandardTask(ArrayList<Task> object, Task task) { // Создание. Сам объект должен передаваться в качестве параметра
        if (task == null) {
            System.out.println("Введено пустое значение");
        } else {
            boolean isSameTask = false;
            for (Task value : object) {
                if ((value.getId() == task.getId()) && (value.isDone() == task.isDone())) {
                    isSameTask = true;
                    System.out.println("Такая задача уже открыта");
                }
            }
            if (!(isSameTask)) {
                System.out.println("Задача добавлена");
                object.add(task);
            }
        }
    }


}
