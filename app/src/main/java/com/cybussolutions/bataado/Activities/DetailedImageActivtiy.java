package com.cybussolutions.bataado.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cybussolutions.bataado.Model.SpacePhoto;
import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

public class DetailedImageActivtiy extends AppCompatActivity {
    public static final String EXTRA_SPACE_PHOTO = "SpacePhotoActivity.SPACE_PHOTO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_image_activtiy);
        ImageView mImageView = findViewById(R.id.image);
        String spacePhoto = getIntent().getStringExtra(EXTRA_SPACE_PHOTO);

        Picasso.with(this)
                .load(End_Points.IMAGE_PROFILE_PIC+spacePhoto)
                .error(R.drawable.error_center_x)
                .placeholder(R.drawable.progress_animation)
                .into(mImageView);
    }
}
