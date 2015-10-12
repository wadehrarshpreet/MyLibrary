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
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class Student extends ActionBarActivity implements AdapterView.OnItemClickListener {
    DrawerLayout drawLay; // for drawerlayout
    ActionBarDrawerToggle actionBarToogle; //for set drawerlistner
    ArrayList<NavMenu> nav_list = new ArrayList<NavMenu>(), sub_nav_list = new ArrayList<NavMenu>();
    ListView list, subList; // mList and sublist
    int[] list_icon = new int[] //initializing mList icon
            {R.drawable.ic_action_action_home, R.drawable.ic_action_issued_book, R.drawable.ic_action_indian_rupee_xxl,
                    R.drawable.ic_action_search_book_512, R.drawable.ic_action_heart_256, R.drawable.ic_action_editor_mode_edit};
    int[] sub_list_icon = new int[] //initializing sub mList icon
            {R.drawable.ic_action_user_information_256, R.drawable.ic_action_password_text_01_256};
    String navData[] = {"Home", "Issued Books", "Check Fine", "Search Book", "WishList", "Edit Profile"};//initializing mList
    String subNavData[] = {"Your Details", "Change Password"};//initializing sub mList
    FragmentManager fm; //fragment manager object declare
    FragmentTransaction ftrans; //fragment trasaction object declare
    LinearLayout navigation; //mList parent used in drawerToggle
    boolean subMenu; //check selected option has Submenu or not
    int save = -1, msave = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_main);
        //<editor-fold desc="Initialization">
        navigation = (LinearLayout) findViewById(R.id.navigation);
        drawLay = (DrawerLayout) findViewById(R.id.drawLayout);
        list = (ListView) findViewById(R.id.navList);
        subList = (ListView) findViewById(R.id.navList2);
        actionBarToogle = new ActionBarDrawerToggle(this, drawLay, R.string.open, R.string.close) {
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
                if (msave == -1) //if sublist is not selected then selected item will remain same
                {
                    list.getChildAt(5).setBackgroundColor(
                            Color.parseColor("#ffffff"));
                    subList.setVisibility(View.INVISIBLE);
                    if (save == -1) //incase nothing is selected at start
                        return;
                    list.getChildAt(save).setBackgroundColor(
                            Color.parseColor("#bcbbb5"));;
                }

            }
        };
        //initializing main mNavigationContainer mList
        for (int i = 0; i < 6; i++) {
            NavMenu item = new NavMenu();
            item.setTitle(navData[i]);
            item.setIcon(list_icon[i]);
            nav_list.add(item);
        }
        //initializing sub mNavigationContainer mList
        for (int i = 0; i < 2; i++) {
            NavMenu item = new NavMenu();
            item.setTitle(subNavData[i]);
            item.setIcon(sub_list_icon[i]);
            sub_nav_list.add(item);
        }
        list.setAdapter(new NavBaseAdapter(getApplicationContext(), nav_list));
        subList.setAdapter(new NavBaseAdapter(getApplicationContext(), sub_nav_list));
        fm = getSupportFragmentManager();
        drawLay.setDrawerListener(actionBarToogle);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //</editor-fold>
        list.setOnItemClickListener(this);
        subList.setOnItemClickListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarToogle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem item = menu.add(Menu.NONE, Menu.NONE, 103, "QR Code");
        MenuItem item2 = menu.add(Menu.NONE, Menu.NONE, 104, "Notification");
        MenuItem item3 = menu.add(Menu.NONE, Menu.NONE, 105, "LogOut");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item3.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        Drawable dr = getResources().getDrawable(R.drawable.ic_action_qr_code_256);
        item.setIcon(dr);
        dr = getResources().getDrawable(R.drawable.ic_action_ic_notifications_black_48dp);
        item2.setIcon(dr);
        dr = getResources().getDrawable(R.drawable.ic_action_logout_256);
        item3.setIcon(dr);
        getMenuInflater().inflate(R.menu.menu_student, menu);
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
            if (drawLay.isDrawerOpen(navigation))
                drawLay.closeDrawer(navigation);
            else
                drawLay.openDrawer(navigation);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ProgressDialog dialog = new ProgressDialog(Student.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        try {
            if (parent.getId() == R.id.navList) //main navigation
            {
                parent.getChildAt(position).setBackgroundColor(
                        Color.parseColor("#bcbbb5"));
                if (save != -1 && save != position) {
                    parent.getChildAt(save).setBackgroundColor(
                            Color.parseColor("#ffffff"));
                }
                if (position != 5)
                    save = position;
                subList.setVisibility(View.INVISIBLE);
                subMenu = false;
                if (position != 7) {
                    subList.getChildAt(0).setBackgroundColor(Color.parseColor("#bcbbb5"));
                    subList.getChildAt(1).setBackgroundColor(Color.parseColor("#bcbbb5"));
                    msave = -1;
                }
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        subList.setVisibility(View.VISIBLE);
                        subMenu = true;
                        break;
                }
            } else if (parent.getId() == R.id.navList2) // sub menu
            {
                subMenu = false;
                parent.getChildAt(position).setBackgroundColor(
                        Color.parseColor("#CACEFF"));
                if (msave != -1 && msave != position) {
                    parent.getChildAt(msave).setBackgroundColor(
                            Color.parseColor("#bcbbb5"));
                }
                msave = position;

                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;
                }


            }
            if (subMenu == false) { //if no submenu then close drawer and show and hide loading
                dialog.show();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.hide();
                            if (subMenu == false)
                                drawLay.closeDrawer(navigation);
                        } catch (Exception e) {
                            Log.d("swipe", e.getMessage());
                        }
                    }
                }, 500);
            }
        } catch (Exception e) {
            Log.d("errror", e.getMessage());
        }
    }
}
