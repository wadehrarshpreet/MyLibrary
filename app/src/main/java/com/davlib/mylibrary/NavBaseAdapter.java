/*
 * Copyright (c) 2015. @WadehraArshpreet
 *
 * Created By Arshpreet Singh Wadehra 9/2/15 5:57 PM
 */

package com.davlib.mylibrary;
/***
 *Create By Arshpreet Singh Wadehra
 * @WadehrArshpreet
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NavBaseAdapter extends BaseAdapter {
    ArrayList<NavMenu> navList = new ArrayList<NavMenu>();
    Context context;
    LayoutInflater inflater;

    public NavBaseAdapter(Context context, ArrayList<NavMenu> navList) {
        this.navList = navList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return navList.size();
    }

    @Override
    public NavMenu getItem(int position) {
        return navList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyObject myObj;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.navigation_list, null);
            myObj = new MyObject();
            convertView.setTag(myObj);
        } else {
            myObj =  (MyObject)convertView.getTag();
        }
        myObj.title = (TextView) convertView.findViewById(R.id.navTitle);
        myObj.icon = (ImageView) convertView.findViewById(R.id.navIcon);
        myObj.title.setText(navList.get(position).getTitle());
        myObj.icon.setBackgroundResource(navList.get(position).getIcon());
        return convertView;
    }

    private class MyObject {
        TextView title;
        ImageView icon;
    }
}
