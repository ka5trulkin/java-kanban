package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File backupFile;
    private final String lineSeparator = "\r?\n";

    public FileBackedTasksManager(File backupFile) {
        this.backupFile = backupFile;
    }

    private void save() {
        if (backupFile.isFile()) {
            try (BufferedWriter bufferedWriter
                         = new BufferedWriter(new FileWriter(backupFile.toString(), StandardCharsets.UTF_8))) {
                bufferedWriter.write(tasksToString()
                        + System.lineSeparator()
                        + historyToString(this.historyManager));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка записи в файл");
            }
        }
    }

    private String tasksToString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("id,type,name,status,description,epic")
                .append(System.lineSeparator());
        for (Task task : tasks.values()) {
            stringBuilder.append(taskToString(task))
                    .append(System.lineSeparator());
        }
        for (Epic epic : epics.values()) {
            stringBuilder.append(taskToString(epic))
                    .append(System.lineSeparator());
        }
        for (Subtask subtask : subtasks.values()) {
            stringBuilder.append(taskToString(subtask))
                    .append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private String taskToString(Task task) {
        return task.getId()
                + "," + task.getType()
                + "," + task.getTaskName()
                + "," + task.getStatus()
                + "," + task.getDescription()
                + "," + task.getParentEpicID();
    }

    private Task taskFromString(String stringLine) {
        Task result;
        String[] dataFromStringLine = stringLine.split(",");
        int id = Integer.parseInt(dataFromStringLine[0]);
        Type type = Type.valueOf(dataFromStringLine[1]);
        String name = dataFromStringLine[2];
        Status status = Status.valueOf(dataFromStringLine[3]);
        String description = dataFromStringLine[4];

        switch (type) {
            case TASK:
                result = new Task(name, description, id, status);
                return result;
            case EPIC:
                result = new Epic(name, description, id, status);
                return result;
            case SUBTASK:
                int epicId = Integer.parseInt(dataFromStringLine[5]);
                result = new Subtask(name, description, id, status, epicId);
                return result;
        }
        throw new RuntimeException();
    }

    private void fillHistoryManager(List<Integer> history) {
        for (Integer id : history) {
            this.getTaskById(id);
            this.getEpicById(id);
            this.getSubTaskById(id);
        }
    }

    private void fillTasksManager(String fileLine) {
        switch (getType(fileLine)) {
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

    private Type getType(String backupFileLine) {
        int firstIndex = backupFileLine.indexOf(",") + 1;
        int secondIndex = backupFileLine.indexOf(",", firstIndex);
        String type = backupFileLine.substring(firstIndex, secondIndex);
        return Type.valueOf(type);
    }

    public static FileBackedTasksManager loadFromFile(File backupFile) {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(backupFile);
        String[] fileLines;
        int infoLine = 1;

        try {
            fileLines = Files.readString(backupFile.toPath()).split(tasksManager.lineSeparator);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String fileLine;

        for (int index = infoLine; index < fileLines.length; index++) {
            fileLine = fileLines[index];
            if (!fileLine.isBlank()) {
                tasksManager.fillTasksManager(fileLine);
            } else if (!fileLines[index + infoLine].isBlank()) {
                tasksManager.fillHistoryManager(historyFromString(fileLines[index + infoLine]));
                break;
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

    static String historyToString(HistoryManager manager) {
        StringBuilder historyLine = new StringBuilder();
        for (Task task : manager.getHistory()) {
            historyLine.append(task.getId()).append(",");
        }
        if (historyLine.length() > 0) {
            historyLine.deleteCharAt(historyLine.length() - 1);
        }
        return historyLine.toString();
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