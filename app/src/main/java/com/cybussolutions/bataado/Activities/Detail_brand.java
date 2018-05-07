package com.cybussolutions.bataado.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import com.cybussolutions.bataado.Adapter.Home_Addapter;
import com.cybussolutions.bataado.Fragments.Drawer_Fragment;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.cybussolutions.bataado.Utils.callBack;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.cybussolutions.bataado.Utils.FilePath.getDataColumn;
import static com.cybussolutions.bataado.Utils.FilePath.isDownloadsDocument;
import static com.cybussolutions.bataado.Utils.FilePath.isExternalStorageDocument;
import static com.cybussolutions.bataado.Utils.FilePath.isMediaDocument;

public class Detail_brand extends AppCompatActivity implements callBack, OnMapReadyCallback {
    private static final int READ_REQUEST_CODE = 42;
    String filepath;
    String[] fileName;
    String file;
    private String upLoadServerUri = null;
    private String imagepath = null;
    Toolbar toolbar;
    ListView review_list;
    TextView brand_name;
    ImageView brand_image;
    TextView brand_reivew, review_count;
    RatingBar brand_rating;
    Home_Addapter home_addapter = null;
    String brandNAme, brandId, brandRating, brandAdress, brandPic, reviewCount;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    private ArrayList<Home_Model> brand_list = new ArrayList<>();
    ProgressDialog ringProgressDialog;
    ProgressDialog progressBar;
    ImageView home_fotter, search_footer, logo;
    ImageView ivAddPhoto, ivAddReview, ivShareReview;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private long fileSize = 0;
    Drawer_Fragment drawerFragment = new Drawer_Fragment();
    Bitmap bitmap;
    private CallbackManager callbackManager;
    private MapView mMapView;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_brand);

        Intent intent = getIntent();
        brandNAme = intent.getStringExtra("brandNAme");
        brandId = intent.getStringExtra("brandId");
        brandRating = intent.getStringExtra("brandRating");
        // brandAdress = intent.getStringExtra("brandAdress");
        // brandPic = intent.getStringExtra("brandPic");
        // reviewCount = intent.getStringExtra("reviewCount");

        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle(brandNAme);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);

        getAllReview();

        review_list = findViewById(R.id.listview_search);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.detail_brand_header, review_list,
                false);

        review_list.addHeaderView(header);
        logo = findViewById(R.id.logo_img);
        brand_name = header.findViewById(R.id.brand_name);
        brand_image = header.findViewById(R.id.brand_image);
        brand_reivew = header.findViewById(R.id.review);
        review_count = header.findViewById(R.id.review_count);
        brand_rating = header.findViewById(R.id.ratingBar_home);
        ivAddPhoto = header.findViewById(R.id.imageView8);
        ivAddReview = header.findViewById(R.id.imageViewReview);
        ivShareReview = header.findViewById(R.id.imageViewShare);
        ivShareReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareResult(view, brandNAme, brandId);
            }
        });
        ivAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postReview("", "", "");
            }
        });
        ivAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(Detail_brand.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

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
        });
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Detail_brand.this, HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent1);
            }
        });
        brand_name.setText(brandNAme);
        brand_reivew.setText(brandAdress);


        Picasso.with(this)
                .load(End_Points.IMAGE_BASE_URL + brandPic)
                .resize(150, 150)
                .centerCrop().transform(new CircleTransform())
                .into(brand_image);


        if (!brandRating.equals("null") && !brandRating.equals("")) {
            brand_rating.setRating(Integer.parseInt(brandRating));
        } else {
            brand_rating.setRating(0);
        }

        home_fotter = findViewById(R.id.home_fotter);
        home_fotter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Detail_brand.this, HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
        search_footer = findViewById(R.id.search_fotter);
        search_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Detail_brand.this, SearchScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

    }

    public void onShareResult(View view, String brandName, String reviewId) {
        FacebookSdk.sdkInitialize(Detail_brand.this);
        callbackManager = CallbackManager.Factory.create();
        final ShareDialog shareDialog = new ShareDialog(Detail_brand.this);

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d("success", "success");
                Toast.makeText(Detail_brand.this, "Posted Successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("error", "error");
            }

            @Override
            public void onCancel() {
                Log.d("error", "cancel");
            }
        });


        if (ShareDialog.canShow(ShareLinkContent.class)) {

            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(brandName)
                    .setContentDescription("Bataado")
                    .setContentUrl(Uri.parse("http://bataado.cybussolutions.com"))
                    .setImageUrl(Uri.parse(End_Points.IMAGE_RREVIEW_URL + "logo.png"))
                    .build();

            shareDialog.show(linkContent);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            //Bitmap photo = (Bitmap) data.getData().getPath();

            Uri selectedImageUri = data.getData();
            bitmap = BitmapFactory.decodeFile(imagepath);

            String path = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                path = getPath(Detail_brand.this, selectedImageUri);
            }

            if (path != null && !path.equals("")) {
                fileName = path.split("/");
                file = fileName[fileName.length - 1];
                File file = new File(path);

                long length = file.length();
                if(length<=1024) {
                    UploadFile uploadFile = new UploadFile();
                    uploadFile.delegate = Detail_brand.this;
                    uploadFile.execute(path);
                    progressBar = new ProgressDialog(Detail_brand.this);
                    progressBar.setCancelable(false);
                    progressBar.setMessage("Uploading ...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                    progressBarStatus = 0;
                    fileSize = 0;
                    new Thread(new Runnable() {
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
                        /*if (progressBarStatus >= 100) {
                            // sleeping for 1 second after operation completed
                            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
                            // close the progress bar dialog
                            progressBar.dismiss();
                        }*/
                        }
                    }).start();

                    /*ringProgressDialog = ProgressDialog.show(Detail_brand.this, "Please wait ...", "Uploading File ...", true);
                    ringProgressDialog.setCancelable(false);
                    ringProgressDialog.show();*/


                }else {
                    Toast.makeText(this, "Maximum size is 10 mb", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "file not found", Toast.LENGTH_LONG).show();
            }


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

    public void getAllReview() {

        ringProgressDialog = ProgressDialog.show(this, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_REVIEW_BRAND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);

                            String count = object.getString("total_reviews");
                            review_count.setText(count);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        ringProgressDialog.dismiss();

                        parseJson(response);

                        home_addapter = new Home_Addapter(Detail_brand.this, brand_list,"");

                        review_list.setAdapter(home_addapter);

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialog.dismiss();

                if (error instanceof NoConnectionError) {
                    new SweetAlertDialog(getApplication(), SweetAlertDialog.ERROR_TYPE)
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                params.put("brand_id", brandId);
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


    public void parseJson(String response) {

        try {

            JSONObject object = new JSONObject(response);

            String res = object.getString("recent_reviews");
            String likeRes = object.getString("likes");
            String unlikeRes = object.getString("dislikes");
            String usefulRes = object.getString("useful");

            JSONArray inner = new JSONArray(res);
            JSONArray inner1 = new JSONArray(likeRes);
            JSONArray inner2 = new JSONArray(unlikeRes);
            JSONArray inner3 = new JSONArray(usefulRes);

            for (int i = 0; i < inner.length(); i++) {
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
                //    home_model.setEmail_brand(innerobj.getString("email"));
                home_model.setFirstname(innerobj.getString("first_name"));
                home_model.setLastname(innerobj.getString("last_name"));
                home_model.setProfilepic(innerobj.getString("profile_pic"));
                home_model.setBrand_name(innerobj.getString("brand_name"));
                // home_model.setNum_review(innerobj.getString("reviews"));
                home_model.setReviewid(innerobj.getString("reviewId"));
                //home_model.setIcon("0");
                home_model.setMedia_type(innerobj.getString("media_type"));
                home_model.setFile_path(innerobj.getString("file_path"));
                if (inner1.getString(i).equals("1")) {
                    home_model.setIcon("1");
                } else if (inner2.getString(i).equals("1")) {
                    home_model.setIcon("2");
                } else if (inner3.getString(i).equals("1")) {
                    home_model.setIcon("3");
                } else {
                    home_model.setIcon("0");
                }
               /* home_model.setUserid(innerobj.getString("user_id"));
                home_model.setReview(innerobj.getString("review"));
                home_model.setPost_id(innerobj.getString("id"));
                home_model.setBrandid(innerobj.getString("brand_id"));
                home_model.setRating(innerobj.getString("reviewRating"));
                home_model.setDate_created(innerobj.getString("creation_date"));
                home_model.setStatus(innerobj.getString("status"));
                //home_model.setEmail_brand(innerobj.getString("email"));
                home_model.setFirstname(innerobj.getString("first_name"));
                home_model.setLastname(innerobj.getString("last_name"));
                home_model.setProfilepic(innerobj.getString("profile_pic"));
                home_model.setBrand_name(innerobj.getString("brand_name"));
                //home_model.setNum_review(innerobj.getString("reviews"));
                home_model.setReviewid(innerobj.getString("reviewId"));*/
              /*  home_model.setPhone(innerobj.getString("phone"));
                home_model.setWebsite_url(innerobj.getString("website_url"));
                home_model.setBlock(innerobj.getString("block"));
                home_model.setArea(innerobj.getString("area_town"));
                home_model.setBrand_logo(innerobj.getString("brand_logo"));*/

                brand_list.add(home_model);

                // Toast.makeText(HomeScreen.this,home_model.getReviewid(), Toast.LENGTH_SHORT).show();
            }


            // Toast.makeText(HomeScreen.this, outer, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processFinish(String output) {
        // if (progressBarStatus >= 100) {
        // sleeping for 1 second after operation completed
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // close the progress bar dialog
        progressBar.dismiss();
        // }
        if (output.equals("200")) {
            //ringProgressDialog.dismiss();
            String fileType;
            // Toast.makeText(mcontext, "uploaded Succesfully"+ fileName, Toast.LENGTH_SHORT).show();
            if (file.contains(".jpg") || file.contains(".jpeg") || file.contains(".png") || file.contains(".gif")) {
                fileType = "picture";
                postDialog(file, file, fileType);
                // saveFile(file,file,fileType);
            } else if (file.contains(".mp4")) {
                fileType = "video";
                postDialog(file, file, fileType);
                // saveFile(file,file,fileType);
            } else {
                //  ringProgressDialog.dismiss();
                Toast.makeText(Detail_brand.this, "File format not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            ringProgressDialog.dismiss();
            Toast.makeText(Detail_brand.this, "upload error", Toast.LENGTH_SHORT).show();

        }
    }

    private void postReview(final String file, String file1, final String fileType) {
        LayoutInflater layoutInflater = LayoutInflater.from(Detail_brand.this);
        final View customView = layoutInflater.inflate(R.layout.post_review_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(Detail_brand.this).create();
        alertDialog.setCancelable(false);
        final TextView heading = customView.findViewById(R.id.brand_name);
        heading.setText(brandNAme);
        final EditText etPost = customView.findViewById(R.id.et_post);
        final RatingBar brand_rating = customView.findViewById(R.id.brand_raiting);
        ImageView cancel = customView.findViewById(R.id.cancel_button);
        Button postReview = customView.findViewById(R.id.postStatusUpdateButton);

        postReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String reviewTxt = etPost.getText().toString();
                float reviewRating = brand_rating.getRating();
                saveFile("", "", "", reviewTxt, reviewRating);
                //  saveFile(file,file,fileType,reviewTxt,reviewRating);
                alertDialog.dismiss();

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

    private void postDialog(final String file, String file1, final String fileType) {
        LayoutInflater layoutInflater = LayoutInflater.from(Detail_brand.this);
        final View customView = layoutInflater.inflate(R.layout.post_picture, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(Detail_brand.this).create();
        alertDialog.setCancelable(false);
        final TextView heading = customView.findViewById(R.id.brand_name);
        final ImageView post_picture = customView.findViewById(R.id.profilePicture);
        heading.setText(brandNAme);
        final EditText etPost = customView.findViewById(R.id.et_post);
        final RatingBar brand_rating = customView.findViewById(R.id.brand_raiting);
        ImageView cancel = customView.findViewById(R.id.cancel_button);
        Button postReview = customView.findViewById(R.id.postStatusUpdateButton);

        postReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String reviewTxt = etPost.getText().toString();
                float reviewRating = brand_rating.getRating();
                saveFile(file, file, fileType, reviewTxt, reviewRating);
                alertDialog.dismiss();

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
        Picasso.with(Detail_brand.this).load(End_Points.IMAGE_RREVIEW_URL + file).resize(400, 400).centerCrop().into(post_picture);
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
                            new SweetAlertDialog(Detail_brand.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error!")
                                    .setConfirmText("OK").setContentText("Some Error ")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();

                                        }
                                    })
                                    .show();
                        } else {

                            new SweetAlertDialog(Detail_brand.this, SweetAlertDialog.SUCCESS_TYPE)
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
                    new SweetAlertDialog(Detail_brand.this, SweetAlertDialog.ERROR_TYPE)
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


                    new SweetAlertDialog(Detail_brand.this, SweetAlertDialog.ERROR_TYPE)
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
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                params.put("user_id", userid);
                params.put("brand_id", brandId);
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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
       /*
       //in old Api Needs to call MapsInitializer before doing any CameraUpdateFactory call
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
       */

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);
       // map.moveCamera(CameraUpdateFactory.newLatLng(43.1, -87.9));

    }

    static class  UploadFile extends AsyncTask<String,Void,String>
    {
        public callBack delegate = null;
        private String upLoadServerUri = null;
        private String imagepath=null;
        private int serverResponseCode = 0;

        @Override
        protected void onPreExecute() {


            super.onPreExecute();
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

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

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

    }
}
