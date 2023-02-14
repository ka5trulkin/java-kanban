package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.http.KVServer;
import ru.yandex.practicum.taskTracker.http.KVTaskClient;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        new KVServer().start();
//        KVTaskClient client = new KVTaskClient(new URI("http://localhost:8078"));
//        String key = "kurva";
//        String value = "Ja perdolal";
//        client.save(key, value);
//        System.out.println("Загруженные данные по ключу: " + key + " - " + client.load(key));
//        client.save(key, "Ne perdolal");
//        System.out.println("Обновленные данные по ключу: " + key + " - " + client.load(key));
    }
}