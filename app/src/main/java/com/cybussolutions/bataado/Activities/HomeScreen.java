package com.cybussolutions.bataado.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RecoverySystem;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cybussolutions.bataado.Adapter.CustomDrawerAdapter;
import com.cybussolutions.bataado.Adapter.Home_Addapter;
import com.cybussolutions.bataado.FireBase_Notifications.RegistrationIntentService;
import com.cybussolutions.bataado.Fragments.Drawer_Fragment;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Brands_Model;
import com.cybussolutions.bataado.Model.DrawerPojo;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.CheckConnection;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.cybussolutions.bataado.Utils.callBack;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.mindorks.paracamera.Camera;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.cybussolutions.bataado.Utils.FilePath.getDataColumn;
import static com.cybussolutions.bataado.Utils.FilePath.isDownloadsDocument;
import static com.cybussolutions.bataado.Utils.FilePath.isExternalStorageDocument;
import static com.cybussolutions.bataado.Utils.FilePath.isMediaDocument;


public class HomeScreen extends AppCompatActivity implements callBack {
    Toolbar toolbar;
    // Create global camera reference in an activity or fragment
    Camera camera;
    Drawer_Fragment drawerFragment = new Drawer_Fragment();
    Home_Addapter home_addapter = null;
    ListView home_list;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    ProgressDialog ringProgressDialog;
    private ArrayList<Brands_Model> list = new ArrayList<>();
    private ArrayList<Home_Model> brand_list;// = new ArrayList<>();
    TextView brand_name,tvCongrats;
    ImageView brand_image, next, previous,crownIcon;
    ImageView search_footer,home_footer, logo,profile_footer;
    SearchView search;
    TextView brand_reivew;
    RatingBar brand_rating,ratingBar_home;
    String reviewTxt, brandid, userid;
    float reviewRating;
    Button Post;
    AutoCompleteTextView etPost,etBrandOrCategory;
    ListView itemList;



    private ArrayAdapter<String> listAdapter;
    ImageView profile_image,searchIcon,notifIcon;
    CircleImageView topReviewer;
    TextView topReviewerName,user_name_home,Page_home,time_home,review_home,noReviewOfDay;
    String reviewOfDayUserId,reviewOfDayUserName;
    String brandNAme, brandId, brandRating, brandAdress, brandPic,brandEmail, reviewCount,brandPhone,brandWebsiteUrl,brandLatitude,brandLongitude;
    String topReviewerUserId,topReviewerUserName;
    int count=0;
    private ListView mDrawerList;
    CustomDrawerAdapter adapterDrawer;
    List<DrawerPojo> dataList;
    DrawerLayout mDrawerLayout;
     int[] couunt = {0};
    public static CallbackManager callbackManager;
    Button addPicVideo;
    ImageView post_picture;
    String fileType,file;
    TextView removePicOrVideo;
    private static long fileSize = 0;
    private static int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    Bitmap bitmap;
    private String imagepath = null;
    String[] fileName;
    static ProgressDialog progressBar;
    public static int forCommentPos=-1;
    public static String noOfComments="0";
    ArrayList<String> ids;
    ArrayList<String> name;
    ArrayList<String> tableName;
    String searchCategoryId,searchTableName;
    SwipeRefreshLayout mySwipeRefreshLayout;
    static Activity activity;
    Calendar now;
    String currentMonth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);





