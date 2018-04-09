package com.cybussolutions.bataado.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cybussolutions.bataado.Activities.Detail_brand;
import com.cybussolutions.bataado.Activities.HomeScreen;
import com.cybussolutions.bataado.Activities.MapsActivity;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Brands_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;


public class BrandAdapter extends ArrayAdapter<String>
{

    private ArrayList<Brands_Model> arraylist;
    private Activity context;
    public BrandAdapter(Activity context, ArrayList<Brands_Model> list)
    {
        super(context, R.layout.row_drawer);
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

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent)
    {
        View rowView;

        final LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.row_brands,null,true);
        final TextView brandName = rowView.findViewById(R.id.brand_name);
        final TextView numReviews = rowView.findViewById(R.id.number_reviews);
        RatingBar rating = rowView.findViewById(R.id.brand_raiting);
        ImageView brandImage = rowView.findViewById(R.id.brand_image);
        final Brands_Model brands_model=arraylist.get(position);
        brandName.setText(brands_model.getBrandname());
        if(!brands_model.getBrandimage().equals("") && !brands_model.getBrandimage().equals("null")) {
            Picasso.with(context)
                    .load(End_Points.IMAGE_BASE_URL + brands_model.getBrandimage())
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(brandImage);
        }else {
            Picasso.with(context)
                    .load(R.drawable.no_logo)
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(brandImage);
        }
        if(brands_model.getBrandRating().equals("") || brands_model.getBrandRating().equals("null")){
            rating.setRating(0);
        }else {
            int ratin=Integer.parseInt(brands_model.getBrandRating());
            rating.setRating(ratin);
        }
        if(brands_model.getBrandReviews().equals("null")){
            numReviews.setText(""+" Reviews");
        }else {
            numReviews.setText(brands_model.getBrandReviews() + " Reviews");
        }
        brandName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getContext(), MapsActivity.class);
                intent.putExtra("brandNAme", brands_model.getBrandname());
                intent.putExtra("brandId", brands_model.getBrandid());
                intent.putExtra("brandRating", brands_model.getBrandRating());
                intent.putExtra("brandAdress",brands_model.getArea()+" "+brands_model.getBlock());
                intent.putExtra("phone",brands_model.getPhone());
                intent.putExtra("websiteUrl",brands_model.getWebsite_url());
                intent.putExtra("latitude",brands_model.getBrand_latitude());
                intent.putExtra("longitude",brands_model.getBrand_longitude());
                intent.putExtra("email",brands_model.getEmail());
                intent.putExtra("brandPic", brands_model.getBrand_logo());
                getContext().startActivity(intent);
            }
        });

        return rowView;
    }

}
