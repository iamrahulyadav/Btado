package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import com.cybussolutions.bataado.Utils.DialogBox;
import com.cybussolutions.bataado.Utils.FieldValidator;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUp extends AppCompatActivity {

    Toolbar toolbar;
    Button signUP;
    EditText username,password,confirm,f_name,l_name,phone_number;
    String struser,strpass,str_fname,str_lname,str_cnfrmpass,str_phone_number;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Sign Up");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        username = findViewById(R.id.usersignupemail);
        f_name = findViewById(R.id.firstname);
        l_name = findViewById(R.id.userlastname);
        phone_number = findViewById(R.id.userPhone);
        password = findViewById(R.id.usersignuppassword);
        confirm = findViewById(R.id.userconfirm_password);
        signUP = findViewById(R.id.signup);

        signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                struser = username.getText().toString();
                str_fname = f_name.getText().toString();
                str_lname = l_name.getText().toString();
                str_phone_number = phone_number.getText().toString();
                strpass = password.getText().toString();
                str_cnfrmpass = confirm.getText().toString();


                    if(struser.equals("")||str_fname.equals("")||str_lname.equals("")||strpass.equals("") || str_cnfrmpass.equals("") || str_phone_number.equals(""))
                    {
                        Toast.makeText(SignUp.this, "Required Fields are missing", Toast.LENGTH_SHORT).show();
                    }else  if(!FieldValidator.ValidateEmail(SignUp.this, struser)){
                        Toast.makeText(SignUp.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                    }else if(!FieldValidator.lengthValidator(SignUp.this,str_phone_number,13,15,"Phone")){
                        Toast.makeText(SignUp.this, "Phone number minimum has 13 digits", Toast.LENGTH_SHORT).show();
                    }else if(!FieldValidator.lengthValidator(SignUp.this,strpass,8,100,"Password")){
                       // Toast.makeText(SignUp.this, "Password should have minimum 8 characters", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(str_cnfrmpass.equals(strpass))
                        {
                        Signup_User();
                        }
                        else
                        {
                            Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                          /*  new SweetAlertDialog(SignUp.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Alert!")
                                    .setConfirmText("OK").setContentText("Passwords do not match")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();

                                        }
                                    })
                                    .show();*/
                        }
                    }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void Signup_User()
    {

        ringProgressDialog = ProgressDialog.show(this, "Please wait ...",	"Signing up user ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.SIGN_UP,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(final String response)
                    {
                        ringProgressDialog.dismiss();
                        if(response.equals("") )
                        {
                            Toast.makeText(SignUp.this, "Error Registering user", Toast.LENGTH_SHORT).show();
                           /* new SweetAlertDialog(SignUp.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Alert!")
                                    .setConfirmText("OK").setContentText("Error Registering user")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();

                                        }
                                    })
                                    .show();*/
                        }
                        else if(response.equals("user Already Registerd with this Email ID"))
                        {
                            Toast.makeText(SignUp.this, "User Already Registered with this Email.", Toast.LENGTH_SHORT).show();
                           /* new SweetAlertDialog(SignUp.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Alert!")
                                    .setConfirmText("OK").setContentText("User Already Registered with this Email ID ")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();

                                        }
                                    })
                                    .show();*/
                        }
                        else {
                           /* new SweetAlertDialog(SignUp.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!")
                                    .setConfirmText("OK").setContentText("User Created Successfully ")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();*/
                            new DialogBox(SignUp.this, "Verification Email Sent Successfully", "signup",
                                    "SuccessSignUP");
                                           /* SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = pref.edit();
                                            // Saving string
                                            editor.putString("total_reviews", "0");
                                            editor.putString("total_photos","0");
                                            editor.putString("total_friends","0");
                                            editor.putString("user_id", response);
                                            editor.putString("user_name",str_fname+" "+str_lname);
                                            editor.putString("email",struser);
                                            editor.putString("profile_pic","");


                                            editor.apply();*/

/*
                                        }
                                    })
                                    .show();*/
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
                params.put("email",struser);
                params.put("fname",str_fname);
                params.put("fb_id","");
                params.put("lname",str_lname);
                params.put("phone",str_phone_number);
                params.put("password",strpass);
                params.put("image","");
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
