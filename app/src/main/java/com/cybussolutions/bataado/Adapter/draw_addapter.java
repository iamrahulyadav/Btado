package com.cybussolutions.bataado.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybussolutions.bataado.R;

import static android.content.Context.MODE_PRIVATE;

public class draw_addapter extends ArrayAdapter<String>

{
    private String title[],isActive;
    private int imgs[];
    private Activity context;



    public draw_addapter(Activity context, String[] title , int[] imgs)
    {
        super(context, R.layout.row_drawer,title);
        this.title = title;
        this.context = context;
        this.imgs = imgs;
    }



    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent)
    {

        View rowView;


        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.drawer_row,null,true);
        TextView name= rowView.findViewById(R.id.text_drawer);
        ImageView img = rowView.findViewById(R.id.img_drawer);
        final ImageView notifiaction_img = rowView.findViewById(R.id.img_notification);

        notifiaction_img.setVisibility(View.GONE);

        final SharedPreferences notification_pref = getContext().getApplicationContext().getSharedPreferences("Notifications",MODE_PRIVATE);
        final SharedPreferences.Editor editor= notification_pref.edit();

        isActive = notification_pref.getString("friend_request","");

        if(isActive.equals("active") && position == 0)
        {
            notifiaction_img.setVisibility(View.VISIBLE);
            notifiaction_img.setImageResource(R.drawable.tnotification_active);
        }else {
            notifiaction_img.setVisibility(View.INVISIBLE);
        }


        name.setText(title[position]);
        img.setImageResource(imgs[position]);

        return rowView;
    }
}

