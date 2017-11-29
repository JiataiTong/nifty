package io.github.cmw025.nifty;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.itangqi.waveloadingview.WaveLoadingView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProcessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProcessFragment extends Fragment {

    private WaveLoadingView mWaveLoadingView;
    private int checkedItem = 0;
    private String projectKey;
    private OnFragmentInteractionListener mListener;
    private DatabaseReference fb;
    private String userDisplayName;
    private String projectFireBaseID;
    private String uid;
    private Activity activity;
    private ProjectModel project;
    private MilestoneGenerator milestoneGenerator;

    private int realColor;

    public ProcessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProcessFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProcessFragment newInstance() {
        ProcessFragment fragment = new ProcessFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectKey = getActivity().getIntent().getStringExtra("projectFireBaseID");
        milestoneGenerator = new MilestoneGenerator(projectKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_process, container, false);
    }

    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        activity = getActivity();
        initFireBase();
        if (getActivity() != null) {
            initProcessBar();
        }

    }

    public void initFireBase() {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDisplayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        fb = FirebaseDatabase.getInstance().getReference();
//        fb.child("projects").child(projectKey).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot data) {
//                project = data.getValue(ProjectModel.class);
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });



    }

    public void initProcessBar() {
        mWaveLoadingView = (WaveLoadingView) activity.findViewById(R.id.waveLoadingView);
        mWaveLoadingView.setAnimDuration(2000);

        // set color
        fb.child("usrs").child(uid).child("projects").child(projectKey).child("color").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                if (data.getValue() != null) {
                    // Set ToolBar color
                    long l = (long) data.getValue();
                    int projectColor = (int) l;
                    realColor = ContextCompat.getColor(getActivity(), projectColor);
                    mWaveLoadingView.setBorderColor(realColor);
                    mWaveLoadingView.setWaveColor(realColor);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        fb.child("projects").child(projectKey).child("tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                double num = 0;
                double finishedNum = 0;
                for (DataSnapshot child : data.getChildren()) {
                    TaskModel task = child.getValue(TaskModel.class);
                    num++;
                    if (task.isFinished()) {
                        finishedNum++;
                    }
                }
                double result = finishedNum/num;
                String title = String.format("%.2f", (result*100))+"%";
                mWaveLoadingView.setCenterTitle(title);
                mWaveLoadingView.setProgressValue((int)(result*100));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //System.out.println(milestoneGenerator.processCalc());

        activity.findViewById(R.id.tv_shape).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getActivity()).setTitle("Shape Type").setSingleChoiceItems(
                        new String[] { "CIRCLE", "TRIANGLE", "SQUARE", "RECTANGLE" }, checkedItem,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                checkedItem = which;
                                switch (which) {
                                    case 0:
                                        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
                                        dialog.dismiss();
                                        break;
                                    case 1:
                                        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.TRIANGLE);
                                        dialog.dismiss();
                                        break;
                                    case 2:
                                        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.SQUARE);
                                        dialog.dismiss();
                                        break;
                                    case 3:
                                        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.RECTANGLE);
                                        dialog.dismiss();
                                        break;
                                    default:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        }).show();
            }
        });
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
