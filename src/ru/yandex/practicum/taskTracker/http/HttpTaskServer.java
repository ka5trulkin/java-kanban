package ru.yandex.practicum.taskTracker.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.service.FileBackedTasksManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ru.yandex.practicum.taskTracker.http.Endpoint.*;

public class HttpTaskServer {
    private final TaskManager manager = FileBackedTasksManager.loadFromFile(new File("resource/backup-task-manager.csv"));
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson = new Gson();

    public void startServer() throws IOException {
        HttpServer httpServer = HttpServer.create();
        int PORT = 8080;
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/tasks/history", new TasksHandler());
        httpServer.createContext("/tasks/subtask/epic", new TasksHandler());
        httpServer.createContext("/tasks/task", new TasksHandler());
        httpServer.createContext("/tasks/epic", new TasksHandler());
        httpServer.createContext("/tasks/subtask", new TasksHandler());
        httpServer.start();
        System.out.println("Сервер запущен");
    }

    class TasksHandler implements HttpHandler {
        private int taskId;
        private final String GET = "GET";
        private final String POST = "POST";
        private final String DELETE = "DELETE";

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                Endpoint endpoint = getEndpoint(exchange);
                switch (endpoint) {
                    case GET_PRIORITIZES_TASKS: {
                        handleGetPrioritizedTasks(exchange);
                        break;
                    }
                    case GET_HISTORY: {
                        handleGetHistory(exchange);
                        break;
                    }
                    case GET_SUBTASKS_FROM_EPIC: {
                        handleGetSubtasksFromEpic(exchange);
                    }
                    case GET_ALL_TASKS: {
                        handleGetAllTasks(exchange);
                        break;
                    }
                    case GET_TASK_BY_ID: {
                        handleGetTaskById(exchange);
                        break;
                    }
                    case POST_TASK: {
                        handlePostTaskById(exchange);
                        break;
                    }
                    case DELETE_ALL_TASKS: {
                        handleDeleteAllTasks(exchange);
                        break;
                    }
                    case DELETE_TASK_BY_ID: {
                        handleDeleteTaskById(exchange);
                        break;
                    }
                    case GET_ALL_EPICS: {
                        handleGetAllEpics(exchange);
                        break;
                    }
                    case GET_EPIC_BY_ID: {
                        handleGetEpicById(exchange);
                        break;
                    }
                    case POST_EPIC: {
                        handlePostEpicById(exchange);
                        break;
                    }
                    case DELETE_ALL_EPICS: {
                        handleDeleteAllEpics(exchange);
                        break;
                    }
                    case DELETE_EPIC_BY_ID: {
                        handleDeleteEpicById(exchange);
                        break;
                    }
                    case GET_ALL_SUBTASKS: {
                        handleGetAllSubtasks(exchange);
                        break;
                    }
                    case GET_SUBTASK_BY_ID: {
                        handleGetSubtaskById(exchange);
                        break;
                    }
                    case POST_SUBTASK: {
                        handlePostSubtaskById(exchange);
                        break;
                    }
                    case DELETE_ALL_SUBTASKS: {
                        handleDeleteAllSubtasks(exchange);
                        break;
                    }
                    case DELETE_SUBTASK_BY_ID: {
                        handleDeleteSubtaskById(exchange);
                        break;
                    }
                    default:
                        writeResponse(exchange, "Ошибка запроса", 404);
                }
            }finally {
                exchange.close();
            }
        }

        private Endpoint getEndpoint(HttpExchange exchange) throws IOException {
            try {
                String[] pathParts = exchange.getRequestURI().getPath().split("/");
                String requestMethod = exchange.getRequestMethod();
                boolean isContainsParameter = exchange.getRequestURI().toString().contains("?");
                boolean isContainsId = exchange.getRequestURI().toString().contains("id=");
                final int contextIndex = 1;
                final int typeIndex = 2;
                final int subtasksFromEpicIndex = 3;
                if ((pathParts.length >= contextIndex + 1) && (pathParts[contextIndex].equals("tasks"))) {
                    if ((pathParts.length == contextIndex + 1) && (requestMethod.equals(GET))) {
                        return GET_PRIORITIZES_TASKS;
                    }
                    if ((pathParts.length == typeIndex + 1) && (pathParts[typeIndex].equals("history"))) {
                        return GET_HISTORY;
                    }
                    if ((pathParts.length == subtasksFromEpicIndex + 1)
                            && (pathParts[typeIndex].equals("subtask"))
                            && (pathParts[subtasksFromEpicIndex].equals("epic"))
                            && (isContainsId)) {
                        return GET_SUBTASKS_FROM_EPIC;
                    }
                    if ((pathParts.length == typeIndex + 1) && (pathParts[typeIndex].equals("task"))) {
                        return processRequestTaskData(exchange, requestMethod, isContainsParameter, isContainsId);
                    }
                    if ((pathParts.length == typeIndex + 1) && (pathParts[typeIndex].equals("epic"))) {
                        return processRequestEpicData(exchange, requestMethod, isContainsParameter, isContainsId);
                    }
                    if ((pathParts.length == typeIndex + 1) && (pathParts[typeIndex].equals("subtask"))) {
                        return processRequestSubtaskData(exchange, requestMethod, isContainsParameter, isContainsId);
                    }
                }
                return UNKNOWN;
            } finally {
                exchange.close();
            }
        }

        private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
            List<Task> taskList = manager.getPrioritizedTasks();
            if (!taskList.isEmpty()) {
                writeResponse(exchange, gson.toJson(taskList), 200);
            } else writeResponse(exchange, "Список приоритетных задач пуст", 204);
        }

        private void handleGetHistory(HttpExchange exchange) throws IOException {
            try {
                List<Task> historyList = manager.getHistory();
                if (!historyList.isEmpty()) {
                    writeResponse(exchange, gson.toJson(historyList), 200);
                } else writeResponse(exchange, "Список истории пуст", 204);
            } finally {
                exchange.close();
            }
        }

        private void handleGetSubtasksFromEpic(HttpExchange exchange) throws IOException {
            try {
                this.taskId = getPostId(exchange);
                List<Subtask> subtasksList = manager.getSubtasksFromEpic(manager.getEpicById(taskId));
                if (!subtasksList.isEmpty()) {
                    writeResponse(exchange, gson.toJson(subtasksList), 200);
                } else writeResponse(exchange, "Список подзадач пуст", 204);
            } finally {
                exchange.close();
            }
        }

        // обработка запросов Tasks
        private Endpoint processRequestTaskData(HttpExchange exchange,
                                                String requestMethod,
                                                boolean isContainsRequest,
                                                boolean isContainsId) throws IOException {
            try {
                if ((requestMethod.equals(GET)) && (!isContainsRequest)) {
                    return GET_ALL_TASKS;
                }
                if ((requestMethod.equals(GET)) && (isContainsId)) {
                    this.taskId = getPostId(exchange);
                    return GET_TASK_BY_ID;
                }
                if (requestMethod.equals(POST)) {
                    return POST_TASK;
                }
                if ((requestMethod.equals(DELETE)) && (!isContainsRequest)) {
                    return DELETE_ALL_TASKS;
                }
                if ((requestMethod.equals(DELETE)) && (isContainsId)) {
                    this.taskId = getPostId(exchange);
                    return DELETE_TASK_BY_ID;
                }
                return UNKNOWN;
            } finally {
                exchange.close();
            }
        }

        private void handleGetAllTasks(HttpExchange exchange) throws IOException {
            try {
                List<Task> taskList = manager.getTasks();
                if (!taskList.isEmpty()) {
                    writeResponse(exchange, gson.toJson(taskList), 200);
                } else writeResponse(exchange, "Список задач пуст", 204);
            } finally {
                exchange.close();
            }
        }

        private void handleGetTaskById(HttpExchange exchange) throws IOException {
            try {
                writeResponse(exchange, gson.toJson(manager.getTaskById(taskId)), 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, exception.getMessage(), 204);
            } finally {
                exchange.close();
            }
        }

        private void handlePostTaskById(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(body, Task.class);
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
            } finally {
                exchange.close();
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
            } finally {
                exchange.close();
            }
        }

        // обработка запросов Epics
        private Endpoint processRequestEpicData(HttpExchange exchange,
                                                String requestMethod,
                                                boolean isContainsRequest,
                                                boolean isContainsId) throws IOException {
            try {
                if ((requestMethod.equals(GET)) && (!isContainsRequest)) {
                    return GET_ALL_EPICS;
                }
                if ((requestMethod.equals(GET)) && (isContainsId)) {
                    this.taskId = getPostId(exchange);
                    return GET_EPIC_BY_ID;
                }
                if (requestMethod.equals(POST)) {
                    return POST_EPIC;
                }
                if ((requestMethod.equals(DELETE)) && (!isContainsRequest)) {
                    return DELETE_ALL_EPICS;
                }
                if ((requestMethod.equals(DELETE)) && (isContainsId)) {
                    this.taskId = getPostId(exchange);
                    return DELETE_EPIC_BY_ID;
                }
                return UNKNOWN;
            } finally {
                exchange.close();
            }
        }

        private void handleGetAllEpics(HttpExchange exchange) throws IOException {
            try {
                List<Epic> epicList = manager.getEpics();
                if (!epicList.isEmpty()) {
                    writeResponse(exchange, gson.toJson(epicList), 200);
                } else writeResponse(exchange, "Список эпиков пуст", 204);
            } finally {
                exchange.close();
            }
        }

        private void handleGetEpicById(HttpExchange exchange) throws IOException {
            try {
                writeResponse(exchange, gson.toJson(manager.getEpicById(taskId)), 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, exception.getMessage(), 204);
            } finally {
                exchange.close();
            }
        }

        private void handlePostEpicById(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(body, Epic.class);
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
            } finally {
                exchange.close();
            }
        }

        private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
            try {
                manager.deleteAllEpics();
                writeResponse(exchange, "Все эпики удалены", 200);
            } finally {
                exchange.close();
            }
        }

        private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
            try {
                manager.deleteEpicById(taskId);
                writeResponse(exchange, "Эпик ID:" + taskId + " удален", 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, "Эпик ID:" + taskId + " не найден", 404);
            } finally {
                exchange.close();
            }
        }

        // обработка запросов Subtasks
        private Endpoint processRequestSubtaskData(HttpExchange exchange,
                                                   String requestMethod,
                                                   boolean isContainsRequest,
                                                   boolean isContainsId) throws IOException {
            try {
                if ((requestMethod.equals(GET)) && (!isContainsRequest)) {
                    return GET_ALL_SUBTASKS;
                }
                if ((requestMethod.equals(GET)) && (isContainsId)) {
                    this.taskId = getPostId(exchange);
                    return GET_SUBTASK_BY_ID;
                }
                if (requestMethod.equals(POST)) {
                    return POST_SUBTASK;
                }
                if ((requestMethod.equals(DELETE)) && (!isContainsRequest)) {
                    return DELETE_ALL_SUBTASKS;
                }
                if ((requestMethod.equals(DELETE)) && (isContainsId)) {
                    this.taskId = getPostId(exchange);
                    return DELETE_SUBTASK_BY_ID;
                }
                return UNKNOWN;
            } finally {
                exchange.close();
            }
        }

        private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
            try {
                List<Subtask> subtaskList = manager.getSubtasks();
                if (!subtaskList.isEmpty()) {
                    writeResponse(exchange, gson.toJson(subtaskList), 200);
                } else writeResponse(exchange, "Список подзадач пуст", 204);
            } finally {
                exchange.close();
            }
        }

        private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
            try {
                writeResponse(exchange, gson.toJson(manager.getSubTaskById(taskId)), 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, exception.getMessage(), 204);
            } finally {
                exchange.close();
            }
        }

        private void handlePostSubtaskById(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(body, Subtask.class);
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
            } finally {
                exchange.close();
            }
        }

        private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
            try {
                manager.deleteAllSubtasks();
                writeResponse(exchange, "Все подзадачи удалены", 200);
            } finally {
                exchange.close();
            }
        }

        private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
            try {
                manager.deleteSubtaskById(taskId);
                writeResponse(exchange, "Подзадача ID:" + taskId + " удалена", 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, "Подзадача ID:" + taskId + " не найдена", 404);
            } finally {
                exchange.close();
            }
        }

        private int getPostId(HttpExchange exchange) throws IOException {
            try {
                String requestURI = exchange.getRequestURI().toString();
                String expectedId = requestURI.substring(requestURI.lastIndexOf("id=".length()));
                return Optional.of(Integer.parseInt(expectedId)).get();
            } catch (NumberFormatException exception) {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                return -1;
            } finally {
                exchange.close();
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

