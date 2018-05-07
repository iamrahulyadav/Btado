package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.cybussolutions.bataado.Adapter.Comments_Adapter;
import com.cybussolutions.bataado.Adapter.ConversationAdapter;
import com.cybussolutions.bataado.Adapter.MessagesAdapter;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Conversation_Model;
import com.cybussolutions.bataado.Model.Messages_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.cybussolutions.bataado.Utils.DialogBox;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Conversation extends AppCompatActivity {
    ProgressDialog ringProgressDialog;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    Toolbar toolbar;
    String chatKey,chatId,brandLogo,chatType,chatFrom,chat_brand_id,chat_flag_id;
    ArrayList<Conversation_Model> list;
    ConversationAdapter conversationAdapter;
    ListView listComments;
    CircleImageView profileImage;
    String userid;
    ImageView addComment;
    EditText etComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Intent intent=getIntent();
        chatKey=intent.getStringExtra("chatKey");
        chatId=intent.getStringExtra("chatId");
        brandLogo=intent.getStringExtra("brandLogo");
        chatType=intent.getStringExtra("chatType");
        chatFrom=intent.getStringExtra("chatFrom");
        chat_flag_id=intent.getStringExtra("chat_flag_id");
        chat_brand_id=intent.getStringExtra("chat_brand_id");
        toolbar = findViewById(R.id.app_bar);
        listComments = findViewById(R.id.listComments);
        profileImage=findViewById(R.id.profile_image);
        addComment=findViewById(R.id.send);
        etComment=findViewById(R.id.commenttext);
        toolbar.setTitle(chatType);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final String pp;
        SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
         userid = pref.getString("user_id", "");
        pp = pref.getString("profile_pic","");
        if(pp.equals(""))
        {
            Picasso.with(Conversation.this)
                    .load(End_Points.IMAGE_RREVIEW_URL+"no-image-icon-hi.png")
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(profileImage);
        }
        else {
            if (pp.startsWith("https://graph.facebook.com/") || pp.startsWith("https://fb-s-b-a.akamaihd.net/")|| pp.startsWith("https://scontent.xx.fbcdn.net/") || pp.startsWith("http://graph.facebook.com/")) {
                Picasso.with(Conversation.this)
                        .load(pp)
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(profileImage);
            } else {
                Picasso.with(Conversation.this)
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
                    sendMessage(comment);
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
                    sendMessage(comment);
                    etComment.setText("");
                    if (textView != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
        getConversation();
    }
    public void getConversation() {
        ringProgressDialog = ProgressDialog.show(this, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_CONVERSATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();

                        ringProgressDialog.dismiss();

                          parseJson(response);
                        conversationAdapter = new ConversationAdapter(Conversation.this,list,brandLogo,userid);

                        listComments.setAdapter(conversationAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ringProgressDialog.dismiss();


                if (error instanceof NoConnectionError) {
                    new DialogBox(Conversation.this, "No Internet Connection !", "Error",
                            "Error");
                } else if (error instanceof TimeoutError) {

                    new DialogBox(Conversation.this, "Connection Time Out Error", "Error",
                            "Error");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                Map<String, String> params = new HashMap<>();
                params.put("userId", userid);
                params.put("chat_key", chatKey);
                params.put("chat_flag", chatType);
                params.put("chatFrom", chatFrom);

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
        list = new ArrayList<>();
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONArray inner = jsonObject.getJSONArray("get_msgs");
            //JSONArray inner1 = new JSONArray(jsonObject.getString("msg_brands_by_comment"));
            // JSONArray inner2 = new JSONArray(jsonObject.getString("total_brand_Reviews"));
            for (int i = 0; i < inner.length(); i++) {
                JSONObject innerobj = new JSONObject(inner.getString(i));

                Conversation_Model conversation_model = new Conversation_Model();
                conversation_model.setChatId(innerobj.getString("chat_id"));
                conversation_model.setChatMessage(innerobj.getString("chat_message"));
                conversation_model.setChatFrom(innerobj.getString("chat_from"));

                list.add(conversation_model);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(final String comment){
        ringProgressDialog = ProgressDialog.show(this, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.SEND_MESSAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();

                        ringProgressDialog.dismiss();
                        getConversation();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ringProgressDialog.dismiss();


                if (error instanceof NoConnectionError) {
                    new DialogBox(Conversation.this, "No Internet Connection !", "Error",
                            "Error");
                } else if (error instanceof TimeoutError) {

                    new DialogBox(Conversation.this, "Connection Time Out Error", "Error",
                            "Error");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                Map<String, String> params = new HashMap<>();
                params.put("chat_from", userid);
                params.put("chat_key", chatKey);
                params.put("chat_flag", chatType);
                params.put("chat_to", chatFrom);
                params.put("chat_brand_id", chat_brand_id);
                params.put("chat_flag_id", chat_flag_id);
                params.put("chat_message", comment);

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
