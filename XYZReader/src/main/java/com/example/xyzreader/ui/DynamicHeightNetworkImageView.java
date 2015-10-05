package com.example.xyzreader.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;
import com.example.xyzreader.R;

public class DynamicHeightNetworkImageView extends NetworkImageView {
    private float mAspectRatio = 1.5f;

    public DynamicHeightNetworkImageView(Context context) {
        super(context);
        init();
    }

    public DynamicHeightNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicHeightNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setAspectRatio(float aspectRatio) {
        mAspectRatio = aspectRatio;
        requestLayout();
    }

    private void init()
    {
        Drawable background;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            background = getContext().getDrawable(R.drawable.photo_background_protection);
        }
        else
        {
            //noinspection deprecation
            background = getContext().getResources()
                    .getDrawable(R.drawable.photo_background_protection);
        }

        setBackground(background);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, (int) (measuredWidth / mAspectRatio));
    }
}
