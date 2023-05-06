package com.dev.engineerrant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class IntroActivity extends AppCompatActivity {
    ViewStub stub, stub1, stub2, stub3, stub4, stub5, stub6;
    SeekBar seekBar;
    View inflated = null;
    TextView textViewNext;
    ArrayList<Integer> layouts;
    ConstraintLayout constraintLayout;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        constraintLayout = findViewById(R.id.constraintLayout);
        textViewNext = findViewById(R.id.textViewNext);
        seekBar = findViewById(R.id.seekBar);
        stub1 = findViewById(R.id.layout_stub1);
        stub2 = findViewById(R.id.layout_stub2);
        stub3 = findViewById(R.id.layout_stub3);
        stub4 = findViewById(R.id.layout_stub4);
        stub5 = findViewById(R.id.layout_stub5);
        stub6 = findViewById(R.id.layout_stub6);
        layouts = new ArrayList<Integer>();
        layouts.add(R.layout.intro_welcome);
        layouts.add(R.layout.intro_love);
        layouts.add(R.layout.intro_block);
        layouts.add(R.layout.intro_surprise);
        layouts.add(R.layout.intro_widgets);
        layouts.add(R.layout.intro_end);
        seekBar.setMax(layouts.size()-1);
        seekBar.setProgress(0);
        setIntro();
    }

    private void setIntro() {
        if (inflated!=null) {
            inflated.setVisibility(View.GONE);
        }
        if (seekBar.getProgress() == 0) {
            stub = stub1;
        } else if (seekBar.getProgress() == 1) {
            stub = stub2;
        } else if (seekBar.getProgress() == 2) {
            stub = stub3;
        } else if (seekBar.getProgress() == 3) {
            stub = stub4;
        } else if (seekBar.getProgress() == 4) {
            stub = stub5;
        } else if (seekBar.getProgress() == 5) {
            stub = stub6;
        }
        stub.setLayoutResource(layouts.get(seekBar.getProgress()));
        View inflated = null;
        if (stub.getParent() != null) {
            inflated = stub.inflate(); // inflate the layout resource
        } else {
            stub.setVisibility(View.VISIBLE);
        }
 
        setUpFadeAnimation();

        if (seekBar.getProgress()==0) {
            final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
            assert inflated != null;
            ImageView imageView = inflated.findViewById(R.id.imageViewBall);
            imageView.startAnimation(animation);
        }
    }

    public void nextIntro(View view) {
        if (layouts.size()-1==seekBar.getProgress()) {
            finish();
        } else {
            seekBar.setProgress(seekBar.getProgress()+1);
            setIntro();
        }
        if (layouts.size()-1==seekBar.getProgress()) {
            textViewNext.setText("F I N I S H");
        }
    }

    private void setUpFadeAnimation() {
        // Start from 0.1f if you desire 90% fade animation
        final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        fadeIn.setStartOffset(0);
        // End to 0.1f if you desire 90% fade animation
        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        fadeOut.setStartOffset(0);

        fadeIn.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeOut when fadeIn ends (continue)
                // textView.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        textViewNext.startAnimation(fadeIn);
    }


    public void doNothing(View view) {
    }

    public void skip(View view) {
        finish();
    }
}