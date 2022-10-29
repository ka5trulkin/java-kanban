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
        System.out.println(taskManager.getTasks() + System.lineSeparator()
                + taskManager.getEpics() + System.lineSeparator()
                + taskManager.getSubtasks() + System.lineSeparator());

        Task task1 = new Task("Просто задача", "Просто Мария создала просто задачу",
                taskManager.setId());
        taskManager.addNewTask(task1);
        Epic epic1 = new Epic("Исправить код", "Исправить все ошибки, выявленные при ревью",
                taskManager.setId());
        taskManager.addNewEpic(epic1);
        System.out.println(taskManager.getEpics().toString() + System.lineSeparator());

        Subtask subtask1 = new Subtask("Исправить класс Task", "Тут могла быть ваша реклама!",
                taskManager.setId());
        taskManager.addNewSubtask(subtask1, 2);
        Subtask subtask2 = new Subtask("Исправить класс Epic", "Сегодня действуют скидки на рекламу!",
                taskManager.setId());
        taskManager.addNewSubtask(subtask2, 2);
        Subtask subtask3 = new Subtask("Исправить класс Subtask",
                "Миллиарды уже купили нашу рекламу, почему не Вы?", taskManager.setId());
        taskManager.addNewSubtask(subtask3, 2);
        System.out.println(taskManager.toString() + System.lineSeparator());

        Epic epic2 = new Epic("Проверить второй эпик", "Проверить работоспособность второго эпика",
                taskManager.setId());
        taskManager.addNewEpic(epic2);
        System.out.println(taskManager.getEpics());
        Subtask subtask4 = new Subtask("Проверить подзадачу № 1", "Описание подзадачи № 1",
                taskManager.setId());
        taskManager.addNewSubtask(subtask4, 6);
        Subtask subtask5 = new Subtask("Проверить подзадачу № 2", "Описание подзадачи № 2",
                taskManager.setId());
        taskManager.addNewSubtask(subtask5, 6);
        Subtask subtask6 = new Subtask("Проверить подзадачу № 3", "Описание подзадачи № 3",
                taskManager.setId());
        taskManager.addNewSubtask(subtask6, 6);
        System.out.println(taskManager.toString() + System.lineSeparator());

        taskManager.getSubTaskById(3).setStatus(Status.DONE);
        System.out.println(taskManager.getSubTaskById(3).toString());
        System.out.println(taskManager.getEpicById(2).toString() + System.lineSeparator());

        Subtask subtask23 = new Subtask("Проверить обнову", "Рекламы не будет!", 3);
        subtask23.setIdEpic(2);
        taskManager.updateSubtasks(subtask23);
        System.out.println(taskManager.getEpicById(2).toString());
        System.out.println(taskManager.getSubTaskById(3).toString());
        System.out.println(taskManager.getEpicById(2).toString() + System.lineSeparator());

        epic1.getEpicSubtaskInfo().put(3,Status.IN_PROGRESS);
        epic1.getEpicSubtaskInfo().put(4,Status.DONE);
        epic1.getEpicSubtaskInfo().put(5,Status.DONE);
        taskManager.updateEpics(epic1);
        System.out.println(taskManager.getSubTaskById(3).toString());
        System.out.println(taskManager.getSubTaskById(4).toString());
        System.out.println(taskManager.getSubTaskById(5).toString());
        System.out.println(taskManager.getEpicById(2).toString() + System.lineSeparator());

        System.out.println(taskManager.getSubtasks().toString());
        System.out.println(taskManager.getEpicById(2).toString() + System.lineSeparator());

        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getSubtasksFromEpic(2));
        taskManager.getSubTaskById(3).setStatus(Status.IN_PROGRESS);
        taskManager.updateEpics(epic1);
        System.out.println(taskManager.getEpicById(2).toString() + System.lineSeparator());

        taskManager.clearAllTasks();
        System.out.println(taskManager.getEpics().toString());
    }
}