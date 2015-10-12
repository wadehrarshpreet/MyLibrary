/*
 * Copyright (c) 2015.
 * Created By Arshpreet Wadehra on 9/19/15 1:52 PM
 * Follow @WadehrArshpreet
 */

package com.davlib.mylibrary;


import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostAddNewBookDetails extends Fragment {
    JSONObject mJSONResponse;
    private TextView book_title, book_authors, book_publisher, book_publishedDate, book_description, book_quantity,book_serial;
    private ImageView book_image;
    private Button done_button;
    private FragmentManager mFragmentManager; //fragment manager object declare
    private FragmentTransaction mFragmentTransaction; //fragment trasaction object declare
    private String mBookDetails[] = new String[8];
    public PostAddNewBookDetails() {
        // Required empty public constructor
    }


    public PostAddNewBookDetails(JSONObject jobj, String... param) {
        mJSONResponse = jobj;
        //get Book Details
        for (int i = 0; i < 7; i++)
            mBookDetails[i] = param[i];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_post_add_new_book_details, container, false);
        getActivity().setTitle("Book Detail");

        book_title = (TextView) v.findViewById(R.id.issue_book_title);
        book_serial = (TextView) v.findViewById(R.id.issue_book_serial);
        book_authors = (TextView) v.findViewById(R.id.issue_book_authors);
        book_publisher = (TextView) v.findViewById(R.id.issue_book_publisher);
        book_publishedDate = (TextView) v.findViewById(R.id.issue_book_published_date);
        book_description = (TextView) v.findViewById(R.id.issue_book_description);
        book_quantity = (TextView) v.findViewById(R.id.issue_book_quantity);
        book_image = (ImageView) v.findViewById(R.id.issue_book_image);
        done_button = (Button) v.findViewById(R.id.add_new_book_done);
        book_title.setText(mBookDetails[0]);
        book_authors.setText(mBookDetails[1]);
        book_publisher.setText(mBookDetails[2]);
        book_publishedDate.setText(mBookDetails[3]);
        book_description.setText(mBookDetails[4]);
        if (!mBookDetails[6].equals("")) {
            new DownloadImageTask(book_image).execute(mBookDetails[6]);
        }
        try {
            String mSerial = mJSONResponse.getString("serial");
            String mQuantity = mJSONResponse.getString("totalquantity");
            book_serial.setText(mSerial);
            book_quantity.setText(mQuantity);
            Toast.makeText(getActivity().getApplicationContext(), "Book Details is Saved Successfully in Database", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Librarian.logged(e);
        }
        mFragmentManager = getActivity().getSupportFragmentManager();
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.slide_down);
                Librarian mTemp = (Librarian) getActivity();
                mTemp.save = 0;
                mTemp.mList.getChildAt(0).setBackgroundColor(
                        Color.parseColor("#bcbbb5"));
                mTemp.mList.getChildAt(5).setBackgroundColor(
                        Color.parseColor("#ffffff"));
                mFragmentTransaction.replace(R.id.fragmentHolder, new Librarian_Home()).commit();
            }
        });
        return v;
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
}
