package com.cybussolutions.bataado.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.DrawerPojo;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomDrawerAdapter extends BaseAdapter {

    Context context;
    List<DrawerPojo> drawerItemList;
    int layoutResources;
    LayoutInflater inflater;
    //private int[] selectedposition;


    public CustomDrawerAdapter(Context context, int resource, List<DrawerPojo> objects) {
       // super(context, resource, objects);
        this.context = context;
        this.drawerItemList = objects;
        this.layoutResources = resource;
    }

    @Override
    public int getCount() {
        return drawerItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return drawerItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder drawerHolder;
        View view =convertView;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new ViewHolder();

            convertView = inflater.inflate(layoutResources, parent, false);
            drawerHolder.tvTitle = convertView
                    .findViewById(R.id.tvNavigationDrawer);
            drawerHolder.profile_pic = convertView.findViewById(R.id.imageIconNavigationDrawer);
            drawerHolder.linearLayout = convertView.findViewById(R.id.linear);


            convertView.setTag(drawerHolder);

        } else {
            drawerHolder = (ViewHolder) convertView.getTag();

        }

        DrawerPojo dItem = this.drawerItemList.get(position);
        String pp = dItem.getUser_image();
        if(dItem.getNotificationId().equals("")){
            drawerHolder.profile_pic.setVisibility(View.GONE);
        }else {
            drawerHolder.profile_pic.setVisibility(View.VISIBLE);
        }
        if(!pp.equals("icon")) {
            drawerHolder.tvTitle.setText(drawerItemList.get(position).getUser_name() + " " + drawerItemList.get(position).getComment());
        }else {
            drawerHolder.tvTitle.setText(drawerItemList.get(position).getUser_name() + "" + drawerItemList.get(position).getComment());
        }
        if(dItem.getNotificationFlag().equals("1")){
            drawerHolder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
        }else {
            drawerHolder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.lightGray));
        }


        if (pp.startsWith("http://graph.facebook.com/")|| pp.startsWith("https://scontent.xx.fbcdn.net/") || pp.startsWith("https://graph.facebook.com/")) {
            Picasso.with(context)
                    .load(pp)
                    .resize(80, 80)
                    .centerCrop().transform(new CircleTransform())
                    .into(drawerHolder.profile_pic);
        }else if(pp.equals("icon")){
            Picasso.with(context)
                    .load(R.drawable.chat_noti_icon)
                    .resize(60, 60)
                    .into(drawerHolder.profile_pic);
        } else {
            if (pp.equals("")) {
                Picasso.with(context)
                        .load("http://bataado.cybussolutions.com/uploads/no-image-icon-hi.png")
                        .resize(80, 80)
                        .centerCrop().transform(new CircleTransform())
                        .into(drawerHolder.profile_pic);
            } else {
                Picasso.with(context)
                        .load(End_Points.IMAGE_PROFILE_PIC + pp)
                        .resize(80, 80)
                        .centerCrop().transform(new CircleTransform())
                        .into(drawerHolder.profile_pic);
            }


        }
//        drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(
//                dItem.getImgResID()));

        Collections.sort(drawerItemList, new Comparator<DrawerPojo>() {
            public int compare(DrawerPojo first, DrawerPojo second)  {
                return second.getDate().compareTo(first.getDate());
            }
        });

        return convertView;
    }



    private class ViewHolder {
        TextView tvTitle;
        ImageView profile_pic;
        LinearLayout linearLayout;
    }



}

