package ru.yandex.practicum.taskTracker.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.service.FileBackedTasksManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ru.yandex.practicum.taskTracker.http.Endpoint.*;

public class HttpTaskServer {
    private final int PORT = 8080;
    private final TaskManager manager = FileBackedTasksManager.loadFromFile(new File("resource/backup-task-manager.csv"));
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson = new Gson();

    public void startServer() throws IOException {
        HttpServer httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/tasks/task", new TasksHandler());
        httpServer.createContext("/tasks/epic", new TasksHandler());
        httpServer.createContext("/tasks/subtask", new TasksHandler());
        httpServer.createContext("/tasks/history", new TasksHandler());
        httpServer.start(); // запускаем сервер
        System.out.println("Сервер запущен");
//        httpServer.stop(60);
    }

    class TasksHandler implements HttpHandler {
        private int taskId;
        private String GET = "GET";
        private String POST = "POST";
        private String DELETE = "DELETE";

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange);

            switch (endpoint) {
                case GET_PRIORITIZES_TASKS: {
                    handleGetPrioritizedTasks(exchange);
                    break;
                }
                case GET_ALL_TASKS: {
                    handleGetTasks(exchange);
                    break;
                }
                case GET_TASK_BY_ID: {
                    handleGetTaskById(exchange);
                    break;
                }
                case POST_TASK:
                    handlePostTaskById(exchange);
                case DELETE_ALL_TASKS:
                    handleDeleteAllTasks(exchange);
                case DELETE_TASK_BY_ID:
                    handleDeleteTaskById(exchange);
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }

        private Endpoint getEndpoint(HttpExchange exchange) throws IOException {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            String requestMethod = exchange.getRequestMethod();
            boolean isContainsRequest = exchange.getRequestURI().toString().contains("?");
            boolean isContainsId = exchange.getRequestURI().toString().contains("id=");
            final int contextIndex = 1;
            final int typeIndex = 2;
            final int contextLength = 2;
            final int taskTypeLength = 3;
            final int requestLength = 4;
            String context = "tasks";
            String task = "task";
            String epic = "epic";
            String subtask = "subtask";

            if (pathParts[typeIndex].equals("task")) {

            }

            if ((pathParts.length >= contextLength) && (pathParts[contextIndex].equals(context))) {
                if ((pathParts.length == contextLength) && (requestMethod.equals(GET))) {
                    return GET_PRIORITIZES_TASKS;
                }
                if ((pathParts.length == taskTypeLength) && (requestMethod.equals(GET)) && (!isContainsRequest)) {
                    return GET_ALL_TASKS;
                }
                if ((pathParts.length == taskTypeLength) && (requestMethod.equals(GET)) && (isContainsId)) {
                    this.taskId = getPostId(exchange);
                    return GET_TASK_BY_ID;
                }
                if ((pathParts.length == taskTypeLength) && (requestMethod.equals(POST))) {
                    return POST_TASK;
                }
                if ((pathParts.length == taskTypeLength) && (requestMethod.equals(DELETE)) && (!isContainsRequest)) {
                    return DELETE_ALL_TASKS;
                }
                if ((pathParts.length == taskTypeLength) && (requestMethod.equals(DELETE)) && (isContainsId)) {
                    this.taskId = getPostId(exchange);
                    return DELETE_TASK_BY_ID;
                }

//                if (pathParts.length == 4 && pathParts[contextIndex].equals("posts") && pathParts[3].equals("comments")) {
//                    if (requestMethod.equals("GET")) {
//                        return Endpoint.GET_ALL_TASKS;
//                    }
//                    if (requestMethod.equals("POST")) {
//                        return Endpoint.GET_TASK_BY_ID;
//                    }
//                }
            }
            return Endpoint.UNKNOWN;
        }

