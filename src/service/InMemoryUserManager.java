package service;

import model.Task;
import user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryUserManager implements UserManager {

    private final Map<Integer, User> users = new HashMap<>();

    final private TaskManager taskManager = Managers.getDefaultInMemoryManager();

    private int generatedId = 0;

    protected int generateId() {
        return ++generatedId;
    }

    @Override
    public int add(User user) {
        int id = generateId();
        user.setId(id);
        users.put(id, user);
        return id;
    }

    @Override
    public void update(User user) {
        int id = user.getId();
        if (!users.containsKey(id) || (id == 0)) {
            return;
        }
        users.put(id, user);
    }

    @Override
    public User getById(int id) {
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<Task> getUserTask(int id) {
        List<Task> list = new ArrayList<>(taskManager.getAllTasks());
        list.addAll(taskManager.getAllEpics());
        list.addAll(taskManager.getAllSubTasks());
        return list.stream()
                .filter(task -> task.getUser().getId() == id)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(int id) {
        users.remove(id);
    }

    @Override
    public TaskManager getTaskManager() {
        return taskManager;
    }
}
