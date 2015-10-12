/*
 * Copyright (c) 2015.
 * Created By Arshpreet Wadehra on 9/25/15 8:31 AM
 * Follow @WadehrArshpreet
 */

package com.davlib.mylibrary;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class YourDetails extends Fragment {

    String mFor, mID;
    Switch mModeChange;
    TextView mUserId, mRUserName, mRUserEmail; //R = ReadOnly
    EditText mUserName, mUserEmail;
    Button mSaveChanges, mEditPassword;
    FragmentManager mFragmentManager; //fragment manager object declare
    FragmentTransaction mFragmentTransaction; //fragment trasaction object declare
    ListView mSubList;
    public YourDetails() {
        // Required empty public constructor
    }

    public YourDetails(String show, String id) {
        mFor = show;
        mID = id; //for lib is emp id
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_your_details, container, false);
        getActivity().setTitle("Your Details");
        mModeChange = (Switch) v.findViewById(R.id.change_mode);
        mFragmentManager = getActivity().getSupportFragmentManager();
        mModeChange.setChecked(false);
        mUserEmail = (EditText) v.findViewById(R.id.edit_user_email);
        mUserName = (EditText) v.findViewById(R.id.edit_user_name);
        mUserId = (TextView) v.findViewById(R.id.user_id);
        mRUserEmail = (TextView) v.findViewById(R.id.user_email);
        mRUserName = (TextView) v.findViewById(R.id.user_name);
        mSaveChanges = (Button) v.findViewById(R.id.save_changes);
        mEditPassword = (Button) v.findViewById(R.id.edit_password);
        mSubList = (ListView) getActivity().findViewById(R.id.navList2);
        mSubList.getChildAt(1).setBackgroundColor(
                Color.parseColor("#bcbbb5"));
        mSubList.getChildAt(0).setBackgroundColor(
                Color.parseColor("#CACEFF"));
        mModeChange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) //editing mode on
                {
                    mRUserName.setVisibility(View.GONE);
                    mRUserEmail.setVisibility(View.GONE);
                    mUserName.setVisibility(View.VISIBLE);
                    mUserName.requestFocus();
                    mUserName.setSelection(mUserName.getText().toString().length());
                    mUserEmail.setVisibility(View.VISIBLE);
                    mSaveChanges.setVisibility(View.VISIBLE);
                    getActivity().setTitle("Edit Details");
                } else {
                    mUserName.setVisibility(View.GONE);
                    mUserEmail.setVisibility(View.GONE);
                    mRUserName.setVisibility(View.VISIBLE);
                    mRUserEmail.setVisibility(View.VISIBLE);
                    mSaveChanges.setVisibility(View.GONE);
                    getActivity().setTitle("Your Details");
                }
            }
        });

        //librarian details
        new LoadUserDetails().execute(mID,mFor);
        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mUserEmail.getText().toString().equals("")) {
                    if (new EmailValidator().validate(mUserEmail.getText().toString())) {
                        new SaveMyChangesPlease().execute(mFor, mUserName.getText().toString(), mUserEmail.getText().toString(), mUserId.getText().toString());
                        return;
                    }
                    Toast.makeText(getActivity().getApplicationContext(), "Enter Valid Email ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                new SaveMyChangesPlease().execute(mFor, mUserName.getText().toString(), mUserEmail.getText().toString(), mUserId.getText().toString());
            }
        });
        mEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.setCustomAnimations(R.anim.slide_left_left, R.anim.slide_left);
                mFragmentTransaction.replace(R.id.fragmentHolder, new ChangePassword(mFor,mID)).commit();
            }
        });

        return v;
    }

    private class SaveMyChangesPlease extends AsyncTask<String, Void, JSONObject> {

        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Saving Changes, Please Wait...");
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
                    HttpPost httpPost = new HttpPost(Librarian.mServerAddress + "/index.php");
                    List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                    nameValuePair.add(new BasicNameValuePair("save_for", params[0]));
                    nameValuePair.add(new BasicNameValuePair("save_name", params[1]));
                    nameValuePair.add(new BasicNameValuePair("save_email", params[2]));
                    nameValuePair.add(new BasicNameValuePair("save_user_id", params[3]));
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

        protected void onPostExecute(JSONObject s) {
            mDialog.hide();
            try {
                String status = s.getString("status");
                if (status.equals("1"))
                    Toast.makeText(getActivity().getApplicationContext(), "Changes Successfully Saved", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity().getApplicationContext(), "ERROR in Saving Changes Try Later", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            } catch (JSONException e) {
                Librarian.logged(e);
            }

        }


    }// end of LoadUser Details


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
                    nameValuePair.add(new BasicNameValuePair("details_require", params[0]));
                    nameValuePair.add(new BasicNameValuePair("details_require_for", params[1]));
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

        protected void onPostExecute(JSONObject s) {
            mDialog.hide();
            updateData(s);
        }
    }// end of LoadUser Details

    private void updateData(JSONObject mData) {
        try {
            String mStatus = mData.getString("status");
            if (mStatus.equals("-1")) {
                Toast.makeText(getActivity().getApplicationContext(), "Error In Fetching Details", Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                return;
            }
            String mID = mData.getString("id");
            String mName = mData.getString("name");
            String mEmail = mData.getString("email");
            mUserId.setText(mID);
            if (mName.equals("") || mName.equals("null"))
                mName = "Not Available";
            mUserName.setText(mName);
            mRUserName.setText(mName);
            mUserEmail.setText(mEmail);
            mRUserEmail.setText(mEmail);
        } catch (Exception e) {
            Librarian.logged(e);
        }
    }

}// end of yourdetails
