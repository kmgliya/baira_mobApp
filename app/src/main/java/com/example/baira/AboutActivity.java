package com.example.baira;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AboutActivity extends BaseActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Найти VideoView по id
        videoView = findViewById(R.id.videoView);

        // Путь к видео в raw
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.intro_video;
        Uri uri = Uri.parse(videoPath);

        // Установить видео в VideoView и запустить
        videoView.setVideoURI(uri);

        // Зациклить видео
        videoView.setOnPreparedListener(mp -> mp.setLooping(true));

        videoView.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.about;
    }
}
