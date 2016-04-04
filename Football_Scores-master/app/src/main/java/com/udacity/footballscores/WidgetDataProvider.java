package com.udacity.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.udacity.footballscores.data.DatabaseContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int HOME_LOGO = 8;
    public static final int AWAY_LOGO = 9;
    public static final int COL_MATCHDAY = 11;
    public static final int COL_ID = 10;
    public static final int COL_MATCHTIME = 2;
    public double detail_match_id = 0;
    List<String> mCollection = new ArrayList<>();
    private static final String TAG = "WidgetDataProvider";
    Context mContext = null;
    Cursor cursor;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext =context;
    }

    @Override
    public void onCreate() {
    //initData();
       // onDataSetChanged();
    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) {
            cursor.close();
        }
        Date fragmentdate = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        String[] fragmentDate = new String[1];
        fragmentDate[0] = mformat.format(fragmentdate);
        cursor = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, fragmentDate, null);
        cursor.getCount();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || cursor == null) {
            return null;
        }
        cursor.moveToPosition(position);
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_scores_list_item);

        view.setTextViewText(R.id.home_name,cursor.getString(COL_HOME));
        view.setTextViewText(R.id.away_name,cursor.getString(COL_AWAY));
        view.setTextViewText(R.id.data_textview, cursor.getString(COL_MATCHTIME));
        view.setTextViewText(R.id.score_textview, Util.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        //mHolder.match_id = cursor.getDouble(COL_ID);
        Bitmap b1;
        try {
            b1 = Glide.with(mContext)
                    .load(cursor.getString(HOME_LOGO))
                    .asBitmap()
                    .error(R.drawable.no_icon)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
            view.setImageViewBitmap(R.id.home_crest,b1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Bitmap b2;
        try {
            b2 = Glide.with(mContext)
                    .load(cursor.getString(AWAY_LOGO))
                    .asBitmap()
                    .error(R.drawable.no_icon)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
            view.setImageViewBitmap(R.id.away_crest,b2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



     // fill the intent from FootBall widget and add extra match id
        final Intent fillInIntent = new Intent();
        final Bundle extras = new Bundle();

        extras.putDouble("match_id",
                cursor.getDouble(COL_ID));
        fillInIntent.putExtras(extras);
        view.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
       return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    /*private void initData() {
        mCollection.clear();
        for (int i = 1; i <= 10; i++) {
            mCollection.add("ListView item " + i);
        }

    }*/

}
