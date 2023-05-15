package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
import model.Task;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient client;

    Gson gson = new Gson();

    public HttpTaskManager(String url) {
        super(Managers.getDefaultHistory(), Path.of("test.csv"));
        this.client = new KVTaskClient(url);
    }

    @Override
    public void save() {
        String tasksJson = gson.toJson(new ArrayList<>(tasks.values()));
        String epicJson = gson.toJson(new ArrayList<>(epics.values()));
        String subTaskJson = gson.toJson(new ArrayList<>(subTasks.values()));
        String historyJson = gson.toJson(new ArrayList<>(getHistory()));
//        String priorTasksJson = gson.toJson(new ArrayList<>(getPrioritizedTasks()));
        client.put("tasks", tasksJson);
        client.put("epics", epicJson);
        client.put("subtasks", subTaskJson);
        client.put("history", historyJson);
//        client.put("prioritizedTasks", priorTasksJson);
    }

    public void load() {
        String responseTasks = client.load("tasks");
        String responseEpic = client.load("epics");
        String responseSubTasks = client.load("subtasks");
        String responseHistory = client.load("history");
        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> tasksList = gson.fromJson(responseTasks, taskType);
        Type epicType = new TypeToken<ArrayList<Epic>>(){}.getType();
        List<Epic> epicList = gson.fromJson(responseEpic, epicType);
        Type subTaskType = new TypeToken<ArrayList<SubTask>>(){}.getType();
        List<SubTask> subTaskList = gson.fromJson(responseSubTasks, subTaskType);

        List<Task> historyList = gson.fromJson(responseHistory, taskType);
    }
}
