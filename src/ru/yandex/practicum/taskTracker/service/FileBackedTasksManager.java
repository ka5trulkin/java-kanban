package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
        Subtask subtask = new Subtask("1", "2", 1);
        fileBackedTasksManager.subtasks.put(subtask.getId(), subtask);
        System.out.println(fileBackedTasksManager.getSubtasks());
        System.out.println(fileBackedTasksManager.toString(fileBackedTasksManager.getSubTaskById(1)));
//        fileBackedTasksManager.toString(fileBackedTasksManager.getTaskById())
    }

    String toString(Task task) {
        StringBuilder stringBuilder = new StringBuilder(task.getId()
                + "," + task.getType() + "," + task.getTaskName() + "," + task.getStatus() + task.getDescription());
        if (task.getType() == Type.SUBTASK) {
            stringBuilder.append(",").append(task.getEpicId());
        }
        return stringBuilder.toString();
    }
}