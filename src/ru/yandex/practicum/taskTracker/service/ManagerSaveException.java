package ru.yandex.practicum.taskTracker.service;

public class ManagerSaveException extends RuntimeException{
    ManagerSaveException(String message) {
        super(message);
    }
}
