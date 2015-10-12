/*
 * Copyright (c) 2015.
 * Created By Arshpreet Wadehra on 9/17/15 11:49 PM
 * Follow @WadehrArshpreet
 */

package com.davlib.mylibrary;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class ISBNManually extends Fragment {

    String mCommingFor, mUserId = "", mMain = "";

    public ISBNManually() {
        // Required empty public constructor
    }

    public ISBNManually(String mCommingFor, String mUserId, String mMain) {
        this.mCommingFor = mCommingFor;
        this.mUserId = mUserId;
        this.mMain = mMain;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vd = inflater.inflate(R.layout.fragment_isbnmanually, container, false);
        final EditText isbnNum = (EditText) vd.findViewById(R.id.isbnNum);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isbnNum.requestFocus();
                imm.showSoftInput(isbnNum, 0);
            }
        }, 1000);
        InputFilter[] in = new InputFilter[1];
        in[0] = new InputFilter.LengthFilter(13);
        isbnNum.setFilters(in); // set length 13 or 10
        isbnNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.imeManualISBN || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (isbnNum.getText().length() == 13 || isbnNum.getText().length() == 10) {
                        Intent result = new Intent(getActivity().getApplicationContext(), Librarian.class);
                        result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        result.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        result.putExtra("request", mCommingFor);
                        result.putExtra("mUserId", mUserId);
                        result.putExtra("main", mMain);
                        result.putExtra("result", String.valueOf(isbnNum.getText()));
                        result.putExtra("resultType", "EAN_13");
                        startActivity(result);
                        getActivity().finish();
                    } else {
                        Toast mErr = Toast.makeText(getActivity(), "ISBN Number Should be 13 digit or 10 Digit", Toast.LENGTH_SHORT);
                        mErr.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                        mErr.show();
                    }
                }
                return true;
            }
        });
        return vd;
    }

}
