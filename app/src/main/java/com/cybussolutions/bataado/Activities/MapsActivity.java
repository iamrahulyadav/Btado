package com.cybussolutions.bataado.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RecoverySystem;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cybussolutions.bataado.Adapter.Home_Addapter;
import com.cybussolutions.bataado.Fragments.Drawer_Fragment;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.CheckConnection;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.cybussolutions.bataado.Utils.DialogBox;
import com.cybussolutions.bataado.Utils.callBack;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.cybussolutions.bataado.Activities.HomeScreen.forCommentPos;
import static com.cybussolutions.bataado.Activities.HomeScreen.noOfComments;
import static com.cybussolutions.bataado.Utils.FilePath.getDataColumn;
import static com.cybussolutions.bataado.Utils.FilePath.isDownloadsDocument;
import static com.cybussolutions.bataado.Utils.FilePath.isExternalStorageDocument;
import static com.cybussolutions.bataado.Utils.FilePath.isMediaDocument;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,callBack {

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
    TextView brand_reivew, review_count,tvAddress,tvWebsite,tvPhone,tvEmail,tvBranches;
    RatingBar brand_rating;
    Home_Addapter home_addapter = null;
    String brandNAme, brandId, brandRating, brandAdress, brandPic,brandEmail, reviewCount,brandPhone,brandWebsiteUrl,brandLatitude,brandLongitude;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    private ArrayList<Home_Model> brand_list = new ArrayList<>();
    ProgressDialog ringProgressDialog;
    static ProgressDialog progressBar;
    ImageView home_fotter, search_footer, logo,profile_footer;
    ImageView ivAddPhoto, ivAddReview, ivShareReview;
    RelativeLayout photosAndVideos;
    private static int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private static long fileSize = 0;
    Drawer_Fragment drawerFragment = new Drawer_Fragment();
    Bitmap bitmap;
    private CallbackManager callbackManager;
    private MapView mMapView;
    GoogleMap mMap;
    ImageView picture1,picture2,picture3;
    TextView textViewSeeAll,removePicOrVideo;
    Button addPicVideo;
    ImageView post_picture;
    String fileType;
    LinearLayout layoutmap;
    static Activity activity;
    Camera camera;
   /* LocationManager locationManager;
    Location loc;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        activity=MapsActivity.this;
        forCommentPos=-1;
        noOfComments="0";


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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        Intent intent = getIntent();
        brandNAme = intent.getStringExtra("brandNAme");
        brandId = intent.getStringExtra("brandId");
        brandRating = intent.getStringExtra("brandRating");
        brandAdress = intent.getStringExtra("brandAdress");
        brandPhone = intent.getStringExtra("phone");
        brandWebsiteUrl = intent.getStringExtra("websiteUrl");
        brandLatitude = intent.getStringExtra("latitude");
        brandLongitude = intent.getStringExtra("longitude");
        brandEmail = intent.getStringExtra("email");
        brandPic = intent.getStringExtra("brandPic");
        // reviewCount = intent.getStringExtra("reviewCount");

        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle(brandNAme);
        //setSupportActionBar(toolbar);
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.menu);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);


        FacebookSdk.sdkInitialize(MapsActivity.this);
        callbackManager = CallbackManager.Factory.create();
        HomeScreen.callbackManager=callbackManager;
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
        tvAddress = header.findViewById(R.id.address);
        tvWebsite = header.findViewById(R.id.website);
        tvPhone = header.findViewById(R.id.phone);
        tvEmail = header.findViewById(R.id.email);
        tvBranches = header.findViewById(R.id.branches);
        brand_rating = header.findViewById(R.id.ratingBar_home);
        ivAddPhoto = header.findViewById(R.id.imageView8);
        ivAddReview = header.findViewById(R.id.imageViewReview);
        picture1 = header.findViewById(R.id.picture1);
        picture2 = header.findViewById(R.id.picture2);
        picture3 = header.findViewById(R.id.picture3);
        textViewSeeAll = header.findViewById(R.id.textViewSeeAll);
        ivShareReview = header.findViewById(R.id.imageViewShare);
        photosAndVideos = header.findViewById(R.id.layoutPhotos);
        layoutmap = header.findViewById(R.id.layoutmap);
        layoutmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tvBranches.getText().equals("No Branches Found")) {
                    Intent intent = new Intent(MapsActivity.this, Branches.class);
                    intent.putExtra("brandId", brandId);
                    intent.putExtra("brandName", brandNAme);
                    startActivity(intent);
                }
            }
        });
        photosAndVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!textViewSeeAll.getText().equals("No Gallery Available")) {
                    Intent intent = new Intent(MapsActivity.this, ViewAllGallery.class);
                    intent.putExtra("brandId", brandId);
                    startActivity(intent);
                }
            }
        });

        tvAddress.setText(brandAdress);
        tvWebsite.setText(brandWebsiteUrl);
        tvPhone.setText(brandPhone);
        tvEmail.setText(brandEmail);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ivShareReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareResult(view, brandNAme, brandId,brandPic);
            }
        });
        ivAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // postReview("", "", "");
                postDialog("","","");
            }
        });
        ivAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[]{"Camera","Storage"};
                AlertDialog.Builder builder= new AlertDialog.Builder(activity);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i==0){


                            if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                                if (Build.VERSION.SDK_INT > 22) {

                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},4);

                                }

                            }else {
                                // Call the camera takePicture method to open the existing camera
                                try {
                                    camera.takePicture();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                postDialog("","","");
                            }


                        }
                        if (i==1){


                            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

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

                                postDialog("","","");
                            }
                        }
                    }
                }).show();




            }
        });
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent1 = new Intent(MapsActivity.this, HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent1);*/
            }
        });
        brand_name.setText(brandNAme);
        brand_reivew.setText(brandAdress);

        if(!brandPic.equals("") && !brandPic.equals("null")) {
            Picasso.with(this)
                    .load(End_Points.IMAGE_BASE_URL + brandPic)
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(brand_image);
        }else {
            Picasso.with(this)
                    .load(R.drawable.no_logo)
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(brand_image);
        }


        if (!brandRating.equals("null") && !brandRating.equals("") && brandRating!=null) {
            brand_rating.setRating(Integer.parseInt(brandRating));
        } else {
            brand_rating.setRating(0);
        }
        getAllReview();
        home_fotter = findViewById(R.id.home_fotter);
        profile_footer = findViewById(R.id.profile_fotter);
        profile_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pp = pref.getString("profile_pic","");
                String strname = pref.getString("user_name","");
                String strid = pref.getString("user_id","");
                Intent intent = new Intent(MapsActivity.this, User_Profile.class);
                intent.putExtra("username", strname);
                intent.putExtra("userID", strid);
                startActivity(intent);
            }
        });
        home_fotter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
        search_footer = findViewById(R.id.search_fotter);
        search_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, SearchScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });


    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = null;
        // Add a marker in Sydney and move the camera
        if(brandLatitude==null || brandLongitude==null ||brandLatitude.equals("")|| brandLatitude.equals("null")|| brandLongitude.equals("") || brandLongitude.equals("null")){
            sydney = new LatLng(31.5213003, 74.32606669999996);
        }else {
            sydney = new LatLng(Float.parseFloat(brandLatitude), Float.parseFloat(brandLongitude));
        }
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
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
                Picasso.with(MapsActivity.this).load(End_Points.IMAGE_RREVIEW_URL + file).resize(400, 400).centerCrop().into(post_picture);

            } else if (file.contains(".mp4")) {
                fileType = "video";
                post_picture.setVisibility(View.VISIBLE);
                removePicOrVideo.setVisibility(View.VISIBLE);
                addPicVideo.setVisibility(View.GONE);
                Picasso.with(MapsActivity.this).load(End_Points.IMAGE_RREVIEW_URL + file).placeholder(R.drawable.vedios_placeholder).resize(400, 400).centerCrop().into(post_picture);

                //postDialog(file, file, fileType);
                // saveFile(file,file,fileType);
            } else {
                //  ringProgressDialog.dismiss();
                Toast.makeText(MapsActivity.this, "File format not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            ringProgressDialog.dismiss();
            Toast.makeText(MapsActivity.this, "upload error", Toast.LENGTH_SHORT).show();

        }
    }
    public void onShareResult(View view, String brandName, String reviewId,String brandLogo) {

        final ShareDialog shareDialog = new ShareDialog(MapsActivity.this);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            SharePhoto photo = new SharePhoto.Builder().setImageUrl(Uri.parse(End_Points.IMAGE_RREVIEW_URL+brandLogo))
                    .setUserGenerated(true).build();
            ShareOpenGraphObject object;

                object = new ShareOpenGraphObject.Builder()
                        .putString("og:type", "article")
                        .putString("og:url", "http://demo.cybussolutions.com/bataado")
                        .putString("og:title", brandName)
                        .putString("og:description", "BataaDo - Rate & Review")
                        .putPhoto("og:image", photo)
                        //.putString("books:isbn", "0-553-57340-3")
                        .build();

            // Create an action
            ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                    .setActionType("news.reads")
                    .putObject("article", object)
                    .build();

            ShareOpenGraphContent linkContent = new ShareOpenGraphContent.Builder()
                    .setPreviewPropertyName("article")
                    .setAction(action)
                    .build();
            shareDialog.registerCallback(HomeScreen.callbackManager,callback);
            shareDialog.show(linkContent, ShareDialog.Mode.AUTOMATIC);
           /* ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(brandName)
                    .setContentDescription("Bataado")
                    .setContentUrl(Uri.parse("http://bataado.cybussolutions.com"))
                    .setImageUrl(Uri.parse(End_Points.IMAGE_RREVIEW_URL + "logo.png"))
                    .build();
            shareDialog.registerCallback(callbackManager,callback);
            shareDialog.show(linkContent, ShareDialog.Mode.AUTOMATIC);*/
        }


    }
    private FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            Toast.makeText(MapsActivity.this,"Successfully posted",Toast.LENGTH_LONG).show();

            // Write some code to do some operations when you shared content successfully.
        }

        @Override
        public void onCancel() {
            Toast.makeText(MapsActivity.this,"Cancelled",Toast.LENGTH_LONG).show();
            // Write some code to do some operations when you cancel sharing content.
        }

        @Override
        public void onError(FacebookException error) {
            Toast.makeText(MapsActivity.this, error.getMessage(),Toast.LENGTH_LONG).show();
            // Write some code to do some operations when some error occurs while sharing content.
        }
    };

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
                    postDialog("","","");
                }

                if (requestCode == 10) {

                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    //   intent.setSelector(Intent.getIntent().removeCategory(););

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
                    postDialog("","","");
                }
            } else {
                Toast.makeText(MapsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
                path = getPath(MapsActivity.this, selectedImageUri);
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
                    uploadFile.delegate = MapsActivity.this;
                    uploadFile.execute(path);

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
                uploadFile.delegate = MapsActivity.this;
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
                            String photos = object.getString("photos");
                            if(photos.equals("false")){
                                picture1.setVisibility(View.GONE);
                                picture2.setVisibility(View.GONE);
                                picture3.setVisibility(View.GONE);
                                textViewSeeAll.setText("No Gallery Available");
                            }

                            else
                            {
                                textViewSeeAll.setText("See All");
                                JSONArray inner = new JSONArray(photos);
                                for (int i = 0; i < inner.length() && i<3; i++) {
                                    JSONObject innerobj = new JSONObject(inner.getString(i));
                                    if(i==0) {
                                        if(innerobj.getString("brand_pic").contains(".mp4")){
                                            Glide.with(MapsActivity.this)
                                                    .load(getResources().getDrawable(R.drawable.vedios_placeholder))
                                                    .apply(new RequestOptions().override(100,100).centerCrop())
                                                    .into(picture1);
                                        }else {
                                            Glide.with(MapsActivity.this)
                                                    .load(End_Points.BRAND_GALLERY + innerobj.getString("brand_pic"))
                                                    .apply(new RequestOptions().override(100,100).centerCrop().placeholder(getResources().getDrawable(R.drawable.images_placeholder)))
                                                    .into(picture1);
                                        }
                                    }else if(i==1){
                                        if(innerobj.getString("brand_pic").contains(".mp4")){
                                            Glide.with(MapsActivity.this)
                                                    .load(getResources().getDrawable(R.drawable.vedios_placeholder))
                                                    .apply(new RequestOptions().override(100,100).centerCrop())
                                                    .into(picture2);
                                        }else {
                                            Glide.with(MapsActivity.this)
                                                    .load(End_Points.BRAND_GALLERY + innerobj.getString("brand_pic"))
                                                    .apply(new RequestOptions().override(100,100).centerCrop().placeholder(getResources().getDrawable(R.drawable.images_placeholder)))
                                                    .into(picture2);
                                        }
                                    }else if(i==2){
                                        if(innerobj.getString("brand_pic").contains(".mp4")){
                                            Glide.with(MapsActivity.this)
                                                    .load(getResources().getDrawable(R.drawable.vedios_placeholder))
                                                    .apply(new RequestOptions().override(100,100).centerCrop())
                                                    .into(picture3);
                                        }else {
                                            Glide.with(MapsActivity.this)
                                                    .load(End_Points.BRAND_GALLERY + innerobj.getString("brand_pic"))
                                                    .apply(new RequestOptions().override(100,100).centerCrop().placeholder(getResources().getDrawable(R.drawable.images_placeholder)))
                                                    .into(picture3);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        ringProgressDialog.dismiss();

                        parseJson(response);

                        home_addapter = new Home_Addapter(MapsActivity.this, brand_list,"");

                        review_list.setAdapter(home_addapter);

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialog.dismiss();

                if (error instanceof NoConnectionError) {
                    new DialogBox(MapsActivity.this, "No Internet Connection !", "Error",
                            "Error");
                } else if (error instanceof TimeoutError) {
                    new DialogBox(MapsActivity.this, "Connection Time Out Error", "Error",
                            "Error");
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
                if (!brandRating.equals("null") && !brandRating.equals("") && brandRating!=null) {
                   // brand_rating.setRating(Integer.parseInt(brandRating));
                    params.put("rating", brandRating);
                } else {
                    params.put("rating", "0");
                }


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
            String shareRes = object.getString("share");
            String unlikeRes = object.getString("dislikes");
            String usefulRes = object.getString("useful");
            String totalComments = object.getString("totalComments");
            String totalShares = object.getString("totalShares");
            String totalLikes = object.getString("total_likes");
            String totalDislikes = object.getString("total_dislikes");
            String totalUseful = object.getString("total_useful");
            String brandRating = object.getString("rating");
            String branches = object.getString("branches");


            JSONArray inner = new JSONArray(res);
            JSONArray inner1 = new JSONArray(likeRes);
            JSONArray inner2 = new JSONArray(unlikeRes);
            JSONArray inner3 = new JSONArray(usefulRes);
            JSONArray inner4 = new JSONArray(shareRes);
           // JSONArray arrayBranches = new JSONArray(branches);
            if(!branches.equals("0")){
                tvBranches.setText("No. of Branches : "+branches);
            }else {
                tvBranches.setText("No Branches Found");
            }
            JSONArray totalRating = new JSONArray(brandRating);
            brand_rating.setRating(Integer.parseInt(totalRating.getString(0)));
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
                if(inner4.getString(i).equals("1")){
                    home_model.setShared("1");
                }else {
                    home_model.setShared("0");
                }
                int total=Integer.parseInt(Totallikes.getString(i))+Integer.parseInt(TotalDislikes.getString(i))+Integer.parseInt(TotalUseful.getString(i));
                home_model.setTotalLikes(total);
                home_model.setTotalComments(innerTotalComments.getString(i));
                home_model.setTotalShares(innerTotalShares.getString(i));
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
    private void postReview(final String file, String file1, final String fileType) {
        LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
        final View customView = layoutInflater.inflate(R.layout.post_review_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
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
                if(!reviewTxt.equals("")&& reviewTxt.trim().length()>0) {
                    saveFile("", "", "", reviewTxt, reviewRating);
                    //  saveFile(file,file,fileType,reviewTxt,reviewRating);
                    alertDialog.dismiss();
                }else {
                    Toast.makeText(MapsActivity.this,"Please add some review",Toast.LENGTH_LONG).show();
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

    private void postDialog(final String file2, String file1, final String fileType1) {
        fileType="";
        file="";
        LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
        final View customView = layoutInflater.inflate(R.layout.post_picture, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
        alertDialog.setCancelable(false);
        final TextView heading = customView.findViewById(R.id.brand_name);
         removePicOrVideo = customView.findViewById(R.id.remove);
         post_picture = customView.findViewById(R.id.profilePicture);
        heading.setText(brandNAme);
        final EditText etPost = customView.findViewById(R.id.et_post);
        final RatingBar brand_rating = customView.findViewById(R.id.brand_raiting);
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


                            if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                                if (Build.VERSION.SDK_INT > 22) {

                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},4);

                                }

                            }else {
                                // Call the camera takePicture method to open the existing camera
                                try {
                                    camera.takePicture();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                //postDialog("","","");
                            }


                        }
                        if (i==1){


                            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

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

                               // postDialog("","","");
                            }
                        }
                    }
                }).show();
//                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//                    if (Build.VERSION.SDK_INT > 22) {
//
//                        requestPermissions(new String[]{Manifest.permission
//                                        .WRITE_EXTERNAL_STORAGE},
//                                10);
//
//                    }
//
//                } else {
//                    Intent intent = new Intent();
//                    intent.setType("*/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    //   intent.setSelector(Intent.getIntent().removeCategory(););
//
//                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
//                }
            }
        });

        postReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckConnection.checkStatus(MapsActivity.this)) {
                    if(brand_rating.getRating()==0){
                        Toast.makeText(MapsActivity.this, "Please rate brand", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (/*file.equals("") &&*/ etPost.getText().toString().equals("")) {
                        Toast.makeText(MapsActivity.this, "Please add some review", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String reviewTxt = etPost.getText().toString();
                    float reviewRating = brand_rating.getRating();
                    saveFile(file, file, fileType, reviewTxt, reviewRating);
                    alertDialog.dismiss();
                }else {
                    Toast.makeText(MapsActivity.this,"No Network Found",Toast.LENGTH_LONG).show();
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
                            new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                            Toast.makeText(MapsActivity.this,"Brand owner cannot post review",Toast.LENGTH_LONG).show();
                        } else {

                            new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
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
                if (error instanceof NoConnectionError)
                {
                    new DialogBox(MapsActivity.this, "No Internet Connection !", "Error",
                            "Error");
                }
                else if (error instanceof TimeoutError) {

                    new DialogBox(MapsActivity.this, "Connection Time Out Error", "Error",
                            "Error");
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

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        total += bytesRead;
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
    @Override
    protected void onResume() {
        super.onResume();
        if(forCommentPos!=-1){
            brand_list.get(forCommentPos).setTotalComments(noOfComments);
            home_addapter.notifyDataSetChanged();
            forCommentPos=-1;
        }
    }
}
