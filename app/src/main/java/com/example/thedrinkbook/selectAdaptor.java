package com.example.thedrinkbook;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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


    public selectAdaptor(Context context, List<Drink> drinklist)
    {
        this.context = context;
        this.drinklist = drinklist;
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
            //convertView = viewInflator.inflate(R.layout.item_of_weatherlist, null);
        }

        return null;

    }

    public void createList(){
    }
}
