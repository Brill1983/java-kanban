package model;

public class SubTask extends Task {

    private Epic epic;

    public SubTask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }
    public SubTask(int id, String name, String description, Status status, Epic epic) {
        super(id, name, description, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", epic= " + epic.getId() + ", " + epic.getStatus() +
                '}';
    }
}
