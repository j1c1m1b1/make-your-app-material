package com.example.xyzreader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Julio Mendoza on 10/5/15.
 */
public class ScrimView extends View {

    public ScrimView(Context context) {
        super(context);
    }

    public ScrimView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float ASPECT_RATIO = 1.5f;
        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, (int)(measuredWidth / ASPECT_RATIO));
    }
}
