package service;

public class Managers {

    public static TaskManager getDefault() { //сделал метод статическим
        return new InMemoryTaskManager(getDefaultHistory());
//        return new InMemoryTaskManager(new InMemoryHistoryManager()); /* равно возможный вариант - мы указываем на
//        основе какой реализацииции интерфейса HistoryManager (здесь это InMemoryHistoryManager нам реализовать
//        InMemoryTaskManager или любой другой будущий класс интерфейса TaskManager. */
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
