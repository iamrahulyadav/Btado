package com.cybussolutions.bataado.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

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
import com.cybussolutions.bataado.Adapter.search_model;
import com.cybussolutions.bataado.Fragments.Drawer_Fragment;
import com.cybussolutions.bataado.Model.Search_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SearchScreen extends AppCompatActivity {


    Toolbar toolbar;
    search_model drawer_addapter = null;
    ListView search_list;
    Drawer_Fragment drawerFragment = new Drawer_Fragment();
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ImageView home_fotter,profile_footer;
    EditText search;
    private ArrayList<Search_Model> category_list= new ArrayList<>();
    ArrayList<Search_Model> filteredLeaves;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Categories");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);



        search_list = findViewById(R.id.listview_search);
        home_fotter = findViewById(R.id.home_fotter);
        profile_footer = findViewById(R.id.profile_fotter);
       // search = findViewById(R.id.search);

        getAllCategories();



        search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String cat_id = category_list.get(i).getCategoryID();
                String cat_name = category_list.get(i).getCategoryName();


                Intent intent= new Intent(SearchScreen.this,Detailed_Category_activity.class);
                intent.putExtra("cat_id",cat_id);
                intent.putExtra("cat_name",cat_name);
                startActivity(intent);

            }
        });
        home_fotter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(SearchScreen.this,HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(intent);
                finish();
            }
        });

        profile_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pp = pref.getString("profile_pic","");
                String strname = pref.getString("user_name","");
                String strid = pref.getString("user_id","");
                Intent intent= new Intent(SearchScreen.this, Account_Settings.class);
                intent.putExtra("username", strname);
                intent.putExtra("userProfile",pp);
                intent.putExtra("userID",strid);
                startActivity(intent);
            }
        });
       /* search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String nameToSearch=search.getText().toString();

                if(search.getText().length() == 0 || search.getText().toString().equals(""))
                {
                    category_list = new ArrayList<>(filteredLeaves);
                }
                else
                {
                    category_list.clear();

                    for (Search_Model data: filteredLeaves) {

                            if (data.getCategoryName().toLowerCase().contains(nameToSearch.toLowerCase())  )
                            {
                                category_list.add(data);

                            }
                        }

                }
                *//*leaveDatas.clear();
                leaveDatas.addAll(filteredLeaves);
                leaves_adapter.notifyDataSetChanged();*//*

                drawer_addapter = new search_model(SearchScreen.this,category_list);

                search_list.setAdapter(drawer_addapter);

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });*/



    }

    public void getAllCategories()
    {

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_CATEGORIES,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                        parseJson(response);

                        drawer_addapter = new search_model(SearchScreen.this,category_list);

                        filteredLeaves = new ArrayList<>(category_list);

                        search_list.setAdapter(drawer_addapter);

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                if (error instanceof NoConnectionError)
                {
                    new SweetAlertDialog(SearchScreen.this, SweetAlertDialog.ERROR_TYPE)
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

                    new SweetAlertDialog(getApplication(), SweetAlertDialog.ERROR_TYPE)
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


    public  void parseJson(String response)
    {

        try {


            JSONArray inner = new JSONArray(response);


            for (int i= 0 ;i<inner.length();i++)
            {
                JSONObject innerobj = new JSONObject(inner.getString(i));

                Search_Model search_model = new Search_Model();

                search_model.setCategoryID(innerobj.getString("id"));
                search_model.setCategoryName(innerobj.getString("category_name"));
                search_model.setCatagoryLogo(innerobj.getString("category_logo"));


                category_list.add(search_model);


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