// Build the camera
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(1)
                .setDirectory("pics")
                .setName("ali" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        FacebookSdk.sdkInitialize(HomeScreen.this);
        callbackManager = CallbackManager.Factory.create();
        activity=HomeScreen.this;
         now = Calendar.getInstance();
        getAllBrands();



        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        mDrawerList = findViewById(R.id.left_drawer);
        drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);

        home_list = findViewById(R.id.listview);

        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup header = (ViewGroup) inflater.inflate(R.layout.list_header, home_list,
                false);

        home_list.addHeaderView(header);
        mySwipeRefreshLayout = findViewById(R.id.swiperefresh);
        //   spinnerValues = new String[]{};
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getAllReview();
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
        brand_name = header.findViewById(R.id.brand_name);
        brand_image = header.findViewById(R.id.brand_image);
        searchIcon = findViewById(R.id.search_img);
        notifIcon = findViewById(R.id.noti_img);
        next = header.findViewById(R.id.next);
        logo = findViewById(R.id.logo_img);
        topReviewer=header.findViewById(R.id.imageView5);
        profile_image=header.findViewById(R.id.profile_image);
        topReviewerName=header.findViewById(R.id.textView5);
        user_name_home=header.findViewById(R.id.user_name_home);
        Page_home=header.findViewById(R.id.Page_home);
        time_home=header.findViewById(R.id.time_home);
        review_home=header.findViewById(R.id.adress);
        noReviewOfDay=header.findViewById(R.id.textView3);
        previous = header.findViewById(R.id.previous);
        crownIcon = header.findViewById(R.id.imageView6);
        tvCongrats = header.findViewById(R.id.textView6);
       // brand_reivew = header.findViewById(R.id.brand_review);
        search = findViewById(R.id.search);
        brand_rating = header.findViewById(R.id.brand_raiting);
        ratingBar_home = header.findViewById(R.id.ratingBar_home);
        search_footer = findViewById(R.id.search_fotter);
        home_footer = findViewById(R.id.home_fotter);
        profile_footer = findViewById(R.id.profile_fotter);
        Post = header.findViewById(R.id.post);
        itemList = findViewById(R.id.listview_search);
        String[] listViewAdapterContent = {"Current Location", "Search By Category/Brand"};
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listViewAdapterContent);
        mDrawerLayout=findViewById(R.id.drawerlayout);
        itemList.setAdapter(listAdapter);
        itemList.setVisibility(View.GONE);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        userid = pref.getString("user_id", "");
        topReviewerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!topReviewerUserId.equals("")) {
                    Intent intent = new Intent(HomeScreen.this, User_Profile.class);
                    intent.putExtra("username", topReviewerUserName);
                    intent.putExtra("userID", topReviewerUserId);
                    startActivity(intent);
                }
            }
        });
        user_name_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!reviewOfDayUserId.equals("")) {
                    Intent intent = new Intent(HomeScreen.this, User_Profile.class);
                    intent.putExtra("username", reviewOfDayUserName);
                    intent.putExtra("userID", reviewOfDayUserId);
                    startActivity(intent);
                }
            }
        });
        Page_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, MapsActivity.class);
                intent.putExtra("brandNAme", brandNAme);
                intent.putExtra("brandId", brandId);
                intent.putExtra("brandRating", brandRating);
                intent.putExtra("brandAdress",brandAdress);
                intent.putExtra("phone",brandPhone);
                intent.putExtra("websiteUrl",brandWebsiteUrl);
                intent.putExtra("latitude",brandLatitude);
                intent.putExtra("longitude",brandLongitude);
                intent.putExtra("email",brandEmail);
                intent.putExtra("brandPic", brandPic);
                //intent.putExtra("reviewCount",home_model.getNum_review());
                startActivity(intent);
            }
        });
        notifIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(dataList.size()>0) {
                    adapterDrawer.notifyDataSetChanged();
                    mDrawerLayout.openDrawer(mDrawerList);
               // }

            }
        });
        brand_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Brands_Model home_model=list.get(couunt[0]);
                Intent intent = new Intent(HomeScreen.this, MapsActivity.class);
                intent.putExtra("brandNAme", home_model.getBrandname());
                intent.putExtra("brandId", home_model.getBrandid());
                intent.putExtra("brandRating", home_model.getBrandRating());
                intent.putExtra("brandAdress",home_model.getArea()+" "+home_model.getBlock());
                intent.putExtra("phone",home_model.getPhone());
                intent.putExtra("websiteUrl",home_model.getWebsite_url());
                intent.putExtra("latitude",home_model.getBrand_latitude());
                intent.putExtra("longitude",home_model.getBrand_longitude());
                intent.putExtra("email",home_model.getEmail());
                intent.putExtra("brandPic", home_model.getBrand_logo());
                //intent.putExtra("reviewCount",home_model.getNum_review());
                startActivity(intent);
            }
        });
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,mDrawerList);
        search_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, SearchScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setVisibility(View.VISIBLE);
                itemList.setVisibility(View.VISIBLE);
                search.setIconified(false);
            }
        });
        home_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(brand_list.size()>0){
                    home_list.setSelection(0);
                }
            }
        });
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(brand_list.size()>0){
                    home_list.setSelection(0);
                }
            }
        });
        profile_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pp = pref.getString("profile_pic","");
                String strname = pref.getString("user_name","");
                String strid = pref.getString("user_id","");
                Intent intent = new Intent(HomeScreen.this, User_Profile.class);
                intent.putExtra("username", strname);
                intent.putExtra("userID", strid);
                startActivity(intent);
            }
        });


        search.setQueryHint("Search");
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                itemList.setVisibility(View.GONE);
                search.setVisibility(View.GONE);
                return false;
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setIconified(false);
                itemList.setVisibility(View.VISIBLE);
            }
        });
        search.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemList.setVisibility(View.VISIBLE);
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // Toast.makeText(getBaseContext(), newText, Toast.LENGTH_LONG).show();
                return false;
            }
        });
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1) {
                    itemList.setVisibility(View.GONE);
                    search.setIconified(true);
                    search.setVisibility(View.GONE);
                    ShowDialog();
                }
                if (position == 0) {
                    itemList.setVisibility(View.GONE);
                    search.setIconified(true);
                    search.setVisibility(View.GONE);
                    Intent i = new Intent(HomeScreen.this, MyLocationUsingLocationAPI.class);
                    startActivity(i);
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                brand_rating.setRating(0);

                if (couunt[0] < list.size() - 1) {
                    int count = couunt[0] + 1;
                    couunt[0]++;
                    brand_name.setText(list.get(count).getBrandname());


                    Picasso.with(HomeScreen.this)
                            .load(End_Points.IMAGE_BASE_URL + list.get(count).getBrandimage())
                            .resize(150, 150)
                            .centerCrop().transform(new CircleTransform())
                            .placeholder(R.drawable.profile_fotter)
                            .into(brand_image);
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                brand_rating.setRating(0);

                if (couunt[0] > 0) {
                    int count = couunt[0] - 1;
                    couunt[0]--;

                    brand_name.setText(list.get(count).getBrandname());

                    Picasso.with(HomeScreen.this)
                            .load(End_Points.IMAGE_BASE_URL + list.get(count).getBrandimage())
                            .resize(150, 150)
                            .centerCrop().transform(new CircleTransform())
                            .placeholder(R.drawable.profile_fotter)
                            .into(brand_image);
                }
            }
        });
      /*  brand_reivew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDialog(couunt[0]);
            }
        });*/


        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDialog(couunt[0]);

            }
        });
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!dataList.get(position).getNotificationId().equals("") && !dataList.get(position).getComment().equals("No notification found")&& !dataList.get(position).getComment().equals("No Internet Connection")&& !dataList.get(position).getComment().equals("You have new Friend Request.")&& !dataList.get(position).getComment().equals("Accepted your Friend Request.") && !dataList.get(position).getComment().startsWith("You have recieved a new message from")) {
                    Intent intent = new Intent(HomeScreen.this, Detail_Review.class);
                    intent.putExtra("review_id", dataList.get(position).getReviewId());
                    intent.putExtra("noti_id", dataList.get(position).getNotificationId());
                    intent.putExtra("table_name", dataList.get(position).getTableName());
                    intent.putExtra("flag", dataList.get(position).getNotificationFlag());
                    if (!dataList.get(position).getNotificationFlag().equals("1")) {
                        count--;
                        dataList.get(position).setNotificationFlag("1");
                        notifIcon.setImageDrawable(buildCounterDrawable(count, R.drawable.notif_icon));
                        adapterDrawer.notifyDataSetChanged();
                    }
                    startActivity(intent);
                }else if(dataList.get(position).getComment().equals("You have new Friend Request.") && !dataList.get(position).getNotificationId().equals(""))
                {
                    Intent intent = new Intent(HomeScreen.this, Friend_Request.class);
                    intent.putExtra("noti_id", dataList.get(position).getNotificationId());
                    intent.putExtra("flag", dataList.get(position).getNotificationFlag());
                    if (!dataList.get(position).getNotificationFlag().equals("1")) {
                        count--;
                        dataList.get(position).setNotificationFlag("1");
                        dataList.remove(position);
                        notifIcon.setImageDrawable(buildCounterDrawable(count, R.drawable.notif_icon));
                        adapterDrawer.notifyDataSetChanged();
                    }
                    startActivity(intent);
                }else if(dataList.get(position).getComment().equals("Accepted your Friend Request.") && !dataList.get(position).getNotificationId().equals("")){
                    Intent intent = new Intent(HomeScreen.this, User_Profile.class);
                    intent.putExtra("username", dataList.get(position).getUser_name());
                    intent.putExtra("userID", dataList.get(position).getReviewId());
                    intent.putExtra("noti_id", dataList.get(position).getNotificationId());
                    intent.putExtra("flag", dataList.get(position).getNotificationFlag());
                    if (!dataList.get(position).getNotificationFlag().equals("1")) {
                        count--;
                        dataList.get(position).setNotificationFlag("1");
                        dataList.remove(position);
                        notifIcon.setImageDrawable(buildCounterDrawable(count, R.drawable.notif_icon));
                        adapterDrawer.notifyDataSetChanged();
                    }
                    startActivity(intent);
                }else if(dataList.get(position).getComment().startsWith("You have recieved a new message from")){
                    Intent intent = new Intent(HomeScreen.this, Conversation.class);
                    intent.putExtra("chatKey", dataList.get(position).getChatKey());
                    intent.putExtra("chatId", dataList.get(position).getChatId());
                    intent.putExtra("brandLogo", dataList.get(position).getBrandImage());
                    intent.putExtra("chatType", dataList.get(position).getChatType());
                    intent.putExtra("chat_brand_id", dataList.get(position).getChatBrandId());
                    intent.putExtra("chat_flag_id", dataList.get(position).getChatFlagId());
                    intent.putExtra("chatFrom", dataList.get(position).getChatFrom());
                   // intent.putExtra("flag", dataList.get(position).getNotificationFlag());
                    if (!dataList.get(position).getNotificationFlag().equals("1")) {
                        count--;
                        dataList.get(position).setNotificationFlag("1");
                        dataList.remove(position);
                        notifIcon.setImageDrawable(buildCounterDrawable(count, R.drawable.notif_icon));
                        adapterDrawer.notifyDataSetChanged();
                    }
                    startActivity(intent);
                }

            }
        });


        Intent intent = new Intent(HomeScreen.this, RegistrationIntentService.class);
        startService(intent);


    }


    private void ShowDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(HomeScreen.this);
        View customView = layoutInflater.inflate(R.layout.custom_dialog_enter_location, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(HomeScreen.this).create();
        alertDialog.setCancelable(false);
        etPost = customView.findViewById(R.id.et_post);
        etBrandOrCategory = customView.findViewById(R.id.et_brand);
        TextView cancel = customView.findViewById(R.id.btn_cancel);
        Button postReview = customView.findViewById(R.id.post_btn);
        showKeyBoard(etPost);
        etPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchCity(etPost.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etBrandOrCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(etBrandOrCategory.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        postReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((etPost.getText().toString().equals("") && etBrandOrCategory.getText().toString().equals("")) || (etPost.getText().toString().trim().length()<1 && etBrandOrCategory.getText().toString().trim().length()<1)) {
                    return;
                }
                if(searchCategoryId==null || searchTableName==null){
                    searchCategoryId="";
                    searchTableName="";
                }
                    Intent intent = new Intent(HomeScreen.this, Brands.class);
                    intent.putExtra("city", etPost.getText().toString());
                    intent.putExtra("category", searchCategoryId);
                    intent.putExtra("tableName", searchTableName);
                    startActivity(intent);
                    hideKeyBoard(etPost);
                    alertDialog.dismiss();
                //} else {
                //    Toast.makeText(HomeScreen.this, "Please add some location to search", Toast.LENGTH_LONG).show();
                //}
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyBoard(etPost);
                alertDialog.dismiss();
            }
        });


        alertDialog.setView(customView);
        alertDialog.show();
    }

    private void searchCity(final String s) {
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.SEARCH_CITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ids = new ArrayList<>();
                        name = new ArrayList<>();
                        tableName = new ArrayList<>();
                        try {
                            JSONArray inner = new JSONArray(response);


                            for (int i= 0 ;i<inner.length();i++)
                            {
                                JSONObject innerobj = new JSONObject(inner.getString(i));
                                ids.add(innerobj.getString("id"));
                                name.add(innerobj.getString("city"));
                            }

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<String>(HomeScreen.this, android.R.layout.simple_list_item_1, name);
                            etPost.setAdapter(adapter);
                            //  to.setOnItemSelectedListener(new CustomOnItemSelectedListener_position());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error instanceof NoConnectionError) {

                  /*  new SweetAlertDialog(SendNewMessageActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("check your internet connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            })
                            .show();*/
                } else if (error instanceof TimeoutError) {

                   /* new SweetAlertDialog(SendNewMessageActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("Connection TimeOut! Please check your internet connection.")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            })
                            .show();*/
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("city", s);

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

    private void showKeyBoard(EditText title) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideKeyBoard(EditText title) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
    }

   /* private void postDialog(final int pos) {
        LayoutInflater layoutInflater = LayoutInflater.from(HomeScreen.this);
        View customView = layoutInflater.inflate(R.layout.custom_dialog_post_review, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(HomeScreen.this).create();
        alertDialog.setCancelable(false);
        etPost = customView.findViewById(R.id.et_post);
        showKeyBoard(etPost);
        TextView cancel = customView.findViewById(R.id.btn_cancel);
        Button postReview = customView.findViewById(R.id.post_btn);
        postReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etPost.getText().toString().equals("")) {
                    hideKeyBoard(etPost);
                    //reviewTxt = brand_reivew.getText().toString();
                    reviewRating = brand_rating.getRating();
                    brandid = list.get(pos).getBrandid();

                    addUserReview(etPost.getText().toString());

//                    brand_reivew.setText("");
                    brand_rating.setRating(0);

                    home_list.invalidate();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(HomeScreen.this, "Please add some review", Toast.LENGTH_LONG).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyBoard(etPost);
                alertDialog.dismiss();
            }
        });


        alertDialog.setView(customView);
        alertDialog.show();
    }*/
   private void searchUser(final String category) {


       StringRequest request = new StringRequest(Request.Method.POST, End_Points.SEARCH_CATEGORY,
               new Response.Listener<String>() {
                   @Override
                   public void onResponse(String response) {

                       ids = new ArrayList<>();
                       name = new ArrayList<>();
                       tableName = new ArrayList<>();
                       try {
                           JSONArray inner = new JSONArray(response);


                           for (int i= 0 ;i<inner.length();i++)
                           {
                               JSONObject innerobj = new JSONObject(inner.getString(i));
                                ids.add(innerobj.getString("id"));
                                name.add(innerobj.getString("value"));
                                tableName.add(innerobj.getString("table_name"));
                           }

                           ArrayAdapter<String> adapter =
                                   new ArrayAdapter<String>(HomeScreen.this, android.R.layout.simple_list_item_1, name);
                           etBrandOrCategory.setAdapter(adapter);
                           //  to.setOnItemSelectedListener(new CustomOnItemSelectedListener_position());

                           etBrandOrCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        searchCategoryId=ids.get(i);
                                        searchTableName=tableName.get(i);
                                }
                            });

                       } catch (JSONException e) {
                           e.printStackTrace();
                       }


                   }
               }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {


               if (error instanceof NoConnectionError) {

                  /*  new SweetAlertDialog(SendNewMessageActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("check your internet connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            })
                            .show();*/
               } else if (error instanceof TimeoutError) {

                   /* new SweetAlertDialog(SendNewMessageActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("Connection TimeOut! Please check your internet connection.")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            })
                            .show();*/
               }
           }
       }) {
           @Override
           protected Map<String, String> getParams() {

               Map<String, String> params = new HashMap<>();
               params.put("category", category);

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
   private void postDialog(final int pos) {
       fileType="";
       file="";
       reviewRating = brand_rating.getRating();
       brandid = list.get(pos).getBrandid();
       LayoutInflater layoutInflater = LayoutInflater.from(HomeScreen.this);
       final View customView = layoutInflater.inflate(R.layout.post_picture, null);
       final AlertDialog alertDialog = new AlertDialog.Builder(HomeScreen.this).create();
       alertDialog.setCancelable(false);
       final TextView heading = customView.findViewById(R.id.brand_name);
       removePicOrVideo = customView.findViewById(R.id.remove);
       post_picture = customView.findViewById(R.id.profilePicture);
       heading.setText(list.get(pos).getBrandname());
       final EditText etPost = customView.findViewById(R.id.et_post);
       final RatingBar brand_rating = customView.findViewById(R.id.brand_raiting);
       brand_rating.setRating(reviewRating);
       ImageView cancel = customView.findViewById(R.id.cancel_button);
       Button postReview = customView.findViewById(R.id.postStatusUpdateButton);
       addPicVideo = customView.findViewById(R.id.uploadVedioPic);
       removePicOrVideo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               addPicVideo.setVisibility(View.VISIBLE);
               removePicOrVideo.setVisibility(View.GONE);
               post_picture.setVisibility(View.GONE);
               fileType="";
               file="";
           }
       });
       addPicVideo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               CharSequence options[] = new CharSequence[]{"Camera","Storage"};
               AlertDialog.Builder builder= new AlertDialog.Builder(activity);
               builder.setItems(options, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

                       if (i==0){


                           if(ActivityCompat.checkSelfPermission(HomeScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeScreen.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                               if (Build.VERSION.SDK_INT > 22) {

                                   requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},4);

                                   alertDialog.dismiss();
                               }

                           }else {
                               // Call the camera takePicture method to open the existing camera
                               try {
                                   camera.takePicture();
                               }catch (Exception e){
                                   e.printStackTrace();
                               }
                           }


                       }
                       if (i==1){


                           if (ActivityCompat.checkSelfPermission(HomeScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                               if (Build.VERSION.SDK_INT > 22) {

                                   requestPermissions(new String[]{Manifest.permission
                                                   .WRITE_EXTERNAL_STORAGE},
                                           10);

                               }

                           } else {
                               Intent intent = new Intent();
                               intent.setType("*/*");
                               intent.setAction(Intent.ACTION_GET_CONTENT);
                               //   intent.setSelector(Intent.getIntent().removeCategory(););

                               startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
                           }
                       }
                   }
               }).show();






           }
       });



       postReview.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (CheckConnection.checkStatus(HomeScreen.this)) {
                   if(brand_rating.getRating()==0){
                       Toast.makeText(HomeScreen.this, "Please rate brand", Toast.LENGTH_LONG).show();
                       return;
                   }
               if(/*file.equals("") && */etPost.getText().toString().equals("")){
                   Toast.makeText(HomeScreen.this,"Please add some review",Toast.LENGTH_LONG).show();
                   return;
               }

               String reviewTxt = etPost.getText().toString();
               float reviewRating = brand_rating.getRating();
               saveFile(file, file, fileType, reviewTxt, reviewRating);
               alertDialog.dismiss();
               }else {
                   Toast.makeText(HomeScreen.this,"No Network Found",Toast.LENGTH_LONG).show();
//                   new DialogBox(HomeScreen.this, "No Network Found", "Error", "Error");
               }
           }
       });
       cancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               alertDialog.dismiss();
           }
       });

       alertDialog.setView(customView);
       alertDialog.show();
   }
    @Override
    public void processFinish(String output) {
       /* try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        // close the progress bar dialog
        progressBar.dismiss();
        // }
        if (output.equals("200")) {
            //ringProgressDialog.dismiss();

            // Toast.makeText(mcontext, "uploaded Succesfully"+ fileName, Toast.LENGTH_SHORT).show();
            if (file.contains(".jpg") || file.contains(".jpeg") || file.contains(".png") || file.contains(".gif")) {
                fileType = "picture";
                //postDialog(file, file, fileType);
                // saveFile(file,file,fileType);
                post_picture.setVisibility(View.VISIBLE);
                removePicOrVideo.setVisibility(View.VISIBLE);
                addPicVideo.setVisibility(View.GONE);
                Picasso.with(HomeScreen.this).load(End_Points.IMAGE_RREVIEW_URL + file).resize(400, 400).centerCrop().into(post_picture);

            } else if (file.contains(".mp4")) {
                fileType = "video";
                post_picture.setVisibility(View.VISIBLE);
                removePicOrVideo.setVisibility(View.VISIBLE);
                addPicVideo.setVisibility(View.GONE);
                Picasso.with(HomeScreen.this).load(End_Points.IMAGE_RREVIEW_URL + file).placeholder(R.drawable.vedios_placeholder).resize(400, 400).centerCrop().into(post_picture);

                //postDialog(file, file, fileType);
                // saveFile(file,file,fileType);
            } else {
                //  ringProgressDialog.dismiss();
                Toast.makeText(HomeScreen.this, "File format not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            ringProgressDialog.dismiss();
            Toast.makeText(HomeScreen.this, "upload error", Toast.LENGTH_SHORT).show();

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == 4 ||
                requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                    if (requestCode==4){

                        try {
                            camera.takePicture();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                }

                if (requestCode == 10) {

                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    //   intent.setSelector(Intent.getIntent().removeCategory(););

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
                }
            } else {
                Toast.makeText(HomeScreen.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            //Bitmap photo = (Bitmap) data.getData().getPath();

            Uri selectedImageUri = data.getData();
            bitmap = BitmapFactory.decodeFile(imagepath);

            String path = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                path = getPath(HomeScreen.this, selectedImageUri);
            }

            if (path != null && !path.equals("")) {
                fileName = path.split("/");
                file = fileName[fileName.length - 1];
                File file = new File(path);

//                long length = file.length();
//                long maxLimit=10000;
                double bytes = file.length();
                double kilobytes = (bytes / 1024);
                double megabytes = (kilobytes / 1024);
                if(megabytes>10){
                    Toast.makeText(this, "File Size Must be less than 10MB", Toast.LENGTH_LONG).show();
                }else {
                    //  if(length<=maxLimit) {
                    UploadFile uploadFile = new UploadFile();
                    uploadFile.delegate = HomeScreen.this;
                    uploadFile.execute(path);

                  /*  new Thread(new Runnable() {
                        public void run() {
                            while (progressBarStatus < 99) {
                                // performing operation
                                progressBarStatus = doOperation();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                // Updating the progress bar
                                progressBarHandler.post(new Runnable() {
                                    public void run() {
                                        progressBar.setProgress(progressBarStatus);
                                    }
                                });
                            }
                            // performing operation if file is downloaded,
                        *//*if (progressBarStatus >= 100) {
                            // sleeping for 1 second after operation completed
                            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
                            // close the progress bar dialog
                            progressBar.dismiss();
                        }*//*
                        }
                    }).start();*/
                }
                    /*ringProgressDialog = ProgressDialog.show(Detail_brand.this, "Please wait ...", "Uploading File ...", true);
                    ringProgressDialog.setCancelable(false);
                    ringProgressDialog.show();*/
