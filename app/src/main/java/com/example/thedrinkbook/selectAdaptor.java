package com.example.thedrinkbook;


import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class selectAdaptor extends BaseAdapter {

    private Context context;
    DatabaseReference drinkDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseDrinks = drinkDatabase.child("Drinks");
    List<Drink> drinklist;
    Drink drink;

    TextView tvDrinkName, tvDrinkPrice;


    public selectAdaptor(Context context, List<Drink> drinklist)
    {
        this.context = context;
        this.drinklist = drinklist;
    }

    public void updateDrinks(List<Drink> drinkList){
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
            convertView = viewInflator.inflate(R.layout.listviewselect, null);
        }

        drink = (Drink) getItem(position);

        if(drink != null)
        {
            tvDrinkName = convertView.findViewById(R.id.txtDrinkname);
            tvDrinkName.setText(drink.Navn);
            tvDrinkPrice = convertView.findViewById(R.id.txtDrinkPrice);
            tvDrinkPrice.setText(String.valueOf(drink.Pris));


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

    public void createList(){
    }
}
