package com.udacity.footballscores;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.udacity.footballscores.data.DatabaseContract;
import com.udacity.footballscores.service.FetchService;


public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,Updatable {
    public ScoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentdate = new String[1];
    private int last_selected_item = -1;
    ListView mScoreList;
    public int mPosition;
    private BroadcastReceiver loadDoneReceiver;
    private Context mContext;

    public MainScreenFragment() {
        mContext = getContext();

    }



    public void setFragmentDate(String date, int position) {
        fragmentdate[0] = date;
        mPosition = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {




        View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);
        mScoreList = (ListView) rootView.findViewById(R.id.scores_list);
        mAdapter = new ScoresAdapter(getActivity(), null, 0);
        mScoreList.setAdapter(mAdapter);
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
        mAdapter.detail_match_id = MainActivity.selectedTabAdapterPosition;
        mScoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScoresAdapter.ViewHolder selected = (ScoresAdapter.ViewHolder) view.getTag();
                mAdapter.detail_match_id = selected.match_id;
                MainActivity.selectedTabAdapterPosition = (int) selected.match_id;
                // MainActivity.selectedTabAdapterPosition = position;
                mAdapter.notifyDataSetChanged();
            }
        });
        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();




        loadDoneReceiver = new LoadDoneReciever();
        IntentFilter filter = new IntentFilter(MainActivity.DONE_LOADING);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(loadDoneReceiver, filter);

    }

    private void restartLoader() {
        getLoaderManager().restartLoader(10, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, fragmentdate, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        mAdapter.swapCursor(cursor);
        if (cursor != null) {
            int i = 0;

            cursor.moveToFirst();
            if (MainActivity.selectedTabAdapterPosition != ListView.INVALID_POSITION) {


                while (!cursor.isAfterLast()) {
                    i++;
                    if (cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.MATCH_ID)) != MainActivity.selectedTabAdapterPosition) {
                        cursor.moveToNext();
                    } else {

                        break;
                    }
                }
                final int scrolltoInt = i;
                mScoreList.post(new Runnable() {

                    @Override
                    public void run() {
                        if (mPosition == MainActivity.selectedMatchId) {

                            mScoreList.setSelection(scrolltoInt-1);
                            mScoreList.smoothScrollToPosition(scrolltoInt-1);
                        }
                    }
                });
            }


        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(loadDoneReceiver);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void update() {

        restartLoader();
    }

    private class LoadDoneReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(MainActivity.DONE_KEY) != null) {
                if (FetchService.data_ok == false) {
                    Util.showAlertDialog(getString(R.string.network_error).toString(), mContext);
                } else {
                    if (intent.getStringExtra(MainActivity.DONE_KEY).equals(MainActivity.DONE_LOADING)) {
                        try {

                            restartLoader();

                        } catch (Exception e) {

                        }
                    }
                }
            }
        }
    }


}

