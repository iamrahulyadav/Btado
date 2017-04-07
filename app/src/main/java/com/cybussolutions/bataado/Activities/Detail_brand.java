package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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

public class Detail_brand extends AppCompatActivity {

    Toolbar toolbar;
    ListView review_list;
    TextView brand_name;
    ImageView brand_image;
    TextView brand_reivew,review_count;
    RatingBar brand_rating;
    Home_Addapter home_addapter = null;
    String brandNAme,brandId,brandRating,brandAdress,brandPic,reviewCount;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    private ArrayList<Home_Model> brand_list= new ArrayList<>();
    ProgressDialog ringProgressDialog;
    ImageView home_fotter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_brand);

        Intent intent = getIntent();
        brandNAme = intent.getStringExtra("brandNAme");
        brandId = intent.getStringExtra("brandId");
        brandRating = intent.getStringExtra("brandRating");
        brandAdress = intent.getStringExtra("brandAdress");
        brandPic = intent.getStringExtra("brandPic");
        reviewCount = intent.getStringExtra("reviewCount");

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(brandNAme);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);


        getAllReview();

        review_list = (ListView) findViewById(R.id.listview_search);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.detail_brand_header, review_list,
                false);

        review_list.addHeaderView(header);

        brand_name = (TextView) header.findViewById(R.id.brand_name);
        brand_image = (ImageView) header.findViewById(R.id.brand_image);
        brand_reivew = (TextView) header.findViewById(R.id.review);
        review_count = (TextView) header.findViewById(R.id.review_count);
        brand_rating = (RatingBar) header.findViewById(R.id.ratingBar_home);

        brand_name.setText(brandNAme);
        brand_reivew.setText(brandAdress);


        Picasso.with(this)
                .load(End_Points.IMAGE_BASE_URL + brandPic)
                .resize(150, 150)
                .centerCrop().transform(new CircleTransform())
                .into(brand_image);



        brand_rating.setRating(Integer.parseInt(brandRating));

        home_fotter = (ImageView) findViewById(R.id.home_fotter);
        home_fotter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Detail_brand.this,HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

    }

    public void getAllReview()
    {

        ringProgressDialog = ProgressDialog.show(this, "",	"Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_REVIEW_BRAND,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                        try {
                            JSONObject object = new JSONObject(response);

                            String count = object.getString("total_reviews");
                            review_count.setText(count);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        ringProgressDialog.dismiss();

                        parseJson(response);

                        home_addapter = new Home_Addapter(Detail_brand.this,brand_list);

                        review_list.setAdapter(home_addapter);

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                ringProgressDialog.dismiss();

                if (error instanceof NoConnectionError)
                {
                    new SweetAlertDialog(getApplication(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection ! ")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();
                }
                else if (error instanceof TimeoutError)
                {

                    new SweetAlertDialog(getApplication(), SweetAlertDialog.ERROR_TYPE)
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
                params.put("brand_id",brandId);

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

            JSONObject object =  new JSONObject(response);

            String res = object.getString("recent_reviews");


            JSONArray inner = new JSONArray(res);


            for (int i= 0 ;i<=inner.length();i++)
            {
                JSONObject innerobj = new JSONObject(inner.getString(i));

                Home_Model home_model = new Home_Model();

                home_model.setUserid(innerobj.getString("user_id"));
                home_model.setReview(innerobj.getString("review"));
                home_model.setPost_id(innerobj.getString("id"));
                home_model.setBrandid(innerobj.getString("brand_id"));
                home_model.setRating(innerobj.getString("reviewRating"));
                home_model.setDate_created(innerobj.getString("creation_date"));
                home_model.setStatus(innerobj.getString("status"));
                home_model.setEmail_brand(innerobj.getString("email"));
                home_model.setFirstname(innerobj.getString("first_name"));
                home_model.setLastname(innerobj.getString("last_name"));
                home_model.setProfilepic(innerobj.getString("profile_pic"));
                home_model.setBrand_name(innerobj.getString("brand_name"));
                home_model.setNum_review(innerobj.getString("reviews"));
                home_model.setReviewid(innerobj.getString("reviewId"));
                home_model.setPhone(innerobj.getString("phone"));
                home_model.setWebsite_url(innerobj.getString("website_url"));
                home_model.setBlock(innerobj.getString("block"));
                home_model.setArea(innerobj.getString("area_town"));
                home_model.setBrand_logo(innerobj.getString("brand_logo"));

                brand_list.add(home_model);

                // Toast.makeText(HomeScreen.this,home_model.getReviewid(), Toast.LENGTH_SHORT).show();
            }



            // Toast.makeText(HomeScreen.this, outer, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
