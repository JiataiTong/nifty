package io.github.cmw025.nifty;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PersonalInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView displayName = findViewById(R.id.user_name);
        displayName.setText(user.getDisplayName());

        TextView email = findViewById(R.id.email);
        email.setText(user.getEmail());


        Button signOutBtn = findViewById(R.id.sign_out_btn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                // Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(PersonalInfoActivity.this, LoginActivity.class));
            }
        });
    }

    public void goBack(View view) {
        finish();
    }
}
