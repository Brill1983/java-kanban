package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {

    TaskManager fileBackedTasksManager = Managers.getDefaultFileManager();

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new Gson();

    //HttpServer httpServer;
    public static void create() {
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
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(),DEFAULT_CHARSET);
            int choice = endpoint(path, method, body);

            String response = "Обрабатываем запрос от клиента";

            exchange.sendResponseHeaders(200,0);
            try(OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    private static int endpoint(String path, String method, String body) {
        String[] pathParts = path.split("/");
        System.out.println(pathParts.length);
        if (method == "GET" && pathParts.length == 2) { // получаем все задачи, эпики и сабтаски
            return 1;
        }
        if (method == "GET" && pathParts.length == 3) {
            if (pathParts[3] == "task") { // получаем список тасков
                return 2;
            } else if (pathParts[3] == "epic") { // получаем список эпиков
                return 3;
            } else if (pathParts[3] == "subTask") { // получаем список сабтасков
                return 4;
            } else {
                System.out.println("Неправильный запрос");
                return 5;
            }
        }
        if (method == "GET" && pathParts.length == 4) {
            String[] value = pathParts[4].split("=");
            // тут остановился
        }


       return 0;
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode,0);
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
