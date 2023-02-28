package model;

public class SubTask extends Task{
    private int epicId; // исправил видимость поля на private, переименовал поле

    public SubTask(String name, String description) {
        super(name, description);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Задача: {" +
                "id=" + getId() +
                ", Название='" + getName() + '\'' +
                ", Описание='" + getDescription() + '\'' +
                ", Статус='" + getStatus() + '\'' +
                ", id Эпика=" + epicId +
                '}';
    }
}
