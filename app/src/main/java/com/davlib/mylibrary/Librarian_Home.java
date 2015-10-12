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

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;


public class Librarian_Home extends Fragment implements View.OnClickListener {

    private Intent mAction;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    ListView mList;

    public Librarian_Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_librarian__home, container, false);
        getActivity().setTitle("MyLibrary");
        LinearLayout mIssueBook = (LinearLayout) v.findViewById(R.id.issue_click);
        LinearLayout mReturnBook = (LinearLayout) v.findViewById(R.id.return_click);
        LinearLayout mAddUser = (LinearLayout) v.findViewById(R.id.add_user_click);
        LinearLayout mAddBook = (LinearLayout) v.findViewById(R.id.add_book_click);
        LinearLayout mUserDetails = (LinearLayout) v.findViewById(R.id.user_detail_click);
        LinearLayout mSearchBook = (LinearLayout) v.findViewById(R.id.search_click);
        mList = (ListView) getActivity().findViewById(R.id.navList);
        mIssueBook.setOnClickListener(this);
        mReturnBook.setOnClickListener(this);
        mAddBook.setOnClickListener(this);
        mAddUser.setOnClickListener(this);
        mUserDetails.setOnClickListener(this);
        mSearchBook.setOnClickListener(this);
        mFragmentManager = getActivity().getSupportFragmentManager();
        //   t.setText(new MD5("helloworld").encode());

        return v;
    }


    @Override
    public void onClick(View v) {
        int save = 0;
        switch (v.getId()) {
            case R.id.issue_click: //issue Book
                save =1;
                mAction = new Intent(getActivity().getApplicationContext(), ScanBarCode.class);
                mAction.putExtra("previousSelection", Integer.toString(0));
                mAction.putExtra("commingFor", "scanusercode");
                mAction.putExtra("main", "issue");
                startActivityForResult(mAction, 1);
                break;
            case R.id.return_click:
                save =2;
                mAction = new Intent(getActivity().getApplicationContext(), ScanBarCode.class);
                mAction.putExtra("previousSelection", Integer.toString(0));
                mAction.putExtra("commingFor", "scanusercode");
                mAction.putExtra("main", "return");
                startActivityForResult(mAction, 1);
                break;
            case R.id.search_click:
                save =4;
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.setCustomAnimations(R.anim.slide_up_from_down,0);
                mFragmentTransaction.replace(R.id.fragmentHolder, new BookSearch()).commit();
                break;
            case R.id.add_book_click:
                save =5;
                mAction = new Intent(getActivity().getApplicationContext(), ScanBarCode.class);
                mAction.putExtra("previousSelection", Integer.toString(0));
                mAction.putExtra("commingFor", "addnewbook");
                mAction.putExtra("main", "newbook");
                startActivityForResult(mAction, 1);
                break;
            case R.id.add_user_click:
                save =6;
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.setCustomAnimations(R.anim.slide_up_from_down,0);
                mFragmentTransaction.replace(R.id.fragmentHolder, new AddNewUser()).commit();
                break;
            case R.id.user_detail_click:
                save =3;
                mAction = new Intent(getActivity().getApplicationContext(), ScanBarCode.class);
                mAction.putExtra("previousSelection", Integer.toString(0));
                mAction.putExtra("commingFor", "scanusercode");
                mAction.putExtra("main", "userDetails");
                startActivityForResult(mAction, 1);
                break;
        }

        for (int i = 0; i < 7; i++)
            mList.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
        mList.getChildAt(save).setBackgroundColor(
                Color.parseColor("#bcbbb5"));
    }
}
