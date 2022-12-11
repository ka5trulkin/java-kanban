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

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path backupFile;

    public FileBackedTasksManager(Path backupFile) {
        this.backupFile = backupFile;
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

    private Type getType(String backupFileLine) {
        int firstIndex = backupFileLine.indexOf(",") + 1;
        int secondIndex = backupFileLine.indexOf(",", firstIndex);
        String type = backupFileLine.substring(firstIndex, secondIndex);
        return Type.valueOf(type);
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

    public static void main(String[] args) {
        Path path = Paths.get("src/ru/yandex/practicum/taskTracker/files/backup-task-manager.csv");
        FileBackedTasksManager taskManager = FileBackedTasksManager.loadFromFile(path);

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
        for (Task task : taskManager.historyManager.getHistory()) {
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
        System.out.println(System.lineSeparator() + "ID задач после вызова:");
        for (Task task : taskManager.historyManager.getHistory()) {
            System.out.print(task.getId() + " ");
        }
        System.out.println();
        FileBackedTasksManager taskManager2 = new FileBackedTasksManager(path);

        System.out.println(System.lineSeparator() + "ID задач из истории просмотров нового объекта:");
        for (Task task : taskManager2.historyManager.getHistory()) {
            System.out.print(task.getId() + " ");
        }
        System.out.println();
        FileBackedTasksManager taskManager3 = FileBackedTasksManager.loadFromFile(path);

        System.out.println("ID задач из истории просмотров восстановленного файла:");
        for (Task task : taskManager3.historyManager.getHistory()) {
            System.out.print(task.getId() + " ");
        }
        System.out.println(System.lineSeparator() + "Вывод данных о задачах восстановленного файла:");
        for (Task task : taskManager.getHistoryFromManager()) {
            System.out.println(task);
        }

    }
}