package service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer {

    public TaskManager fileBackedTasksManager;

    public HttpTaskServer() {
        this.fileBackedTasksManager = FileBackedTasksManager.loadFromFile(Paths.get("test.csv").toFile());
    }

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new Gson();


    public static void create() {
//        TaskManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(Paths.get("test.csv").toFile());

        try {
            HttpServer httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TaskHandler());
            httpServer.start();

            System.out.println("Starting server at port: " + PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private static class TaskHandler implements HttpHandler {
        TaskManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(Paths.get("test.csv").toFile());
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            int choice = endpoint(path, method, body, query);

            String response = "Обрабатываем запрос от клиента";

            switcher(choice, exchange, fileBackedTasksManager);

            exchange.sendResponseHeaders(200, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private void switcher(int choice, HttpExchange exchange, TaskManager fileBackedTasksManager) {
            switch (choice) {
                case 1: outputAll(exchange, fileBackedTasksManager); break;
                case 2: outputAllTasks(exchange, fileBackedTasksManager); break;
                case 3: outputAllEpics(exchange, fileBackedTasksManager); break;
                case 4: outputAllSubTasks(exchange, fileBackedTasksManager); break;
                case 5: writeResponse(exchange, "Неверно составлен запрос", 400);
                case 6: receiveTaskById(exchange, fileBackedTasksManager); break;
                case 7: receiveEpicById(exchange, fileBackedTasksManager); break;
                case 8: receiveSubTaskById(exchange, fileBackedTasksManager); break;
                case 9: createNewTask(exchange, fileBackedTasksManager); break;
                case 10: createNewEpic(exchange, fileBackedTasksManager); break;
                case 11: createNewSubTask(exchange, fileBackedTasksManager); break;
                case 12: changeTask(exchange, fileBackedTasksManager); break;
                case 13: changeEpic(exchange, fileBackedTasksManager); break;
                case 14: changeSubTask(exchange, fileBackedTasksManager); break;
                case 15: removeAll(exchange, fileBackedTasksManager); break;
                case 16: removeAllTasks(exchange, fileBackedTasksManager); break;
                case 17: removrAllEpics(exchange, fileBackedTasksManager); break;
                case 18: removeAllSubTasks(exchange, fileBackedTasksManager); break;
                case 19: removeTaskById(exchange, fileBackedTasksManager); break;
                case 20: removeEpicById(exchange, fileBackedTasksManager); break;
                case 21: removeSubTaskById(exchange, fileBackedTasksManager); break;
                case 22: receiveHistory(exchange, fileBackedTasksManager); break;
            }

        }

        private void changeSubTask(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            InputStream inputStream = exchange.getRequestBody();
            try {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                SubTask subtask = gson.fromJson(body, SubTask.class);
                fileBackedTasksManager.updateSubTask(subtask);
                writeResponse(exchange, "Подзадача изменена", 201);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорреткный JSON", 400);
            }
        }

        private void changeEpic(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            InputStream inputStream = exchange.getRequestBody();
            try {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Epic epic = gson.fromJson(body, Epic.class);
                fileBackedTasksManager.updateEpic(epic);
                writeResponse(exchange, "Эпик изменен", 201);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорреткный JSON", 400);
            }
        }

        private void changeTask(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            InputStream inputStream = exchange.getRequestBody();
            try {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Task task = gson.fromJson(body, Task.class);
                fileBackedTasksManager.updateTask(task);
                writeResponse(exchange, "Задача изменена", 201);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорреткный JSON", 400);
            }
        }

        private void createNewSubTask(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            InputStream inputStream = exchange.getRequestBody();
            try {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                SubTask subtask = gson.fromJson(body, SubTask.class);
                fileBackedTasksManager.createTask(subtask);
                writeResponse(exchange, "Подзадача добалена", 201);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорреткный JSON", 400);
            }
        }

        private void createNewEpic(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            InputStream inputStream = exchange.getRequestBody();
            try {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Epic epic = gson.fromJson(body, Epic.class);
                fileBackedTasksManager.createEpic(epic);
                writeResponse(exchange, "Эпик добален", 201);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорреткный JSON", 400);
            }
        }

        private void createNewTask(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            InputStream inputStream = exchange.getRequestBody();
            try {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Task task = gson.fromJson(body, Task.class);
                fileBackedTasksManager.createTask(task);
                writeResponse(exchange, "Задача добалена", 201);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Получен некорреткный JSON", 400);
            }
        }

        private void receiveSubTaskById(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String query = exchange.getRequestURI().getQuery();
            String[] queryParts = query.split("=");
            String stringJson = gson.toJson(fileBackedTasksManager.getSubTaskById(Integer.parseInt(queryParts[1])));
            writeResponse(exchange, stringJson, 200);
        }

        private void receiveEpicById(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String query = exchange.getRequestURI().getQuery();
            String[] queryParts = query.split("=");
            String stringJson = gson.toJson(fileBackedTasksManager.getEpicById(Integer.parseInt(queryParts[1])));
            writeResponse(exchange, stringJson, 200);
        }

        private void receiveTaskById(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String query = exchange.getRequestURI().getQuery();
            String[] queryParts = query.split("=");
            String stringJson = gson.toJson(fileBackedTasksManager.getTaskById(Integer.parseInt(queryParts[1])));
            writeResponse(exchange, stringJson, 200);
        }

        private void outputAllSubTasks(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String stringJson = gson.toJson(fileBackedTasksManager.getAllSubTasks());
            writeResponse(exchange, stringJson, 200);
        }

        private void outputAllEpics(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String stringJson = gson.toJson(fileBackedTasksManager.getAllEpics());
            writeResponse(exchange, stringJson, 200);
        }

        private void outputAllTasks(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            String stringJson = gson.toJson(fileBackedTasksManager.getAllTasks());
            writeResponse(exchange, stringJson, 200);
        }

        private void outputAll(HttpExchange exchange, TaskManager fileBackedTasksManager) {
            List<Task> list = new ArrayList<>(fileBackedTasksManager.getAllTasks());
            list.addAll(fileBackedTasksManager.getAllEpics());
            list.addAll(fileBackedTasksManager.getAllSubTasks());
            String listJson = gson.toJson(list);
            writeResponse(exchange, listJson, 200);
        }

        private int endpoint(String path, String method, String body, String query) {
            String[] pathParts = path.split("/");
            if (method.equals("GET")) {
                if (pathParts.length == 2) return 1;
                else if (pathParts.length == 3 && query.isEmpty()) {
                    if (pathParts[2].equals("task")) return 2; // получаем список тасков
                    else if (pathParts[2].equals("epic")) return 3; // получаем список эпиков
                    else if (pathParts[2].equals("subTask")) return 4; // получаем список сабтасков
                    else if (pathParts[2].equals("history")) return 22; // получаем историю
                    else return 5; // обшибка - неправильный запрос
                } else if (pathParts.length == 3) {
                    String[] queryParts = query.split("=");
                    if (queryParts.length <= 1 || !queryParts[0].equals("id") || queryParts.length > 2)
                        return 5; // обшибка - неправильный запрос
                    try {
                        Integer.parseInt(queryParts[1]); // проверяем, что второе значение параметра запроса - число.
                        if (pathParts[2].equals("task")) return 6; // получаем таску по ID
                        else if (pathParts[2].equals("epic")) return 7; // получаем эпик по ID
                        else if (pathParts[2].equals("subTask")) return 8; // получаем сабтаску по ID
                        else return 5;// обшибка - неправильный запрос
                    } catch (NumberFormatException e) {
                        return 5; // обшибка - неправильный запрос
                    }
                } else return 5;
            } else if (method.equals("POST")) {
                if (body.isBlank()) return 5; // обшибка - неправильный запрос
                if (pathParts.length == 3 && query.isEmpty()) {
                    if (pathParts[2].equals("task")) return 9; // создаем таску
                    else if (pathParts[2].equals("epic")) return 10; // создаем эпик
                    else if (pathParts[2].equals("subTask")) return 11; // создаем сабтаску
                    else return 5; // обшибка - неправильный запрос
                } else if (pathParts.length == 3) {
                    String[] queryParts = query.split("=");
                    if (queryParts.length <= 1 || !queryParts[0].equals("id") || queryParts.length > 2)
                        return 5; // обшибка - неправильный запрос
                    try {
                        Integer.parseInt(queryParts[1]); // проверяем, что второе значение параметра запроса - число.
                        if (pathParts[2].equals("task")) return 12; // изменяем таску по ID
                        else if (pathParts[2].equals("epic")) return 13; // изменяем эпик по ID
                        else if (pathParts[2].equals("subTask")) return 14; // изменяем сабтаску по ID
                        else return 5;// обшибка - неправильный запрос
                    } catch (NumberFormatException e) {
                        return 5; // обшибка - неправильный запрос
                    }
                } else return 5;
            } else if (method.equals("DELETE")) {
                if (pathParts.length == 2) return 15; // удалить все эпики, таски, сабтаски
                else if (pathParts.length == 3 && query.isEmpty()) {
                    if (pathParts[2].equals("task")) return 16; // удаление всех тасков
                    else if (pathParts[2].equals("epic")) return 17; // удаление всех эпиков
                    else if (pathParts[2].equals("subTask")) return 18; // удаление всех сабтасков
                    else return 5; // обшибка - неправильный запрос
                } else if (pathParts.length == 3) {
                    String[] queryParts = query.split("=");
                    if (queryParts.length <= 1 || !queryParts[0].equals("id") || queryParts.length > 2)
                        return 5; // обшибка - неправильный запрос
                    try {
                        Integer.parseInt(queryParts[1]); // проверяем, что второе значение параметра запроса - число.
                        if (pathParts[2].equals("task")) return 19; // удаляем таску по ID
                        else if (pathParts[2].equals("epic")) return 20; // удаление всех эпик по ID
                        else if (pathParts[2].equals("subTask")) return 21; // удаление всех сабтаску по ID
                        else return 5;// обшибка - неправильный запрос
                    } catch (NumberFormatException e) {
                        return 5; // обшибка - неправильный запрос
                    }
                } else return 5; // обшибка - неправильный запрос
            }
            return 5; // обшибка - неправильный запрос
        }

        private void writeResponse(HttpExchange exchange, String responseString, int responseCode) {
            if (responseString.isBlank()) {
                try {
                    exchange.sendResponseHeaders(responseCode, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                try {
                    exchange.sendResponseHeaders(responseCode, bytes.length);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            exchange.close();
        }
    }
}
