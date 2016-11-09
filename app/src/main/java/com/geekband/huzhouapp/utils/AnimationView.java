package com.geekband.huzhouapp.utils;

import android.view.View;
import android.widget.TextView;

import com.geekband.huzhouapp.custom.text.shimmer.Shimmer;
import com.geekband.huzhouapp.custom.text.shimmer.ShimmerTextView;

/**
 * Created by Administrator on 2016/11/4
 */
public class AnimationView {

    public static void shimmerAnimation(Shimmer shimmer, ShimmerTextView stv) {
        if (shimmer != null && shimmer.isAnimating()) {
            shimmer.cancel();
        } else {
            shimmer = new Shimmer();
            shimmer.start(stv);
        }
    }
}
