package model;

public class SubTask extends Task {

    private int epic;

    public SubTask(String name, String description, int epic) {
        super(name, description);
        this.epic = epic;
    }

    public SubTask(int id, String name, String description, int epic) {
        super(id, name, description);
        this.epic = epic;
    }

    public int getEpic() {
        return epic;
    }

    public void setEpic(int epic) {
        this.epic = epic;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return new String(getId() + ",SUBTASK," + getName() + "," + getStatus() + "," + getDescription() + "," + getEpic());
    }
}
