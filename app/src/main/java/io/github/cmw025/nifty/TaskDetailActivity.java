package io.github.cmw025.nifty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    public void addMembers(View view) {
        Intent intent = new Intent (this, AddMemberActivity.class);
        startActivity(intent);
    }

    public void setDue(View view) {}

    public void goBack(View view) {
        finish();
        overridePendingTransition(R.animator.slide_in_left_to_right, R.animator.slide_out_left_to_right);
    }

}
