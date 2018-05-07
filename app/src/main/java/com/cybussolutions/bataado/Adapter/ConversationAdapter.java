package com.cybussolutions.bataado.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cybussolutions.bataado.Activities.Conversation;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Conversation_Model;
import com.cybussolutions.bataado.Model.Messages_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rizwan Jillani on 23-Apr-18.
 */
public class ConversationAdapter extends ArrayAdapter<String>
{

    private ArrayList<Conversation_Model> arraylist;
    Activity context;
    String brandLogo;
    String userId;
    public ConversationAdapter(Activity context, ArrayList<Conversation_Model> list,String brandLogo,String userId)
    {
        super(context, R.layout.search_detail_row);
        this.context = context;
        this.arraylist = list;
        this.brandLogo = brandLogo;
        this.userId = userId;

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
        SharedPreferences pref = context.getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        LayoutInflater inflater = context.getLayoutInflater();
        if(!userId.equals(arraylist.get(position).getChatFrom()))
        rowView = inflater.inflate(R.layout.conversation_item_list,null,true);
        else
            rowView = inflater.inflate(R.layout.conversation_item_list1,null,true);
        final TextView chatMessage = rowView.findViewById(R.id.chatMessage);
        final TextView creationDate = rowView.findViewById(R.id.creationDate);
        ImageView brandImage = rowView.findViewById(R.id.brand_image);
        final Conversation_Model conversation_model=arraylist.get(position);
        chatMessage.setText(conversation_model.getChatMessage());
        if(!userId.equals(arraylist.get(position).getChatFrom())) {
            if (!brandLogo.equals("") && !brandLogo.equals("null")) {
                Picasso.with(context)
                        .load(End_Points.IMAGE_BASE_URL + brandLogo)
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(brandImage);
            } else {
                Picasso.with(context)
                        .load(R.drawable.no_logo)
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(brandImage);
            }
        }else {
            String pp = pref.getString("profile_pic","");
            if (pp.startsWith("https://graph.facebook.com/") || pp.startsWith("https://fb-s-b-a.akamaihd.net/")|| pp.startsWith("https://scontent.xx.fbcdn.net/") || pp.startsWith("http://graph.facebook.com/")) {
                Picasso.with(getContext())
                        .load(pp)
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(brandImage);
            } else {
                if (pp.equals("")) {
                    Picasso.with(getContext())
                            .load("http://bataado.cybussolutions.com/uploads/no-image-icon-hi.png")
                            .resize(150, 150)
                            .centerCrop().transform(new CircleTransform())
                            .into(brandImage);
                } else {
                    Picasso.with(getContext())
                            .load(End_Points.IMAGE_PROFILE_PIC + pp)
                            .resize(150, 150)
                            .centerCrop().transform(new CircleTransform())
                            .into(brandImage);
                }


            }
        }
        return rowView;
    }





}



