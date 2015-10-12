/*
 * Copyright (c) 2015.
 * Created By Arshpreet Wadehra on 9/22/15 6:23 PM
 * Follow @WadehrArshpreet
 */

package com.davlib.mylibrary;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowUserDetails extends Fragment implements View.OnClickListener {
    String mUserId, mSerialNo, mExtras;
    FragmentManager mFragmentManager; //fragment manager object declare
    FragmentTransaction mFragmentTransaction; //fragment trasaction object declare
    TextView user_id, user_name, user_fine, user_email, book_issued_status, DOI[] = new TextView[4], DOR[] = new TextView[4];
    TextView book_serial[] = new TextView[4];
    Button no_issue;
    TableRow T[] = new TableRow[5];

    public ShowUserDetails() {
        // Required empty public constructor
    }

    public ShowUserDetails(String mUserId, String extras) {
        this.mUserId = mUserId;
        this.mExtras = extras;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentManager = getActivity().getSupportFragmentManager();
        getActivity().setTitle("USER DETAILS");
        View v = inflater.inflate(R.layout.fragment_show_user_details, container, false);
        user_id = (TextView) v.findViewById(R.id.user_id);
        user_name = (TextView) v.findViewById(R.id.user_name);
        user_fine = (TextView) v.findViewById(R.id.user_fine);
        user_email = (TextView) v.findViewById(R.id.user_email);
        no_issue = (Button) v.findViewById(R.id.no_issue);
        T[0] = (TableRow) v.findViewById(R.id.issue_book_detail_0);
        T[1] = (TableRow) v.findViewById(R.id.issue_book_detail_1);
        T[2] = (TableRow) v.findViewById(R.id.issue_book_detail_2);
        T[3] = (TableRow) v.findViewById(R.id.issue_book_detail_3);
        T[4] = (TableRow) v.findViewById(R.id.issue_book_detail_4);
        book_serial[0] = (TextView) v.findViewById(R.id.book_detail_1);
        book_serial[1] = (TextView) v.findViewById(R.id.book_detail_2);
        book_serial[2] = (TextView) v.findViewById(R.id.book_detail_3);
        book_serial[3] = (TextView) v.findViewById(R.id.book_detail_4);
        T[1].setOnClickListener(this);
        T[2].setOnClickListener(this);
        T[3].setOnClickListener(this);
        T[4].setOnClickListener(this);
        DOI[0] = (TextView) v.findViewById(R.id.DOI_1);
        DOI[1] = (TextView) v.findViewById(R.id.DOI_2);
        DOI[2] = (TextView) v.findViewById(R.id.DOI_3);
        DOI[3] = (TextView) v.findViewById(R.id.DOI_4);
        DOR[0] = (TextView) v.findViewById(R.id.DOR_1);
        DOR[1] = (TextView) v.findViewById(R.id.DOR_2);
        DOR[2] = (TextView) v.findViewById(R.id.DOR_3);
        DOR[3] = (TextView) v.findViewById(R.id.DOR_4);
        book_issued_status = (TextView) v.findViewById(R.id.book_issued_status);
        for (int i = 0; i < 5; i++)
            T[i].setVisibility(View.INVISIBLE);
        new LoadUserDetails().execute(mUserId);

        if (mExtras.equals("getdetailonly")) {
            getActivity().setTitle("UserDetails");
        }


        no_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExtras.equals("getdetailonly"))
                    getActivity().finish();
                else
                    getActivity().onBackPressed();
            }
        });
        return v;
    }

    @Override
    public void onClick(View v) { //for Table Row Selection ONly
        switch (v.getId()) {
            case R.id.issue_book_detail_1:
                mSerialNo = (String) book_serial[0].getText();
                break;
            case R.id.issue_book_detail_2:
                mSerialNo = (String) book_serial[1].getText();
                break;
            case R.id.issue_book_detail_3:
                mSerialNo = (String) book_serial[2].getText();
                break;
            case R.id.issue_book_detail_4:
                mSerialNo = (String) book_serial[3].getText();
                break;
        }

        try {
            FetchBookDetailsFromSerial temp =new FetchBookDetailsFromSerial(mSerialNo,getActivity());
            temp.showData();
        } catch (Exception e) {
            Librarian.logged(e);
        }

    }

    //    Load User Details
    private class LoadUserDetails extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Checking User Details, Please Wait...");
            mDialog.setIndeterminate(true);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject mJSONResponse = null;
            try {
                if (new ConnectionDetector(getActivity().getApplicationContext()).isConnectingToInternet()) {
                    HttpClient mClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(Librarian.mServerAddress + "/fetch.php");
                    List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                    nameValuePair.add(new BasicNameValuePair("user_details_require", params[0]));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                    HttpResponse response = mClient.execute(httpPost);
                    String mResponseData = EntityUtils.toString(response.getEntity());
                    mJSONResponse = new JSONObject(mResponseData);
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("NO INTERNET CONNECTIVITY");
                    alertDialog.setMessage("PLEASE CONNECT TO INTERNET!");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    });
                }
            } catch (Exception e) {
                Log.d("errror", e.getMessage());
            }
            return mJSONResponse;
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            mDialog.hide();
            updateData(s);
        }
    }

    private void updateData(JSONObject mData) {
        try {
            String mStatus = mData.getString("status");
            if (mStatus.equals("-1")) {
                Toast.makeText(getActivity().getApplicationContext(), "No User Exist With this ID.", Toast.LENGTH_LONG).show();
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.slide_down);
                mFragmentTransaction.replace(R.id.fragmentHolder, new Librarian_Home()).commit();
                return;
            }
            String mID = mData.getString("id");
            String mName = mData.getString("name");
            String mIssued = mData.getString("issuedbook");
            String mWishList = mData.getString("wishlist");
            String mFine = mData.getString("fine");
            String mEmail = mData.getString("email");
            user_id.setText(mID);
            if (mName.equals("") || mName.equals("null"))
                mName = "Not Available";
            user_name.setText(mName);
            user_fine.setText(mFine);
            user_email.setText(mEmail);
            if (mIssued.equals("null") || mIssued.equals("")) {
                book_issued_status.setText("NO BOOK ISSUED CURRENTLY");
            } else {
                book_issued_status.setText("ISSUED BOOK DETAILS");
                T[0].setVisibility(View.VISIBLE);
                String mIssuedDetail[] = mIssued.split(";");
                int i = 0;
                for (String mIssue : mIssuedDetail) {
                    if (mIssue.equals("")) // if empty issue detail then break the loop
                        break;
                    T[i + 1].setVisibility(View.VISIBLE);
                    String[] mSubDetailsIssue = mIssue.split("=");
                    book_serial[i].setText(mSubDetailsIssue[0]);
                    DOI[i].setText(mSubDetailsIssue[1]);
                    SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd-yyyy");
                    Calendar mCalendar = Calendar.getInstance();
                    Date mIssueDate = mDateFormat.parse(mSubDetailsIssue[1]);
                    mCalendar.setTime(mIssueDate);
                    mCalendar.add(Calendar.DATE, 14);
                    String mReturnDate = mDateFormat.format(mCalendar.getTime());
                    DOR[i].setText(mReturnDate);
                    i++;
                }
            }

        } catch (Exception e) {
            Librarian.logged(e);
        }
    }

}
