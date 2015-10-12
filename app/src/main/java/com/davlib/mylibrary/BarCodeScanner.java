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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BarCodeScanner extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private boolean isFlashOn;
    private String mCommingFor;
    String mUserId="",mMain="";
    private List<BarcodeFormat> mAcceptFormat;

    public BarCodeScanner() {
        // Required empty public constructor
        isFlashOn = false;
        mCommingFor = "issue";
    }

    public BarCodeScanner(boolean isFlash, String commingFor,String mUserId,String mMain) {
        isFlashOn = isFlash;
        mCommingFor = commingFor;
        this.mUserId = mUserId;
        this.mMain = mMain;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_bar_code_scanner, container, false);
        mAcceptFormat = new ArrayList<BarcodeFormat>();
        if (mCommingFor.equals("addnewbook"))
            getActivity().setTitle("ISBN | NEWBOOK");
        if (mCommingFor.equals("scanusercode")) {
            getActivity().setTitle("SCAN USER ID");
            mAcceptFormat.add(BarcodeFormat.QR_CODE);
        } else {
            getActivity().setTitle("SCAN ISBN NO");
            mAcceptFormat.add(BarcodeFormat.EAN_13);
            mAcceptFormat.add(BarcodeFormat.EAN_8);
        }
        LinearLayout qrCameraLayout = (LinearLayout) fragmentView.findViewById(R.id.ll_qrcamera);

        mScannerView = new ZXingScannerView(getActivity().getApplicationContext());

        mScannerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        qrCameraLayout.addView(mScannerView);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.setFlash(isFlashOn);
        mScannerView.setFormats(mAcceptFormat);
//        mScannerView.setAutoFocus(true);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.d("errror", rawResult.getText());
        try {
            Intent result = new Intent(getActivity().getApplicationContext(), Librarian.class);
            result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            result.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            result.putExtra("request", mCommingFor);
            if(mCommingFor.equals("issue"))
                result.putExtra("mUserId", mUserId);
            result.putExtra("result", rawResult.getText());
            result.putExtra("main", mMain);
            result.putExtra("resultType", rawResult.getBarcodeFormat().toString());
            startActivity(result);
            getActivity().finish();
        } catch (Exception e) {
            Log.d("errror", e.getMessage());
        }
    }
}
