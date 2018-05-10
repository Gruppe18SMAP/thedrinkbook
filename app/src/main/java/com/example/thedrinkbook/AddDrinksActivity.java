package com.example.thedrinkbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class AddDrinksActivity extends AppCompatActivity {

    Button bntSaveAddProduct, bntAddProduct, bntCancelAdmin, bntDeleteProduct;
    ListView lvAddDrinksAdmin;

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
    }

    private void Initialiseobjects() {

        bntSaveAddProduct = findViewById(R.id.bntSaveAddProduct);
        bntAddProduct = findViewById(R.id.bntAddProduct);
        bntCancelAdmin = findViewById(R.id.bntCancelAdmin);
        bntDeleteProduct = findViewById(R.id.bntDeleteProduct);
        lvAddDrinksAdmin = findViewById(R.id.lvAddDrinksAdmin);
    }


}
