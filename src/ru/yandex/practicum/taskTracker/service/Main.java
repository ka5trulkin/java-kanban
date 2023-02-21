package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.http.HttpTaskServer;
import ru.yandex.practicum.taskTracker.http.KVServer;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        new KVServer().start();
        new HttpTaskServer(Managers.getDefault()).start();
    }
}