package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> history = new ArrayList<>(10); // инициировал, конструктор удалил

    @Override
    public void add(Task task) {
        if(history.size() == 10) { //доработал if - убрал else
            history.remove(0);
        }
        history.add(task); // вынес добавление после if
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
