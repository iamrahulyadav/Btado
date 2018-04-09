
package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.cybussolutions.bataado.Utils.DialogBox;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Splash extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    Button login,newUser;
    private int[] layouts;
    LoginButton loginButton;
    private CallbackManager callbackManager;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;
    String review_count,photo_count,friends_count,userid,fb_ids="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()



        FacebookSdk.sdkInitialize(getApplicationContext());
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_splash);

      /*  try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }*/

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        String user_session= pref.getString("remmember_me", null);
        if(user_session != null && user_session.equals("loggedin"))
        {
            Intent intent= new Intent(Splash.this, HomeScreen.class);
            finish();
            startActivity(intent);


        }
        else
        {


        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        login = findViewById(R.id.login);
        newUser = findViewById(R.id.new_user);

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcom_slider1,
                R.layout.welcom_slider2,
                R.layout.welcom_slider3};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        // FaceBook login

        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","user_friends", "email"));
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final  LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                JSONObject obj = response.getJSONObject();
                                try {



                                    String name = obj.getString("name");

                                    String nameArray[] = name.split(" ");

                                    String f_name =  nameArray[0];
                                    String l_name = nameArray[1];


                                    String picture  = obj.getString("picture");

                                    JSONObject image = new JSONObject(picture);

                                    String data = image.getString("data");

                                    String fb_id = obj.getString("id");


                                    JSONObject url = new JSONObject(data);
                                    String imageURL ="https://graph.facebook.com/"+fb_id+"/picture?type=large";

                                    String email;
                                    if (!obj.isNull("email"))
                                    {
                                         email = obj.getString("email");
                                    }
                                    else
                                    {
                                         email = obj.getString("id") + "@facebook.com";
                                    }


                                    Signup_User(f_name,l_name,email,imageURL,fb_id);
                                  //  Toast.makeText(Splash.this,obj.getString("name"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                new GraphRequest(
                                        loginResult.getAccessToken(),
                                        "/me/friends",
                                        null,
                                        HttpMethod.GET,
                                        new GraphRequest.Callback() {
                                            public void onCompleted(GraphResponse response) {
                                       /* handle the result */
                                                JSONObject obj = response.getJSONObject();

                                                try {
                                                    String data = obj.getString("data");

                                                    JSONArray jsonArray = new JSONArray(data);

                                                    for (int i =0 ; i < jsonArray.length() ; i++) {
                                                        JSONObject datObj = new JSONObject(jsonArray.getString(i));

                                                        fb_ids +=datObj.getString("id")+",";

                                                        SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = pref.edit();
                                                        // Saving string
                                                        editor.putString("user_friends", fb_ids);

                                                        editor.apply();

                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                ).executeAsync();


                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

                Toast.makeText(Splash.this, "Canceled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Splash.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });





        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(Splash.this,Login.class);
                startActivity(intent);
            }
        });

        newUser.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {


                    Intent intent= new Intent(Splash.this,SignUp.class);
                    finish();
                    startActivity(intent);
                }
            });
        }
    }



    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }



    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void login_user(final String name, final String pass)
    {


        StringRequest request = new StringRequest(Request.Method.POST, End_Points.LOGIN,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                        //Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();




                        if(response.equals("false"))
                        {
                            ringProgressDialog.dismiss();
                            new DialogBox(Splash.this, "Incorrect User name or Password !!", "Error",
                                    "Error");
                        }
                        else if(response.equals("0"))
                        {
                            ringProgressDialog.dismiss();
                            Toast.makeText(Splash.this, "Your Account has been blocked ! Contact Administration .", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String name,email,profile_pic;

                            try {

                                JSONObject object = new JSONObject(response);

                                userid = object.getString("id");
                                name = object.getString("first_name");
                                name += " "+object.getString("last_name");
                                email = object.getString("email");
                                profile_pic = object.getString("profile_pic");

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                // Saving string
                                editor.putString("user_id", userid);
                                editor.putString("user_name",name);
                                editor.putString("first_name",object.getString("first_name"));
                                editor.putString("last_name",object.getString("last_name"));
                                editor.putString("email",email);
                                editor.putString("remmember_me","loggedin");
                                editor.putString("profile_pic",profile_pic);
                                editor.putString("fb_ids", fb_ids);
                                editor.putString("fb_user", "1");

                                editor.apply();

                                getCounts();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {


                if (error instanceof NoConnectionError)
                {
                    ringProgressDialog.dismiss();
                    new DialogBox(Splash.this, "No Internet Connection!", "Error",
                            "Error");
                }
                else if (error instanceof TimeoutError) {

                    new DialogBox(Splash.this, "Connection Time Out Error", "Error",
                            "Error");
                }
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {

                Map<String,String> params = new HashMap<>();
                params.put("user_name",name);
                params.put("password",pass);
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

    public void Signup_User(final String fname, final String lname, final String email,final String image, final String fb_id)
    {
        ringProgressDialog = ProgressDialog.show(this, "Please wait ...",	"Checking Credentials ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.SIGN_UP,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                        if(response.equals("") )
                        {
                            ringProgressDialog.dismiss();
                            new DialogBox(Splash.this, "Error Registering user", "Error",
                                    "Error");

                        }
                        else {
                                            login_user(email,"");

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
                    LoginManager.getInstance().logOut();
                }
                else if (error instanceof TimeoutError)
                {
                    LoginManager.getInstance().logOut();
                    Toast.makeText(getApplication(), "Connection TimeOut! Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("fname",fname);
                params.put("image",image);
                params.put("fb_id",fb_id);
                params.put("lname",lname);

                params.put("password","");
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
                        ringProgressDialog.dismiss();
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


                            Intent intent= new Intent(Splash.this, HomeScreen.class);
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
                ringProgressDialog.dismiss();
                if (error instanceof NoConnectionError)
                {
                    new DialogBox(Splash.this, "No Internet Connection!", "Error",
                            "Error");
                }
                else if (error instanceof TimeoutError) {

                    new DialogBox(Splash.this, "Connection Time Out Error", "Error",
                            "Error");
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
