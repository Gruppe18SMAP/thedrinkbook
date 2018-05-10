package com.example.thedrinkbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminOverviewAdaptor extends BaseAdapter {

    private Context context;
    DatabaseReference drinkDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseDrinks = drinkDatabase.child("Drinks");
    List<Drink> drinklist;
    Drink drink;

    ImageView imgSodaicon;
    TextView txtDrinkname, txtAmount;

    public AdminOverviewAdaptor(Context context, List<Drink> drinklist){
        this.context = context;
        this.drinklist = drinklist;

    }

    public void updateDrinkList(List<Drink> drinkList){
        this.drinklist = drinkList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(drinklist != null)
        {
            return drinklist.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(drinklist != null)
        {
            return drinklist.get(position);
        }
        else
        {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //If the current view is not created, a new inflator will be created
        if (convertView == null)
        {
            LayoutInflater viewInflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = viewInflator.inflate(R.layout.listviewadminverview, null);
        }

        drink = (Drink) getItem(position);

        if(drink != null)
        {
            txtDrinkname = convertView.findViewById(R.id.txtDrinkname);
            txtDrinkname.setText(drink.Navn);
            txtAmount = convertView.findViewById(R.id.txtAmount);
            txtAmount.setText((String.valueOf(drink.Antal)));


            imgSodaicon  = convertView.findViewById(R.id.imgSodaicon);

            Picasso.with(this.context).load(drink.Ikon).into(imgSodaicon);

            /*int amount = drink.Antal;
            tvAmount = convertView.findViewById(R.id.txtAmount);
            tvAmount.setText(String.valueOf(amount));*/

        }
        else {
            return null;
        }
        convertView = convertView;
        return convertView;

    }
}
