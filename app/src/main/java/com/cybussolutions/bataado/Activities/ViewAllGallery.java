package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.cybussolutions.bataado.Adapter.BrandGallaryAdapter;
import com.cybussolutions.bataado.Adapter.ImageGalleryAdapter;
import com.cybussolutions.bataado.Model.SpacePhoto;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewAllGallery extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;
    private ArrayList<SpacePhoto> review_list= new ArrayList<>();
    BrandGallaryAdapter adapter;
    String brandId;
    TextView noGalleryFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_gallery);
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Photos and Videos");
        Intent intent = getIntent();
        brandId = intent.getStringExtra("brandId");
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

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView = findViewById(R.id.rv_images);
        noGalleryFound = findViewById(R.id.noImage);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        getBrandGallery("");
    }
    public void getBrandGallery(final String user_id)
    {

        ringProgressDialog = ProgressDialog.show(this, "",	"Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_BRAND_GALLERY,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        ringProgressDialog.dismiss();
                        parseJson(response);
                        adapter = new BrandGallaryAdapter(ViewAllGallery.this, review_list,"gallery");
                        recyclerView.setAdapter(adapter);

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                ringProgressDialog.dismiss();

                if (error instanceof NoConnectionError)
                {
                    new SweetAlertDialog(ViewAllGallery.this, SweetAlertDialog.ERROR_TYPE)
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

                    new SweetAlertDialog(ViewAllGallery.this, SweetAlertDialog.ERROR_TYPE)
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
                SharedPreferences pref =getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id","");
                // params.put("revID",reviewId);
                params.put("brandId",brandId);
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
            if(!response.equals("\"\"")) {
                JSONArray inner = new JSONArray(response);
                if (inner.length() < 1) {
                    noGalleryFound.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < inner.length(); i++) {
                    JSONObject innerobj = new JSONObject(inner.getString(i));
                    SpacePhoto spacePhoto = new SpacePhoto("", innerobj.getString("file_path"));
                    spacePhoto.setmTitle(innerobj.getString("file_path"));
                    spacePhoto.setMediaType(innerobj.getString("media_type"));
                    review_list.add(spacePhoto);
                }

            }else {
                noGalleryFound.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
