package com.udacity.alexandris;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.udacity.alexandria.R;
import com.udacity.alexandris.data.*;
import com.udacity.alexandris.services.*;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";

    private String mResultString = "";
    private final int LOADER_ID = 1;
    private View rootView;
    private final String EAN_CONTENT = "eanContent";
    private final String AUTHOR = "author";
    private final String TITLE = "title";
    private final String SUB_TITLE = "sub_title";
    private final String CATEGORIES = "sub_title";

    private final String IMG_URL = "img_url";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";
    @Bind(R.id.ean)
    EditText ean;
    @Bind(R.id.bookCover)
    ImageView mImage;
    @Bind(R.id.bookTitle)
    TextView mBookTitleTextView;
    @Bind(R.id.bookSubTitle)
    TextView mBookSubTitleTextView;
    @Bind(R.id.authors)
    TextView mAuthorsTextView;
    @Bind(R.id.categories)
    TextView mCategoriesTextView;
    @Bind(R.id.scan_button)
    Button mScanButton;
    @Bind(R.id.save_button)
    ImageButton mSaveButton;
    @Bind(R.id.delete_button)
    ImageButton mDeleteButton;
    String mImgUrl;
    String mTitle;
    String mSubTitle;
    String mAuthors;
    String mCategories;


    boolean mDataLoaded;

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";
    private static String DATA_LOADED = "data_loaded";

    private BroadcastReceiver loadDoneReceiver;

    public AddBook() {
    }

    @OnClick(R.id.delete_button)
    public void clickDeleteButton() {
        Intent bookIntent = new Intent(getActivity().getApplicationContext(), BookService.class);
        bookIntent.putExtra(BookService.EAN, ean.getText().toString());
        bookIntent.setAction(BookService.DELETE_BOOK);
        getActivity().startService(bookIntent);
        ean.setText("");
        mResultString = "";
    }

    @OnClick(R.id.save_button)
    public void clickSaveButton() {
        ean.setText("");
        mResultString = "";
    }

    @OnClick(R.id.scan_button)
    public void clickScanButton() {
        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

            Utilities.showAlertDialog(getString(R.string.no_camera), getActivity());

        } else if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            Utilities.showAlertDialog(getString(R.string.no_autofocus), getActivity());
        } else {
            Context context = getActivity();
            Intent scanIntent = new Intent(getActivity().getApplicationContext(), ScanBarcode.class);
            startActivityForResult(scanIntent, 9001);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ean != null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
            outState.putString(TITLE,mBookTitleTextView.getText().toString());
            outState.putString(SUB_TITLE,mBookSubTitleTextView.getText().toString());
            outState.putString(AUTHOR,mAuthors);
            outState.putString(CATEGORIES,mCategoriesTextView.getText().toString());
            outState.putString(IMG_URL,mImgUrl);

            outState.putBoolean(DATA_LOADED, mDataLoaded);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        // this was moved to onResume because ot di not work in the onCreateView
        if (mDataLoaded) {
            mBookTitleTextView.setText(mTitle);
            mBookSubTitleTextView.setText(mSubTitle);
            String[] authorsArr = mAuthors.split(",");
            mAuthorsTextView.setLines(authorsArr.length);
            mAuthorsTextView.setText(mAuthors.replace(",", "\n"));
            mCategoriesTextView.setText(mCategories);
            if (Patterns.WEB_URL.matcher(mImgUrl).matches()) {
                Glide.with(this)
                        .load(mImgUrl)
                        .error(R.drawable.ic_launcher)
                        .crossFade()
                        .into(mImage);
            }
        }
        loadDoneReceiver = new LoadDoneReciever();
        IntentFilter filter = new IntentFilter(MainActivity.DONE_LOADING);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(loadDoneReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(loadDoneReceiver);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ButterKnife.bind(this, rootView);


        ean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();
                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978")) {
                    ean = "978" + ean;
                }
                if (ean.length() < 13) {
                    clearFields();
                    return;
                }
                //Once we have an ISBN, start a book Intent
                Intent bookIntent = new Intent(getActivity().getApplicationContext(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                //AddBook.this.restartLoader();
            }
        });

        mDataLoaded = false;
        if (savedInstanceState != null) {
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            ean.setHint("");

            if (savedInstanceState.getBoolean(DATA_LOADED, false)) {
                mDataLoaded = true;
                mSaveButton.setVisibility(View.VISIBLE);
                mDeleteButton.setVisibility(View.VISIBLE);
                mImage.setVisibility(View.VISIBLE);
                mTitle = savedInstanceState.getString(TITLE);

                mSubTitle = savedInstanceState.getString(SUB_TITLE);


                mAuthors = savedInstanceState.getString(AUTHOR);

                mCategories = savedInstanceState.getString(CATEGORIES);

                mImgUrl = savedInstanceState.getString(IMG_URL);
                int imageHeight = 0;
                int imageWidth = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageHeight = mImage.getMaxHeight();
                    imageWidth = mImage.getMaxWidth();
                }


            }
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            mResultString = intent.getStringExtra("result");

            String regex = "[0-9]+";
            if (mResultString != "" && mResultString.matches(regex)) {
                ean.setText(mResultString);
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, mResultString);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().getApplicationContext().startService(bookIntent);
            }
        } catch (Exception e) {

        }

    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (ean.getText().length() == 0 && mResultString.equals("")) {
            return null;
        }
        String eanStr = ean.getText().length() == 0 ? mResultString : ean.getText().toString();
        //String eanStr= ean.getText().toString();
        if (eanStr.length() == 10 && !eanStr.startsWith("978")) {
            eanStr = "978" + eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
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
        showBookDetails(data);

    }


    public void showBookDetails(Cursor data) {
        mTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        mBookTitleTextView.setText(mTitle);

        mSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        mBookSubTitleTextView.setText(mSubTitle);

        mAuthors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = mAuthors.split(",");
        mAuthorsTextView.setLines(authorsArr.length);
        mAuthorsTextView.setText(mAuthors.replace(",", "\n"));

        mImgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        mImage.setVisibility(View.VISIBLE);
        if (Patterns.WEB_URL.matcher(mImgUrl).matches()) {
            /*new DownloadImage((ImageView) rootView.findViewById(R.id.bookCover)).execute(imgUrl);
            rootView.findViewById(R.id.bookCover).setVisibility(View.VISIBLE);*/
            Glide.with(this)
                    .load(mImgUrl)
                    .error(R.drawable.ic_launcher)
                    .crossFade()
                    .into(mImage);

        } else {
            Glide.with(this)
                    .load(R.drawable.ic_launcher)
                    .error(R.drawable.ic_launcher)
                    .crossFade()
                    .into(mImage);
        }

        mCategories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        mCategoriesTextView.setText(mCategories);

        mSaveButton.setVisibility(View.VISIBLE);
        mDeleteButton.setVisibility(View.VISIBLE);
        mDataLoaded = true;
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields() {
        mBookTitleTextView.setText("");
        mBookSubTitleTextView.setText("");
        mAuthorsTextView.setText("");
        mCategoriesTextView.setText("");
        mImage.setVisibility(View.INVISIBLE);
        mSaveButton.setVisibility(View.INVISIBLE);
        mDeleteButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    private class LoadDoneReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(MainActivity.DONE_KEY) != null) {
                if (intent.getStringExtra(MainActivity.DONE_KEY).equals(MainActivity.DONE_LOADING))
                    try {
                        restartLoader();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(ean.getWindowToken(), 0);

                    } catch (Exception e) {

                    }
            }
        }
    }


}
