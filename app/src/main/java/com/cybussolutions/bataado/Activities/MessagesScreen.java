package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.cybussolutions.bataado.Adapter.BrandAdapter;
import com.cybussolutions.bataado.Adapter.Home_Addapter;
import com.cybussolutions.bataado.Adapter.MessagesAdapter;
import com.cybussolutions.bataado.Model.Brands_Model;
import com.cybussolutions.bataado.Model.Messages_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.cybussolutions.bataado.Utils.DialogBox;
import com.facebook.share.widget.MessageDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessagesScreen extends AppCompatActivity {
    ProgressDialog ringProgressDialog;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    Toolbar toolbar;
    private ArrayList<Messages_Model> list;
    MessagesAdapter messagesAdapter;
    ListView listViewMessages;
    TextView noData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_screen);
        toolbar = findViewById(R.id.app_bar);
        listViewMessages=findViewById(R.id.listview_messages);
        noData=findViewById(R.id.noData);
        toolbar.setTitle("Inbox");
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

        getMessages();
    }
    public void getMessages() {
        ringProgressDialog = ProgressDialog.show(this, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_MESSAGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();

                        ringProgressDialog.dismiss();

                        parseJson(response);
                        messagesAdapter = new MessagesAdapter(MessagesScreen.this,list);

                        listViewMessages.setAdapter(messagesAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ringProgressDialog.dismiss();


                if (error instanceof NoConnectionError) {
                    new DialogBox(MessagesScreen.this, "No Internet Connection !", "Error",
                            "Error");
                } else if (error instanceof TimeoutError) {

                    new DialogBox(MessagesScreen.this, "Connection Time Out Error", "Error",
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
            if(jsonObject.getString("msg_brands_by_review").equals("")){
                noData.setVisibility(View.VISIBLE);
            }

            JSONArray inner = jsonObject.getJSONArray("msg_brands_by_review");
            //JSONArray inner1 = new JSONArray(jsonObject.getString("msg_brands_by_comment"));
           // JSONArray inner2 = new JSONArray(jsonObject.getString("total_brand_Reviews"));

            for (int i = 0; i < inner.length(); i++) {
                JSONObject innerobj = new JSONObject(inner.getString(i));

                Messages_Model messages_model = new Messages_Model();
                messages_model.setBrandid(innerobj.getString("chat_id"));
                messages_model.setBrandname(innerobj.getString("brand_name"));
                messages_model.setBrandimage(innerobj.getString("brand_logo"));
                messages_model.setBrandRating(innerobj.getString("rating"));
                messages_model.setMessageDate(innerobj.getString("chatCreatedAt"));
                messages_model.setMessageType(innerobj.getString("chat_flag"));
                messages_model.setChatMessage(innerobj.getString("chat_message"));
                messages_model.setChatKey(innerobj.getString("chat_key"));
                messages_model.setChatFrom(innerobj.getString("chat_from"));
                messages_model.setChatBrandId(innerobj.getString("chat_brand_id"));
                messages_model.setChatFlagId(innerobj.getString("chat_flag_id"));

               list.add(messages_model);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
