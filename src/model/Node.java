package model;

public class Node<Task>{
    public Task task;
    public Node<Task> next;
    public Node<Task> prev;
    public Node(Node<Task> prev, Task task, Node<Task> next) {
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
