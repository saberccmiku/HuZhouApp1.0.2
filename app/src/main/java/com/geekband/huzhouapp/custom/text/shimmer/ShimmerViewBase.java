package com.geekband.huzhouapp.custom.text.shimmer;

/**
 * Created by Administrator on 2016/11/3.
 */
public interface ShimmerViewBase {
     float getGradientX();
     void setGradientX(float gradientX);
     boolean isShimmering();
     void setShimmering(boolean isShimmering);
     boolean isSetUp();
     void setAnimationSetupCallback(ShimmerViewHelper.AnimationSetupCallback callback);
     int getPrimaryColor();
     void setPrimaryColor(int primaryColor);
     int getReflectionColor();
     void setReflectionColor(int reflectionColor);
}
