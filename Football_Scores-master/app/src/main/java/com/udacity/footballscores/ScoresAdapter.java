package com.udacity.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;


/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter
{
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
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";
    public ScoresAdapter(Context context, Cursor cursor, int flags)
    {
        super(context,cursor,flags);
    }
    public static class ViewHolder
    {
        public TextView home_name;
        public TextView away_name;
        public TextView score;
        public TextView date;
        public ImageView home_crest;
        public ImageView away_crest;
        public LinearLayout item_layout;
        public double match_id;
        public ViewHolder(View view)
        {
            home_name = (TextView) view.findViewById(R.id.home_name);
            away_name = (TextView) view.findViewById(R.id.away_name);
            score     = (TextView) view.findViewById(R.id.score_textview);
            date      = (TextView) view.findViewById(R.id.data_textview);
            home_crest = (ImageView) view.findViewById(R.id.home_crest);
            away_crest = (ImageView) view.findViewById(R.id.away_crest);
            item_layout =  (LinearLayout) view.findViewById(R.id.item_layout);
        }
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);

        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        mHolder.home_name.setText(cursor.getString(COL_HOME));
        mHolder.away_name.setText(cursor.getString(COL_AWAY));
        mHolder.date.setText(cursor.getString(COL_MATCHTIME));
        mHolder.score.setText(Util.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        mHolder.match_id = cursor.getDouble(COL_ID);

        Glide.with(context)
                .load(cursor.getString(HOME_LOGO))
                .error(R.drawable.no_icon)
                .crossFade()
                .into(mHolder.home_crest);
        Glide.with(context)
                .load(cursor.getString(AWAY_LOGO))
                .error(R.drawable.no_icon)
                .crossFade()
                .into(mHolder.away_crest);

        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        mHolder.item_layout.setBackgroundColor(context.getResources().getColor(R.color.colorOffWhite));

        if(mHolder.match_id == detail_match_id)
        {


            mHolder.item_layout.setBackgroundColor(context.getResources().getColor(R.color.colorLightGray));
            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Util.getMatchDay(cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Util.getLeague(cursor.getInt(COL_LEAGUE)));
            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    context.startActivity(createShareFootballScoresIntent(mHolder.home_name.getText() + " "
                            + mHolder.score.getText() + " " + mHolder.away_name.getText() + " "));
                }
            });
        }
        else
        {
            container.removeAllViews();
        }

    }
    public Intent createShareFootballScoresIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}
