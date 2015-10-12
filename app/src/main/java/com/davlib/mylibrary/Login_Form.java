/*
 * Copyright (c) 2015. @WadehraArshpreet
 *
 * Created By Arshpreet Singh Wadehra 9/2/15 5:57 PM
 */

package com.davlib.mylibrary;
/***
 * Create By Arshpreet Singh Wadehra
 *
 * @WadehrArshpreet
 */

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Login_Form extends ActionBarActivity {
    String logas = null;
    Boolean isInternetPresent = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__form);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final EditText loginPassword = (EditText) findViewById(R.id.loginPassword);
        final Button letMeLogin = (Button) findViewById(R.id.letMeLogin);
        final DBHelper db = new DBHelper(this);
        final EditText loginId = (EditText) findViewById(R.id.loginId);
        //start keyboard when activity starts
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loginId.requestFocus();
                imm.showSoftInput(loginId, 0);
            }
        }, 1000);

        try {
            isInternetPresent = new ConnectionDetector(getApplicationContext()).isConnectingToInternet();
        } catch (Exception e) {
            Librarian.logged(e);
        }
        logas = getIntent().getStringExtra("logas");

        if(logas.equals("1"))
        {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("NO INTERNET CONNECTIVITY");
            alertDialog.setMessage("PLEASE CONNECT TO INTERNET");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            if (isInternetPresent == false)
                alertDialog.create().show();

        }
        if (logas.equals("0")) {//set user id length limit
            InputFilter[] in = new InputFilter[1];
            in[0] = new InputFilter.LengthFilter(6);
            loginId.setFilters(in); // set length 6
            loginId.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        letMeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logas.equals("1")) // librarian
                {

                } else // user
                {

                }
            }
        });

    }
}
