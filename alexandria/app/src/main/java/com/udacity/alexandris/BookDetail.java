package com.udacity.alexandris;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.ShareActionProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.udacity.alexandria.R;

import com.udacity.alexandris.data.*;
import com.udacity.alexandris.services.*;

public class BookDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EAN_KEY = "EAN";
    private final int LOADER_ID = 10;
    private View rootView;
    private String ean;
    private String bookTitle;
    private ShareActionProvider shareActionProvider;
    @Bind(R.id.delete_button)
    Button mDeleteButton;
    @Bind(R.id.fullBookSubTitle)
    TextView mSubTitleTextView;
    @Bind(R.id.fullBookTitle)
    TextView mFullTitleTextView;
    @Bind(R.id.fullBookDesc)
    TextView mFullBookDescTextView;
    @Bind(R.id.authors)
    TextView mAuthorsTextView;
    @Bind(R.id.fullBookCover)
    ImageView mFullBookCoverImageView;
    @Bind(R.id.categories)
    TextView mCategoriesTextview;


    @OnClick(R.id.delete_button)
    public void clickDelete(View view) {

        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, ean);
        bookIntent.setAction(BookService.DELETE_BOOK);
        getActivity().startService(bookIntent);
        if (!Utilities.isTablet(getActivity())) {
            getActivity().finish();
        } else {
           clearEntries();
        }
    }



    public BookDetail(){
    }


    public void clearEntries() {
        bookTitle = getString(R.string.empty);
        mFullTitleTextView.setText(R.string.empty);
        mSubTitleTextView.setText(R.string.empty);
        mFullBookDescTextView.setText(R.string.empty);
        mAuthorsTextView.setText(R.string.empty);
        mFullBookCoverImageView.setImageResource(android.R.color.transparent);
        mCategoriesTextview.setText(R.string.empty);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            ean = arguments.getString(BookDetail.EAN_KEY);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        return rootView;

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareBookIntent());


    }
    private Intent createShareBookIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text)+bookTitle);
        return shareIntent;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        mFullTitleTextView.setText(bookTitle);
        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        mSubTitleTextView.setText(bookSubTitle);
        String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        mFullBookDescTextView.setText(desc);
        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr;
        if(!authors.isEmpty()){
            authorsArr = authors.split(",");
            mAuthorsTextView.setLines(authorsArr.length);
            mAuthorsTextView.setText(authors.replace(",", "\n"));

        }else{
            mAuthorsTextView.setLines(1);
            mAuthorsTextView.setText("No Author");
        }
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            Glide.with(this)
                    .load(imgUrl)
                    .error(R.drawable.ic_launcher)
                    .crossFade()
                    .into(mFullBookCoverImageView);
        }


        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        mCategoriesTextview.setText(categories);



    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if(MainActivity.IS_TABLET && rootView.findViewById(R.id.right_container)==null){
           // getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}