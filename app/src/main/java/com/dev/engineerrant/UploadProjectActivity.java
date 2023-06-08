package com.dev.engineerrant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dev.engineerrant.animations.Tools;

public class UploadProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_project);
    }
}