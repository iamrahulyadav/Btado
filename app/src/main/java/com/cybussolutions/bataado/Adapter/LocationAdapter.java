package com.cybussolutions.bataado.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.cybussolutions.bataado.Activities.EditLocation;
import com.cybussolutions.bataado.Activities.ProfilePhotos;
import com.cybussolutions.bataado.Model.Home_Model;
import com.cybussolutions.bataado.Model.Locations_Model;
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

public class LocationAdapter extends ArrayAdapter<String> {
    private ArrayList<Locations_Model> mSpacePhotos;
    private Activity mContext;
    private String mtype;
    private AlertDialog myalertdialog;
    public LocationAdapter(Activity context, ArrayList<Locations_Model> review_list, String type) {
        super(context, R.layout.row_drawer);
        mContext = context;
        mSpacePhotos = review_list;
        mtype=type;
    }

    @Override
    public int getCount() {
        return mSpacePhotos.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @SuppressLint({"InflateParams", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View itemView;

        final LayoutInflater inflater = mContext.getLayoutInflater();
        itemView = inflater.inflate(R.layout.item_locations, null, true);
        final TextView name=itemView.findViewById(R.id.name);
        TextView city=itemView.findViewById(R.id.city);
        TextView address=itemView.findViewById(R.id.address);
        TextView primary=itemView.findViewById(R.id.primary);
        ImageView deleteLocation=itemView.findViewById(R.id.deleteLocaton);
        ImageView editLocation=itemView.findViewById(R.id.editLocaton);
        name.setText(Html.fromHtml("<b>"+"Location Name    : "+"</b>"+mSpacePhotos.get(position).getName()));
        city.setText(Html.fromHtml("<b>"+"City        : "+"</b>"+mSpacePhotos.get(position).getCity()));
        address.setText(Html.fromHtml("<b>"+"Address : "+"</b>"+mSpacePhotos.get(position).getAddress()));
        deleteLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Confirmation!")
                        .setConfirmText("OK").setContentText("Are You Sure to delete Location. Would you like to continue?")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                              //  myalertdialog.dismiss();
                                DeleteLocation(mSpacePhotos.get(position).getId(),position);
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
        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, EditLocation.class);
                intent.putExtra("location_id",mSpacePhotos.get(position).getId());
                intent.putExtra("location_name",mSpacePhotos.get(position).getName());
                intent.putExtra("location_city",mSpacePhotos.get(position).getCity());
                intent.putExtra("location_area",mSpacePhotos.get(position).getArea());
                intent.putExtra("location_address",mSpacePhotos.get(position).getAddress());
                mContext.finish();
                mContext.startActivity(intent);
            }
        });
        if(mSpacePhotos.get(position).getPrimary().equals("1")) {
            primary.setText(Html.fromHtml(/*"<b>"+"Primary  : " +"</b>"+ */"Primary"));
            primary.setTextColor(mContext.getResources().getColor(R.color.black));
            deleteLocation.setVisibility(View.GONE);
            primary.setClickable(false);
        }
        else {
            primary.setClickable(true);
            primary.setText("Set as Primary");
            primary.setTextColor(mContext.getResources().getColor(R.color.navy));
            deleteLocation.setVisibility(View.VISIBLE);
        }
        primary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mSpacePhotos.get(position).getPrimary().equals("1")){
                    new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Confirmation!")
                            .setConfirmText("OK").setContentText("Are you sure to set primary Location. Would you like to continue?")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                  //  myalertdialog.dismiss();
                                    SetPrimary(mSpacePhotos.get(position).getId(),position);
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
        return itemView;
    }

    private void SetPrimary(final String id, final int position) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(mContext, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.SET_PRIMARY_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ringProgressDialog.dismiss();
                        if(!response.equals("0")){
                            for(int i=0;i<mSpacePhotos.size();i++){
                                mSpacePhotos.get(i).setPrimary("0");
                            }
                            mSpacePhotos.get(position).setPrimary("1");
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
                SharedPreferences pref = mContext.getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
                String userid = pref.getString("user_id", "");
                params.put("userId", userid);
                params.put("location_id", id);
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

    private void DeleteLocation(final String id, final int position) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(mContext, "", "Please wait ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.DELETE_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ringProgressDialog.dismiss();
                        if(!response.equals("0")){
                            mSpacePhotos.remove(position);
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
                params.put("location_id", id);
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




}
