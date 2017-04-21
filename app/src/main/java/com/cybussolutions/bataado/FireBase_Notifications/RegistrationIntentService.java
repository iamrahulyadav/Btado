package com.cybussolutions.bataado.FireBase_Notifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;


public class RegistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        try {

            SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
            String userid = pref.getString("user_id", null);

            // Make a call to Instance API
            String instanceID = FirebaseInstanceId.getInstance().getToken();
             String senderId = getResources().getString(R.string.gcm_defaultSenderId);

            Jsonsend(instanceID,userid);

            // request token that will be used by the server to send push notifications
//            String token = instanceID.getToken();

        } catch (Exception e) {
            e.getCause();

        }



    }


    public void Jsonsend(final String token , final String userid)
    {


        StringRequest request = new StringRequest(Request.Method.POST, End_Points.REFISTER_TOKEN,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                       //  Toast.makeText(getApplicationContext(),response, Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {

                Map<String,String> params = new HashMap<String, String>();
                params.put("token_id",token);
                params.put("user_id",userid);
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);



    }

}