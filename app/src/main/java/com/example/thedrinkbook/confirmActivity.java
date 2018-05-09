package com.example.thedrinkbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class confirmActivity extends AppCompatActivity {


    Button bntLogOutConfirm, bntOK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        bntLogOutConfirm = findViewById(R.id.bntLogOutConfirm);
        bntLogOutConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        bntOK = findViewById(R.id.bntOK);
        bntOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finishActivity(0);

            }
        });

    }

}
