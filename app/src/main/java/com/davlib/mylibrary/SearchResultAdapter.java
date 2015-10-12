/*
 * Copyright (c) 2015.
 * Created By Arshpreet Wadehra on 9/24/15 4:38 PM
 * Follow @WadehrArshpreet
 */

package com.davlib.mylibrary;

/**
 * Created by Arshpreet on 9/24/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class SearchResultAdapter extends BaseAdapter {
    ArrayList<SearchResult> mSearchResult = new ArrayList<SearchResult>();
    Context context;
    LayoutInflater inflater;

    public SearchResultAdapter(Context context, ArrayList<SearchResult> mSearchResult) {
        this.mSearchResult = mSearchResult;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mSearchResult.size();
    }

    @Override
    public SearchResult getItem(int position) {
        return mSearchResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyObject myObj;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.search_result_layout, null);
            myObj = new MyObject();
            convertView.setTag(myObj);
        } else {
            myObj =  (MyObject)convertView.getTag();
        }
        myObj.title = (TextView) convertView.findViewById(R.id.mBookTitle);
        myObj.serial = (TextView) convertView.findViewById(R.id.mBookSerial);
        myObj.publisher = (TextView) convertView.findViewById(R.id.mBookPublisher);
        myObj.description = (TextView) convertView.findViewById(R.id.mBookDescription);
        myObj.title.setText(mSearchResult.get(position).getmTitle());
        myObj.serial.setText(mSearchResult.get(position).getmQuantity());
        myObj.publisher.setText(mSearchResult.get(position).getmAuthor());
        myObj.description.setText(mSearchResult.get(position).getmDescription());
        return convertView;
    }

    private class MyObject {
        TextView title,description,serial,publisher;
    }
}
