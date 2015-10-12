package com.davlib.mylibrary;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arshpreet on 10/8/2015.
 */
public class FetchBookDetailsFromSerial {
    String mSerialNo;
    Context mContext;
    private ArrayList<SearchResult> mSResult = new ArrayList<SearchResult>();
    FetchBookDetailsFromSerial(String serial, Context context) {
        this.mSerialNo = serial;
        this.mContext = context;
    }
    void showData() {
        new FetchBookDetails(mContext).execute(mSerialNo);
    }


    private class FetchBookDetails extends AsyncTask<String, Void, JSONObject> {
        Context mContext;
        ProgressDialog mDialog;
        FetchBookDetails(Context mContext) {
            this.mContext = mContext;
            mDialog = new ProgressDialog(mContext);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Loading Book Details, Please Wait...");
            mDialog.setIndeterminate(true);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject mJSONResponse = null;
            try {
                if (new ConnectionDetector(mContext.getApplicationContext()).isConnectingToInternet()) {
                    HttpClient mClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(Librarian.mServerAddress + "/fetch.php");
                    List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                    nameValuePair.add(new BasicNameValuePair("mSearch", "1"));
                    nameValuePair.add(new BasicNameValuePair("mSearchRefine", "serial"));
                    nameValuePair.add(new BasicNameValuePair("mSearchQuery", params[0]));
                    nameValuePair.add(new BasicNameValuePair("mStart", "0"));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                    HttpResponse response = mClient.execute(httpPost);
                    String mResponseData = EntityUtils.toString(response.getEntity());
                    Log.d("errror",mResponseData);
                    mJSONResponse = new JSONObject(mResponseData);
                } else {
                    Log.d("errror","no Iternet");
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                    alertDialog.setTitle("NO INTERNET CONNECTIVITY");
                    alertDialog.setMessage("PLEASE CONNECT TO INTERNET!");
                    alertDialog.setPositiveButton("OK", null);
                    return null;
                }
            } catch (Exception e) {
                Librarian.logged(e);
            }
            return mJSONResponse;
        }

        @Override
        protected void onPostExecute(JSONObject mData) {
            try {
                String mCount = mData.getString("count");
                String mBooks[] = new String[(Integer.parseInt(mCount) == 0 ? 1 : Integer.parseInt(mCount))];
                if (mCount.equals("0"))
                    mBooks[0] = "No Result Found";
                else {
                    String mTempDesc;
                    JSONObject temp;
                    for (int i = 0; i < Integer.parseInt(mCount); i++) {
                        temp = new JSONObject(mData.getString("S" + Integer.toString(i)));
                        SearchResult mItem = new SearchResult();
                        ArrayList<IssuedDetails> mIssuedD = new ArrayList<IssuedDetails>();
                        mTempDesc = (temp.getString("description").equals("UNKNOWN...") ? "No Description Available" : temp.getString("description"));
                        mItem.setmFullDescription(mTempDesc);
                        mTempDesc = mTempDesc.substring(0, (mTempDesc.length() > 100 ? 97 : mTempDesc.length())) + "...";
                        mItem.setmTitle(temp.getString("name"));
                        mItem.setmISBN(temp.getString("isbn"));
                        mItem.setmQuantity(temp.getString("quantity"));
                        mItem.setmImage(temp.getString("image"));
                        mItem.setmDescription(mTempDesc);
                        String mIssuedDet = temp.getString("issued");
                        JSONObject mIssuedDetail = new JSONObject(mIssuedDet);
                        if (mIssuedDetail.getString("count").equals("0")) {
                            IssuedDetails mIssuedDetailStorage = new IssuedDetails();
                            mIssuedDetailStorage.setmDateOfReturn("0");
                            mIssuedDetailStorage.setmRollNo("0");
                            mIssuedD.add(mIssuedDetailStorage);
                        }
                        for (int j = 0; j < Integer.parseInt(mIssuedDetail.getString("count")); j++) {
                            IssuedDetails mIssuedDetailStorage = new IssuedDetails();
                            JSONArray mIssueNum = (JSONArray) mIssuedDetail.get("I" + j);
                            String mDOR = mIssueNum.getString(0);
                            String mROLL = mIssueNum.getString(1);
                            mIssuedDetailStorage.setmDateOfReturn(mDOR);
                            mIssuedDetailStorage.setmRollNo(mROLL);
                            mIssuedD.add(mIssuedDetailStorage);
                        }
                        mItem.setmIssuedDetails(mIssuedD);
                        mItem.setmAuthor((temp.getString("author").equals("UNKNOWN") ? "" : temp.getString("author")));
                        mItem.setmPublisher((temp.getString("publisher").equals("UNKNOWN") ? "" : temp.getString("publisher")));
                        mItem.setmPublishedDate((temp.getString("publishedDate").equals("UNKNOWN") ? "" : temp.getString("publishedDate")));
                        mSResult.add(mItem);
                    }
                    mDialog.hide();
                    Intent mShowResult = new Intent(mContext.getApplicationContext(), DetailSearchResult.class);
                    mShowResult.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    SearchResult mResult = mSResult.get(0);
                    mShowResult.putExtra("data", mResult);
                    mContext.startActivity(mShowResult);
                }
            } catch (Exception e) {
                Librarian.logged(e);
            }
        }


    }
}

