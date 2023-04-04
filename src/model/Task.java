package model;

import java.util.Objects;

public class Task {

    private int id;
    private String name;
    private String description;
    private Status status = Status.NEW;

    //несколько вариантов конструктора, для разных вариантов создания объекта Task и наследников Epic и SubTask
    //в дальнейшем, при уточнении состава вводимых данных можно сократить количество конструкторов
    public Task(int id, String name, String description, Status status) { // конструктор с параметром id используется для изменения задачи.
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }
// тут был конструктор без id но со "статусом" - он использоался для создания новой задачи.
// после удаления их него принимаемого параметра "статус" он стал не нужен, т.к. такой уже есть

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status.getName() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return (getId() == task.getId()) &&
                Objects.equals(getName(), task.getName()) &&
                Objects.equals(getDescription(), task.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(),getDescription());
    }
}
