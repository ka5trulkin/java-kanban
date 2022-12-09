package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileBackedTasksManager extends InMemoryTaskManager{
    Path backupFile;

    public FileBackedTasksManager(String backupFile) {
        this.backupFile = Paths.get(backupFile);
    }

    private String taskToString(Task taskOrEpic) {
        return taskOrEpic.getId()
                + "," + taskOrEpic.getType()
                + "," + taskOrEpic.getTaskName()
                + "," + taskOrEpic.getStatus()
                + "," + taskOrEpic.getDescription();
    }

    private String taskToString(Subtask subtask) {
        return this.taskToString((Task) subtask) + "," + subtask.getEpicId();
    }

    private Task taskFromString(String stringLine) {
        Task result = null;
        String[] dataFromStringLine = stringLine.split(",");
        int id = Integer.parseInt(dataFromStringLine[0]);
        Type type = Type.valueOf(dataFromStringLine[1]);
        String name = dataFromStringLine[2];
        Status status = Status.valueOf(dataFromStringLine[3]);
        String description = dataFromStringLine[4];

        if (type == Type.TASK) {
            result = new Task(name, description, id, status);
        } else if (type == Type.EPIC) {
            result = new Epic(name, description, id, status);
        } else if (type == Type.SUBTASK) {
            int epicId = Integer.parseInt(dataFromStringLine[5]);
            result = new Subtask(name, description, id, status, epicId);
        }
        return result;
    }

    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager
                = new FileBackedTasksManager("src/ru/yandex/practicum/taskTracker/files/backup-task-manager.csv");
        System.out.println(fileBackedTasksManager.backupFile.toString());
        Task task1 = new Task("Просто задача", "Просто Мария создала просто задачу", fileBackedTasksManager.setId());
        fileBackedTasksManager.addNewTask(task1);
        Epic epic1 = new Epic("Исправить код", "Исправить все ошибки выявленные при ревью", fileBackedTasksManager.setId());
        fileBackedTasksManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Исправить класс Task", "Тут могла быть ваша реклама!", fileBackedTasksManager.setId()
                , 2);
        fileBackedTasksManager.addNewSubtask(subtask1);
        System.out.println(fileBackedTasksManager.getSubtasks());
        System.out.println(fileBackedTasksManager.taskToString(fileBackedTasksManager.getSubTaskById(3)));
        System.out.println(fileBackedTasksManager.taskToString(fileBackedTasksManager.getTaskById(1)));
        System.out.println(fileBackedTasksManager.taskToString(fileBackedTasksManager.getEpicById(2)));
        System.out.println(subtask1.getType());
        Task task2 = fileBackedTasksManager.taskFromString("1,TASK,Task1,NEW,Description task1,");
        System.out.println(task2);
        Subtask subtask2 = (Subtask) fileBackedTasksManager.taskFromString("3,SUBTASK,Sub Task2,DONE,Description sub task3,2");
        System.out.println(subtask2);
        Epic epic2 = (Epic) fileBackedTasksManager.taskFromString("2,EPIC,Epic2,DONE,Description epic2,");
        System.out.println(epic2);
    }
}