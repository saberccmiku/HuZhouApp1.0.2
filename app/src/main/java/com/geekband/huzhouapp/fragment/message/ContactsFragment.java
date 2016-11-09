package com.geekband.huzhouapp.fragment.message;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.activity.MainActivity;
import com.geekband.huzhouapp.baseadapter.CommonRecyclerAdapter;
import com.geekband.huzhouapp.baseadapter.CommonRecyclerViewHolder;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.geekband.huzhouapp.utils.DataUtils;
import com.geekband.huzhouapp.vo.UserBaseInfo;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/12
 */
public class ContactsFragment extends Fragment {
    MainActivity mMainActivity;
    private ArrayList<UserBaseInfo> mUserBaseInfos;
    private PtrClassicFrameLayout mPtr;
    private static final int PULL_TO_REFRESH = 1;//下拉刷新
    private static final int PULL_TO_LOAD = 2;//上拉加载
    private int currentPage = 0;
    private int pageSize = 15;
    private RecyclerAdapterWithHF mAdapterWithHF;
    private BitmapUtils mBitmapUtils;

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, null);
        mBitmapUtils = BitmapHelper.getBitmapUtils(mMainActivity, null, R.drawable.head_default, R.drawable.head_default);
        mUserBaseInfos = new ArrayList<>();
        initView(view);
        return view;
    }

    private void initView(View view) {
        mPtr = (PtrClassicFrameLayout) view.findViewById(R.id.contacts_ptr);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.contacts_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(mMainActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        CommonRecyclerAdapter adapter = new CommonRecyclerAdapter<UserBaseInfo>(mMainActivity, R.layout.contacts_item, mUserBaseInfos) {

            @Override
            public void convertView(CommonRecyclerViewHolder holder, UserBaseInfo userBaseInfo) {
                mBitmapUtils.display(holder.getView(R.id.contacts_image_item),userBaseInfo.getAvatarUrl());
                holder.setText(R.id.contacts_name_item, userBaseInfo.getRealName());
                holder.setText(R.id.contacts_sex_item, userBaseInfo.getSex());
                holder.setText(R.id.contacts_phone_item,userBaseInfo.getPhoneNum());
            }
        };
        //noinspection unchecked
        mAdapterWithHF = new RecyclerAdapterWithHF(adapter);
        rv.setAdapter(mAdapterWithHF);

        mPtr.setLastUpdateTimeRelateObject(this);
        mPtr.setResistance(1.7f);
        mPtr.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtr.setDurationToClose(200);
        mPtr.setDurationToCloseHeader(1000);

        mPtr.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtr.autoRefresh();
            }
        }, 100);

        mPtr.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                new Thread() {
                    @Override
                    public void run() {
                        currentPage = 0;
                        mUserBaseInfos.clear();
                        ArrayList<UserBaseInfo> userBaseInfos = DataUtils.getUserInfo(currentPage, pageSize);
                        if (userBaseInfos != null) {
                            mUserBaseInfos.addAll(userBaseInfos);
                        }
                        Message message = mHandler.obtainMessage();
                        message.what = PULL_TO_REFRESH;
                        mHandler.sendMessage(message);
                    }
                }.start();
            }
        });

        mPtr.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                new Thread() {
                    @Override
                    public void run() {
                        currentPage += 1;
                        ArrayList<UserBaseInfo> moreList = DataUtils.getUserInfo(currentPage, pageSize);
                        if (moreList != null) {
                            mUserBaseInfos.addAll(moreList);
                        }
                        Message message = mHandler.obtainMessage();
                        message.what = PULL_TO_LOAD;
                        mHandler.sendMessage(message);
                    }
                }.start();
            }
        });
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PULL_TO_REFRESH:
                    mAdapterWithHF.notifyDataSetChanged();
                    mPtr.refreshComplete();
                    mPtr.setLoadMoreEnable(true);
                    break;
                case PULL_TO_LOAD:
                    mAdapterWithHF.notifyDataSetChanged();
                    mPtr.loadMoreComplete(true);
                    break;
            }
            return false;
        }
    });
}
