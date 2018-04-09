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
import com.cybussolutions.bataado.Utils.FieldValidator;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditProfile extends AppCompatActivity {
    Toolbar toolbar;
    EditText username,etphone,etaddress,f_name,l_name;
    String struser,strphone,str_fname,str_lname,strAddress;
    Button signUP;
    ProgressDialog ringProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        f_name = findViewById(R.id.firstname);
        l_name = findViewById(R.id.userlastname);
        etphone = findViewById(R.id.usersignuppassword);
        etaddress = findViewById(R.id.userconfirm_password);
        signUP = findViewById(R.id.signup);
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Update Profile");
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
        signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_fname = f_name.getText().toString();
                str_lname = l_name.getText().toString();
                strphone = etphone.getText().toString();
                strAddress = etaddress.getText().toString();
                if(str_fname.equals("") || str_lname.equals("")){
                    Toast.makeText(EditProfile.this, "Required Fields are missing", Toast.LENGTH_SHORT).show();
                }else {
                    Update_User();
                }

            }
        });
        String[] name=pref.getString("user_name","").split(" ");
        String firstName=pref.getString("first_name","");
        String lastName=pref.getString("last_name","");
        String phone=pref.getString("phone","");
        String address=pref.getString("address","");
        f_name.setText(firstName);
        l_name.setText(lastName);
        if(!phone.equals("null"))
        etphone.setText(phone);
        else
            etphone.setText("");
        if(!address.equals("null"))
        etaddress.setText(address);
        else
            etaddress.setText("");


    }
    public void Update_User()
    {

        ringProgressDialog = ProgressDialog.show(this, "Please wait ...",	"Signing up user ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.UPDATE_USER,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(final String response)
                    {
                        ringProgressDialog.dismiss();
                        if(response.equals("") )
                        {
                            new SweetAlertDialog(EditProfile.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error!")
                                    .setConfirmText("OK").setContentText("Error Updating Profile")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();

                                        }
                                    })
                                    .show();
                        }
                        else {
                            new SweetAlertDialog(EditProfile.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!")
                                    .setConfirmText("OK").setContentText("User Updated Successfully ")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();
                                            SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = pref.edit();
                                            // Saving string
                                            editor.putString("user_name",str_fname+" "+str_lname);
                                            editor.putString("phone",strphone);
                                            editor.putString("address",strAddress);

                                            editor.apply();
                                            finish();

                                        }
                                    })
                                    .show();
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
                params.put("fname",str_fname);
                params.put("lname",str_lname);
                params.put("address",strAddress);
                params.put("phone",strphone);
                params.put("userID",user_id);
                params.put("profile_pic","");
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
