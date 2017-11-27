package io.github.cmw025.nifty;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.getIntent;
import static java.util.jar.Pack200.Unpacker.TRUE;

public class ProjectContributionFragment extends Fragment {
public int test;
    // Unique FireBase ID for this task
    private String projectFireBaseID;
    private String taskFireBaseKey;
    private long taskListID;
    private DatabaseReference fb;
    private DatabaseReference taskRef;
    private ArrayList<String> usr;
    private List<Map<String, Integer>> contrib1;

    public ProjectContributionFragment(){

    }

    private LineChart linech;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.contributionplot, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        Activity activity = getActivity();
        projectFireBaseID = getActivity().getIntent().getStringExtra("projectFireBaseID");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        linech = (LineChart) activity.findViewById(R.id.linechart);
        setData(40,60);
        linech.animateX(1000);

        fb = FirebaseDatabase.getInstance().getReference();
        fb.child("projects").child(projectFireBaseID).child("tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                ArrayList<Date> finishedDates = new ArrayList<>();
                for (DataSnapshot child : data.getChildren()) {
                    TaskModel task = child.getValue(TaskModel.class);
                    if (task.getFinishDate() != null) {
                        finishedDates.add(task.getFinishDate());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        DatabaseReference usrRef = (DatabaseReference) fb.child("usrs").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
//                    usr.add(snapshot.child("name").getKey());
//                    Map<String, Integer> tempmap = new HashMap<String,Integer>();
//                    int ad = 1;
//                    for(DataSnapshot taskSnapshot: snapshot.child("tasks").getChildren()){
//                        if ( tempmap.get(taskSnapshot.child("finisheddata").getKey()) == null){
//                            tempmap.put(taskSnapshot.child("finisheddate").getKey(), ad);
//                        }
//                        else{
//                            tempmap.put(taskSnapshot.child("finisheddate").getKey(), int (tempmap.get(taskSnapshot.child("finisheddata").getKey())+=1 );
//                        }
//
//                        //taskSnapshot.child("finished").getKey()
//                    }
//                    contrib1.add(tempmap);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });




    }

    public void setData(int count, int range){

        ArrayList<Entry> yAXES1 = new ArrayList<>();
        for (int i = 0; i<count; i++){
            float val = (float) (Math.random()*range)+250;
            yAXES1.add(new Entry(i,val));
        }
        ArrayList<Entry> yAXES2 = new ArrayList<>();
        for (int i = 0; i<count; i++){
            float val = (float) (Math.random()*range)+250;
            yAXES2.add(new Entry(i,val));
        }
        ArrayList<Entry> yAXES3 = new ArrayList<>();
        for (int i = 0; i<count; i++){
            float val = (float) (Math.random()*range)+250;
            yAXES3.add(new Entry(i,val));
        }
        LineDataSet set1 = new LineDataSet(yAXES1, "2nd");
        LineDataSet set2 = new LineDataSet(yAXES2, "2nd");
        LineDataSet set3 = new LineDataSet(yAXES3, "2nd");
        LineData data = new LineData(set1,set2,set3);
        linech.setData(data);





        /*
         ArrayList<String> xAXES = new ArrayList<>();
        ArrayList<Entry> yAXES1 = new ArrayList<>();
        ArrayList<Entry> yAXES2 = new ArrayList<>();
        double x = 0;
        int numDatap = 1000;
        for(int i=0; i<numDatap;i++){
            float a = (float) i;
            float b = (float) i*2;
            x = x+1;
            yAXES1.add(new Entry(a,i));
            yAXES2.add(new Entry(b,i));
            xAXES.add(i, String.valueOf(x));

        }
        String[] xaxs = new String[xAXES.size()];
        for (int i=0; i<xAXES.size();i++){
            xaxs[i] = xAXES.get(i).toString();
        }


        ArrayList linedatas = new ArrayList() {
        };
        LineDataSet linedataset1 = new LineDataSet(yAXES1,"1st");
        linedataset1.setDrawCircles(false);
        linedataset1.setColor(Color.BLUE);


        LineDataSet lineDataSet2 = new LineDataSet(yAXES2, "2nd");
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setColor(Color.GRAY);

        linedatas.add(linedataset1);
        linedatas.add(lineDataSet2);

        linech.setData(new LineData());
        LineData s = new LineData();
         */
    }




}
