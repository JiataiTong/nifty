package io.github.cmw025.nifty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.relex.circleindicator.CircleIndicator;

public class ProjectActivity extends FragmentActivity {
    private static final int NUM_PAGES = 4;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */

    private PagerAdapter mPagerAdapter;

    private int realColor;
    private String projectFireBaseID;
    private String projectName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        projectFireBaseID = getIntent().getStringExtra("projectFireBaseID");
        projectName = getIntent().getStringExtra("projectName");

        setContentView(R.layout.viewpager);
        TextView display = findViewById(R.id.project_name);
        display.post(new Runnable(){
            @Override
            public void run() {
                display.setText(projectName);
            }
        });

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);


        Toolbar toolbar = findViewById(R.id.toolbar);

        // FireBase
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("color").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                if (data.getValue() != null) {
                    // Set ToolBar color
                    long l = (long) data.getValue();
                    int projectColor = (int) l;
                    realColor = ContextCompat.getColor(ProjectActivity.this, projectColor);
                    toolbar.setBackgroundColor(ContextCompat.getColor(ProjectActivity.this, projectColor));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new ListFragment();
                    break;
                case 1:
                    fragment = new ProjectInfoFragment();
                    break;
                case 2:
                    fragment = new CalendarFragment();
                    break;
                case 3:
                    fragment = new ProjectInfoFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public void goBack(View view) {
        finish();
    }

    public void setSettings(View view) {
        Intent intent = new Intent(ProjectActivity.this, ProjectSettingsActivity.class);
        intent.putExtra("realColor", realColor);
        intent.putExtra("projectFireBaseID", projectFireBaseID);
        intent.putExtra("projectName", projectName);
        startActivity(intent);
    }

    public void startChat(View view) {
        Intent intent = new Intent(this, ChattingActivity.class);
        startActivity(intent);
    }
}


