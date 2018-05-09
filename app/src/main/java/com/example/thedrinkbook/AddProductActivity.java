package com.example.thedrinkbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.internal.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class AddProductActivity extends AppCompatActivity {

    EditText txtProductName, txtProductPrice;
    ImageView ivProductPicture;

    Drink drink;

    FirebaseStorage storage;
    StorageReference storageReference;


    static final int REQUEST_ICON = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        initiate();

    }

    public void getproduct()
    {
        drink.Navn = txtProductName.getText().toString();
        drink.Pris = Integer.parseInt(txtProductPrice.getText().toString());
    }

    public void initiate()
    {
        txtProductName = findViewById(R.id.txtProductName);
        txtProductPrice = findViewById(R.id.txtProductPrice);

        ivProductPicture = findViewById(R.id.ivProductPicture);
    }

    //inspired from https://developer.android.com/training/camera/photobasics and https://www.learnhowtoprogram.com/android/gestures-animations-flexible-uis/using-the-camera-and-saving-images-to-firebase
    //resolveActivity is called to make sure the app does not crash, if the app cannot handle the intent.
    private void takeAPhoto()
    {
        Intent PictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (PictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(PictureIntent, REQUEST_ICON);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == REQUEST_ICON && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap iconBitmap = (Bitmap) extras.get("data");
            ivProductPicture.setImageBitmap(iconBitmap);
            //uploadIconToFireBase(iconBitmap);
        }

    }
/*
    private void uploadIconToFireBase(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        String icon = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);


    }
  */


}
