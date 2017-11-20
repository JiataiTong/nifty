package io.github.cmw025.nifty;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.github.cmw025.nifty.RecyclerViewCheckboxAdapter.DataModel;

import java.util.ArrayList;

public class AddMemberActivity extends AppCompatActivity {

    ArrayList<DataModel> dataModels;
    ListView listView;
    private RecyclerViewCheckboxAdapter adapter;
    private String ProjectFireBaseID;
    private ArrayList<String> currentMembers;
    private DatabaseReference projectRef;
    private DatabaseReference taskRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        // Set up list adapter
        dataModels = new ArrayList();
        adapter = new RecyclerViewCheckboxAdapter(dataModels, getApplicationContext());

        ProjectFireBaseID = getIntent().getStringExtra("projectFireBaseID");
        currentMembers = getIntent().getStringArrayListExtra("currentMembers");

        // Set up FireBase
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        projectRef = fb.child("projects").child(uid).child(ProjectFireBaseID);

        // Get project member list
        projectRef.child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                for (DataSnapshot child : data.getChildren()) {
                    String name = child.getKey();
                    boolean alreadyOnTask = currentMembers.contains(name);
                    DataModel member = new DataModel(name, alreadyOnTask);
                    dataModels.add(member);
                }
                adapter.updateItems(dataModels);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView = (ListView) findViewById(R.id.listView);

//        dataModels.add(new DataModel("Jimmy Wei", false));
//        dataModels.add(new DataModel("Sonia", false));
//        dataModels.add(new DataModel("GWW", false));
//        dataModels.add(new DataModel("Troy Tong", true));

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                DataModel dataModel= dataModels.get(position);
                dataModel.checked = !dataModel.checked;
                adapter.notifyDataSetChanged();
            }
        });
    }
    public void goBack(View view) {
        finish();
        overridePendingTransition(R.animator.slide_in_left_to_right, R.animator.slide_out_left_to_right);
    }
}
