package com.cybussolutions.bataado.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class Account_Settings extends AppCompatActivity {

    Toolbar toolbar;
    TextView userName;
    LinearLayout editProfile,profilePhotos,profilePassword,profileLocation;
    CircleImageView profile_image;
    View view2,view3;
    EditText oldPassword,newPassword,confirmPassword;
    AlertDialog alertDialog;
    ProgressDialog ringProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account__settings);
        userName=findViewById(R.id.user_name);
        editProfile=findViewById(R.id.linear1);
        profilePhotos=findViewById(R.id.linear3);
        profilePassword=findViewById(R.id.linear2);
        profileLocation=findViewById(R.id.linear4);
        view2=findViewById(R.id.view2);
        view3=findViewById(R.id.view3);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Account_Settings.this,EditProfile.class);
                startActivity(intent);
            }
        });
        profilePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePasswordDialog();
            }
        });
        profilePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Account_Settings.this,ProfilePhotos.class);
                startActivity(intent);
            }
        });
        profileLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Account_Settings.this,UserLocation.class);
                startActivity(intent);
            }
        });
        profile_image=findViewById(R.id.profile_image);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        String pp = pref.getString("profile_pic","");
        String strname = pref.getString("user_name","");
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Account Settings");
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
        userName.setText(strname);
        if(pref.getString("fb_user","").equals("1")){
            profilePassword.setVisibility(View.GONE);
            profilePhotos.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
            view3.setVisibility(View.GONE);
        }
        if(pp.equals(""))
        {
            Picasso.with(Account_Settings.this)
                    .load(End_Points.IMAGE_RREVIEW_URL+"no-image-icon-hi.png")
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(profile_image);
        }
        else {
            if (pp.startsWith("https://graph.facebook.com/")) {
                Picasso.with(Account_Settings.this)
                        .load(pp)
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(profile_image);
            } else {
                Picasso.with(Account_Settings.this)
                        .load(End_Points.IMAGE_PROFILE_PIC + pp)
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(profile_image);

            }
        }
    }
    private void changePasswordDialog() {

        LayoutInflater inflater = LayoutInflater.from(Account_Settings.this);
        View subView = inflater.inflate(R.layout.custom_dialog_for_change_password, null);
        oldPassword = subView.findViewById(R.id.oldPass);
        newPassword = subView.findViewById(R.id.etPass);
        confirmPassword = subView.findViewById(R.id.etConfirmPass);

        alertDialog = new AlertDialog.Builder(Account_Settings.this).create();
        alertDialog.setCancelable(false);
        TextView ok = subView.findViewById(R.id.ok);
        TextView cancel = subView.findViewById(R.id.cancel_btn);
        showKeyBoard(oldPassword);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldpass=oldPassword.getText().toString();
                String newpass=newPassword.getText().toString();
                String confirmpass=confirmPassword.getText().toString();
                if(oldpass.length()<1 && newpass.length()<1 && confirmpass.length()<1) {
                    Toast.makeText(Account_Settings.this,"Please Enter Password",Toast.LENGTH_LONG).show();
                }else {
                    if(newpass.equals(confirmpass)) {
                        if(newpass.length()>7 && !newpass.contains(" ")) {
                            CheckPass();
                            hideKeyBoard(oldPassword);
                            alertDialog.dismiss();
                        }
                        else if(newpass.length()<8)
                            Toast.makeText(Account_Settings.this,"Password length must be minimum 8 characters",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(Account_Settings.this,"Password should not contain spaces",Toast.LENGTH_LONG).show();

                    }else {
                        Toast.makeText(Account_Settings.this,"New password and confirm password fields mismatches",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyBoard(oldPassword);
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(subView);
        alertDialog.show();
    }
    private void showKeyBoard(EditText title) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }
    private void hideKeyBoard(EditText title) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
    }
    public void CheckPass() {

        ringProgressDialog = ProgressDialog.show(Account_Settings.this, "Please wait ...", "Checking Credentials ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.CHANGE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ringProgressDialog.dismiss();

                        if(response.equals("1")) {
                            Toast.makeText(Account_Settings.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                        }else if(response.equals("2")){
                            Toast.makeText(Account_Settings.this, "Your old password does not match", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            Toast.makeText(Account_Settings.this, "Not Updated", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ringProgressDialog.dismiss();
                if (error instanceof NoConnectionError) {

                    new SweetAlertDialog(Account_Settings.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            })
                            .show();
                } else if (error instanceof TimeoutError) {

                    new SweetAlertDialog(Account_Settings.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("Connection TimeOut! Please check your internet connection.")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                final SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid= pref.getString("user_id","");

                params.put("userId",userid);
                params.put("password",oldPassword.getText().toString());
                params.put("password_new",newPassword.getText().toString());
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Account_Settings.this, HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        String pp = pref.getString("profile_pic","");
        if(pp.equals(""))
        {
            Picasso.with(Account_Settings.this)
                    .load(End_Points.IMAGE_RREVIEW_URL+"no-image-icon-hi.png")
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(profile_image);
        }
        else {
            if (pp.startsWith("https://graph.facebook.com/") || pp.startsWith("https://fb-s-b-a.akamaihd.net/")|| pp.startsWith("https://scontent.xx.fbcdn.net/") || pp.startsWith("http://graph.facebook.com/")) {
                Picasso.with(Account_Settings.this)
                        .load(pp)
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(profile_image);
            } else {
                Picasso.with(Account_Settings.this)
                        .load(End_Points.IMAGE_PROFILE_PIC + pp)
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(profile_image);

            }
        }
    }
}
