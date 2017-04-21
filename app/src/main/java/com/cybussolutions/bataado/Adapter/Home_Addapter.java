package com.cybussolutions.bataado.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cybussolutions.bataado.Activities.Detail_brand;
import com.cybussolutions.bataado.Activities.User_Profile;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class Home_Addapter extends ArrayAdapter<String>
{

    private ArrayList<Home_Model> arraylist;
    Activity context;
    String reviewrating = "";

    public Home_Addapter(Activity context, ArrayList<Home_Model> list)
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View rowView;

        final LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.row_home,null,true);

        final TextView username = (TextView) rowView.findViewById(R.id.user_name_home);
        TextView place = (TextView) rowView.findViewById(R.id.Page_home);
        TextView date = (TextView) rowView.findViewById(R.id.time_home);
        TextView comments = (TextView) rowView.findViewById(R.id.adress);
        RatingBar rating = (RatingBar) rowView.findViewById(R.id.ratingBar_home);
        ImageView profile_pic = (ImageView) rowView.findViewById(R.id.profile_image);

        final Home_Model  home_model = arraylist.get(position);
        username.setText(home_model.getFirstname()+" "+home_model.getLastname());
        place.setText(home_model.getBrand_name());
        comments.setText(home_model.getReview());
        date.setText(home_model.getDate_created());
        int testrating = 0;

        reviewrating = home_model.getRating();

        if(reviewrating == null )
        {
            rating.setRating(testrating);
        }

        else
        {
            testrating= Integer.parseInt(home_model.getRating());
            rating.setRating(testrating);

        }


        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
                {

                    Intent intent= new Intent(getContext(), User_Profile.class);
                    intent.putExtra("username",username.getText().toString());
                    intent.putExtra("userID",home_model.getUserid());
                    getContext().startActivity(intent);
            }
        });

        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(getContext(), Detail_brand.class);
                intent.putExtra("brandNAme",home_model.getBrand_name());
                intent.putExtra("brandId",home_model.getBrandid());
                intent.putExtra("brandRating",home_model.getBrand_raiting());
                intent.putExtra("brandAdress",home_model.getArea()+" "+home_model.getBlock());
                intent.putExtra("brandPic",home_model.getBrand_logo());
                intent.putExtra("reviewCount",home_model.getNum_review());
                getContext().startActivity(intent);
            }
        });

        String pp = home_model.getProfilepic();



        if(pp.startsWith("https://graph.facebook.com/"))
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


                /*Counter_Model counter_model = counter_list.get(position);
                Likes.setText(counter_model.getLiked());
                dislike.setText(counter_model.getDislikes());
                comments_count.setText(counter_model.getComment());
                userfull.setText(counter_model.getUsefull());
                share.setText(counter_model.getShares());*/

        return rowView;
    }


}
