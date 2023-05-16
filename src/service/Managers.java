package service;

public class Managers {

    public static TaskManager getDefaultInMemoryManager() {
        TaskManager manager = new InMemoryTaskManager(getDefaultHistory());
        return manager;
    }

    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
