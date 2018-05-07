package com.cybussolutions.bataado.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.cybussolutions.bataado.Adapter.Comments_Adapter;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Comment_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Comments extends AppCompatActivity {

    Toolbar toolbar;
    CircleImageView profileImage;
    String reviewId;
    ListView listViewComments;
    Comments_Adapter commentsAdapter;
    ImageView addComment;
    EditText etComment;
    private ArrayList<Comment_Model> list;// = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Intent intent=getIntent();

        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Comments");
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
        profileImage=findViewById(R.id.profile_image);
        listViewComments=findViewById(R.id.listComments);
        addComment=findViewById(R.id.send);
        etComment=findViewById(R.id.commenttext);
        final String pp;
        SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);

        pp = pref.getString("profile_pic","");
        reviewId=intent.getStringExtra("reviewId");
        getComments();
        if(pp.equals(""))
        {
            Picasso.with(Comments.this)
                    .load(End_Points.IMAGE_RREVIEW_URL+"no-image-icon-hi.png")
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(profileImage);
        }
        else {
            if (pp.startsWith("https://graph.facebook.com/") || pp.startsWith("https://fb-s-b-a.akamaihd.net/")|| pp.startsWith("https://scontent.xx.fbcdn.net/") || pp.startsWith("http://graph.facebook.com/")) {
                Picasso.with(Comments.this)
                        .load(pp)
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(profileImage);
            } else {
                Picasso.with(Comments.this)
                        .load(End_Points.IMAGE_PROFILE_PIC + pp)
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(profileImage);

            }
        }

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment=etComment.getText().toString();
                if(!comment.equals("") && comment.trim().length()>0) {
                    AddComment(comment);
                    etComment.setText("");
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        etComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String comment = etComment.getText().toString();
                if (!comment.equals("") && comment.trim().length() > 0) {
                    AddComment(comment);
                    etComment.setText("");
                    if (textView != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
    }

    private void AddComment(final String comment) {
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.ADD_NEW_COMMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equals("0")){
                            getComments();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    Toast.makeText(Comments.this,"Check your internet",Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {

                    Toast.makeText(Comments.this,"Check your internet",Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                params.put("userid", userid);
                params.put("reviewid", reviewId);
                params.put("comment", comment);
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

    public void getComments() {
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_ALL_COMMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJson(response);

                        commentsAdapter = new Comments_Adapter(Comments.this,list,reviewId);

                        listViewComments.setAdapter(commentsAdapter);

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    Toast.makeText(Comments.this,"Check your internet",Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {

                    Toast.makeText(Comments.this,"Check your internet",Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", reviewId);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);


    }
    public  void parseJson(String response)
    {
        list = new ArrayList<>();
        try {
            JSONArray inner = new JSONArray(response);

            HomeScreen.noOfComments=String.valueOf(inner.length());
            for (int i = 0; i < inner.length(); i++) {
                JSONObject innerobj = new JSONObject(inner.getString(i));

                Comment_Model comment_model = new Comment_Model();
                comment_model.setComment(innerobj.getString("comment_reply"));
                comment_model.setCommentBy(innerobj.getString("first_name")+" "+innerobj.getString("last_name"));
                comment_model.setCommentByProfile(innerobj.getString("profile_pic"));
                comment_model.setCommentById(innerobj.getString("user_id"));
                comment_model.setCommentId(innerobj.getString("id"));
                comment_model.setInappropriate(innerobj.getString("inappropriate"));

                list.add(comment_model);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
