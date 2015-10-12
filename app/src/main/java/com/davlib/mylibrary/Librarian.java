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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Librarian extends ActionBarActivity implements AdapterView.OnItemClickListener {
    final public static String mServerAddress = "http://mylibrary.esy.es/lib";
    final public static int mFinePerDay = 2;
    boolean isInternetAvailable = false;
    private static String mRequest = null, mMain = null;//get return result
    final int ID_NEWBOOK = 1, ID_NEWUSER = 2, ID_LOGOUT = 3;
    int emp_id = 1; // change later
    DrawerLayout mDrawerLayout; // for drawerlayout
    ActionBarDrawerToggle mActionBarToogle; //for set drawerlistner
    ArrayList<NavMenu> nav_list = new ArrayList<NavMenu>(), sub_nav_list = new ArrayList<NavMenu>();
    ListView mList, mSubList; // mList and sublist
    int[] mListIcon = new int[] //initializing mList icon
            {R.drawable.ic_action_action_home, R.drawable.ic_action_issue_book, R.drawable.ic_action_return_book,
                    R.drawable.ic_action_user_information_256, R.drawable.ic_action_search_book_512, R.drawable.ic_action_book_add_256,
                    R.drawable.ic_action_user_add_256, R.drawable.ic_action_editor_mode_edit};
    int[] mSubListIcon = new int[] //initializing sub mList icon
            {R.drawable.ic_action_user_information_256, R.drawable.ic_action_password_text_01_256};
    String mNavData[]; //initializing mList
    String mSubNavData[];//initializing sub mList
    FragmentManager mFragmentManager; //fragment manager object declare
    FragmentTransaction mFragmentTransaction; //fragment trasaction object declare
    LinearLayout mNavigationContainer; //mList parent used in drawerToggle
    boolean subMenu; //check selected option has Submenu or not
    public int save = 0, msave = -1; // save use for last position selected item in navigation list
    private DBHelper mDatabase;
    private Boolean exit = false;
    private ProgressDialog mDialog;

    //Debug Purpose
    public static void logged(Exception msg) {
        Log.d("errror", msg.getMessage());
        msg.printStackTrace();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.librarian_main);
        //<editor-fold desc="Initialization">
        emp_id = getIntent().getIntExtra("empid",1);
        mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("Loading, Please Wait...");
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        mNavigationContainer = (LinearLayout) findViewById(R.id.navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawLayout);
        mList = (ListView) findViewById(R.id.navList);
        mSubList = (ListView) findViewById(R.id.navList2);
        mActionBarToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close) {
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
                if (msave == -1) //if sublist is not selected then selected item will remain same
                {
                    mList.getChildAt(7).setBackgroundColor(
                            Color.parseColor("#ffffff"));
                    mSubList.setVisibility(View.INVISIBLE);
                    mList.getChildAt(save).setBackgroundColor(
                            Color.parseColor("#bcbbb5"));
                }
            }
        };
        Resources mRes = getResources();
        mNavData = mRes.getStringArray(R.array.NavigationMenuItems);
        mSubNavData = mRes.getStringArray(R.array.SubNavigationMenuItems);
        //initializing main navigation mList
        for (int i = 0; i <= 7; i++) {
            NavMenu item = new NavMenu();
            item.setTitle(mNavData[i]);
            item.setIcon(mListIcon[i]);
            nav_list.add(item);
        }
        //initializing sub navigation mList
        for (int i = 0; i < 2; i++) {
            NavMenu item = new NavMenu();
            item.setTitle(mSubNavData[i]);
            item.setIcon(mSubListIcon[i]);
            sub_nav_list.add(item);
        }
        try {
            isInternetAvailable = new ConnectionDetector(getApplicationContext()).isConnectingToInternet();
            if (!isInternetAvailable) {
                new AlertDialog.Builder(this)
                        .setTitle("No INTERNET CONNECTIVITY")
                        .setMessage("Please Connect Your Device with INTERNET")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        }).show();
            }
        } catch (Exception e) {
            Librarian.logged(e);
        }
        mList.setAdapter(new NavBaseAdapter(getApplicationContext(), nav_list));
        mSubList.setAdapter(new NavBaseAdapter(getApplicationContext(), sub_nav_list));
        mFragmentManager = getSupportFragmentManager();
        mDrawerLayout.setDrawerListener(mActionBarToogle);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mDatabase = new DBHelper(this); // initialize database
        //</editor-fold>
        mList.setOnItemClickListener(this);
        mSubList.setOnItemClickListener(this);

        //handling request from different intents like after scan code get isbn no.
        int tsel = 0;
        try {
            mRequest = getIntent().getStringExtra("request");
            mMain = getIntent().getStringExtra("main");
        } catch (NullPointerException e) {
            mRequest = null;
        }

        if (mRequest != null) {
            if (mRequest.equals("issue")) {
                try {
                    String mISBN = getIntent().getStringExtra("result");
                    String mUserId = getIntent().getStringExtra("mUserId");
                    String mResultType = getIntent().getStringExtra("resultType");
                    if (mResultType.equals("EAN_13")) {
                        save = 1;
                        mFragmentTransaction = mFragmentManager.beginTransaction();
                        mFragmentTransaction.replace(R.id.fragmentHolder, new IssueBook(mISBN, mUserId)).commit();
                    } else {
                        Toast.makeText(this, "INVALID ISBN NUMBER", Toast.LENGTH_SHORT).show();
                        mFragmentTransaction = mFragmentManager.beginTransaction();
                        mFragmentTransaction.replace(R.id.fragmentHolder, new Librarian_Home()).commit();
                        save = 0;
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mList.getChildAt(save).setBackgroundColor(
                                    Color.parseColor("#bcbbb5"));
                        }
                    }, 1000);
                } catch (Exception e) {
                    Librarian.logged(e);
                }
            } else if (mRequest.equals("addnewbook")) {
                try {
                    String mISBN = getIntent().getStringExtra("result");
                    String mResultType = getIntent().getStringExtra("resultType");
                    if (mResultType.equals("EAN_13") || mResultType.equals("EAN_10")) {
                        save = 5;
                        mFragmentTransaction = mFragmentManager.beginTransaction();
                        mFragmentTransaction.replace(R.id.fragmentHolder, new AddNewBook(mISBN, mResultType)).commit();
                    } else {
                        Toast.makeText(this, "INVALID ISBN NUMBER", Toast.LENGTH_SHORT).show();
                        mFragmentTransaction = mFragmentManager.beginTransaction();
                        mFragmentTransaction.replace(R.id.fragmentHolder, new Librarian_Home()).commit();
                        save = 0;
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mList.getChildAt(save).setBackgroundColor(
                                    Color.parseColor("#bcbbb5"));
                        }
                    }, 1000);
                } catch (Exception e) {
                    Librarian.logged(e);
                }
            } else if (mRequest.equals("scanusercode")) {
                try {
                    String mExtras = null;
                    String mUserId = getIntent().getStringExtra("result");
                    String mResultType = getIntent().getStringExtra("resultType");
                    try {
                        mExtras = getIntent().getStringExtra("extras");
                    } catch (Exception e) {
                        mExtras = null;
                    }
                    save = 1;
                    mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.setCustomAnimations(R.anim.slide_down_from_up, 0);
                    if (mMain.equals("issue"))
                        mFragmentTransaction.replace(R.id.fragmentHolder, new IssueMeBook(mUserId)).commit();
                    else if (mMain.equals("return"))
                        mFragmentTransaction.replace(R.id.fragmentHolder, new ReturnMyBook(mUserId)).commit();
                    else if (mMain.equals("userDetails"))
                        mFragmentTransaction.replace(R.id.fragmentHolder, new ShowUserDetails(mUserId,mExtras)).commit();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mList.getChildAt(save).setBackgroundColor(
                                    Color.parseColor("#bcbbb5"));
                        }
                    }, 1000);
                } catch (Exception e) {
                    Librarian.logged(e);
                }

            }
        } else {
            //initialize Home Fragment
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.fragmentHolder, new Librarian_Home());
            mFragmentTransaction.commit();
            tsel = 0;
            mDialog.show();
            //select HOME in navigation giving time for setAdapter
            Handler hn = new Handler();
            final int setTsel = tsel;
            hn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mList.getChildAt(setTsel).setBackgroundColor(
                                Color.parseColor("#bcbbb5"));
                        mDialog.hide();
                    } catch (Exception e) {
                        Log.d("errror", e.getMessage());
                    }
                }
            }, 1000);
        }
    }

    //hamburger icon
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarToogle.syncState();
    }

    //create action bar icons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem item = menu.add(Menu.NONE, ID_NEWBOOK, 103, "Add New Book"); // add new Book to Library
        MenuItem item2 = menu.add(Menu.NONE, ID_NEWUSER, 104, "Add New User"); // Add new User
        MenuItem item3 = menu.add(Menu.NONE, ID_LOGOUT, 105, "LogOut"); // LogOut
        Drawable dr = getResources().getDrawable(R.drawable.ic_action_book_add_256);
        item.setIcon(dr);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item3.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        dr = getResources().getDrawable(R.drawable.ic_action_user_add_256);
        item2.setIcon(dr);
        dr = getResources().getDrawable(R.drawable.ic_action_logout_256);
        item3.setIcon(dr);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //handle actionbar item click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mNavigationContainer))
                mDrawerLayout.closeDrawer(mNavigationContainer);
            else
                mDrawerLayout.openDrawer(mNavigationContainer);
        } else if (id == ID_NEWBOOK) {
            Intent Scanner = new Intent(getApplicationContext(), ScanBarCode.class);
            Scanner.putExtra("previousSelection", Integer.toString(save));
            Scanner.putExtra("commingFor", "addnewbook");
            startActivityForResult(Scanner, 1);
        } else if (id == ID_LOGOUT) {

            try {
                if (mDatabase.onDelete() != -1) {
                    startActivity(new Intent(getApplicationContext(), SelectUserType.class));
                    finish();
                }
            } catch (Exception e) {
                Librarian.logged(e);
            }
        } else if (id == ID_NEWUSER) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.setCustomAnimations(R.anim.slide_up_from_down, 0);
            mFragmentTransaction.replace(R.id.fragmentHolder, new AddNewUser()).commit();
        }
        return super.onOptionsItemSelected(item);
    }


    //handle navmenu click
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imm.hideSoftInputFromWindow(Librarian.this.getCurrentFocus().getWindowToken(), 0);
                }
            }, 400);
        } catch (Exception e) {
            this.logged(e);
        }
        final ProgressDialog dialog = new ProgressDialog(Librarian.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        try {
            if (parent.getId() == R.id.navList) //main navigation
            {
                parent.getChildAt(position).setBackgroundColor(
                        Color.parseColor("#bcbbb5")); // select color change of selected navigation item
                if (save != -1 && save != position) {
                    parent.getChildAt(save).setBackgroundColor(
                            Color.parseColor("#ffffff"));
                }
                mSubList.setVisibility(View.INVISIBLE); // Hide SubMenu
                subMenu = false; // set SubMenu Exist to False
                // sub menu selected item unselect in case if selected
                if (position != 7) {
                    mSubList.getChildAt(0).setBackgroundColor(Color.parseColor("#bcbbb5"));
                    mSubList.getChildAt(1).setBackgroundColor(Color.parseColor("#bcbbb5"));
                    msave = -1;
                }
                mFragmentTransaction = mFragmentManager.beginTransaction();
                //select action of each menu item clicked
                switch (position) {
                    case 0:
                        mFragmentTransaction.replace(R.id.fragmentHolder, new Librarian_Home());
                        mFragmentTransaction.commit();
                        break;
                    case 1:
                        Intent Scanner = new Intent(getApplicationContext(), ScanBarCode.class);
                        Scanner.putExtra("previousSelection", Integer.toString(save));
                        Scanner.putExtra("commingFor", "scanusercode");
                        Scanner.putExtra("main", "issue");
                        startActivityForResult(Scanner, 1);
                        break;
                    case 2:
                        Scanner = new Intent(getApplicationContext(), ScanBarCode.class);
                        Scanner.putExtra("previousSelection", Integer.toString(save));
                        Scanner.putExtra("commingFor", "scanusercode");
                        Scanner.putExtra("main", "return");
                        startActivityForResult(Scanner, 1);
                        break;
                    case 3:
                        Scanner = new Intent(getApplicationContext(), ScanBarCode.class);
                        Scanner.putExtra("previousSelection", Integer.toString(save));
                        Scanner.putExtra("commingFor", "scanusercode");
                        Scanner.putExtra("main", "userDetails");
                        startActivityForResult(Scanner, 1);
                        break;
                    case 4://Search
                        mFragmentTransaction.setCustomAnimations(R.anim.slide_up_from_down, 0);
                        mFragmentTransaction.replace(R.id.fragmentHolder, new BookSearch()).commit();
                        break;
                    case 5:
                        Scanner = new Intent(getApplicationContext(), ScanBarCode.class);
                        Scanner.putExtra("previousSelection", Integer.toString(save));
                        Scanner.putExtra("commingFor", "addnewbook");
                        Scanner.putExtra("main", "newbook");
                        startActivityForResult(Scanner, 1);
                        break;
                    case 6: //add new user
                        mFragmentTransaction.setCustomAnimations(R.anim.slide_up_from_down, 0);
                        mFragmentTransaction.replace(R.id.fragmentHolder, new AddNewUser()).commit();
                        break;
                    case 7:
                        mSubList.setVisibility(View.VISIBLE); // show Visibility
                        subMenu = true;
                        break;
                }
                if (position != 7) //store current position
                    save = position;
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
                mFragmentTransaction = mFragmentManager.beginTransaction();
                switch (position) {
                    case 0:
                        mFragmentTransaction.setCustomAnimations(R.anim.slide_up_from_down, 0);
                        mFragmentTransaction.replace(R.id.fragmentHolder, new YourDetails("lib",Integer.toString(emp_id))).commit();
                        break;
                    case 1:
                        mFragmentTransaction.setCustomAnimations(R.anim.slide_up_from_down, 0);
                        mFragmentTransaction.replace(R.id.fragmentHolder, new ChangePassword("lib",Integer.toString(emp_id))).commit();
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
                                mDrawerLayout.closeDrawer(mNavigationContainer);
                        } catch (Exception e) {
                            Log.d("swipe", e.getMessage());
                        }
                    }
                }, 1000);
            }
        } catch (Exception e) {
            Log.d("errror", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            save = Integer.parseInt(data.getStringExtra("save"));
            mList.getChildAt(1).setBackgroundColor(
                    Color.parseColor("#ffffff"));
            mList.getChildAt(save).setBackgroundColor(
                    Color.parseColor("#bcbbb5"));
        }

    }

    @Override
    public void onBackPressed() {
        String loc = this.getTitle().toString();
        if (loc.equals("MyLibrary"))
            if (exit) {
                finish();
            } else {
                Toast.makeText(Librarian.this, "Press Back to Exit", Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3000);
            }
        else if(loc.equals("UserDetails")){
            this.finish();
        }
        else {
            save = 0;
            for (int i = 1; i <= 7; i++)
                mList.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
            for (int i = 0; i <= 1; i++)
                mSubList.getChildAt(i).setBackgroundColor(Color.parseColor("#bcbbb5"));

            mList.getChildAt(save).setBackgroundColor(
                    Color.parseColor("#bcbbb5"));
            mSubList.setVisibility(View.INVISIBLE);

            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.slide_down);
            mFragmentTransaction.replace(R.id.fragmentHolder, new Librarian_Home()).commit();
        }
    }
}
