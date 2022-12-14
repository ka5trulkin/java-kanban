package ru.yandex.practicum.taskTracker;

import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.service.FileBackedTasksManager;
import ru.yandex.practicum.taskTracker.service.Managers;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File ("resource/backup-task-manager.csv");
        TaskManager taskManager = Managers.getDefault();
        System.out.println("Пуста ли история просмотров? = " + taskManager.getHistoryFromManager().isEmpty());

        Task task1 = new Task(
                "Просто задача", "Просто Мария создала просто задачу", taskManager.setId());
        taskManager.addNewTask(task1);
        Epic epic1 = new Epic(
                "Исправить код", "Исправить все ошибки выявленные при ревью", taskManager.setId());
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(
                "Исправить класс Task", "Тут могла быть ваша реклама!", taskManager.setId()
                , 2);
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(
                "Исправить класс Epic", "Сегодня действуют скидки на рекламу!", taskManager.setId()
                , 2);
        taskManager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("Исправить класс Subtask",
                "Миллиарды уже купили нашу рекламу почему не Вы?", taskManager.setId(), 2);
        taskManager.addNewSubtask(subtask3);
        Epic epic2 = new Epic("Проверить второй эпик",
                "Проверить работоспособность второго эпика", taskManager.setId());
        taskManager.addNewEpic(epic2);
        Subtask subtask4 = new Subtask("Проверить подзадачу № 1",
                "Описание подзадачи № 1", taskManager.setId(), 6);
        taskManager.addNewSubtask(subtask4);
        Subtask subtask5 = new Subtask("Проверить подзадачу № 2",
                "Описание подзадачи № 2", taskManager.setId(), 6);
        taskManager.addNewSubtask(subtask5);
        Subtask subtask6 = new Subtask("Проверить подзадачу № 3",
                "Описание подзадачи № 3", taskManager.setId(), 6);
        taskManager.getSubTaskById(9);
        taskManager.getSubTaskById(8);
        taskManager.getSubTaskById(7);
        taskManager.getEpicById(6);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(3);
        taskManager.getEpicById(2);
        taskManager.getTaskById(1);
        System.out.println(System.lineSeparator()
                + "Вывод данных после заполнения задач:" + System.lineSeparator()
                + "ID задач из истории просмотров:");
        for (Task task : taskManager.getHistoryFromManager()) {
            System.out.print(task.getId() + " ");
        }
        System.out.println();
        for (Task task : taskManager.getHistoryFromManager()) {
            System.out.println(task);
        }
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
        System.out.println(System.lineSeparator() + "ID задач после разных вызовов:");
        for (Task task : taskManager.getHistoryFromManager()) {
            System.out.print(task.getId() + " ");
        }
        System.out.println();
        TaskManager taskManager2 = new FileBackedTasksManager(file);

        System.out.println(System.lineSeparator() + "ID задач из истории просмотров нового объекта:");
        for (Task task : taskManager2.getHistoryFromManager()) {
            System.out.print(task.getId() + " ");
        }
        System.out.println();
        TaskManager taskManager3 = FileBackedTasksManager.loadFromFile(file);

        System.out.println("ID задач из истории просмотров восстановленного файла:");
        for (Task task : taskManager3.getHistoryFromManager()) {
            System.out.print(task.getId() + " ");
        }
        System.out.println(System.lineSeparator() + "Вывод данных о задачах восстановленного файла:");
        for (Task task : taskManager.getHistoryFromManager()) {
            System.out.println(task);
        }
    }
}