package ru.yandex.practicum.taskTracker.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.model.Type;
import ru.yandex.practicum.taskTracker.service.Managers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ru.yandex.practicum.taskTracker.http.Endpoint.*;
import static ru.yandex.practicum.taskTracker.http.Method.*;
import static ru.yandex.practicum.taskTracker.model.Type.*;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private final TaskManager manager;
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson GSON = Managers.getGson();
    private final int PORT = 8080;

    public HttpTaskServer(TaskManager taskManager) throws URISyntaxException, IOException {
        manager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/tasks/history", new TasksHandler());
        httpServer.createContext("/tasks/subtask/epic", new TasksHandler());
        httpServer.createContext("/tasks/task", new TasksHandler());
        httpServer.createContext("/tasks/epic", new TasksHandler());
        httpServer.createContext("/tasks/subtask", new TasksHandler());
    }

    public void start() {
        httpServer.start();
        System.out.println("HttpTaskServer запущен на порту: " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HttpTaskServer остановлен");
    }

    class TasksHandler implements HttpHandler {
        private int taskId;

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange);
            switch (endpoint) {
                case GET_PRIORITIZES_TASKS:
                    handleGetPrioritizedTasks(exchange);
                    break;
                case GET_HISTORY:
                    handleGetHistory(exchange);
                    break;
                case GET_SUBTASKS_FROM_EPIC:
                    handleGetSubtasksFromEpic(exchange);
                case GET_ALL_TASKS:
                    handleGetAllTasks(exchange);
                    break;
                case GET_TASK_BY_ID:
                    handleGetTaskById(exchange);
                    break;
                case POST_TASK:
                    handlePostTaskById(exchange);
                    break;
                case DELETE_ALL_TASKS:
                    handleDeleteAllTasks(exchange);
                    break;
                case DELETE_TASK_BY_ID:
                    handleDeleteTaskById(exchange);
                    break;
                case GET_ALL_EPICS:
                    handleGetAllEpics(exchange);
                    break;
                case GET_EPIC_BY_ID:
                    handleGetEpicById(exchange);
                    break;
                case POST_EPIC:
                    handlePostEpicById(exchange);
                    break;
                case DELETE_ALL_EPICS:
                    handleDeleteAllEpics(exchange);
                    break;
                case DELETE_EPIC_BY_ID:
                    handleDeleteEpicById(exchange);
                    break;
                case GET_ALL_SUBTASKS:
                    handleGetAllSubtasks(exchange);
                    break;
                case GET_SUBTASK_BY_ID:
                    handleGetSubtaskById(exchange);
                    break;
                case POST_SUBTASK:
                    handlePostSubtaskById(exchange);
                    break;
                case DELETE_ALL_SUBTASKS:
                    handleDeleteAllSubtasks(exchange);
                    break;
                case DELETE_SUBTASK_BY_ID:
                    handleDeleteSubtaskById(exchange);
                    break;
                default:
                    writeResponse(exchange, "Ошибка запроса", 404);
            }
        }

        private Endpoint getEndpoint(HttpExchange exchange) throws IOException {
            final String[] pathParts = exchange.getRequestURI().getPath().split("/");
            final String requestMethod = exchange.getRequestMethod();
            final boolean isContainsRequestId = exchange.getRequestURI().toString().contains("?id=");
            final boolean isGetMethod = GET.equalsMethod(requestMethod);
            final int contextIndex = 1;
            final int typeIndex = 2;
            final int subtasksFromEpicIndex = 3;
            if (pathParts[contextIndex].equals("tasks")) {
                if ((pathParts.length == contextIndex + 1) && (isGetMethod)) {
                    return GET_PRIORITIZES_TASKS;
                }
                if ((pathParts.length == subtasksFromEpicIndex + 1)
                        && (pathParts[typeIndex].equals(SUBTASK.toLowerCase()))
                        && (pathParts[subtasksFromEpicIndex].equals(EPIC.toLowerCase()))
                        && (isContainsRequestId)
                        && (isGetMethod)) {
                    return GET_SUBTASKS_FROM_EPIC;
                }
                if ((pathParts.length == typeIndex + 1)) {
                    switch (Type.valueOf(pathParts[typeIndex].toUpperCase())) {
                        case HISTORY:
                            if (isGetMethod) {
                                return GET_HISTORY;
                            }
                        case TASK:
                            return processRequestData(TASK, exchange, requestMethod, isContainsRequestId);
                        case EPIC:
                            return processRequestData(EPIC, exchange, requestMethod, isContainsRequestId);
                        case SUBTASK:
                            return processRequestData(SUBTASK, exchange, requestMethod, isContainsRequestId);
                    }
                }
            }
            return UNKNOWN;
        }

        private Endpoint processRequestData(Type taskType,
                                            HttpExchange exchange,
                                            String requestMethod,
                                            boolean isContainsRequestId) throws IOException {
            switch (Method.valueOf(requestMethod)) {
                case POST:
                    return post(taskType);
                case GET:
                    if (isContainsRequestId) {
                        this.taskId = getPostId(exchange);
                        return getById(taskType);
                    } else return getAll(taskType);
                case DELETE:
                    if (isContainsRequestId) {
                        this.taskId = getPostId(exchange);
                        return deleteById(taskType);
                    } else return deleteAll(taskType);
                default:
                    return UNKNOWN;
            }
        }

        private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
            List<Task> taskList = manager.getPrioritizedTasks();
            if (!taskList.isEmpty()) {
                writeResponse(exchange, GSON.toJson(taskList), 200);
            } else writeResponse(exchange, "Список приоритетных задач пуст", 204);
        }

        private void handleGetHistory(HttpExchange exchange) throws IOException {
            List<Task> historyList = manager.getHistory();
            if (!historyList.isEmpty()) {
                writeResponse(exchange, GSON.toJson(historyList), 200);
            } else writeResponse(exchange, "Список истории пуст", 204);
        }

        private void handleGetSubtasksFromEpic(HttpExchange exchange) throws IOException {
            this.taskId = getPostId(exchange);
            List<Subtask> subtasksList;
            try {
                subtasksList = manager.getSubtasksFromEpic(manager.getEpicById(taskId));
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, exception.getMessage(), 204);
                return;
            }
            if (!subtasksList.isEmpty()) {
                writeResponse(exchange, GSON.toJson(subtasksList), 200);
            } else writeResponse(exchange, "Список подзадач пуст", 204);
        }

        private void handleGetAllTasks(HttpExchange exchange) throws IOException {
            List<Task> taskList = manager.getTasks();
            if (!taskList.isEmpty()) {
                writeResponse(exchange, GSON.toJson(taskList), 200);
            } else writeResponse(exchange, "Список задач пуст", 204);
        }

        private void handleGetTaskById(HttpExchange exchange) throws IOException {
            try {
                writeResponse(exchange, GSON.toJson(manager.getTaskById(taskId)), 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, exception.getMessage(), 204);
            }
        }

        private void handlePostTaskById(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Task task = GSON.fromJson(body, Task.class);
            try {
                manager.updateTask(task);
                writeResponse(exchange, "Задача обновлена", 201);
            } catch (IllegalArgumentException exceptionUpdate) {
                try {
                    manager.addNewTask(task);
                    writeResponse(exchange, "Задача добавлена", 201);
                } catch (IllegalArgumentException exceptionOfAddition) {
                    writeResponse(exchange, "Ошибка добавления задачи", 404);
                }
            }
        }

        private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
            manager.deleteAllTasks();
            writeResponse(exchange, "Все задачи удалены", 200);
        }

        private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
            try {
                manager.deleteTaskById(taskId);
                writeResponse(exchange, "Задача ID:" + taskId + " удалена", 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, "Задача ID:" + taskId + " не найдена", 404);
            }
        }

        private void handleGetAllEpics(HttpExchange exchange) throws IOException {
            List<Epic> epicList = manager.getEpics();
            if (!epicList.isEmpty()) {
                writeResponse(exchange, GSON.toJson(epicList), 200);
            } else writeResponse(exchange, "Список эпиков пуст", 204);
        }

        private void handleGetEpicById(HttpExchange exchange) throws IOException {
            try {
                writeResponse(exchange, GSON.toJson(manager.getEpicById(taskId)), 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, exception.getMessage(), 204);
            }
        }

        private void handlePostEpicById(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Epic epic = GSON.fromJson(body, Epic.class);
            try {
                manager.updateEpic(epic);
                writeResponse(exchange, "Эпик обновлен", 201);
            } catch (IllegalArgumentException exceptionUpdate) {
                try {
                    manager.addNewEpic(epic);
                    writeResponse(exchange, "Эпик добавлен", 201);
                } catch (IllegalArgumentException exceptionOfAddition) {
                    writeResponse(exchange, "Ошибка добавления эпика", 404);
                }
            }
        }

        private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
            manager.deleteAllEpics();
            System.out.println("Delete epics");
            writeResponse(exchange, "Все эпики удалены", 200);
        }

        private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
            try {
                manager.deleteEpicById(taskId);
                writeResponse(exchange, "Эпик ID:" + taskId + " удален", 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, "Эпик ID:" + taskId + " не найден", 404);
            }
        }

        private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
            List<Subtask> subtaskList = manager.getSubtasks();
            if (!subtaskList.isEmpty()) {
                writeResponse(exchange, GSON.toJson(subtaskList), 200);
            } else writeResponse(exchange, "Список подзадач пуст", 204);
        }

        private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
            try {
                writeResponse(exchange, GSON.toJson(manager.getSubTaskById(taskId)), 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, exception.getMessage(), 204);
            }
        }

        private void handlePostSubtaskById(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = GSON.fromJson(body, Subtask.class);
            try {
                manager.updateSubtask(subtask);
                writeResponse(exchange, "Подзадача обновлена", 201);
            } catch (IllegalArgumentException exceptionUpdate) {
                try {
                    manager.addNewSubtask(subtask);
                    writeResponse(exchange, "Подзадача добавлена", 201);
                } catch (IllegalArgumentException exceptionOfAddition) {
                    writeResponse(exchange, "Ошибка добавления подзадачи", 404);
                }
            }
        }

        private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
            manager.deleteAllSubtasks();
            writeResponse(exchange, "Все подзадачи удалены", 200);
        }

        private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
            try {
                manager.deleteSubtaskById(taskId);
                writeResponse(exchange, "Подзадача ID:" + taskId + " удалена", 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, "Подзадача ID:" + taskId + " не найдена", 404);
            }
        }

        private int getPostId(HttpExchange exchange) throws IOException {
            try {
                String requestURI = exchange.getRequestURI().toString();
                String expectedId = requestURI.substring(requestURI.lastIndexOf("id=") + 3);
                return Optional.of(Integer.parseInt(expectedId)).get();
            } catch (NumberFormatException exception) {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                return -1;
            }
        }

        private void writeResponse(HttpExchange exchange,
                                   String responseString,
                                   int responseCode) throws IOException {
            if (responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }
    }
}