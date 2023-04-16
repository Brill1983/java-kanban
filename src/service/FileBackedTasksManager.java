package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager{

    protected Path path;

    public FileBackedTasksManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    public void save() { // D:\test.csv
        try {
            FileWriter fileRecord = new FileWriter(path.toString());
            fileRecord.write("id,type,name,status,description,epic\n");
            for(Integer key: tasks.keySet()) {
                fileRecord.write(tasks.get(key).toString() + "\n");
            }
            for(Integer key: epics.keySet()) {
                fileRecord.write(epics.get(key).toString()+ "\n");
            }
            for(Integer key: subTasks.keySet()) {
                fileRecord.write(subTasks.get(key).toString()+ "\n");
            }
            fileRecord.write("\n");
            if(!historyManager.getHistory().isEmpty()) {
                fileRecord.write(historyToString(historyManager));
            }
            fileRecord.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadFromFile(File file) {
        try {
            String fileToString = Files.readString(Path.of(file.toString()));
            String[] lines = fileToString.split("\n");
            for (int i = 1; i < lines.length; i ++) {
                if(!lines[i].isEmpty()) {
                    Task task = fromString(lines[i]);
                    if (String.valueOf(task.getClass()).contains("Task")) {
                        createTask(task);
                    } else if (String.valueOf(task.getClass()).contains("Epic")) {
                        createEpic((Epic) task);
                    } else {
                        createSubTask((SubTask) task);
                    }
                }
               // прошли все задачи - записали в мапы,
               // теперь надо считать историю вызова
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Task fromString(String value) { // нужно подать не пустое значение и не
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        Status status = Status.valueOf(split[3]);
        Task task;
        if (split[1].equals("TASK")) {
            task = new Task(id, split[2], split[4]);
            task.setStatus(status);
        } else if (split[1].equals("EPIC")) {
            task = new Epic(id, split[2], split[4]);
            task.setStatus(status);
        } else if (split[1].equals("SUBTASK")){
            task = new SubTask(id, split[2], split[4], epics.get(Integer.parseInt(split[5])));
            task.setStatus(status);
        } else {
            task = null;
        }
        return task;
    }

    private String historyToString(HistoryManager historyManager) {
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

    public static void main(String[] args) {
    //    input();
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), Paths.get("D:\\test.csv"));
        fileBackedTasksManager.loadFromFile(new File("D:\\test.csv"));
    }

    public static void input() {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), Paths.get("D:\\test.csv"));
        Task task1 = fileBackedTasksManager.createTask(new Task("Task #1", "DT"));
        Task task2 = fileBackedTasksManager.createTask(new Task("Task #2", "DT"));
        Task task3 = fileBackedTasksManager.createTask(new Task("Task #3", "DT"));
        Epic epic4 = fileBackedTasksManager.createEpic(new Epic("Epic #4", "DE"));
        Epic epic5 = fileBackedTasksManager.createEpic(new Epic("Epic #5", "DE"));
        Epic epic6 = fileBackedTasksManager.createEpic(new Epic("Epic #6", "DE"));
        SubTask subTask7 = fileBackedTasksManager.createSubTask(new SubTask("SubTask #7", "DS", epic5));
        SubTask subTask8 = fileBackedTasksManager.createSubTask(new SubTask("SubTask #8", "DS", epic6));
        SubTask subTask9 = fileBackedTasksManager.createSubTask(new SubTask("SubTask #9", "DS", epic6));
        SubTask subTask10 = fileBackedTasksManager.createSubTask(new SubTask("SubTask #10", "DS", epic6));
        SubTask subTask11 = fileBackedTasksManager.createSubTask(new SubTask("SubTask #11", "DS", epic4));

        System.out.println(fileBackedTasksManager.getTaskById(3));
        Task correctedTask = new Task(3, "Task #3.1", "new DT");
        fileBackedTasksManager.updateTask(correctedTask, Status.IN_PROGRESS);
        System.out.println();
        System.out.println(fileBackedTasksManager.getTaskById(3));
        System.out.println();
        System.out.println("Update epic");
        Epic correctedEpic = new Epic(5, "NEW Epic", "NEW DE");
        System.out.println(fileBackedTasksManager.getEpicById(5));
        fileBackedTasksManager.updateEpic(correctedEpic);
        System.out.println();
        System.out.println(fileBackedTasksManager.getEpicById(5));
        System.out.println();
        System.out.println("Update subTask");
        System.out.println(fileBackedTasksManager.getSubTaskById(10));
        System.out.println(fileBackedTasksManager.getEpicById(5));
        System.out.println(fileBackedTasksManager.getEpicById(6));
        System.out.println();
        fileBackedTasksManager.updateSubTask(new SubTask(10, "SubTask #4444", "DE", fileBackedTasksManager.getEpicById(5)), Status.IN_PROGRESS);
        System.out.println(fileBackedTasksManager.getSubTaskById(10));
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
        fileBackedTasksManager.deleteEpicById(6);
        System.out.println();
        System.out.println("Get history:");

        List<Task> history1 = fileBackedTasksManager.getHistory();
        for (Task task : history1) {
            System.out.println(task);
        }


    }

}
