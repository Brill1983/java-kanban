package service;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private HashMap<Integer, Node> history = new HashMap<>();

    public void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        int id = task.getId();
        history.put(id, newNode);
    }
    private List<Task> getTasks(){
        List<Task> list = new ArrayList<>();
        Node current = head;
        while(current != null) {
            list.add(current.task);
            current = current.next;
        }
        return list;
    }
    @Override
    public void add(Task task) {
        int id = task.getId();
        if (history.containsKey(id)){
            Node node = history.get(id);
            linkLast(task);
            removeNode(node);
        } else {
            linkLast(task);
        }
    }
    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
    @Override
    public void remove(int id) { // удаляет из HashMap
        if (!history.containsKey(id)) {
            System.out.println("No such ID in history");
            return;
        }
        Node node = history.remove(id);
        removeNode(node);
    }
    private void removeNode(Node node) {
        Node next = node.next;
        Node prev = node.prev;
        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }
        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        node.task = null;
    }
}
