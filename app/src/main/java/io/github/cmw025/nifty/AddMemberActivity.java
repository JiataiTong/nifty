package io.github.cmw025.nifty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import io.github.cmw025.nifty.RecyclerViewCheckboxAdapter.MemberModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddMemberActivity extends AppCompatActivity {

    ArrayList<MemberModel> memberModels;
    ListView listView;
    private RecyclerViewCheckboxAdapter adapter;
    private String projectFireBaseID;
    private String taskFireBaseKey;
    private HashSet<MemberModel> currentMembers;
    private DatabaseReference projectRef;
    private DatabaseReference taskRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        // Set up list adapter
        memberModels = new ArrayList();
        adapter = new RecyclerViewCheckboxAdapter(memberModels, getApplicationContext());

        projectFireBaseID = getIntent().getStringExtra("projectFireBaseID");
        taskFireBaseKey = getIntent().getStringExtra("taskFireBaseKey");
        currentMembers = (HashSet<MemberModel>) getIntent().getSerializableExtra("currentMembers");

        // Set up FireBase
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        projectRef = fb.child("projects").child(projectFireBaseID);
        taskRef = fb.child("tasks").child(taskFireBaseKey);

        // Get project member list
        projectRef.child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                for (DataSnapshot child : data.getChildren()) {
                    MemberModel member = child.getValue(MemberModel.class);
                    member.checked = currentMembers.contains(member);
                    memberModels.add(member);
                }
                adapter.updateItems(memberModels);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                RecyclerViewCheckboxAdapter.MemberModel clickedMember= memberModels.get(position);
                clickedMember.checked = !clickedMember.checked;
                if (clickedMember.checked ) {
                    currentMembers.add(clickedMember);
                    String name = clickedMember.getName();
                    Log.v("member: ", name + " checked");
                    for (MemberModel member : currentMembers) {
                        String memberName = member.getName();
                        Log.v("member", "currentMember contains: " + memberName);
                    }
                }
                else {
                    currentMembers.remove(clickedMember);
                    String name = clickedMember.getName();
                    Log.v("member: ", name + " unchecked");
                    for (MemberModel member : currentMembers) {
                        String memberName = member.getName();
                        Log.v("member", "currentMember contains: " + memberName);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }



    public void goBack(View view) {
        if (currentMembers != null) {
            if (!currentMembers.isEmpty()) {
                taskRef.child("members").setValue(new ArrayList(currentMembers));
            }
            else {
                taskRef.child("members").removeValue();
            }
        }
        else {
            taskRef.child("members").removeValue();
        }
        finish();
        overridePendingTransition(R.animator.slide_in_left_to_right, R.animator.slide_out_left_to_right);
    }
}
