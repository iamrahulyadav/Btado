package com.cybussolutions.bataado.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Counter_Model;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Model.Search_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class search_model extends ArrayAdapter<String>

{

    private Activity context;
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




    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {

        View rowView;


        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.row_categories,null,true);
        TextView name= rowView.findViewById(R.id.heading);
        ImageView imageView= rowView.findViewById(R.id.img);


        Search_Model search_model = category_list.get(position);
        name.setText(search_model.getCategoryName());
        if(!category_list.get(position).getCatagoryLogo().equals("")) {
            Picasso.with(context)
                    .load(End_Points.IMAGE_BASE_URL_CATAGORY + category_list.get(position).getCatagoryLogo())
                    .resize(50, 50)
                    .centerCrop().transform(new CircleTransform())
                    .into(imageView);
        }else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.university));
        }

        return rowView;
    }
}
