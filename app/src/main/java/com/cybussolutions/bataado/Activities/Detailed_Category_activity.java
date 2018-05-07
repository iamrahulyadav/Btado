package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.cybussolutions.bataado.Adapter.Search_Detail_Addapter;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Detailed_Category_activity extends AppCompatActivity {


    String cat_id,cat_name;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;
    Search_Detail_Addapter search_detail_addapter;
    private ArrayList<Home_Model> brand_list= new ArrayList<>();
    EditText searchText;
    Toolbar toolbar;
    ListView search_list;
    ImageView home_fotter;
    ArrayList<Home_Model> filteredLeaves;
    TextView tvNoData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_category);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final Intent intent= getIntent();
        cat_id = intent.getStringExtra("cat_id");
        cat_name = intent.getStringExtra("cat_name");

        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle(cat_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        search_list = findViewById(R.id.listview_search);
        tvNoData = findViewById(R.id.noData);
        searchText = findViewById(R.id.search);

        searchText.setHint(cat_name);
        searchText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchText.setCursorVisible(true);
                return false;
            }
        });

        getAllReview();

        search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent1= new Intent(Detailed_Category_activity.this,MapsActivity.class);
                    intent1.putExtra("brandId",brand_list.get(i).getBrandid());
                    intent1.putExtra("brandNAme",brand_list.get(i).getBrand_name());
                    intent1.putExtra("brandRating",brand_list.get(i).getRating());
                    intent1.putExtra("brandAdress",brand_list.get(i).getAdress());
                    intent1.putExtra("brandPic",brand_list.get(i).getBrand_logo());
                    intent1.putExtra("reviewCount",brand_list.get(i).getNum_review());
                    startActivity(intent1);

            }
        });


        home_fotter = findViewById(R.id.home_fotter);
        home_fotter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Detailed_Category_activity.this,HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nameToSearch=searchText.getText().toString();

                if(searchText.getText().length() == 0 || searchText.getText().toString().equals(""))
                {
                    brand_list=new ArrayList<>(filteredLeaves);
                }
                else
                {
                    brand_list.clear();

                for (Home_Model data: filteredLeaves) {



                        if (data.getBrand_name().toLowerCase().contains(nameToSearch.toLowerCase())  )
                        {
                            brand_list.add(data);
                        }
                    }
                }
                if(brand_list.size()<1){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }
                /*leaveDatas.clear();
                leaveDatas.addAll(filteredLeaves);
                leaves_adapter.notifyDataSetChanged();*/

                search_detail_addapter = new Search_Detail_Addapter(Detailed_Category_activity.this,brand_list);

                search_list.setAdapter(search_detail_addapter);



            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
    }


    public void getAllReview()
    {


        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_CATEGORIES_DATA,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        if(response.equals("\"\"")){
                            tvNoData.setVisibility(View.VISIBLE);
                            tvNoData.setText("No Data Available in "+cat_name+".");
                        }

                        parseJson(response);

                        search_detail_addapter = new Search_Detail_Addapter(Detailed_Category_activity.this,brand_list);

                        filteredLeaves=new ArrayList<>(brand_list);

                        search_list.setAdapter(search_detail_addapter);

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

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
                params.put("cat_id",cat_id);
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

            String res = object.getString("brands");
            if(res.equals("")){
                tvNoData.setVisibility(View.VISIBLE);
                tvNoData.setText("No Data Available in "+cat_name+".");
            }
            String rating = object.getString("rating");
            JSONArray inner = new JSONArray(res);

            JSONArray inner1 = new JSONArray(rating);


            for (int i= 0 ;i<inner.length();i++)
            {
                JSONObject innerobj = new JSONObject(inner.getString(i));
              //  JSONObject innerobj1 = new JSONObject(inner1.getString(i));

                Home_Model home_model = new Home_Model();


                home_model.setPost_id(innerobj.getString("id"));
                home_model.setBrandid(innerobj.getString("brand_id"));
                home_model.setDate_created(innerobj.getString("creation_date"));
                home_model.setStatus(innerobj.getString("status"));
                home_model.setEmail_brand(innerobj.getString("email"));
                home_model.setBrand_name(innerobj.getString("brand_name"));
                home_model.setNum_review(innerobj.getString("reviews"));
                home_model.setAdress(innerobj.getString("address"));
                home_model.setRating(inner1.getString(i));
                home_model.setIcon("0");
                home_model.setPhone(innerobj.getString("phone"));
                home_model.setWebsite_url(innerobj.getString("website_url"));
                home_model.setBlock(innerobj.getString("block"));
                home_model.setArea(innerobj.getString("area_town"));
                home_model.setBrand_logo(innerobj.getString("brand_logo"));
               // home_model.setMedia_type(innerobj.getString("media_type"));
               // home_model.setFile_path(innerobj.getString("file_path"));

                brand_list.add(home_model);

                // Toast.makeText(HomeScreen.this,home_model.getReviewid(), Toast.LENGTH_SHORT).show();
            }



            // Toast.makeText(HomeScreen.this, outer, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(searchText.isCursorVisible()){
            searchText.setCursorVisible(false);
            searchText.setText("");
        }else {
            super.onBackPressed();
        }
    }
}
