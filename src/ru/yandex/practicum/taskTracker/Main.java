package ru.yandex.practicum.taskTracker;

import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.service.Managers;

public class Main {
    static TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) {
        createTasks();
        System.out.println(taskManager.getSubTaskById(4).toString());
        printInfo(1);
        printInfo(taskManager);
        taskManager.getSubTaskById(4);
        printInfo(2);
        printInfo(taskManager);
        taskManager.removeTaskById(1);
        taskManager.removeTaskById(1);
        taskManager.removeSubtaskById(4);
        taskManager.removeSubtaskById(4);
        printInfo(3);
        printInfo(taskManager);
        taskManager.removeEpicById(2);
        printInfo(4);
        printInfo(taskManager);
        taskManager.clearAllEpics();
        printInfo(5);
        printInfo(taskManager);
        printInfo(6);
        taskManager.getTasks();
    }

    static void createTasks() {
        Task task1 = new Task("Просто задача", "Просто Мария создала просто задачу");
        taskManager.addNewTask(task1);
        Epic epic1 = new Epic("Исправить код", "Исправить все ошибки, выявленные при ревью");
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Исправить класс Task", "Тут могла быть ваша реклама!"
                , 2);
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Исправить класс Epic", "Сегодня действуют скидки на рекламу!"
                , 2);
        taskManager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("Исправить класс Subtask",
                "Миллиарды уже купили нашу рекламу, почему не Вы?", 2);
        taskManager.addNewSubtask(subtask3);
        Epic epic2 = new Epic("Проверить второй эпик", "Проверить работоспособность второго эпика");
        taskManager.addNewEpic(epic2);
        Subtask subtask4 = new Subtask("Проверить подзадачу № 1", "Описание подзадачи № 1", 6);
        taskManager.addNewSubtask(subtask4);
        Subtask subtask5 = new Subtask("Проверить подзадачу № 2", "Описание подзадачи № 2", 6);
        taskManager.addNewSubtask(subtask5);
        Subtask subtask6 = new Subtask("Проверить подзадачу № 3", "Описание подзадачи № 3", 6);
        taskManager.addNewSubtask(subtask6);
        taskManager.getSubTaskById(3).setStatus(Status.IN_PROGRESS);
        taskManager.getSubTaskById(3).setStatus(Status.DONE);
        taskManager.getSubTaskById(3).setStatus(Status.IN_PROGRESS);
        taskManager.getSubTaskById(5).setStatus(Status.DONE);
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
    }

    static void printInfo(int count) {
        System.out.println(lineCount(count)
                + System.lineSeparator()
                + taskManager.getHistoryFromManager().size());
        for (Task task : taskManager.getHistoryFromManager()) {
            System.out.println(task);
        }

    }

    static String lineCount(int counter) {
        return "\uD83D\uDC47 " + counter + " \uD83D\uDC40";
    }

    static void printInfo(TaskManager taskManager) {
        System.out.println(System.lineSeparator()
                + taskManager.getTasks() + System.lineSeparator()
                + taskManager.getSubtasks() + System.lineSeparator()
                + taskManager.getEpics() + System.lineSeparator());
    }
}