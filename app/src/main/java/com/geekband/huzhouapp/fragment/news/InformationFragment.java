package com.geekband.huzhouapp.fragment.news;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.database.pojo.OpinionTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.activity.InformationContentActivity;
import com.geekband.huzhouapp.activity.MainActivity;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.baseadapter.CommonRecyclerAdapter;
import com.geekband.huzhouapp.baseadapter.CommonRecyclerViewHolder;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.DataUtils;
import com.geekband.huzhouapp.utils.FileUtils;
import com.geekband.huzhouapp.vo.BirthdayInfo;
import com.geekband.huzhouapp.vo.ClassInfo;
import com.geekband.huzhouapp.vo.DynamicNews;
import com.geekband.huzhouapp.vo.GradeInfo;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/5/12
 * 考勤模块
 */
public class InformationFragment extends Fragment implements RecyclerAdapterWithHF.OnItemClickListener {
    MainActivity mMainActivity;
    private PtrClassicFrameLayout mPtr;
    private RecyclerAdapterWithHF mAdapterWithHF;
    private ArrayList<ClassInfo> mAllClasses;
    public boolean isLoaded_review;
    public boolean isLoaded_result;
    public boolean isLoaded_grade;
    public boolean isLoaded_birthday;
    public boolean isLoaded_bless;
    private ProgressBar mAttendance_progress;
    //查询的数据对象
    ClassInfo mReviewData ;
    ClassInfo mResultData ;
    ClassInfo mGradeData ;
    ClassInfo mBirthdayData ;
    ClassInfo mBlessData ;

