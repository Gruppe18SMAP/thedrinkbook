package com.example.thedrinkbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AddDrinksAdaptor extends BaseAdapter {

    Context context;
    ArrayList<Drink> drinklist;
    Drink drink;

    ImageView imgSodaicon;
    TextView txtDrinkname;
    EditText txtAmount;

    public AddDrinksAdaptor(Context context, ArrayList<Drink> drinklist) {
        this.drinklist = drinklist;
        this.context = context;
    }

    public void updateDrinkList(ArrayList<Drink> drinkList){
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
            convertView = viewInflator.inflate(R.layout.listviewadddrinks, null);
        }

        drink = (Drink) getItem(position);
        if(drink != null)
        {
            txtDrinkname = convertView.findViewById(R.id.txtDrinkname);
            txtDrinkname.setText(drink.Navn);

            imgSodaicon  = convertView.findViewById(R.id.imgSodaicon);
            Picasso.with(this.context).load(drink.Ikon).into(imgSodaicon);

        }
        else{
            return  null;
        }

        convertView = convertView;
        return convertView;

    }
}
