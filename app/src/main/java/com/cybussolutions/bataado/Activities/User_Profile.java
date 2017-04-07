package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.cybussolutions.bataado.Adapter.Home_Addapter;
import com.cybussolutions.bataado.Fragments.Drawer_Fragment;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class User_Profile extends AppCompatActivity {

    Toolbar toolbar;
    ListView reviews_list;
    ImageView search_footer;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;
    Drawer_Fragment drawerFragment = new Drawer_Fragment();
    String user_id,email,profile_pic,adress;
    String review_count,username,photo_count,friends_count,userInfo,f_name,userId;
    ImageView pp;
    TextView usernameTXT,firstname,adressTXT,review,photo,friend;
    private ArrayList<Home_Model> review_list= new ArrayList<>();
    Home_Addapter home_addapter = null;
    RelativeLayout freinds_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent= getIntent();

        username = intent.getStringExtra("username");
        user_id= intent.getStringExtra("userID");



        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(username);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);


        drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);


        reviews_list = (ListView) findViewById(R.id.listview_profile);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.user_profile_header, reviews_list,
                false);

        reviews_list.addHeaderView(header);

        usernameTXT = (TextView) header.findViewById(R.id.username);
        firstname = (TextView) header.findViewById(R.id.firstname);
        adressTXT = (TextView) header.findViewById(R.id.adress_user);
        review = (TextView) header.findViewById(R.id.rw_count);
        photo = (TextView) header.findViewById(R.id.ph_count);
        friend = (TextView) header.findViewById(R.id.fr_count);
        pp = (ImageView) header.findViewById(R.id.pp);
        search_footer = (ImageView) findViewById(R.id.search_fotter);
        freinds_layout = (RelativeLayout)header.findViewById(R.id.freinds_layout);

        addUserCounts();

        search_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Profile.this,SearchScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

        freinds_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(User_Profile.this,User_Friends.class);
                intent.putExtra("user_id",user_id);
                startActivity(intent);
                finish();

            }
        });


    }

    public void addUserCounts()
    {


        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_COUNT,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        //Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();


                        try {
                            JSONObject object = new JSONObject(response);

                                    review_count=object.getString("total_reviews");
                                    photo_count=object.getString("total_photos");
                                    friends_count=object.getString("total_friends");
                                    userInfo=object.getString("userInfo");


                            JSONObject userIn = new JSONObject(userInfo);

                                    email = userIn.getString("email");
                                    profile_pic= userIn.getString("profile_pic");
                                    adress= userIn.getString("adress");
                                    f_name= userIn.getString("first_name");
                                    userId= userIn.getString("id");

                            if(profile_pic.equals(""))
                            {
                                Picasso.with(User_Profile.this)
                                        .load("http://bataado.cybussolutions.com/uploads/no-image-icon-hi.png")
                                        .resize(150, 150)
                                        .centerCrop().transform(new CircleTransform())
                                        .into(pp);
                            }

                            if(profile_pic.startsWith("https://fb"))
                            {
                                Picasso.with(User_Profile.this)
                                        .load(profile_pic)
                                        .resize(150, 150)
                                        .centerCrop().transform(new CircleTransform())
                                        .into(pp);
                            }
                            else
                            {
                                Picasso.with(User_Profile.this)
                                        .load(End_Points.IMAGE_PROFILE_PIC +profile_pic )
                                        .resize(150, 150)
                                        .centerCrop().transform(new CircleTransform())
                                        .into(pp);

                            }



                            usernameTXT.setText(username);
                            firstname.setText(f_name);
                            adressTXT.setText(adress);
                            review.setText(review_count);
                            photo.setText(photo_count);
                            friend.setText(friends_count);


                            getUserReview();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {


                if (error instanceof NoConnectionError)
                {
                    new SweetAlertDialog(User_Profile.this, SweetAlertDialog.ERROR_TYPE)
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


                    new SweetAlertDialog(User_Profile.this, SweetAlertDialog.ERROR_TYPE)
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
                params.put("user_id",user_id);

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

    public void getUserReview()
    {
        ringProgressDialog = ProgressDialog.show(this, "",	"Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_USER_REVIEWS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        //Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();

                        ringProgressDialog.dismiss();

                        parseJson(response);

                        home_addapter = new Home_Addapter(User_Profile.this,review_list);

                        reviews_list.setAdapter(home_addapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                ringProgressDialog.dismiss();


                if (error instanceof NoConnectionError)
                {
                    new SweetAlertDialog(User_Profile.this, SweetAlertDialog.ERROR_TYPE)
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


                    new SweetAlertDialog(User_Profile.this, SweetAlertDialog.ERROR_TYPE)
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
                params.put("user_id",user_id);

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

    public  void parseJson(String response)
    {

        try {

            JSONArray inner = new JSONArray(response);

            for (int i= 0 ;i<=inner.length();i++)
            {
                JSONObject innerobj = new JSONObject(inner.getString(i));

                Home_Model home_model = new Home_Model();

                home_model.setUserid(innerobj.getString("user_id"));
                home_model.setReview(innerobj.getString("review"));
                home_model.setPost_id(innerobj.getString("id"));
                home_model.setFirstname(username);
                home_model.setLastname("");
                home_model.setProfilepic(profile_pic);
                home_model.setBrandid(innerobj.getString("brand_id"));
                home_model.setBrand_name(innerobj.getString("brand_name"));
                home_model.setRating(innerobj.getString("reviewRating"));
                home_model.setDate_created(innerobj.getString("creation_date"));


                review_list.add(home_model);

                // Toast.makeText(HomeScreen.this,home_model.getReviewid(), Toast.LENGTH_SHORT).show();
            }



            // Toast.makeText(HomeScreen.this, outer, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

