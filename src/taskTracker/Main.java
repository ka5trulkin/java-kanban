package taskTracker;

import taskTracker.model.Epic;
import taskTracker.model.Status;
import taskTracker.model.SubTask;
import taskTracker.model.Task;
import taskTracker.service.TaskManager;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        ArrayList<Task> taskManager = new ArrayList<>();
        ArrayList<Epic> epicManager = new ArrayList<>();

        Task task1 = new Task("Проснуться");
        Task task2 = new Task("Поесть");
        Task task3 = new Task("Поспать");
        Epic epic1 = new Epic("Проснуться", "Прожить сегодняшний день");
        SubTask subTask1 = new SubTask("Поесть");
        SubTask subTask2 = new SubTask("Поспасть");
        Epic epic2 = new Epic("Снова проснуться", "Прожить завтрашний день");
        SubTask subTask3 = new SubTask("Снова поесть");
        SubTask subTask4 = new SubTask("Снова поспасть");

        System.out.println(epic1.toString());
        System.out.println(epic1.getSubtasks().get(0).toString() + System.lineSeparator());

        manager.addEpic(epicManager, epic1);
        manager.addEpic(epicManager, epic2);
        System.out.println(epicManager.toString());
        manager.addSubtaskToEpic(epicManager,-2029203213 , subTask1);
        manager.addSubtaskToEpic(epicManager,-2029203213, subTask2);
        System.out.println(epicManager.toString());
        System.out.println(epic1.getSubtasks().toString() + System.lineSeparator());

        System.out.println("Список Эпиков с Подзадачами: " + manager.getEpicList(epicManager));
        manager.addSubtaskToEpic(epicManager,-1201755597, subTask3);
        manager.addSubtaskToEpic(epicManager,-1201755597, subTask4);
        System.out.println(epicManager.toString());
        System.out.println(manager.getEpicById(epicManager, -2029203213));
        manager.addSubtaskToEpic(epicManager, -2029203213, subTask1);
        manager.addSubtaskToEpic(epicManager, -2029203213, subTask4);
        System.out.println("Список Эпиков с Подзадачами: " + manager.getEpicList(epicManager) + System.lineSeparator());

        manager.deleteAllEpic(epicManager);
        System.out.println(epicManager.toString());
        manager.addSubtaskToEpic(epicManager,1201755597, subTask3);






//        manager.updateTask(taskManager, 117946027, task2);
//        manager.addTask(taskManager, task1);
//        taskManager.get(0).setDone();
//        manager.addTask(taskManager, task2);
//        manager.addTask(taskManager, task3);
//        for (int index = 0; index < taskManager.size(); index++) {
//            System.out.println("Задача № " + (index + 1) + " "
//                    + taskManager.get(index).getTaskName() + " " + taskManager.get(index).toString());
//        }
//        System.out.println("Размер массива: " + taskManager.size());
//        System.out.println(manager.getTaskList(taskManager));
//        manager.getTaskById(taskManager, 117946027);
//        task2.setStatus(Status.IN_PROGRESS);
//        manager.updateTask(taskManager, 117946027, task2);
//        manager.updateTask(taskManager, 0, task2);
//        System.out.println("Статус задачи: " + task2.getTaskName() + " - " + task2.getStatus());
//        manager.deleteTaskById(taskManager, 1855353638);
//
//        System.out.println("Размер массива: " + taskManager.size());
//        manager.deleteAllTasks(taskManager);
//        System.out.println("Размер массива: " + taskManager.size());
//        manager.updateTask(taskManager, 117946027, task2);
    }
}
