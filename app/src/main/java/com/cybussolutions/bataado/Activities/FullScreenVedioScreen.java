package com.cybussolutions.bataado.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

import com.cybussolutions.bataado.Network.End_Points;
import com.cybussolutions.bataado.R;
import com.github.rtoshiro.view.video.FullscreenVideoLayout;

import java.io.IOException;

public class FullScreenVedioScreen extends AppCompatActivity {
    String fileName,mediaType;
    FullscreenVideoLayout videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_vedio_screen);
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        videoView = findViewById(R.id.video_view);
        ImageButton imgFullScreen = videoView.findViewById(R.id.vcv_img_fullscreen);
        imgFullScreen.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.exit_full_screen));
        imgFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        fileName=intent.getStringExtra("title");
        Uri videoUri = Uri.parse(End_Points.IMAGE_RREVIEW_URL+fileName);
        try {
            videoView.setVideoURI(videoUri);

        } catch (IOException e) {
            e.printStackTrace();
        }
        videoView.start();
    }
}
