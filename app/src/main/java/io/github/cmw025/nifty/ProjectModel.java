package io.github.cmw025.nifty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by troytong on 2017/11/9.
 */

public class ProjectModel {
    private String name;
    private String content;
    private Date startDate;
    private Date dueDate;
    private Date finishDate;
    private List<TaskModel> unfinishedTasks;
    private List<TaskModel> finishedTasks;
    private int index;
    private boolean overDue = false;
    private boolean finished;
    //private Milestone milestone;
    
    public ProjectModel(){}

    public ProjectModel(String name, String content, Date startDate, Date dueDate, int index){
        assert (startDate.before(dueDate));
        assert (dueDate.after(new Date()));
        this.content = content;
        this.name = name;
        this.index = index;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.finishDate = null;
        this.unfinishedTasks = new ArrayList<TaskModel>();
        //this.milstone = new Milestone();
        this.finished = false;
        this.finishedTasks = new ArrayList<TaskModel>();
    }

    //getters

    public int getNumTasks() {
        return this.unfinishedTasks.size()+this.finishedTasks.size();
    }

    public int getNumUNFTasks() {
        return this.unfinishedTasks.size();
    }

    public int getNumFTasks() {
        return this.finishedTasks.size();
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

    public List<TaskModel> getUnfinishedTasks() {
        return this.unfinishedTasks;
    }

    public List<TaskModel> getFinishedTasks() {
        return this.finishedTasks;
    }

    public TaskModel getUnfinishedTask(int index) {
        assert (index < this.unfinishedTasks.size());
        assert (index >= 0);
        return this.unfinishedTasks.get(index);
    }

    public TaskModel getFinishedTask(int index) {
        assert (index < this.finishedTasks.size());
        assert (index >= 0);
        return this.finishedTasks.get(index);
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

    //methods
    public void addUnfinishedTask(TaskModel task){
        this.unfinishedTasks.add(task);
        Collections.sort(this.getUnfinishedTasks());
    }

    public void deleteUnfinishedTask(TaskModel task) {
        this.unfinishedTasks.remove(task);
    }

    public void addFinishedTask(TaskModel task) {
        this.finishedTasks.add(task);
    }

    public void deleteFinishedTask(TaskModel task) {
        this.finishedTasks.remove(task);
    }

    public boolean isOverDue(){
        if (!this.finished && this.dueDate.before(new Date())) {
            this.overDue = true;
        }
        return this.overDue;
    }

    public void finishTask(TaskModel task) {
        this.unfinishedTasks.remove(task);
        task.finish();
        this.finishedTasks.add(task);

    }

    public boolean finishProject() {
        if (this.isFinished() != true && this.unfinishedTasks.size() == 0) {
            this.finished = true;
            this.finishDate = new Date();
            this.isOverDue();
            return true;
        }
        return false;
    }


}
