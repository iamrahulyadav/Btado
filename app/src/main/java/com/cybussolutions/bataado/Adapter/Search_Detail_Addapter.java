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


public class Search_Detail_Addapter extends ArrayAdapter<String>
{

    private ArrayList<Home_Model> arraylist;
    Activity context;




    public Search_Detail_Addapter(Activity context, ArrayList<Home_Model> list)
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
        rowView = inflater.inflate(R.layout.search_detail_row,null,true);

        TextView place = (TextView) rowView.findViewById(R.id.Page_home);
        TextView adress = (TextView) rowView.findViewById(R.id.adress);
        RatingBar rating = (RatingBar) rowView.findViewById(R.id.ratingBar_home);
        ImageView profile_pic = (ImageView) rowView.findViewById(R.id.profile_image);

        Home_Model  home_model = arraylist.get(position);
        place.setText(home_model.getBrand_name());

        String adressing =home_model.getAdress()+" "+home_model.getBlock()+" "+home_model.getArea();
        adress.setText(adressing);
        int testrating;
        if(home_model.getRating() == null)
        {
            testrating= 0;
        }
        else
        {
            testrating = Integer.parseInt(home_model.getRating());
        }

        rating.setRating(testrating);




        Picasso.with(context)
                .load(End_Points.IMAGE_BASE_URL + home_model.getBrand_logo())
                .resize(150, 150)
                .centerCrop().transform(new CircleTransform())
                .into(profile_pic);

                /*Counter_Model counter_model = counter_list.get(position);
                Likes.setText(counter_model.getLiked());
                dislike.setText(counter_model.getDislikes());
                comments_count.setText(counter_model.getComment());
                userfull.setText(counter_model.getUsefull());
                share.setText(counter_model.getShares());*/


        return rowView;
    }





}
