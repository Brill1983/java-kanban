import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.FileBackedTasksManager;
import service.Managers;
import service.TaskManager;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
// Перенося блоки команд в комментарий, можно менять варианты тестирования
//        LocalDateTime startTime = LocalDateTime.of(2023, 05, 8, 01, 00);
//        TaskManager saveInFileByManager = new FileBackedTasksManager(Managers.getDefaultHistory(), Paths.get("test.csv"));
//        Task task1 = saveInFileByManager.createTask(new Task("Task #1", "DT", startTime, Duration.ofDays(2)));
//        Task task2 = saveInFileByManager.createTask(new Task("Task #2", "DT", LocalDateTime.of(2023, 04, 30, 21, 30), Duration.ofMinutes(35)));
//        Task task3 = saveInFileByManager.createTask(new Task("Task #3", "DT", startTime.plusHours(12), Duration.ofHours(10)));
//        Epic epic4 = saveInFileByManager.createEpic(new Epic("Epic #4", "DE"));
//        Epic epic5 = saveInFileByManager.createEpic(new Epic("Epic #5", "DE"));
//        Epic epic6 = saveInFileByManager.createEpic(new Epic("Epic #6", "DE"));
//        SubTask subTask7 = saveInFileByManager.createSubTask(new SubTask("SubTask #7", "DS", 5, startTime, Duration.ofMinutes(10)));
//        SubTask subTask8 = saveInFileByManager.createSubTask(new SubTask("SubTask #8", "DS", 6, startTime.plusMinutes(30), Duration.ofMinutes(10)));
//        SubTask subTask9 = saveInFileByManager.createSubTask(new SubTask("SubTask #9", "DS", 6, startTime.plusMinutes(35), Duration.ofMinutes(10)));
//        SubTask subTask10 = saveInFileByManager.createSubTask(new SubTask("SubTask #10", "DS", 6, startTime, Duration.ofMinutes(20)));
////        SubTask subTask11 = saveInFileByManager.createSubTask(new SubTask("SubTask #11", "DS", 4));
//
//        System.out.println("Get Task, Epic, SubTask by ID");
//        System.out.println(saveInFileByManager.getTaskById(1));
//        System.out.println(saveInFileByManager.getTaskById(3));
//        System.out.println(saveInFileByManager.getEpicById(4));
//        System.out.println(saveInFileByManager.getSubTaskById(7));
//        System.out.println(saveInFileByManager.getSubTaskById(8));
//        System.out.println(saveInFileByManager.getSubTaskById(9));
//        System.out.println(saveInFileByManager.getSubTaskById(10));
//        System.out.println();
//
//        System.out.println("Get history:");
//        List<Task> history = saveInFileByManager.getHistory();
//        for (Task task : history) {
//            System.out.println(task);
//        }
//
//        TaskManager loadFromFileByManager = FileBackedTasksManager.loadFromFile(new File("test.csv"));
//
//        System.out.println(loadFromFileByManager.getAllTasks());
//        System.out.println(loadFromFileByManager.getAllEpics());
//        System.out.println(loadFromFileByManager.getAllSubTasks());
//        System.out.println("Get history:");
//
//        List<Task> history1 = loadFromFileByManager.getHistory();
//        for (Task task : history1) {
//            System.out.println(task);
//        }

        TaskManager taskManager = Managers.getDefault(); // получаем объект через метод класса Managers

        LocalDateTime startTime = LocalDateTime.of(2023, 05, 8, 01, 00);

        System.out.println("Create Tasks, Epics, SubTasks");
        Task task1 = taskManager.createTask(new Task("Task #1", "DT", startTime, Duration.ofMinutes(9)));
        Task task2 = taskManager.createTask(new Task("Task #2", "DT", startTime.plusMinutes(50), Duration.ofMinutes(9)));
        Task task3 = taskManager.createTask(new Task("Task #3", "DT", startTime.plusMinutes(60), Duration.ofMinutes(9)));
//        System.out.println(task1.getStartTime() + " -- " + task1.getEndTime());
//        System.out.println(task2.getStartTime() + " -- " + task2.getEndTime());
//        System.out.println(task3.getStartTime() + " -- " + task3.getEndTime());
        Epic epic4 = taskManager.createEpic(new Epic("Epic #4", "DE"));
        Epic epic5 = taskManager.createEpic(new Epic("Epic #5", "DE"));
        Epic epic6 = taskManager.createEpic(new Epic("Epic #6", "DE"));

        SubTask subTask7 = taskManager.createSubTask(new SubTask("SubTask #7", "DS", 5, startTime.minusMinutes(10), Duration.ofMinutes(9)));
        SubTask subTask8 = taskManager.createSubTask(new SubTask("SubTask #8", "DS", 6, startTime.plusMinutes(10), Duration.ofMinutes(19)));
        SubTask subTask9 = taskManager.createSubTask(new SubTask("SubTask #9", "DS", 6, startTime.plusMinutes(30), Duration.ofMinutes(7)));
        SubTask subTask10 = taskManager.createSubTask(new SubTask("SubTask #10", "DS", 6, null, Duration.ofMinutes(20)));
