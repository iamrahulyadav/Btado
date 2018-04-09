package com.cybussolutions.bataado.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.cybussolutions.bataado.Activities.DetailedImageActivtiy;
import com.cybussolutions.bataado.Activities.ProfilePhotos;
import com.cybussolutions.bataado.Model.SpacePhoto;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rizwan Jillani on 15-Feb-18.
 */

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>  {

    @Override
    public ImageGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.item_photo, parent, false);
        ImageGalleryAdapter.MyViewHolder viewHolder = new ImageGalleryAdapter.MyViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImageGalleryAdapter.MyViewHolder holder, int position) {

        SpacePhoto spacePhoto = mSpacePhotos.get(position);
        ImageView imageView = holder.mPhotoImageView;
        if(mtype.equals("gallery")) {
            Glide.with(mContext)
                    .load(End_Points.IMAGE_PROFILE_PIC + spacePhoto.getmTitle())
                    .apply(new RequestOptions().placeholder(R.drawable.error_center_x)
                    )
                    .into(imageView);
        }else {
            Glide.with(mContext)
                    .load(End_Points.IMAGE_PROFILE_PIC + spacePhoto.getmTitle())
                    .apply(new RequestOptions().placeholder(R.drawable.error_center_x)
                    )
                    .into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return (mSpacePhotos.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mPhotoImageView;

        MyViewHolder(View itemView) {

            super(itemView);
            mPhotoImageView = itemView.findViewById(R.id.iv_photo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION && mtype.equals("gallery")) {
                SpacePhoto spacePhoto = mSpacePhotos.get(position);
                Intent intent = new Intent(mContext, DetailedImageActivtiy.class);
                intent.putExtra(DetailedImageActivtiy.EXTRA_SPACE_PHOTO, spacePhoto.getmTitle());
                mContext.startActivity(intent);
            }else {
                SpacePhoto spacePhoto = mSpacePhotos.get(position);
                ImageDialog(mSpacePhotos.size(),position,spacePhoto.getmTitle(),spacePhoto.getIsPrimary(),spacePhoto.getProfileId(),spacePhoto.getmTitle());
            }
        }
    }

    private ArrayList<SpacePhoto> mSpacePhotos;
    private Activity mContext;
    private String mtype;
    private AlertDialog myalertdialog;

    public ImageGalleryAdapter(Activity context, ArrayList<SpacePhoto> review_list, String type) {
        mContext = context;
        mSpacePhotos = review_list;
        mtype=type;
    }
    private void ImageDialog(final int totalPics, final int pos, final String imageName, final String isPrimary, final String attachmentId, final String originalFileName) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.custom_diloag_download_image, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final int[] post = {pos};
        final ImageView image= dialogView.findViewById(R.id.imageLoaded);
        final TextView label= dialogView.findViewById(R.id.label);
        final Button makeCoverBtn= dialogView.findViewById(R.id.makeCoverBtn);
        final Button downloadImageBtn= dialogView.findViewById(R.id.downloadImageBtn);
        final ProgressBar progressBar= dialogView.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        downloadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Confirmation!")
                        .setConfirmText("OK").setContentText("Are You Sure to delete Photo. Would you like to continue?")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                myalertdialog.dismiss();
                                DeletePhoto(mSpacePhotos.get(pos).getProfileId(),pos);
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
        });
        if (mSpacePhotos.get(pos).getIsPrimary().equals("1")) {
           downloadImageBtn.setVisibility(View.GONE);
            makeCoverBtn.setText("Primary Photo");
        }
        else if (mSpacePhotos.get(pos).getIsPrimary().equals("0")) {
            downloadImageBtn.setVisibility(View.VISIBLE);
            makeCoverBtn.setText("Make Profile");
        }

        makeCoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(makeCoverBtn.getText().equals("Make Profile")) {
                    new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Confirmation!")
                            .setConfirmText("OK").setContentText("Are you sure to set primary photo. Would you like to continue?")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    myalertdialog.dismiss();
                                    SetPrimaryPhoto(mSpacePhotos.get(pos).getProfileId(), pos);
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

       image.setOnTouchListener(new OnSwipeTouchListener(mContext){
           @Override
           public void onSwipeLeft() {
               if(totalPics>1) {
                   int position = post[0] - 1;
                   post[0]--;
                   if (position < 0) {
                       position = totalPics - 1;
                       post[0] = totalPics - 1;
                   }
                   // progressBar.setVisibility(View.VISIBLE);
                   Glide.with(mContext)
                           .load(End_Points.IMAGE_PROFILE_PIC + mSpacePhotos.get(position).getmTitle())
                           .apply(new RequestOptions().override(mContext.getResources().getDimensionPixelSize(R.dimen.attachment_popup_width),mContext.getResources().getDimensionPixelSize(R.dimen.attachment_popup_height))
                                   .centerCrop().onlyRetrieveFromCache(true)
                           )
                           .listener(new RequestListener<Drawable>() {
                               @Override
                               public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                                   progressBar.setVisibility(View.GONE);
                                   return false;
                               }

                               @Override
                               public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                   progressBar.setVisibility(View.GONE);
                                   return false;
                               }

                           })
                           .into(image);

                   label.setText(mSpacePhotos.get(position).getmTitle());
                   if (mSpacePhotos.get(position).getIsPrimary().equals("1")) {
                       makeCoverBtn.setText("Primary Photo");
                       downloadImageBtn.setVisibility(View.GONE);
                   }
                   else if (mSpacePhotos.get(position).getIsPrimary().equals("0")) {
                       makeCoverBtn.setText("Make Profile");
                       downloadImageBtn.setVisibility(View.VISIBLE);
                   }
               }
           }

           @Override
           public void onSwipeRight() {
               int position=post[0]+1;
               post[0]++;
               if(position>totalPics-1) {
                   position = 0;
                   post[0]=0;
               }
               //   progressBar.setVisibility(View.VISIBLE);
               Glide.with(mContext)
                       .load(End_Points.IMAGE_PROFILE_PIC + mSpacePhotos.get(position).getmTitle())
                       .apply(new RequestOptions().override(mContext.getResources().getDimensionPixelSize(R.dimen.attachment_popup_width),mContext.getResources().getDimensionPixelSize(R.dimen.attachment_popup_height)).centerCrop().onlyRetrieveFromCache(true))
                       .listener(new RequestListener<Drawable>() {
                           @Override
                           public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                               progressBar.setVisibility(View.GONE);
                               return false;
                           }

                           @Override
                           public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                               progressBar.setVisibility(View.GONE);
                               return false;
                           }

                       })
                       .into(image);
               label.setText(mSpacePhotos.get(position).getmTitle());
               if (mSpacePhotos.get(position).getIsPrimary().equals("1")) {
                   makeCoverBtn.setText("Primary Photo");
                   downloadImageBtn.setVisibility(View.GONE);
               }
               else if (mSpacePhotos.get(position).getIsPrimary().equals("0")) {
                   makeCoverBtn.setText("Make Profile");
                   downloadImageBtn.setVisibility(View.VISIBLE);
               }
           }
       });
        Button cancelBtn= dialogView.findViewById(R.id.cancelBtn);
        label.setText(imageName);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.get(mContext).clearMemory();
                myalertdialog.dismiss();
            }
        });
        Glide.with(mContext)
                .load(End_Points.IMAGE_PROFILE_PIC + mSpacePhotos.get(pos).getmTitle())
                .apply(new RequestOptions().override(mContext.getResources().getDimensionPixelSize(R.dimen.attachment_popup_width),mContext.getResources().getDimensionPixelSize(R.dimen.attachment_popup_height)).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                })
                .into(image);
        myalertdialog = builder.create();
        myalertdialog.show();

    }

    private void DeletePhoto(final String photoId,final int pos) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(mContext, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.DELETE_PHOTO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ringProgressDialog.dismiss();
                        if(!response.equals("0")){
                            mSpacePhotos.remove(pos);
                            notifyDataSetChanged();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialog.dismiss();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(mContext,"Check your internet",Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {

                    Toast.makeText(mContext,"Check your internet",Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                SharedPreferences pref = mContext.getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
//                String userid = pref.getString("user_id", "");
//                params.put("userId", userid);
                params.put("photo_id", photoId);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(request);
    }

    private void SetPrimaryPhoto(final String photoid,final int pos) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(mContext, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.SET_PRIMARY_PHOTO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ringProgressDialog.dismiss();
                        if(!response.equals("0")){
                            for(int i=0;i<mSpacePhotos.size();i++){
                                mSpacePhotos.get(i).setIsPrimary("0");
                            }
                            mSpacePhotos.get(pos).setIsPrimary("1");
                            notifyDataSetChanged();
                            SharedPreferences pref = mContext.getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("profile_pic",mSpacePhotos.get(pos).getmTitle());
                            editor.apply();
                            Intent intent=new Intent(mContext, ProfilePhotos.class);
                            mContext.finish();
                            mContext.startActivity(intent);
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ringProgressDialog.dismiss();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(mContext,"Check your internet",Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {

                    Toast.makeText(mContext,"Check your internet",Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences pref = mContext.getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                params.put("userId", userid);
                params.put("photo_id", photoid);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(request);
    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureListener());
        }

        public void onSwipeLeft() {

        }

        public void onSwipeRight() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_DISTANCE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0)
                        onSwipeRight();
                    else
                        onSwipeLeft();
                    return true;
                }
                return false;
            }
        }
    }
}
