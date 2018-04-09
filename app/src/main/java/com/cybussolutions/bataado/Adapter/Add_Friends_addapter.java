package com.cybussolutions.bataado.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.cybussolutions.bataado.Activities.HomeScreen;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Add_Friends_addapter extends ArrayAdapter<String>
{
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;
    String userid;
    private ArrayList<Home_Model> arraylist;
    Activity context;
    Button add_friend;



    public Add_Friends_addapter(Activity context, ArrayList<Home_Model> list)
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
        rowView = inflater.inflate(R.layout.add_friend_row,null,true);

        final TextView username = rowView.findViewById(R.id.fr_name);
        ImageView profile_pic = rowView.findViewById(R.id.fr_pp);
         add_friend = rowView.findViewById(R.id.add_friend);

        final Home_Model  home_model = arraylist.get(position);
        username.setText(home_model.getFirstname() +" "+ home_model.getLastname());

        SharedPreferences pref = getContext().getApplicationContext().getSharedPreferences("BtadoPrefs", getContext().MODE_PRIVATE);
        userid = pref.getString("user_id","");

        final String[] status = {home_model.getStatus()};

        if (status[0].equals("1"))
        {
            add_friend.setText("Friends");

        }
        else if (status[0].equals("0"))
        {
            add_friend.setText("Pending");

        }


        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(status[0].equals("2"))
                {
                    status[0] = "0";
                    add_friend.setText("Pending");
                    arraylist.get(position).setStatus("0");
                    notifyDataSetChanged();
                    sendRequest(userid,home_model.getUserid());
                }
                else if(status[0].equals("0"))
                {
                    Toast.makeText(getContext(), "You have Already Sent Friend Request to "+ username.getText().toString(), Toast.LENGTH_SHORT).show();
                }

                else if(status[0].equals("1"))
                {
                    Toast.makeText(getContext(), "You are Already Friends with "+ username.getText().toString(), Toast.LENGTH_SHORT).show();
                }




            }
        });

        String pp =home_model.getProfilepic();

        if(pp.startsWith("https://graph.facebook.com/")|| pp.startsWith("https://scontent.xx.fbcdn.net/") || pp.startsWith("https://graph.facebook.com/"))
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


    public void sendRequest(final String sender, final String reciever)
    {

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.ADD_FRIEND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(!(response.equals("")))
                        {
                            new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!")
                                    .setConfirmText("OK").setContentText("Frient request sent ")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();
                                            add_friend.setText("Pending");
                                        }
                                    })
                                    .show();

                        }
                        else
                        {
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error!")
                                    .setConfirmText("OK").setContentText("Some thing went wrong !! ")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();

                                        }
                                    })
                                    .show();
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
                params.put("sender",sender);
                params.put("reciever",reciever);

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