    //创建线程池
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public static InformationFragment newInstance() {
        return new InformationFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, null);
        findView(view);
        mAllClasses = new ArrayList<>();
        initView(view);
        return view;
    }

    private void findView(View v) {
        mAttendance_progress = (ProgressBar) v.findViewById(R.id.attendance_progress);
        mAttendance_progress.setVisibility(View.VISIBLE);
    }


    private void initView(View view) {
        mPtr = (PtrClassicFrameLayout) view.findViewById(R.id.ptr_frameLayout_information);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recycler_view_information);
        LinearLayoutManager manager = new LinearLayoutManager(mMainActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        CommonRecyclerAdapter commonRecyclerAdapter = new CommonRecyclerAdapter<ClassInfo>(getActivity(), R.layout.information_item, mAllClasses) {
            @Override
            public void convertView(CommonRecyclerViewHolder holder, ClassInfo classInfo) {
                holder.getView(R.id.information_title_item).setSelected(true);
                holder.setImageResource(R.id.information_icon_item, classInfo.getImageId());
                holder.setText(R.id.information_title_item, classInfo.getClassTitle());
                if (classInfo.getContentCount() == 0) {
                    holder.setText(R.id.information_content_item, "点击查看学分情况");
                } else {
                    holder.setText(R.id.information_content_item, classInfo.getContentCount() + "条待处理信息");
                }
                holder.setText(R.id.information_date_item, classInfo.getDateTime());
            }
        };
        //noinspection unchecked
        mAdapterWithHF = new RecyclerAdapterWithHF(commonRecyclerAdapter);
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
                mAttendance_progress.setVisibility(View.VISIBLE);
                isLoaded_review = false;
                isLoaded_result = false;
                isLoaded_grade = false;
                isLoaded_birthday = false;
                isLoaded_bless = false;
                mExecutorService.execute(review_task);
                mExecutorService.execute(result_task);
                mExecutorService.execute(grade_task);
                mExecutorService.execute(birthday_task);
                mExecutorService.execute(bless_task);
            }

        });

    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.PULL_TO_LOAD:
                    mAdapterWithHF.notifyDataSetChanged();
                    break;
                case Constants.REVIEW_INFORMATION:
                    mAdapterWithHF.notifyDataSetChanged();
                    mPtr.refreshComplete();
                    isLoaded_review = true;
                    sendMessage(Constants.LOADED);
                    break;
                case Constants.RESULT_INFORMATION:
                    mAdapterWithHF.notifyDataSetChanged();
                    mPtr.refreshComplete();
                    isLoaded_result = true;
                    sendMessage(Constants.LOADED);
                    break;
                case Constants.GRADE_INFORMATION:
                    mAdapterWithHF.notifyDataSetChanged();
                    mPtr.refreshComplete();
                    isLoaded_grade = true;
                    sendMessage(Constants.LOADED);
                    break;
                case Constants.BIRTHDAY_INFORMATION:
                    mAdapterWithHF.notifyDataSetChanged();
                    mPtr.refreshComplete();
                    isLoaded_birthday = true;
                    sendMessage(Constants.LOADED);
                    break;
                case Constants.BLESS_INFORMATION:
                    mAdapterWithHF.notifyDataSetChanged();
                    mPtr.refreshComplete();
                    isLoaded_bless = true;
                    sendMessage(Constants.LOADED);
                    break;
                case Constants.LOADED:
                    if (isLoaded_review && isLoaded_result && isLoaded_grade && isLoaded_birthday && isLoaded_bless) {
                        mAttendance_progress.setVisibility(View.GONE);
                    }
                    break;

            }
            return false;
        }
    });

    public void sendMessage(int flag) {
        Message message = mHandler.obtainMessage();
        message.what = flag;
        mHandler.sendMessage(message);
    }

    @Override
    public void onItemClick(RecyclerAdapterWithHF adapter, RecyclerView.ViewHolder vh, int position) {
        ClassInfo classInfo = mAllClasses.get(position);
        Intent intent = new Intent();
        intent.setClass(mMainActivity, InformationContentActivity.class);
        intent.setAction(Constants.INFORMATION_CONTENT);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.INFORMATION_CONTENT, classInfo);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //需求意见审核
    Runnable review_task = new Runnable() {
        @Override
        public void run() {
            isLoaded_review = false;
            //需求意见审核
            //noinspection unchecked
            ArrayList<OpinionTable> review_opinionTables = (ArrayList<OpinionTable>) DataUtils.getToDoOpinions();
            if (review_opinionTables != null && review_opinionTables.size() != 0) {
                //需要审核文件的数量
                int count = review_opinionTables.size();
                //审核通知的时间,以最新投递时间为准
                String postTime = review_opinionTables.get(count - 1).getField(OpinionTable.FIELD_POSTTIME);
                mAllClasses.remove(mReviewData);
                mReviewData = new ClassInfo(review_opinionTables, R.drawable.review_information, "待审核文件", count, postTime, Constants.REVIEW_INFORMATION);
                mAllClasses.add(mReviewData);
            }
            sendMessage(Constants.REVIEW_INFORMATION);
        }
    };

    //需求意见审核结果通知
    Runnable result_task = new Runnable() {
        @Override
        public void run() {
            isLoaded_result = false;
            //noinspection unchecked
            ArrayList<OpinionTable> result_opinionTables = (ArrayList<OpinionTable>) DataUtils.getOpinionsInformation();
            if (result_opinionTables != null && result_opinionTables.size() != 0) {
                //需要审核文件的数量
                int count = result_opinionTables.size();
                //审核通知的时间,以最新投递时间为准
                String postTime = result_opinionTables.get(count - 1).getField(OpinionTable.FIELD_POSTTIME);
                mAllClasses.remove(mResultData);
                mResultData = new ClassInfo(result_opinionTables, R.drawable.result_information, "需求意见审核结果通知", count, postTime, Constants.RESULT_INFORMATION);
                mAllClasses.add(mResultData);
            }
            sendMessage(Constants.RESULT_INFORMATION);
        }
    };

    //学分通知
    Runnable grade_task = new Runnable() {
        @Override
        public void run() {
            isLoaded_grade = false;
            ArrayList<GradeInfo> list = new ArrayList<>();
            String userId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, null);
            GradeInfo grade_classes = DataUtils.getGrade(userId);
            if (grade_classes != null) {
                if (Integer.valueOf(grade_classes.getNeedGrade()) - Integer.valueOf(grade_classes.getAlreadyGrade()) > 0) {
                    list.add(grade_classes);
                    String postTIme = "今天";
                    mAllClasses.remove(mGradeData);
                    mGradeData = new ClassInfo(list, R.drawable.grade_information, "学分未达标通知", 0, postTIme, Constants.GRADE_INFORMATION);
                    mAllClasses.add(mGradeData);

                }
            }
            sendMessage(Constants.GRADE_INFORMATION);
        }
    };

    //今天过生日的人员名单
    Runnable birthday_task = new Runnable() {
        @Override
        public void run() {
            isLoaded_birthday = false;
            ArrayList<BirthdayInfo> birthday_classes = DataUtils.getBirthdayInfo();
            if (birthday_classes != null && birthday_classes.size() != 0) {
                String postTIme = FileUtils.getCurrentTimeStr("今天");
                int count = birthday_classes.size();
                mAllClasses.remove(mBirthdayData);
                mBirthdayData = new ClassInfo(birthday_classes, R.drawable.birthday_information, "有好友今天过生日", count, postTIme, Constants.BIRTHDAY_INFORMATION);
                mAllClasses.add(mBirthdayData);
            }
            sendMessage(Constants.BIRTHDAY_INFORMATION);
        }
    };

    //发来生日祝福的人员名单
    Runnable bless_task = new Runnable() {
        @Override
        public void run() {
            isLoaded_bless = false;
            ArrayList<DynamicNews> bless_classes = DataUtils.getGiftInfo();
            if (bless_classes != null && bless_classes.size() != 0) {
                String postTime = bless_classes.get(0).getDate();
                int count = bless_classes.size();
                mAllClasses.remove(mBlessData);
                mBlessData = new ClassInfo(bless_classes, R.drawable.bless_information, "好友发来祝福", count, postTime, Constants.BLESS_INFORMATION);
                mAllClasses.add(mBlessData);
            }
            sendMessage(Constants.BLESS_INFORMATION);
        }
    };
}
