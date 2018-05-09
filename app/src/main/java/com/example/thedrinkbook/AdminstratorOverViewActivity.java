package com.example.thedrinkbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

public class AdminstratorOverViewActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnRefill, btnLogout;
    ListView lvDrinks;

    static final int REQUEST_DRINKS= 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_adminstrator_over_view);

       initializeObjects();
    }

    private void initializeObjects() {
        btnRefill = findViewById(R.id.bntFill);
        btnLogout = findViewById(R.id.bntLogoutAdminOverview);
        lvDrinks = findViewById(R.id.lvDrinksAdmin);

        btnRefill.setOnClickListener(AdminstratorOverViewActivity.this);
        btnLogout.setOnClickListener(AdminstratorOverViewActivity.this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if(viewId == R.id.bntFill){
            //OPEN FILL ACTIVITY
            Intent addDrinksIntent = new Intent(AdminstratorOverViewActivity.this, AddDrinksActivity.class);
            startActivityForResult(addDrinksIntent, REQUEST_DRINKS);
        }
        else if(viewId == R.id.bntLogoutAdminOverview){
            FirebaseAuth.getInstance().signOut();
            finish();
        }

    }
}
