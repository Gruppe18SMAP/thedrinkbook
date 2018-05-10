package com.example.thedrinkbook;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BackgroundService extends Service {

    private static final String PAYED = "Payed for drinks";
    private static final String LOG = "service";
    private static final String BOUGHTDRINKS = "Drinks bought" ;
    static final String BROADCAST_BACKGROUNDSERVICE_LOAD = "broadcast from background with result from load from database";
    static final String LOAD_RESULT = "Result from load from database" ;
    private boolean started = false;

    DatabaseReference drinkDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseDrinks = drinkDatabase.child("Drinks");
    ArrayList<Drink> drinksList;
    long drinksCount = 0;
    int snapshotCount = 0;

    public class BackgroundServiceBinder extends Binder {
        BackgroundService getService(){
            return BackgroundService.this;
        }
    }

    private final IBinder binder = new BackgroundServiceBinder();

    public BackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        drinksList = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loadFromDrinksDatabase();

        return START_NOT_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    private void loadFromDrinksDatabase() {
        drinkDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                if(dataSnapshot.getKey().equals("Drinks")){
                    drinksCount = dataSnapshot.getChildrenCount();
                    loadDrinks();
                }
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

    private void loadDrinks() {
        databaseDrinks.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Drink drink = dataSnapshot.getValue(Drink.class);
                drink.Key = dataSnapshot.getKey();
                drinksList.add(drink);
                snapshotCount = snapshotCount+1;
                if(snapshotCount >= drinksCount)
                {
                    broadcastLoadResult(drinksList);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Drink drink = dataSnapshot.getValue(Drink.class);
                drink.Key = dataSnapshot.getKey();

                for(int i = 0; i < drinksList.size(); i++){
                    if(drinksList.get(i).Key == drink.Key){
                        drinksList.remove(i);
                        drinksList.add(i,drink);
                        broadcastLoadResult(drinksList);
                    }
                }

                /*Iterator<Drink> iterator = drinksList.iterator();

                while(iterator.hasNext()){
                    Drink dbdrink = iterator.next();
                    if(dbdrink.Key == drink.Key){
                        int index = drinksList.indexOf(dbdrink);
                        iterator.remove();
                        drinksList.add(index, drink);
                    }
                }*/

                /*for(Drink dbdrink : drinksList){
                    if(dbdrink.Key == drink.Key){
                        int index = drinksList.indexOf(dbdrink);
                        drinksList.remove(dbdrink);
                        drinksList.add(index, drink);
                    }
                }*/


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


    private void broadcastLoadResult(ArrayList<Drink> listOfDrinks) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_BACKGROUNDSERVICE_LOAD);
        broadcastIntent.putExtra(LOAD_RESULT, listOfDrinks);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void boughtFromDatabase(ArrayList<Drink> boughtDrinks) {
        for(Drink boughtDrink : boughtDrinks) {
            for(Drink dbDrink : drinksList) {
                if(boughtDrink.Key.equals(dbDrink.Key)) {
                    int antal = dbDrink.Antal - boughtDrink.Antal;
                    databaseDrinks.child(dbDrink.Key).child("Antal").setValue(antal);
                }
            }
        }


        /*final Drink[] databaseDrink = new Drink[1];
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
        }*/
    }

    //Husk at kalde setNotification på den listener der har til funktion af tjekke hvor mange der er tilbage
    //Notifikationfunktionalitet

    Notification notification;
    Notifications notifications;
    private int notificationsID = 5;
    private SimpleDateFormat timeFormat;
    private Timestamp timestamp;

    private void setNotification() {
        notifications = new Notifications(this);

        notification = new NotificationCompat.Builder(this, Notifications.CHANNEL_ID)
                .setSmallIcon(R.mipmap.drinkslogo)
                .setContentText("Der er mindre end 5 stk af en drikkevarer")
                .setContentTitle("Thedrinkbook")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(notifications.pendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationsID, notification);



    }




}
