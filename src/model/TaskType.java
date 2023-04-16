package model;

public enum TaskType {
    Task("TASK"),
    Epic("EPIC"),
    SubTask("SUBTASK");

    String taskType;

    TaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskType() {
        return taskType;
    }
}
