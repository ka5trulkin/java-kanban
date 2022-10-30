package ru.yandex.practicum.taskTracker;

import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.service.TaskManager;

public class Main {

    // Доброго дня! Надеюсь в этот раз справился получше...

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        printInfo(taskManager);

        taskManager.getTasks();
        taskManager.getSubtasks();
        taskManager.getEpics();

        Task task1 = new Task("Просто задача", "Просто Мария создала просто задачу",
                taskManager.setId());
        taskManager.addNewTask(task1);
        Epic epic1 = new Epic("Исправить код", "Исправить все ошибки, выявленные при ревью",
                taskManager.setId());
        taskManager.addNewEpic(epic1);
        printInfo(taskManager);

        Subtask subtask1 = new Subtask("Исправить класс Task", "Тут могла быть ваша реклама!",
                taskManager.setId(),2);
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Исправить класс Epic", "Сегодня действуют скидки на рекламу!",
                taskManager.setId(), 2);
        taskManager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("Исправить класс Subtask",
                "Миллиарды уже купили нашу рекламу, почему не Вы?", taskManager.setId(), 2);
        taskManager.addNewSubtask(subtask3);
        System.out.println(taskManager.toString() + System.lineSeparator());

        Epic epic2 = new Epic("Проверить второй эпик", "Проверить работоспособность второго эпика",
                taskManager.setId());
        taskManager.addNewEpic(epic2);
        printInfo(taskManager);

        Subtask subtask4 = new Subtask("Проверить подзадачу № 1", "Описание подзадачи № 1",
                taskManager.setId(), 6);
        taskManager.addNewSubtask(subtask4);
        Subtask subtask5 = new Subtask("Проверить подзадачу № 2", "Описание подзадачи № 2",
                taskManager.setId(), 6);
        taskManager.addNewSubtask(subtask5);
        Subtask subtask6 = new Subtask("Проверить подзадачу № 3", "Описание подзадачи № 3",
                taskManager.setId(), 6);
        taskManager.addNewSubtask(subtask6);
       printInfo(taskManager);

       taskManager.getSubTaskById(3).setStatus(Status.IN_PROGRESS);
       taskManager.getSubTaskById(5).setStatus(Status.DONE);
       printInfo(taskManager);

        System.out.println("Все подзадачи" + taskManager.getSubtasks().toString() + System.lineSeparator());

        System.out.println(taskManager.getTaskById(1));
        Task task2 = new Task("Задача 2", "Проверить обновление задачи", 1);
        taskManager.updateTasks(task2);
        System.out.println(taskManager.getTaskById(1) + System.lineSeparator());

        taskManager.clearAllEpics();
        printInfo(taskManager);
    }

    static void printInfo(TaskManager taskManager) {
        System.out.println(taskManager.getTasks() + System.lineSeparator()
                + taskManager.getEpics() + System.lineSeparator()
                + taskManager.getSubtasks() + System.lineSeparator());
    }
}