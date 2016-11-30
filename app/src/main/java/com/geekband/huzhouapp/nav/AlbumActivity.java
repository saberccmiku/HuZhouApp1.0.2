package com.geekband.huzhouapp.nav;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.activity.BaseActivity;
import com.geekband.huzhouapp.adapter.PictureSlidePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/24
 */
public class AlbumActivity extends BaseActivity {
    private ViewPager mPicture_pager;
    private PictureSlidePagerAdapter mPictureSlidePagerAdapter;
    private ArrayList<String> mAlbumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Intent intent = getIntent();
        String tag = intent.getAction();
        mAlbumList = new ArrayList<>();
        int position = 0;
        if (tag.equals("albumList")) {
            mAlbumList.clear();
            mAlbumList = intent.getStringArrayListExtra("albumList");
            position = intent.getIntExtra("position",0);
        } else if (tag.equals("localImages")) {
            mAlbumList.clear();
            mAlbumList = intent.getStringArrayListExtra("localImages");
        }
        mPicture_pager = (ViewPager) findViewById(R.id.picture_pager);
        mPictureSlidePagerAdapter = new PictureSlidePagerAdapter(getSupportFragmentManager(), mAlbumList);
        mPicture_pager.setAdapter(mPictureSlidePagerAdapter);
        mPicture_pager.setCurrentItem(position);
    }
}
