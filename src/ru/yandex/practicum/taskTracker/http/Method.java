package ru.yandex.practicum.taskTracker.http;

public enum Method {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE");

    private final String name;

    Method(String name) {
        this.name = name;
    }

    public boolean equalsMethod(String method) {
        return name.equals(method);
    }

    public String toString() {
        return this.name;
    }
}