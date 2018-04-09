package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by Rizwan Jillani on 14-Feb-18.
 */

public class User_Reviews extends AppCompatActivity {

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
    RelativeLayout freinds_layout,reviews_layout,photos_layout;
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_reviews);
        FacebookSdk.sdkInitialize(User_Reviews.this);
        callbackManager = CallbackManager.Factory.create();
        HomeScreen.callbackManager=callbackManager;
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Reviews");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        forCommentPos=-1;
        noOfComments="0";
        drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);

        Intent intent=  getIntent();

        user_id = intent.getStringExtra("userID");
        profile_pic = intent.getStringExtra("profilePic");
        username = intent.getStringExtra("username");


        reviews_list = findViewById(R.id.listview);
        getUserReview();

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

                        home_addapter = new Home_Addapter(User_Reviews.this,review_list);

                        reviews_list.setAdapter(home_addapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                ringProgressDialog.dismiss();


                if (error instanceof NoConnectionError)
                {
                    new DialogBox(User_Reviews.this, "No Internet Connection !", "Error",
                            "Error");
                }
                else if (error instanceof TimeoutError) {

                    new DialogBox(User_Reviews.this, "Connection Time Out Error", "Error",
                            "Error");
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



            // Toast.makeText(HomeScreen.this, outer, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
   /* public  void parseJson(String response)
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
                home_model.setIcon("0");


                review_list.add(home_model);

                // Toast.makeText(HomeScreen.this,home_model.getReviewid(), Toast.LENGTH_SHORT).show();
            }



            // Toast.makeText(HomeScreen.this, outer, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
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
}

