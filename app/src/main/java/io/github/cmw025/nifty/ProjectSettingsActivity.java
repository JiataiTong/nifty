package io.github.cmw025.nifty;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProjectSettingsActivity extends AppCompatActivity {

    private int color;
    private String uid;
    private String projectFireBaseID;
    private String projectName;
    private DatabaseReference fb;
    private int seekBarSelection;
    private EditText editProjectName;

    private Switch parentSwitch;
    private Switch notifyMileStones;
    private Switch notifyMyTasks;
    private Switch notifyAllTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);

        projectFireBaseID = getIntent().getStringExtra("projectFireBaseID");
        projectName = getIntent().getStringExtra("projectName");
        int realColor = getIntent().getIntExtra("realColor", 0);
        toolbar.setBackgroundColor(realColor);

        // FireBase
        fb = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uid = user.getUid();


        // Project Name
        editProjectName = findViewById(R.id.edit_project_name);
        if (projectName != null) {
            editProjectName.setText(projectName);
        }

        initMember();
        initDate();
        initNotification();
        initSeekBar();
        initRadioGroup();
    }

    // Member
    public void initMember() {
        TextView memberList = findViewById(R.id.member_list);

        // Get project member list
        fb.child("projects").child(projectFireBaseID).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                StringBuffer buffer = new StringBuffer();
                for (DataSnapshot child : data.getChildren()) {
                    RecyclerViewCheckboxAdapter.MemberModel member = child.getValue(RecyclerViewCheckboxAdapter.MemberModel.class);
                    buffer.append(member.getName() + ", ");
                }
                String string = buffer.toString();
                string = string.substring(0, string.length() - 2);
                memberList.setText(string);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initDate() {
        // Date
    }

    public void initNotification() {
        // Notification
        parentSwitch = findViewById(R.id.parent_switch);
        notifyMileStones = findViewById(R.id.notify_milestones);
        notifyMyTasks = findViewById(R.id.notify_my_tasks);
        notifyAllTasks = findViewById(R.id.notify_all_tasks);
        parentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    notifyMileStones.setEnabled(true);
                    notifyMyTasks.setEnabled(true);
                    notifyAllTasks.setEnabled(true);
                }
                else {
                    notifyMileStones.setChecked(false);
                    notifyMileStones.setEnabled(false);
                    notifyMyTasks.setChecked(false);
                    notifyMyTasks.setEnabled(false);
                    notifyAllTasks.setChecked(false);
                    notifyAllTasks.setEnabled(false);
                }
            }
        });

        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("notification_enabled").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                if (data.getValue() != null) {
                    boolean checked = (boolean) data.getValue();
                    parentSwitch.setChecked(checked);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("notify_mile_stones").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                if (data.getValue() != null) {
                    boolean checked = (boolean) data.getValue();
                    notifyMileStones.setChecked(checked);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("notify_my_tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                if (data.getValue() != null) {
                    boolean checked = (boolean) data.getValue();
                    notifyMyTasks.setChecked(checked);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("notify_all_tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                if (data.getValue() != null) {
                    boolean checked = (boolean) data.getValue();
                    notifyAllTasks.setChecked(checked);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initSeekBar() {
        // SeekBar
        TextView monthly = findViewById(R.id.monthly);
        TextView weekly = findViewById(R.id.weekly);
        TextView daily = findViewById(R.id.daily);

        // Default to "Daily"
        seekBarSelection = 2;

        SeekBar seekBar = findViewById(R.id.seekBar);
        int seekBarProgress = seekBar.getProgress();
        switch (seekBarProgress) {
            case 0:
                monthly.setTextSize(20);
                monthly.setTypeface(null, Typeface.BOLD);
                weekly.setTextSize(15);
                weekly.setTypeface(null, Typeface.NORMAL);
                daily.setTextSize(15);
                daily.setTypeface(null, Typeface.NORMAL);
                seekBarSelection = 0;
                break;
            case 1:
                weekly.setTextSize(20);
                weekly.setTypeface(null, Typeface.BOLD);
                monthly.setTextSize(15);
                monthly.setTypeface(null, Typeface.NORMAL);
                daily.setTextSize(15);
                daily.setTypeface(null, Typeface.NORMAL);
                seekBarSelection = 1;
                break;
            case 2:
                daily.setTextSize(20);
                daily.setTypeface(null, Typeface.BOLD);
                monthly.setTextSize(15);
                monthly.setTypeface(null, Typeface.NORMAL);
                weekly.setTextSize(15);
                weekly.setTypeface(null, Typeface.NORMAL);
                seekBarSelection = 2;
                break;
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.v("click", "clicked on " + i);
                switch (i) {
                    case 0:
                        monthly.setTextSize(20);
                        monthly.setTypeface(null, Typeface.BOLD);
                        weekly.setTextSize(15);
                        weekly.setTypeface(null, Typeface.NORMAL);
                        daily.setTextSize(15);
                        daily.setTypeface(null, Typeface.NORMAL);
                        seekBarSelection = 0;
                        break;
                    case 1:
                        weekly.setTextSize(20);
                        weekly.setTypeface(null, Typeface.BOLD);
                        monthly.setTextSize(15);
                        monthly.setTypeface(null, Typeface.NORMAL);
                        daily.setTextSize(15);
                        daily.setTypeface(null, Typeface.NORMAL);
                        seekBarSelection = 1;
                        break;
                    case 2:
                        daily.setTextSize(20);
                        daily.setTypeface(null, Typeface.BOLD);
                        monthly.setTextSize(15);
                        monthly.setTypeface(null, Typeface.NORMAL);
                        weekly.setTextSize(15);
                        weekly.setTypeface(null, Typeface.NORMAL);
                        seekBarSelection = 2;
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("notification_frequency").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                if (data.getValue() != null) {
                    long l = (long) data.getValue();
                    int i = (int) l;
                    seekBarSelection = i;
                    seekBar.setProgress(seekBarSelection);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initRadioGroup() {
        // Radio Group
        color = R.color.light_red;
        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.color_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId) {
                    case R.id.red:
                        //color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_red));
                        color = R.color.light_red;
                        break;
                    case R.id.orange:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_orange));
                        color = R.color.light_orange;
                        break;
                    case R.id.yellow:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_yellow));
                        color = R.color.light_yellow;
                        break;
                    case R.id.green:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_green));
                        color = R.color.light_green;
                        break;
                    case R.id.cyan:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_cyan));
                        color = R.color.light_cyan;
                        break;
                    case R.id.aqua:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_aqua));
                        color = R.color.light_aqua;
                        break;
                    case R.id.blue:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_blue));
                        color = R.color.light_blue;
                        break;
                    case R.id.purple:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_purple));
                        color = R.color.light_purple;
                        break;
                }
            }
        });
    }

    public void inviteMembers(View view) {
        Intent intent = new Intent(ProjectSettingsActivity.this, InviteMemberActivity.class);
        startActivity(intent);
    }

    public void goBack(View view) {
        finish();
    }

    public void saveSettings(View view) {
        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("color").setValue(color);
        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("notification_enabled").setValue(parentSwitch.isChecked());
        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("notification_frequency").setValue(seekBarSelection);
        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("notify_mile_stones").setValue(notifyMileStones.isChecked());
        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("notify_my_tasks").setValue(notifyMyTasks.isChecked());
        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("notify_all_tasks").setValue(notifyAllTasks.isChecked());

        String newProjectName = editProjectName.getText().toString();
        fb.child("projects").child(projectFireBaseID).child("name").setValue(newProjectName);
        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("name").setValue(newProjectName);

        finish();
    }
}
