package service;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, SubTask> subTasks;
    protected HistoryManager historyManager;
    protected Set<Task> prioritizedTasks;
    protected int seq = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = historyManager;
        this.prioritizedTasks = new TreeSet<>(taskComparator);
    }

    private int generateId() { //генератор id
        return ++seq;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getAllTasks() { //получить список Тасков
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() { //получить список Эпиков
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() { //получить список Сабтасков
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Task createTask(Task task) { //создание Таска
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) { //создание Эпика
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) { //создание СабТаска
        int saved = subTask.getEpic();
        if (!epics.containsKey(saved)) { //проверка на наличие эпиков к которым относятся саб таски
            System.out.println("No such epic in Hash Map");
            return null;
        }
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(saved); // вынимаем из мапы эпик, по id эпика, который пришел с сабтаской
        epic.setStatus(calculateStatus(saved)); // расчитываем статус

        List<SubTask> subTaskList = epic.getSubTasks(); // вынимаем из эпика список сабтасков
        subTaskList.add(subTask); // добавляем в список эпика сабтаску
        epic.setSubTasks(subTaskList); // сохраняем обновленный список сабтасков в эпик
        createEpicDateTime(subTask, epic);
        epics.put(epic.getId(), epic); //записываем эпик в мапу на прежнее место
        prioritizedTasks.add(subTask);
        return subTask;
    }

    @Override
    public Task getTaskById(int id) { //получение Таска по индентификатору
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) { //получение Эпика по индентификатору
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) { //получение СабТаска по индентификатору
        SubTask subTask = subTasks.get(id);
        if (subTask == null) {
            return null;
        }
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void updateTask(Task task, Status status) { //обновление Таска
        if (!tasks.containsKey(task.getId())) {
            System.out.println("The task has incorrect number");
            return;
        }
        Task saved = tasks.get(task.getId());
        prioritizedTasks.remove(saved);
        saved.setName(task.getName());
        saved.setStatus(status);
        saved.setDescription(task.getDescription());
        saved.setStartTime(task.getStartTime());
        saved.setDuration(task.getDuration());
        prioritizedTasks.add(saved);
    }

    @Override
    public void updateEpic(Epic epic) { //обновление Эпика
        boolean isEpicExists = false;
        for (Integer key : epics.keySet()) { //проверяем, что id воходящего эпика существует в хранилище эпиков
            Epic saved = epics.get(key);
            if (saved.getId() == epic.getId()) {
                isEpicExists = true;
            }
        }
        if (!isEpicExists) {
            System.out.println("No epic with such ID in memory");
            return;
        }
        Epic saved = epics.get(epic.getId()); // статус и список останутся прежними
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubTask(SubTask subTask, Status status) { //обновление СабТаски
        if (!subTasks.containsKey(subTask.getId())) {
            System.out.println("The subtask has incorrect number");
            return;
        }
        SubTask saved = subTasks.get(subTask.getId()); // из хранилища СабТасков вынули равную по ID с входящей
        prioritizedTasks.remove(saved);
        saved.setName(subTask.getName());
        saved.setDescription(subTask.getDescription());
        saved.setStatus(status);
        saved.setStartTime(subTask.getStartTime());
        saved.setDuration(subTask.getDuration());
        int oldEpicId = saved.getEpic();
        int newEpicId = subTask.getEpic();
        if (!epics.containsKey(newEpicId)) {
            System.out.println("The epic for subtask has incorrect number");
            return;
        }
        if (oldEpicId != newEpicId) {
            Epic oldEpic = epics.get(oldEpicId); // эпик по старой СабТаске
            Epic newEpic = epics.get(newEpicId); // эпик по входящей сабтаске
            saved.setEpic(subTask.getEpic());
            oldEpic.setStatus(calculateStatus(oldEpicId));
            newEpic.setStatus(calculateStatus(newEpicId));
            oldEpic.setSubTasks(createList(oldEpic));
            newEpic.setSubTasks(createList(newEpic));
        } else {
            Epic newEpic = epics.get(newEpicId); // эпик по входящей сабтаске
            saved.setEpic(subTask.getEpic());
            newEpic.setStatus(calculateStatus(newEpicId));
            newEpic.setSubTasks(createList(newEpic));
        }
        prioritizedTasks.add(saved);
        System.out.println();
    }

    @Override
    public void deleteTaskById(int id) { //удаление Таски по идентификатору
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) { //удаление Эпика по идентификатору
        if(!epics.containsKey(id)) {
            System.out.println("No such id in epic map");
            return;
        }
        Epic saved = epics.get(id); //выгружаем Эпик из хранилища в переменную
        ArrayList<Integer> index = new ArrayList<>(); //создаем список ключей СабТасков из Мапы, в которых есть удаляемый Эпик
        for (Integer key : subTasks.keySet()) {
            if (subTasks.get(key).getEpic() == (saved.getId())) {
                index.add(key); //собираем список ключей
            }
        }
        for (Integer subTaskIdForDelete : index) { //обходим список ключей и по каждому удаляем СабТаску
            subTasks.remove(subTaskIdForDelete);
            historyManager.remove(subTaskIdForDelete);
        }
        epics.remove(id); //удаляем Эпик
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) { //удаление СабТаски по идентификатору
        if (!subTasks.containsKey(id)) {
            System.out.println("No such id in subTasks storage");
            return;
        }
        int savedEpicId = subTasks.get(id).getEpic(); //выгружаем Эпик у удаляемой СабТаски
        Epic savedEpic = epics.get(savedEpicId);
        subTasks.remove(id);
        savedEpic.setStatus(calculateStatus(savedEpicId)); //пересчитываем статус Эпика
        savedEpic.setSubTasks(createList(savedEpic)); //пересчитываем список сабстасков Эпика
        epics.put(savedEpicId, savedEpic);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() { //удаление всех Тасков
        for(Task task: tasks.values()){
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() { //удаление всех Эпиков
        for(Epic epic: epics.values()){
            historyManager.remove(epic.getId());
        }
        for(SubTask subTask: subTasks.values()){
            historyManager.remove(subTask.getId());
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() { //удаление всех СабТасков
        subTasks.clear();
        for (Integer key : epics.keySet()) {
            epics.get(key).setStatus(Status.NEW);
            epics.get(key).getSubTasks().clear();
            epics.get(key).setStartTime(null);
            epics.get(key).setEndTime(null);
            epics.get(key).setDuration(null);
        }
        for(Task task: historyManager.getHistory()){
            historyManager.remove(task.getId());
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> prioritizedTaskList = new ArrayList<>(prioritizedTasks);
        return prioritizedTaskList;
    }

    @Override
    public List<SubTask> getSubTaskList(Epic epic) { //получение списка СабТасков принимаемого Эпика
        if (!epics.containsValue(epic)) {
            System.out.println("No such Epic in storage");
            return null;
        }
        return epics.get(epic.getId()).getSubTasks();
    }

    public Status calculateStatus (int id) { //метод расчета статуса Эпика, расчитывается при создании, удалении, изменении СабТасков
        int count = 0;
        int inProgressCount = 0;
        int doneCount = 0;
        for (Integer key: subTasks.keySet()) {
            SubTask subTask = subTasks.get(key);
            int saved = subTask.getEpic();
            if (saved == id) { // айди эпиков совпали
                count ++;
                if (subTask.getStatus().equals(Status.IN_PROGRESS)) {
                    inProgressCount ++;
                } else if (subTask.getStatus().equals(Status.DONE)) {
                    doneCount ++;
                }
            }
        }
        if (count == doneCount) {
            return Status.DONE;
        }
        if (count == 0) {
            return Status.NEW;
        }
        if (inProgressCount != 0 || doneCount != 0) {
            return Status.IN_PROGRESS;
        }
        return Status.NEW;
    }

    public List<SubTask> createList(Epic epic) { //пересобираем список СабТасков для Эпика, в случае удаления или изменения СабТаска
        epic.setDuration(null);
        epic.setStartTime(null);
        epic.setEndTime(null);
        List<SubTask> subTaskList = new ArrayList<>();
        for (Integer key : subTasks.keySet()) {
            if (subTasks.get(key).getEpic() == (epic.getId())) {
                subTaskList.add(subTasks.get(key));
                createEpicDateTime(subTasks.get(key), epic);
            }
        }
        return subTaskList;
    }

    public void createEpicDateTime(SubTask subTask, Epic epic) {
        LocalDateTime subTaskStartTime = subTask.getStartTime();
        if (subTaskStartTime == null) {
            return;
        }
        Duration subTaskDuration = subTask.getDuration();
        LocalDateTime subTaskEndTime = subTask.getEndTime();
        if (epic.getStartTime() == null) {
            epic.setStartTime(subTaskStartTime);
            epic.setDuration(subTaskDuration);
            epic.setEndTime(subTaskEndTime);
        } else {
            if (epic.getStartTime().isAfter(subTaskStartTime)) {
                epic.setStartTime(subTaskStartTime);
            }
            if (subTaskEndTime.isAfter(epic.getEndTime())) {
                epic.setEndTime(subTask.getEndTime());
            }
            epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()));
        }
    }

    Comparator<Task> taskComparator = (o1, o2) -> {
        if (o1.getId() == o2.getId()) {
            return 0;
        }
        if (o1.getStartTime() == null) {
            return 1;
        }
        if (o2.getStartTime() == null) {
            return -1;
        }
        if (o1.getStartTime().isBefore(o2.getStartTime())) {
            return -1;
        } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
            return 1;
        } else {
            return o1.getId() - o2.getId();
        }
    };
}
