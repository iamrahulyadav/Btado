package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.cybussolutions.bataado.Adapter.Home_Addapter;
import com.cybussolutions.bataado.Fragments.Drawer_Fragment;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.cybussolutions.bataado.Utils.DialogBox;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.cybussolutions.bataado.Activities.HomeScreen.forCommentPos;
import static com.cybussolutions.bataado.Activities.HomeScreen.noOfComments;

public class User_Profile extends AppCompatActivity {

    Toolbar toolbar;
    ListView reviews_list;
    ImageView search_footer,home_footer,profile_footer;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    ProgressDialog ringProgressDialog;
    Drawer_Fragment drawerFragment = new Drawer_Fragment();
    String user_id, email, profile_pic, adress;
    String review_count, username, photo_count, friends_count, userInfo, f_name, userId, friendsStatus;
    ImageView pp;
    TextView usernameTXT, firstname, adressTXT, review, photo, friend,num_reviews_user,tvMyFeeds;
    private ArrayList<Home_Model> review_list = new ArrayList<>();
    Home_Addapter home_addapter = null;
    RelativeLayout freinds_layout, reviews_layout, photos_layout,linearReviews;
    public static String noOfReviews="0";
    Button sendRequest;
    String notification_id,flag;
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        forCommentPos=-1;
        noOfComments="0";
        noOfReviews="1";
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();

