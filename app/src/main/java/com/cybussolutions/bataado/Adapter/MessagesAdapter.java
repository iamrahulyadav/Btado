package com.cybussolutions.bataado.Adapter;

/**
 * Created by Rizwan Jillani on 23-Apr-18.
 */
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cybussolutions.bataado.Activities.Conversation;
import com.cybussolutions.bataado.Activities.User_Profile;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Brands_Model;
import com.cybussolutions.bataado.Model.Messages_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessagesAdapter extends ArrayAdapter<String>
{

    private ArrayList<Messages_Model> arraylist;
    Activity context;




    public MessagesAdapter(Activity context, ArrayList<Messages_Model> list)
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
        rowView = inflater.inflate(R.layout.messages_item_list,null,true);
        final TextView brandName = rowView.findViewById(R.id.brand_name);
        final TextView chatType = rowView.findViewById(R.id.chatType);
        final TextView chatMessage = rowView.findViewById(R.id.chatMessage);
        final TextView creationDate = rowView.findViewById(R.id.creationDate);
        RatingBar rating = rowView.findViewById(R.id.brand_raiting);
        ImageView brandImage = rowView.findViewById(R.id.brand_image);
        final Messages_Model messages_model=arraylist.get(position);
        brandName.setText(messages_model.getBrandname());
        chatType.setText(messages_model.getMessageType());
        chatMessage.setText(messages_model.getChatMessage());
        creationDate.setText(messages_model.getMessageDate());
        if(messages_model.getBrandRating().equals("") || messages_model.getBrandRating().equals("null")){
            rating.setRating(0);
        }else {
            int ratin=Integer.parseInt(messages_model.getBrandRating());
            rating.setRating(ratin);
        }
        if(!messages_model.getBrandimage().equals("") && !messages_model.getBrandimage().equals("null")) {
            Picasso.with(context)
                    .load(End_Points.IMAGE_BASE_URL + messages_model.getBrandimage())
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
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, Conversation.class);
                intent.putExtra("chatKey",messages_model.getChatKey());
                intent.putExtra("chatId",messages_model.getBrandid());
                intent.putExtra("brandLogo",messages_model.getBrandimage());
                intent.putExtra("chatType",messages_model.getMessageType());
                intent.putExtra("chatFrom",messages_model.getChatFrom());
                intent.putExtra("chat_flag_id",messages_model.getChatFlagId());
                intent.putExtra("chat_brand_id",messages_model.getChatBrandId());
                context.startActivity(intent);
            }
        });
        return rowView;
    }





}

