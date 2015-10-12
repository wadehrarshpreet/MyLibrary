/*
 * Copyright (c) 2015. @WadehraArshpreet
 *
 * Created By Arshpreet Singh Wadehra 9/2/15 5:57 PM
 */

package com.davlib.mylibrary;
/***
 *Create By Arshpreet Singh Wadehra
 * @WadehrArshpreet
 */
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;

public class Splash_Screen extends ActionBarActivity {

private int mTimer = 0;
private WebView mLoadingGif;
    private DBHelper mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//full screen
        setContentView(R.layout.activity_splash__screen);
        mLoadingGif = (WebView) findViewById(R.id.loading);
        mLoadingGif.setBackgroundColor(Color.TRANSPARENT);
        mLoadingGif.loadUrl("file:///android_asset/load.html"); // load loading gif
        mDatabase = new DBHelper(this); // create or open database
        String mUserDetails[] = mDatabase.getDetails(); //load login info if stored
        final Intent mGoToUserSelection = new Intent(getApplicationContext(),SelectUserType.class);
        if(mUserDetails == null) {
            mTimer = 500;
            mGoToUserSelection.putExtra("isUserDetailExist",false);
        }
        else {
            mTimer = 1500;
            mGoToUserSelection.putExtra("isUserDetailExist",true);
            mGoToUserSelection.putExtra("UserDetailId",mUserDetails[0]);
            mGoToUserSelection.putExtra("UserDetailPassword",mUserDetails[1]);
        }
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    startActivity(mGoToUserSelection);
                    finish();
                } catch (Exception e) {
                    Librarian.logged(e);
                }
            }
        }, mTimer);

    }
}
