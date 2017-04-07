package com.cybussolutions.bataado.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybussolutions.bataado.Model.Counter_Model;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Model.Search_Model;
import com.cybussolutions.bataado.R;

import java.util.ArrayList;


public class search_model extends ArrayAdapter<String>

{

    Activity context;
    private ArrayList<Search_Model> category_list;


    public search_model(Activity context, ArrayList<Search_Model > list )
    {
        super(context, R.layout.row_drawer);
        this.context = context;
        category_list = list;
    }


    @Override
    public int getCount() {
        return category_list.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View rowView;


        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.row_drawer,null,true);
        TextView name=(TextView) rowView.findViewById(R.id.heading);



        Search_Model search_model = category_list.get(position);
        name.setText(search_model.getCategoryName());


        return rowView;
    }
}
