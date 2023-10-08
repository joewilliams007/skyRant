package com.dev.engineerrant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dev.engineerrant.animations.Tools;

public class CommunityUploadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_upload);
    }
}