package com.example.thedrinkbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class AddProductActivity extends AppCompatActivity {

    EditText txtProductName, txtProductPrice;
    ImageView ivProductPicture;
    Button bntSaveAddProduct, bntCancelAddProduct;



    Drink drink;
    String icon;


    static final int REQUEST_ICON = 1;
    BackgroundService bgservice;

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
            }
        });

    }

    public void uploadProductToFirebase() {

        drink.Navn = txtProductName.getText().toString();
        drink.Pris = Integer.parseInt(txtProductPrice.getText().toString());
        drink.Key = drink.Navn.toLowerCase().replace(" ", "");
        drink.Ikon = icon;


    }

    public void initiate() {
        txtProductName = findViewById(R.id.txtProductName);
        txtProductPrice = findViewById(R.id.txtProductPrice);

        ivProductPicture = findViewById(R.id.ivProductPicture);

        bntCancelAddProduct = findViewById(R.id.bntCancelAddProduct);
        bntSaveAddProduct = findViewById(R.id.bntSaveAddProduct);
    }


    //inspired from https://developer.android.com/training/camera/photobasics.
    //resolveActivity is called to make sure the app does not crash, if the app cannot handle the intent.
    private void takeAPhoto() {
        Intent PictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (PictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(PictureIntent, REQUEST_ICON);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ICON && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap iconBitmap = (Bitmap) extras.get("data");
            ivProductPicture.setImageBitmap(iconBitmap);
            uploadIconToStorage(iconBitmap);
        }
    }


    private void uploadIconToStorage(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        icon = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }



}