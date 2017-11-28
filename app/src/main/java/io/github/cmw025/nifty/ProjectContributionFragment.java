package io.github.cmw025.nifty;




import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.communication.IOnPointFocusedListener;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ProjectContributionFragment extends Fragment {
    public int test;
    // Unique FireBase ID for this task
    private String projectFireBaseID;
    private String taskFireBaseKey;
    private long taskListID;
    private DatabaseReference fb;
    private DatabaseReference taskRef;
    private ArrayList<String> contrib1;
    private ArrayList<Integer> countdate;
    private ArrayList<String> lista;
    private ValueLineChart linech;

    private ArrayList<Date> finishedDates;
    private ArrayList<Date> check;

    public ProjectContributionFragment(){

    }




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

        linech = (ValueLineChart) activity.findViewById(R.id.linechart);
        check = new ArrayList<>();
        finishedDates = new ArrayList<>();

        fb = FirebaseDatabase.getInstance().getReference();
        fb.child("projects").child(projectFireBaseID).child("tasks").addValueEventListener(new ValueEventListener() {

            @Override

            public void onDataChange(DataSnapshot data) {
                for (DataSnapshot data1 : data.getChildren()) {
                    TaskModel task = data1.getValue(TaskModel.class);

                    if (task.isFinished()) {
                        finishedDates.add(task.getFinishDate());
                    }
                }

                ArrayList<String> finishDatetoStr = new ArrayList<>();
                if (!finishedDates.isEmpty()) {
                    for (int i = 0; i < finishedDates.size(); i++) {
                        Date today = finishedDates.get(i);
                        Date newDate = new Date(today.getTime() + (604800000L * 2) + (24 * 60 * 60));
                        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
                        String stringdate = dt.format(newDate);
                        finishDatetoStr.add(stringdate);
                    }
                }



                Collections.sort(finishDatetoStr, new Comparator<String>() {

                    @Override
                    public int compare(String arg0, String arg1) {
                        SimpleDateFormat format = new SimpleDateFormat(
                                "yyyy-mm-dd");
                        int compareResult = 0;
                        try {
                            Date arg0Date = format.parse(arg0);
                            Date arg1Date = format.parse(arg1);
                            compareResult = arg0Date.compareTo(arg1Date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            compareResult = arg0.compareTo(arg1);
                        }
                        return compareResult;
                    }
                });
                contrib1 = finishDatetoStr;

                countDate();
                setData();
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
//                            tempmap.put(taskSnapshot.child("finisheddate").getKey(), in (tempmap.get(taskSnapshot.child("finisheddata").getKey())+=1 );
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


    public void countDate(){
        ArrayList<String> templist = contrib1;
        lista = new ArrayList<>();
        countdate = new ArrayList<>();

        Map<String, Integer> tempmap= new HashMap<String, Integer>();
        try {
            for (int i = 0; i < templist.size(); i++) {
                if (tempmap.get(templist.get(i)) == null) {
                    tempmap.put(templist.get(i), 1);
                    lista.add(templist.get(i));
                } else {
                    int tcount = tempmap.get(templist.get(i));
                    tcount++;
                    tempmap.put(templist.get(i), tcount);
                }
            }
            for (int i = 0; i < tempmap.size(); i++) {
                countdate.add(tempmap.get(templist.get(i)));
            }
        }catch(Exception e){

        }

    }

    public void setData(){

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);
        series.addPoint(new ValueLinePoint("start",0f));

        for (int i = 0 ; i < countdate.size(); i++){
            series.addPoint(new ValueLinePoint(lista.get(i),countdate.get(i)));
        }

        linech.addSeries(series);
        linech.addStandardValue(1.0f);
        linech.addStandardValue(2.0f);
        linech.addStandardValue(3.0f);
        linech.setOnPointFocusedListener(new IOnPointFocusedListener() {

            @Override
            public void onPointFocused(int _PointPos) {
                Log.d("Test", "Pos: " + _PointPos);
            }
        });
        linech.startAnimation();
    }
}
