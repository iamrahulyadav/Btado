package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
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

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUp extends AppCompatActivity {

    Toolbar toolbar;
    Button signUP;
    EditText username,password,confirm,f_name,l_name;
    String struser,strpass,str_fname,str_lname,str_cnfrmpass;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Sign Up with Bataa Do");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        username = (EditText) findViewById(R.id.usersignupemail);
        f_name = (EditText) findViewById(R.id.firstname);
        l_name = (EditText) findViewById(R.id.userlastname);
        password = (EditText) findViewById(R.id.usersignuppassword);
        confirm = (EditText) findViewById(R.id.userconfirm_password);
        signUP = (Button) findViewById(R.id.signup);

        signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                struser = username.getText().toString();
                str_fname = f_name.getText().toString();
                str_lname = l_name.getText().toString();
                strpass = password.getText().toString();
                str_cnfrmpass = confirm.getText().toString();

                if(str_cnfrmpass.equals(strpass))
                {
                    if(struser.equals("")||str_fname.equals("")||str_lname.equals("")||strpass.equals(""))
                    {
                        Toast.makeText(SignUp.this, "Required Fields are missing", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Signup_User();
                    }
                }
                else
                {
                    new SweetAlertDialog(SignUp.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("Passwords do not match")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();
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
                    public void onResponse(String response)
                    {
                        ringProgressDialog.dismiss();
                        if(response.equals("") )
                        {
                            new SweetAlertDialog(SignUp.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error!")
                                    .setConfirmText("OK").setContentText("Error Registering user")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();

                                        }
                                    })
                                    .show();
                        }
                        else if(response.equals("user Already Registerd with this Email ID"))
                        {
                            new SweetAlertDialog(SignUp.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error!")
                                    .setConfirmText("OK").setContentText("User Already Registered with this Email ID ")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();

                                        }
                                    })
                                    .show();
                        }
                        else {
                            new SweetAlertDialog(SignUp.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!")
                                    .setConfirmText("OK").setContentText("User Created Successfully ")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();
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
                params.put("email",struser);
                params.put("fname",str_fname);
                params.put("fb_id","");
                params.put("lname",str_lname);
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
}
