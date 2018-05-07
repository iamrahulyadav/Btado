package com.cybussolutions.bataado.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.cybussolutions.bataado.Adapter.Add_Friends_addapter;
import com.cybussolutions.bataado.Fragments.Drawer_Fragment;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.cybussolutions.bataado.Utils.FieldValidator;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.internal.FacebookDialogFragment;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.model.ShareMessengerGenericTemplateContent;
import com.facebook.share.model.ShareMessengerGenericTemplateElement;
import com.facebook.share.model.ShareMessengerURLActionButton;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.MessageDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Find_Friends extends AppCompatActivity{

    Drawer_Fragment drawerFragment = new Drawer_Fragment();
    Toolbar toolbar;
    String ids;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;
    ListView friends_list;
    ArrayList<Home_Model> friendsmodel = new ArrayList<>();
    Add_Friends_addapter friends_addapter =  null;

    ImageView search_footer,logo,home_footer;
    RelativeLayout sendEmailInvitation,inviteFbFriends;
//    CallbackManager callbackManager;
//    AppInviteDialog inviteDialog;
    CallbackManager facebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__friends);
        facebookCallbackManager = CallbackManager.Factory.create();
       /* setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);*/

        FacebookSdk.sdkInitialize(getApplicationContext());
       // callbackManager = CallbackManager.Factory.create();
        toolbar = findViewById(R.id.app_bar);
        sendEmailInvitation=findViewById(R.id.sendMail);
        inviteFbFriends=findViewById(R.id.inviteFbFriends);
        toolbar.setTitle("Find Friends");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);


        inviteFbFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // AppInviteDialog appInviteDialog = new AppInviteDialog(Find_Friends.this);
