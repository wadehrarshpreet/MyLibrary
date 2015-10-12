/*
 * Copyright (c) 2015.
 * Created By Arshpreet Wadehra on 9/19/15 7:55 PM
 * Follow @WadehrArshpreet
 */

package com.davlib.mylibrary;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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
public class UserCodeManual extends Fragment {


    String mCommingFor, mMain = "";

    public UserCodeManual() {
        // Required empty public constructor
    }

    public UserCodeManual(String mCommingFor, String mMain) {
        this.mCommingFor = mCommingFor;
        this.mMain = mMain;
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vd = inflater.inflate(R.layout.fragment_user_code_manual, container, false);
        final EditText isbnNum = (EditText) vd.findViewById(R.id.isbnNum);
        final EditText isbnNum2 = (EditText) vd.findViewById(R.id.isbnNum2);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isbnNum.requestFocus();
                imm.showSoftInput(isbnNum, 0);
            }
        }, 1000);
        isbnNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    isbnNum2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        isbnNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.imeManualUserId || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (isbnNum.getText().length() == 3)
                        isbnNum2.requestFocus();
                }
                return false;
            }
        });
        isbnNum2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.imeManualUserId || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (isbnNum.getText().length() == 3 && isbnNum2.getText().length() == 2) {
                        Intent result = new Intent(getActivity().getApplicationContext(), Librarian.class);
                        result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        result.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        result.putExtra("request", mCommingFor);
                        result.putExtra("main", mMain);
                        result.putExtra("extras", "null");
                        result.putExtra("result", String.valueOf(isbnNum.getText()) + "/" + String.valueOf(isbnNum2.getText()));
                        result.putExtra("resultType", "QR_CODE");
                        startActivity(result);
                        getActivity().finish();
                    } else {
                        Toast mErr = Toast.makeText(getActivity(), "InValid USER ID", Toast.LENGTH_SHORT);
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
