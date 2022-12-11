package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileBackedTasksManager extends InMemoryTaskManager{
    Path backupFile;

    public FileBackedTasksManager(Path backupFile) {
        this.backupFile = backupFile;
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

    public Task taskFromString(String stringLine) {
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

    public static FileBackedTasksManager loadFromFile(Path backupFile) {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(backupFile);
        String[] fileLines;
        try {
            fileLines = Files.readString(backupFile).split("\r?\n");
        } catch (IOException e) {
            throw new ManagerReadException();
        }
        int nextLine = 1;

        if (fileLines.length > nextLine) {
            String fileLine;

            for (int index = nextLine; index < fileLines.length; index++) {
                fileLine = fileLines[index];
                if (!fileLine.isBlank()) {
                   tasksManager.fillTasksManager(fileLine);
                } else if (fileLine.isBlank() && !fileLines[index + nextLine].isBlank()) {
                    historyFromString(fileLines[index + nextLine]);
                    break;
                }
            }
        }
        return tasksManager;
    }

    static List<Integer> historyFromString(String fileLine) {
        List<Integer> list = new ArrayList<>();

        for (String id : fileLine.split(",")) {
            list.add(Integer.parseInt(id));
        }
        return list;
    }

    private void fillTasksManager(String fileLine) {
        if (getType(fileLine) == Type.TASK) {
            fillTasks(fileLine);
        } else if (getType(fileLine) == Type.EPIC) {
            fillEpics(fileLine);
        } else if (getType(fileLine) == Type.SUBTASK) {
            fillSubtasks(fileLine);
        }
    }

    private void fillTasks(String fileLine) {
        Task task = taskFromString(fileLine);
        int key = task.getId();
        this.tasks.put(key, task);
    }

    private void fillEpics(String fileLine) {
        Epic task = (Epic) taskFromString(fileLine);
        int key = task.getId();
        this.epics.put(key, task);
    }

    private void fillSubtasks(String fileLine) {
        Subtask task = (Subtask) taskFromString(fileLine);
        int key = task.getId();
        this.subtasks.put(key, task);
    }

    private Type getType(String backupFileLine) {
        int firstIndex = backupFileLine.indexOf(",") + 1;
        int secondIndex = backupFileLine.indexOf(",", firstIndex);
        String type = backupFileLine.substring(firstIndex, secondIndex);
        return Type.valueOf(type);
    }

    private String fileToString(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Path path = Paths.get("src/ru/yandex/practicum/taskTracker/files/backup-task-manager.csv");
        FileBackedTasksManager tasksManager1 = FileBackedTasksManager.loadFromFile(path);

        System.out.println(tasksManager1.getTasks().toString());
    }
}