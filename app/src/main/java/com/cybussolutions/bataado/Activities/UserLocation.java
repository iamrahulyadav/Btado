package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
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
import com.cybussolutions.bataado.Adapter.ImageGalleryAdapter;
import com.cybussolutions.bataado.Adapter.LocationAdapter;
import com.cybussolutions.bataado.Fragments.Drawer_Fragment;
import com.cybussolutions.bataado.Model.Locations_Model;
import com.cybussolutions.bataado.Model.SpacePhoto;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.cybussolutions.bataado.Utils.DialogBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserLocation extends AppCompatActivity {
    Toolbar toolbar;
    Drawer_Fragment drawerFragment = new Drawer_Fragment();
    ListView recyclerView;
    ProgressDialog ringProgressDialog;
    LocationAdapter adapter;
    private ArrayList<Locations_Model> review_list;
    ImageView addLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Locations");
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
     //   drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);
        recyclerView = findViewById(R.id.rv_location);
        addLocation=findViewById(R.id.addLocation);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserLocation.this,AddLocation.class);
                finish();
                startActivity(intent);
            }
        });

        getLocations();
    }


    public void getLocations()
    {

        ringProgressDialog = ProgressDialog.show(this, "",	"Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_LOCATIONS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        ringProgressDialog.dismiss();
                        parseJson(response);
                        adapter = new LocationAdapter(UserLocation.this, review_list,"");
                        recyclerView.setAdapter(adapter);

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                ringProgressDialog.dismiss();

                if (error instanceof NoConnectionError)
                {
                    new DialogBox(UserLocation.this, "No Internet Connection !", "Error",
                            "Error");
                }
                else if (error instanceof TimeoutError)
                {
                    new DialogBox(UserLocation.this, "Connection Time Out Error", "Error",
                            "Error");
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
                params.put("userId",userid);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);


    }
    public  void parseJson(String response)
    {
        review_list= new ArrayList<>();
        try {
            if(!response.equals("false")) {
                JSONArray inner = new JSONArray(response);
                for (int i = 0; i < inner.length(); i++) {
                    JSONObject innerobj = new JSONObject(inner.getString(i));
                    Locations_Model model=new Locations_Model();
                    model.setName(innerobj.getString("location_name"));
                    model.setId(innerobj.getString("id"));
                    model.setCity(innerobj.getString("city"));
                    model.setAddress(innerobj.getString("address"));
                    model.setPrimary(innerobj.getString("primary"));
                    model.setArea(innerobj.getString("area_town"));
                    review_list.add(model);

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
