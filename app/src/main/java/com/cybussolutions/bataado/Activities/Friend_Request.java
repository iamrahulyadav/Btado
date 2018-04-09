package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
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
import com.cybussolutions.bataado.Adapter.Add_Friends_addapter;
import com.cybussolutions.bataado.Adapter.Responce_Friends_addapter;
import com.cybussolutions.bataado.Fragments.Drawer_Fragment;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Friend_Request extends AppCompatActivity {

    Drawer_Fragment drawerFragment = new Drawer_Fragment();
    Toolbar toolbar;
    String ids;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;
    ListView friends_list;
    Responce_Friends_addapter  responce_friends_addapter = null;
    ArrayList<Home_Model> friendsmodel = new ArrayList<>();
    String notification_id,flag;
    ImageView search_footer,home_footer,profile_footer;
    TextView tvNoRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend__request);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        Intent intent=getIntent();
        if(intent.hasExtra("noti_id") && intent.hasExtra("flag")) {
            notification_id = intent.getStringExtra("noti_id");
            flag = intent.getStringExtra("flag");
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        friends_list = findViewById(R.id.friends_list );
        tvNoRequest = findViewById(R.id.noRequestText );

        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Friend Requests");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        search_footer = findViewById(R.id.search_fotter);
        home_footer = findViewById(R.id.home_fotter);
        profile_footer = findViewById(R.id.profile_fotter);
        drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);

        getAllFriendRequests();
        if(intent.hasExtra("noti_id") && intent.hasExtra("flag")) {
            if (!flag.equals("1")) {
                changeFlag();
            }
        }
        search_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Friend_Request.this, SearchScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

        home_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Friend_Request.this, HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

        profile_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pp = pref.getString("profile_pic","");
                String strname = pref.getString("user_name","");
                String strid = pref.getString("user_id","");
                Intent intent= new Intent(Friend_Request.this, Account_Settings.class);
                intent.putExtra("username", strname);
                intent.putExtra("userProfile",pp);
                intent.putExtra("userID",strid);
                startActivity(intent);
            }
        });
    }

    private void changeFlag() {
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.BASE_URL+"updateFriendNotificationFlag",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("id", notification_id);

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

    public void getAllFriendRequests()
    {

        ringProgressDialog = ProgressDialog.show(this, "",	"Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();


        final StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_ALL_FRIEND_REQUESTS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        ringProgressDialog.dismiss();

                        parseJson(response);

                        responce_friends_addapter = new Responce_Friends_addapter(Friend_Request.this,friendsmodel);

                        friends_list.setAdapter(responce_friends_addapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                ringProgressDialog.dismiss();

                if (error instanceof NoConnectionError)
                {
                    new SweetAlertDialog(Friend_Request.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    LoginManager.getInstance().logOut();
                                }
                            })
                            .show();
                }
                else if (error instanceof TimeoutError) {


                    new SweetAlertDialog(Friend_Request.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("Connection Time Out Error")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    LoginManager.getInstance().logOut();
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

                SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String id  = pref.getString("user_id","");


                Map<String,String> params = new HashMap<>();
                params.put("user_id",id);
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

            if(!response.equals("false")) {


                JSONArray inner = new JSONArray(response);


                for (int i = 0; i < inner.length(); i++) {
                    JSONObject innerobj = new JSONObject(inner.getString(i));

                    Home_Model home_model = new Home_Model();
                    home_model.setPost_id(innerobj.getString("noti_id"));
                    home_model.setUserid(innerobj.getString("id"));
                    home_model.setEmail_brand(innerobj.getString("email"));
                    home_model.setFirstname(innerobj.getString("first_name"));
                    home_model.setLastname(innerobj.getString("last_name"));
                    home_model.setProfilepic(innerobj.getString("profile_pic"));
                    home_model.setBlock(innerobj.getString("address"));

                    friendsmodel.add(home_model);

                    // Toast.makeText(HomeScreen.this,home_model.getReviewid(), Toast.LENGTH_SHORT).show();
                }
            }else {
                tvNoRequest.setVisibility(View.VISIBLE);
            }








            // Toast.makeText(HomeScreen.this, outer, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
