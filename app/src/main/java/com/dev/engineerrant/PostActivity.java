package com.dev.engineerrant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dev.engineerrant.animations.Tools;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
    }

    public void rant(View view) {
        compose(1,"R a n t / S t o r y");
    }

    public void joke(View view) {
        compose(2,"J o k e / M e m e");
    }

    public void question(View view) {
        compose(3,"Q u e s t i o n");
    }

    public void devrant(View view) {
        compose(5,"d e v R a n t");
    }

    public void random(View view) {
        compose(6,"R a n d o m");
    }

    private void compose(int type, String typeName) {
        Intent intent;
        intent = new Intent(PostActivity.this, PostComposeActivity.class);
        intent.putExtra("type", String.valueOf(type));
        intent.putExtra("typeName", typeName);
        startActivity(intent);
    }
}