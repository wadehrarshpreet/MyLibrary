/*
 * Copyright (c) 2015.
 * Created By Arshpreet Wadehra on 9/18/15 11:25 PM
 * Follow @WadehrArshpreet
 */

package com.davlib.mylibrary;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewBook extends Fragment {
    private String mISBNNumber;
    private String mScanType, mImageUrl = "";
    private EditText book_title, book_authors, book_publisher, book_publishedDate, book_description;
    private ImageView book_image;
    private Button save_button;
    private FragmentManager mFragmentManager; //fragment manager object declare
    private FragmentTransaction mFragmentTransaction; //fragment trasaction object declare
    private Handler mHandler = new Handler();

    public AddNewBook() {
        // Required empty public constructor
    }

    public AddNewBook(String mISBNNumber, String mScanType) {
        // Required empty public constructor
        this.mISBNNumber = mISBNNumber;
        this.mScanType = mScanType;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_new_book, container, false);
        getActivity().setTitle("Add New Book");
        book_title = (EditText) v.findViewById(R.id.issue_book_title);
        book_authors = (EditText) v.findViewById(R.id.issue_book_authors);
        book_publisher = (EditText) v.findViewById(R.id.issue_book_publisher);
        book_publishedDate = (EditText) v.findViewById(R.id.issue_book_published_date);
        book_description = (EditText) v.findViewById(R.id.issue_book_description);
        book_image = (ImageView) v.findViewById(R.id.issue_book_image);
        save_button = (Button) v.findViewById(R.id.save_details);
        new LoadBookDetails().execute(mISBNNumber);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(book_title.getText()).equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Fill The Book Title", Toast.LENGTH_SHORT).show();
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            book_title.requestFocus();
                            imm.showSoftInput(book_title, 0);
                        }
                    }, 1000);
                } else {
                    if (String.valueOf(book_authors.getText()).equals(""))
                        book_authors.setText("UNKNOWN");
                    if (String.valueOf(book_publishedDate.getText()).equals(""))
                        book_publishedDate.setText("UNKNOWN");
                    if (String.valueOf(book_publisher.getText()).equals(""))
                        book_publisher.setText("UNKNOWN");
                    if (String.valueOf(book_description.getText()).equals(""))
                        book_description.setText("UNKNOWN");

                    new SaveBookDetails().execute(String.valueOf(book_title.getText()), String.valueOf(book_authors.getText()), String.valueOf(book_publisher.getText()), String.valueOf(book_publishedDate.getText()), String.valueOf(book_description.getText()), mISBNNumber, mImageUrl);
                }
            }
        });
        return v;
    }

    protected void updateData(JSONObject mData) {
        try {
            String mStatus = mData.getString("status");
            String mFrom = mData.getString("from");
            if (mStatus.equals("-1")) {
                Toast.makeText(getActivity().getApplicationContext(), "No Book Found. Please Enter Book Details Manaul", Toast.LENGTH_LONG).show();
                //open keyboard focus on book title
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        book_title.requestFocus();
                        imm.showSoftInput(book_title, 0);
                    }
                }, 1000);
                return;
            }
            if (mFrom.equals("db")) {
                book_title.setFocusableInTouchMode(false);
                book_authors.setFocusableInTouchMode(false);
                book_description.setFocusableInTouchMode(false);
                book_publishedDate.setFocusableInTouchMode(false);
                book_publisher.setFocusableInTouchMode(false);
                book_title.setFocusable(false);
                book_authors.setFocusable(false);
                book_description.setFocusable(false);
                book_publisher.setFocusable(false);
                book_publishedDate.setFocusable(false);
                if (!mData.getString("serial").equals("null"))
                    Toast.makeText(getActivity().getApplicationContext(), "Book Already In Library Saving Book Just Increase Quantity of Book", Toast.LENGTH_LONG).show();
            }
            String mBookName = mData.getString("book_name");
            String mPublishDate = mData.getString("publishedDate");
            String mPublisher = mData.getString("publisher");
            mImageUrl = mData.getString("imglink");
            String mDescription = mData.getString("description");
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
            if (mDescription.equals("null") || mDescription.equals(""))
                mDescription = "UNKNOWN";
            if (mImageUrl.equals("null") || mImageUrl.equals(""))
                mImageUrl = "";
            book_title.setText(mBookName);
            book_publishedDate.setText(mPublishDate);
            book_publisher.setText(mPublisher);
            book_description.setText(mDescription);
            for (int i = 0; i < mAuthors.length; i++)
                book_authors.setText(book_authors.getText() + (i == 0 ? "" : ",") + mAuthors[i]); //old + new + comma
            if (!mImageUrl.equals(""))
                new DownloadImageTask(book_image).execute(mImageUrl);
        } catch (JSONException e) {
            Librarian.logged(e);
        }
    }

    private class LoadBookDetails extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog mDialog;
        private int mResponseCode = -1;
        //private ImageView bookImg;

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
                    nameValuePair.add(new BasicNameValuePair("bisbn", params[0]));
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
            mDialog.show();
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
            mDialog.hide();
            bImage.setImageBitmap(result);
        }
    }

    private class SaveBookDetails extends AsyncTask<String, Void, Boolean> {
        ProgressDialog mDialog;
        JSONObject mJSONResponse;
        boolean success;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Saving Details, Please Wait...");
            mDialog.setIndeterminate(true);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            int mResponseCode = -1;
            try {
                if (new ConnectionDetector(getActivity().getApplicationContext()).isConnectingToInternet()) {
                    HttpClient mClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(Librarian.mServerAddress + "/index.php");
                    // Building post parameters, key and value pair
                    List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                    nameValuePair.add(new BasicNameValuePair("regBookName", params[0]));
                    nameValuePair.add(new BasicNameValuePair("regBookAuthors", params[1]));
                    nameValuePair.add(new BasicNameValuePair("regBookPublisher", params[2]));
                    nameValuePair.add(new BasicNameValuePair("regBookPublishedDate", params[3]));
                    nameValuePair.add(new BasicNameValuePair("regBookDescription", params[4]));
                    nameValuePair.add(new BasicNameValuePair("regBookISBN", params[5]));
                    nameValuePair.add(new BasicNameValuePair("regBookImage", params[6]));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                    HttpResponse response = mClient.execute(httpPost);
                    String mResponseData = EntityUtils.toString(response.getEntity());
                    mJSONResponse = new JSONObject(mResponseData);
                    success = (mJSONResponse.getString("status").equals("1") ? true : false);
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
            return success;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean == false)
                return;
            //Do after Book Details is Safe in Database
            mFragmentManager = getActivity().getSupportFragmentManager();
            View mTempView = getActivity().getCurrentFocus();
            if (mTempView != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTempView.getWindowToken(), 0);
            }
            try {
                if (mJSONResponse.getString("status").equals("0")) {
                    Toast.makeText(getActivity().getApplicationContext(), "There is Problem in Inserting a Book Details! Try Again", Toast.LENGTH_LONG).show();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFragmentTransaction = mFragmentManager.beginTransaction();
                            mFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.slide_down);
                            //set navigation selection
                            Librarian mTemp = (Librarian) getActivity();
                            mTemp.save = 0;
                            mTemp.mList.getChildAt(0).setBackgroundColor(
                                    Color.parseColor("#bcbbb5"));
                            mTemp.mList.getChildAt(5).setBackgroundColor(
                                    Color.parseColor("#ffffff"));
                            mFragmentTransaction.replace(R.id.fragmentHolder, new Librarian_Home()).commit();
                        }
                    }, 2000);
                }
            } catch (Exception e) {
                Librarian.logged(e);
            }
            try {
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.setCustomAnimations(R.anim.slide_left_left, R.anim.slide_left);
                mFragmentTransaction.replace(R.id.fragmentHolder, new PostAddNewBookDetails(mJSONResponse, String.valueOf(book_title.getText()), String.valueOf(book_authors.getText()), String.valueOf(book_publisher.getText()), String.valueOf(book_publishedDate.getText()), String.valueOf(book_description.getText()), mISBNNumber, mImageUrl)).commit();
            } catch (Exception e) {
                Librarian.logged(e);
            }
            mDialog.hide();
        }
    }


}
