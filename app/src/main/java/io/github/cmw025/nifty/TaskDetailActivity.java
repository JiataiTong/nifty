package io.github.cmw025.nifty;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        tv = (TextView) findViewById(R.id.details);
    }

    public void addMembers(View view) {}

    public void editText(View view) {
        tv.setText("lalalalalala");
    }

    public void setDue(View view) {}

}
