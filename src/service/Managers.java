package service;

public class Managers {

    public static TaskManager getDefault() { //сделал метод статическим
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
