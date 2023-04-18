package service;

import model.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager{

    protected Path path;

    public FileBackedTasksManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    private void save() { // D:\test.csv
        try (FileWriter fileRecord = new FileWriter(path.toString())){
            fileRecord.write("id,type,name,status,description,epic\n");
            for (Integer key: tasks.keySet()) {
                fileRecord.write(tasks.get(key).toString() + "\n");
            }
            for (Integer key: epics.keySet()) {
                fileRecord.write(epics.get(key).toString()+ "\n");
            }
            for (Integer key: subTasks.keySet()) {
                fileRecord.write(subTasks.get(key).toString()+ "\n");
            }
            fileRecord.write("\n");
            if (!historyManager.getHistory().isEmpty()) {
                fileRecord.write(historyToString(historyManager));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка записи в файл", e);
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        Path path = Paths.get(file.toString());
        FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistory(), path);
        fileManager.load();
        return fileManager;
    }

    private void load() {
        int maxId = 0;
        try (BufferedReader reader =new BufferedReader(new FileReader(path.toString()))) {
            reader.readLine();
            while (true) {
                String line = reader.readLine();
                if (line.isEmpty()) {
                    break;
                }
                Task task = fromString(line);
                int id = task.getId();
                if (task.getType() == TaskType.TASK) {
                    tasks.put(id, task);
                } else if (task.getType() == TaskType.EPIC) {
                    epics.put(id, (Epic) task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    subTasks.put(id, (SubTask) task);
                    Epic e = epics.get(subTasks.get(id).getEpic());
                    e.setSubTasks(createList(e));
                }
                if (maxId < id) {
                    maxId = id;
                }
            }

            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                List<Integer> list = historyFromString(line);
                for (Integer id : list) {
                    if (tasks.containsKey(id)) {
                        historyManager.add(tasks.get(id));
                    }
                    if (epics.containsKey(id)) {
                        historyManager.add(epics.get(id));
                    }
                    if (subTasks.containsKey(id)) {
                        historyManager.add(subTasks.get(id));
                    }
                }
            }
            } catch(FileNotFoundException e) {
                throw new RuntimeException("Ошибка! Файл не найден!", e);
            } catch(IOException e) {
                throw new ManagerSaveException("Произошла ошибка чтения из файла, возможно файл поврежден", e);
            }
        seq = maxId;
    }

    public static List<Integer> historyFromString(String value) {
        String[] split = value.split(",");
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < split.length; i++) {
            list.add(Integer.valueOf(split[i]));
        }
        return list;
    }

    public static String historyToString(HistoryManager historyManager) {
        List<Task> list = historyManager.getHistory();
        String record = "";
        int counter = 0;
        for (Task task: list) {
            if(counter < list.size() - 1) {
                record = record + task.getId() + ",";
            } else {
                record = record + task.getId();
            }
            counter ++;
        }
        return record;
    }

    private Task fromString(String value) {
        String[] columns = value.split(",");
        int id = Integer.parseInt(columns[0]);
        Status status = Status.valueOf(columns[3]);
        TaskType type = TaskType.valueOf(columns[1]);
        Task task;
        switch (type) {
            case TASK:
                task = new Task(id, columns[2], columns[4]);
                task.setStatus(status);
                break;

            case EPIC:
                task = new Epic(id, columns[2], columns[4]);
                task.setStatus(status);
                break;

            case SUBTASK:
                task = new SubTask(id, columns[2], columns[4], Integer.parseInt(columns[5]));
                task.setStatus(status);
                break;

            default:
                System.out.println("Unexpected value: " + type);
                task = null;
        }
        return task;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask newSubTask = super.createSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public void updateTask(Task task, Status status) {
        super.updateTask(task, status);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask, Status status) {
        super.updateSubTask(subTask, status);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    public static void main(String[] args) {
        input();
        TaskManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File("test.csv"));

        System.out.println(fileBackedTasksManager.getAllTasks());
        System.out.println(fileBackedTasksManager.getAllEpics());
        System.out.println(fileBackedTasksManager.getAllSubTasks());
        System.out.println("Get history:");

        List<Task> history1 = fileBackedTasksManager.getHistory();
        for (Task task : history1) {
            System.out.println(task);
        }
    }

    public static void input() {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), Paths.get("test.csv"));
        Task task1 = fileBackedTasksManager.createTask(new Task("Task #1", "DT"));
        Task task2 = fileBackedTasksManager.createTask(new Task("Task #2", "DT"));
        Task task3 = fileBackedTasksManager.createTask(new Task("Task #3", "DT"));
        Epic epic4 = fileBackedTasksManager.createEpic(new Epic("Epic #4", "DE"));
        Epic epic5 = fileBackedTasksManager.createEpic(new Epic("Epic #5", "DE"));
        Epic epic6 = fileBackedTasksManager.createEpic(new Epic("Epic #6", "DE"));
        SubTask subTask7 = fileBackedTasksManager.createSubTask(new SubTask("SubTask #7", "DS", 5));
        SubTask subTask8 = fileBackedTasksManager.createSubTask(new SubTask("SubTask #8", "DS", 6));
        SubTask subTask9 = fileBackedTasksManager.createSubTask(new SubTask("SubTask #9", "DS", 6));
        SubTask subTask10 = fileBackedTasksManager.createSubTask(new SubTask("SubTask #10", "DS", 6));
        SubTask subTask11 = fileBackedTasksManager.createSubTask(new SubTask("SubTask #11", "DS", 4));

//        System.out.println(fileBackedTasksManager.getTaskById(3));
//        Task correctedTask = new Task(3, "Task #3.1", "new DT");
//        fileBackedTasksManager.updateTask(correctedTask, Status.IN_PROGRESS);
//        System.out.println();
//        System.out.println(fileBackedTasksManager.getTaskById(3));
//        System.out.println();
//        System.out.println("Update epic");
//        Epic correctedEpic = new Epic(5, "NEW Epic", "NEW DE");
//        System.out.println(fileBackedTasksManager.getEpicById(5));
//        fileBackedTasksManager.updateEpic(correctedEpic);
//        System.out.println();
//        System.out.println(fileBackedTasksManager.getEpicById(5));
//        System.out.println();
//        System.out.println("Update subTask");
//        System.out.println(fileBackedTasksManager.getSubTaskById(10));
//        System.out.println(fileBackedTasksManager.getEpicById(5));
        System.out.println(fileBackedTasksManager.getEpicById(6));
//        System.out.println();
//        fileBackedTasksManager.updateSubTask(new SubTask(10, "SubTask #4444", "DE", 5), Status.IN_PROGRESS);
//        System.out.println(fileBackedTasksManager.getSubTaskById(10));
        System.out.println(fileBackedTasksManager.getEpicById(5));
        System.out.println(fileBackedTasksManager.getEpicById(6));
        System.out.println();
        System.out.println("Get Task, Epic, SubTask by ID");
        System.out.println(fileBackedTasksManager.getTaskById(1));
        System.out.println(fileBackedTasksManager.getTaskById(3));
        System.out.println(fileBackedTasksManager.getEpicById(4));
        System.out.println(fileBackedTasksManager.getSubTaskById(7));
        System.out.println(fileBackedTasksManager.getSubTaskById(8));
        System.out.println(fileBackedTasksManager.getSubTaskById(9));
        System.out.println(fileBackedTasksManager.getSubTaskById(10));
        System.out.println();

        System.out.println("Get history:");
        List<Task> history = fileBackedTasksManager.getHistory();
        for (Task task : history) {
            System.out.println(task);
        }
//        fileBackedTasksManager.deleteEpicById(6);
        System.out.println();
        System.out.println("Get history:");

        List<Task> history1 = fileBackedTasksManager.getHistory();
        for (Task task : history1) {
            System.out.println(task);
        }
    }
}
