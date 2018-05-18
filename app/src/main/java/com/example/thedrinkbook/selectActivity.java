package com.example.thedrinkbook;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
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

    // Widgets from layout
    private Button btnBuy, btnLogout;
    private ListView lvDrinks;

    // List for drinks
    private ArrayList<Drink> drinks, selectedDrinks, emptyInDatabase;

    //  Adapter
    private selectAdaptor listviewAdapter;

    // For Service
    private BackgroundService bgservice;
    private Intent serviceIntent;

    // For the intent starting the Buy activity
    public static final String SELECTEDDRINKS = "Selected drinks";

    // For log messages
    final static String LOG = "SelectActivity";
    
    private int firstVisibleItem;
    private int lastVisibleItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        // Start service
        serviceIntent = new Intent(this, BackgroundService.class);
        startService(serviceIntent);
        Log.d(LOG, "Starts backgroundservice");

        // Bind to service
        bindService(serviceIntent,serviceConnection,Context.BIND_AUTO_CREATE);
        Log.d(LOG, "Binded to backgroundservice");

        initializeObjects();

        // Create filter to receive broadcasts from Service
        IntentFilter filter = new IntentFilter();
        filter.addAction(BackgroundService.BROADCAST_BACKGROUNDSERVICE_LOAD);
        LocalBroadcastManager.getInstance(this).registerReceiver(onBackgroundServiceLoadResult, filter);

        /*
         * Implementation for onSaveInstanceState started but due to different "sizes" of the listview in portrait contra landscape mode
         * the implementation has been stopped and time used on other functionalities
         */
        /* if(savedInstanceState != null){
            ArrayList<Integer> amounts = savedInstanceState.getIntegerArrayList("amount");

            for (int e = 0; e < amounts.size(); e++){
                Integer amount = amounts.get(e);
                View childAt = lvDrinks.getChildAt(e);
                EditText viewById = childAt.findViewById(R.id.txtAmount);
                viewById.setText(amount.toString());
            }
        }*/
    }

    /*
     *Implementation started but due to different "sizes" of the listview in portrait contra landscape mode
     * the implementation has been stopped and time used on other functionalities
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        /*int elements = lvDrinks.getAdapter().getCount();

        ArrayList<Integer> amounts = new ArrayList<>();


        for (int e = 0; e < elements; e++) {
            View listView = lvDrinks.getChildAt(e);
            final TextView tvName = listView.findViewById(R.id.txtDrinkname);
            EditText etAmount = listView.findViewById(R.id.txtAmount);
            String stringAmount = etAmount.getText().toString();

            Integer intAmount = 0;
            if (!stringAmount.equals("")) {
                intAmount = Integer.parseInt(stringAmount);
            }

            amounts.add(intAmount);
        }

        outState.putIntegerArrayList("amount", amounts);*/

        super.onSaveInstanceState(outState);
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
        
        firstVisibleItem = lvDrinks.getFirstVisiblePosition();
        lastVisibleItem = lvDrinks.getLastVisiblePosition();

        btnBuy.setOnClickListener(selectActivity.this);
        btnLogout.setOnClickListener(selectActivity.this);

        // Set adapter for the listview
        listviewAdapter = new selectAdaptor(this,drinks);
        lvDrinks.setAdapter(listviewAdapter);

        lvDrinks.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                firstVisibleItem = lvDrinks.getFirstVisiblePosition();
                lastVisibleItem = lvDrinks.getLastVisiblePosition();
            }
        });
    }

    // Connection to service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BackgroundService.BackgroundServiceBinder binder = (BackgroundService.BackgroundServiceBinder) iBinder;
            bgservice = binder.getService();
            listviewAdapter.updateDrinks(drinks, bgservice, firstVisibleItem, lastVisibleItem);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bgservice = null;
        }
    };

    // BroadcastReceiver to receive from Service
    private BroadcastReceiver onBackgroundServiceLoadResult = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra from the broadcast
            drinks = (ArrayList<Drink>)intent.getSerializableExtra(BackgroundService.LOAD_RESULT);
            // Notify adapter to update listview
            listviewAdapter.updateDrinks(drinks, bgservice, firstVisibleItem, lastVisibleItem);
            Log.d(LOG, "Broadcast received");
        }
    };

    @Override
    public void onClick(View view) {
        // Get the id of the view the click was on
        int viewId = view.getId();
        // If it's on the buy button
        if(viewId == R.id.bntBuy){
            // Find number of elements in the listview
            int elements = lvDrinks.getAdapter().getCount();
            // If a purchase has been made earlier on, then the list has old data and should be cleared
            if(!selectedDrinks.isEmpty()){
                selectedDrinks.clear();
            }
            // If a purchase has been made earlier on, then the list has old data and should be cleared
            if(!emptyInDatabase.isEmpty()){
                emptyInDatabase.clear();
            }

            String[] amounts = listviewAdapter.getAmounts();

            for(int i = 0; i < amounts.length; i++){
                if(amounts[i] != null){
                    Drink selectedDrink = new Drink();
                    if(drinks.get(i).Antal >= Integer.parseInt(amounts[i])){
                        selectedDrink = new Drink(drinks.get(i));
                        selectedDrink.Antal = Integer.parseInt(amounts[i]);
                    }
                    else{
                        emptyInDatabase.add(drinks.get(i));
                    }

                    if(selectedDrink.Navn != null){
                        selectedDrinks.add(selectedDrink);
                    }
                }
            }

            /*
            // Run through elements in listview
                for (int e = 0; e < elements; e++) {
                    View listView = lvDrinks.getChildAt(e);
                    final TextView tvName = listView.findViewById(R.id.txtDrinkname);
                    EditText etAmount = listView.findViewById(R.id.txtAmount);
                    // Find the amount for the element
                    String stringAmount = etAmount.getText().toString();

                    Integer intAmount = 0;
                    // If the amount is not empty, then convert the amount to integer
                    if (!stringAmount.equals("")) {
                        intAmount = Integer.parseInt(stringAmount);
                    }

                    // If the amount is not zero, then the user wants to buy an amount of the product at that element
                    if (intAmount != 0) {
                        // Prepare a drink object
                        Drink selectedDrink = new Drink();

                        // Run through the drinks in the database-list
                        for (Drink drink : drinks) {
                            // Compare element-name and database-name
                            if (drink.Navn.equals(tvName.getText().toString())) {
                                // Check whether it's possible to buy that amount of the product
                                if(drink.Antal >= intAmount) {
                                    selectedDrink = new Drink(drink);
                                    selectedDrink.Antal = intAmount;
                                }
                                else{
                                    emptyInDatabase.add(drink);
                                }
                            }
                        }
                        // If there is truely created a selected Drink object
                        if(selectedDrink.Navn != null) {
                            selectedDrinks.add(selectedDrink);
                        }
                    }
                }
                */

                // If there are any selected drinks
                if(selectedDrinks.size() != 0) {
                    //OPEN BUY ACTIVITY
                    Intent buyIntent = new Intent(selectActivity.this, BuyActivity.class);
                    buyIntent.putExtra(SELECTEDDRINKS, selectedDrinks);
                    startActivityForResult(buyIntent, 12);
                }
                // Else, if there have not been found an amount that was too many compared to the amount in database
                // then there must be no products selected
                else if(emptyInDatabase.isEmpty()){
                    Toast noItemsToast = Toast.makeText(selectActivity.this,R.string.no_chosen_items, Toast.LENGTH_LONG);
                    noItemsToast.show();
                }
                // Else, if the list is not empty, then the user has choosen a too big amount of one or more products compared to the stock in the database
                else if(!emptyInDatabase.isEmpty()){
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
        // If the click is on the log out button, then sign out the user and finish the activity
        else if(viewId == R.id.bntLogout){
            FirebaseAuth.getInstance().signOut();
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If returning from an intent created from this activity
        if(requestCode == 12){
            if(resultCode == RESULT_CANCELED){
                // If return/cancelled is hit, then nothing should be done, just return to the same activity
            }
            // Returning on an OK result
            // Then the amount should be cleared, as the user is returning from having already made a purchase
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // When this activity is destroyed, then it should unregister to broadcast,
        // unbind to the service
        // and stop the service
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onBackgroundServiceLoadResult);
        unbindService(serviceConnection);
        stopService(serviceIntent);
        Log.d(LOG, "Unbinded to backgroundservice");
    }


}
