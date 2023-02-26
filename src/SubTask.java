public class SubTask extends Task{
    protected int masterId;
    public SubTask(String name, String description) {
        super(name, description);
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    @Override
    public String toString() {
        return "Задача: {" +
                "id=" + getId() +
                ", Название='" + getName() + '\'' +
                ", Описание='" + getDescription() + '\'' +
                ", Статус='" + getStatus() + '\'' +
                ", masterId=" + masterId +
                '}';
    }
}
