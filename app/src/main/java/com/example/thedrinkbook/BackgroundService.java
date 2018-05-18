package com.example.thedrinkbook;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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


    //When activities want to bind to the service, this methode is called.
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

                Log.d(msg, "Drink changes");
                for(int i = 0; i < drinksList.size(); i++){
                    if(drinksList.get(i).Key.equals(drink.Key)){
                        drinksList.remove(i);
                        drinksList.add(i,drink);
                        broadcastLoadResult(drinksList);
                    }
                }
                if(drink.Antal < 5)
                {
                    setNotification(drink.Navn);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Drink drink = dataSnapshot.getValue(Drink.class);
                drink.Key = dataSnapshot.getKey();


                for(int i = 0; i < drinksList.size(); i++){
                    if(drinksList.get(i).Key.equals(drink.Key)){
                        drinksList.remove(i);
                        broadcastLoadResult(drinksList);
                        removeIconToStorage(drink.Key);
                    }
                }
                Log.d(msg, "Drink is removed");
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

    public void startloadIconBuyRunnable(Context c, String url, ImageView imageview)
    {
        Icon icon = new Icon(c, url, imageview);
        icons.add(icon);
        handler.post(runnableLoadIcon);
        iconCount = 0; elementCount = 0;
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

    //loads the icon and showes it on a imageview.
    private void loadIcon() {
        for (Icon icon : icons) {
            Picasso.with(icon.c).load(icon.url).into(icon.imageview);
        }
    }

    //returns the list of drinks
    public ArrayList<Drink> getDrinksList() {
        return drinksList;
    }

    //sends the result to the activities when changes is detected
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

    //Is called when drinks is bought and updates the firebase with the new ammount.
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
    }

    //Is called when the admin has refilled the drinks and updates the firebase with the new ammount.
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

    // Is called when a product is added. updates the firebase with the new drink.
    public void addProduct(Drink drink)
    {
        databaseDrinks.child(drink.Key).setValue(drink);
        Log.d(msg, "Product is added");
    }

    //Is called when the admin has deleted a drink. updates firebase.
    public void removeProduct(Drink drink){
        databaseDrinks.child(drink.Key).removeValue();
    }

    //Is inspired from https://stackoverflow.com/questions/40885860/how-to-save-bitmap-to-firebase;
    //Is called when a new product is added. Converts the bitmap to a byte and puts the byte in firebase storage.
    // the url to storage location is get by a Uri.
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
                String iconstring = iconUri.getPath();
                String icontoken = iconUri.getQuery();
                String storageUrl = iconStartAddress+iconstring+"?"+icontoken;
                databaseDrinks.child(key).child("Ikon").setValue(storageUrl);
                Log.d(msg, "Billedet er uploadet til firebase storage");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(msg, "Icon is not uploaded to firebase storage");
            }
        });

    }

    //Deletes the icon in firebase storage when the admin deletes the product in firebase database.
    private void removeIconToStorage(String key) {
        StorageReference reference = mStorageRef.child(key);

        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(msg, "Icon removed from storage");
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

    private void setNotification(String navn) {
        notifications = new Notifications(this);

        notification = new NotificationCompat.Builder(this, Notifications.CHANNEL_ID)
                .setSmallIcon(R.mipmap.drinkslogo)
                .setContentText(String.format(getResources().getString(R.string.lessthanfivedrinksleft) + " "+ navn))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(notifications.pendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationsID, notification);
    }


    //public void savePreferences(ArrayList<Object> dataObject){
      //  SharedPreferences.Editor editor = getSharedPreferences("preferences", MODE_PRIVATE.edit();
        //editor.pu("Data", dataObject);
    //}


}
