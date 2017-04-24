package com.cybussolutions.bataado.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Responce_Friends_addapter extends ArrayAdapter<String>
{
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;
    String userid;
    private ArrayList<Home_Model> arraylist;
    Activity context;
    ImageView accpet,reject;



    public Responce_Friends_addapter(Activity context, ArrayList<Home_Model> list)
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
        rowView = inflater.inflate(R.layout.responce_friend_row,null,true);

        final TextView username = (TextView) rowView.findViewById(R.id.fr_name);
        ImageView profile_pic = (ImageView) rowView.findViewById(R.id.fr_pp);
        accpet = (ImageView) rowView.findViewById(R.id.accept);
        reject = (ImageView) rowView.findViewById(R.id.reject);

        final Home_Model  home_model = arraylist.get(position);
        username.setText(home_model.getFirstname() +" "+ home_model.getLastname());

        SharedPreferences pref = getContext().getApplicationContext().getSharedPreferences("BtadoPrefs", getContext().MODE_PRIVATE);
        userid = pref.getString("user_id","");





        accpet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResponce(home_model.getUserid(),userid,"1");

            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResponce(home_model.getUserid(),userid,"2");
            }
        });



        String pp =home_model.getProfilepic();

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

        return rowView;
    }


    public void sendResponce(final String sender, final String reciever , final  String status)
    {

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.SEND_RESPONCE_REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.startsWith("1%updated") ||response.startsWith("0%updated")  ) {

                            new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success")
                                    .setConfirmText("OK").setContentText("Your respoce has been updated")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();

                                        }
                                    })
                                    .show();

                        }
                        else {

                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();

                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {


                if (error instanceof NoConnectionError)
                {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();
                }
                else if (error instanceof TimeoutError) {


                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("Connection Time Out Error")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();
                }
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {

                Map<String,String> params = new HashMap<>();
                params.put("reciever",sender);
                params.put("sender",reciever);
                params.put("status",status);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);


    }


}
