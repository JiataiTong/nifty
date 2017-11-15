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
import java.util.Calendar;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tv;
    int mYear, mMonth, mDay;
    Button btn;
    TextView dateDisplay;
    final int DATE_DIALOG = 1;
    private EditText editText;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        tv = (TextView) findViewById(R.id.details);
        btn = (Button) findViewById(R.id.dateChoose);
        dateDisplay = (TextView) findViewById(R.id.dateChoose);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });

        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        editText = (EditText) findViewById(R.id.edit_text);
        mTextView = (TextView) findViewById(R.id.textview);
        editText.addTextChangedListener(mTextWatcher);
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            temp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            editStart = editText.getSelectionStart();
            editEnd = editText.getSelectionEnd();
            mTextView.setText(temp.length() + " letters input");
            if (temp.length() > 2000) {
                Toast.makeText(TaskDetailActivity.this,
                        "the note is too long!", Toast.LENGTH_SHORT)
                        .show();
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                editText.setText(s);
                editText.setSelection(tempSelection);
            }
        }
    };


    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    public void display() {
        dateDisplay.setText(new StringBuffer().append(mMonth + 1).append("-").append(mDay).append("-").append(mYear).append(" "));
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
        startActivity(intent);
        overridePendingTransition(R.animator.slide_in_right_to_left, R.animator.slide_out_right_to_left);
    }

    public void editText(View view) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Toast.makeText(TaskDetailActivity.this, String.valueOf(actionId), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public void setDue(View view){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });

        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
    }


    public void status(View view){
        int id = view.getId();
        if (id == R.id.inprog) {
        } else if (id == R.id.finished) {
        }
    }

    public void goBack(View view) {
        finish();
        overridePendingTransition(R.animator.slide_in_left_to_right, R.animator.slide_out_left_to_right);
    }

}
