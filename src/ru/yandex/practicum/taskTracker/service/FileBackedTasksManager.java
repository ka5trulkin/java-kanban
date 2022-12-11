package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager{
    Path backupFile;

    public static void main(String[] args) {
//        Path path = Paths.get("src/ru/yandex/practicum/taskTracker/files/backup-task-manager.csv");
//        FileBackedTasksManager tasksManager1 = FileBackedTasksManager.loadFromFile(path);

    }

    private void save() {
        if (!Files.isDirectory(backupFile)) {
            String infoLine = "id,type,name,status,description,epic";

            try (BufferedWriter bufferedWriter
                         = new BufferedWriter(new FileWriter(backupFile.toString(), StandardCharsets.UTF_8))) {

                bufferedWriter.write(infoLine + System.lineSeparator()
                        + tasksToString() + System.lineSeparator()
                        + historyToString(this.historyManager));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка записи в файл");
            }
        } else {
            throw new ManagerSaveException("Указанный файл является директорией");
        }
    }

    private String tasksToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : tasks.values()) {
            stringBuilder.append(taskToString(task)).append(System.lineSeparator());
        }
        for (Epic epic : epics.values()) {
            stringBuilder.append(taskToString(epic)).append(System.lineSeparator());
        }
        for (Subtask subtask : subtasks.values()) {
            stringBuilder.append(taskToString(subtask)).append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private String taskToString(Task task) {
        String result = task.getId()
                + "," + task.getType()
                + "," + task.getTaskName()
                + "," + task.getStatus()
                + "," + task.getDescription();
        if (task.getType() == Type.SUBTASK) {
            Subtask subtask = (Subtask) task;
            result += "," + subtask.getEpicId();
        }
        return result;
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

    private void fillHistoryManager(List<Integer> history) {
        for (Integer id : history) {
            this.getTaskById(id);
            this.getEpicById(id);
            this.getSubTaskById(id);
        }
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

    public static FileBackedTasksManager loadFromFile(Path backupFile) {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(backupFile);
        String[] fileLines;
        int infoLine = 1;

        try {
            fileLines = Files.readString(backupFile).split("\r?\n");
        } catch (IOException e) {
            throw new ManagerReadException("Ошибка чтения файла");
        }
        if (fileLines.length > infoLine) {
            String fileLine;

            for (int index = infoLine; index < fileLines.length; index++) {
                fileLine = fileLines[index];
                if (!fileLine.isBlank()) {
                    tasksManager.fillTasksManager(fileLine);
                } else if (fileLine.isBlank() && !fileLines[index + infoLine].isBlank()) {
                    tasksManager.fillHistoryManager(historyFromString(fileLines[index + infoLine]));
                    break;
                }
            }
        }
        return tasksManager;
    }

    private Type getType(String backupFileLine) {
        int firstIndex = backupFileLine.indexOf(",") + 1;
        int secondIndex = backupFileLine.indexOf(",", firstIndex);
        String type = backupFileLine.substring(firstIndex, secondIndex);
        return Type.valueOf(type);
    }

    public FileBackedTasksManager(Path backupFile) {
        this.backupFile = backupFile;
    }

    static List<Integer> historyFromString(String fileLine) {
        List<Integer> list = new ArrayList<>();

        for (String id : fileLine.split(",")) {
            list.add(Integer.parseInt(id));
        }
        return list;
    }

    static String historyToString(HistoryManager manager) {
        StringBuilder historyLine = new StringBuilder();
        for (Task task : manager.getHistory()) {
            historyLine.append(task.getId()).append(",");
        }
        if (historyLine.length() > 0){
            historyLine.deleteCharAt(historyLine.length() - 1);
        }
        return historyLine.toString();
    }
}