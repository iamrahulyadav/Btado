package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.cybussolutions.bataado.Utils.DialogBox;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Login extends AppCompatActivity {

    Toolbar toolbar;
    Button signUP, login;
    EditText username,password;
    String struser,strpass;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;
    CheckBox remCheckBox;
    String review_count,photo_count,friends_count,userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        String user_session= pref.getString("remmember_me", null);
        if(user_session != null && user_session.equals("loggedin"))
        {
            Intent intent= new Intent(Login.this, HomeScreen.class);
            finish();
            startActivity(intent);


        }
        else
        {
        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Log In");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        signUP = findViewById(R.id.signup);
        login = findViewById(R.id.login);
        username = findViewById(R.id.userloginemail);
        password = findViewById(R.id.userloginpassword);
        remCheckBox = findViewById(R.id.checkBox);

        signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(Login.this,SignUp.class);
                startActivity(intent);

            }
        });

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    struser = username.getText().toString();
                    strpass = password.getText().toString();

                    if(struser.equals(""))
                    {
                        Toast.makeText(Login.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                    else if(strpass.equals(""))
                    {
                        Toast.makeText(Login.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        login_user();
                    }


                }
            });



        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void login_user()
    {

        ringProgressDialog = ProgressDialog.show(this, "Please wait ...",	"Checking Credentials ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.LOGIN,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                        //Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();

                        ringProgressDialog.dismiss();


                        if(response.equals("false"))
                        {
                            Toast.makeText(Login.this, "Incorrect Email or Password !!", Toast.LENGTH_SHORT).show();

                           /* new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error!")
                                    .setConfirmText("OK").setContentText("Incorrect Email or Password!!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();

                                        }
                                    })
                                    .show();*/
                        }
                        else if(response.equals("0"))
                        {
                            Toast.makeText(Login.this, "Your Account has been blocked ! Contact Administration .", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String name,email,profile_pic,phone,address;

                            try {

                               JSONObject object = new JSONObject(response);
                                if(object.getString("is_active").equals("1")) {
                                    userid = object.getString("id");
                                    name = object.getString("first_name");
                                    name += " " + object.getString("last_name");
                                    email = object.getString("email");
                                    profile_pic = object.getString("profile_pic");
                                    phone = object.getString("phone_number");
                                    address = object.getString("address");


                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    // Saving string
                                    editor.putString("user_id", userid);
                                    editor.putString("user_name", name);
                                    editor.putString("first_name", object.getString("first_name"));
                                    editor.putString("last_name", object.getString("last_name"));
                                    editor.putString("email", email);
                                    editor.putString("profile_pic", profile_pic);
                                    editor.putString("phone", phone);
                                    editor.putString("address", address);
                                    editor.putString("fb_user", "0");

                                    if (remCheckBox.isChecked()) {

                                        editor.putString("remmember_me", "loggedin");

                                    }

                                    editor.apply();

                                    getCounts();
                                }else if(object.getString("is_active").equals("2")){
                                    new DialogBox(Login.this, "Please verify your email address", "Alert",
                                            "Error");
                                }else {
                                    new DialogBox(Login.this, "Your account has been deactivated by Admin. Please contact Admin for further information", "Alert",
                                            "Error");
                                }




                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                ringProgressDialog.dismiss();
                if (error instanceof NoConnectionError)
                {
                    new DialogBox(Login.this, "No Internet Connection", "Error",
                            "Error");
                    /*new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();*/
                }
                else if (error instanceof TimeoutError) {

                    new DialogBox(Login.this, "Connection Time Out Error", "Error",
                            "Error");
                  /*  new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("Connection Time Out Error")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();*/
                }
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {

                Map<String,String> params = new HashMap<>();
                params.put("user_name",struser);
                params.put("password",strpass);
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

    public void getCounts()
    {



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

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            // Saving string
                            editor.putString("total_reviews", review_count);
                            editor.putString("total_photos",photo_count);
                            editor.putString("total_friends",friends_count);


                            editor.apply();


                            Intent intent= new Intent(Login.this, HomeScreen.class);
                            finish();
                            startActivity(intent);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {


                if (error instanceof NoConnectionError)
                {
                    new DialogBox(Login.this, "No Internet Connection", "Error",
                            "Error");
                   /* new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Alert!")
                            .setConfirmText("OK").setContentText("No Internet Connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();*/
                }
                else if (error instanceof TimeoutError) {

                    new DialogBox(Login.this, "Connection Time Out Error", "Error",
                            "Error");
                   /* new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Alert!")
                            .setConfirmText("OK").setContentText("Connection Time Out Error")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();*/
                }
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {

                Map<String,String> params = new HashMap<>();
                params.put("user_id",userid);
                params.put("stalkerID",userid);

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
