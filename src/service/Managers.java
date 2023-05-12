package service;

import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFileManager() {
        return new FileBackedTasksManager(getDefaultHistory(), Paths.get("test.csv"));
    }

}
