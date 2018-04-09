package com.cybussolutions.bataado.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.cybussolutions.bataado.Activities.Detail_brand;
import com.cybussolutions.bataado.Activities.HomeScreen;
import com.cybussolutions.bataado.Helper.CircleTransform;
import com.cybussolutions.bataado.Model.Brands_Model;
import com.cybussolutions.bataado.Model.Comment_Model;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rizwan Jillani on 22-Feb-18.
 */

public class Comments_Adapter extends ArrayAdapter<String>
{

    private ArrayList<Comment_Model> arraylist;
    private Activity context;
    private String reviewId;
    public Comments_Adapter(Activity context, ArrayList<Comment_Model> list,String reviewId)
    {
        super(context, R.layout.row_drawer);
        this.context = context;
        this.arraylist = list;
        this.reviewId=reviewId;
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
    public View getView(final int position, View convertView, @NonNull ViewGroup parent)
    {
        View rowView;

        final LayoutInflater inflater = context.getLayoutInflater();
        rowView = inflater.inflate(R.layout.row_comments,null,true);
        CircleImageView profile_pic =rowView.findViewById(R.id.profile_image);
        TextView userName=rowView.findViewById(R.id.user_name);
        final ImageView delete=rowView.findViewById(R.id.delete);
        final Comment_Model comment_model=arraylist.get(position);
        String pp=comment_model.getCommentByProfile();
        userName.setText(Html.fromHtml("<b>" +comment_model.getCommentBy()+ "</b> "+"   "+comment_model.getComment()));
        if (pp.startsWith("https://graph.facebook.com/")|| pp.startsWith("https://scontent.xx.fbcdn.net/") || pp.startsWith("http://graph.facebook.com/")) {
            Picasso.with(getContext())
                    .load(pp)
                    .resize(150, 150)
                    .centerCrop().transform(new CircleTransform())
                    .into(profile_pic);
        } else {
            if (pp.equals("")) {
                Picasso.with(getContext())
                        .load(End_Points.IMAGE_RREVIEW_URL+"no-image-icon-hi.png")
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
        SharedPreferences pref = context.getSharedPreferences("BtadoPrefs", MODE_PRIVATE);
        final String strid = pref.getString("user_id","");
        if(strid.equals(comment_model.getCommentById())){
          delete.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_menu_delete));
        }else {
            if(!comment_model.getInappropriate().equals("1")) {
                delete.setImageDrawable(context.getResources().getDrawable(R.drawable.flag));
            }else {
                delete.setVisibility(View.GONE);
            }
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(strid.equals(comment_model.getCommentById())){
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Confirmation!")
                            .setConfirmText("OK").setContentText("Do you really want to delete?")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    DeleteComment(comment_model.getCommentId(),"delete",position);
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
                            .setConfirmText("OK").setContentText("Mark Comment as inappropriate")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    DeleteComment(comment_model.getCommentId(),"Flag",position);
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

        return rowView;
    }
    private void DeleteComment(final String commentId,final String flag,final int position) {
        StringRequest request = new StringRequest(Request.Method.POST, End_Points.DELETE_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equals("0") && flag.equals("delete")){
                            arraylist.remove(position);
                            notifyDataSetChanged();
                            HomeScreen.noOfComments=String.valueOf(arraylist.size());
                        }else if(!response.equals("0") && flag.equals("flag")){
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
                params.put("commentid", commentId);
                params.put("revID", reviewId);
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
}

