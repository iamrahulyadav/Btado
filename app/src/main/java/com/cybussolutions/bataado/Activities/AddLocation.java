package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddLocation extends AppCompatActivity {
    Toolbar toolbar;
    EditText location,etaddress,name,city,area;
    Button addLocationBtn;
    String str_name,str_city,str_location,str_area;
    ProgressDialog ringProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        name = findViewById(R.id.locationName);
        city = findViewById(R.id.userCity);
        area = findViewById(R.id.area);
        etaddress = findViewById(R.id.userconfirm_password);
        addLocationBtn = findViewById(R.id.signup);
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Add Location");
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
        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_name = name.getText().toString();
                str_city = city.getText().toString();
                str_area = area.getText().toString();
                str_location = etaddress.getText().toString();
                if(str_name.equals("") || str_city.equals("")){
                    Toast.makeText(AddLocation.this, "Required Fields are missing", Toast.LENGTH_SHORT).show();
                }else {
                    addLocation();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(AddLocation.this,UserLocation.class);
        finish();
        startActivity(intent);
    }

    private void addLocation() {
        ringProgressDialog = ProgressDialog.show(this, "Please wait ...",	"", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.ADD_LOCATION,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(final String response)
                    {
                        ringProgressDialog.dismiss();
                        if(response.equals("0") )
                        {
                            new SweetAlertDialog(AddLocation.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error!")
                                    .setConfirmText("OK").setContentText("Error Adding Location")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();

                                        }
                                    })
                                    .show();
                        }
                        else {
                            Intent intent=new Intent(AddLocation.this,UserLocation.class);
                            finish();
                            startActivity(intent);
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                ringProgressDialog.dismiss();
                if (error instanceof NoConnectionError)
                {
                    Toast.makeText(getApplication(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof TimeoutError)
                {
                    Toast.makeText(getApplication(), "Connection TimeOut! Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<>();
                final SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String user_id = pref.getString("user_id","");
                params.put("location_name",str_name);
                params.put("city",str_city);
                params.put("address",str_location);
                params.put("area_town",str_area);
                params.put("userId",user_id);
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
}
