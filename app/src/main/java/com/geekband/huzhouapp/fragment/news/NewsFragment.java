package com.geekband.huzhouapp.fragment.news;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.custom.PagerSlidingTabStrip;

/**
 * Created by Administrator on 2016/5/12
 * 最新动态模块，包含新闻，日志，考勤
 */
public class NewsFragment extends Fragment {
    private BreakingNewsFragment bnf;
    private CourseFragment lf;
    private InformationFragment af;
    private PagerSlidingTabStrip mTabs;
    private ViewPager mViewPager;
    private MyPagerAdapter mMyPagerAdapter;
    private View fragment_news;//缓存页面

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragment_news = inflater.inflate(R.layout.fragment_news, container, false);

        mTabs = (PagerSlidingTabStrip) fragment_news.findViewById(R.id.news_tabs);
        mViewPager = (ViewPager) fragment_news.findViewById(R.id.news_viewPager);
        mMyPagerAdapter = new MyPagerAdapter(getFragmentManager());
        //设置缓存页数
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mMyPagerAdapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);
        mTabs.setViewPager(mViewPager);
        return fragment_news;
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        String[] titles = {"新闻推送", "课程中心", "通知公告"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (bnf == null) {
                    bnf = BreakingNewsFragment.newInstance();
                }
                return bnf;

            } else if (position == 1) {
                if (lf == null) {
                    lf = CourseFragment.newInstance();
                }
                return lf;
            } else if (position == 2) {
                if (af == null) {
                    af = InformationFragment.newInstance();
                }
                return af;
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
