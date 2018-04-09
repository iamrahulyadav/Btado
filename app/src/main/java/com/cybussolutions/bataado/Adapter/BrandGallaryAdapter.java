package com.cybussolutions.bataado.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cybussolutions.bataado.Activities.DetailGallery;
import com.cybussolutions.bataado.Activities.DetailedImageActivtiy;
import com.cybussolutions.bataado.Model.SpacePhoto;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.github.rtoshiro.view.video.FullscreenVideoLayout;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Rizwan Jillani on 02-Mar-18.
 */

public class BrandGallaryAdapter extends RecyclerView.Adapter<BrandGallaryAdapter.MyViewHolder>  {

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.item_brand_gallery, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        SpacePhoto spacePhoto = mSpacePhotos.get(position);
       // ImageView imageView = holder.mPhotoImageView;
       // FullscreenVideoLayout videoView=holder.videoView;
        if(spacePhoto.getMediaType().equals("picture")) {
            holder.videoView.setVisibility(View.GONE);
            holder.videoThumb.setVisibility(View.GONE);
            holder.mPhotoImageView.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(End_Points.BRAND_GALLERY + spacePhoto.getmTitle())
                    .apply(new RequestOptions().placeholder(R.drawable.error_center_x)
                    )
                    .into(holder.mPhotoImageView);
        }else if(spacePhoto.getMediaType().equals("video")){
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoThumb.setVisibility(View.VISIBLE);
            holder.mPhotoImageView.setVisibility(View.GONE);
            Uri videoUri = Uri.parse(End_Points.BRAND_VIDEO+spacePhoto.getmTitle());
            holder.videoView.setVideoURI(videoUri);

        }

    }

    @Override
    public int getItemCount() {
        return (mSpacePhotos.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mPhotoImageView,videoThumb;
        VideoView videoView;
        MyViewHolder(View itemView) {

            super(itemView);
            mPhotoImageView = itemView.findViewById(R.id.iv_photo);
            videoThumb = itemView.findViewById(R.id.thumbNail);
            videoView = itemView.findViewById(R.id.video_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                SpacePhoto spacePhoto = mSpacePhotos.get(position);
                Intent intent = new Intent(mContext, DetailGallery.class);
                intent.putExtra("title", spacePhoto.getmTitle());
                intent.putExtra("media", spacePhoto.getMediaType());
                mContext.startActivity(intent);
            }
        }
    }

    private ArrayList<SpacePhoto> mSpacePhotos;
    private Activity mContext;
    private String mtype;
    private AlertDialog myalertdialog;

    public BrandGallaryAdapter(Activity context, ArrayList<SpacePhoto> review_list, String type) {
        mContext = context;
        mSpacePhotos = review_list;
        mtype=type;
    }


}
