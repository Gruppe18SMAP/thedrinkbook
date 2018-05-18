package com.example.thedrinkbook;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class AddDrinksActivity extends AppCompatActivity {

    Button bntSaveAddProduct, bntAddProduct, bntCancelAdmin;
    ListView lvAddDrinksAdmin;

    ArrayList<Drink> drinkList;
    AddDrinksAdaptor addDrinksAdaptor;
    BackgroundService bgservice;
    ArrayList<Drink> updateList;
    Intent serviceIntent;
    private int firstVisibleItem = 0, lastVisibleItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drinks);

        Initialiseobjects();

        bntAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddDrinksActivity.this, AddProductActivity.class);
                startActivityForResult(intent,1);
            }
        });


        bntCancelAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //adds the new amount to the drink and updated drink to a list of drinks.
        bntSaveAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int elements = lvAddDrinksAdmin.getAdapter().getCount();
                updateList.clear();

                String[] amounts = addDrinksAdaptor.getAmounts();

                for(int i=0; i < amounts.length; i++){
                    if(amounts[i] != null){
                        Drink updatedDrink = new Drink();
                        updatedDrink = new Drink(drinkList.get(i));
                        updatedDrink.Antal = Integer.parseInt(amounts[i]);
                        updateList.add(updatedDrink);
                    }
                }


/*
                for (int i=0; i < elements; i++)
                {
                    View lv = lvAddDrinksAdmin.getChildAt(i);
                    final TextView tvName = lv.findViewById(R.id.txtDrinkname);
                    TextView tvPrice = lv.findViewById(R.id.txtAmount);
                    String stringAmount = tvPrice.getText().toString();

                    Integer intAmount = 0;
                    if (!stringAmount.equals("")) {
                        intAmount = Integer.parseInt(stringAmount);
                    }


                    if(intAmount != 0)
                    {
                        Drink updateddrink = new Drink();

                        for (Drink drink : drinkList) {
                            if (drink.Navn.equals(tvName.getText().toString())) {
                                updateddrink= new Drink(drink);
                                updateddrink.Antal = intAmount;
                            }
                        }
                        updateList.add(updateddrink);

                    }

                }*/

                //updates the amount in the service.
                bgservice.updateAmount(updateList);
                finish();
            }
        });
    }

    //initialise the objects.
    private void Initialiseobjects() {

        bntSaveAddProduct = findViewById(R.id.bntSaveAddProduct);
        bntAddProduct = findViewById(R.id.bntAddProduct);
        bntCancelAdmin = findViewById(R.id.bntCancelAdmin);
        lvAddDrinksAdmin = findViewById(R.id.lvAddDrinksAdmin);

        firstVisibleItem = lvAddDrinksAdmin.getFirstVisiblePosition();
        lastVisibleItem = lvAddDrinksAdmin.getLastVisiblePosition();

        addDrinksAdaptor = new AddDrinksAdaptor(this, drinkList);
        lvAddDrinksAdmin.setAdapter(addDrinksAdaptor);



        lvAddDrinksAdmin.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstItem, int visibleItemCount, int totalItemCount) {
                firstVisibleItem = lvAddDrinksAdmin.getFirstVisiblePosition();
                lastVisibleItem = lvAddDrinksAdmin.getLastVisiblePosition();
            }
        });

        drinkList = new ArrayList<>();
        bgservice = new BackgroundService();
        updateList = new ArrayList<>();

        serviceIntent = new Intent(this, BackgroundService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);

        //set up an intent filter, for the background service.
        IntentFilter filter = new IntentFilter();
        filter.addAction(BackgroundService.BROADCAST_BACKGROUNDSERVICE_LOAD);
        LocalBroadcastManager.getInstance(this).registerReceiver(onBackgroundServiceLoadResult, filter);
    }


    //initiate the background service and updates the listview.
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BackgroundService.BackgroundServiceBinder binder = (BackgroundService.BackgroundServiceBinder) iBinder;
            bgservice = binder.getService();

            drinkList = bgservice.getDrinksList();
            addDrinksAdaptor.updateDrinkList(drinkList, firstVisibleItem, lastVisibleItem);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bgservice = null;
        }
    };

    //Recieves the broadcast when changes is detected.
    private BroadcastReceiver onBackgroundServiceLoadResult = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            drinkList = (ArrayList<Drink>)intent.getSerializableExtra(BackgroundService.LOAD_RESULT);
            addDrinksAdaptor.updateDrinkList(drinkList, firstVisibleItem, lastVisibleItem);
        }
    };

    //unregister the broadcastreceiver
    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onBackgroundServiceLoadResult);
    }

    //unbind the service
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }


}
