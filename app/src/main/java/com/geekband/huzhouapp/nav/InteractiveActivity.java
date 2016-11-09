package com.geekband.huzhouapp.nav;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.baseadapter.CommonRecyclerAdapter;
import com.geekband.huzhouapp.baseadapter.CommonRecyclerViewHolder;
import com.geekband.huzhouapp.custom.FloatView;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.DataUtils;
import com.geekband.huzhouapp.utils.FileUtils;
import com.geekband.huzhouapp.vo.BirthdayInfo;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 2016/6/29
 */
public class InteractiveActivity extends Activity implements RecyclerAdapterWithHF.OnItemClickListener{

    private static final int PULL_TO_REFRESH = 1;//下拉刷新
    private static final int PULL_TO_LOAD = 2;//上拉加载
    int pageSize = 10;
    int currentPage = 1;
    private ArrayList<BirthdayInfo> mList;
    private PtrClassicFrameLayout mPtr;
    private RecyclerAdapterWithHF mAdapterWithHF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive);
        mList = new ArrayList<>();
        initView();
        findView();
    }

    private void findView() {

        Button birthdayList_backBtn = (Button) findViewById(R.id.birthdayList_backBtn);
        birthdayList_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InteractiveActivity.this.finish();
            }
        });


        //添加悬浮按钮
        int layoutWidth = FileUtils.getScreenWidth(this);
        int layoutHeight = FileUtils.getScreenHeight(this);
        FloatView floatView = new FloatView(this,layoutWidth,layoutHeight/2,R.layout.float_view_layout);
        floatView.addToWindow();
        floatView.setFloatViewClickListener(new FloatView.IFloatViewClick() {
            @Override
            public void onFloatViewClick() {
                Intent intent = new Intent(InteractiveActivity.this,ReceiveBlessListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        final BitmapUtils bitmapUtils = BitmapHelper.getBitmapUtils(this, null, R.drawable.head_default, R.drawable.head_default);
        mPtr = (PtrClassicFrameLayout) findViewById(R.id.ptrLayout_birthday);
        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerView_birthday);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        CommonRecyclerAdapter adapter = new CommonRecyclerAdapter<BirthdayInfo>(this, R.layout.item_birthday, mList) {

            @Override
            public void convertView(CommonRecyclerViewHolder holder, BirthdayInfo birthdayInfo) {
                ImageView imageView = holder.getView(R.id.avatar_birthday);
                bitmapUtils.display(imageView, birthdayInfo.getAvatarImage());
                holder.setText(R.id.name_birthday, birthdayInfo.getRealName());
                int time = Integer.parseInt(DataUtils.getDays(birthdayInfo.getDate()));
                if (time<0&&time>=-7){
                    holder.setText(R.id.content_birthday, "生日已经过去" + Math.abs(time) + "天,日夜思君不见君,泪如雨淋淋");
                }else if (time>0){
                    holder.setText(R.id.content_birthday, "距离生日还有" + time + "天，准备为伟大的诞生献上最美好的祝福啦");
                }else if (time==0){
                    holder.setText(R.id.content_birthday, "今天这里聚集了世界的目光，点击发送生日贺卡吧");
                    holder.setImageResource(R.id.happy_birthday, R.drawable.happy_birthday_ico);
                }
            }
        };
        //noinspection unchecked
        mAdapterWithHF = new RecyclerAdapterWithHF(adapter);
        rv.setAdapter(mAdapterWithHF);
        mAdapterWithHF.setOnItemClickListener(this);
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
                        currentPage = 1;

                        ArrayList<BirthdayInfo> birthdays = DataUtils.getBirthdayInfo(pageSize, currentPage);
                        if (birthdays != null) {
                            for (int i = birthdays.size() - 1; i >= 0; i--) {
                                String date = birthdays.get(i).getDate();
                                if (date == null || date.isEmpty()) {
                                    birthdays.remove(i);
                                }else {
                                    if (Integer.parseInt(DataUtils.getDays(date))<-7){
                                        birthdays.remove(i);
                                    }
                                }
                            }
                            mList.clear();
                            mList.addAll(birthdays);
                            Message message = mHandler.obtainMessage();
                            message.what = PULL_TO_REFRESH;
                            mHandler.sendMessage(message);
                        }

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
                        ArrayList<BirthdayInfo> birthdays = DataUtils.getBirthdayInfo(pageSize, currentPage);
                        if (birthdays != null) {
                            for (int i = birthdays.size()-1; i >=0 ; i--) {
                                if (birthdays.get(i).getDate() == null) {
                                    birthdays.remove(i);
                                }
                            }
                            mList.addAll(birthdays);
                            Message message = mHandler.obtainMessage();
                            message.what = PULL_TO_LOAD;
                            mHandler.sendMessage(message);
                        }
                    }
                }.start();
            }
        });

    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Collections.sort(mList,mComparator);
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

    @Override
    public void onItemClick(RecyclerAdapterWithHF adapter, RecyclerView.ViewHolder vh, int position) {
        BirthdayInfo birthdayInfo = mList.get(position);
        String userId = birthdayInfo.getUserId();
        Intent intent =  new Intent();
        intent.putExtra(Constants.BIRTHDAY_GIFT,userId);
        intent.setClass(this,SendGiftActivity.class);
        this.startActivity(intent);
    }


    //按照日期排序
    Comparator<BirthdayInfo> mComparator = new Comparator<BirthdayInfo>() {
        @Override
        public int compare(BirthdayInfo lhs, BirthdayInfo rhs) {
            return Integer.parseInt(lhs.getDate())-Integer.parseInt(rhs.getDate());
        }
    };
}
