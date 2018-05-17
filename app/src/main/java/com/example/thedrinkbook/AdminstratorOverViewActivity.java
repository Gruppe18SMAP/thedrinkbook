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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminstratorOverViewActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference drinkDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseDrinks = drinkDatabase.child("Drinks");

    Button btnRefill, btnLogout;
    ListView lvDrinksAdmin;
    ArrayList<Drink> drinks;
    Intent serviceIntent;

    private AdminOverviewAdaptor listviewAdapter;
    private BackgroundService bgservice;

    static final int REQUEST_DRINKS= 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_adminstrator_over_view);

       initializeObjects();
        serviceIntent = new Intent(this, BackgroundService.class);
        startService(serviceIntent);

        lvDrinksAdmin.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {


                Toast.makeText(AdminstratorOverViewActivity.this, "long clicked, "+"pos: " + i, Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }


    private void initializeObjects() {
        btnRefill = findViewById(R.id.bntFill);
        btnLogout = findViewById(R.id.bntLogoutAdminOverview);
        lvDrinksAdmin = findViewById(R.id.lvDrinksAdmin);

        btnRefill.setOnClickListener(AdminstratorOverViewActivity.this);
        btnLogout.setOnClickListener(AdminstratorOverViewActivity.this);

        listviewAdapter = new AdminOverviewAdaptor(this, drinks);
        lvDrinksAdmin.setAdapter(listviewAdapter);
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
            listviewAdapter.updateDrinkList(drinks);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if(requestCode == 12){
            if(resultCode == RESULT_CANCELED){

            }
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onBackgroundServiceLoadResult);
        unbindService(serviceConnection);
        stopService(serviceIntent);
    }
}

