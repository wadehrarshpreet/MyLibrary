/*
 * Copyright (c) 2015. @WadehraArshpreet
 *
 * Created By Arshpreet Singh Wadehra 9/2/15 5:57 PM
 */

package com.davlib.mylibrary;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectUserType extends ActionBarActivity {
    TextView mSelectUserText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // full screen
        mSelectUserText = (TextView) findViewById(R.id.ask);
        final TextView lib = (TextView) findViewById(R.id.lib);
        final TextView user = (TextView) findViewById(R.id.user);
        Typeface fon = Typeface.createFromAsset(getAssets(), "kristen.ttf");
        Typeface fon2 = Typeface.createFromAsset(getAssets(), "mistral.ttf");
        mSelectUserText.setTypeface(fon);
        lib.setTypeface(fon2);
        user.setTypeface(fon2);
        final ImageView libIcon = (ImageView) findViewById(R.id.libIcon);
        final ImageView userIcon = (ImageView) findViewById(R.id.userIcon);
        final ImageView divIcon = (ImageView) findViewById(R.id.divIcon);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.select_lib);
        final Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.select_div);
        libIcon.setAnimation(anim);
        userIcon.setAnimation(anim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                divIcon.setAlpha((float) 0.9);
                user.setAlpha(1);
                lib.setAlpha(1);
                divIcon.setAnimation(anim2);
                user.setAnimation(anim2);
                lib.setAnimation(anim2);
            }
        }, 1000);
        final boolean isInternetPresent = new ConnectionDetector(getApplicationContext()).isConnectingToInternet();
        final Intent in = new Intent(getApplicationContext(), Login_Form.class);
        libIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in.putExtra("logas", "1");
                startActivity(in);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
        });
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in.putExtra("logas", "0");
                startActivity(in);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            }
        });
    }


}
