package com.cybussolutions.bataado.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
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
import com.cybussolutions.bataado.Adapter.ImageGalleryAdapter;
import com.cybussolutions.bataado.Fragments.Drawer_Fragment;
import com.cybussolutions.bataado.Model.SpacePhoto;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.cybussolutions.bataado.Utils.DialogBox;
import com.mindorks.paracamera.Camera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfilePhotos extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS=0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    ProgressDialog ringProgressDialog;
    ImageGalleryAdapter adapter;
    RecyclerView recyclerView;
    String user_id;
    Drawer_Fragment drawerFragment = new Drawer_Fragment();
    private ArrayList<SpacePhoto> review_list;//= new ArrayList<>();
    Toolbar toolbar;
    ImageView addPhoto;
    private static int IMG_RESULT = 2;
    String formattedDate;
    Camera camera;
    String b64,ImageDecode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photos);
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Profile Photos");
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
      //  drawerFragment.setup((DrawerLayout) findViewById(R.id.drawerlayout), toolbar);
        Intent intent=  getIntent();
        user_id = intent.getStringExtra("userID");
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView = findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        getImagesReviews(user_id);
        addPhoto=findViewById(R.id.addPhoto);
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(1)
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(ProfilePhotos.this);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog();
            }
        });
    }
    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                ProfilePhotos.this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        /*Intent intent = new Intent(MainActivity.this, AlbumSelectActivity.class);
                        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 4);
                        startActivityForResult(intent, Constants.REQUEST_CODE);*/

                        if (ActivityCompat.checkSelfPermission(ProfilePhotos.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                            if (Build.VERSION.SDK_INT > 22) {

                                requestPermissions(new String[]{Manifest.permission
                                                .WRITE_EXTERNAL_STORAGE},
                                        REQUEST_PERMISSIONS);

                            }

                        } else {
                            upload();

                        }

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (ActivityCompat.checkSelfPermission(ProfilePhotos.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                            if (Build.VERSION.SDK_INT > 22) {

                                requestPermissions(new String[]{Manifest.permission
                                                .CAMERA},
                                        REQUEST_IMAGE_CAPTURE);
                      /*  Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);*/
                                // Toast.makeText(MainActivity.this.getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();

                            }

                        } else {
                            camera();

                        }

                    }
                });
        myAlertDialog.show();
    }
    private void camera() {
        try {
            camera.takePicture();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void upload() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMG_RESULT);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Camera.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = camera.getCameraBitmap();
            Uri tempUri = getImageUri(getApplicationContext(), bitmap);

            String picturePath=getPathFromURI(tempUri);
            Bitmap bm = BitmapFactory.decodeFile(picturePath);
            if (!picturePath.equals("")) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                formattedDate = df.format(c.getTime());

                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                byte[] ba = bao.toByteArray();
                b64 = Base64.encodeToString(ba,Base64.DEFAULT);
                uploadImage(formattedDate,b64,picturePath);

            }
        }
        if (requestCode == IMG_RESULT  && resultCode == RESULT_OK && data != null) {
            // textView.setText(stringBuffer.toString());
            Uri URI = data.getData();
            String[] FILE = {MediaStore.Images.Media.DATA};


            Cursor cursor = getContentResolver().query(URI,
                    FILE, null, null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(FILE[0]);
            ImageDecode = cursor.getString(columnIndex);
            cursor.close();
            // CircleImageView imageView=(CircleImageView) findViewById(R.id.profilePic);

            Bitmap unscaled= BitmapFactory.decodeFile(ImageDecode);
            b64 = encodeImage(unscaled);
            uploadImage(formattedDate,b64,formattedDate);
        }


    }
    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
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
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_PERMISSIONS || requestCode==REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                if(requestCode==0) {
                    upload();
                }
                else if(requestCode==1){
                    camera();
                }
            } else {

                Toast.makeText(ProfilePhotos.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void uploadImage(final String formattedDate, final String b64, final String originalName) {
        ringProgressDialog = ProgressDialog.show(ProfilePhotos.this, "Please wait ...", "Uploading image ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,End_Points.UPLOAD_FILE_URL+"uploadPhotofromApp.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // loading.dismiss();
                ringProgressDialog.dismiss();
                if (!(response.equals(""))) {
                    saveNewAttachment(response.trim(),originalName);

                } else {
                    ringProgressDialog.dismiss();
                    Toast.makeText(ProfilePhotos.this, "Picture not uploaded", Toast.LENGTH_SHORT).show();
                }
            }

        }
                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   loading.dismiss();
                ringProgressDialog.dismiss();
                String message = null;
                if (error instanceof NoConnectionError)
                {
                    new DialogBox(ProfilePhotos.this, "No Internet Connection !", "Error",
                            "Error");
                }
                else if (error instanceof TimeoutError) {

                    new DialogBox(ProfilePhotos.this, "Connection Time Out Error", "Error",
                            "Error");
                }
            }


        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("image", b64);
                map.put("type", "user");
             //   map.put("name", formattedDate);
                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(ProfilePhotos.this);
        requestQueue.add(request);
    }

    private void saveNewAttachment(final String trim, String originalName) {
        ringProgressDialog = ProgressDialog.show(ProfilePhotos.this, "Please wait ...", " ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,End_Points.ADD_NEW_PHOTO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // loading.dismiss();
                ringProgressDialog.dismiss();
                if (!(response.equals(""))) {
                   getImagesReviews(user_id);

                }
            }

        }
                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   loading.dismiss();
                ringProgressDialog.dismiss();
                String message = null;
                if (error instanceof NoConnectionError)
                {
                    new DialogBox(ProfilePhotos.this, "No Internet Connection !", "Error",
                            "Error");
                }
                else if (error instanceof TimeoutError) {

                    new DialogBox(ProfilePhotos.this, "Connection Time Out Error", "Error",
                            "Error");
                }
            }


        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                SharedPreferences pref =getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id","");
                params.put("userId", userid);
                params.put("photo", trim);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ProfilePhotos.this);
        requestQueue.add(request);
    }

    public void getImagesReviews(final String user_id)
    {

        ringProgressDialog = ProgressDialog.show(this, "",	"Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_PROFILE_PHOTOS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        ringProgressDialog.dismiss();
                        parseJson(response);
                        adapter = new ImageGalleryAdapter(ProfilePhotos.this, review_list,"profile");
                        recyclerView.setAdapter(adapter);

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                ringProgressDialog.dismiss();

                if (error instanceof NoConnectionError)
                {
                    new DialogBox(ProfilePhotos.this, "No Internet Connection !", "Error",
                            "Error");
                }
                else if (error instanceof TimeoutError) {

                    new DialogBox(ProfilePhotos.this, "Connection Time Out Error", "Error",
                            "Error");
                }
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<>();
                SharedPreferences pref =getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id","");
                // params.put("revID",reviewId);
                params.put("userId",userid);
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
        review_list= new ArrayList<>();
        try {
            if(!response.equals("false")) {
                JSONArray inner = new JSONArray(response);
                for (int i = 0; i < inner.length(); i++) {
                    JSONObject innerobj = new JSONObject(inner.getString(i));
                    SpacePhoto spacePhoto = new SpacePhoto("", innerobj.getString("photo"));
                    spacePhoto.setmTitle(innerobj.getString("photo"));
                    spacePhoto.setIsPrimary(innerobj.getString("primary"));
                    spacePhoto.setProfileId(innerobj.getString("id"));
                    SharedPreferences pref = getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    if(innerobj.getString("primary").equals("1")){

                        editor.putString("profile_pic",innerobj.getString("photo"));
                        editor.apply();
                    }
                    editor.putString("total_photos",String.valueOf(inner.length()));
                    editor.apply();
                    review_list.add(spacePhoto);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

