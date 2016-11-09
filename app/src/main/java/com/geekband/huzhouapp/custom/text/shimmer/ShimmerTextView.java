package com.geekband.huzhouapp.custom.text.shimmer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/11/3.
 */
public class ShimmerTextView extends TextView implements ShimmerViewBase {

    private ShimmerViewHelper mShimmerViewHelper;

    public ShimmerTextView(Context context) {
        super(context);
        mShimmerViewHelper = new ShimmerViewHelper(this, getPaint(), null);
        mShimmerViewHelper.setPrimaryColor(getCurrentTextColor());
    }

    public ShimmerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mShimmerViewHelper = new ShimmerViewHelper(this, getPaint(), attrs);
        mShimmerViewHelper.setPrimaryColor(getCurrentTextColor());
    }

    public ShimmerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mShimmerViewHelper = new ShimmerViewHelper(this, getPaint(), attrs);
        mShimmerViewHelper.setPrimaryColor(getCurrentTextColor());
    }


    @Override
    public float getGradientX() {
        return mShimmerViewHelper.getGradientX();
    }

    @Override
    public void setGradientX(float gradientX) {
        mShimmerViewHelper.setGradientX(gradientX);
    }

    @Override
    public boolean isShimmering() {
        return mShimmerViewHelper.isShimmering();
    }

    @Override
    public void setShimmering(boolean isShimmering) {
        mShimmerViewHelper.setShimmering(isShimmering);
    }

    @Override
    public boolean isSetUp() {
        return mShimmerViewHelper.isSetUp();
    }

    @Override
    public void setAnimationSetupCallback(ShimmerViewHelper.AnimationSetupCallback callback) {
        mShimmerViewHelper.setAnimationSetupCallback(callback);
    }

    @Override
    public int getPrimaryColor() {
        return mShimmerViewHelper.getPrimaryColor();
    }

    @Override
    public void setPrimaryColor(int primaryColor) {
        mShimmerViewHelper.setPrimaryColor(primaryColor);
    }

    @Override
    public int getReflectionColor() {
        return mShimmerViewHelper.getReflectionColor();
    }

    @Override
    public void setReflectionColor(int reflectionColor) {
        mShimmerViewHelper.setReflectionColor(reflectionColor);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        if (mShimmerViewHelper!=null){
            mShimmerViewHelper.setPrimaryColor(getCurrentTextColor());
        }
    }

    @Override
    public void setTextColor(ColorStateList colors) {
        super.setTextColor(colors);
        if (mShimmerViewHelper!=null){
            mShimmerViewHelper.setPrimaryColor(getCurrentTextColor());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mShimmerViewHelper!=null){
            mShimmerViewHelper.onSizeChanged();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mShimmerViewHelper!=null){
            mShimmerViewHelper.onDraw();
        }
    }
}
