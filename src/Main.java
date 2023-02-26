import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) { // для тестирования
        Scanner scanner = new Scanner(System.in);
        Manager manager = new Manager();
        String command;

        while (true) {
            System.out.println("Какие задачи Вас интересуют? \n 1. Простые\n 2. Эпики\n 3. Подзадачи");
            command = scanner.next();
            if (command.equals("1")) {

                label:
                while (true) {
                    System.out.println("1. Ввести новую простую задачу");
                    System.out.println("2. Вывести список простых задач");
                    System.out.println("3. Удалить все простые задачи");
                    System.out.println("4. Показать задачу по ID");
                    System.out.println("5. Удалить задачу по ID");
                    System.out.println("6. Обновить задачу по ID");
                    System.out.println("0. Выход");
                    command = scanner.next();
                    switch (command) {
                        case "1":
                            manager.makeTask(new Task("Задача 1", "Описание 1"), "NEW");
                            manager.makeTask(new Task("Задача 2", "Описание 2"), "NEW");
                            manager.makeTask(new Task("Задача 3", "Описание 3"), "NEW");
                            break;
                        case "2":
                            manager.getAllTasks();
                            break;
                        case "3":
                            manager.deleteAllTasks();
                            break;
                        case "4": {
                            System.out.println(manager.showTaskById(3));
                            break;
                        }
                        case "5": {
                            manager.deleteTaskById(1);
                            break;
                        }
                        case "6": {
                            Task task = new Task("Задача не из списка", "Новое описание");
                            task.setId(2);
                            task.setStatus("IN_PROGRESS");

                            manager.changeTask(task);
                            break;
                        }
                        case "0":
                            break label;
                    }
                }
            } else if (command.equals("2")) {

                label:
                while (true) {
                    System.out.println("1. Ввести новый эпик");
                    System.out.println("2. Вывести список эпиков");
                    System.out.println("3. Удалить все эпики");
                    System.out.println("4. Показать эпик по ID");
                    System.out.println("5. Удалить эпик по ID");
                    System.out.println("6. Обновить эпик по ID");
                    System.out.println("7. Вывести подчиненные задачи по ID");
                    System.out.println("0. Выход");
                    command = scanner.next();
                    switch (command) {
                        case "1":
                            manager.makeEpic(new Epic("Эпик 1", "Описание 1"));
                            manager.makeEpic(new Epic("Эпик 2", "Описание 2"));
                            manager.makeEpic(new Epic("Эпик 3", "Описание 3"));
                            break;
                        case "2":
                            manager.getAllEpics();
                            break;
                        case "3":
                            manager.deleteAllEpics();
                            break;
                        case "4": {
                            System.out.println(manager.showEpicById(2));
                            break;
                        }
                        case "5": {
                            manager.deleteEpicById(3);
                            break;
                        }
                        case "6": {
                            Epic epic = new Epic("Эпик не из списка", "Новое описание");
                            epic.setId(2);
                            manager.changeEpic(epic);
                            break;
                        }
                        case "7": {

                            ArrayList<SubTask> subTasksList = manager.getSubTaskListById(2);
                            for (SubTask subTask : subTasksList) {
                                System.out.println(subTask);
                            }
                            break;
                        }
                        case "0":
                            break label;
                    }
                }
            } else if (command.equals("3")) {
                label:
                while (true) {
                    System.out.println("1. Ввести новую подзадачу");
                    System.out.println("2. Вывести список подзадач");
                    System.out.println("3. Удалить все подзадачи");
                    System.out.println("4. Показать подзадачу по ID");
                    System.out.println("5. Удалить подзадачу по ID");
                    System.out.println("6. Обновить подзадачу по ID");
                    System.out.println("0. Выход");
                    command = scanner.next();
                    switch (command) {
                        case "1":
                            manager.makeSubTask(new SubTask("Саб 1", "К эпику 2"), 2,"IN_PROGRESS");
                            manager.makeSubTask(new SubTask("Саб 2", "К эпику 2"), 2,"NEW");
                            manager.makeSubTask(new SubTask("Саб 3", "К эпику 1"), 1,"NEW");
                            manager.makeSubTask(new SubTask("Саб 4", "К эпику 3"), 3,"DONE");
                            break;
                        case "2":
                            manager.getAllSubTasks();
                            break;
                        case "3":
                            manager.deleteAllSubTasks();
                            break;
                        case "4": {
                            System.out.println(manager.showSubTaskById(2));
                            break;
                        }
                        case "5": {
                            manager.deleteSubTaskById(4);
                            manager.deleteSubTaskById(7);
                            break;
                        }
                        case "6": {
                            SubTask subTask = new SubTask("Саб не из списка", "Новое описание");
                            subTask.setId(5);
                            subTask.setMasterId(2);
                            subTask.setStatus("NEW");
                            manager.changeSubTask(subTask);
                            break;
                        }
                        case "0":
                            break label;
                    }
                }
            }
        }
    }
}