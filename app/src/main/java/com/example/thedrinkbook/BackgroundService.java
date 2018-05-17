package com.example.thedrinkbook;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BackgroundService extends Service {

    private static final String PAYED = "Payed for drinks";
    private static final String LOG = "service";
    private static final String BOUGHTDRINKS = "Drinks bought" ;
    static final String BROADCAST_BACKGROUNDSERVICE_LOAD = "broadcast from background with result from load from database";
    static final String LOAD_RESULT = "Result from load from database" ;

    static  final String msg = "BackgroundService";
    private boolean started = false;


    DatabaseReference drinkDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseDrinks = drinkDatabase.child("Drinks");
    private StorageReference mStorageRef;
    ArrayList<Drink> drinksList;
    ArrayList<Icon> icons;
    long drinksCount = 0;
    int snapshotCount = 0;

    private Handler handler;
    private Context iconContext;
    private String iconUrl;
    private ImageView iconView;
    private int iconCount = 0, elementCount = 0;
    private String iconStartAddress = "https://firebasestorage.googleapis.com";
    private String iconEndAdress = ".jpg?alt=media&token=5f289d8b-1b11-4246-a7d3-7a9d02826d97";


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
        handler = new Handler();
        icons = new ArrayList<Icon>();
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://thedrinkbook.appspot.com");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loadFromDrinksDatabase();
        Log.d(msg, "The backgroundService is started");
        return START_NOT_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    public void loadFromDrinksDatabase() {
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

                Log.d(msg, "Drinks is changes");
                for(int i = 0; i < drinksList.size(); i++){
                    if(drinksList.get(i).Key.equals(drink.Key)){
                        drinksList.remove(i);
                        drinksList.add(i,drink);
                        broadcastLoadResult(drinksList);
                    }
                }
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

    public void startloadIconRunnable(Context c, String url, ImageView imageview){
        Icon icon = new Icon(c, url, imageview);
        icons.add(icon);
        iconCount = iconCount+1;
        elementCount = drinksList.size();
        if(iconCount == elementCount) {
            handler.post(runnableLoadIcon);
            iconCount = 0; elementCount = 0;
        }
    }

    /**
     * Runnable for load of icons from storage
     */
    private Runnable runnableLoadIcon = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacksAndMessages(runnableLoadIcon);
            loadIcon();
            icons.clear();
        }
    };

    private void loadIcon() {
        for (Icon icon : icons) {
            //String url = iconStartAddress+icon.url+iconEndAdress;
            Picasso.with(icon.c).load(icon.url).into(icon.imageview);
        }
    }

    public ArrayList<Drink> getDrinksList() {
        return drinksList;
    }

    private void broadcastLoadResult(ArrayList<Drink> listOfDrinks) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_BACKGROUNDSERVICE_LOAD);
        broadcastIntent.putExtra(LOAD_RESULT, listOfDrinks);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
        Log.d(msg, "Brodcasting...");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return binder;
    }

    public void boughtFromDatabase(ArrayList<Drink> boughtDrinks) {
        for(Drink boughtDrink : boughtDrinks) {
            for(Drink dbDrink : drinksList) {
                if(boughtDrink.Key.equals(dbDrink.Key)) {
                    int antal = dbDrink.Antal - boughtDrink.Antal;
                    databaseDrinks.child(dbDrink.Key).child("Antal").setValue(antal);
                    Log.d(msg, "Amount of drinks is updated");
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

    public void updateAmount(ArrayList<Drink> updatedDrinks){
        for(Drink updatedDrink : updatedDrinks) {
            for(Drink dbDrink : drinksList) {
                if(updatedDrink.Key.equals(dbDrink.Key)) {
                    int amount = dbDrink.Antal + updatedDrink.Antal;
                    databaseDrinks.child(dbDrink.Key).child("Antal").setValue(amount);
                    Log.d(msg, "Amount is updated");
                }
            }
        }

    }

    public void addProduct(Drink drink)
    {
        databaseDrinks.child(drink.Key).setValue(drink);
        Log.d(msg, "Product is added");
    }

    //Is inspiret from https://stackoverflow.com/questions/40885860/how-to-save-bitmap-to-firebase;
    public void uploadIconToStorage(final String key, Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        byte[] data = outputStream.toByteArray();
        StorageReference reference = mStorageRef.child(key);

        UploadTask UT = reference.putBytes(data);
        UT.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri iconUri = taskSnapshot.getDownloadUrl();
                String iconString = iconUri.getPath();
                databaseDrinks.child(key).child("Ikon").setValue(iconString);
                Log.d(msg, "Billedet er uploadet til firebase storage");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(msg, "Billedet er ikke uplodaet til firebase storage");
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //drinksList.clear();
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
                .setContentText(getResources().getString(R.string.lessthanfivedrinksleft))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(notifications.pendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationsID, notification);
    }


}
