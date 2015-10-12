/*
 * Copyright (c) 2015.
 * Created By Arshpreet Wadehra on 9/17/15 11:50 PM
 */

package com.davlib.mylibrary;
/***
 * Create By Arshpreet Singh Wadehra
 *
 * @WadehrArshpreet
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanBarCode extends ActionBarActivity {
    FragmentManager fm; //fragment manager object declare
    FragmentTransaction ftrans; //fragment trasaction object declare
    boolean mCheckFlashAvailable, isFlashOn = false, isManualISBN = false;
    Menu mActionMenu;
    String mCommingFor,mUserId,mMain="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scan_bar_code);
        mCommingFor = getIntent().getStringExtra("commingFor");
        mMain = getIntent().getStringExtra("main");
        if (mCommingFor.equals("issue"))
            mUserId = getIntent().getStringExtra("mUserId");
        else
            mUserId = "";
        fm = getSupportFragmentManager();
        ftrans = fm.beginTransaction();
        ftrans.replace(R.id.fragmentHolder, new BarCodeScanner(false, mCommingFor,mUserId,mMain));
        ftrans.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mActionMenu = menu;
        //check flash available or not
        mCheckFlashAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        MenuItem item = menu.add(Menu.NONE, R.id.cancel_scan, 103, R.string.scanCancel);
        //MenuItem item2 = menu.add(Menu.NONE, R.id.flash_scan, 102, R.string.scanFlash);
        //item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem item3 = menu.add(Menu.NONE, R.id.manual_scan, 101, R.string.scanAddISBN);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item3.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        Drawable dr = getResources().getDrawable(R.drawable.ic_action_cancel_256);
        item.setIcon(dr);
        //dr = getResources().getDrawable(R.drawable.ic_action_image_flash_off);
        //item2.setIcon(dr);
        dr = getResources().getDrawable(R.drawable.ic_action_ic_edit_48pt_3xx2);
        item3.setIcon(dr);
        getMenuInflater().inflate(R.menu.menu_scan_bar_code, menu);
       /* if (!mCheckFlashAvailable)
            item2.setVisible(false);
       */ return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cancel_scan) {
            Intent intent = new Intent();
            intent.putExtra("save", getIntent().getStringExtra("previousSelection"));
            setResult(1, intent);
            finish();
        } else if (id == R.id.flash_scan) {
            isFlashOn = !isFlashOn;
            if (isFlashOn)
                mActionMenu.findItem(R.id.flash_scan).setIcon(getResources().getDrawable(R.drawable.ic_action_image_flash_on));
            else
                mActionMenu.findItem(R.id.flash_scan).setIcon(getResources().getDrawable(R.drawable.ic_action_image_flash_off));
            ftrans = fm.beginTransaction();
            ftrans.replace(R.id.fragmentHolder, new BarCodeScanner(isFlashOn, mCommingFor,mUserId,mMain));
            ftrans.commit();
        } else if (id == R.id.manual_scan) {
            isManualISBN = !isManualISBN;
            ftrans = fm.beginTransaction();
            View mTempView = this.getCurrentFocus();
            if (mTempView != null) {
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTempView.getWindowToken(), 0);
            }
            if (mCommingFor.equals("scanusercode")) {
                if (isManualISBN) {
                    ftrans.setCustomAnimations(R.anim.slide_down_from_up, R.anim.slide_down);
                    ftrans.replace(R.id.fragmentHolder, new UserCodeManual(mCommingFor,mMain));
                    mActionMenu.findItem(R.id.flash_scan).setVisible(false);
                    mActionMenu.findItem(R.id.manual_scan).setIcon(getResources().getDrawable(R.drawable.ic_action_image_crop_free));
                } else {
                    ftrans.setCustomAnimations(R.anim.fade_in, R.anim.slide_up);
                    ftrans.replace(R.id.fragmentHolder, new BarCodeScanner(isFlashOn, mCommingFor,mUserId,mMain));
                    mActionMenu.findItem(R.id.flash_scan).setVisible(true);
                    mActionMenu.findItem(R.id.manual_scan).setIcon(getResources().getDrawable(R.drawable.ic_action_ic_edit_48pt_3xx2));
                }
            } else {
                if (isManualISBN) {
                    ftrans.setCustomAnimations(R.anim.slide_down_from_up, R.anim.slide_down);
                    ftrans.replace(R.id.fragmentHolder, new ISBNManually(mCommingFor,mUserId,mMain));
                    mActionMenu.findItem(R.id.flash_scan).setVisible(false);
                    mActionMenu.findItem(R.id.manual_scan).setIcon(getResources().getDrawable(R.drawable.ic_action_image_crop_free));
                } else {
                    ftrans.setCustomAnimations(R.anim.fade_in, R.anim.slide_up);
                    ftrans.replace(R.id.fragmentHolder, new BarCodeScanner(isFlashOn, mCommingFor,mUserId,mMain));
                    mActionMenu.findItem(R.id.flash_scan).setVisible(true);
                    mActionMenu.findItem(R.id.manual_scan).setIcon(getResources().getDrawable(R.drawable.ic_action_ic_edit_48pt_3xx2));
                }
            }
            ftrans.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("save", getIntent().getStringExtra("previousSelection"));
        setResult(1, intent);
        finish();
    }
}
