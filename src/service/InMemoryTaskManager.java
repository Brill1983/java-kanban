package service;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, SubTask> subTasks;
    protected HistoryManager historyManager;
    protected int seq = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = historyManager;
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
        Task saved = tasks.get(task.getId());
        saved.setName(task.getName());
        saved.setStatus(status);
        saved.setDescription(task.getDescription());
        saved.setStartTime(task.getStartTime());
        saved.setDuration(task.getDuration());
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
        SubTask saved = subTasks.get(subTask.getId()); // из хранилиза СабТасков вынули равную по ID с входящей
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

//        Epic previousEpic = epics.get(oldEpicId); // отдельно сохранили Эпик выгруженной из хранилища СабТаски
//        Epic epic = epics.get(newEpicId); // получили эпик из входящей СабТаски
//        Epic savedEpic = epics.get(epic.getId()); // проверяем что такой эпик есть в хранилище Эпиков
//        if (savedEpic == null) {
//            System.out.println("The epic for subtask has incorrect number");
//            return;
//        }
//        saved.setEpic(epic); // присвоили Эпик из входящей СабТаски в выгруженную СабТаску из хранилища
//        subTasks.put(subTask.getId(), saved);// записали выгруженную СабаТаску с измененными полями обартно в хранилище под тем же ID
//         теперь надо у Эпика пришедшего с задачей и у эпика старой СабТаски пересчитать статус и перезаписать список
//        savedEpic.setStatus(calculateStatus(savedEpic));
//        savedEpic.setSubTasks(createList(savedEpic));
//        epics.put(epic.getId(), savedEpic);
//        previousEpic.setStatus(calculateStatus(previousEpic));
//        previousEpic.setSubTasks(createList(previousEpic));
//        epics.put(previousEpic.getId(), previousEpic);

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
}
