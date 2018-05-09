package com.example.thedrinkbook;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class BackgroundService extends Service {

    private static final String PAYED = "Payed for drinks";
    private static final String LOG = "service";
    private static final String BOUGHTDRINKS = "Drinks bought" ;
    private boolean started = false;

    DatabaseReference drinkDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseDrinks = drinkDatabase.child("Drinks");
    ArrayList<Drink> drinksList;

    public BackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadFromDrinksDatabase();

    }

    private void loadFromDrinksDatabase() {
        databaseDrinks.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Drink drink = dataSnapshot.getValue(Drink.class);
                drink.Key = dataSnapshot.getKey();
                drinksList.add(drink);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action){
            case PAYED:
                ArrayList<Drink> boughtDrinks = (ArrayList<Drink>) intent.getSerializableExtra(BOUGHTDRINKS);
                boughtFromDatabase(boughtDrinks);
        }



        return START_NOT_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void boughtFromDatabase(ArrayList<Drink> boughtDrinks) {
        final Drink[] databaseDrink = new Drink[1];
        for(Drink drink : boughtDrinks){
            DatabaseReference key = databaseDrinks.child(drink.Key);
            key.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    databaseDrink[0] = dataSnapshot.getValue(Drink.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            int value = databaseDrink[0].Antal - drink.Antal;
            key.child("Antal").setValue(value);
        }
    }




}
