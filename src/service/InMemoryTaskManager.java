package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;
    private HistoryManager historyManager;
    int seq = 0;
    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
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
    public Task createTask(Task task) { //моздание Таска
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
        Epic saved = subTask.getEpic();
        if (!epics.containsValue(saved)) { //проверка на наличие эпиков к которым относятся саб таски
            System.out.println("No such epic in Hash Map");
            return null;
        }
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(saved.getId()); // вынимаем из мапы эпик, по id эпика, который пришел с сабтаской
        epic.setStatus(calculateStatus(saved)); // расчитываем статус

        List<SubTask> subTaskList = epic.getSubTasks(); // вынимаем из эпика список сабтасков
        subTaskList.add(subTask); // добавляем в список эпика сабтаску
        epic.setSubTasks(subTaskList); // сохраняем обновленный список сабтасков в эпик
        epics.put(epic.getId(), epic); //записываем эпик в мапу на прежнее место
        return subTask;
    }

    @Override
    public Task getTaskById(int id) { //получение Таска по индентификатору
        Task task = tasks.get(id);
        historyManager.add(task);
//        historyRecord(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) { //получение Эпика по индентификатору
        Epic epic = epics.get(id);
        historyManager.add(epic);
//        historyRecord(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) { //получение СабТаска по индентификатору
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
//        historyRecord(subTask);
        return subTask;
    }

    @Override
    public void updateTask(Task task) { //обновление Таска
        tasks.put(task.getId(), task);
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
        epics.put(saved.getId(), saved);
    }

    @Override
    public void updateSubTask(SubTask subTask) { //обновление СабТаски
        SubTask saved = subTasks.get(subTask.getId()); // из хранилиза СабТасков вынули равную по ID с входящей
        saved.setName(subTask.getName());
        saved.setDescription(subTask.getDescription());
        saved.setStatus(subTask.getStatus());
        Epic previousEpic = saved.getEpic(); // отдельно сохранили Эпик выгруженной из хранилища СабТаски
        Epic epic = subTask.getEpic(); // получили эпик из входящей СабТаски
        Epic savedEpic = epics.get(epic.getId()); // проверяем что такой эпик есть в хранилище Эпиков
        if (savedEpic == null) {
            System.out.println("The epic for subtask has incorrect number");
            return;
        }
        saved.setEpic(epic); // присвоили Эпик из входящей СабТаски в выгруженную СабТаску из хранилища
        subTasks.put(subTask.getId(), saved);// записали выгруженную СабаТаску с измененными полями обартно в хранилище под тем же ID
        // теперь надо у Эпика пришедшего с задачей и у эпика старой СабТаски пересчитать статус и перезаписать список
        savedEpic.setStatus(calculateStatus(savedEpic));
        savedEpic.setSubTasks(createList(savedEpic));
        epics.put(epic.getId(), savedEpic);
        previousEpic.setStatus(calculateStatus(previousEpic));
        previousEpic.setSubTasks(createList(previousEpic));
        epics.put(previousEpic.getId(), previousEpic);

    }

    @Override
    public void deleteTaskById(int id) { //удаление Таски по идентификатору
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) { //удаление Эпика по идентификатору
        Epic saved = epics.get(id); //выгружаем Эпик из хранилища в переменную
        ArrayList<Integer> index = new ArrayList<>(); //создаем список ключей СабТасков из Мапы, в которых есть удаляемый Эпик
        for (Integer key : subTasks.keySet()) {
            if (subTasks.get(key).getEpic().equals(saved)) {
                index.add(key); //собираем список ключей
            }
        }
        for (Integer subTaskIdForDelete : index) { //обходим список ключей и по каждому удаляем СабТаску
            subTasks.remove(subTaskIdForDelete);
        }
        epics.remove(id); //удалчем Эпик
    }

    @Override
    public void deleteSubTaskById(int id) { //удаление СабТаски по идентификатору
        Epic savedEpic = subTasks.get(id).getEpic(); //выгружаем Эпик у удаляемой СабТаски
        subTasks.remove(id);
        savedEpic.setStatus(calculateStatus(savedEpic)); //пересчитываем статус Эпика
        savedEpic.setSubTasks(createList(savedEpic)); //пересчитываем список сабстасков Эпика
        epics.put(savedEpic.getId(), savedEpic);
    }

    @Override
    public void deleteAllTasks() { //удаление всех Тасков
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() { //удаление всех Эпиков
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() { //удаление всех СабТасков
        subTasks.clear();
        for (Integer key : epics.keySet()) {
            epics.get(key).setStatus(Status.NEW);
            epics.get(key).setSubTasks(null);
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
    // Далее перечислены вспомогательные методы
    public Status calculateStatus (Epic epic) { //метод расчета статуса Эпика, расчитывается при создании, удалении, изменении СабТасков
        int count = 0;
        int inProgressCount = 0;
        int doneCount = 0;
        for (Integer key: subTasks.keySet()) {
            SubTask subTask = subTasks.get(key);
            Epic saved = subTask.getEpic();
            if (saved.getId() == epic.getId()) { // айди эпиков совпали
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
        List<SubTask> subTaskList = new ArrayList<>();
        for (Integer key : subTasks.keySet()) {
            if (subTasks.get(key).getEpic().equals(epic)) {
                subTaskList.add(subTasks.get(key));
            }
        }
        return subTaskList;
    }
}
