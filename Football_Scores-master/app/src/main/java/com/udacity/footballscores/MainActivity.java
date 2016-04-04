package com.udacity.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {
    public static int selectedMatchId;
    public static int default_current_fragment = 2;
    public static int selectedTabAdapterPosition = 0;
    public static double MatchId;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PagerAdapter adapter;
    public static final String DONE_KEY = "DONE_EXTRA";
    public static final String DONE_LOADING = "DONE_LOADING";
    private Bundle storedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        Configuration config = FootballApp.getAppContext().getResources().getConfiguration();
        Intent intent= getIntent();

        tabLayout =
                (TabLayout) findViewById(R.id.tab_layout);


        //if (!(config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL)) {
        tabLayout.addTab(tabLayout.newTab().setText(Util.getTabName(this, 0)));
        tabLayout.addTab(tabLayout.newTab().setText(Util.getTabName(this, 1)));
        tabLayout.addTab(tabLayout.newTab().setText(Util.getTabName(this, 2)));
        tabLayout.addTab(tabLayout.newTab().setText(Util.getTabName(this, 3)));
        tabLayout.addTab(tabLayout.newTab().setText(Util.getTabName(this, 4)));

        //}
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new TabPagerAdapter
                (getSupportFragmentManager(),
                        tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                selectedMatchId = tab.getPosition();
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
        viewPager.setCurrentItem(default_current_fragment);
        if (savedInstanceState == null) {
            int check = (int) intent.getDoubleExtra("match_id",-1);
            if (check != -1) {
                selectedTabAdapterPosition = check;
            }
            tabLayout.setScrollPosition(default_current_fragment, 0f, true);
            //tabLayout.setupWithViewPager(viewPager);

            viewPager.setCurrentItem(default_current_fragment);

            selectedMatchId = default_current_fragment;
            Util.update_scores(this);
        } else {
            selectedMatchId = savedInstanceState.getInt("Selected_match",default_current_fragment);
            selectedTabAdapterPosition = savedInstanceState.getInt("Selected_adapter_position",0);
            MatchId= 0.0;

            viewPager.setCurrentItem(selectedMatchId);
            tabLayout.setScrollPosition(selectedMatchId, 0f, true);


        }
        storedInstanceState = savedInstanceState;
        // check network availability
        if(Util.isNetworkAvailable(this)) {
            Util.updateScores(this);
        } else {
            Util.showAlertDialog(getString(R.string.network_error).toString(), this);
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        viewPager.setCurrentItem(selectedMatchId);
        tabLayout.setScrollPosition(selectedMatchId, 0f, true);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt("Selected_match", selectedMatchId);
        outState.putInt("Selected_adapter_position", selectedTabAdapterPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        selectedMatchId = savedInstanceState.getInt("Selected_match", selectedMatchId);
        selectedTabAdapterPosition = savedInstanceState.getInt("Selected_adapter_position", 0);

    }

}
