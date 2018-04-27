package com.example.thedrinkbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

public class selectActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnBuy, btnLogout;
    ListView lvDrinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        initializeObjects();
    }

    private void initializeObjects() {
        btnBuy = findViewById(R.id.bntBuy);
        btnLogout = findViewById(R.id.bntLogout);
        lvDrinks = findViewById(R.id.lvDrinksUser);

        btnBuy.setOnClickListener(selectActivity.this);
        btnLogout.setOnClickListener(selectActivity.this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if(viewId == R.id.bntBuy){
            //OPEN BUY ACTIVITY
        }
        else if(viewId == R.id.bntLogout){
            FirebaseAuth.getInstance().signOut();
            finish();
        }

    }
}
