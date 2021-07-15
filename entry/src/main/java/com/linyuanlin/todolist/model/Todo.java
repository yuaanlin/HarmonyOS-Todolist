package com.linyuanlin.todolist.model;

public class Todo {
    public int id;
    public String title;
    public String description;
    public String deadline;
    public boolean done;

    public Todo(int id, String title, String description, String deadline, boolean done) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.done = done;
    }
}
