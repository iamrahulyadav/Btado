package com.cybussolutions.bataado.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.cybussolutions.bataado.Activities.Comments;
import com.cybussolutions.bataado.Activities.FullScreenVedioScreen;
import com.cybussolutions.bataado.Activities.HomeScreen;
import com.cybussolutions.bataado.Activities.MapsActivity;
import com.cybussolutions.bataado.Activities.OpenImage;
import com.cybussolutions.bataado.Activities.User_Profile;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.cybussolutions.bataado.Utils.Blur;
import com.cybussolutions.bataado.Utils.DialogBox;
import com.cybussolutions.bataado.Utils.TimeAgo;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.github.rtoshiro.view.video.FullscreenVideoLayout;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;


public class Home_Addapter extends ArrayAdapter<String> implements CallbackManager{

    private ArrayList<Home_Model> arraylist;
    private Activity context;
    private String reviewrating = "";
    private ImageView ivLike, ivShare, ivPhoto, ivComments;
    private TextView tvLikes;
    private TextView tvUnlikes;
    private TextView tvUseful;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    private CallbackManager callbackManager;
    int sharePosition=-1;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int WEEK_MILLIS = 7 * DAY_MILLIS;
    String activeContext;

    public Home_Addapter(Activity context, ArrayList<Home_Model> list,String activeContext) {
        super(context, R.layout.row_drawer);
        this.context = context;
        this.arraylist = list;
        this.activeContext=activeContext;
    }

    @Override
    public int getCount() {
        return arraylist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View rowView;


        final LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.row_home, null, true);

