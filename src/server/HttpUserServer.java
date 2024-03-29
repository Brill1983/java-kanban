package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import service.Managers;
import service.TaskManager;
import service.UserManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpUserServer {

    public static final int PORT = 8080;
    private HttpServer server;
    private Gson gson;
    private TaskManager taskManager;
    private UserManager userManager;

    public HttpUserServer() throws IOException {
        this(Managers.getUserDefault());
    }

    public HttpUserServer(UserManager userManager) throws IOException {
        this.userManager = userManager;
        this.taskManager = userManager.getTaskManager();
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/api/v1/users", this::handleUsers);
    }

    private void handleUsers(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/api/v1/users$", path)) {
                        String response = gson.toJson(userManager.getAll());
                        sendText(httpExchange, response);
                        break;
                    }

                    if (Pattern.matches("^/api/v1/users/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/users/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(userManager.getById(id));
                            sendText(httpExchange, response);
                            break;
                        } else {
                            System.out.println("Received id: " + pathId + " is not correct");
                            httpExchange.sendResponseHeaders(405, 0);
                            break;
                        }
                    }
                    if (Pattern.matches("^/api/v1/users/\\d+/tasks$", path)) {
                        String pathId = path.replaceFirst("/api/v1/users/", "")
                                .replaceFirst("/tasks", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(userManager.getUserTask(id));
                            sendText(httpExchange, response);
                            break;
                        } else {
                            System.out.println("Received id: " + pathId + " is not correct");
                            httpExchange.sendResponseHeaders(405, 0);
                            break;
                        }
                    }
                    break;
                }
                case "DELETE": {
// ^ - начало строки, \d - любой цифровой символ, + - значит что предыдущий символ встречается 1 или более раз
// второй \ - экранирует первый слэш,$ - означает конец строки
                    if (Pattern.matches("^/api/v1/users/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/users/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            userManager.delete(id);
                            System.out.println("User " + id + " was deleted");
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            System.out.println("Received id: " + pathId + " is not correct");
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                default: {
                    System.out.println("Waiting for GET or DELETE request, but received: " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }

        } catch (Exception exception) { // попробовать IP Exception
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    public void start() {
        System.out.println("Start UserServer " + PORT);
        System.out.println("http://localhost:" + PORT + "/api/v1/users");
        server.start();
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped ar port " + PORT);
    }
}



