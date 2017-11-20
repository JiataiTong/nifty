package io.github.cmw025.nifty;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.github.cmw025.nifty.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FireBase Sign In if necessary
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();

<<<<<<< HEAD

        //DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        //fb.child("name").setValue(123);
        //Firebase ayy = fb.child("name");


=======
>>>>>>> ccd7209a86d7ee6482060fef3297e775fd14e16a
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            startActivity(intent);
        }

        // Save user profile to FireBase
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        String uid = currentUser.getUid();
        DatabaseReference profile = fb.child("usrs").child(uid);
        profile.child("name").setValue(currentUser.getDisplayName());
        profile.child("email").setValue(currentUser.getEmail());
        profile.child("phone").setValue(currentUser.getPhoneNumber());

        // Default fragment to list
        Fragment fragment = new ListFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.commit();

        // BottomNavigationBar
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.tasklist, "Tasks"))
                .addItem(new BottomNavigationItem(R.drawable.calendar, "Calendar"))
                .addItem(new BottomNavigationItem(R.drawable.repo, "Projects"))
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                Fragment fragment = null;
                //start
                switch(position) {
                    case 0:
                        fragment = new ListFragment();
                        break;
                    case 1:
                        fragment = new CalendarFragment();
                        break;
                    case 2:
                        fragment = new ProjectListFragment();
                        // Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
                        // myIntent.putExtra("key", value); //Optional parameters
                        // startActivity(intent);
                        break;
                }
                if (fragment != null) {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.contentFragment, fragment);
                    transaction.commit();
                }
            }
            @Override
            public void onTabUnselected(int position) {
            }
            @Override
            public void onTabReselected(int position) {
            }
        });
    }
}
