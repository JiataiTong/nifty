package io.github.cmw025.nifty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import io.github.cmw025.nifty.RecyclerViewCheckboxAdapter.MemberModel;

/**
 * Created by troytong on 2017/11/9.
 */

public class ProjectModel {
    private String name;
    private String content;
    private Date startDate;
    private Date dueDate;
    private Date finishDate;
    private int index;
    private boolean overDue = false;
    private boolean finished;
    private String key;
    private int color;
    //private Milestone milestone;
    
    public ProjectModel(){}

    public ProjectModel(String name, String content, Date startDate, Date dueDate, int index, String key, int color){
        assert (startDate.before(dueDate));
        assert (dueDate.after(new Date()));
        this.content = content;
        this.name = name;
        this.index = index;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.finishDate = null;
        //this.milstone = new Milestone();
        this.finished = false;
        this.key = key;
        this.color = color;
    }

    //getters

//    public int getNumTasks() {
//        return this.tasks.size();
//    }


    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public String getContent() {
        return this.content;
    }

    public String getKey() {
        return this.key;
    }

    public int getColor() {
        return this.color;
    }

    public boolean isFinished() {
        return this.finished;
    }

    //setters
    public void setName(String name) {
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectModel that = (ProjectModel) o;

        return index == that.index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isOverDue(){
        if (!this.finished && this.dueDate.before(new Date())) {
            this.overDue = true;
        }
        return this.overDue;
    }

//    public void finishTask(TaskModel task) {
//        task.finish();
//    }
//
//    public boolean finishProject() {
//        if (this.isFinished() != true && this.unfinishedTasks.size() == 0) {
//            this.finished = true;
//            this.finishDate = new Date();
//            this.isOverDue();
//            return true;
//        }
//        return false;
//    }


}
