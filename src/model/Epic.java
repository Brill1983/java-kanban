package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<SubTask> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
//        return "Epic{" +
//                "id=" + getId() +
//                ", name='" + getName() + '\'' +
//                ", description='" + getDescription() + '\'' +
//                ", status='" + getStatus().getName() + '\'' +
//                ", subTasks=" + subTasks.size() +
//                '}';
        return new String(getId() + ",EPIC," + getName() + "," + getStatus() + "," + getDescription() + ",");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return (getId() == epic.getId()) &&
                Objects.equals(getName(), epic.getName()) &&
                Objects.equals(getDescription(), epic.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(),getDescription());
    }
}