        final TextView username = rowView.findViewById(R.id.user_name_home);
        TextView place = rowView.findViewById(R.id.Page_home);
        final TextView viewMore = rowView.findViewById(R.id.showMore);
        final TextView like = rowView.findViewById(R.id.like_review);
        final TextView share = rowView.findViewById(R.id.share_review);
        final TextView commentToReview = rowView.findViewById(R.id.comment_to_review);
//        VideoView videoView = rowView.findViewById(R.id.video_view);
        ImageView ivDeleteReview = rowView.findViewById(R.id.deleteReview);
        final FullscreenVideoLayout videoView = rowView.findViewById(R.id.video_view);
        videoView.setActivity(context);
        TextView date = rowView.findViewById(R.id.time_home);
        final TextView comments = rowView.findViewById(R.id.adress);
        RatingBar rating = rowView.findViewById(R.id.ratingBar_home);
        ImageView profile_pic = rowView.findViewById(R.id.profile_image);
        ivLike = rowView.findViewById(R.id.like_image_view);
        ivShare = rowView.findViewById(R.id.share_image_view);
        ivPhoto = rowView.findViewById(R.id.image);
        ivComments = rowView.findViewById(R.id.comment_image_view);
        final Home_Model home_model = arraylist.get(position);
        username.setText(home_model.getFirstname() + " " + home_model.getLastname());
        place.setText(home_model.getBrand_name());
        comments.setText(home_model.getReview());
        commentToReview.setText(home_model.getTotalComments()+" Comment");
        share.setText(home_model.getTotalShares()+" Share");
        ImageButton imgFullScreen = videoView.findViewById(R.id.vcv_img_fullscreen);
        imgFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, FullScreenVedioScreen.class);
                intent.putExtra("title",home_model.getFile_path());
                context.startActivity(intent);
            }
        });
        commentToReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, Comments.class);
                HomeScreen.forCommentPos=position;
                intent.putExtra("reviewId",home_model.getReviewid());
                context.startActivity(intent);
            }
        });
        ivComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, Comments.class);
                HomeScreen.forCommentPos=position;
                intent.putExtra("reviewId",home_model.getReviewid());
                context.startActivity(intent);
            }
        });
        //  String check=comments.getText().toString();
        if (home_model.getReview().length() > 64 || home_model.getReview().contains("\n")) {
            viewMore.setVisibility(View.VISIBLE);
        } else {
            viewMore.setVisibility(View.GONE);
        }
        SharedPreferences pref = context.getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        final String currentuserid = pref.getString("user_id","");
        if(home_model.getUserid().equals(currentuserid)){
            ivDeleteReview.setVisibility(View.VISIBLE);
            ivDeleteReview.setBackground(context.getResources().getDrawable(R.drawable.ic_menu_delete));
        }else {
            if(!home_model.getInappropriate().equals("1")) {
                ivDeleteReview.setVisibility(View.VISIBLE);
                ivDeleteReview.setBackground(context.getResources().getDrawable(R.drawable.flag_blue));
            }else {
                ivDeleteReview.setVisibility(View.GONE);
            }
        }
        ivDeleteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentuserid.equals(home_model.getUserid())){
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Confirmation!")
                            .setConfirmText("OK").setContentText("Do you really want to delete?")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    DeleteReview(home_model.getReviewid(),"delete",position);
                                }
                            })
                            .setCancelText("Cancel")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();

                }else {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Confirmation!")
                            .setConfirmText("OK").setContentText("Mark Review as inappropriate")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    DeleteReview(home_model.getReviewid(),"Flag",position);
                                }
                            })
                            .setCancelText("Cancel")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();

                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!home_model.getShared().equals("1")) {
                    sharePosition = position;
                    onShareResult(view, home_model.getBrand_name(), home_model.getReviewid(),home_model.getFile_path(),home_model.getReview(),home_model.getMedia_type());
                }else {
                    Toast.makeText(context,"Already Shared !",Toast.LENGTH_LONG).show();
                }
            }
        });
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!home_model.getShared().equals("1")) {
                    sharePosition = position;
                    onShareResult(view, home_model.getBrand_name(), home_model.getReviewid(),home_model.getFile_path(),home_model.getReview(),home_model.getMedia_type());
                }else {
                    Toast.makeText(context,"Already Shared !",Toast.LENGTH_LONG).show();
                }
            }
        });
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] location = new int[2];
                like.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                dialog_like_review(home_model.getReviewid(), home_model.getIcon(), x - 80, y - 210, position);
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] location = new int[2];
                like.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                dialog_like_review(home_model.getReviewid(), home_model.getIcon(), x - 80, y - 210, position);
            }
        });
        viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewMore.getText().toString().equals("View More")) {
                    comments.setMaxLines(Integer.MAX_VALUE);
                    viewMore.setText("View Less");
                } else {
                    comments.setMaxLines(2);
                    comments.setEllipsize(TextUtils.TruncateAt.END);
                    viewMore.setText("View More");
                }
            }
        });
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),OpenImage.class);
                intent.putExtra("title",home_model.getFile_path());
               context.startActivity(intent);
            }
        });
        if (!home_model.getMedia_type().equals("") && !home_model.getFile_path().equals("") && ! home_model.getMedia_type().equals("video")) {
            ivPhoto.setVisibility(View.VISIBLE);
           /* Picasso.with(context)
                    .load(End_Points.IMAGE_RREVIEW_URL + arraylist.get(position).getFile_path()) // thumbnail url goes here
                    .placeholder(R.drawable.progress_animation)
                    .transform(blurTransformation)
                    .into(ivPhoto, new Callback() {
                        @Override
                        public void onSuccess() {*/
                            Picasso.with(context)
                                    .load(End_Points.IMAGE_RREVIEW_URL + home_model.getFile_path())
                                    .resize(400,200)
                                    .centerCrop()// image url goes here
                                    .placeholder(R.drawable.progress_animation)
                                    .into(ivPhoto);
                       /* }

                        @Override
                        public void onError() {
                        }
                    });*/
           /* Picasso.with(getContext())
                    .load(End_Points.IMAGE_RREVIEW_URL + home_model.getFile_path())
                    .into(ivPhoto);*/
        } else {
            ivPhoto.setVisibility(View.GONE);
        }
        if (!home_model.getMedia_type().equals("") && !home_model.getFile_path().equals("") && home_model.getMedia_type().equals("video")) {

            videoView.setVisibility(View.VISIBLE);
            Uri videoUri = Uri.parse(End_Points.IMAGE_RREVIEW_URL+home_model.getFile_path());
            try {
                videoView.setVideoURI(videoUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
           // videoView.setVideoPath(End_Points.IMAGE_RREVIEW_URL+home_model.getFile_path()).setFingerprint(position);
        } else {
            videoView.setVisibility(View.GONE);
        }
       /* try {
            long now = System.currentTimeMillis();
           // String datetime1 = "06/12/2015 03:58 PM";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
            Date convertedDate = dateFormat.parse(home_model.getDate_created());

            CharSequence relavetime1 = DateUtils.getRelativeTimeSpanString(
                    convertedDate.getTime(),
                    now,
                    DateUtils.SECOND_IN_MILLIS);

            date.append(relavetime1);
          //  System.out.println(relavetime1);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        //timeago
       /* String[] dueDateSplit=home_model.getDate_created().split("-");
        String dayofmonth[]=dueDateSplit[2].split(" ");
        String timer[]=dayofmonth[1].split(":");
        int dayOfMonth = Integer.parseInt(dayofmonth[0]);
        int month = Integer.parseInt(dueDateSplit[1]);
        int year = Integer.parseInt(dueDateSplit[0]);
        int hour=Integer.parseInt(timer[0]);

        int minute=Integer.parseInt(timer[1]);
        int second=Integer.parseInt(timer[2]);

        Calendar cal = Calendar.getInstance(tz);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.MONTH, month-1);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.HOUR,hour);
        cal.set(Calendar.MINUTE,minute);
        cal.set(Calendar.SECOND,second);
        long time = cal.getTimeInMillis();*/
       // String timeago=getTimeAgo(time,home_model.getDate_created());
        //date.setText(timeago);
        TimeZone tz = TimeZone.getTimeZone("Asia/Karachi");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/M/yyyy HH:mm:ss",Locale.US);
//        outputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date myDate = null;
       // myDate = cal.getTime();

        //        date.setReferenceTime(time);
        try {
            myDate=dateFormat.parse(home_model.getDate_created());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TimeAgo timeAgo = new TimeAgo().locale(context).with(dateFormat);
        String result = timeAgo.getTimeAgo(myDate);
        date.setText(result);

        //date.setText(home_model.getDate_created());
        int testrating = 0;

        reviewrating = home_model.getRating();

        if (reviewrating == null) {
            rating.setRating(testrating);
        } else {
            testrating = Integer.parseInt(home_model.getRating());
            rating.setRating(testrating);

        }
        if(home_model.getShared().equals("1")){
            share.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            ivShare.setBackground(context.getResources().getDrawable(R.drawable.arrow_icon_green));
        }else {
            share.setTextColor(context.getResources().getColor(R.color.message_color));
            ivShare.setBackground(context.getResources().getDrawable(R.drawable.arrow_icon));
        }
        switch (home_model.getIcon()) {
            case "1":
                ivLike.setBackground(getContext().getResources().getDrawable(R.drawable.thumb_on));
                like.setText(home_model.getTotalLikes() + " Like");
                break;
            case "2":
                ivLike.setBackground(getContext().getResources().getDrawable(R.drawable.thumb_down_button_on));
                like.setText(home_model.getTotalLikes() + " Dislike");
                break;
            case "3":
                ivLike.setBackground(getContext().getResources().getDrawable(R.drawable.useful_on));
                like.setText(home_model.getTotalLikes() + " Useful");
                break;
            default:
                ivLike.setBackground(getContext().getResources().getDrawable(R.drawable.hand_icon));
                like.setText(home_model.getTotalLikes() + " Like");
                break;
        }

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), User_Profile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("username", username.getText().toString());
                intent.putExtra("userID", home_model.getUserid());
                getContext().startActivity(intent);
            }
        });

        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("brandNAme", home_model.getBrand_name());
                intent.putExtra("brandId", home_model.getBrandid());
                intent.putExtra("brandRating", home_model.getBrand_raiting());
                intent.putExtra("brandAdress",home_model.getArea()+" "+home_model.getBlock());
                intent.putExtra("phone",home_model.getPhone());
                intent.putExtra("websiteUrl",home_model.getWebsite_url());
                intent.putExtra("latitude",home_model.getBrand_latitude());
                intent.putExtra("longitude",home_model.getBrand_longitude());
                intent.putExtra("email",home_model.getEmail_brand());
                intent.putExtra("brandPic", home_model.getBrand_logo());
                //intent.putExtra("reviewCount",home_model.getNum_review());
                getContext().startActivity(intent);
            }
        });

        String pp = home_model.getProfilepic();


        if (pp.startsWith("https://graph.facebook.com/") || pp.startsWith("https://fb-s-b-a.akamaihd.net/")|| pp.startsWith("https://scontent.xx.fbcdn.net/") || pp.startsWith("http://graph.facebook.com/")) {
            Picasso.with(getContext())
                    .load(pp)
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(profile_pic);
        } else {
            if (pp.equals("")) {
                Picasso.with(getContext())
                        .load("http://bataado.cybussolutions.com/uploads/no-image-icon-hi.png")
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(profile_pic);
            } else {
                Picasso.with(getContext())
                        .load(End_Points.IMAGE_PROFILE_PIC + pp)
                        .resize(150, 150)
                        .centerCrop().transform(new CircleTransform())
                        .into(profile_pic);
            }


        }


                /*Counter_Model counter_model = counter_list.get(position);
                Likes.setText(counter_model.getLiked());
                dislike.setText(counter_model.getDislikes());
                comments_count.setText(counter_model.getComment());
                userfull.setText(counter_model.getUsefull());
                share.setText(counter_model.getShares());*/

        return rowView;
    }
    public static String getTimeAgo(long time,String date) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//        long now=cal.getTimeInMillis();
        Time today = new Time();
        today.setToNow();
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else if (diff < 7 * DAY_MILLIS) {
            return diff / DAY_MILLIS + " days ago";
        } else if (diff < 2 * WEEK_MILLIS) {
            return "a week ago";
        } else {
            return date;
        }
    }
    private void DeleteReview(final String reviewid, final String flag, final int position) {
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.DELETE_REVIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Review Deleted Successfully") && flag.equals("delete")){
                            arraylist.remove(position);
                            notifyDataSetChanged();
                            if(activeContext.equals("User_Profile"))
                            ((User_Profile)context).OnDelete();
                        }else if(response.equals("flag") && flag.equals("Flag")){
                            arraylist.get(position).setInappropriate("1");
                            notifyDataSetChanged();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    Toast.makeText(context,"Check your internet",Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {

                    Toast.makeText(context,"Check your internet",Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = context.getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                params.put("userId", userid);
                params.put("reviewId", reviewid);
                params.put("flag", flag);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }


    private void onShareResult(View view, final String brandName, String reviewId, final String brandLogo, final String review,final String mediaType){

        final ShareDialog shareDialog = new ShareDialog(context);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
          /*  final ImageView imageView=new ImageView(context);
            Picasso.with(context).load(End_Points.IMAGE_RREVIEW_URL+brandLogo).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap  bitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.email_icon)).getBitmap();*/
                    SharePhoto photo = new SharePhoto.Builder().setImageUrl(Uri.parse(End_Points.IMAGE_RREVIEW_URL+brandLogo))
                            .setUserGenerated(true).build();
            ShareOpenGraphObject object;
                    if(mediaType.equals("")){
                         object = new ShareOpenGraphObject.Builder()
                                .putString("og:type", "article")
                                .putString("og:url","http://demo.cybussolutions.com/bataado")
                                .putString("og:title", brandName)
                                .putString("og:description", review)
                                //.putString("books:isbn", "0-553-57340-3")
                                .build();
                    }else if(!mediaType.equals("video")){
                         object = new ShareOpenGraphObject.Builder()
                                .putString("og:type", "article")
                                .putString("og:url", "http://demo.cybussolutions.com/bataado")
                                .putString("og:title", brandName)
                                .putString("og:description", review)
                                .putPhoto("og:image", photo)
                                //.putString("books:isbn", "0-553-57340-3")
                                .build();
                    }else {
                        object = new ShareOpenGraphObject.Builder()
                                .putString("og:type", "article")
                                .putString("og:url", "http://demo.cybussolutions.com/bataado")
                                .putString("og:title", brandName)
                                .putString("og:description", "video")
                                //.putPhoto("og:image", photo)
                                //.putString("books:isbn", "0-553-57340-3")
                                .build();
                    }

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
                }

               /* @Override
                public void onError() {
                    Toast.makeText(context,"Please try again something wrong",Toast.LENGTH_LONG).show();
                }
            });*/


          /*  ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Bataado")
                    .setContentDescription("Bataado")
                    .setContentUrl(Uri.parse("http://demo.cybussolutions.com/bataado"))
                    .setImageUrl(Uri.parse(End_Points.IMAGE_BASE_URL+brandLogo))
                    .build();*/



    }
      private FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
          @Override
          public void onSuccess(Sharer.Result result) {
              Toast.makeText(context,"Successfully posted",Toast.LENGTH_LONG).show();
              shareReview(sharePosition,arraylist.get(sharePosition).getReviewid());

              // Write some code to do some operations when you shared content successfully.
          }

          @Override
          public void onCancel() {
              Toast.makeText(context,"Cancelled",Toast.LENGTH_LONG).show();
              // Write some code to do some operations when you cancel sharing content.
          }

          @Override
          public void onError(FacebookException error) {
              Toast.makeText(context, error.getMessage(),Toast.LENGTH_LONG).show();
              // Write some code to do some operations when some error occurs while sharing content.
          }
      };

    private void shareReview(final int sharePosition,final String reviewId) {
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.SHARE_REVIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("shared")) {
                            int totalshares=Integer.parseInt(arraylist.get(sharePosition).getTotalShares());
                            totalshares++;
                            arraylist.get(sharePosition).setTotalShares(String.valueOf(totalshares));
                            arraylist.get(sharePosition).setShared("1");
                            notifyDataSetChanged();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    new DialogBox(context, "No Internet Connection !", "Error",
                            "Error");
                } else if (error instanceof TimeoutError) {
                    new DialogBox(context, "Connection Time Out Error", "Error",
                            "Error");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                params.put("reviewId", reviewId);
                params.put("userId", userid);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }

    private Transformation blurTransformation = new Transformation() {
        @Override
        public Bitmap transform(Bitmap source) {
            Bitmap blurred = Blur.fastblur(context, source, 10);
            source.recycle();
            return blurred;
        }

        @Override
        public String key() {
            return "blur()";
        }
    };

    private void dialog_like_review(final String reviewid, final String icon, int x, int y, final int pos) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.custom_dialog_like_review, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // etPost = (EditText) customView.findViewById(R.id.et_post);
        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = x;
        wmlp.y = y;
        final LikeButton likeBtn = customView.findViewById(R.id.thumb_button);
        final LikeButton unlikeBtn = customView.findViewById(R.id.thumb_button_unlike);
        final LikeButton usefulBtn = customView.findViewById(R.id.heart_button);
        tvLikes = customView.findViewById(R.id.tvNumLikes);
        tvUnlikes = customView.findViewById(R.id.tvNumUnLikes);
        tvUseful = customView.findViewById(R.id.tvNumUsefull);
        if (icon.equals("1")) {
            likeBtn.setLiked(true);
            unlikeBtn.setLiked(false);
            usefulBtn.setLiked(false);
            // likeBtn.setBackground(getContext().getResources().getDrawable(R.drawable.thumb_on));
            //unlikeBtn.setBackground(getContext().getResources().getDrawable(R.drawable.thumb_down_button_off));
            //usefulBtn.setBackground(getContext().getResources().getDrawable(R.drawable.heart_off));
        } else if (icon.equals("2")) {
            likeBtn.setLiked(false);
            unlikeBtn.setLiked(true);
            usefulBtn.setLiked(false);
           /* likeBtn.setBackground(getContext().getResources().getDrawable(R.drawable.thumb_off));
            unlikeBtn.setBackground(getContext().getResources().getDrawable(R.drawable.thumb_down_button_on));
            usefulBtn.setBackground(getContext().getResources().getDrawable(R.drawable.heart_off));*/
        } else if (icon.equals("3")) {
            likeBtn.setLiked(false);
            unlikeBtn.setLiked(false);
            usefulBtn.setLiked(true);
          /*  usefulBtn.setBackground(getContext().getResources().getDrawable(R.drawable.heart_on));
            likeBtn.setBackground(getContext().getResources().getDrawable(R.drawable.thumb_off));
            unlikeBtn.setBackground(getContext().getResources().getDrawable(R.drawable.thumb_down_button_off));*/
        } else {
            likeBtn.setLiked(false);
            unlikeBtn.setLiked(false);
            usefulBtn.setLiked(false);
           /* likeBtn.setBackground(getContext().getResources().getDrawable(R.drawable.thumb_off));
            unlikeBtn.setBackground(getContext().getResources().getDrawable(R.drawable.thumb_down_button_off));
            usefulBtn.setBackground(getContext().getResources().getDrawable(R.drawable.heart_off));*/
        }
        likeBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                alertDialog.dismiss();
                likeReview(reviewid, "enableLike", pos, likeBtn, unlikeBtn, usefulBtn);
//                arraylist.get(pos).setIcon("1");
//                notifyDataSetChanged();
                likeBtn.setLiked(true);
                unlikeBtn.setLiked(false);
                usefulBtn.setLiked(false);
                //  ivLike.setBackground(getContext().getResources().getDrawable(R.drawable.like));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                alertDialog.dismiss();
                likeReview(reviewid, "disableLike", pos, likeBtn, unlikeBtn, usefulBtn);
//                arraylist.get(pos).setIcon("0");
//                notifyDataSetChanged();
                likeBtn.setLiked(false);
                unlikeBtn.setLiked(false);
                usefulBtn.setLiked(false);

                //  ivLike.setBackground(getContext().getResources().getDrawable(R.drawable.heart_icon));
            }
        });
        unlikeBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                alertDialog.dismiss();
                unlikeReview(reviewid, "enableDislike", pos, likeBtn, unlikeBtn, usefulBtn);
                /*arraylist.get(pos).setIcon("2");
                notifyDataSetChanged();*/
                likeBtn.setLiked(false);
                unlikeBtn.setLiked(true);
                usefulBtn.setLiked(false);

                // ivLike.setBackground(getContext().getResources().getDrawable(R.drawable.unlike));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                alertDialog.dismiss();
                unlikeReview(reviewid, "disableDislike", pos, likeBtn, unlikeBtn, usefulBtn);
               /* arraylist.get(pos).setIcon("0");
                notifyDataSetChanged();*/
                likeBtn.setLiked(false);
                unlikeBtn.setLiked(false);
                usefulBtn.setLiked(false);

                //ivLike.setBackground(getContext().getResources().getDrawable(R.drawable.heart_icon));
            }
        });
        usefulBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                alertDialog.dismiss();
                usefulReview(reviewid, "enableUseful", pos, likeBtn, unlikeBtn, usefulBtn);
                /*arraylist.get(pos).setIcon("3");
                notifyDataSetChanged();*/
                likeBtn.setLiked(false);
                unlikeBtn.setLiked(false);
                usefulBtn.setLiked(true);

                //ivLike.setBackground(getContext().getResources().getDrawable(R.drawable.heart));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                alertDialog.dismiss();
                usefulReview(reviewid, "disableUseful", pos, likeBtn, unlikeBtn, usefulBtn);
               /* arraylist.get(pos).setIcon("0");
                notifyDataSetChanged();*/
                likeBtn.setLiked(false);
                unlikeBtn.setLiked(false);
                usefulBtn.setLiked(false);

                //ivLike.setBackground(getContext().getResources().getDrawable(R.drawable.heart_icon));
            }
        });

        alertDialog.setView(customView);
        alertDialog.show();
        alertDialog.getWindow().setLayout(550, 160);
        getReaction(reviewid, "like");
    }

    private void usefulReview(final String reviewid, final String action, final int pos, LikeButton likeButton, LikeButton unlikeButton, LikeButton usefulButton) {
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.USEFUL_REVIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("1")) {
                            if (action.equals("enableUseful")) {
                                int updated = Integer.parseInt(String.valueOf(tvUseful.getText())) + 1;
                                tvUseful.setText(String.valueOf(updated));
                                if (Integer.parseInt(String.valueOf(tvUnlikes.getText())) > 0) {
                                    int updated2 = Integer.parseInt(String.valueOf(tvUnlikes.getText())) - 1;
                                    tvUnlikes.setText(String.valueOf(updated2));
                                }
                                if (Integer.parseInt(tvLikes.getText().toString()) > 0) {
                                    int updated1 = Integer.parseInt(String.valueOf(tvLikes.getText())) - 1;
                                    tvLikes.setText(String.valueOf(updated1));
                                }
                                if(arraylist.get(pos).getIcon().equals("0")) {
                                    int likes = arraylist.get(pos).getTotalLikes() + 1;
                                    arraylist.get(pos).setTotalLikes(likes);
                                }
                                arraylist.get(pos).setIcon("3");
                            } else {
                                if (Integer.parseInt(tvUseful.getText().toString()) > 0) {
                                    int updated = Integer.parseInt(String.valueOf(tvUseful.getText())) - 1;
                                    tvUseful.setText(String.valueOf(updated));
                                }
                                arraylist.get(pos).setIcon("0");
                                int likes=arraylist.get(pos).getTotalLikes()-1;
                                arraylist.get(pos).setTotalLikes(likes);
                            }
                            notifyDataSetChanged();
                        } else if (response.equals("Their is a probem,Comment Not Marked Useful Successfully")) {
                            Toast.makeText(getContext(), "Their is a problem,Comment Not Marked Useful Successfully", Toast.LENGTH_LONG).show();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    new DialogBox(context, "No Internet Connection ! ", "Error",
                            "Error");
                    /*new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setConfirmText("OK").setContentText("No Internet Connection ! ")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();

                                }
                            })
                            .show();*/
                } else if (error instanceof TimeoutError) {
                    new DialogBox(context, "Connection Time Out Error", "Error",
                            "Error");
                   /* new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                params.put("revID", reviewid);
                params.put("userId", userid);
                params.put("action", action);
                // params.put("react",reaction);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }

    private void unlikeReview(final String reviewid, final String action, final int pos, LikeButton likeButton, LikeButton unlikeButton, LikeButton usefulButton) {
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.DIS_LIKE_REVIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("1")) {
                            if (action.equals("enableDislike")) {
                                int updated = Integer.parseInt(String.valueOf(tvUnlikes.getText())) + 1;
                                tvUnlikes.setText(String.valueOf(updated));
                                if (Integer.parseInt(String.valueOf(tvLikes.getText())) > 0) {
                                    int updated2 = Integer.parseInt(String.valueOf(tvLikes.getText())) - 1;
                                    tvLikes.setText(String.valueOf(updated2));
                                }
                                if (Integer.parseInt(tvUseful.getText().toString()) > 0) {
                                    int updated1 = Integer.parseInt(String.valueOf(tvUseful.getText())) - 1;
                                    tvUseful.setText(String.valueOf(updated1));
                                }
                                if(arraylist.get(pos).getIcon().equals("0")) {
                                    int likes=arraylist.get(pos).getTotalLikes()+1;
                                    arraylist.get(pos).setTotalLikes(likes);
                                }
                                arraylist.get(pos).setIcon("2");

                            } else {
                                if (Integer.parseInt(String.valueOf(tvUnlikes.getText())) > 0) {
                                    int updated = Integer.parseInt(String.valueOf(tvUnlikes.getText())) - 1;
                                    tvUnlikes.setText(String.valueOf(updated));
                                }
                                arraylist.get(pos).setIcon("0");
                                int likes=arraylist.get(pos).getTotalLikes()-1;
                                arraylist.get(pos).setTotalLikes(likes);
                            }
                            notifyDataSetChanged();
                        } else if (response.equals("Their is a probem,Comment Not DisLiked Successfully")) {
                            Toast.makeText(getContext(), "Their is a problem,Comment Not DisLiked Successfully", Toast.LENGTH_LONG).show();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    Toast.makeText(context,"No Internet Connection !",Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(context,"Connection Time Out Error",Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                params.put("revID", reviewid);
                params.put("userId", userid);
                params.put("action", action);
                // params.put("react",reaction);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }

    private void likeReview(final String reviewId, final String action, final int pos, LikeButton likeButton, LikeButton unlikeButton, LikeButton usefulButton) {
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.LIKE_REVIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("1")) {
                            if (action.equals("enableLike")) {
                                int updated = Integer.parseInt(String.valueOf(tvLikes.getText())) + 1;
                                tvLikes.setText(String.valueOf(updated));
                                if (Integer.parseInt(String.valueOf(tvUnlikes.getText())) > 0) {
                                    int updated2 = Integer.parseInt(String.valueOf(tvUnlikes.getText())) - 1;
                                    tvUnlikes.setText(String.valueOf(updated2));
                                }
                                if (Integer.parseInt(tvUseful.getText().toString()) > 0) {
                                    int updated1 = Integer.parseInt(String.valueOf(tvUseful.getText())) - 1;
                                    tvUseful.setText(String.valueOf(updated1));
                                }
                                if(arraylist.get(pos).getIcon().equals("0")) {
                                    int likes = arraylist.get(pos).getTotalLikes() + 1;
                                    arraylist.get(pos).setTotalLikes(likes);
                                }
                                arraylist.get(pos).setIcon("1");

                            } else {
                                if (Integer.parseInt(String.valueOf(tvLikes.getText())) > 0) {
                                    int updated = Integer.parseInt(String.valueOf(tvLikes.getText())) - 1;
                                    tvLikes.setText(String.valueOf(updated));
                                }
                                arraylist.get(pos).setIcon("0");
                                int likes=arraylist.get(pos).getTotalLikes()-1;
                                arraylist.get(pos).setTotalLikes(likes);
                            }

                            notifyDataSetChanged();
                        } else if (response.equals("Their is a probem,Comment Not Like Successfully")) {
                            Toast.makeText(getContext(), "There is a problem,Comment Not Like Successfully", Toast.LENGTH_LONG).show();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    Toast.makeText(context,"No Internet Connection !",Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                   Toast.makeText(context,"Connection Time Out Error",Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                params.put("revID", reviewId);
                params.put("userId", userid);
                params.put("action", action);
                // params.put("react",reaction);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }

    public void getReaction(final String reviewid, final String reaction) {
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.GET_REACTIONS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        JSONObject outer;
                        try {
                            outer = new JSONObject(response);
                            if (!outer.get("likes").equals(false)) {
                                // JSONArray likes = outer.getJSONArray("likes");
                                tvLikes.setText(String.valueOf(outer.get("likes")));
                            } else {
                                tvLikes.setText("0");
                            }
                            if (!outer.get("numDis").equals(false)) {
                                // JSONArray dislikes = outer.getJSONArray("dislikes");
                                tvUnlikes.setText(String.valueOf(outer.get("numDis")));
                            } else {
                                tvUnlikes.setText("0");
                            }
                            if (!outer.get("numUse").equals(false)) {
                                //  JSONArray useful = outer.getJSONArray("useful");
                                tvUseful.setText(String.valueOf(outer.get("numUse")));
                            } else {
                                tvUseful.setText("0");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    Toast.makeText(context,"No Internet Connection !",Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(context,"Connection Time Out Error",Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = getContext().getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                params.put("revID", reviewid);
                // params.put("react",reaction);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        return false;
    }
}
