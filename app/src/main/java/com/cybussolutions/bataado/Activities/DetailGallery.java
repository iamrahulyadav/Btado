package com.cybussolutions.bataado.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.github.rtoshiro.view.video.FullscreenVideoLayout;

import java.io.IOException;

public class DetailGallery extends AppCompatActivity {

    String fileName,mediaType;
    ImageView mPhotoImageView;
    FullscreenVideoLayout videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_gallery);
        mPhotoImageView = findViewById(R.id.iv_photo);
        videoView = findViewById(R.id.video_view);
        Intent intent = getIntent();
        fileName=intent.getStringExtra("title");
        mediaType=intent.getStringExtra("media");
        if(mediaType.equals("picture")) {
            videoView.setVisibility(View.GONE);
            mPhotoImageView.setVisibility(View.VISIBLE);
            Glide.with(DetailGallery.this)
                    .load(End_Points.BRAND_GALLERY + fileName)
                    .apply(new RequestOptions().placeholder(R.drawable.error_center_x)
                    )
                    .into(mPhotoImageView);
        }else if(mediaType.equals("video")){
            videoView.setVisibility(View.VISIBLE);
            mPhotoImageView.setVisibility(View.GONE);
            Uri videoUri = Uri.parse(End_Points.BRAND_VIDEO+fileName);
            try {
                videoView.setVideoURI(videoUri);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
