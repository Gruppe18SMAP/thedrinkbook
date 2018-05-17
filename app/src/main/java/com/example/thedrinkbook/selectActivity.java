package com.example.thedrinkbook;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class selectActivity extends AppCompatActivity implements View.OnClickListener {


    DatabaseReference drinkDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseDrinks = drinkDatabase.child("Drinks");

    Button btnBuy, btnLogout;
    ListView lvDrinks;
    ArrayList<Drink> drinks, selectedDrinks, emptyInDatabase;
    private selectAdaptor listviewAdapter;
    private BackgroundService bgservice;
    Intent serviceIntent;
    final static String LOG = "SelectActivity";

    // For the intent starting the Buy activity
    public static final String SELECTEDDRINKS = "Selected drinks";
    public static final String DATABASEDRINKS = "Drinks in database";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        serviceIntent = new Intent(this, BackgroundService.class);
        startService(serviceIntent);
        Log.d(LOG, "Starts backgroundservice");

        bindService(serviceIntent,serviceConnection,Context.BIND_AUTO_CREATE);
        Log.d(LOG, "Binded to backgroundservice");

        initializeObjects();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BackgroundService.BROADCAST_BACKGROUNDSERVICE_LOAD);
        LocalBroadcastManager.getInstance(this).registerReceiver(onBackgroundServiceLoadResult, filter);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initializeObjects() {
        drinks = new ArrayList<>();
        selectedDrinks = new ArrayList<>();
        emptyInDatabase = new ArrayList<>();
        btnBuy = findViewById(R.id.bntBuy);
        btnLogout = findViewById(R.id.bntLogout);
        lvDrinks = findViewById(R.id.lvDrinksUser);

        btnBuy.setOnClickListener(selectActivity.this);
        btnLogout.setOnClickListener(selectActivity.this);

        listviewAdapter = new selectAdaptor(this,drinks);
        lvDrinks.setAdapter(listviewAdapter);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BackgroundService.BackgroundServiceBinder binder = (BackgroundService.BackgroundServiceBinder) iBinder;
            bgservice = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bgservice = null;
        }
    };

    private BroadcastReceiver onBackgroundServiceLoadResult = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            drinks = (ArrayList<Drink>)intent.getSerializableExtra(BackgroundService.LOAD_RESULT);
            listviewAdapter.updateDrinks(drinks, bgservice);
            Log.d(LOG, "Broadcast recived");
        }
    };

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if(viewId == R.id.bntBuy){
            int elements = lvDrinks.getAdapter().getCount();
            if(!selectedDrinks.isEmpty()){
                selectedDrinks.clear();
            }
            if(!emptyInDatabase.isEmpty()){
                emptyInDatabase.clear();
            }

                for (int e = 0; e < elements; e++) {
                    View listView = lvDrinks.getChildAt(e);
                    final TextView tvName = listView.findViewById(R.id.txtDrinkname);
                    TextView tvPrice = listView.findViewById(R.id.txtDrinkPrice);
                    EditText etAmount = listView.findViewById(R.id.txtAmount);
                    String stringAmount = etAmount.getText().toString();

                    Integer intAmount = 0;
                    if (!stringAmount.equals("")) {
                        intAmount = Integer.parseInt(stringAmount);
                    }

                    if (intAmount != 0) {
                        Drink selectedDrink = new Drink();

                        for (Drink drink : drinks) {
                            if (drink.Navn.equals(tvName.getText().toString())) {
                                if(drink.Antal >= intAmount) {
                                    selectedDrink = new Drink(drink);
                                    selectedDrink.Antal = intAmount;
                                }
                                else{
                                    emptyInDatabase.add(drink);
                                }
                            }
                        }
                        if(selectedDrink.Navn != null) {
                            selectedDrinks.add(selectedDrink);
                        }
                    }
                }

                if(selectedDrinks.size() != 0) {
                    //OPEN BUY ACTIVITY
                    Intent buyIntent = new Intent(selectActivity.this, BuyActivity.class);
                    buyIntent.putExtra(SELECTEDDRINKS, selectedDrinks);
                    startActivityForResult(buyIntent, 12);
                } else if(emptyInDatabase.isEmpty()){
                    Toast noItemsToast = Toast.makeText(selectActivity.this,R.string.no_chosen_items, Toast.LENGTH_LONG);
                    noItemsToast.show();
                } else if(!emptyInDatabase.isEmpty()){
                    String emptyDrinks = new String();
                    for(int e = 0; e < emptyInDatabase.size(); e++){
                        if(e == 0){
                            emptyDrinks = emptyDrinks + emptyInDatabase.get(e).Navn;
                        }
                        else{
                            emptyDrinks = emptyDrinks + ", " + emptyInDatabase.get(e).Navn;
                        }
                    }

                    String text = getResources().getString(R.string.empty_database,emptyDrinks);
                    Toast emptyDBToast = Toast.makeText(selectActivity.this, text, Toast.LENGTH_LONG);
                    emptyDBToast.show();
                }

            Log.d(LOG, "Drinks selected");

            }
        else if(viewId == R.id.bntLogout){
            FirebaseAuth.getInstance().signOut();
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 12){
            if(resultCode == RESULT_CANCELED){

            }
            if (resultCode == RESULT_OK){
                int elements = lvDrinks.getAdapter().getCount();
                for (int e = 0; e < elements; e++) {
                    View listView = lvDrinks.getChildAt(e);
                    EditText etAmount = listView.findViewById(R.id.txtAmount);
                    etAmount.setText("");
                }
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onBackgroundServiceLoadResult);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        stopService(serviceIntent);
        Log.d(LOG, "Unbinded to backgroundservice");
    }


}
