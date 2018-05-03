package com.example.thedrinkbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class selectActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference drinkDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseDrinks = drinkDatabase.child("Drinks");

    Button btnBuy, btnLogout;
    ListView lvDrinks;
    ArrayList<Drink> drinks;
    private selectAdaptor listviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        initializeObjects();
        drinks = new ArrayList<>();

        databaseDrinks.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Drink drink = dataSnapshot.getValue(Drink.class);
                drinks.add(drink);
                listviewAdapter.updateDrinks(drinks);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeObjects() {
        btnBuy = findViewById(R.id.bntBuy);
        btnLogout = findViewById(R.id.bntLogout);
        lvDrinks = findViewById(R.id.lvDrinksUser);

        btnBuy.setOnClickListener(selectActivity.this);
        btnLogout.setOnClickListener(selectActivity.this);

        listviewAdapter = new selectAdaptor(this,drinks);
        lvDrinks.setAdapter(listviewAdapter);
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
