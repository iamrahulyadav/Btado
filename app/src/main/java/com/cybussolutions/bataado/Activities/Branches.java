package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.cybussolutions.bataado.Adapter.BranchesAdapter;
import com.cybussolutions.bataado.Adapter.Home_Addapter;
import com.cybussolutions.bataado.Model.Branches_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Branches extends AppCompatActivity {
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    Toolbar toolbar;
    String brandName,brandId;
    ProgressDialog ringProgressDialog;
    ListView branchesListView;
    BranchesAdapter branchesAdapter;
    private ArrayList<Branches_Model> arraylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);
        Intent intent=getIntent();
        brandId=intent.getStringExtra("brandId");
        brandName=intent.getStringExtra("brandName");
        branchesListView=findViewById(R.id.listview);
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Branches of "+brandName);
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
        getBranches();
    }
    public void getBranches() {

        ringProgressDialog = ProgressDialog.show(this, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_BRANCHES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ringProgressDialog.dismiss();
                        try {
                            arraylist=new ArrayList<>();
                            JSONArray branchesArray=new JSONArray(response);
                            for(int i=0;i<branchesArray.length();i++){
                                Branches_Model branches_model=new Branches_Model();
                                JSONObject jsonObject=branchesArray.getJSONObject(i);
                                branches_model.setBranchName(jsonObject.getString("area_town"));
                                branches_model.setBranchContact(jsonObject.getString("phone"));
                                branches_model.setBranchEmail(jsonObject.getString("email"));
                                branches_model.setBranchTiming(jsonObject.getString("opening_time")+"-"+jsonObject.getString("closing_time"));
                                branches_model.setBranchAddress(jsonObject.getString("address")+" "+jsonObject.getString("block")+" "+jsonObject.getString("area_town"));
                                arraylist.add(branches_model);
                            }
                            branchesAdapter=new BranchesAdapter(Branches.this,arraylist);
                            branchesListView.setAdapter(branchesAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialog.dismiss();

                if (error instanceof NoConnectionError) {
                    Toast.makeText(Branches.this,"No Internet Connection",Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {

                    Toast.makeText(Branches.this,"Connection Timeout error",Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                params.put("id",brandId);
                //params.put("userId", userid);
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
