package io.github.cmw025.nifty;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Invite New Member");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.invite_new_member_dialog, findViewById(android.R.id.content), false);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.invite_new_member_input);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String email = input.getText().toString();
                email = email.replace(".", ",");
                fb.child("emails").child(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot data) {
                        // If email data exists on server, add invitee to the project
                        if (data.getValue() != null) {
                            RecyclerViewCheckboxAdapter.MemberModel invitee = data.getValue(RecyclerViewCheckboxAdapter.MemberModel.class);
                            // Add invitee as project member
                            fb.child("projects").child(projectFireBaseID).child("members").child(invitee.getUid()).setValue(invitee);


                            // Send project info to invitee
                            fb.child("projects").child(projectFireBaseID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot data) {
                                    if (data.getValue() != null) {
                                        ProjectModel project = data.getValue(ProjectModel.class);
                                        fb.child("usrs").child(invitee.getUid()).child("projects").child(projectFireBaseID).setValue(project);

                                        // Send member list to invitee
                                        fb.child("projects").child(projectFireBaseID).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot data) {
                                                for (DataSnapshot child : data.getChildren()) {
                                                    RecyclerViewCheckboxAdapter.MemberModel member = child.getValue(RecyclerViewCheckboxAdapter.MemberModel.class);
                                                    fb.child("usrs").child(invitee.getUid()).child("projects").child(projectFireBaseID).child("members").child(member.getUid()).setValue(member);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        // Send task list to invitee
                                        fb.child("projects").child(projectFireBaseID).child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot data) {
                                                for (DataSnapshot child : data.getChildren()) {
                                                    TaskModel task = child.getValue(TaskModel.class);
                                                    fb.child("usrs").child(invitee.getUid()).child("projects").child(projectFireBaseID).child("tasks").child(task.getKey()).setValue(task);
                                                    fb.child("usrs").child(invitee.getUid()).child("tasks").child(task.getKey()).setValue(task);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Snackbar.make(findViewById(R.id.dis_shit), "Invitation success! New member added.", Snackbar.LENGTH_LONG).show();
                        }

                        // If email data not found on server, send out invitation email
                        else {
                            Snackbar.make(findViewById(R.id.dis_shit), "Email not found on server, invitation sent.", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        builder.show();
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