//
//        System.out.println(taskManager.getEpicById(6).getStartTime() + "---" + taskManager.getEpicById(6).getEndTime() + " --- " + taskManager.getEpicById(6).getDuration().toMinutes());

//        System.out.println("Update task");
//        Task correctedTask = new Task(3, "Task #3.1", "new DT", null, null);
//        System.out.println(taskManager.getTaskById(3));
//        taskManager.updateTask(correctedTask, Status.IN_PROGRESS);
//        System.out.println();
//        System.out.println(taskManager.getTaskById(3));
//        System.out.println();
//        System.out.println("Update epic");
//        Epic correctedEpic = new Epic(5, "NEW Epic", "NEW DE");
//        System.out.println(taskManager.getEpicById(5));
//        taskManager.updateEpic(correctedEpic);
//        System.out.println();
//        System.out.println(taskManager.getEpicById(5));
//        System.out.println();
//        System.out.println("Update subTask");
//        System.out.println(taskManager.getSubTaskById(10));
//        System.out.println(taskManager.getEpicById(5));
//        System.out.println(taskManager.getEpicById(6));
//        System.out.println();
//        taskManager.updateSubTask(new SubTask(10, "SubTask #4444", "DE", 5, null, null), Status.IN_PROGRESS);

//        List<Task> priorTaskList = taskManager.getPrioritizedTasks();
//        for (Task task : priorTaskList) {
//            System.out.println(task);
//        }
//        System.out.println(taskManager.getEpicById(5).getStartTime() + "---" + taskManager.getEpicById(5).getEndTime() + " --- " + taskManager.getEpicById(5).getDuration().toMinutes());
//        System.out.println(taskManager.getEpicById(6).getStartTime() + "---" + taskManager.getEpicById(6).getEndTime() + " --- " + taskManager.getEpicById(6).getDuration().toMinutes());
//        System.out.println(taskManager.getSubTaskById(10));
        System.out.println(taskManager.getEpicById(5));
        System.out.println(taskManager.getEpicById(6));
//        System.out.println();
//        System.out.println("Get Task, Epic, SubTask by ID");
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getTaskById(3));
//        System.out.println(taskManager.getEpicById(4));
//
        System.out.println(taskManager.getSubTaskById(8));
        System.out.println(taskManager.getSubTaskById(9));
        System.out.println(taskManager.getSubTaskById(10));
//        System.out.println();
//        System.out.println("Get all Tasks, Epics, SubTasks");
//        System.out.println(taskManager.getAllTasks());
//        System.out.println(taskManager.getAllEpics());
//        System.out.println(taskManager.getAllSubTasks());
//        System.out.println();
//        System.out.println("Remove Task, Epic, SubTask by id");
//        taskManager.deleteTaskById(2);
//        System.out.println(taskManager.getAllTasks());
//        System.out.println();
//
//        taskManager.deleteEpicById(6);
//        System.out.println();
//        System.out.println(taskManager.getAllEpics());
//        System.out.println();
//        System.out.println(taskManager.getAllSubTasks());
//        taskManager.deleteSubTaskById(8);
//        taskManager.deleteSubTaskById(10);
//        System.out.println(taskManager.getAllSubTasks());
//        System.out.println();
//        System.out.println(taskManager.getEpicById(6));
//        System.out.println();
        System.out.println("Delete all tasks");
//        System.out.println(taskManager.getAllTasks());
//        taskManager.deleteAllTasks();
//        System.out.println(taskManager.getAllTasks());
//        System.out.println();
//        System.out.println("Delete all epics");
//        System.out.println(taskManager.getAllEpics());
//        taskManager.deleteAllEpics();
//        System.out.println(taskManager.getAllEpics());
//        System.out.println();
//        System.out.println("Delete all sub tasks");
//        System.out.println(taskManager.getAllSubTasks());
        taskManager.deleteAllSubTasks();
//        System.out.println(taskManager.getAllSubTasks());
//        System.out.println();
//        System.out.println(taskManager.getAllEpics());
//        System.out.println();
//        System.out.println("Get SubTasks list from Epic by id");
//        System.out.println(taskManager.getSubTaskList(epic5));
//        System.out.println(taskManager.getSubTaskList(epic6));
//
//        System.out.println("Get history:");
//        List<Task> history = taskManager.getHistory();
//        for (Task task : history) {
//            System.out.println(task);
//        }
//        taskManager.deleteEpicById(6);
//        System.out.println();
//        System.out.println("Get history:");
//
//        List<Task> history1 = taskManager.getHistory();
//        for (Task task : history1) {
//            System.out.println(task);
//        }

    }
}