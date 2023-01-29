package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File backupFile;
    private final String lineSeparator = "\r?\n";
    private final String infoLine = "id,type,name,status,description,startTime,endTime,epic";

    public FileBackedTasksManager(File backupFile) {
        this.backupFile = backupFile;
    }

    private void save() {
        if (backupFile.isFile()) {
            try (BufferedWriter bufferedWriter
                         = new BufferedWriter(new FileWriter(backupFile.toString(), StandardCharsets.UTF_8))) {
                bufferedWriter.write(tasksToString());
                bufferedWriter.write(historyToString(this.historyManager));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка записи в файл");
            }
        }
    }

    private String tasksToString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(infoLine);
        stringBuilder.append(System.lineSeparator());
        tasks.values().forEach(task -> stringBuilder.append(taskToString(task)));
        epics.values().forEach(epic -> stringBuilder.append(taskToString(epic)));
        subtasks.values().forEach(subtask -> stringBuilder.append(taskToString(subtask)));
        stringBuilder.append(System.lineSeparator());
        return stringBuilder.toString();
    }

    private String taskToString(Task task) {
        return task.getId()
                + "," + task.getType()
                + "," + task.getTaskName()
                + "," + task.getStatus()
                + "," + task.getDescription()
                + "," + task.getStartTime()
                + "," + task.getEndTime()
                + "," + task.getParentEpicID()
                + System.lineSeparator();
    }

    private Task taskFromString(String stringLine) {
        Task result;
        String[] dataFromStringLine = stringLine.split(",");
        int id = Integer.parseInt(dataFromStringLine[0]);
        Type type = Type.valueOf(dataFromStringLine[1]);
        String name = dataFromStringLine[2];
        Status status = Status.valueOf(dataFromStringLine[3]);
        String description = dataFromStringLine[4];
        LocalDateTime startTime = LocalDateTime.parse(dataFromStringLine[5]);
        Duration duration = Duration.between(startTime, LocalDateTime.parse(dataFromStringLine[6]));

        switch (type) {
            case TASK:
                result = new Task(name, description, startTime, duration, id, status);
                return result;
            case EPIC:
                result = new Epic(name, description, id, status);
                return result;
            case SUBTASK:
                int epicId = Integer.parseInt(dataFromStringLine[7]);
                result = new Subtask(name, description, startTime, duration, id, status, epicId);
                return result;
            default:
                throw new IllegalStateException("Неизвестный тип задачи: " + type);
        }
    }

    private void fillHistoryManager(List<Integer> history) {
        for (Integer id : history) {
            this.getTaskById(id);
            this.getEpicById(id);
            this.getSubTaskById(id);
        }
    }

    private void fillTasksManager(String fileLine) {
        switch (taskFromString(fileLine).getType()) {
            case TASK:
                fillTasks(fileLine);
                break;
            case EPIC:
                fillEpics(fileLine);
                break;
            case SUBTASK:
                fillSubtasks(fileLine);
                break;
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

    private static List<Integer> historyFromString(String fileLine) {
        if (fileLine.isBlank()) {
            return Collections.emptyList();
        }
        List<Integer> list = new ArrayList<>();

        for (String id : fileLine.split(",")) {
            list.add(Integer.parseInt(id));
        }
        return list;
    }

    private static String historyToString(HistoryManager manager) {
        if (!manager.getHistory().isEmpty()) {
            StringBuilder historyBuilder = new StringBuilder();
            List<Task> history = manager.getHistory();

            historyBuilder.append(history.get(0).getId());
            for (int index = 1; index < history.size(); index++) {
                historyBuilder.append(",");
                historyBuilder.append(history.get(index).getId());
            }
            return historyBuilder.toString();
        } else return "";
    }

    public static FileBackedTasksManager loadFromFile(File backupFile) {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(backupFile);
        String[] fileLines;
        String fileLine;
        String historyLine;
        int dataLine = 1;

        try {
            fileLines = Files.readString(backupFile.toPath()).split(tasksManager.lineSeparator);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения " + backupFile);
        }
        for (int index = dataLine; index < fileLines.length; index++) {
            fileLine = fileLines[index];
            historyLine = fileLines[index + dataLine];
            if (!fileLine.isBlank()) {
                tasksManager.fillTasksManager(fileLine);
            } else if (!historyLine.isBlank()) {
                tasksManager.fillHistoryManager(historyFromString(historyLine));
                break;
            }
        }
        return tasksManager;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> result = super.getTasks();
        save();
        return result;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> result = super.getEpics();
        save();
        return result;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> result = super.getSubtasks();
        save();
        return result;
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task result = super.getTaskById(taskId);
        save();
        return result;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic result = super.getEpicById(epicId);
        save();
        return result;
    }

    @Override
    public Subtask getSubTaskById(int subtaskId) {
        Subtask result = super.getSubTaskById(subtaskId);
        save();
        return result;
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }
}