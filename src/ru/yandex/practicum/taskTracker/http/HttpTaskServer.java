package ru.yandex.practicum.taskTracker.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Task;
import ru.yandex.practicum.taskTracker.service.FileBackedTasksManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    }

    class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_ALL_TASKS: {
                    handleGetAllTasks(exchange);
                    break;
                }
                case GET_TASKS: {
                    handleGetTasks(exchange);
                    break;
                }
//                case POST_COMMENT: {
//                    handlePostComments(exchange);
//                    break;
//                }
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }

        private Endpoint getEndpoint(String requestPath, String requestMethod) {
            String[] pathParts = requestPath.split("/");
            final int context = 1;
            final int taskType = 2;
            final int request = 3;
            final int contextLength = 2;
            final int taskTypeLength = 3;
            final int requestLength = 4;
            String tasks = "tasks";
            String task = "task";
            String epic = "epic";
            String subtask = "subtask";
            System.out.println(Arrays.toString(pathParts));

            if ((pathParts.length >= contextLength) && (pathParts[context].equals(tasks)))
            if (pathParts.length == contextLength) {
                return Endpoint.GET_ALL_TASKS;
            }
            if (pathParts.length == taskTypeLength) {
                return Endpoint.GET_TASKS;
            }
            if (pathParts.length == requestLength
                    && pathParts[context].equals(tasks)
                    && pathParts[taskType].equals(task)) {
//                if (pathParts[request] == )
            }

            if (pathParts.length == 4 && pathParts[context].equals("posts") && pathParts[3].equals("comments")) {
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_TASKS;
                }
                if (requestMethod.equals("POST")) {
                    return Endpoint.POST_COMMENT;
                }
            }
            return Endpoint.UNKNOWN;
        }

        private void handleGetAllTasks(HttpExchange exchange) throws IOException {
            List<Task> taskList = new ArrayList<>(manager.getTasks());
            taskList.addAll(manager.getEpics());
            taskList.addAll(manager.getSubtasks());
            writeResponse(exchange, gson.toJson(taskList), 200);
        }

        private void handleGetTasks(HttpExchange exchange) throws IOException {
            writeResponse(exchange, gson.toJson(manager.getTasks()), 200);
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

        private Optional<Integer> getPostId(HttpExchange exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            try {
                return Optional.of(Integer.parseInt(pathParts[2]));
            } catch (NumberFormatException exception) {
                return Optional.empty();
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

