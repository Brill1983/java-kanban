package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks(); //получить список Тасков

    List<Epic> getAllEpics(); //получить список Эпиков

    List<SubTask> getAllSubTasks(); //получить список Сабтасков

    Task createTask(Task task); //создание Таска

    Epic createEpic(Epic epic); //создание Эпика

    SubTask createSubTask(SubTask subTask);  //создание СабТаска

    Task getTaskById(int id); //получение Таска по индентификатору

    Epic getEpicById(int id); //получение Эпика по индентификатору

    SubTask getSubTaskById(int id); //получение СабТаска по индентификатору

    void updateTask(Task task, Status status); //обновление Таска

    void updateEpic(Epic epic); //обновление Эпика

    void updateSubTask(SubTask subTask, Status status); //обновление СабТаски

    void deleteTaskById(int id); //удаление Таски по идентификатору

    void deleteEpicById(int id); //удаление Эпика по идентификатору

    void deleteSubTaskById(int id); //удаление СабТаски по идентификатору

    void deleteAllTasks(); //удаление всех Тасков

    void deleteAllEpics(); //удаление всех Эпиков

    void deleteAllSubTasks(); //удаление всех СабТасков

    List<SubTask> getSubTaskList(Epic epic); //получение списка СабТасков принимаемого Эпика

    List<Task> getHistory(); //нужен ли этот метод здесь, ели он уже присутствует в интерфейсе HistoryManagers?
}