        username = intent.getStringExtra("username");
        user_id = intent.getStringExtra("userID");
        if(intent.hasExtra("noti_id") && intent.hasExtra("flag")) {
            notification_id = intent.getStringExtra("noti_id");
            flag = intent.getStringExtra("flag");
        }

        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle(username);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);


        drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);
        FacebookSdk.sdkInitialize(User_Profile.this);
        callbackManager = CallbackManager.Factory.create();
        HomeScreen.callbackManager=callbackManager;

        reviews_list = findViewById(R.id.listview_profile);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.user_profile_header, reviews_list,
                false);

        reviews_list.addHeaderView(header);
        final SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        final String Currentuserid = pref.getString("user_id", "");
        usernameTXT = header.findViewById(R.id.username);
        firstname = header.findViewById(R.id.firstname);
        adressTXT = header.findViewById(R.id.adress_user);
        review = header.findViewById(R.id.rw_count);
        num_reviews_user = header.findViewById(R.id.num_reviews_user);
        photo = header.findViewById(R.id.ph_count);
        friend = header.findViewById(R.id.fr_count);
        pp = header.findViewById(R.id.pp);
        search_footer = findViewById(R.id.search_fotter);
        home_footer = findViewById(R.id.home_fotter);
        profile_footer = findViewById(R.id.profile_fotter);
        freinds_layout = header.findViewById(R.id.freinds_layout);
        reviews_layout = header.findViewById(R.id.reviews_layout);
        photos_layout = header.findViewById(R.id.photos_layout);
        linearReviews = header.findViewById(R.id.linearReviews);
        tvMyFeeds = header.findViewById(R.id.textView18);
        linearReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_Profile.this, User_Reviews.class);
                intent.putExtra("username", username);
                intent.putExtra("userID", user_id);
                intent.putExtra("profilePic", profile_pic);
                startActivity(intent);
            }
        });
        sendRequest = header.findViewById(R.id.button);
        home_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Profile.this, HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
        profile_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  String pp = pref.getString("profile_pic","");
                String strname = pref.getString("user_name","");
                String strid = pref.getString("user_id","");
                Intent intent= new Intent(User_Profile.this, Account_Settings.class);
                intent.putExtra("username", strname);
                intent.putExtra("userProfile",pp);
                intent.putExtra("userID",strid);
                finish();
                startActivity(intent);*/
            }
        });
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (friendsStatus.equals("1")) {
                    Toast.makeText(User_Profile.this, "You are Already Friends with " + username, Toast.LENGTH_SHORT).show();

                } else if (friendsStatus.equals("0")) {
                    Toast.makeText(User_Profile.this, "You have Already Sent Friend Request to " + username, Toast.LENGTH_SHORT).show();

                } else {
                    friendsStatus="0";
                    sendRequest(Currentuserid, user_id);

                }
            }
        });
        if (user_id.equals(Currentuserid)) {
            sendRequest.setVisibility(View.GONE);
        }else {
            tvMyFeeds.setText("Reviews");
        }

        addUserCounts();

        search_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Profile.this, SearchScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

        freinds_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(User_Profile.this, User_Friends.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);

            }
        });
        reviews_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Profile.this, User_Reviews.class);
                intent.putExtra("username", username);
                intent.putExtra("userID", user_id);
                intent.putExtra("profilePic", profile_pic);
                startActivity(intent);

            }
        });
        photos_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Profile.this, ImageGallary.class);
                intent.putExtra("username", username);
                intent.putExtra("userID", user_id);
                intent.putExtra("profilePic", profile_pic);
                startActivity(intent);

            }
        });
        if(intent.hasExtra("noti_id") && intent.hasExtra("flag")) {
            if (!flag.equals("1")) {
                changeFlag();
            }
        }

    }
    private void changeFlag() {
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.UPDATE_FRIEND_ACCEPTED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("id", notification_id);

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
    public void sendRequest(final String sender, final String reciever) {

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.ADD_FRIEND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (!(response.equals(""))) {
                            sendRequest.setBackground(getResources().getDrawable(R.drawable.pending));
                            new DialogBox(User_Profile.this, "Friend request sent", "Error",
                                    "Success");
                        } else {
                            new DialogBox(User_Profile.this, "Some thing went wrong !! ", "Error",
                                    "Error");
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error instanceof NoConnectionError) {
                    new DialogBox(User_Profile.this, "No Internet Connection !", "Error",
                            "Error");
                } else if (error instanceof TimeoutError) {
                    new DialogBox(User_Profile.this, "Connection Time Out Error", "Error",
                            "Error");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("sender", sender);
                params.put("reciever", reciever);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        RequestQueue requestQueue = Volley.newRequestQueue(User_Profile.this);
        requestQueue.add(request);


    }
    public void addUserCounts1() {


        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();


                        try {
                            JSONObject object = new JSONObject(response);

                            review_count = object.getString("total_reviews");
                            photo_count = object.getString("total_photos");
                            friends_count = object.getString("total_friends");
                            friendsStatus = object.getString("friendsStatus");
                            userInfo = object.getString("userInfo");


                            JSONObject userIn = new JSONObject(userInfo);

                            email = userIn.getString("email");
                            profile_pic = userIn.getString("profile_pic");
                            adress = userIn.getString("adress");
                            f_name = userIn.getString("first_name");
                            userId = userIn.getString("id");
                            if (profile_pic.equals("")) {
                                Picasso.with(User_Profile.this)
                                        .load("http://bataado.cybussolutions.com/uploads/no-image-icon-hi.png")
                                        .resize(150, 150)
                                        .centerCrop().transform(new CircleTransform())
                                        .into(pp);
                            } else {

                                if (profile_pic.startsWith("https://graph.facebook.com/")|| profile_pic.startsWith("https://scontent.xx.fbcdn.net/") || profile_pic.startsWith("http://graph.facebook.com/")) {
                                    Picasso.with(User_Profile.this)
                                            .load(profile_pic)
                                            .resize(150, 150)
                                            .centerCrop().transform(new CircleTransform())
                                            .into(pp);
                                } else {
                                    Picasso.with(User_Profile.this)
                                            .load(End_Points.IMAGE_PROFILE_PIC + profile_pic)
                                            .resize(150, 150)
                                            .centerCrop().transform(new CircleTransform())
                                            .into(pp);

                                }

                            }
                            firstname.setText("Pakistan");
                            if(adress.equals("") || adress.equals("null") || adress==null)
                                adress="no Location Found";
                            usernameTXT.setText(username);

                            adressTXT.setText(adress);
                            review.setText(review_count);
                            //noOfReviews=review_count;
                            num_reviews_user.setText(review_count);
                            photo.setText(photo_count);
                            friend.setText(friends_count);
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);

                            String uid = pref.getString("user_id","");
                            if(user_id.equals(uid)) {
                                SharedPreferences.Editor editor = pref.edit();
                                // Saving string
                                editor.putString("total_reviews", review_count);
                                editor.putString("total_photos", photo_count);
                                editor.putString("total_friends", friends_count);
                                editor.apply();
                                editor.commit();
                                drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);
                            }
                            if (friendsStatus.equals("{\"status\":\"1\"}")) {
                                friendsStatus = "1";
                                sendRequest.setBackground(getResources().getDrawable(R.drawable.friends_btn));
                            } else if (friendsStatus.equals("{\"status\":\"0\"}")) {
                                friendsStatus = "0";
                                sendRequest.setBackground(getResources().getDrawable(R.drawable.pending));
                            } else {
                                friendsStatus = "2";
                                sendRequest.setBackground(getResources().getDrawable(R.drawable.add_friend));
                            }

                          //  getUserReview();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error instanceof NoConnectionError) {
                    new DialogBox(User_Profile.this, "No Internet Connection !", "Error",
                            "Error");
                } else if (error instanceof TimeoutError) {

                    new DialogBox(User_Profile.this, "Connection Time Out Error", "Error",
                            "Error");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                final SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                final String Currentuserid = pref.getString("user_id", "");
                params.put("user_id", user_id);
                params.put("stalkerID", Currentuserid);

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
    public void addUserCounts() {


        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();


                        try {
                            JSONObject object = new JSONObject(response);

                            review_count = object.getString("total_reviews");
                            photo_count = object.getString("total_photos");
                            friends_count = object.getString("total_friends");
                            friendsStatus = object.getString("friendsStatus");
                            userInfo = object.getString("userInfo");


                            JSONObject userIn = new JSONObject(userInfo);

                            email = userIn.getString("email");
                            profile_pic = userIn.getString("profile_pic");
                            adress = userIn.getString("adress");
                            f_name = userIn.getString("first_name");
                            userId = userIn.getString("id");
                            if (profile_pic.equals("")) {
                                Picasso.with(User_Profile.this)
                                        .load("http://bataado.cybussolutions.com/uploads/no-image-icon-hi.png")
                                        .resize(150, 150)
                                        .centerCrop().transform(new CircleTransform())
                                        .into(pp);
                            } else {

                                if (profile_pic.startsWith("https://graph.facebook.com/")|| profile_pic.startsWith("https://scontent.xx.fbcdn.net/") || profile_pic.startsWith("http://graph.facebook.com/")) {
                                    Picasso.with(User_Profile.this)
                                            .load(profile_pic)
                                            .resize(150, 150)
                                            .centerCrop().transform(new CircleTransform())
                                            .into(pp);
                                } else {
                                    Picasso.with(User_Profile.this)
                                            .load(End_Points.IMAGE_PROFILE_PIC + profile_pic)
                                            .resize(150, 150)
                                            .centerCrop().transform(new CircleTransform())
                                            .into(pp);

                                }

                            }
                            firstname.setText("Pakistan");
                            if(adress.equals("") || adress.equals("null") || adress==null)
                                adress="no Location Found";
                            usernameTXT.setText(username);

                            adressTXT.setText(adress);
                            review.setText(review_count);
                            //noOfReviews=review_count;
                            num_reviews_user.setText(review_count);
                            photo.setText(photo_count);
                            friend.setText(friends_count);
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);

                            String uid = pref.getString("user_id","");
                            if(user_id.equals(uid)) {
                               SharedPreferences.Editor editor = pref.edit();
                                // Saving string
                                editor.putString("total_reviews", review_count);
                                editor.putString("total_photos", photo_count);
                                editor.putString("total_friends", friends_count);
                                editor.apply();
                                editor.commit();
                            }
                            if (friendsStatus.equals("{\"status\":\"1\"}")) {
                                friendsStatus = "1";
                                sendRequest.setBackground(getResources().getDrawable(R.drawable.friends_btn));
                            } else if (friendsStatus.equals("{\"status\":\"0\"}")) {
                                friendsStatus = "0";
                                sendRequest.setBackground(getResources().getDrawable(R.drawable.pending));
                            } else {
                                friendsStatus = "2";
                                sendRequest.setBackground(getResources().getDrawable(R.drawable.add_friend));
                            }

                            getUserReview();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error instanceof NoConnectionError) {
                    new DialogBox(User_Profile.this, "No Internet Connection !", "Error",
                            "Error");
                } else if (error instanceof TimeoutError) {

                    new DialogBox(User_Profile.this, "Connection Time Out Error", "Error",
                            "Error");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                final SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                final String Currentuserid = pref.getString("user_id", "");
                params.put("user_id", user_id);
                params.put("stalkerID", Currentuserid);

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

    public void getUserReview() {
        ringProgressDialog = ProgressDialog.show(this, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_USER_REVIEWS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();

                        ringProgressDialog.dismiss();

                        parseJson(response);

                        home_addapter = new Home_Addapter(User_Profile.this, review_list,"User_Profile");

                        reviews_list.setAdapter(home_addapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ringProgressDialog.dismiss();


                if (error instanceof NoConnectionError) {
                    new DialogBox(User_Profile.this, "No Internet Connection !", "Error",
                            "Error");
                } else if (error instanceof TimeoutError) {

                    new DialogBox(User_Profile.this, "Connection Time Out Error", "Error",
                            "Error");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);

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

    public void parseJson(String response) {

        try {
            JSONObject object = new JSONObject(response);

            String res = object.getString("recent_reviews");
            String likeRes = object.getString("likes");
            String shareRes = object.getString("share");
            String unlikeRes = object.getString("dislikes");
            String usefulRes = object.getString("useful");
            String totalComments = object.getString("totalComments");
            String totalShares = object.getString("totalShares");
            String totalLikes = object.getString("total_likes");
            String totalDislikes = object.getString("total_dislikes");
            String totalUseful = object.getString("total_useful");

            JSONArray inner = new JSONArray(res);
            JSONArray inner1 = new JSONArray(likeRes);
            JSONArray inner2 = new JSONArray(unlikeRes);
            JSONArray inner3 = new JSONArray(usefulRes);
            JSONArray inner4 = new JSONArray(shareRes);

            JSONArray innerTotalComments = new JSONArray(totalComments);
            JSONArray innerTotalShares = new JSONArray(totalShares);
            JSONArray Totallikes = new JSONArray(totalLikes);
            JSONArray TotalDislikes = new JSONArray(totalDislikes);
            JSONArray TotalUseful = new JSONArray(totalUseful);


            for (int i = 0; i < inner.length(); i++) {
                JSONObject innerobj = new JSONObject(inner.getString(i));

                Home_Model home_model = new Home_Model();
                home_model.setUserid(innerobj.getString("user_id"));
                home_model.setReview(innerobj.getString("review"));
                home_model.setPost_id(innerobj.getString("id"));
                if(innerobj.getString("forced_rating").equals("1"))
                    home_model.setBrand_raiting(innerobj.getString("rating"));
                else
                    home_model.setBrand_raiting("0");
                home_model.setBrandid(innerobj.getString("brand_id"));
                home_model.setRating(innerobj.getString("reviewRating"));
                home_model.setDate_created(innerobj.getString("creation_date"));
                home_model.setStatus(innerobj.getString("status"));
                //    home_model.setEmail_brand(innerobj.getString("email"));
                home_model.setFirstname(innerobj.getString("first_name"));
                home_model.setLastname(innerobj.getString("last_name"));
                home_model.setProfilepic(innerobj.getString("profile_pic"));
                home_model.setBrand_name(innerobj.getString("brand_name"));
                // home_model.setNum_review(innerobj.getString("reviews"));
                home_model.setReviewid(innerobj.getString("reviewId"));
                //home_model.setIcon("0");
                home_model.setMedia_type(innerobj.getString("media_type"));
                home_model.setFile_path(innerobj.getString("file_path"));
                home_model.setPhone(innerobj.getString("phone"));
                home_model.setWebsite_url(innerobj.getString("website_url"));
                home_model.setBlock(innerobj.getString("block"));
                home_model.setArea(innerobj.getString("area_town"));
                home_model.setBrand_latitude(innerobj.getString("latitude"));
                home_model.setBrand_longitude(innerobj.getString("longitude"));
                home_model.setBrand_logo(innerobj.getString("brand_logo"));
                home_model.setInappropriate(innerobj.getString("inappropriate"));
                if (inner1.getString(i).equals("1")) {
                    home_model.setIcon("1");
                } else if (inner2.getString(i).equals("1")) {
                    home_model.setIcon("2");
                } else if (inner3.getString(i).equals("1")) {
                    home_model.setIcon("3");
                } else {
                    home_model.setIcon("0");
                }
                if(inner4.getString(i).equals("1")){
                    home_model.setShared("1");
                }else {
                    home_model.setShared("0");
                }
                home_model.setTotalComments(innerTotalComments.getString(i));
                home_model.setTotalShares(innerTotalShares.getString(i));
                int total=Integer.parseInt(Totallikes.getString(i))+Integer.parseInt(TotalDislikes.getString(i))+Integer.parseInt(TotalUseful.getString(i));
                home_model.setTotalLikes(total);
                review_list.add(home_model);

                // Toast.makeText(HomeScreen.this,home_model.getReviewid(), Toast.LENGTH_SHORT).show();
            }
          /*  JSONArray inner = new JSONArray(response);

            for (int i = 0; i < inner.length(); i++) {
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
                home_model.setBrand_raiting(innerobj.getString("rating"));
                home_model.setDate_created(innerobj.getString("creation_date"));
                home_model.setIcon("0");
                home_model.setMedia_type(innerobj.getString("media_type"));
                home_model.setFile_path(innerobj.getString("file_path"));


                review_list.add(home_model);

                // Toast.makeText(HomeScreen.this,home_model.getReviewid(), Toast.LENGTH_SHORT).show();
            }*/


            // Toast.makeText(HomeScreen.this, outer, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(forCommentPos!=-1){
            review_list.get(forCommentPos).setTotalComments(noOfComments);
            home_addapter.notifyDataSetChanged();
            forCommentPos=-1;
        }
    }
    public void OnDelete(){
        addUserCounts1();
    }
}

