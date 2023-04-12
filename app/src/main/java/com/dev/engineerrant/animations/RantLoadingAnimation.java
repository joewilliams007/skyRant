package com.dev.engineerrant.animations;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

public class RantLoadingAnimation {

    // remember to add api level check before running this
    // (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)

    private final RelativeLayout relContainer;

    private View[] devs;

    private int startY = 0;
    private int endY = 0;
    private int minEndY = 0;
    private int spaceX = 0;

    private boolean hasStopped = false;

    public RantLoadingAnimation(RelativeLayout relContainer){
        this.relContainer = relContainer;

        initializer();
        beginAnimation();
    }

    private void initializer() {
        startY = Tools.dpToPx(50);
        endY = Tools.dpToPx(150);
        minEndY = Tools.dpToPx(10);
        spaceX = Tools.dpToPx(150);

        devs = new View[relContainer.getChildCount()];
        for (int i = 0; i < devs.length; i++) {
            devs[i] = relContainer.getChildAt(i);
            devs[i].setAlpha(0);
        }
    }

    private int firstRandomStartTime = 50;
    private int i = 0;
    private void beginAnimation() {


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                start(i);


                i++;
                if (i < devs.length) {
                    float random = (float) Math.random();
                    firstRandomStartTime = (int) (random * 100);
                    handler.postDelayed(this, firstRandomStartTime);
                }
            }
        }, firstRandomStartTime);
    }

    private void start(int i) {
        setRandomStartSituation(i);
        index = i;
        new Handler().post(runnable);
    }

    private void setRandomStartSituation(int position) {
        View view = devs[position];

        view.setTranslationX(0);
        view.setTranslationY(startY);
        view.setAlpha(1);

        double random = Math.random();

        view.setRotation((float) (random * 360));

        float scale = (float) (random * 2) + .5f;
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    private int index;
    private final Runnable runnable = new Runnable() {
        @SuppressLint("NewApi")
        @Override
        public void run() {

            final int i = index;

            View view = devs[i];

            final float random = (float) Math.random();

            final int speed = (int) ((random * 1000) + 150);

            view.animate()
                    .translationY(-((random * endY) + minEndY))
                    .translationX((random * spaceX) - spaceX / 2)
                    .setDuration(speed).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            if (!hasStopped){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        start(i);
                                    }
                                }, (long) (random * 100));
                            }
                        }
                    });


            final int whenToFade = (int) (speed * .7f);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    devs[i].animate().alpha(0).setDuration(speed - whenToFade).withLayer();
                }
            }, whenToFade);
        }
    };

    public void stop(){
        hasStopped = true;
    }

}
