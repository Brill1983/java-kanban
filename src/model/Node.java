package model;

public class Node {
    public Task task;
    public Node next;
    public Node prev;
    public Node(Node prev, Task task, Node next) {
        this.prev = prev;
        this.task = task;
        this.next = next;
    }

    @Override
    public String toString() {
        return "Node={" +
                "task=" + task +
                '}';
    }
}
