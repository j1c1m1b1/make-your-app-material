package com.example.xyzreader.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;

    private long mSelectedItemId;

    private TextView bylineView;
    private ViewPager mPager;
    private CollapsingToolbarLayout layoutCollapsing;
    private AspectRatioImageView mPhotoView;
    private FloatingActionButton shareFab;

    private MyPagerAdapter mPagerAdapter;
    private ViewPager.SimpleOnPageChangeListener onPageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_article_detail);

        supportPostponeEnterTransition();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();

        if(bar != null)
        {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        layoutCollapsing =
                (CollapsingToolbarLayout)findViewById(R.id.layoutCollapsing);


        bylineView = (TextView) findViewById(R.id.article_byline);

        bylineView.setMovementMethod(new LinkMovementMethod());

        mPhotoView = (AspectRatioImageView) findViewById(R.id.photo);

        shareFab = (FloatingActionButton)findViewById(R.id.share_fab);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);

        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                    mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
                }
            }
        };

        mPager.addOnPageChangeListener(onPageChangeListener);

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mSelectedItemId = ItemsContract.Items.getItemId(getIntent().getData());
            }
        }

        getSupportLoaderManager().initLoader(0, null, this);
    }

    public void bindViews(String title, CharSequence byline, String photoUrl)
    {
        layoutCollapsing.setTitle(title);

        bylineView.setText(byline);

        final String shareText = String.format(getString(R.string.share_text_format), title);

        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent
                        .createChooser(ShareCompat.IntentBuilder.from(ArticleDetailActivity.this)
                                .setType("text/plain")
                                .setText(shareText)
                                .getIntent(), getString(R.string.action_share)));
            }
        });

        Glide.with(this).load(photoUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mPhotoView.setImageBitmap(resource);
                supportStartPostponedEnterTransition();
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                supportStartPostponedEnterTransition();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPager.removeOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        mCursor.moveToFirst();
        mPagerAdapter.notifyDataSetChanged();

        int position = 0;
        while(!mCursor.isAfterLast())
        {
            if (mCursor.getLong(ArticleLoader.Query._ID) == mSelectedItemId) {
                position = mCursor.getPosition();
                mPager.setCurrentItem(position, false);
                break;
            }
            mCursor.moveToNext();
        }

        mCursor.moveToPosition(position);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ArticleDetailFragment
                    .newInstance(mSelectedItemId);
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }

    }
}
