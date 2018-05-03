package com.example.thedrinkbook;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminOverviewAdaptor extends BaseAdapter {

    private Context context;
    DatabaseReference drinkDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseDrinks = drinkDatabase.child("Drinks");
    List<Drink> drinklist;
    Drink drink;

    ImageView imgSodaicon;
    TextView txtDrinkname, txtAmount;

    public void AdminOverviewAdaptor(Context context, List<Drink> drinklist){
        this.context = context;
        this.drinklist = drinklist;

    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
