package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Status;
import model.Task;
import org.junit.jupiter.api.*;
import service.Managers;
import service.TaskManager;
import service.UserManager;
import user.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpUserServerTest {

    private HttpUserServer userServer;
    private TaskManager taskManager;
    private UserManager userManager;
    private final Gson gson = Managers.getGson();
    private Task task;
    private User user;

    @BeforeEach
    void init() throws IOException {
        userManager = Managers.getUserDefault();
        taskManager = userManager.getTaskManager();

        userServer = new HttpUserServer(userManager);
        user = new User("Тестов Тест Тестович");
        userManager.add(user);
        task = new Task("Task #1", "Task 1 description",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15), user);
        taskManager.createTask(task);

        userServer.start();
    }

    @AfterEach
    void tearDown() {
        userServer.stop();
    }

    @Test
    void getUsers() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/users");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type userType = new TypeToken<ArrayList<User>>() {
        }.getType();
        List<User> list = gson.fromJson(response.body(), userType);

        assertNotNull(list, "User list is not returning");
        assertEquals(1, list.size(), "Number of user is not correct");
        assertEquals(user, list.get(0), "Users are not equal.");
    }

    @Test
    void getUserById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/users/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type userType = new TypeToken<User>() {
        }.getType();
        User actual = gson.fromJson(response.body(), userType);

        assertNotNull(actual, "User is not returning");
        assertEquals(user, actual, "Users are not equal.");
    }

    @Test
    void getUserTaskList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/users/1/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> list = gson.fromJson(response.body(), taskType);

        assertNotNull(list, "Task list is not returning");
        assertEquals(1, list.size(), "Number of tasks is not correct");
        assertEquals(task, list.get(0), "Tasks are not equal.");
    }

    @Test
    void deleteUserById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/users/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}