//                }else {
//                    Toast.makeText(this, "File Size Must be less than 10MB", Toast.LENGTH_LONG).show();
//                }
            } else {
                Toast.makeText(this, "file not found", Toast.LENGTH_LONG).show();
            }


        }
        if(requestCode == Camera.REQUEST_TAKE_PHOTO){
            Bitmap bitmap = camera.getCameraBitmap();
            if(bitmap != null) {
                Uri tempUri = getImageUri(getApplicationContext(), bitmap);
               // bitmap = BitmapFactory.decodeFile(imagepath);

                String path = "";
                path=getPathFromURI(tempUri);
                /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    path = getPath(HomeScreen.this, tempUri);
                }*/
                if (path != null && !path.equals("")) {
                    fileName = path.split("/");
                    file = fileName[fileName.length - 1];
                    File file = new File(path);
                }
              //  String picturePath=getPathFromURI(tempUri);
                UploadFile uploadFile = new UploadFile();
                uploadFile.delegate = HomeScreen.this;
                uploadFile.execute(path);
            }else{
                Toast.makeText(this.getApplicationContext(),"Picture not taken!",Toast.LENGTH_SHORT).show();
            }
        }



    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        //    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //  inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    static class  UploadFile extends AsyncTask<String,Integer,String> implements RecoverySystem.ProgressListener
    {
        public callBack delegate = null;
        private String upLoadServerUri = null;
        private String imagepath=null;
        private int serverResponseCode = 0;

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(activity);
            progressBar.setCancelable(false);
            progressBar.setMessage("Uploading ...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setProgress(0);
            progressBar.setSecondaryProgress(0);
            progressBar.setMax(100);
            progressBar.show();
            progressBarStatus = 0;
            fileSize = 0;

            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            progressBar.setSecondaryProgress(values[0]);
            //  super.onProgressUpdate(values);
        }
        @Override
        protected String doInBackground(String... strings) {
            String fileName = strings[0];

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(fileName);
            long filesize=sourceFile.length();
            filesize = filesize / 1024;

            if (!sourceFile.isFile()) {

                //  dialog.dismiss();

                Log.e("uploadFile", "Source File not exist :" + imagepath);


                return "0";

            } else {
                try {

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(End_Points.UPLOAD_FILE_URL+"upload_file_app.php");

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                            + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    long total = 0;
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bytesRead);
                        total += bytesRead;
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        long dataUploaded = ((total / 1024) * 100 ) /filesize ;
                        publishProgress((int) dataUploaded);
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);





                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (MalformedURLException ex) {

                    // dialog.dismiss();
                    ex.printStackTrace();


                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {

                    //  dialog.dismiss();
                    e.printStackTrace();

                    //Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);
                }
                //   dialog.dismiss();
                return serverResponseCode+"";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            delegate.processFinish(s);
        }

        @Override
        public void onProgress(int progress) {
            progressBar.setProgress(progress);
        }
    }
    public int doOperation() {
        //The range of ProgressDialog starts from 0 to 10000
        while (fileSize <= 10000) {
            fileSize++;
            if (fileSize == 1) {
                return 10;
            } else if (fileSize == 2) {
                return 20;
            } else if (fileSize == 3) {
                return 30;
            } else if (fileSize == 4) {
                return 40;//you can add more else if
            } else if (fileSize == 5) {
                return 50;//you can add more else if
            } else if (fileSize == 6) {
                return 60;//you can add more else if
            } else if (fileSize == 7) {
                return 70;//you can add more else if
            } else if (fileSize == 8) {
                return 80;//you can add more else if
            } else if (fileSize == 9) {
                return 90;//you can add more else if
            } else {
                return 99;
            }
        }//end of while
        return 100;
    }//end of doOperation

    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }
    @Override
    public void onBackPressed() {
        if (!search.isIconified()) {
            itemList.setVisibility(View.GONE);
            search.setIconified(true);
        } else {
            super.onBackPressed();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    public void getAllBrands() {
        ringProgressDialog = ProgressDialog.show(HomeScreen.this, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_ALL_BRANDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ringProgressDialog.dismiss();
                        JSONArray inner;
                        try {
                            inner = new JSONArray(response);

                            for (int i = 0; i < inner.length(); i++) {
                                JSONObject innerobj = new JSONObject(inner.getString(i));

                                Brands_Model brands_model = new Brands_Model();
                                brands_model.setBrandid(innerobj.getString("id"));
                                brands_model.setBrandname(innerobj.getString("brand_name"));
                                brands_model.setBrandimage(innerobj.getString("brand_logo"));
                                brands_model.setPhone(innerobj.getString("phone"));
                                brands_model.setWebsite_url(innerobj.getString("website_url"));
                                brands_model.setBlock(innerobj.getString("block"));
                                brands_model.setArea(innerobj.getString("area_town"));
                                brands_model.setBrand_latitude(innerobj.getString("latitude"));
                                brands_model.setBrand_longitude(innerobj.getString("longitude"));
                                brands_model.setBrand_logo(innerobj.getString("brand_logo"));
                                brands_model.setEmail(innerobj.getString("email"));
                                brands_model.setBrandRating(innerobj.getString("rating"));
                                list.add(brands_model);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        brand_name.setText(list.get(0).getBrandname());
                        Picasso.with(HomeScreen.this)
                                .load(End_Points.IMAGE_BASE_URL + list.get(count).getBrandimage())
                                .resize(150, 150)
                                .centerCrop().transform(new CircleTransform())
                                .placeholder(R.drawable.profile_fotter)
                                .into(brand_image);
                        getAllReview();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialog.dismiss();
                if (error instanceof NoConnectionError) {
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
                } else if (error instanceof TimeoutError) {
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
        }) {
            @Override
            protected Map<String, String> getParams() {
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


    public void getAllReview() {
        ringProgressDialog = ProgressDialog.show(HomeScreen.this, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_ALL_REVIEWS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ringProgressDialog.dismiss();

                        parseJson(response);

                        home_addapter = new Home_Addapter(HomeScreen.this, brand_list,"");

                        home_list.setAdapter(home_addapter);
                        getNotifications();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialog.dismiss();
                getNotifications();
                if (error instanceof NoConnectionError) {

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
                } else if (error instanceof TimeoutError) {

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
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                // params.put("revID",reviewId);
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

    private void getNotifications() {
        /*ringProgressDialog = ProgressDialog.show(this, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();*/

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_NOTIFICATIONS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        count=0;

                        ringProgressDialog.dismiss();
                        try {
                            dataList = new ArrayList<>();
                            JSONObject object = new JSONObject(response);

                            String res = object.getString("comments");
                            String likeRes = object.getString("likes");
                            String unlikeRes = object.getString("dislikes");
                            String usefulRes = object.getString("useful");
                            String shares = object.getString("shares");
                            String friendRequests = object.getString("friendRequest");
                            String friendRequestAccepted = object.getString("friendRequestAccepted");
                            String messages = object.getString("messages");


                            if(res.equals("false") && likeRes.equals("false") && unlikeRes.equals("false")&& usefulRes.equals("false") && friendRequests.equals("false") && friendRequestAccepted.equals("false") && shares.equals("false") && messages.equals("false")){
                                DrawerPojo drawerPojo=new DrawerPojo();
                                drawerPojo.setUser_image("");
                                drawerPojo.setUser_name("");
                                drawerPojo.setComment("No notification found");
                                drawerPojo.setReviewId("");
                                drawerPojo.setDate("");
                                drawerPojo.setNotificationId("");
                                drawerPojo.setTableName("");
                                drawerPojo.setBrandImage("");
                                drawerPojo.setChatId("");
                                drawerPojo.setChatKey("");
                                drawerPojo.setChatType("");
                                drawerPojo.setChatBrandId("");
                                drawerPojo.setChatFlagId("");
                                drawerPojo.setChatFrom("");
                                drawerPojo.setNotificationFlag("");
                                dataList.add(drawerPojo);
                            }
                            if(!messages.equals("false")){
                                JSONArray inner6 = new JSONArray(messages);
                                for(int i=0;i<inner6.length();i++){
                                    DrawerPojo drawerPojo=new DrawerPojo();
                                    JSONObject innerobj = inner6.getJSONObject(i);
                                    drawerPojo.setUser_image("icon");
                                    drawerPojo.setUser_name("");
                                    drawerPojo.setComment("You have recieved a new message from "+ innerobj.getString("brand_name"));
                                    drawerPojo.setReviewId(innerobj.getString("uid"));
                                    drawerPojo.setDate(innerobj.getString("creation_date"));
                                    drawerPojo.setNotificationId(innerobj.getString("noti_id"));
                                    drawerPojo.setTableName("");
                                    drawerPojo.setBrandImage(innerobj.getString("brand_logo"));
                                    drawerPojo.setChatId(innerobj.getString("chat_id"));
                                    drawerPojo.setChatKey(innerobj.getString("chat_key"));
                                    drawerPojo.setChatType(innerobj.getString("chat_flag"));
                                    drawerPojo.setChatBrandId(innerobj.getString("chat_brand_id"));
                                    drawerPojo.setChatFlagId(innerobj.getString("chat_flag_id"));
                                    drawerPojo.setChatFrom(innerobj.getString("chat_from"));
                                    drawerPojo.setNotificationFlag(innerobj.getString("notification_flag"));
                                    if(innerobj.getString("notification_flag").equals("0")){
                                        count++;
                                    }
                                    dataList.add(drawerPojo);
                                }
                            }
                            if(!friendRequestAccepted.equals("false")){
                                JSONArray inner5 = new JSONArray(friendRequestAccepted);
                                for(int i=0;i<inner5.length();i++){
                                    DrawerPojo drawerPojo=new DrawerPojo();
                                    JSONObject innerobj = inner5.getJSONObject(i);
                                    drawerPojo.setUser_image("");
                                    drawerPojo.setUser_name(innerobj.getString("first_name")+" "+innerobj.getString("last_name"));
                                    drawerPojo.setComment("Accepted your Friend Request.");
                                    drawerPojo.setReviewId(innerobj.getString("uid"));
                                    drawerPojo.setDate(innerobj.getString("creation_date"));
                                    drawerPojo.setNotificationId(innerobj.getString("noti_id"));
                                    drawerPojo.setBrandImage("");
                                    drawerPojo.setChatId("");
                                    drawerPojo.setChatKey("");
                                    drawerPojo.setChatType("");
                                    drawerPojo.setTableName("");
                                    drawerPojo.setChatBrandId("");
                                    drawerPojo.setChatFlagId("");
                                    drawerPojo.setChatFrom("");
                                    drawerPojo.setNotificationFlag(innerobj.getString("notification_flag"));
                                    if(innerobj.getString("notification_flag").equals("0")){
                                        count++;
                                    }
                                    dataList.add(drawerPojo);
                                }
                            }
                            if(!friendRequests.equals("false")){
                                JSONArray inner4 = new JSONArray(friendRequests);
                                for(int i=0;i<inner4.length();i++){
                                    DrawerPojo drawerPojo=new DrawerPojo();
                                    JSONObject innerobj = inner4.getJSONObject(i);
                                    drawerPojo.setUser_image("");
                                    drawerPojo.setUser_name("");
                                    drawerPojo.setComment("You have new Friend Request.");
                                    drawerPojo.setReviewId("");
                                    drawerPojo.setDate(innerobj.getString("creation_date"));
                                    drawerPojo.setNotificationId(innerobj.getString("noti_id"));
                                    drawerPojo.setBrandImage("");
                                    drawerPojo.setChatId("");
                                    drawerPojo.setChatKey("");
                                    drawerPojo.setChatType("");
                                    drawerPojo.setTableName("");
                                    drawerPojo.setChatBrandId("");
                                    drawerPojo.setChatFlagId("");
                                    drawerPojo.setChatFrom("");
                                    drawerPojo.setNotificationFlag(innerobj.getString("notification_flag"));
                                    if(innerobj.getString("notification_flag").equals("0")){
                                        count++;
                                    }
                                    dataList.add(drawerPojo);
                                }
                            }
                            if(!shares.equals("false")) {

                                JSONArray inner6 = new JSONArray(shares);
                                for(int i=0;i<inner6.length();i++){
                                    DrawerPojo drawerPojo=new DrawerPojo();
                                    JSONObject innerobj = inner6.getJSONObject(i);
                                    drawerPojo.setUser_image(innerobj.getString("profile_pic"));
                                    drawerPojo.setUser_name(innerobj.getString("first_name")+" "+innerobj.getString("last_name"));
                                    drawerPojo.setComment("Shared Your Review.");
                                    drawerPojo.setReviewId(innerobj.getString("reviews_id"));
                                    drawerPojo.setDate(innerobj.getString("creation_date"));
                                    drawerPojo.setNotificationId(innerobj.getString("noti_id"));
                                    drawerPojo.setTableName("reviews_share");
                                    drawerPojo.setBrandImage("");
                                    drawerPojo.setChatId("");
                                    drawerPojo.setChatKey("");
                                    drawerPojo.setChatType("");
                                    drawerPojo.setChatBrandId("");
                                    drawerPojo.setChatFlagId("");
                                    drawerPojo.setChatFrom("");
                                    drawerPojo.setNotificationFlag(innerobj.getString("notification_flag"));
                                    if(innerobj.getString("notification_flag").equals("0")){
                                        count++;
                                    }
                                    dataList.add(drawerPojo);
                                }

                            }
                            if(!usefulRes.equals("false")) {

                                JSONArray inner3 = new JSONArray(usefulRes);
                                for(int i=0;i<inner3.length();i++){
                                    DrawerPojo drawerPojo=new DrawerPojo();
                                    JSONObject innerobj = inner3.getJSONObject(i);
                                    drawerPojo.setUser_image(innerobj.getString("profile_pic"));
                                    drawerPojo.setUser_name(innerobj.getString("first_name")+" "+innerobj.getString("last_name"));
                                    drawerPojo.setComment("Marked your review as Useful.");
                                    drawerPojo.setReviewId(innerobj.getString("reviews_id"));
                                    drawerPojo.setDate(innerobj.getString("creation_date"));
                                    drawerPojo.setNotificationId(innerobj.getString("noti_id"));
                                    drawerPojo.setTableName("reviews_useful");
                                    drawerPojo.setBrandImage("");
                                    drawerPojo.setChatId("");
                                    drawerPojo.setChatKey("");
                                    drawerPojo.setChatType("");
                                    drawerPojo.setChatBrandId("");
                                    drawerPojo.setChatFlagId("");
                                    drawerPojo.setChatFrom("");
                                    drawerPojo.setNotificationFlag(innerobj.getString("notification_flag"));
                                    if(innerobj.getString("notification_flag").equals("0")){
                                        count++;
                                    }
                                    dataList.add(drawerPojo);
                                }

                            }
                            if(!unlikeRes.equals("false")) {

                                JSONArray inner2 = new JSONArray(unlikeRes);
                                for(int i=0;i<inner2.length();i++){
                                    DrawerPojo drawerPojo=new DrawerPojo();
                                    JSONObject innerobj =inner2.getJSONObject(i);
                                    drawerPojo.setUser_image(innerobj.getString("profile_pic"));
                                    drawerPojo.setUser_name(innerobj.getString("first_name")+" "+innerobj.getString("last_name"));
                                    drawerPojo.setComment("disliked your review.");
                                    drawerPojo.setReviewId(innerobj.getString("reviews_id"));
                                    drawerPojo.setDate(innerobj.getString("creation_date"));
                                    drawerPojo.setNotificationId(innerobj.getString("noti_id"));
                                    drawerPojo.setTableName("reviews_dislike");
                                    drawerPojo.setBrandImage("");
                                    drawerPojo.setChatId("");
                                    drawerPojo.setChatKey("");
                                    drawerPojo.setChatType("");
                                    drawerPojo.setChatBrandId("");
                                    drawerPojo.setChatFlagId("");
                                    drawerPojo.setChatFrom("");
                                    drawerPojo.setNotificationFlag(innerobj.getString("notification_flag"));
                                    if(innerobj.getString("notification_flag").equals("0")){
                                        count++;
                                    }
                                    dataList.add(drawerPojo);
                                }

                            }
                            if(!likeRes.equals("false")) {

                                JSONArray inner1 = new JSONArray(likeRes);
                                for(int i=0;i<inner1.length();i++){
                                    DrawerPojo drawerPojo=new DrawerPojo();
                                    JSONObject innerobj = inner1.getJSONObject(i);
                                    drawerPojo.setUser_image(innerobj.getString("profile_pic"));
                                    drawerPojo.setUser_name(innerobj.getString("first_name")+" "+innerobj.getString("last_name"));
                                    drawerPojo.setComment("liked your review.");
                                    drawerPojo.setReviewId(innerobj.getString("reviews_id"));
                                    drawerPojo.setDate(innerobj.getString("creation_date"));
                                    drawerPojo.setNotificationId(innerobj.getString("noti_id"));
                                    drawerPojo.setTableName("reviews_like");
                                    drawerPojo.setBrandImage("");
                                    drawerPojo.setChatId("");
                                    drawerPojo.setChatKey("");
                                    drawerPojo.setChatType("");
                                    drawerPojo.setChatBrandId("");
                                    drawerPojo.setChatFlagId("");
                                    drawerPojo.setChatFrom("");
                                    drawerPojo.setNotificationFlag(innerobj.getString("notification_flag"));
                                    if(innerobj.getString("notification_flag").equals("0")){
                                        count++;
                                    }
                                    dataList.add(drawerPojo);
                                }

                            }
                            if(!res.equals("false")) {

                                JSONArray inner = new JSONArray(res);
                                for(int i=0;i<inner.length();i++){
                                    DrawerPojo drawerPojo=new DrawerPojo();
                                    JSONObject innerobj = inner.getJSONObject(i);
                                    drawerPojo.setUser_image(innerobj.getString("profile_pic"));
                                    drawerPojo.setUser_name(innerobj.getString("first_name")+" "+innerobj.getString("last_name"));
                                    drawerPojo.setComment("commented on your review.");
                                    drawerPojo.setReviewId(innerobj.getString("reviews_id"));
                                    drawerPojo.setDate(innerobj.getString("creation_date"));
                                    drawerPojo.setNotificationId(innerobj.getString("noti_id"));
                                    drawerPojo.setTableName("review_history");
                                    drawerPojo.setBrandImage("");
                                    drawerPojo.setChatId("");
                                    drawerPojo.setChatKey("");
                                    drawerPojo.setChatType("");
                                    drawerPojo.setChatBrandId("");
                                    drawerPojo.setChatFlagId("");
                                    drawerPojo.setChatFrom("");
                                    drawerPojo.setNotificationFlag(innerobj.getString("notification_flag"));
                                    if(innerobj.getString("notification_flag").equals("0")){
                                        count++;
                                    }
                                    dataList.add(drawerPojo);
                                }

                            }
                            notifIcon.setImageDrawable(buildCounterDrawable(count,  R.drawable.notif_icon));
                            adapterDrawer = new CustomDrawerAdapter(HomeScreen.this, R.layout.list_item_drawer, dataList);
                            mDrawerList.setAdapter(adapterDrawer);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialog.dismiss();
                dataList=new ArrayList<>();
                DrawerPojo drawerPojo=new DrawerPojo();
                drawerPojo.setUser_image("");
                drawerPojo.setUser_name("");
                drawerPojo.setComment("No Internet Connection");
                drawerPojo.setReviewId("");
                drawerPojo.setDate("");
                drawerPojo.setBrandImage("");
                drawerPojo.setChatId("");
                drawerPojo.setChatKey("");
                drawerPojo.setChatBrandId("");
                drawerPojo.setChatFlagId("");
                drawerPojo.setChatFrom("");
                drawerPojo.setNotificationId("");
                drawerPojo.setTableName("");
                drawerPojo.setNotificationFlag("");
                dataList.add(drawerPojo);
                adapterDrawer = new CustomDrawerAdapter(HomeScreen.this, R.layout.list_item_drawer, dataList);
                mDrawerList.setAdapter(adapterDrawer);
                if (error instanceof NoConnectionError) {
                    Toast.makeText(HomeScreen.this,"No Internet Connection",Toast.LENGTH_LONG).show();
                   /* new SweetAlertDialog(HomeScreen.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();*/
                } else if (error instanceof TimeoutError) {

                    Toast.makeText(HomeScreen.this,"Connection Time Out Error",Toast.LENGTH_LONG).show();
                  /*  new SweetAlertDialog(HomeScreen.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("Connection Time Out Error")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();*/
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                // params.put("revID",reviewId);
                params.put("userID", userid);
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

    public void addUserReview(final String reviewTxt) {

        ringProgressDialog = ProgressDialog.show(this, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.ADD_REVIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();

                        ringProgressDialog.dismiss();


                        if (response.equals("false")) {
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
                        }else if(response.equals("You cannot post review"))
                        {
                            Toast.makeText(HomeScreen.this,"Brand owner cannot post review",Toast.LENGTH_LONG).show();
                        } else {

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
            public void onErrorResponse(VolleyError error) {

                ringProgressDialog.dismiss();
                if (error instanceof NoConnectionError) {
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
                } else if (error instanceof TimeoutError) {


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
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", userid);
                params.put("brand_id", brandid);
                params.put("review", reviewTxt);
                params.put("rating", reviewRating + "");
                params.put("media_type", "");
                params.put("file_path", "");

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

    public void parseJson(String response) {

        try {
            brand_list = new ArrayList<>();
            JSONObject object = new JSONObject(response);

            String res = object.getString("recent_reviews");
            String likeRes = object.getString("likes");
            String shareRes = object.getString("share");
            String unlikeRes = object.getString("dislikes");
            String usefulRes = object.getString("useful");
            String top_reviewer = object.getString("top_reviewer");
            String review_of_day = object.getString("review_of_day");
            String totalComments = object.getString("totalComments");
            String totalShares = object.getString("totalShares");
            String totalLikes = object.getString("total_likes");
            String totalDislikes = object.getString("total_dislikes");
            String totalUseful = object.getString("total_useful");

            JSONArray inner = new JSONArray(res);
            JSONArray inner1 = new JSONArray(likeRes);
            JSONArray inner7 = new JSONArray(shareRes);
            JSONArray inner2 = new JSONArray(unlikeRes);
            JSONArray inner3 = new JSONArray(usefulRes);

            JSONArray inner5 = null;
            if (!review_of_day.equals("")) {
                inner5 = new JSONArray(review_of_day);
                if (inner5.length() > 0) {
                    JSONObject innerobj = new JSONObject(inner5.getString(0));
                    String pp = innerobj.getString("profile_pic");
                    if (pp.startsWith("http://graph.facebook.com/") || pp.startsWith("https://scontent.xx.fbcdn.net/") || pp.startsWith("https://graph.facebook.com/")) {
                        Picasso.with(HomeScreen.this).load(pp)
                                .resize(150, 150)
                                .centerCrop()
                                .transform(new CircleTransform()).into(profile_image);
                    } else if (pp.equals("")) {
                        Picasso.with(HomeScreen.this)
                                .load("http://bataado.cybussolutions.com/uploads/no-image-icon-hi.png")
                                .resize(150, 150)
                                .centerCrop().transform(new CircleTransform())
                                .into(profile_image);
                    } else {
                        Picasso.with(HomeScreen.this).load(End_Points.IMAGE_PROFILE_PIC + pp)
                                .resize(150, 150)
                                .centerCrop()
                                .transform(new CircleTransform()).into(profile_image);
                    }
                    reviewOfDayUserId=innerobj.getString("rod_user_id");
                    reviewOfDayUserName=innerobj.getString("rod_username");
                    user_name_home.setText(innerobj.getString("rod_username"));
                    Page_home.setText(innerobj.getString("brand_name"));
                    review_home.setText(innerobj.getString("rod_review"));
                    time_home.setText(innerobj.getString("reviewDate"));
                    brandPhone=innerobj.getString("phone");
                    brandWebsiteUrl=(innerobj.getString("website_url"));
                    brandAdress=(innerobj.getString("block")+" "+innerobj.getString("area_town"));
                    brandLatitude=(innerobj.getString("latitude"));
                    brandLongitude=(innerobj.getString("longitude"));
                    brandPic=(innerobj.getString("brand_logo"));
                    brandRating=(innerobj.getString("rating"));
                    brandId=(innerobj.getString("brand_id"));
                    brandEmail=(innerobj.getString("email"));
                    brandNAme=innerobj.getString("brand_name");
                }
            }else {
                profile_image.setVisibility(View.INVISIBLE);
                time_home.setVisibility(View.INVISIBLE);
                reviewOfDayUserId="";
                review_home.setText("");
                Page_home.setText("");
                user_name_home.setText("");
                noReviewOfDay.setText("No review of the Day");
                noReviewOfDay.setTextSize(15);
                noReviewOfDay.setTextColor(getResources().getColor(R.color.colorPrimary));
                ratingBar_home.setVisibility(View.INVISIBLE);
            }
            JSONArray inner4 = null;
            if (!top_reviewer.equals("")){
                inner4 = new JSONArray(top_reviewer);
            if (inner4.length() > 0) {
                JSONObject innerobj = new JSONObject(inner4.getString(0));
                String pp = innerobj.getString("profile_pic");
                if (pp.startsWith("http://graph.facebook.com/") || pp.startsWith("https://scontent.xx.fbcdn.net/") || pp.startsWith("https://graph.facebook.com/")) {
                    Picasso.with(HomeScreen.this).load(pp)
                            .resize(80, 80)
                            .centerCrop()
                            .transform(new CircleTransform()).into(topReviewer);
                } else if (pp.equals("")) {
                    Picasso.with(HomeScreen.this)
                            .load("http://bataado.cybussolutions.com/uploads/no-image-icon-hi.png")
                            .resize(80, 80)
                            .centerCrop().transform(new CircleTransform())
                            .into(topReviewer);
                } else {
                    Picasso.with(HomeScreen.this).load(End_Points.IMAGE_PROFILE_PIC + pp)
                            .resize(80, 80)
                            .centerCrop()
                            .transform(new CircleTransform()).into(topReviewer);
                }
                topReviewerUserId=innerobj.getString("tr_user_id");
                topReviewerUserName=innerobj.getString("tr_user_name");
                topReviewerName.setText("Congratulations " +"\n"+ innerobj.getString("tr_user_name") + " !");
                String month=getMonth();
                tvCongrats.setText("On becoming top reviewer for "+ month);
            }
        }else {
                topReviewerUserId="";
                topReviewerName.setText("No Reward awarded yet.");
                topReviewer.setVisibility(View.INVISIBLE);
                crownIcon.setVisibility(View.INVISIBLE);
                tvCongrats.setVisibility(View.INVISIBLE);
            }

            JSONArray innerTotalComments = new JSONArray(totalComments);
            JSONArray innerTotalShares = new JSONArray(totalShares);
            JSONArray Totallikes = new JSONArray(totalLikes);
            JSONArray TotalDislikes = new JSONArray(totalDislikes);
            JSONArray TotalUseful = new JSONArray(totalUseful);
            for (int i = 0; i < inner.length(); i++) {
                JSONObject innerobj = new JSONObject(inner.getString(i));
                Home_Model home_model = new Home_Model();

                home_model.setUserid(innerobj.getString("user_id"));
                home_model.setReview(innerobj.getString("review"));
                home_model.setPost_id(innerobj.getString("id"));
                if(innerobj.getString("forced_rating").equals("1"))
                home_model.setBrand_raiting(innerobj.getString("rating"));
                else
                    home_model.setBrand_raiting("0");
                home_model.setBrandid(innerobj.getString("brand_id"));
                home_model.setRating(innerobj.getString("reviewRating"));
                home_model.setDate_created(innerobj.getString("creation_date"));
                home_model.setStatus(innerobj.getString("status"));
                home_model.setEmail_brand(innerobj.getString("email"));
                home_model.setFirstname(innerobj.getString("first_name"));
                home_model.setLastname(innerobj.getString("last_name"));
                home_model.setProfilepic(innerobj.getString("profile_pic"));
                home_model.setBrand_name(innerobj.getString("brand_name"));
                // home_model.setNum_review(innerobj.getString("reviews"));
                home_model.setReviewid(innerobj.getString("reviewId"));
                home_model.setMedia_type(innerobj.getString("media_type"));
                home_model.setFile_path(innerobj.getString("file_path"));
                home_model.setPhone(innerobj.getString("phone"));
                home_model.setWebsite_url(innerobj.getString("website_url"));
                home_model.setBlock(innerobj.getString("block"));
                home_model.setArea(innerobj.getString("area_town"));
                home_model.setBrand_latitude(innerobj.getString("latitude"));
                home_model.setBrand_longitude(innerobj.getString("longitude"));
                home_model.setBrand_logo(innerobj.getString("brand_logo"));
                home_model.setInappropriate(innerobj.getString("inappropriate"));
                if (inner1.getString(i).equals("1")) {
                    home_model.setIcon("1");
                } else if (inner2.getString(i).equals("1")) {
                    home_model.setIcon("2");
                } else if (inner3.getString(i).equals("1")) {
                    home_model.setIcon("3");
                } else {
                    home_model.setIcon("0");
                }
                if(inner7.getString(i).equals("1")){
                    home_model.setShared("1");
                }else {
                    home_model.setShared("0");
                }
                int total=Integer.parseInt(Totallikes.getString(i))+Integer.parseInt(TotalDislikes.getString(i))+Integer.parseInt(TotalUseful.getString(i));
                home_model.setTotalLikes(total);
                home_model.setTotalComments(innerTotalComments.getString(i));
                home_model.setTotalShares(innerTotalShares.getString(i));


                brand_list.add(home_model);

                // Toast.makeText(HomeScreen.this,home_model.getReviewid(), Toast.LENGTH_SHORT).show();
            }


            // Toast.makeText(HomeScreen.this, outer, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.layout_notifications, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    private void saveFile(final String file, String file1, final String fileType, final String reviewTxt, final float reviewRating) {
        ringProgressDialog = ProgressDialog.show(this, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.ADD_REVIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();

                        ringProgressDialog.dismiss();


                        if (response.equals("false")) {
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
                        }else if(response.equals("You cannot post review")){
                            Toast.makeText(HomeScreen.this,"Brand owner cannot post review",Toast.LENGTH_LONG).show();
                        } else {

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
            public void onErrorResponse(VolleyError error) {

                ringProgressDialog.dismiss();
                if (error instanceof NoConnectionError) {
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
                } else if (error instanceof TimeoutError) {


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
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                params.put("user_id", userid);
                params.put("brand_id", brandid);
                params.put("review", reviewTxt);
                params.put("rating", reviewRating + "");
                params.put("media_type", fileType);
                params.put("file_path", file);

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

    @Override
    protected void onResume() {
        super.onResume();
       // drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);
        if(forCommentPos!=-1){
            brand_list.get(forCommentPos).setTotalComments(noOfComments);
            home_addapter.notifyDataSetChanged();
            forCommentPos=-1;
        }
    }
    private static String getMonth() {
        Date d = new Date();//SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
        return monthName;
    }
}
