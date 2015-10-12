/*
 * Copyright (c) 2015.
 * Created By Arshpreet Wadehra on 9/26/15 2:20 PM
 * Follow @WadehrArshpreet
 */

package com.davlib.mylibrary;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassword extends Fragment {

    String mFor, mID;
    ListView mSubList;
    EditText mCurrent, mNow, mConfirm;
    Button mSave;

    public ChangePassword() {
        // Required empty public constructor
    }

    public ChangePassword(String mFor, String mID) {
        this.mFor = mFor;
        this.mID = mID;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_change_password, container, false);
        mSubList = (ListView) getActivity().findViewById(R.id.navList2);
        mSubList.getChildAt(0).setBackgroundColor(
                Color.parseColor("#bcbbb5"));
        mSubList.getChildAt(1).setBackgroundColor(
                Color.parseColor("#CACEFF"));
        getActivity().setTitle("Edit Password");
        mCurrent = (EditText) v.findViewById(R.id.current_password);
        mNow = (EditText) v.findViewById(R.id.new_password);
        mConfirm = (EditText) v.findViewById(R.id.confirm_password);
        mSave = (Button) v.findViewById(R.id.save_changes);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCurrent.requestFocus();
                imm.showSoftInput(mCurrent, 0);
            }
        }, 1000);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cur = String.valueOf(mCurrent.getText());
                String now = String.valueOf(mNow.getText());
                String confirm = String.valueOf(mConfirm.getText());
                if (cur.equals("") || now.equals("") || confirm.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!now.equals(confirm)) {
                    Toast.makeText(getActivity().getApplicationContext(), "New Password Not Matched", Toast.LENGTH_SHORT).show();
                    return;
                } else if (now.length() < 6) {
                    Toast.makeText(getActivity().getApplicationContext(), "New Password must be atleast 6 character", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new PleaseChangeMyPassword().execute(mFor, mID, cur, now);
                }
            }
        });

        mConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.imeChange || actionId == EditorInfo.IME_ACTION_DONE) {
                    mSave.performClick();
                }
                return true;
            }
        });
        return v;

    }

    private class PleaseChangeMyPassword extends AsyncTask<String, Void, JSONObject> {
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
                    nameValuePair.add(new BasicNameValuePair("save_pass_for", params[0]));
                    nameValuePair.add(new BasicNameValuePair("save_pass_id", params[1]));
                    nameValuePair.add(new BasicNameValuePair("save_pass_current", MD5.encode(params[2])));
                    nameValuePair.add(new BasicNameValuePair("save_pass_new", MD5.encode(params[3])));
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
                    Toast.makeText(getActivity().getApplicationContext(), "Password Change Successfully", Toast.LENGTH_SHORT).show();
                else if (status.equals("2")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Wrong Current Password ", Toast.LENGTH_LONG).show();
                    return;
                } else
                    Toast.makeText(getActivity().getApplicationContext(), "ERROR in Changing Password", Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
            } catch (JSONException e) {
                Librarian.logged(e);
            }
        }
    }

}
