package com.example.thedrinkbook;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class selectAdaptor extends BaseAdapter {

    private BackgroundService bgservice;


    private Context context;
    List<Drink> drinklist;
    Drink drink;

    TextView tvDrinkName, tvDrinkPrice;
    ImageView imgSodaicon;
    EditText etAmount;

    private String[] amountlist;
    private int firstVisibleItem, lastVisibleItem;

    public selectAdaptor(Context context, List<Drink> drinklist)
    {
        this.context = context;
        this.drinklist = drinklist;
        amountlist = new String[0];
    }

//updates the listview
    public void updateDrinks(List<Drink> drinkList, BackgroundService service, int firstVisibleItem, int lastVisibleItem){
        this.bgservice = service;
        this.drinklist = drinkList;
        amountlist = new String[drinkList.size()];
        this.firstVisibleItem = firstVisibleItem;
        this.lastVisibleItem = lastVisibleItem;
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

    //set the objects in the listview
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //If the current view is not created, a new inflator will be created
        if (convertView == null)
        {
            LayoutInflater viewInflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = viewInflator.inflate(R.layout.listviewselect, null);
        }

        etAmount = convertView.findViewById(R.id.txtAmount);
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                amountlist[position] = editable.toString();
            }
        });

        drink = (Drink) getItem(position);

        if(drink != null)
        {
            tvDrinkName = convertView.findViewById(R.id.txtDrinkname);
            tvDrinkName.setText(drink.Navn);
            tvDrinkPrice = convertView.findViewById(R.id.txtDrinkPrice);
            tvDrinkPrice.setText(String.valueOf(drink.Pris) + " kroner");

            imgSodaicon  = convertView.findViewById(R.id.imgSodaicon);

            if(bgservice != null){
              bgservice.startloadIconRunnable(this.context, drink.Ikon, imgSodaicon);
            }

            if(firstVisibleItem != 0 || lastVisibleItem != 0){
                if(amountlist[position] != null  && position < lastVisibleItem && position > firstVisibleItem){
                    if(!amountlist[position].equals("")) {
                        etAmount.setText(drink.Antal);
                    }
                }
            }

            //            Picasso.with(this.context).load(drink.Ikon).into(imgSodaicon);

            /*int amount = drink.Antal;
            tvAmount = convertView.findViewById(R.id.txtAmount);
            tvAmount.setText(String.valueOf(amount));*/

        }
        else {
            return null;
        }
        return convertView;

    }

    public String[] getAmounts() {
        return amountlist;
    }
}
