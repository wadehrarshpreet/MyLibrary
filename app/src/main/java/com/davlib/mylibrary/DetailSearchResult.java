/*
 * Copyright (c) 2015.
 * Created By Arshpreet Wadehra on 9/24/15 11:08 PM
 * Follow @WadehrArshpreet
 */

package com.davlib.mylibrary;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class DetailSearchResult extends ActionBarActivity {
    SearchResult mData;
    Button goBack;
    TextView mISBNNo, mBookTitle, mBookAuthor, mBookPublisher, mBookPublishedDate, mDescription, mAvailable;
    ImageView mBookImage;
    ArrayList<IssuedDetails> mIssuedDetails;
    ListView mReviewIssueRoll, mReviewIssueDOR;
    LinearLayout chkIssueDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_search_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mData = (SearchResult) getIntent().getSerializableExtra("data");
        this.setTitle(mData.getmTitle());
        goBack = (Button) findViewById(R.id.go_back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_right_right, R.anim.slide_right);
            }
        });
        mReviewIssueRoll = (ListView) findViewById(R.id.reviewIssueRoll);
        mReviewIssueDOR = (ListView) findViewById(R.id.reviewIssueDOR);
        chkIssueDetail = (LinearLayout) findViewById(R.id.issued_details);
        mIssuedDetails = mData.getmIssuedDetails();
        ArrayList<String> mIssuedDataDOR = new ArrayList<String>(), mIssuedDataRoll = new ArrayList<String>();
        chkIssueDetail.setVisibility(View.VISIBLE);
        for (int i = 0; i < mIssuedDetails.size(); i++) {
            if(mIssuedDetails.get(i).getmRollNo().equals("0"))
            {chkIssueDetail.setVisibility(View.GONE);
                break;}
            mIssuedDataDOR.add(mIssuedDetails.get(i).getmDateOfReturn());
            mIssuedDataRoll.add(mIssuedDetails.get(i).getmRollNo());
        }
        ViewGroup.LayoutParams lp = mReviewIssueDOR.getLayoutParams();
        lp.height = mIssuedDetails.size() * 100;
        mReviewIssueDOR.setLayoutParams(lp);
        ViewGroup.LayoutParams mLP = mReviewIssueRoll.getLayoutParams();
        mLP.height = mIssuedDetails.size() * 100;
        mReviewIssueRoll.setLayoutParams(mLP);
        mReviewIssueDOR.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mIssuedDataDOR));
        mReviewIssueRoll.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mIssuedDataRoll));
        mISBNNo = (TextView) findViewById(R.id.mISBNLoad);
        mBookTitle = (TextView) findViewById(R.id.book_title);
        mBookAuthor = (TextView) findViewById(R.id.book_authors);
        mBookPublishedDate = (TextView) findViewById(R.id.book_published_date);
        mBookPublisher = (TextView) findViewById(R.id.book_publisher);
        mDescription = (TextView) findViewById(R.id.book_description);
        mAvailable = (TextView) findViewById(R.id.book_available);
        mBookImage = (ImageView) findViewById(R.id.book_image);
        mISBNNo.setText(mData.getmISBN());
        mBookTitle.setText(mData.getmTitle());
        if(mData.getmFullDescription().equals(""))
            mDescription.setText("UNKNOWN");
        else
            mDescription.setText(mData.getmFullDescription());
        mBookAuthor.setText(mData.getmAuthor());
        if (Integer.parseInt(mData.getmQuantity()) > 0)
            mAvailable.setText("Available in Library");
        else
            mAvailable.setText("Not Available in Library Currently");
        mBookPublishedDate.setText(mData.getmPublishedDate());
        mBookPublisher.setText(mData.getmPublisher());
        String mImage = mData.getmImage();
        mReviewIssueRoll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent result = new Intent(getApplicationContext(), Librarian.class);
                result.putExtra("request", "scanusercode");
                result.putExtra("main", "userDetails");
                result.putExtra("extras", "getdetailonly");
                result.putExtra("result", mIssuedDetails.get(position).getmRollNo());
                result.putExtra("resultType", "QR_CODE");
                startActivity(result);
            }
        });
        if (mImage.equals("") || mImage.equals("null")) {
            return;
        } else
            new DownloadImageTask(mBookImage).execute(mImage);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_search_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            this.finish();
            overridePendingTransition(R.anim.slide_right_right, R.anim.slide_right);
        }

        return super.onOptionsItemSelected(item);
    }

    public void CopyMyISBN(View view) {
        String mISBN = (String) mISBNNo.getText();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("ISBN",mISBN);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(DetailSearchResult.this, "ISBN COPIED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
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
            mDialog = new ProgressDialog(getApplicationContext());
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_right_right, R.anim.slide_right);
    }

}
