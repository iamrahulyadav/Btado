package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

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
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.cybussolutions.bataado.Activities.HomeScreen.forCommentPos;
import static com.cybussolutions.bataado.Activities.HomeScreen.noOfComments;

public class Detail_Review extends AppCompatActivity {
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    ProgressDialog ringProgressDialog;
    ListView home_list;
    private ArrayList<Home_Model> brand_list = new ArrayList<>();
    Home_Addapter home_addapter = null;
    String reviewId,notification_id,tableName,flag;
    Toolbar toolbar;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail__review);
        forCommentPos=-1;
        noOfComments="0";
        FacebookSdk.sdkInitialize(Detail_Review.this);
        callbackManager = CallbackManager.Factory.create();
        HomeScreen.callbackManager=callbackManager;

        home_list=findViewById(R.id.listview);
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Review");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent=getIntent();
        reviewId=intent.getStringExtra("review_id");
        notification_id=intent.getStringExtra("noti_id");
        tableName=intent.getStringExtra("table_name");
        flag=intent.getStringExtra("flag");
        getReviewById();
        if(!flag.equals("1")){
            changeFlag();
        }
    }
    public void getReviewById() {

        ringProgressDialog = ProgressDialog.show(this, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_REVIEWS_BY_ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ringProgressDialog.dismiss();

                        parseJson(response);

                        home_addapter = new Home_Addapter(Detail_Review.this, brand_list);

                        home_list.setAdapter(home_addapter);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialog.dismiss();

                if (error instanceof NoConnectionError) {
                    new SweetAlertDialog(Detail_Review.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection ! ")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();
                } else if (error instanceof TimeoutError) {

                    new SweetAlertDialog(Detail_Review.this, SweetAlertDialog.ERROR_TYPE)
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                 params.put("revID",reviewId);
                params.put("userId", userid);
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
            String shareRes = object.getString("share");
            String likeRes = object.getString("likes");
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
                home_model.setFirstname(innerobj.getString("first_name"));
                home_model.setLastname(innerobj.getString("last_name"));
                home_model.setProfilepic(innerobj.getString("profile_pic"));
                home_model.setBrand_name(innerobj.getString("brand_name"));
                home_model.setReviewid(innerobj.getString("reviewId"));
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
                int total=Integer.parseInt(Totallikes.getString(i))+Integer.parseInt(TotalDislikes.getString(i))+Integer.parseInt(TotalUseful.getString(i));
                home_model.setTotalLikes(total);
                home_model.setTotalComments(innerTotalComments.getString(i));
                home_model.setTotalShares(innerTotalShares.getString(i));

                brand_list.add(home_model);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void changeFlag(){
        String Url = null;
        switch (tableName) {
            case "reviews_useful":
                Url = "updateUsefulNotificationFlag";
                break;
            case "reviews_dislike":
                Url = "updateDislikeNotificationFlag";
                break;
            case "reviews_like":
                Url = "updateLikeNotificationFlag";
                break;
            case "reviews_share":
                Url = "updateShareNotificationFlag";
                break;
            default:
                Url = "updateCommentNotificationFlag";
                break;
        }
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.BASE_URL+Url,
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(forCommentPos!=-1){
            brand_list.get(forCommentPos).setTotalComments(noOfComments);
            home_addapter.notifyDataSetChanged();
            forCommentPos=-1;
        }
    }
}
