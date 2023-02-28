package service;

import model.Epic;
import model.IdGenerator;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private IdGenerator idGenerator = new IdGenerator(); // модификатор доступа сделал private
    private HashMap<Integer, Task> tasks = new HashMap<>(); // модификатор доступа сделал private
    private HashMap<Integer, Epic> epics = new HashMap<>(); // модификатор доступа сделал private
    private HashMap<Integer, SubTask> subTasks = new HashMap<>(); // модификатор доступа сделал private

    public void getAllTasks(){
       for (Integer key: tasks.keySet()) {
           Task task = tasks.get(key);
           System.out.println(task);
       }
    }

    public void getAllEpics(){
        for (Integer key: epics.keySet()) {
            Epic epic = epics.get(key);
            System.out.println(epic);
        }
    }

    public void getAllSubTasks(){
        for (Integer key: subTasks.keySet()) {
            SubTask subTask = subTasks.get(key);
            System.out.println(subTask);
        }
    }

    public void makeTask(Task task, String status){
        task.setId(idGenerator.generateId());
        task.setStatus(status);
        tasks.put(task.getId(), task);
    }

    public void makeEpic(Epic epic) {
        epic.setId(idGenerator.generateId());
        epic.setStatus(setEpicStatus(epic.getId())); // при создании методом определяем статус
        epic.setSubTaskList(makeSubTaskList(epic.getId())); // вносим в поле Эпика список с подзадачами
        epics.put(epic.getId(), epic);
    }

    public void makeSubTask(SubTask subTask, int epicId, String status){
        subTask.setId(idGenerator.generateId());
        subTask.setStatus(status);
        subTask.setEpicId(epicId);
        if (!epics.containsKey(epicId)) { //нельзя создать Подзадачу без Эпика, для этого есть обычные Задачи (model.Task)
            System.out.println("Для подзадачи нет эпика. Введите вначале эпик.");
            return;
        }
        subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(epicId);
            epic.setStatus(setEpicStatus(epicId)); //при добавлении Подзадачи - переопределяем статус у Эпика
            epic.setSubTaskList(makeSubTaskList(epicId)); //при добавлении Подзадачи - переделыв. список Подзадач Эпика
            epics.put(epicId, epic);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Integer key: epics.keySet()) {
            Epic epic = epics.get(key);
            epic.setStatus("NEW"); //при удалении Подзадач переопределяем статусы эпиков
            epic.setSubTaskList(null); // в список подзадач всех Эпиков возвращаем null
            epics.put(key, epic);
        }
    }

    public Task showTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задачи с таким идентификатором нет");
            return null;
        }
        return tasks.get(id);
    }

    public Epic showEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпика с таким идентификатором нет");
            return null;
        }
        return epics.get(id);
    }

    public SubTask showSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            System.out.println("Подзадачи с таким идентификатором нет");
            return null;
        }
        return subTasks.get(id);
    }

    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задачи с таким идентификатором нет");
            return;
        }
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпика с таким идентификатором нет");
            return;
        }
        epics.remove(id);
    }

    public void deleteSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            System.out.println("Подзадачи с таким идентификатором нет");
            return;
        }
        SubTask subTask = subTasks.get(id);
        int epicId = subTask.getEpicId();
        subTasks.remove(id);
            Epic epic = epics.get(epicId);
            epic.setStatus(setEpicStatus(epicId)); // после удаления Подзадачи, переопределяем статусы Эпика
            epic.setSubTaskList(makeSubTaskList(epicId)); // после удаления Подзадачи, меняем список подзадач Эпика
            epics.put(epicId, epic);
    }

    public void changeTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
    }

    public void changeEpic(Epic epic) {
        int id = epic.getId();
        epic.setStatus(setEpicStatus(id)); //после изменения Эпика, переопределяем статус (возможно лишее)
        epic.setSubTaskList(makeSubTaskList(id)); //после изменения Эпика, переопредел. список подзадач (возможно лишее)
        epics.put(id, epic);
    }

    public void changeSubTask(SubTask subTask) {
        int id = subTask.getId();
        int epicId = subTask.getEpicId();
        subTasks.put(id, subTask);
            Epic epic = epics.get(epicId);
            epic.setStatus(setEpicStatus(epicId)); // после изменения Подзадачи, переопределяем статус Эпика
            epic.setSubTaskList(makeSubTaskList(epicId)); // после измен. Подзадачи, переопредел. список Подзадач Эпика
            epics.put(epicId, epic);
    }

    public String setEpicStatus(int id) {
        int count = 0;
        int newCount = 0;
        int doneCount = 0;
        for (Integer key: subTasks.keySet()) {
            SubTask subTask = subTasks.get(key);
            if (subTask.getEpicId() == id) {
                count++;
                if (subTask.getStatus().equals("IN_PROGRESS")){
                    newCount++;
                } else if (subTask.getStatus().equals("DONE") ) {
                    doneCount++;
                }
            }
        }
        if (count == 0) {
            return "NEW";
        }
        if (count == doneCount) {
            return "DONE";
        }
        if (newCount != 0 || doneCount != 0) {
            return "IN_PROGRESS";
        }
        return "NEW";
    }

    public ArrayList<SubTask> makeSubTaskList(int id) { //возвращаем список с Подзадачами Эпика с указанным ID
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        for (Integer key: subTasks.keySet()) {
            SubTask subTask = subTasks.get(key);
            if (subTask.getEpicId() == id) {
                subTaskList.add(subTask);
            }
        }
        return subTaskList;
    }

    public ArrayList<SubTask> getSubTaskListById(int id) { // запрашиваем геттером список Подзадач у Эпика с id
        if (!epics.containsKey(id)) {
            System.out.println("Эпика с таким идентификатором нет");
            return null;
        }
        Epic epic = epics.get(id);
        return epic.getSubTaskList(); // если подзадач нет - то он вернет Null
    }
}
