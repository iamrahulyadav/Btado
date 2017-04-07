package com.cybussolutions.bataado.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Friends_addapter extends ArrayAdapter<String>
{

    private ArrayList<Home_Model> arraylist;
    Activity context;




    public Friends_addapter(Activity context, ArrayList<Home_Model> list)
    {
        super(context, R.layout.search_detail_row);
        this.context = context;
        this.arraylist = list;

    }


    @Override
    public int getCount() {
        return arraylist.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View rowView;

        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.friends_list,null,true);

        TextView username = (TextView) rowView.findViewById(R.id.fr_name);
        TextView adress = (TextView) rowView.findViewById(R.id.adress);
        ImageView profile_pic = (ImageView) rowView.findViewById(R.id.fr_pp);

        Home_Model  home_model = arraylist.get(position);
        username.setText(home_model.getFirstname() +" "+ home_model.getLastname());


        String adressing =home_model.getBlock();

        if(adress ==  null )
        {
            adress.setText("No Adress");
        }
        else
        {
            adress.setText(adressing);

        }
        String pp =home_model.getProfilepic();

        if(pp.startsWith("https://fb"))
        {
            Picasso.with(getContext())
                    .load(pp)
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(profile_pic);
        }
        else
        {
            if(pp.equals(""))
            {
                Picasso.with(getContext())
                        .load("http://bataado.cybussolutions.com/uploads/no-image-icon-hi.png")
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(profile_pic);
            }
            else
            {
                Picasso.with(getContext())
                        .load(End_Points.IMAGE_PROFILE_PIC +pp )
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(profile_pic);
            }


        }

        return rowView;
    }





}
