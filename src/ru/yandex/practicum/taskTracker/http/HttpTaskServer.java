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
            final int subtaskFromEpicIndex = 3;

            if ((pathParts.length >= contextIndex + 1) && (pathParts[contextIndex].equals("tasks"))) {
                if ((pathParts.length == contextIndex + 1) && (requestMethod.equals(GET))) {
                    return GET_PRIORITIZES_TASKS;
                }
                if ((pathParts.length == typeIndex + 1) && (pathParts[typeIndex].equals("history"))) {
                    return GET_HISTORY;
                }
                if ((pathParts.length == subtaskFromEpicIndex + 1)
                        && (pathParts[typeIndex].equals("subtask"))
                        && (pathParts[subtaskFromEpicIndex].equals("epic"))
                        && (isContainsId)) {
                    return GET_SUBTASKS_FROM_EPIC;
                }
                if ((pathParts.length == typeIndex + 1) && (pathParts[typeIndex].equals("task"))) {
                    return processRequestTaskData(exchange, requestMethod, isContainsRequest, isContainsId);
                }
                if ((pathParts.length == typeIndex + 1) && (pathParts[typeIndex].equals("epic"))) {
                    return processRequestEpicData(exchange, requestMethod, isContainsRequest, isContainsId);
                }
                if ((pathParts.length == typeIndex + 1) && (pathParts[typeIndex].equals("subtask"))) {
                    return processRequestSubtaskData(exchange, requestMethod, isContainsRequest, isContainsId);
                }
            }
            return UNKNOWN;
        }

        private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
            List<Task> taskList = manager.getPrioritizedTasks();
            if (!taskList.isEmpty()) {
                writeResponse(exchange, gson.toJson(taskList), 200);
            } else writeResponse(exchange, "Список приоритетных задач пуст", 204);
        }

        private void handleGetHistory(HttpExchange exchange) throws IOException {
            List<Task> historyList = manager.getHistory();
            if (!historyList.isEmpty()) {
                writeResponse(exchange, gson.toJson(historyList), 200);
            } else writeResponse(exchange, "Список истории пуст", 204);

        }

        private void handleGetSubtasksFromEpic(HttpExchange exchange) throws IOException {
            this.taskId = getPostId(exchange);
            List<Subtask> subtasksList = manager.getSubtasksFromEpic(manager.getEpicById(taskId));
            if (!subtasksList.isEmpty()) {
                writeResponse(exchange, gson.toJson(subtasksList), 200);
            } else writeResponse(exchange, "Список подзадач пуст", 204);
        }
        // обработка запросов Tasks
        private Endpoint processRequestTaskData(HttpExchange exchange,
                                                String requestMethod,
                                                boolean isContainsRequest,
                                                boolean isContainsId) throws IOException {
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
            } return UNKNOWN;
        }

        private void handleGetAllTasks(HttpExchange exchange) throws IOException {
            List<Task> taskList = manager.getTasks();
            if (!taskList.isEmpty()) {
                writeResponse(exchange, gson.toJson(taskList), 200);
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
        // обработка запросов Epics
        private Endpoint processRequestEpicData(HttpExchange exchange,
                                                String requestMethod,
                                                boolean isContainsRequest,
                                                boolean isContainsId) throws IOException {
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
            } return UNKNOWN;
        }

        private void handleGetAllEpics(HttpExchange exchange) throws IOException {
            List<Epic> epicList = manager.getEpics();
            if (!epicList.isEmpty()) {
                writeResponse(exchange, gson.toJson(epicList), 200);
            } else writeResponse(exchange, "Список эпиков пуст", 204);
        }

        private void handleGetEpicById(HttpExchange exchange) throws IOException {
            try {
                writeResponse(exchange, gson.toJson(manager.getEpicById(taskId)), 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, exception.getMessage(), 204);
            }
        }

        private void handlePostEpicById(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
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
            }
        }

        private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
            manager.deleteAllEpics();
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
        // обработка запросов Subtasks
        private Endpoint processRequestSubtaskData(HttpExchange exchange,
                                                String requestMethod,
                                                boolean isContainsRequest,
                                                boolean isContainsId) throws IOException {
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
            } return UNKNOWN;
        }

        private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
            List<Subtask> subtaskList = manager.getSubtasks();
            if (!subtaskList.isEmpty()) {
                writeResponse(exchange, gson.toJson(subtaskList), 200);
            } else writeResponse(exchange, "Список подзадач пуст", 204);
        }

        private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
            try {
                writeResponse(exchange, gson.toJson(manager.getSubTaskById(taskId)), 200);
            } catch (IllegalArgumentException exception) {
                writeResponse(exchange, exception.getMessage(), 204);
            }
        }

        private void handlePostSubtaskById(HttpExchange exchange) throws IOException {
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

        private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
            manager.deleteAllTasks();
            writeResponse(exchange, "Все задачи удалены", 200);
        }

        private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
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
