package com.example.thedrinkbook;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddDrinksAdaptor extends BaseAdapter {

    Context context;
    ArrayList<Drink> drinklist;
    String[] amountlist;
    Drink drink;

    ImageView imgSodaicon;
    TextView txtDrinkname;
    EditText txtAmount;
    private int firstVisibleItem = 0, lastVisibleItem = 0;

//constructor
    public AddDrinksAdaptor(Context context, ArrayList<Drink> drinklist) {
        this.drinklist = drinklist;
        this.context = context;
        amountlist = new String[0];


    }
//updates the listview with drinks.
    public void updateDrinkList(ArrayList<Drink> drinkList, int firstVisibleItem, int lastVisibleItem){
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
            convertView = viewInflator.inflate(R.layout.listviewadddrinks, null);
        }

        txtAmount = convertView.findViewById(R.id.txtAmount);
        txtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                amountlist[position] = editable.toString();
                //drinklist.get(position).Antal = Integer.parseInt(editable.toString());

            }
        });

        drink = (Drink) getItem(position);
        if(drink != null)
        {
            txtDrinkname = convertView.findViewById(R.id.txtDrinkname);
            txtDrinkname.setText(drink.Navn);

            imgSodaicon  = convertView.findViewById(R.id.imgSodaicon);
            Picasso.with(this.context).load(drink.Ikon).into(imgSodaicon);

            if(firstVisibleItem != 0 || lastVisibleItem != 0) {
                if(amountlist[position] != null && position <= lastVisibleItem && position >= firstVisibleItem) {
                    txtAmount.setText(drink.Antal);
                }
            }

        }
        else{
            return  null;
        }

        return convertView;

    }

    public String[] getAmounts() {
        return amountlist;
    }
}
