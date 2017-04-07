package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.ListView;
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
import com.cybussolutions.bataado.Adapter.Add_Friends_addapter;
import com.cybussolutions.bataado.Adapter.Friends_addapter;
import com.cybussolutions.bataado.Fragments.Drawer_Fragment;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Find_Friends extends AppCompatActivity {

    Drawer_Fragment drawerFragment = new Drawer_Fragment();
    Toolbar toolbar;
    String ids;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;
    ListView friends_list;
    ArrayList<Home_Model> friendsmodel = new ArrayList<>();
    Add_Friends_addapter friends_addapter =  null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__friends);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Find Friends");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);



        friends_list = (ListView) findViewById(R.id.friends_list );




        Intent intent= getIntent();

        ids = intent.getStringExtra("fb_ids");
        ids = ids.substring(0, ids.length()-1);

        FindFriends();



    }


    public void FindFriends()
    {

        ringProgressDialog = ProgressDialog.show(this, "",	"Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();


        final StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_FB_FRIENDS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        ringProgressDialog.dismiss();


                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++)

                            {
                                JSONObject object = new JSONObject(jsonArray.getString(i));

                                Home_Model home_model = new Home_Model();

                                home_model.setUserid(object.getString("id"));
                                home_model.setFirstname(object.getString("first_name"));
                                home_model.setLastname(object.getString("last_name"));
                                home_model.setProfilepic(object.getString("profile_pic"));


                                friendsmodel.add(home_model);


                            }

                            friends_addapter = new Add_Friends_addapter(Find_Friends.this,friendsmodel);

                            friends_list.setAdapter(friends_addapter);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                ringProgressDialog.dismiss();

                if (error instanceof NoConnectionError)
                {
                    new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    LoginManager.getInstance().logOut();
                                }
                            })
                            .show();
                }
                else if (error instanceof TimeoutError) {


                    new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("Connection Time Out Error")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    LoginManager.getInstance().logOut();
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
                params.put("fb_id",ids);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);


    }
}
