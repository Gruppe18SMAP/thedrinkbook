package com.example.thedrinkbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BuyActivity extends AppCompatActivity {

    ListView lvChosenDrinks;
    TextView txtTotalPrice;
    Button btnPay, btnCancel;

    buyAdaptor BA;
    ArrayList<Drink> drinkList;
    Drink drink;
    int totalPrice;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        initiate();

        getSelectedData();

        BA.updateDrinkList(drinkList);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Implement the mobileCode here.

                Intent mobileIntent = new Intent();
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

    private void getSelectedData()
    {
        //Gets the list of Drinks from SelectActivity
        drinkList = (ArrayList<Drink>) getIntent().getSerializableExtra(selectActivity.SELECTEDDRINKS);

        //Gets the total price
        for (int i = 0; i < drinkList.size(); i++) {
            drink = drinkList.get(i);
            totalPrice = drink.Pris*drink.Antal +totalPrice;
        }

        txtTotalPrice.setText(String.format("%d kroner", totalPrice));
    }


}
