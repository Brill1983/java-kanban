import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    Counter counter = new Counter();
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();

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
        task.setId(counter.count());
        task.setStatus(status);
        tasks.put(task.getId(), task);
    }

    public void makeEpic(Epic epic) {
        epic.setId(counter.count());
        epic.setStatus(setEpicStatus(epic.getId())); // при создании методом определяем статус
        epics.put(epic.getId(), epic);
    }

    public void makeSubTask(SubTask subTask, int masterId, String status){
        subTask.setId(counter.count());
        subTask.setStatus(status);
        subTask.setMasterId(masterId);
        if (!epics.containsKey(masterId)) { //нельзя создать Подзадачу без Эпика, для этого есть обычные Задачи (Task)
            System.out.println("Для подзадачи нет эпика. Введите вначале эпик.");
            return;
        }
        subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(masterId);
            epic.setStatus(setEpicStatus(masterId)); // при добавлении новой Подзадачи - переопределяем статус у эпика.
            epics.put(masterId, epic);
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
        int masterId = subTask.getMasterId();
        subTasks.remove(id);
            Epic epic = epics.get(masterId);
            epic.setStatus(setEpicStatus(masterId)); // после удаления Подзадачи, переопределяем статусы Эпика
            epics.put(masterId, epic);
    }

    public void changeTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
    }

    public void changeEpic(Epic epic) {
        int id = epic.getId();
        epic.setStatus(setEpicStatus(id)); // после изменения Эпика, переопределяем статус (возможно это лишее)
        epics.put(id, epic);
    }

    public void changeSubTask(SubTask subTask) {
        int id = subTask.getId();
        int masterId = subTask.getMasterId();
            Epic epic = epics.get(masterId);
            epic.setStatus(setEpicStatus(id)); // после изменения Подзадачи, переопределяем статус
            epics.put(masterId, epic);
        subTasks.put(id, subTask);
    }

    public String setEpicStatus(int id) {
        int count = 0;
        int newCount = 0;
        int doneCount = 0;
        for (Integer key: subTasks.keySet()) {
            SubTask subTask = subTasks.get(key);
            if (subTask.getMasterId() == id) {
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
        if (newCount != 0) {
            return "IN_PROGRESS";
        }
        return "NEW";
    }

    public ArrayList<SubTask> getSubTaskListById(int id) { //возвращаем список с Подзадачами Эпика с указанным ID
        if (!epics.containsKey(id)) {
            System.out.println("Эпика с таким идентификатором нет");
            return null;
        }
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        for (Integer key: subTasks.keySet()) {
            SubTask subTask = subTasks.get(key);
            if (subTask.getMasterId() == id) {
                subTaskList.add(subTask);
            }
        }
        return subTaskList;
    }
}
