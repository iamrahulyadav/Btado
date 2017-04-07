package com.cybussolutions.bataado.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybussolutions.bataado.R;

public class draw_addapter extends ArrayAdapter<String>

{
    String title[];
    int imgs[];
    Activity context;



    public draw_addapter(Activity context, String[] title , int[] imgs)
    {
        super(context, R.layout.row_drawer,title);
        this.title = title;
        this.context = context;
        this.imgs = imgs;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View rowView;


        LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.drawer_row,null,true);
        TextView name=(TextView) rowView.findViewById(R.id.text_drawer);
        ImageView img = (ImageView) rowView.findViewById(R.id.img_drawer);


        name.setText(title[position]);
        img.setImageResource(imgs[position]);

        return rowView;
    }
}

