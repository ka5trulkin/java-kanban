package ru.yandex.practicum.taskTracker;

import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.service.Managers;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        taskManager.removeTaskById(10);

        printInfo(taskManager);

        taskManager.getTasks();
        taskManager.getSubtasks();
        taskManager.getEpics();

        Task task1 = new Task("Просто задача", "Просто Мария создала просто задачу");
        taskManager.addNewTask(task1);
        Epic epic1 = new Epic("Исправить код", "Исправить все ошибки, выявленные при ревью");
        taskManager.addNewEpic(epic1);
        printInfo(taskManager);

        Subtask subtask1 = new Subtask("Исправить класс Task", "Тут могла быть ваша реклама!"
                ,2);
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Исправить класс Epic", "Сегодня действуют скидки на рекламу!"
                , 2);
        taskManager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("Исправить класс Subtask",
                "Миллиарды уже купили нашу рекламу, почему не Вы?", 2);
        taskManager.addNewSubtask(subtask3);
        System.out.println(taskManager.toString() + System.lineSeparator());

        Epic epic2 = new Epic("Проверить второй эпик", "Проверить работоспособность второго эпика");
        taskManager.addNewEpic(epic2);
        printInfo(taskManager);

        Subtask subtask4 = new Subtask("Проверить подзадачу № 1", "Описание подзадачи № 1", 6);
        taskManager.addNewSubtask(subtask4);
        Subtask subtask5 = new Subtask("Проверить подзадачу № 2", "Описание подзадачи № 2", 6);
        taskManager.addNewSubtask(subtask5);
        Subtask subtask6 = new Subtask("Проверить подзадачу № 3", "Описание подзадачи № 3", 6);
        taskManager.addNewSubtask(subtask6);
        printInfo(taskManager);

        taskManager.getSubTaskById(3).setStatus(Status.IN_PROGRESS);
        printInfo(taskManager);
        taskManager.getSubTaskById(3).setStatus(Status.DONE);
        printInfo(taskManager);

        taskManager.getSubTaskById(3).setStatus(Status.IN_PROGRESS);
        taskManager.getSubTaskById(5).setStatus(Status.DONE);
        printInfo(taskManager);

        taskManager.getSubTaskById(4).setStatus(Status.DONE);
        taskManager.updateEpic(epic1);
        printInfo(taskManager);

//       taskManager.getSubTaskById(3).setStatus(Status.IN_PROGRESS);
//       taskManager.updateEpic(epic1);
//       printInfo(taskManager);

       taskManager.removeSubtaskById(3);
       printInfo(taskManager);

       Subtask subtaskTest = taskManager.getSubTaskById(4);
       subtaskTest.setTaskName("Проверка метода Set()");
       taskManager.updateSubtask(subtaskTest);
       printInfo(taskManager);

       taskManager.removeSubtaskById(4);
       printInfo(taskManager);

       taskManager.getSubTaskById(9);
       taskManager.getSubTaskById(7);
       taskManager.getSubTaskById(8);
       taskManager.getEpicById(6);
       taskManager.getEpicById(2);
       taskManager.getTaskById(1);
       taskManager.getSubTaskById(9);
       taskManager.getSubTaskById(7);
       taskManager.getSubTaskById(8);
       taskManager.getEpicById(6);
       taskManager.getEpicById(2);
       taskManager.getTaskById(1);
       taskManager.getSubTaskById(9);
       taskManager.getSubTaskById(7);
       taskManager.getSubTaskById(8);
       taskManager.getEpicById(6);
       taskManager.getEpicById(2);

        System.out.println(taskManager.getHistoryFromManager().size() + System.lineSeparator()
                + taskManager.getHistoryFromManager().toString());
        printInfo(taskManager);
    }

    static void printInfo(TaskManager taskManager) {
        System.out.println(taskManager.getTasks() + System.lineSeparator()
                + taskManager.getEpics() + System.lineSeparator()
                + taskManager.getSubtasks() + System.lineSeparator());
    }
}