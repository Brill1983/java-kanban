package service;

public class Managers {

    public static TaskManager getDefaultInMemoryManager() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
