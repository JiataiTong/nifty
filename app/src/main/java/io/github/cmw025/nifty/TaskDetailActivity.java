package io.github.cmw025.nifty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import java.util.List;

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
    private DatabaseReference taskRef;
    private String projectFireBaseID;

    private ArrayList<String> currentMembers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskName = (EditText) findViewById(R.id.fragment_note_title);
        taskContent = (EditText) findViewById(R.id.task_content);

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

        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        taskRef = fb.child("tasks").child(uid).child(taskFireBaseKey);

        taskRef.child("project").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                projectFireBaseID = (String) data.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                TaskModel task = data.getValue(TaskModel.class);

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
                currentMembers = new ArrayList<>();
                for (DataSnapshot child : data.getChildren()) {
                    currentMembers.add(child.getKey());
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
        intent.putStringArrayListExtra("currentMembers", currentMembers);
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
        TaskModel newTask = new TaskModel(taskName.getText().toString(), taskContent.getText().toString(), date, date, taskListID, taskFireBaseKey);
        taskRef.setValue(newTask);
        finish();
        overridePendingTransition(R.animator.slide_in_left_to_right, R.animator.slide_out_left_to_right);
    }

}
