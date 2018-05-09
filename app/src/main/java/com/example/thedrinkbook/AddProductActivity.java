package com.example.thedrinkbook;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AddProductActivity extends AppCompatActivity {

    EditText txtProductName, txtProductPrice;
    ImageView ivProductPicture;

    Drink drink;

    static final int REQUEST_IMAGE_CAPTURE = 1;

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

    //inspired from https://developer.android.com/training/camera/photobasics.
    //resolveActivity is called to make sure the app does not crash, if the app cannot handle the intent.
    private void takeAPhoto()
    {
        Intent PictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (PictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(PictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }
}
