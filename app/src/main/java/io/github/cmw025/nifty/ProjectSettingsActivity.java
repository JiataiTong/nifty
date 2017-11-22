package io.github.cmw025.nifty;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioGroup;

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

        // Default to red
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
    public void goBack(View view) {
        finish();
    }

    public void saveSettings(View view) {
        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("color").setValue(color);
        finish();
    }
}
