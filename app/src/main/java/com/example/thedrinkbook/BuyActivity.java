package com.example.thedrinkbook;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;

import dk.danskebank.mobilepay.sdk.Country;
import dk.danskebank.mobilepay.sdk.MobilePay;
import dk.danskebank.mobilepay.sdk.ResultCallback;
import dk.danskebank.mobilepay.sdk.model.FailureResult;
import dk.danskebank.mobilepay.sdk.model.Payment;
import dk.danskebank.mobilepay.sdk.model.SuccessResult;


public class BuyActivity extends AppCompatActivity {

    private static final String PAYED = "Payed for drinks";
    private static final String BOUGHTDRINKS = "Drinks bought" ;
    static final String LOG = "BuyActivity";
    DatabaseReference drinkDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseDrinks = drinkDatabase.child("Drinks");

    private BackgroundService bgservice;


    ListView lvChosenDrinks;
    TextView txtTotalPrice;
    Button btnPay, btnCancel;

    buyAdaptor BA;
    ArrayList<Drink> drinkList;
    Drink drink;
    int totalPrice;

    int MOBILEPAY_PAYMENT_REQUEST_CODE = 14;
    int CONFIRM_REQUEST_CODE = 142;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        MobilePay.getInstance().init(getString(R.string.merchant_id_generic), Country.DENMARK);

        initiate();

        getSelectedData();

        Intent serviceIntent = new Intent(this, BackgroundService.class);
        bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(LOG, "Binded to backgroundservice");

        //BA.updateDrinkList(drinkList, bgservice);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Implement the mobileCode here.

                boolean isMobilePayInstalled = MobilePay.getInstance().isMobilePayInstalled(getApplicationContext());

                if(isMobilePayInstalled){
                    Payment payment = new Payment();
                    payment.setProductPrice(new BigDecimal(totalPrice));
                    payment.setOrderId("testorder");

                    Intent paymentIntent = MobilePay.getInstance().createPaymentIntent(payment);
                    Log.d(LOG, "Paying...");
                    startActivityForResult(paymentIntent, MOBILEPAY_PAYMENT_REQUEST_CODE);

                }
                else {
                    Intent installMPIntent = MobilePay.getInstance().createDownloadMobilePayIntent(getApplicationContext());
                    Log.d(LOG, "Mobilepay is not installed");
                    startActivity(installMPIntent);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void initiate()
    {
        lvChosenDrinks = findViewById(R.id.lvChosenDrinks);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        btnCancel = findViewById(R.id.btnCancel);
        btnPay = findViewById(R.id.btnPay);

        drinkList = new ArrayList<>();
        drink = new Drink();
        BA = new buyAdaptor(this, drinkList);


        totalPrice = 0;

        lvChosenDrinks.setAdapter(BA);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BackgroundService.BackgroundServiceBinder binder = (BackgroundService.BackgroundServiceBinder) iBinder;
            bgservice = binder.getService();
            BA.updateDrinkList(drinkList, bgservice);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bgservice = null;
        }
    };

    private void getSelectedData()
    {
        //Gets the list of selected drinks from SelectActivity
        drinkList = (ArrayList<Drink>) getIntent().getSerializableExtra(selectActivity.SELECTEDDRINKS);


        //Gets the total price
        for (int i = 0; i < drinkList.size(); i++) {
            drink = drinkList.get(i);
            totalPrice = drink.Pris*drink.Antal +totalPrice;
        }

        txtTotalPrice.setText(String.format("%d kroner", totalPrice));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == MOBILEPAY_PAYMENT_REQUEST_CODE) {
                MobilePay.getInstance().handleResult(resultCode, data, new ResultCallback() {
                    @Override
                    public void onSuccess(SuccessResult successResult) {
                        Intent confirmIntent = new Intent(BuyActivity.this, confirmActivity.class);
                        startActivityForResult(confirmIntent, CONFIRM_REQUEST_CODE);

                        bgservice.boughtFromDatabase(drinkList);
                        Log.d(LOG, "Payment accepted");

                    }

                    @Override
                    public void onFailure(FailureResult failureResult) {
                        Toast failureToast = Toast.makeText(BuyActivity.this, getResources().getString(R.string.Paymentfailed), Toast.LENGTH_LONG);
                        failureToast.show();
                        Log.d(LOG, "Payment failed");
                    }

                    @Override
                    public void onCancel() {
                        Toast cancelToast = Toast.makeText(BuyActivity.this, getResources().getString(R.string.paymentCanceled), Toast.LENGTH_LONG);
                        cancelToast.show();
                        Log.d(LOG, "Payment canceled");
                    }
                });
            }
            if (requestCode == CONFIRM_REQUEST_CODE) {
                setResult(RESULT_OK);
                finish();
            }
        } else if (resultCode == confirmActivity.RESULT_LOGOUT){
            setResult(confirmActivity.RESULT_LOGOUT);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        Log.d(LOG, "Unbinded to backgroundservice");

    }
}
