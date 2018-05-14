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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddDrinksActivity extends AppCompatActivity {

    Button bntSaveAddProduct, bntAddProduct, bntCancelAdmin, bntDeleteProduct;
    ListView lvAddDrinksAdmin;

    ArrayList<Drink> drinkList;
    AddDrinksAdaptor addDrinksAdaptor;
    BackgroundService bgservice;
    ArrayList<Drink> updateList;
    Intent serviceIntent;

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

        bntSaveAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int elements = lvAddDrinksAdmin.getAdapter().getCount();
                updateList.clear();

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

                }
                bgservice.updateAmount(updateList);
                finish();
            }
        });
    }

    private void Initialiseobjects() {

        bntSaveAddProduct = findViewById(R.id.bntSaveAddProduct);
        bntAddProduct = findViewById(R.id.bntAddProduct);
        bntCancelAdmin = findViewById(R.id.bntCancelAdmin);
        lvAddDrinksAdmin = findViewById(R.id.lvAddDrinksAdmin);

        addDrinksAdaptor = new AddDrinksAdaptor(this, drinkList);
        lvAddDrinksAdmin.setAdapter(addDrinksAdaptor);

        drinkList = new ArrayList<>();
        bgservice = new BackgroundService();
        updateList = new ArrayList<>();

        serviceIntent = new Intent(this, BackgroundService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BackgroundService.BROADCAST_BACKGROUNDSERVICE_LOAD);
        LocalBroadcastManager.getInstance(this).registerReceiver(onBackgroundServiceLoadResult, filter);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BackgroundService.BackgroundServiceBinder binder = (BackgroundService.BackgroundServiceBinder) iBinder;
            bgservice = binder.getService();

            drinkList = bgservice.getDrinksList();
            addDrinksAdaptor.updateDrinkList(drinkList);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bgservice = null;
        }
    };

    private BroadcastReceiver onBackgroundServiceLoadResult = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            drinkList = (ArrayList<Drink>)intent.getSerializableExtra(BackgroundService.LOAD_RESULT);
            addDrinksAdaptor.updateDrinkList(drinkList);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onBackgroundServiceLoadResult);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
