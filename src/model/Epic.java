package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> subTaskList; // завел поле для списка ссылок на подзадачи

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<SubTask> getSubTaskList() { // геттер для поля со списком
        return subTaskList;
    }

    public void setSubTaskList(ArrayList<SubTask> subTaskList) { // сеттор поля со списком
        this.subTaskList = subTaskList;
    }

    @Override
    public String toString() {
        return "Задача: {" +
                "id=" + getId() +
                ", Название='" + getName() + '\'' +
                ", Описание='" + getDescription() + '\'' +
                ", Статус='" + getStatus() + '\'' +
                '}';
    }
}
