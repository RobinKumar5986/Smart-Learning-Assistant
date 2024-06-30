package com.nationalhackaton.smartlearningassist.DataHolde;

public class TaskDataMode {
    String task;
    int status;
    int ID;

    public TaskDataMode(String task, int status, int ID) {
        this.task = task;
        this.status = status;
        this.ID = ID;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
