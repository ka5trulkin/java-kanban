package ru.yandex.practicum.taskTracker.service;

import com.google.gson.Gson;
import ru.yandex.practicum.taskTracker.http.KVTaskClient;
import ru.yandex.practicum.taskTracker.model.Task;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final String key = "User";
    private final Gson gson = Managers.getGson();
    URI uri;

    public HttpTaskManager(URI uri) {
        this.uri = uri;
        this.client = new KVTaskClient(uri);
    }

    @Override
    protected void save() {
        StringBuilder stringBuilder = new StringBuilder()
                .append(tasksToString())
                .append(historyToString(this.historyManager));
        client.save(key, stringBuilder.toString());
    }

    public void load() {
        String[] fileLines;
        String fileLine;
        String historyLine;
        int dataLine = 1;

        fileLines = this.client.load(this.key).split(System.lineSeparator());
        System.out.println("Проверка fileLines: " + Arrays.toString(fileLines).length());
        historyLine = fileLines[fileLines.length - 1];
        for (int index = dataLine; index < fileLines.length; index++) {
            fileLine = fileLines[index];
            if (!fileLine.isBlank()) {
                this.fillTasksManager(fileLine);
            } else if (!historyLine.isBlank()) {
                this.fillHistoryManager(this.historyFromString(historyLine));
                break;
            }
        }
    }


    public static HttpTaskManager loadFromURI(URI uri) {
        HttpTaskManager manager = new HttpTaskManager(uri);
        String[] fileLines;
        String fileLine;
        String historyLine;
        int dataLine = 1;

        fileLines = manager.client.load(manager.key).split(System.lineSeparator());
        System.out.println("Проверка fileLines: " + Arrays.toString(fileLines).length());
        historyLine = fileLines[fileLines.length - 1];
        for (int index = dataLine; index < fileLines.length; index++) {
            fileLine = fileLines[index];
            if (!fileLine.isBlank()) {
                manager.fillTasksManager(fileLine);
            } else if (!historyLine.isBlank()) {
                manager.fillHistoryManager(manager.historyFromString(historyLine));
                break;
            }
        }
        return manager;
    }

    private void getBackupFile() {
        String data = client.load("key");
        if (this.backupFile.isFile()) {
            try (BufferedWriter bufferedWriter
                         = new BufferedWriter(new FileWriter(backupFile.toString(), StandardCharsets.UTF_8))) {
                bufferedWriter.write(data);
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка записи в файл");
            }
        }
    }

    public static void main(String[] args) throws URISyntaxException {
        HttpTaskManager manager = new HttpTaskManager(new URI("http://localhost:8078"));
        HttpTaskManager manager1 = new HttpTaskManager(new URI("http://localhost:8078"));
        manager.addNewTask(new Task("New Task", "New Description", 1));
//        System.out.println(manager.getTasks());
        System.out.println(manager.client.load(manager.key));
////        manager.deleteAllTasks();
//        System.out.println("Удалили задачи: " + manager.getTasks());
//        manager1 = HttpTaskManager.loadFromURI(manager1.uri);
        manager1.load();
        System.out.println("Должны восстановиться задачи, но это не точно! " + manager1.getTasks());
    }
}
