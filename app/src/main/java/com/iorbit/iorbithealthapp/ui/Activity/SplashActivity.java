package com.iorbit.iorbithealthapp.ui.Activity;

import static maes.tech.intentanim.CustomIntent.customType;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iorbit.iorbithealthapp.R;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;
    ImageView txtFadeIn;
    Animation animFadeIn;
    LinearLayout linearLayouthead,linearLayout2,linearLayout3;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        txtFadeIn=findViewById(R.id.txt_fade_in);
        linearLayout2=findViewById(R.id.time_visible);
        linearLayout3=findViewById(R.id.where_visible);
        linearLayouthead=findViewById(R.id.device_visible);
        animFadeIn = AnimationUtils.loadAnimation(SplashActivity.this,
                R.anim.fade_in);
        animFadeIn = AnimationUtils.loadAnimation(SplashActivity.this,
                R.anim.fade_in);
        txtFadeIn.setVisibility(View.VISIBLE);
        txtFadeIn.startAnimation(animFadeIn);




        animFadeIn = AnimationUtils.loadAnimation(SplashActivity.this,
                R.anim.time);
        animFadeIn = AnimationUtils.loadAnimation(SplashActivity.this,
                R.anim.time);
        linearLayout2.setVisibility(View.VISIBLE);
        linearLayout2.startAnimation(animFadeIn);


        animFadeIn = AnimationUtils.loadAnimation(SplashActivity.this,
                R.anim.location);
        animFadeIn = AnimationUtils.loadAnimation(SplashActivity.this,
                R.anim.location);
        linearLayout3.setVisibility(View.VISIBLE);
        linearLayout3.startAnimation(animFadeIn);


        animFadeIn = AnimationUtils.loadAnimation(SplashActivity.this,
                R.anim.device);
        animFadeIn = AnimationUtils.loadAnimation(SplashActivity.this,
                R.anim.device);
        linearLayouthead.setVisibility(View.VISIBLE);
        linearLayouthead.startAnimation(animFadeIn);

        settingView();
    }


    private void settingView() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                customType(SplashActivity.this,"fadein-to-fadeout");
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}