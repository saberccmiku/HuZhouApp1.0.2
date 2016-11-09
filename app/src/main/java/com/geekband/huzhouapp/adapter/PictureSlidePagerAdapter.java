package com.geekband.huzhouapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.geekband.huzhouapp.fragment.picture.PictureSlideFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/24
 */
public class PictureSlidePagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<String> pictureList;

    public PictureSlidePagerAdapter(FragmentManager fm, ArrayList<String> list) {
        super(fm);
        pictureList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return PictureSlideFragment.newInstance(position, pictureList);
    }

    @Override
    public int getCount() {
        return pictureList.size();
    }

}
