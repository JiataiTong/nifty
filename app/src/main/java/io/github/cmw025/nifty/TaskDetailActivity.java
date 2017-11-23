package io.github.cmw025.nifty;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import io.github.cmw025.nifty.RecyclerViewCheckboxAdapter.MemberModel;

public class TaskDetailActivity extends AppCompatActivity {

    int mYear, mMonth, mDay;
    final int DATE_DIALOG = 1;

    private Button dueButton;
    private EditText taskContent;
    private EditText taskName;
    // private TextView mTextView;

    // Unique FireBase ID for this task
    private String taskFireBaseKey;
    private long taskListID;
    private DatabaseReference fb;
    private DatabaseReference taskRef;
    private String projectFireBaseID;
    private String projectFireBaseKey;
    private String uid;

    private int realColor;

    private HashSet<MemberModel> currentMembers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskName = (EditText) findViewById(R.id.fragment_note_title);
        taskContent = (EditText) findViewById(R.id.task_content);
        currentMembers = new HashSet<>();

        dueButton = (Button) findViewById(R.id.dateChoose);
        dueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });

        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        // mTextView = (TextView) findViewById(R.id.textview);
        // taskContent.addTextChangedListener(mTextWatcher);

        // Set up FireBase
        Intent intent = getIntent();
        taskFireBaseKey = intent.getStringExtra("taskFireBaseKey");
        taskListID = intent.getLongExtra("taskListID", 0);

        fb = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set ToolBar color
        Toolbar toolbar = findViewById(R.id.task_detail_toolbar);
        projectFireBaseKey = intent.getStringExtra("projectFireBaseKey");
        fb.child("usrs").child(uid).child("projects").child(projectFireBaseKey).child("color").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                if (data.getValue() != null) {
                    long l = (long) data.getValue();
                    int projectColor = (int) l;
                    realColor = ContextCompat.getColor(TaskDetailActivity.this, projectColor);
                    toolbar.setBackgroundColor(realColor);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        taskRef = fb.child("tasks").child(taskFireBaseKey);

        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                TaskModel task = data.getValue(TaskModel.class);

                projectFireBaseID = task.getProjectKey();

                taskName.post(new Runnable(){
                    @Override
                    public void run() {
                        taskName.setText(task.getName());
                    }
                });

                taskContent.post(new Runnable(){
                    @Override
                    public void run() {
                        taskContent.setText(task.getContent());
                    }
                });

                dueButton.post(new Runnable(){
                    @Override
                    public void run() {
                        Date date = task.getDueDate();
                        if (date != null) {
                            mYear = date.getYear() + 1900;
                            mMonth = date.getMonth();
                            mDay = date.getDate();
                            display();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Get who are already on the task
        taskRef.child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                currentMembers = new HashSet<>();
                for (DataSnapshot child : data.getChildren()) {
                    MemberModel member = child.getValue(MemberModel.class);
                    currentMembers.add(member);
                }

                // Update UI
                if (!currentMembers.isEmpty()) {
                    StringBuffer buffer = new StringBuffer();
                    for (MemberModel member : currentMembers) {
                        buffer.append(member.getName() + ", ");
                    }
                    String string = buffer.toString();
                    string = string.substring(0, string.length() - 2);
                    Button addMember = findViewById(R.id.add_member);
                    addMember.setText(string);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

//    TextWatcher mTextWatcher = new TextWatcher() {
//        private CharSequence temp;
//        private int editStart;
//        private int editEnd;
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            // TODO Auto-generated method stub
//            temp = s;
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count,
//                                      int after) {
//            // TODO Auto-generated method stub
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            // TODO Auto-generated method stub
//            editStart = taskContent.getSelectionStart();
//            editEnd = taskContent.getSelectionEnd();
//            mTextView.setText(temp.length() + " letters input");
//            if (temp.length() > 2000) {
//                Toast.makeText(TaskDetailActivity.this,
//                        "the note is too long!", Toast.LENGTH_SHORT)
//                        .show();
//                s.delete(editStart - 1, editEnd);
//                int tempSelection = editStart;
//                taskContent.setText(s);
//                taskContent.setSelection(tempSelection);
//            }
//        }
//    };

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    public void display() {
        dueButton.setText(new StringBuffer().append(mMonth + 1).append("-").append(mDay).append("-").append(mYear).append(" "));
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display();
        }
    };

    public void addMembers(View view) {
        Intent intent = new Intent (this, AddMemberActivity.class);
        intent.putExtra("projectFireBaseID", projectFireBaseID);
        intent.putExtra("taskFireBaseKey", taskFireBaseKey);
        intent.putExtra("currentMembers", currentMembers);
        intent.putExtra("color", realColor);
        startActivity(intent);
        overridePendingTransition(R.animator.slide_in_right_to_left, R.animator.slide_out_right_to_left);
    }

    public void status(View view){
        int id = view.getId();
        if (id == R.id.inprog) {
        } else if (id == R.id.finished) {
        }
    }

    public void goBack(View view) {
        // Update changed to FireBase
        Date date = new Date(mYear - 1900, mMonth, mDay);
        taskRef.child("name").setValue(taskName.getText().toString());
        taskRef.child("content").setValue(taskContent.getText().toString());
        taskRef.child("dueDate").setValue(date);

        DatabaseReference taskRef2 = fb.child("projects").child(projectFireBaseID).child("tasks").child(taskFireBaseKey);
        taskRef2.child("name").setValue(taskName.getText().toString());
        taskRef2.child("content").setValue(taskContent.getText().toString());
        taskRef2.child("dueDate").setValue(date);

        finish();
        overridePendingTransition(R.animator.slide_in_left_to_right, R.animator.slide_out_left_to_right);
    }
}
