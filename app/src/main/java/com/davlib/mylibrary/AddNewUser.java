/*
 * Copyright (c) 2015.
 * Created By Arshpreet Wadehra on 9/22/15 7:08 PM
 * Follow @WadehrArshpreet
 */

package com.davlib.mylibrary;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
public class AddNewUser extends Fragment {

    EditText user_id_1, user_id_2, user_name, user_email;
    Button add_new_user;

    public AddNewUser() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Register New User");
        View v = inflater.inflate(R.layout.fragment_add_new_user, container, false);
        user_id_1 = (EditText) v.findViewById(R.id.user_id_1);
        user_id_2 = (EditText) v.findViewById(R.id.user_id_2);
        user_name = (EditText) v.findViewById(R.id.user_name);
        user_email = (EditText) v.findViewById(R.id.user_email);
        add_new_user = (Button) v.findViewById(R.id.add_new_user);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        user_id_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 3) {
                    user_id_2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        user_id_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2) {
                    user_name.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        user_id_1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.imeManualUserId || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (user_id_1.getText().length() == 3)
                        user_id_2.requestFocus();
                }
                return false;
            }
        });

        user_id_2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.imeManualUserId || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (user_id_2.getText().length() == 2)
                        user_name.requestFocus();
                }
                return false;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                user_id_1.requestFocus();
                imm.showSoftInput(user_id_1, 0);
            }
        }, 1000);
        add_new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(user_id_1.getText()).length() == 3 && String.valueOf(user_id_2.getText()).length() == 2) {
                    if (!String.valueOf(user_email.getText()).equals("")  && !String.valueOf(user_name.getText()).equals("")) {
                        String user_id = String.valueOf(user_id_1.getText()) + "/" + String.valueOf(user_id_2.getText());
                        String password = "password" + String.valueOf(user_id_1.getText());
                        String mEmail = String.valueOf(user_email.getText());
                        if(new EmailValidator().validate(mEmail))
                            new RegisterNewUser().execute(user_id, password, mEmail, String.valueOf(user_name.getText()));
                        else
                            Toast.makeText(getActivity().getApplicationContext(), "Please Enter Valid Email Address", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getActivity().getApplicationContext(), "Enter Valid Roll No", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private class RegisterNewUser extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Registering USER, Please Wait...");
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
                    nameValuePair.add(new BasicNameValuePair("needNewUserPlease", "1"));
                    nameValuePair.add(new BasicNameValuePair("newUserId", params[0]));
                    nameValuePair.add(new BasicNameValuePair("newUserPassword", MD5.encode(params[1])));
                    nameValuePair.add(new BasicNameValuePair("newUserEmail", params[2]));
                    nameValuePair.add(new BasicNameValuePair("newUserName", params[3]));
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
        protected void onPostExecute(JSONObject mData) {
            mDialog.hide();
            try {
                String result = mData.getString("status");
                if (result.equals("1"))
                {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("SUCCESS!")
                            .setMessage("USER REGISTER SUCCESSFULLY with Password\n \"password"+user_id_1.getText()+"\"")
                            .setIcon(R.drawable.ic_action_user_add_256)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().onBackPressed();
                                }
                            }).show();
                    return;
                }
                else if (result.equals("2"))
                {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("ERROR!")
                            .setMessage("USER ALREADY EXIST")
                            .setIcon(R.drawable.ic_action_user_add_256)
                            .setPositiveButton("OK", null).show();
                    return;
                }
                else
                    Toast.makeText(getActivity().getApplicationContext(), "Error In Register new User", Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
            } catch (JSONException e) {
                Librarian.logged(e);
            }
        }

    }

}
