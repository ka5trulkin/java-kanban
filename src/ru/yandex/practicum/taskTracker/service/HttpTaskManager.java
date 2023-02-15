package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.http.KVTaskClient;

import java.net.URI;
import java.util.Arrays;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private String key;

    public HttpTaskManager(URI uri) {
        this.client = new KVTaskClient(uri);
        this.key = "default";
    }

    @Override
    protected void save() {
        String value = tasksToString() + historyToString(this.historyManager);
        client.save(key, value);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static HttpTaskManager loadFromURI(URI uri) {
        HttpTaskManager manager = new HttpTaskManager(uri);
        String[] fileLines;
        String fileLine;
        String historyLine;
        int dataLine = 1;
        fileLines = manager.client.load(manager.key).split(System.lineSeparator());
        System.out.println("Проверка fileLines: " + Arrays.toString(fileLines));
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
}