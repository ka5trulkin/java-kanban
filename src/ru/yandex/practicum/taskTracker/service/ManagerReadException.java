package ru.yandex.practicum.taskTracker.service;

public class ManagerReadException extends RuntimeException {
    ManagerReadException(String message) {
        super(message);
    }
}
