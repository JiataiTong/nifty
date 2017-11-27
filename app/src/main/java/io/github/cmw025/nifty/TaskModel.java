package io.github.cmw025.nifty;

import com.google.android.gms.tasks.Task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by troytong on 2017/11/9.
 */

public class TaskModel implements Comparable<TaskModel> {
    // Stable ID required for Drag & Drop of RecyclerView
    private long id;
    // FireBase ID
    private String key;
    private String projectKey;

    private String content;
    private Date startDate;
    private Date dueDate;
    private boolean finished;
    //private int index;
    private String name;
    private Date finishDate;

    public int position;

    public TaskModel(String name, String content, Date startDate, Date dueDate, long id, String key, String projectKey){
        this.name = name;
        this.content = content;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.finished = false;
        //this.index = index;
        this.id = id;
        this.key = key;
        this.projectKey = projectKey;
    }

    // Empty constructor for Firebase
    public TaskModel() {}

    public TaskModel(String name, String content, long id, String key, String projectKey){
        this.name = name;
        this.content = content;
        this.finished = false;
        this.id = id;
        this.key = key;
        this.projectKey = projectKey;
    }

    //getters
    public boolean isFinished() {
        return this.finished;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public Date getDueDate(){
        return this.dueDate;
    }

    public String getContent() {
        return this.content;
    }

    public String getName() {
        return this.name;
    }

    public Date getFinishDate(){
        if (this.isFinished()){
            return this.finishDate;
        }
        return null;
    }

    public long getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public String getProjectKey() {
        return this.projectKey;
    }


    //setters
    public void setContent(String content) {
        this.content = content;
    }

    public void setStartDateDate(Date date) {
        this.startDate = date;
    }

    public void setDueDate(Date date) {
        this.dueDate = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskModel taskModel = (TaskModel) o;

        if (finished != taskModel.finished) return false;
        if (!content.equals(taskModel.content)) return false;
        return name.equals(taskModel.name);
    }


    public int compareTo(TaskModel otherTask){
        if (this.dueDate.before(otherTask.dueDate)){
            return -1;
        }
        else {
            if (this.dueDate.after(otherTask.dueDate)) return 1;
        }
        return 0;
    }

    public void finish(){
        this.finishDate = new Date();
        this.finished = true;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int pos) {
        this.position = pos;
    }

}
