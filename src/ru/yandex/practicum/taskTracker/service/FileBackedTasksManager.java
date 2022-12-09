package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTasksManager extends InMemoryTaskManager{
    Path backupFile;

    public FileBackedTasksManager(String backupFile) {
        this.backupFile = Paths.get(backupFile);
    }

    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager
                = new FileBackedTasksManager("src/ru/yandex/practicum/taskTracker/files/backup-task-manager.csv");
        System.out.println(fileBackedTasksManager.backupFile.toString());
        Task task1 = new Task("Просто задача", "Просто Мария создала просто задачу");
        fileBackedTasksManager.addNewTask(task1);
        Epic epic1 = new Epic("Исправить код", "Исправить все ошибки выявленные при ревью");
        fileBackedTasksManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Исправить класс Task", "Тут могла быть ваша реклама!"
                , 2);
        fileBackedTasksManager.addNewSubtask(subtask1);
        System.out.println(fileBackedTasksManager.getSubtasks());
        System.out.println(fileBackedTasksManager.taskToString(fileBackedTasksManager.getSubTaskById(3)));
        System.out.println(fileBackedTasksManager.taskToString(fileBackedTasksManager.getTaskById(1)));
        System.out.println(fileBackedTasksManager.taskToString(fileBackedTasksManager.getEpicById(2)));

    }

    String taskToString(Task taskOrEpic) {
        return taskOrEpic.getId()
                + "," + taskOrEpic.getType()
                + "," + taskOrEpic.getTaskName()
                + "," + taskOrEpic.getStatus()
                + "," + taskOrEpic.getDescription();
    }

    String taskToString(Subtask subtask) {
        return this.taskToString((Task) subtask) + "," + subtask.getEpicId();
    }
}