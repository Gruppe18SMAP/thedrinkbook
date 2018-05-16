package com.example.thedrinkbook;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AddProductActivity extends AppCompatActivity {

    EditText txtProductName, txtProductPrice;
    ImageView ivProductPicture;
    Button bntSaveAddProduct, bntCancelAddProduct;

    Bitmap iconBitmap;

    static final String LOG = "AddProductActivity";

    static final int REQUEST_ICON = 1;
    BackgroundService bgservice;
    Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        initiate();

        ivProductPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAPhoto();
            }
        });

        bntCancelAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bntSaveAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProductToFirebase();
                finish();
            }
        });

    }

    public void initiate() {
        txtProductName = findViewById(R.id.txtProductName);
        txtProductPrice = findViewById(R.id.txtProductPrice);

        ivProductPicture = findViewById(R.id.ivProductPicture);

        bntCancelAddProduct = findViewById(R.id.bntCancelAddProduct);
        bntSaveAddProduct = findViewById(R.id.bntSaveAddProduct);

        bgservice = new BackgroundService();

        serviceIntent = new Intent(this, BackgroundService.class);
    }

    public void uploadProductToFirebase() {

        if(TextUtils.isEmpty(txtProductName.getText().toString())){
            txtProductName.setError(getResources().getString(R.string.add_productname));}
        else if(TextUtils.isEmpty(txtProductPrice.getText().toString())){
            txtProductPrice.setError(getResources().getString(R.string.add_productprice));}
        else if(txtProductPrice.getText().toString().startsWith("-")){
                txtProductPrice.setError(getResources().getString(R.string.add_priceNotnegativ)); }
        else if(iconBitmap.equals(0))
        {
            Toast.makeText(this, getResources().getString(R.string.iconError), Toast.LENGTH_LONG).show();
        }
        else {
        Drink drink = new Drink();
        drink.Navn = txtProductName.getText().toString();
        drink.Pris = Integer.parseInt(txtProductPrice.getText().toString());
        drink.Key = drink.Navn.toLowerCase().replace(" ", "");
            Log.d(LOG, "Uploading Drink to firebase");
        bgservice.addProduct(drink);
        bgservice.uploadIconToStorage(drink.Key, iconBitmap);

        }

    }


    //inspired from https://developer.android.com/training/camera/photobasics.
    //resolveActivity is called to make sure the app does not crash, if the app cannot handle the intent.
    private void takeAPhoto() {
        Intent PictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (PictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(PictureIntent, REQUEST_ICON);
            Log.d(LOG, "Taking picture...");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ICON && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            iconBitmap = (Bitmap) extras.get("data");
            ivProductPicture.setImageBitmap(iconBitmap);
            Log.d(LOG, "Icon recived");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(LOG, "Binds to backgroundservice");

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        Log.d(LOG, "Unbinded to backgroundservice");
    }
}