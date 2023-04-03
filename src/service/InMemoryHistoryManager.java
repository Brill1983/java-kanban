package service;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

//    private List<Task> history = new ArrayList<>(10); // инициировал, конструктор удалил
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;
    private HashMap<Integer, Node> idToNode = new HashMap<>();

    public void removeNode(Node<Task> node) {
        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;
        if (prevNode != null) {
            prevNode.next = nextNode;
        }
        if (nextNode != null) {
            nextNode.prev = prevNode;
        }
        if (node.task.getId() == head.task.getId()) {
            Node<Task> newHead = head.next;
            head = newHead;
            head.prev = null;
        }
        node = null;
    }
    public void linkLast(Task task) {
        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        size ++;
        int id = task.getId();
        idToNode.put(id, newNode);
    }
    public List<Task> getTasks(){
        List<Task> history = new ArrayList<>();
        Node<Task> node = head;
//        while(node.next != null) {
        while(true) {
            history.add(node.task);
            Node<Task> oldNode = node;
            if (node.next == null) {
                return history;
            }
            node = oldNode.next;
        }
//        return history;
    }
    @Override
    public void add(Task task) {
        int id = task.getId();
        if (idToNode.containsKey(id)){
            Node<Task> node = idToNode.get(id);

            linkLast(task);
            removeNode(node);
        } else {
            linkLast(task);
            size ++;
        }
    }
//    @Override
//    public void add(Task task) {
//        if(history.size() == 10) { //доработал if - убрал else
//            history.remove(0);
//        }
//        history.add(task); // вынес добавление после if
//    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (!idToNode.containsKey(id)) {
            System.out.println("No such task un history");
            return;
        }
        Node<Task> node = idToNode.get(id);
        removeNode(node);
        idToNode.remove(id);
    }
}