        private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
            List<Task> taskList = manager.getPrioritizedTasks();
            if (!taskList.isEmpty()) {
                writeResponse(exchange, gson.toJson(taskList), 200);
            } else writeResponse(exchange, "Список приоритетных задач пуст", 204);
        }

        private void handleGetTasks(HttpExchange exchange) throws IOException {
            List<Task> taskList = manager.getTasks();
            if (!taskList.isEmpty()) {
                writeResponse(exchange, gson.toJson(manager.getTasks()), 200);
            } else writeResponse(exchange, "Список задач пуст", 204);
        }

        private void handleGetTaskById(HttpExchange exchange) throws IOException {
            try {
                writeResponse(exchange, gson.toJson(manager.getTaskById(taskId)), 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, exception.getMessage(), 204);
            }
        }

        private void handlePostTaskById(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
            Task task = gson.fromJson(body, Task.class);
            System.out.println("Post task");
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


//        private void handlePostComments(HttpExchange exchange) throws IOException {
//            // реализуйте обработку добавления комментария
//
//            // извлеките идентификатор поста и обработайте исключительные ситуации
//            Optional<Integer> postIdOpt = getPostId(exchange);
//            if (postIdOpt.isEmpty()) {
//                writeResponse(exchange, "Некорректный идентификатор поста", 400);
//                return;
//            }
//            int postId = postIdOpt.get();
//            Post post;
//
//            try {
//                post = posts.stream()
//                        .filter(post1 -> post1.getId() == postId)
//                        .findFirst()
//                        .orElseThrow();
//            } catch (NoSuchElementException exception) {
//                writeResponse(exchange, "Пост с идентификатором " + postId + " не найден", 404);
//                return;
//            }
//            InputStream inputStream = exchange.getRequestBody();
//            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
//            try {
//                Comment comment = gson.fromJson(body, Comment.class);
//                if ((comment.getUser() == null) || (comment.getText() == null)) {
//                    writeResponse(exchange, "Поля комментария не могут быть пустыми", 400);
//                    return;
//                }
//                post.addComment(comment);
//                writeResponse(exchange, "Комментарий добавлен", 201);
//            } catch (JsonSyntaxException e) {
//                writeResponse(exchange, "Получен некорректный JSON", 400);
//            }
//            /* Получите тело запроса в виде текста в формате JSON и преобразуйте его в объект Comment.
//            Учтите, что может быть передан некоректный JSON — эту ситуацию нужно обработать.
//            Подумайте, какие ещё ситуации требуют обработки. */
//            // ...
//
//            // найдите пост с указанным идентификатором и добавьте в него комментарий

//        }


//        private void handleGetComments(HttpExchange exchange) throws IOException {
//            Optional<Integer> postIdOpt = getPostId(exchange);
//            if (postIdOpt.isEmpty()) {
//                writeResponse(exchange, "Некорректный идентификатор поста", 400);
//                return;
//            }
//            int postId = postIdOpt.get();
//
//            for (Post post : posts) {
//                if (post.getId() == postId) {
//                    String commentsJson = gson.toJson(post.getCommentaries());
//                    writeResponse(exchange, commentsJson, 200);
//                    return;
//                }
//            }
//
//            writeResponse(exchange, "Пост с идентификатором " + postId + " не найден", 404);
//        }

        private int getPostId(HttpExchange exchange) throws IOException {
            String requestURI = exchange.getRequestURI().toString();
//            if (requestURI.contains("id=")) {
                try {
                    String expectedId = requestURI.substring(requestURI.lastIndexOf("id=") + 3);
                    Optional<Integer> id = Optional.of(Integer.parseInt(expectedId));
                    return id.get();
                } catch (NumberFormatException exception) {
                    writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                    return 0;
                }
//            }
        }

//        private Optional<Integer> getPostId(HttpExchange exchange) {
//            String[] pathParts = exchange.getRequestURI().getPath().split("/");
//            try {
//                return Optional.of(Integer.parseInt(pathParts[2]));
//            } catch (NumberFormatException exception) {
//                return Optional.empty();
//            }
//        }

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

