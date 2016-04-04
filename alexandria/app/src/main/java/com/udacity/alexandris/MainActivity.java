package com.udacity.alexandris;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.udacity.alexandria.R;

import com.udacity.alexandris.api.*;


public class MainActivity extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener, Callback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */



    private CharSequence title;
    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReciever;
    private Menu mMenu;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    public static final String DONE_KEY = "DONE_EXTRA";
    public static final String DONE_DELETING_KEY = "DONE_DELETING_KEY";
    public static final String DONE_DELETING = "DONE_DELETING";

    public static final String DONE_LOADING = "DONE_LOADING";
    public static final String STATE_SELECTED_POSITION = "position";
    private int mPosition;
    private Fragment fragmentDetail;
    private View myView;
    private Context mContext;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IS_TABLET = Utilities.isTablet(this);
        if (IS_TABLET) {
            setContentView(R.layout.activity_main_tablet);

        } else {
            setContentView(R.layout.activity_main);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        toolbar.setTitle(this.getString(R.string.books));
        setSupportActionBar(toolbar);
        mContext = this;
        messageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever, filter);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMenu = navigationView.getMenu();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (savedInstanceState == null) {

            mPosition = Integer.parseInt(prefs.getString("pref_startFragment", "0"));
            if (mPosition == 0) {
                getSupportActionBar().setTitle(this.getString(R.string.books));
                attachFragment(mPosition);

            } else if (mPosition == 1){
                getSupportActionBar().setTitle(this.getString(R.string.scan));
                attachFragment(mPosition);
            } else {
                getSupportActionBar().setTitle(this.getString(R.string.about));
                attachFragment(mPosition);
            }

        } else {
            mPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, mPosition);
            if(mPosition ==1 || mPosition ==2) {
                removeFragmentDetail();
            }
        }





    }

    @Override
    protected void onResume() {
        super.onResume();

      /*  if (mPosition == 0) {
            getSupportActionBar().setTitle(this.getString(R.string.books));
            onNavigationItemSelected(mMenu.findItem(R.id.nav_books));

        } else {
            getSupportActionBar().setTitle(this.getString(R.string.scan));
            onNavigationItemSelected(mMenu.findItem(R.id.nav_scan));
        }*/
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment = null;
        int id = item.getItemId();
        if (id == R.id.nav_books) {
            nextFragment = new ListOfBooks();
            if (findViewById(R.id.right_container) != null) {
                findViewById(R.id.right_container).setVisibility(View.VISIBLE);
            }
            getSupportActionBar().setTitle(this.getString(R.string.books));
            mPosition =0;


        } else if (id == R.id.nav_scan) {
            nextFragment = new AddBook();
            getSupportActionBar().setTitle(this.getString(R.string.scan));
            removeFragmentDetail();
            mPosition =1;

        } else if (id == R.id.nav_about) {
            nextFragment = new About();
            getSupportActionBar().setTitle(this.getString(R.string.about));
            removeFragmentDetail();
            mPosition =2;

        }


        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment)
                .commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void attachFragment(int position) {
        Fragment nextFragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position) {
            default:
            case 0:
                nextFragment = new ListOfBooks();
                if (findViewById(R.id.right_container) != null) {
                    findViewById(R.id.right_container).setVisibility(View.VISIBLE);
                }

                break;
            case 1:
                nextFragment = new AddBook();
                removeFragmentDetail();
                break;
            case 2:
                nextFragment = new About();
                removeFragmentDetail();
                break;
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment)
                .addToBackStack((String) title)
                .commit();
    }
    public void setTitle(int titleId) {
        title = getString(titleId);
    }
    public void removeFragmentDetail() {

        /*if (findViewById(R.id.right_container) != null && fragmentDetail != null) {
            int id = R.id.right_container;
            getSupportFragmentManager().beginTransaction()
                    .remove(fragmentDetail)
                    .commit();
            fragmentDetail = null;
        }*/
        if (findViewById(R.id.right_container) != null) {
            findViewById(R.id.right_container).setVisibility(View.GONE);
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(STATE_SELECTED_POSITION, mPosition);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {

            Bundle args = new Bundle();
            args.putString(BookDetail.EAN_KEY, ean);

            fragmentDetail = new BookDetail();
            fragmentDetail.setArguments(args);

            int id = R.id.container;
            if (findViewById(R.id.right_container) != null) {
                id = R.id.right_container;


                getSupportFragmentManager().beginTransaction()
                        .replace(id, fragmentDetail)
                        .commit();
            } else {
                Intent intent = new Intent(this, BookDetailActivity.class);
                       intent.putExtra(BookDetail.EAN_KEY, ean);
                startActivity(intent);
            }



    }

    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(MESSAGE_KEY) != null) {
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
                //Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
                //Snackbar.make(coordinatorLayout, intent.getStringExtra(MESSAGE_KEY), Snackbar.LENGTH_LONG)
                        //.show();
                Utilities.showAlertDialog(intent.getStringExtra(MESSAGE_KEY),mContext);
            }
        }
    }

    public void goBack(View view) {
        getSupportFragmentManager().popBackStack();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if(getSupportFragmentManager().getBackStackEntryCount()<2){
            finish();
        }
        super.onBackPressed();
    }


}