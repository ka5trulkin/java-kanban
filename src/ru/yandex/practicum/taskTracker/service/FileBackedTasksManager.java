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
    protected final File backupFile;
    protected final String lineSeparator = "\r?\n";

    public FileBackedTasksManager(File backupFile) {
        this.backupFile = backupFile;
    }

    protected FileBackedTasksManager() {
        backupFile = new File("");
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
        String infoLine = "id,type,name,status,description,startTime,endTime,epic";
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
        LocalDateTime startTime;
        Duration duration;

        if (!(dataFromStringLine[5].equals("null")) /*&& !(dataFromStringLine[6].equals("null"))*/) {
            startTime = LocalDateTime.parse(dataFromStringLine[5]);
            duration = Duration.between(startTime, LocalDateTime.parse(dataFromStringLine[6]));
        } else {
            startTime = null;
            duration = Duration.ZERO;
        }

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

    protected void fillHistoryManager(List<Integer> history) {
        for (Integer id : history) {
            if (tasks.containsKey(id)) {
                this.getTaskById(id);
            } else if (epics.containsKey(id)) {
                this.getEpicById(id);
            } else if (subtasks.containsKey(id)) {
                this.getSubTaskById(id);
            }
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
        this.addNewTask(task);
        this.checkIdCounter(task.getId());
    }

    private void fillEpics(String fileLine) {
        Epic epic = (Epic) taskFromString(fileLine);
        this.addNewEpic(epic);
        this.checkIdCounter(epic.getId());
    }

    private void fillSubtasks(String fileLine) {
        Subtask subtask = (Subtask) taskFromString(fileLine);
        this.addNewSubtask(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        this.checkEpic(epic);
        this.checkIdCounter(subtask.getId());
    }

    private List<Integer> historyFromString(String fileLine) {
        if (fileLine.isBlank()) {
            return Collections.emptyList();
        }
        List<Integer> list = new ArrayList<>();

        for (String id : fileLine.split(",")) {
            list.add(Integer.parseInt(id));
        }
        return list;
    }

    private String historyToString(HistoryManager manager) {
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

    protected void checkIdCounter(int id) {
        if (id > this.getIdCounter()) {
            this.setIdCounter(id);
        }
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
        historyLine = fileLines[fileLines.length - 1];
        for (int index = dataLine; index < fileLines.length; index++) {
            fileLine = fileLines[index];
            if (!fileLine.isBlank()) {
                tasksManager.fillTasksManager(fileLine);
            } else if (!historyLine.isBlank()) {
                tasksManager.fillHistoryManager(tasksManager.historyFromString(historyLine));
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
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
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
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }
}