/*
                String appLinkUrl, previewImageUrl;

                appLinkUrl = "https://fb.me/493857950766025";
                previewImageUrl = "http://2.bp.blogspot.com/-99shOruuadw/VQsG2T233sI/AAAAAAAAEi0/noFTxUBh_rg/s1600/appscripts.png";
                GameRequestDialog gameRequestDialog=new GameRequestDialog(Find_Friends.this);

                GameRequestContent content = new GameRequestContent.Builder()
                        .setMessage("Install the Bataado App")
                        .build();
                gameRequestDialog.show(content);
                gameRequestDialog.registerCallback(facebookCallbackManager,
                        new FacebookCallback<GameRequestDialog.Result>() {
                            public void onSuccess(GameRequestDialog.Result result) {
                                String id = result.getRequestId();
                                Toast.makeText(Find_Friends.this,"Successfully send invitation to friends",Toast.LENGTH_LONG).show();
                            }
                            public void onCancel() {
                                Toast.makeText(Find_Friends.this,"cancel",Toast.LENGTH_LONG).show();
                            }
                            public void onError(FacebookException error) {
                                Toast.makeText(Find_Friends.this,error.toString(),Toast.LENGTH_LONG).show();
                            }
                        }
                );*/
                ShareMessengerURLActionButton actionButton =
                        new ShareMessengerURLActionButton.Builder()
                                .setTitle("Visit Bataado")
                                .setUrl(Uri.parse("http://demo.cybussolutions.com/bataado"))
                                .build();
                ShareMessengerGenericTemplateElement genericTemplateElement =
                        new ShareMessengerGenericTemplateElement.Builder()
                                .setTitle("Visit Bataado")
                                .setSubtitle("Visit Bataado")
                                .setImageUrl(Uri.parse("http://demo.cybussolutions.com/bataado/uploads/logo.png"))
                                .setButton(actionButton)
                                .build();
                ShareMessengerGenericTemplateContent genericTemplateContent =
                        new ShareMessengerGenericTemplateContent.Builder()
                                .setPageId("Your Page Id") // Your page ID, required
                                .setGenericTemplateElement(genericTemplateElement)
                                .build();
                try {
                    MessageDialog messageDialog = new MessageDialog(Find_Friends.this);
                    if (messageDialog.canShow(genericTemplateContent)) {
                        MessageDialog.show(Find_Friends.this, genericTemplateContent);
                    } else {
                        Toast.makeText(Find_Friends.this, "Please Install Facebook Messenger App", Toast.LENGTH_SHORT).show();
                    }
                }catch (android.content.ActivityNotFoundException ex){
                    Toast.makeText(Find_Friends.this, "Please Install Facebook Messenger App", Toast.LENGTH_SHORT).show();
                }
                //Intent shareIntent = new Intent();
                //shareIntent.setAction(Intent.ACTION_SEND);


               /* shareIntent
                        .putExtra(Intent.EXTRA_TEXT,
                                "Install the Bataado App");
                shareIntent.setType("text/plain");
                shareIntent.setPackage("com.facebook.orca");
                try {
                    startActivity(shareIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(Find_Friends.this, "Please Install Facebook Messenger App", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
        friends_list = findViewById(R.id.friends_list );
        home_footer = findViewById(R.id.home_fotter);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);

        ids = pref.getString("user_friends","");

        if(!(ids.equals(""))){
            ids = ids.substring(0, ids.length()-1);
        }

        sendEmailInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iviteUserthroughEmail();
            }
        });
        FindFriends();
        home_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Find_Friends.this,HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });



    }
    FacebookCallback callback =  new FacebookCallback<AppInviteDialog.Result>() {
        @Override
        public void onSuccess(AppInviteDialog.Result result) {
            Toast.makeText(Find_Friends.this,result.toString(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(Find_Friends.this,"cancel",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(FacebookException error) {
            Toast.makeText(Find_Friends.this,error.toString(),Toast.LENGTH_LONG).show();
        }
    };
    private void iviteUserthroughEmail() {
        LayoutInflater inflater = LayoutInflater.from(Find_Friends.this);
        View subView = inflater.inflate(R.layout.custom_dialog_for_add_bulk_members, null);
        final EditText member = subView.findViewById(R.id.etMember);
        final EditText etMessage = subView.findViewById(R.id.etMessage);


        Button save = subView.findViewById(R.id.copy);
        TextView cancel = subView.findViewById(R.id.close);


        final AlertDialog alertDialog = new AlertDialog.Builder(Find_Friends.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setView(subView);
        alertDialog.show();
        showKeyBoard(member);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String members = member.getText().toString();
                if (!member.getText().toString().equals("") ) {
                    if(FieldValidator.ValidateEmail(Find_Friends.this,members)) {
                        if (!etMessage.getText().toString().equals("")) {
                            if (members.contains(";")) {
                                Toast.makeText(getApplicationContext(), "Please use ',' to add multiple emails", Toast.LENGTH_LONG).show();
                            } else {
                                sendRequestThroughEmail(members, etMessage.getText().toString());
                                hideKeyBoard(member);
                                alertDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Add Message!", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "Please Add Correct Email(s)!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Email(s)", Toast.LENGTH_LONG).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyBoard(member);
                alertDialog.dismiss();
            }
        });
    }
    private void showKeyBoard(EditText title) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }
    private void hideKeyBoard(EditText title) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
    }
    private void sendRequestThroughEmail(final String members, final String message) {
        ringProgressDialog = ProgressDialog.show(this, "",	"Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();


        final StringRequest request = new StringRequest(Request.Method.POST, End_Points.SEND_EMAIL_INVITATION,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        ringProgressDialog.dismiss();
                        if(!response.equals("0") && !response.equals("-1") && !response.equals("-2") && !response.equals("-3")){
                            Toast.makeText(Find_Friends.this,"Friend Request Sent to "+response+" Users",Toast.LENGTH_LONG).show();
                        }else if(response.equals("-1")){
                            Toast.makeText(Find_Friends.this,"Friend request already sent.",Toast.LENGTH_LONG).show();
                        }else if(response.equals("-2")){
                            Toast.makeText(Find_Friends.this,"Friend request to own email cannot be sent.",Toast.LENGTH_LONG).show();
                        }else if(response.equals("-3")){
                            Toast.makeText(Find_Friends.this,"You are already friend with this user",Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                ringProgressDialog.dismiss();

                if (error instanceof NoConnectionError)
                {
                   /* new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    LoginManager.getInstance().logOut();
                                }
                            })
                            .show();*/
                }
                else if (error instanceof TimeoutError) {


                   /* new SweetAlertDialog(Find_Friends.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("Connection Time Out Error")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    LoginManager.getInstance().logOut();
                                }
                            })
                            .show();*/
                }
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {

                SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String id  = pref.getString("user_id","");
                String userFirstName=pref.getString("first_name","");
                String userLastName=pref.getString("last_name","");
                String userEmail=pref.getString("email","");


                Map<String,String> params = new HashMap<>();
                params.put("userid",id);
                params.put("from_firstName",userFirstName);
                params.put("from_lastName",userLastName);
                params.put("from_email",userEmail);
                params.put("to",members);
                params.put("msg",message);
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

    public void FindFriends()
    {

        ringProgressDialog = ProgressDialog.show(this, "",	"Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();


        final StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_FB_FRIENDS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {


                        ringProgressDialog.dismiss();

                        parseJson(response);

                        friends_addapter = new Add_Friends_addapter(Find_Friends.this,friendsmodel);

                        friends_list.setAdapter(friends_addapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                ringProgressDialog.dismiss();

                if (error instanceof NoConnectionError)
                {
                   /* new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    LoginManager.getInstance().logOut();
                                }
                            })
                            .show();*/
                }
                else if (error instanceof TimeoutError) {


                   /* new SweetAlertDialog(Find_Friends.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("Connection Time Out Error")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    LoginManager.getInstance().logOut();
                                }
                            })
                            .show();*/
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
                params.put("fb_id",ids);
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

            JSONObject object =  new JSONObject(response);

            String res = object.getString("user_info");

            JSONArray inner = new JSONArray(res);

            String user_status = object.getString("user_status");
            HashMap<String,String> status = new HashMap<>();

            if(user_status.equals("0"))
            {


                for (int i= 0 ;i<inner.length();i++)
                {
                    JSONObject innerobj = new JSONObject(inner.getString(i));

                    status.put(innerobj.getString("id"),"2");
                    // Toast.makeText(HomeScreen.this,home_model.getReviewid(), Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                JSONArray user_status_inner = new JSONArray(user_status);



                for (int i= 0 ;i<user_status_inner.length();i++)
                {
                    JSONObject innerobj = new JSONObject(user_status_inner.getString(i));

                    status.put(innerobj.getString("id"),innerobj.getString("status"));
                    // Toast.makeText(HomeScreen.this,home_model.getReviewid(), Toast.LENGTH_SHORT).show();
                }
            }


            for (int i= 0 ;i<=inner.length();i++)
            {
                JSONObject innerobj = new JSONObject(inner.getString(i));

                Home_Model home_model = new Home_Model();

                home_model.setUserid(innerobj.getString("id"));
                home_model.setEmail_brand(innerobj.getString("email"));
                home_model.setFirstname(innerobj.getString("first_name"));
                home_model.setLastname(innerobj.getString("last_name"));
                home_model.setProfilepic(innerobj.getString("profile_pic"));
                home_model.setBlock(innerobj.getString("address"));


                if(status.containsKey(innerobj.getString("id")))
                {
                    String  sta = status.get(innerobj.getString("id"));
                    home_model.setStatus(sta);
                }
                else {
                    home_model.setStatus("2");
                }

                friendsmodel.add(home_model);

                // Toast.makeText(HomeScreen.this,home_model.getReviewid(), Toast.LENGTH_SHORT).show();
            }








            // Toast.makeText(HomeScreen.this, outer, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        // This is the line I was lacking
        //callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
