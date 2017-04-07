package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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
import com.cybussolutions.bataado.Adapter.Home_Addapter;
import com.cybussolutions.bataado.Fragments.Drawer_Fragment;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Brands_Model;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class HomeScreen extends AppCompatActivity {

    Toolbar toolbar;
    Drawer_Fragment drawerFragment = new Drawer_Fragment();
    Home_Addapter home_addapter = null;
    ListView home_list;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;
    private ArrayList<Brands_Model> list = new ArrayList<>();
    private ArrayList<Home_Model> brand_list= new ArrayList<>();
    TextView brand_name;
    ImageView brand_image,next,previous;
    ImageView search_footer,logo;
    EditText brand_reivew,search;
    RatingBar brand_rating;
    String reviewTxt,brandid,userid;
    float reviewRating;
    Button Post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getAllBrands();

        getAllReview();

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Home Screen");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);

        home_list = (ListView) findViewById(R.id.listview);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.list_header, home_list,
                false);

        home_list.addHeaderView(header);

        brand_name = (TextView) header.findViewById(R.id.brand_name);
        brand_image = (ImageView) header.findViewById(R.id.brand_image);
        next = (ImageView) header.findViewById(R.id.next);
        logo = (ImageView) findViewById(R.id.logo_img);
        previous = (ImageView) header.findViewById(R.id.previous);
        brand_reivew = (EditText) header.findViewById(R.id.brand_review);
        search = (EditText) findViewById(R.id.search);
        brand_rating = (RatingBar) header.findViewById(R.id.brand_raiting);
        search_footer = (ImageView) findViewById(R.id.search_fotter);
        Post = (Button) header.findViewById(R.id.post);

        SharedPreferences pref =getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        userid = pref.getString("user_id","");

        search_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this,SearchScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

        final int[] couunt = {1};

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nameToSearch=search.getText().toString();
                ArrayList<Home_Model> filteredLeaves=new ArrayList<Home_Model>();

                for (Home_Model data: brand_list) {
                    if (data.getBrand_name().toLowerCase().contains(nameToSearch.toLowerCase())  )
                    {
                        filteredLeaves.add(data);
                    }


                }
                /*leaveDatas.clear();
                leaveDatas.addAll(filteredLeaves);
                leaves_adapter.notifyDataSetChanged();*/

                home_addapter = new Home_Addapter(HomeScreen.this,filteredLeaves);

                home_list.setAdapter(home_addapter);



            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });




        
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                brand_rating.setRating(0);

                if(couunt[0]<list.size())
                {
                    int count = couunt[0]++;
                    brand_name.setText(list.get(count).getBrandname());


                    Picasso.with(HomeScreen.this)
                            .load(End_Points.IMAGE_BASE_URL + list.get(count).getBrandimage())
                            .resize(150, 150)
                            .centerCrop().transform(new CircleTransform())
                            .into(brand_image);
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                brand_rating.setRating(0);

                if(couunt[0]>1)
                {
                    int count = couunt[0]--;

                    brand_name.setText(list.get(count).getBrandname());

                    Picasso.with(HomeScreen.this)
                            .load(End_Points.IMAGE_BASE_URL + list.get(count).getBrandimage())
                            .resize(150, 150)
                            .centerCrop().transform(new CircleTransform())
                            .into(brand_image);
                }
            }
        });


        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reviewTxt = brand_reivew.getText().toString();
                reviewRating = brand_rating.getRating();
                brandid = list.get(couunt[0]-1).getBrandid();

                addUserReview();

                brand_reivew.setText("");
                brand_rating.setRating(0);

                home_list.invalidate();

            }
        });


    }


    public void getAllBrands()
    {

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_ALL_BRANDS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {


                        JSONArray inner;
                        try {
                            inner = new JSONArray(response);

                        for (int i= 0 ;i<=inner.length();i++) {
                            JSONObject innerobj = new JSONObject(inner.getString(i));

                            Brands_Model brands_model = new Brands_Model();
                            brands_model.setBrandid(innerobj.getString("id"));
                            brands_model.setBrandname(innerobj.getString("brand_name"));
                            brands_model.setBrandimage(innerobj.getString("brand_logo"));

                            list.add(brands_model);
                        }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        brand_name.setText(list.get(0).getBrandname());

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                if (error instanceof NoConnectionError)
                {
                    new SweetAlertDialog(HomeScreen.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection ! ")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();
                }
                else if (error instanceof TimeoutError)
                {
                        new SweetAlertDialog(HomeScreen.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error!")
                                .setConfirmText("OK").setContentText("Connection Time Out Error")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismiss();

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
                return new HashMap<>();
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);


    }


    public void getAllReview()
    {

        ringProgressDialog = ProgressDialog.show(this, "",	"Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_ALL_REVIEWS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        ringProgressDialog.dismiss();
                        parseJson(response);

                        home_addapter = new Home_Addapter(HomeScreen.this,brand_list);

                        home_list.setAdapter(home_addapter);

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                ringProgressDialog.dismiss();

                if (error instanceof NoConnectionError)
                {
                    new SweetAlertDialog(HomeScreen.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection ! ")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();
                }
                else if (error instanceof TimeoutError)
                {

                    new SweetAlertDialog(HomeScreen.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("Connection Time Out Error")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

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

                return new HashMap<>();
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);


    }

    public void addUserReview()
    {

        ringProgressDialog = ProgressDialog.show(this, "",	"Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.ADD_REVIEW,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                        //Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();

                        ringProgressDialog.dismiss();


                        if(response.equals("false"))
                        {
                            new SweetAlertDialog(HomeScreen.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error!")
                                    .setConfirmText("OK").setContentText("Some Error ")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();

                                        }
                                    })
                                    .show();
                        }
                        else
                        {

                            new SweetAlertDialog(HomeScreen.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!")
                                    .setConfirmText("OK").setContentText("Review added successfully ")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();
                                            home_addapter.clear();
                                            brand_list.clear();

                                            getAllReview();

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
                    new SweetAlertDialog(HomeScreen.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();
                }
                else if (error instanceof TimeoutError) {


                    new SweetAlertDialog(HomeScreen.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("Connection Time Out Error")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

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

                Map<String,String> params = new HashMap<>();
                params.put("user_id",userid);
                params.put("brand_id",brandid);
                params.put("review",reviewTxt);
                params.put("rating",reviewRating+"");

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

            JSONObject object =  new JSONObject(response);

            String res = object.getString("recent_reviews");


            JSONArray inner = new JSONArray(res);


            for (int i= 0 ;i<=inner.length();i++)
            {
                JSONObject innerobj = new JSONObject(inner.getString(i));

                Home_Model home_model = new Home_Model();

                home_model.setUserid(innerobj.getString("user_id"));
                home_model.setReview(innerobj.getString("review"));
                home_model.setPost_id(innerobj.getString("id"));
                home_model.setBrand_raiting(innerobj.getString("rating"));
                home_model.setBrandid(innerobj.getString("brand_id"));
                home_model.setRating(innerobj.getString("reviewRating"));
                home_model.setDate_created(innerobj.getString("creation_date"));
                home_model.setStatus(innerobj.getString("status"));
                home_model.setEmail_brand(innerobj.getString("email"));
                home_model.setFirstname(innerobj.getString("first_name"));
                home_model.setLastname(innerobj.getString("last_name"));
                home_model.setProfilepic(innerobj.getString("profile_pic"));
                home_model.setBrand_name(innerobj.getString("brand_name"));
                home_model.setNum_review(innerobj.getString("reviews"));
                home_model.setReviewid(innerobj.getString("reviewId"));
                home_model.setPhone(innerobj.getString("phone"));
                home_model.setWebsite_url(innerobj.getString("website_url"));
                home_model.setBlock(innerobj.getString("block"));
                home_model.setArea(innerobj.getString("area_town"));
                home_model.setBrand_logo(innerobj.getString("brand_logo"));

                brand_list.add(home_model);

                // Toast.makeText(HomeScreen.this,home_model.getReviewid(), Toast.LENGTH_SHORT).show();
            }



            // Toast.makeText(HomeScreen.this, outer, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
