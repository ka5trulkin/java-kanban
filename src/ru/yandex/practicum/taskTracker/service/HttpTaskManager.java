package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.http.KVTaskClient;
import ru.yandex.practicum.taskTracker.model.Epic;

import java.net.URI;
import java.net.URISyntaxException;
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

    public void load() {
        String[] fileLines;
        String fileLine;
        String historyLine;
        int dataLine = 1;
        fileLines = this.client.load(this.key).split(System.lineSeparator());
        System.out.println("Проверка fileLines: " + Arrays.toString(fileLines));
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

    public static void main(String[] args) throws URISyntaxException {
        HttpTaskManager manager = new HttpTaskManager(new URI("http://localhost:8078"));
        manager.addNewEpic(new Epic("New Epic", "Epic Description", 777));
        System.out.println(manager.getPrioritizedTasks());
        System.out.println(manager.getEpics());
        System.out.println("Старый ключ: " + manager.getKey());
        manager.setKey("new");
        System.out.println("Новый ключ: " + manager.getKey());
        HttpTaskManager manager1 = new HttpTaskManager(new URI("http://localhost:8078"));
        System.out.println("Second manager: " + manager1.getEpics());
//        manager1.load();
//        System.out.println(manager1.getEpics());

    }
}
