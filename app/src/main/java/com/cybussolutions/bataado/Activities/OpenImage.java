package com.cybussolutions.bataado.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.squareup.picasso.Picasso;

public class OpenImage extends AppCompatActivity {
    public static final String EXTRA_SPACE_PHOTO = "SpacePhotoActivity.SPACE_PHOTO";
    String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);
        ImageView mImageView = findViewById(R.id.image);
        Intent intent = getIntent();
        fileName=intent.getStringExtra("title");

        Picasso.with(this)
                .load(End_Points.IMAGE_RREVIEW_URL+fileName)
                .error(R.drawable.error_center_x)
                .placeholder(R.drawable.progress_animation)
                .into(mImageView);
    }
}
