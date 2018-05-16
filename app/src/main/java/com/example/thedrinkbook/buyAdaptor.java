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

public class buyAdaptor extends BaseAdapter {

    BackgroundService bgservice;

    private Context context;
    List<Drink> drinklist;
    Drink drink;

    public buyAdaptor(Context context, List<Drink> drinklist)
    {
        this.context = context;
        this.drinklist = drinklist;
    }

    //Updates the listview
    public void updateDrinkList(List<Drink> drinklist, BackgroundService bgservice)
    {
        this.bgservice = bgservice;
        this.drinklist.clear();
        this.drinklist = drinklist;
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
    public Drink getItem(int position) {
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
        if(drinklist != null)
        {
            //return drinklist.get(position).navn;
            return 0;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //If the current view is not created, a new inflator will be created
        if (convertView == null)
        {
            LayoutInflater viewInflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = viewInflator.inflate(R.layout.listviewbuy, null);
        }

        drink = getItem(position);

        if(drink != null)
        {
            TextView txtPrice = convertView.findViewById(R.id.txtPrice);
            txtPrice.setText(String.format("%d kr.", drink.Pris));

            TextView txtDrinkname = convertView.findViewById(R.id.txtDrinkname);
            txtDrinkname.setText(drink.Navn);

            TextView txtAmount = convertView.findViewById(R.id.txtAmount);
            txtAmount.setText(String.format("%d x", drink.Antal));

            ImageView imgSodaicon = convertView.findViewById(R.id.imgSodaicon);

            if(bgservice != null){
                bgservice.startloadIconRunnable(this.context, drink.Ikon, imgSodaicon);
            }

            //Picasso.with(this.context).load(drink.Ikon).into(imgSodaicon);

        }
        else
        {
            return  null;
        }

        return convertView;
    }
}
