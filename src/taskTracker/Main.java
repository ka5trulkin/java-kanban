package taskTracker;

import taskTracker.model.Task;
import taskTracker.service.TaskManager;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        ArrayList<Task> taskManager = new ArrayList<>();
        Task task1 = new Task("Поесть");
        Task task2 = new Task("Поспать");
        Task task3 = new Task("Поспать");
//        Epic epic1 = new Epic("Эпик1", "Проверка методов Эпической задачи 1");
//        Epic epic2 = new Epic("Эпик1", "Проверка методов Эпической задачи 2");
//        SubTask subTask1 = new SubTask("Эпик1", "Проверка методов Эпической задачи 1");
//        SubTask subTask2 = new SubTask("Эпик1", "Проверка методов Эпической задачи 1");

        manager.setStandardTask(taskManager, task1);
        taskManager.get(0).setDone();
        manager.setStandardTask(taskManager, task2);
        manager.setStandardTask(taskManager, task3);
        for (int index = 0; index < taskManager.size(); index++) {
            System.out.println("Задача № " + (index + 1) + " "
                    + taskManager.get(index).getTaskName() + " " + taskManager.get(index).toString());
        }
        System.out.println("Размер массива: " + taskManager.size());
        System.out.println(manager.getTaskList(taskManager));

        manager.getTaskById(taskManager, 117946027);

        manager.deleteTaskList(taskManager);
        System.out.println("Размер массива: " + taskManager.size());
        System.out.println(manager.getTaskList(taskManager));



//        taskManager.getTask().changeStatus(Status.IN_PROGRESS);
//        System.out.println(taskManager.getTask().get(0));

//        taskManager.getTask().changeStatus(Status.DONE);
//        System.out.println(taskManager.getTask().);


//        System.out.println("epic1 размер листа: " + epic1.getSubTasks().size() + System.lineSeparator() +
//                "epic1 счетчик: " +  epic1.getCounter() + System.lineSeparator() +
//                epic1.getTask() + System.lineSeparator());
//
//        System.out.println("toString task1: " + task1.toString() + System.lineSeparator() +
//                "toString task2: " +  task2.toString() + System.lineSeparator() +
//                "toString epic1: " +  epic1.toString() + System.lineSeparator() +
//                "toString epic2: " +  epic2.toString() + System.lineSeparator() +
//                "toString subTask1: " +  subTask1.toString() + System.lineSeparator() +
//                "toString subTask2: " +  subTask2.toString() + System.lineSeparator());
//
//
//        System.out.println("Статус task1: " + task1.getStatus() + System.lineSeparator() +
//                "Статус task2: " +  task2.getStatus() + System.lineSeparator() +
//                "Статус epic1: " +  epic1.getStatus() + System.lineSeparator() +
//                "Статус epic2: " +  epic2.getStatus() + System.lineSeparator() +
//                "Статус subTask1: " +  subTask1.getStatus() + System.lineSeparator() +
//                "Статус subTask2: " +  subTask2.getStatus() + System.lineSeparator());
//
//        task1.changeStatus(Status.IN_PROGRESS);
//        task2.changeStatus(Status.DONE);
//        epic2.changeStatus(Status.DONE);
//        subTask1.changeStatus(Status.IN_PROGRESS);
//
//        System.out.println("Статус task1: " + task1.getStatus() + System.lineSeparator() +
//                "Статус task2: " +  task2.getStatus() + System.lineSeparator() +
//                "Статус epic1: " +  epic1.getStatus() + System.lineSeparator() +
//                "Статус epic2: " +  epic2.getStatus() + System.lineSeparator() +
//                "Статус subTask1: " +  subTask1.getStatus() + System.lineSeparator() +
//                "Статус subTask2: " +  subTask2.getStatus() + System.lineSeparator());
//
//        System.out.println("isDone task1: " + task1.isDone() + System.lineSeparator() +
//                "isDone task2: " +  task2.isDone() + System.lineSeparator() +
//                "isDone epic1: " +  epic1.isDone() + System.lineSeparator() +
//                "isDone epic2: " +  epic2.isDone() + System.lineSeparator() +
//                "isDone subTask1: " +  subTask1.isDone() + System.lineSeparator() +
//                "isDone subTask2: " +  subTask2.isDone() + System.lineSeparator());
//
//        System.out.println("ID task1: " + task1.getId() + System.lineSeparator() +
//                "ID task2: " + task2.getId() + System.lineSeparator() +
//                "ID epic1: " + epic1.getId() + System.lineSeparator() +
//                "ID epic2: " + epic2.getId() + System.lineSeparator() +
//                "ID subTask1: " + subTask1.getId() + System.lineSeparator() +
//                "ID subTask2: " + subTask2.getId() + System.lineSeparator());

    }
}
