package io.github.cmw025.nifty;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import io.github.cmw025.nifty.RecyclerViewCheckboxAdapter.MemberModel;

/**
 * Created by troytong on 2017/11/27.
 */

public class MilestoneGenerator {

    private boolean isfinished;
    private String projectName;
    private String startDate;
    private String teamates;
    private String firstTaskName;
    private String firstTaskDate;
    private String middleTaskName;
    private String middleTaskDate;
    private String lastTaskName;
    private String lastTaskDate;
    private long projectLength;
    private int taskNum;
    private DatabaseReference firebase;
    private String uid;
    private ProjectModel project;
    private ArrayList<MemberModel> members = new ArrayList<>();
    private ArrayList<TaskModel> tasks = new ArrayList<>();
    private ArrayList<TaskModel> finishedTasks = new ArrayList<>();

    public MilestoneGenerator(String projectKey) {

        firebase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uid = user.getUid();
        String userDisplayName = user.getDisplayName();
        firebase.child("projects").child(projectKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                project = data.getValue(ProjectModel.class);
                firebase.child("projects").child(projectKey).child("members").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot data) {
                        for (DataSnapshot child : data.getChildren()) {
                            members.add(child.getValue(MemberModel.class));
                        }
                        int counter = 0;
                        for (MemberModel member : members) {
                            if (member.getName() != userDisplayName){
                                teamates += member.getName();
                                if (counter == 3 || counter == members.size()-1) {
                                    break;
                                }
                                teamates += ", ";
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                isfinished = project.isFinished();
                projectName = project.getName();
                Date start = project.getStartDate();
                startDate = start.toString();



                firebase.child("projects").child(projectKey).child("tasks").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot data) {
                        for (DataSnapshot child : data.getChildren()) {
                            tasks.add(child.getValue(TaskModel.class));
                        }
                        //System.out.println(tasks.size()+"*");
                        finishedTasks = Sorting.task_sorting(tasks,false);
                        //System.out.println(tasks.size());
                        if (isfinished) {
                            firstTaskName = finishedTasks.get(0).getName();
                            firstTaskDate = finishedTasks.get(0).getStartDate().toString();
                            middleTaskName = finishedTasks.get((int)(finishedTasks.size()/2)).getName();
                            middleTaskDate = finishedTasks.get((int)(finishedTasks.size()/2)).getStartDate().toString();
                            lastTaskName = finishedTasks.get(finishedTasks.size() - 1).getName();
                            lastTaskDate = finishedTasks.get(finishedTasks.size() - 1).getStartDate().toString();
//            projectLength = (project.getFinishDate().getTime() - project.getStartDate().getTime())/((long)(2 * 24 * 60 * 60 * 1000));
                            taskNum = finishedTasks.size();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //System.out.println(tasks.size()+"*");

    }


    public double processCalc(){
        if (finishedTasks == null) {
            return 0;
        }
        //System.out.println(tasks.size());
        return ((double)finishedTasks.size())/((double)tasks.size());
        //return 0.55;
    }

}
