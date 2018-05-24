package com.example.thedrinkbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class confirmActivity extends AppCompatActivity {


    Button bntLogOutConfirm, bntOK;
    int CONFIRM_REQUEST_CODE = 142;
    final static String LOG = "ConfirmActivity";
    public static int RESULT_LOGOUT = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        // log-ud funktion
        bntLogOutConfirm = findViewById(R.id.bntLogOutConfirm);
        bntLogOutConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Log.d(LOG, "User loggedout");
                setResult(RESULT_LOGOUT);
                finish();
            }
        });

        bntOK = findViewById(R.id.bntOK);
        // sender brugeren til overview
        bntOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(RESULT_OK);
                finish();

            }
        });

    }

}
