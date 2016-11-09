package com.geekband.huzhouapp.fragment.message;


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
 */
public class MessageFragment extends Fragment {
    private SuggestFragment sf;
    private SystemFragment ssf;
    private ContactsFragment rf;
    private PagerSlidingTabStrip mTabs;
    private ViewPager mViewPager;
    private MyPagerAdapter mMyPagerAdapter;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_message = inflater.inflate(R.layout.fragment_message, container, false);

        mTabs = (PagerSlidingTabStrip) fragment_message.findViewById(R.id.message_tabs);
        mViewPager = (ViewPager) fragment_message.findViewById(R.id.message_viewPager);
        mMyPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mMyPagerAdapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);
        mTabs.setViewPager(mViewPager);

        return fragment_message;
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        String[] titles = {"消息通知", "意见信箱"};

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
                if (ssf == null) {
                    ssf = SystemFragment.newInstance();
                }
                return ssf;

            }
            /**else if (position == 1) {
                if (rf == null) {
                    rf = ContactsFragment.newInstance();
                }
                return rf;
            } */
            else if (position == 1) {
                if (sf == null) {
                    sf = SuggestFragment.newInstance();
                }
                return sf;
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }

}
