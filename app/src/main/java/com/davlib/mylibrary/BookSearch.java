/*
 * Copyright (c) 2015.
 * Created By Arshpreet Wadehra on 9/22/15 10:39 PM
 * Follow @WadehrArshpreet
 */

package com.davlib.mylibrary;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

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


public class BookSearch extends Fragment implements AbsListView.OnScrollListener{
    private String mRefineItem[] = {"Title", "Author", "Publisher","ISBN"};
    AutoCompleteTextView mSearchQuery;
    Spinner mRefineSearch;
    private int visibleThreshold = 0;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<SearchResult> mSResult = new ArrayList<SearchResult>();
    private ListView mSearchResult,mList;
    public BookSearch() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_book__search, container, false);
        getActivity().setTitle("SEARCH BOOK");
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mList = (ListView) getActivity().findViewById(R.id.navList);
        for (int i = 0; i < 7; i++)
            mList.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
        mList.getChildAt(4).setBackgroundColor(
                Color.parseColor("#bcbbb5"));
        mSearchQuery = (AutoCompleteTextView) v.findViewById(R.id.search_query);
        mRefineSearch = (Spinner) v.findViewById(R.id.refine_search);
        mSearchResult = (ListView) v.findViewById(R.id.search_result);
        mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.simple_dropdown_item_1line, new String[]{""});
        mSearchQuery.setAdapter(mAdapter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.simple_spinner_item, mRefineItem);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mRefineSearch.setAdapter(adapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchQuery.requestFocus();
                imm.showSoftInput(mSearchQuery, 0);
            }
        }, 1000);
        mSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (String.valueOf(s).equals("No Result Found"))
                    mSearchQuery.setText("");
                if (s.length() >= 3) {
                    String mSearch = String.valueOf(mSearchQuery.getText());
                    String mRefine = String.valueOf(mRefineSearch.getSelectedItem().toString());
                    new UpdateAutoCompleteList().execute(mSearch, mRefine);
                } else {
                    mSearchQuery.clearListSelection();
                    mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.simple_dropdown_item_1line, new String[]{""});
                    mSearchQuery.setAdapter(mAdapter);
                    mSearchQuery.dismissDropDown();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSearchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.imeSearch || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String mSearch = String.valueOf(mSearchQuery.getText());
                    String mRefine = String.valueOf(mRefineSearch.getSelectedItem().toString());
                    visibleThreshold = 0;
                    new LoadSearchResult().execute(mSearch, mRefine, String.valueOf(visibleThreshold));
                }
                return false;
            }
        });
        mSearchQuery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mSearch = String.valueOf(mSearchQuery.getText());
                String mRefine = String.valueOf(mRefineSearch.getSelectedItem().toString());
                visibleThreshold = 0;
                new LoadSearchResult().execute(mSearch, mRefine, String.valueOf(visibleThreshold));
            }
        });

        mRefineSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mSearch = String.valueOf(mSearchQuery.getText());
                String mRefine = String.valueOf(mRefineSearch.getSelectedItem().toString());
                if(mSearch.length() >= 2)
                new UpdateAutoCompleteList().execute(mSearch, mRefine);
            //Just change autocomplete menu
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //when Search IS CLICKED
        mSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent mShowResult = new Intent(getActivity().getApplicationContext(),DetailSearchResult.class);
                    SearchResult mResult = (SearchResult) mSearchResult.getItemAtPosition(position);
                    mShowResult.putExtra("data", mResult);
                    startActivity(mShowResult);
                    getActivity().overridePendingTransition(R.anim.slide_left_left,R.anim.slide_left);
                } catch (Exception e) {
                    Librarian.logged(e);
                }
            }
        });
        return v;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    //Update AutoComplete List
    private class UpdateAutoCompleteList extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject mJSONResponse = null;
            try {
                if (new ConnectionDetector(getActivity().getApplicationContext()).isConnectingToInternet()) {
                    HttpClient mClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(Librarian.mServerAddress + "/fetch.php");
                    List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                    nameValuePair.add(new BasicNameValuePair("mSearch", "1"));
                    nameValuePair.add(new BasicNameValuePair("mSearchQuery", params[0]));
                    nameValuePair.add(new BasicNameValuePair("mSearchRefine", params[1]));
                    nameValuePair.add(new BasicNameValuePair("mStart", "0"));
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
                Librarian.logged(e);
            }
            return mJSONResponse;
        }

        @Override
        protected void onPostExecute(JSONObject mData) {
            try {
                String mCount = mData.getString("count");
                String mBooks[] = new String[(Integer.parseInt(mCount)==0?1:Integer.parseInt(mCount))];
                if(mCount.equals("0"))
                    mBooks[0] = "No Result Found";
                else
                {
                    JSONObject temp;
                    for(int i=0;i<Integer.parseInt(mCount);i++)
                    {
                        temp = new JSONObject(mData.getString("S"+Integer.toString(i)));
                        mBooks[i] = temp.getString("name");
                    }
                }
                mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.simple_dropdown_item_1line, mBooks);
                mSearchQuery.setAdapter(mAdapter);
                mSearchQuery.showDropDown();
            } catch (Exception e) {
                Librarian.logged(e);
            }
        }
    }

    //Load Search RESULT
    private class LoadSearchResult extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Loading Result, Please Wait...");
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
                    nameValuePair.add(new BasicNameValuePair("mSearch", "1"));
                    nameValuePair.add(new BasicNameValuePair("mSearchQuery", params[0]));
                    nameValuePair.add(new BasicNameValuePair("mSearchRefine", params[1]));
                    nameValuePair.add(new BasicNameValuePair("mStart", params[2]));
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
                Librarian.logged(e);
            }
            return mJSONResponse;
        }

        @Override
        protected void onPostExecute(JSONObject mData) {
            try {
                String mCount = mData.getString("count");
                String mBooks[] = new String[(Integer.parseInt(mCount)==0?1:Integer.parseInt(mCount))];
                String mISBN[] = new String[Integer.parseInt(mCount)];
                if(mCount.equals("0"))
                    mBooks[0] = "No Result Found";
                else
                {
                    mSResult.clear();
                    String mTempDesc;
                    JSONObject temp;
                    for(int i=0;i<Integer.parseInt(mCount);i++)
                    {
                        temp = new JSONObject(mData.getString("S"+Integer.toString(i)));
                        SearchResult mItem = new SearchResult();
                        ArrayList<IssuedDetails> mIssuedD = new ArrayList<IssuedDetails>();
                        mTempDesc = (temp.getString("description").equals("UNKNOWN...") ? "No Description Available" : temp.getString("description"));
                        mItem.setmFullDescription(mTempDesc);
                        mTempDesc = mTempDesc.substring(0,(mTempDesc.length()>100?97:mTempDesc.length()))+"...";
                        mItem.setmTitle(temp.getString("name"));
                        mItem.setmISBN(temp.getString("isbn"));
                        mItem.setmQuantity(temp.getString("quantity"));
                        mItem.setmImage(temp.getString("image"));
                        mItem.setmDescription(mTempDesc);
                        String mIssuedDet = temp.getString("issued");
                        JSONObject mIssuedDetail = new JSONObject(mIssuedDet);
                        if(mIssuedDetail.getString("count").equals("0"))
                        {
                            IssuedDetails mIssuedDetailStorage = new IssuedDetails();
                            mIssuedDetailStorage.setmDateOfReturn("0");
                            mIssuedDetailStorage.setmRollNo("0");
                            mIssuedD.add(mIssuedDetailStorage);
                        }
                        for(int j=0;j<Integer.parseInt(mIssuedDetail.getString("count"));j++)
                        {
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
                    mSearchResult.setAdapter(new SearchResultAdapter(getActivity().getApplicationContext(), mSResult));
                    mSearchQuery.dismissDropDown();
                    mSearchQuery.setSelection(0);
                    View mTempView = getActivity().getCurrentFocus();
                    if(mTempView != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mTempView.getWindowToken(), 0);
                    }
                }
            //Display Search Result
                mDialog.hide();

            } catch (Exception e) {
                Librarian.logged(e);
            }
        }
    }

}
