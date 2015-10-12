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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class IssueBook extends Fragment {

    private String mISBNNumber, mUserId = "";
    private TextView book_title, book_authors, book_publisher, book_publishedDate, book_serial;
    private ImageView book_image;
    FragmentManager mFragmentManager; //fragment manager object declare
    FragmentTransaction mFragmentTransaction; //fragment trasaction object declare
    Spinner serial_selection;

    public IssueBook() {

    }

    public IssueBook(String mISBNNumber, String mUserId) {
        // Required empty public constructor
        this.mISBNNumber = mISBNNumber;
        this.mUserId = mUserId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_issue_book, container, false);
        book_title = (TextView) v.findViewById(R.id.issue_book_title);
        book_authors = (TextView) v.findViewById(R.id.issue_book_authors);
        book_publisher = (TextView) v.findViewById(R.id.issue_book_publisher);
        book_publishedDate = (TextView) v.findViewById(R.id.issue_book_published_date);
        book_image = (ImageView) v.findViewById(R.id.issue_book_image);
        serial_selection = (Spinner) v.findViewById(R.id.serial_selection);
        new LoadBookDetails().execute(mISBNNumber);
        getActivity().setTitle("Issue Book");
        Button issue_cancel = (Button) v.findViewById(R.id.issue_cancel);
        Button issue_to = (Button) v.findViewById(R.id.issue_to);
        mFragmentManager = getActivity().getSupportFragmentManager();
        issue_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.slide_down);
                mFragmentTransaction.replace(R.id.fragmentHolder, new Librarian_Home()).commit();
            }
        });
        issue_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mSerial = serial_selection.getSelectedItem().toString(); // get serial
                new PleaseIssueBook().execute(mUserId,mSerial,"1");
            }
        });
        return v;
    }

    protected void updateData(JSONObject mData) {
        try {
            String mStatus = mData.getString("status");
            if (mStatus.equals("-1")) {
                Toast.makeText(getActivity().getApplicationContext(), "No Book Found in this Library.", Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                return;
            }
            if (mStatus.equals("2")) {
                Toast.makeText(getActivity().getApplicationContext(), "All Book Issued", Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                return;
            }
            String mBookName = mData.getString("book_name");
            String mPublishDate = mData.getString("publishedDate");
            String mPublisher = mData.getString("publisher");
            JSONArray mSerialNo = (JSONArray) mData.get("serial");
            String mSerialNos[] = new String[(mSerialNo.length() > 0 ? mSerialNo.length() : 1)];
            for (int i = 0; i < mSerialNo.length(); i++)
                mSerialNos[i] = mSerialNo.getString(i);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.simple_spinner_item, mSerialNos);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            serial_selection.setAdapter(adapter);
            String mImage = mData.getString("imglink");
            JSONArray mAuthor = (JSONArray) mData.get("authors");
            String mAuthors[] = new String[(mAuthor.length() > 0 ? mAuthor.length() : 1)];//at least size of 1 array
            for (int i = 0; i < mAuthor.length(); i++)
                mAuthors[i] = mAuthor.getString(i);
            if (mPublishDate.equals("null") || mPublishDate.equals(""))
                mPublishDate = "UNKNOWN";
            if (mPublisher.equals("null") || mPublisher.equals(""))
                mPublisher = "UNKNOWN";
            if (mAuthors[0].equals("null") || mAuthors[0].equals(""))
                mAuthors[0] = "UNKNOWN";
            if (mImage.equals("null") || mImage.equals(""))
                mImage = "";
            book_title.setText(mBookName);
            book_publishedDate.setText(mPublishDate);
            book_publisher.setText(mPublisher);
            for (int i = 0; i < mAuthors.length; i++)
                book_authors.setText(book_authors.getText() + (i == 0 ? "" : ",") + mAuthors[i]); //old + new + comma
            if (!mImage.equals(""))
                new DownloadImageTask(book_image).execute(mImage);
        } catch (JSONException e) {
            Librarian.logged(e);
        }
    }

    //Issue Book Finally
    private class PleaseIssueBook extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Issuing Book, Please Wait...");
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
                    nameValuePair.add(new BasicNameValuePair("mUserId", params[0]));
                    nameValuePair.add(new BasicNameValuePair("mSerialNo", params[1]));
                    nameValuePair.add(new BasicNameValuePair("mIssue", params[2]));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                    HttpResponse response = mClient.execute(httpPost);
                    String mResponseData = EntityUtils.toString(response.getEntity());
                    Log.d("errror",mResponseData);
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
                Librarian.logged(e);
            }
            return mJSONResponse;
        }

        @Override
        protected void onPostExecute(JSONObject mData) {
            mDialog.hide();
            try {
                String mStatus = mData.getString("status");
                if(mStatus.equals("1"))
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Issued Successfully", Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
            } catch (Exception e) {
                Librarian.logged(e);
            }
        }
    }

    //<editor-fold desc="LoadBookDetails">
    private class LoadBookDetails extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Checking Online Database, Please Wait...");
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
                    nameValuePair.add(new BasicNameValuePair("issueISBN", params[0]));
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
    //</editor-fold>

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bImage;
        ProgressDialog mDialog;

        public DownloadImageTask(ImageView book_image) {
            bImage = book_image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Loading Image, Please Wait...");
            mDialog.setIndeterminate(true);
            mDialog.setCanceledOnTouchOutside(false);
            //mDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mBookImage = null;
            try {
                InputStream in = new URL(urlDisplay).openStream();
                mBookImage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return mBookImage;
        }

        protected void onPostExecute(Bitmap result) {
            //mDialog.hide();
            bImage.setImageBitmap(result);
        }
    }
}