package com.geekband.huzhouapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.geekband.huzhouapp.utils.DataOperation;
import com.geekband.huzhouapp.vo.pojo.ContentTable;
import com.geekband.huzhouapp.vo.pojo.OpinionTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.baseadapter.CommonAdapter;
import com.geekband.huzhouapp.baseadapter.ViewHolder;
import com.geekband.huzhouapp.fragment.news.GradeActivity;
import com.geekband.huzhouapp.nav.InteractiveActivity;
import com.geekband.huzhouapp.nav.ReceiveBlessListActivity;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.FileUtils;
import com.geekband.huzhouapp.vo.BirthdayInfo;
import com.geekband.huzhouapp.vo.ClassInfo;
import com.geekband.huzhouapp.vo.DynamicNews;
import com.geekband.huzhouapp.vo.GradeInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/9
 */
public class InformationContentActivity extends BaseActivity implements View.OnClickListener {

    private TextView mClass_title;
    private ArrayList<DynamicNews> mContents;
    private ListView mListView;
    private CommonAdapter<DynamicNews> mAdapter;
    private RadioGroup mReview_radio;
    private RadioButton mReview_radio_true;
    private RadioButton mReview_radio_false;
    private String mIsPassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_content);
        //布局数据的容器
        mContents = new ArrayList<>();
        //获取传递的值
        Intent intent = getIntent();
        ClassInfo classInfo = intent.getParcelableExtra(Constants.INFORMATION_CONTENT);
        //初始化控件
        findView();
        //设置标题
        setClassTitle(classInfo, mClass_title);
        //获取文件类型
        int flag = classInfo.getFlag();

        if (flag == Constants.REVIEW_INFORMATION) {
            //noinspection unchecked
            ArrayList<OpinionTable> list = (ArrayList<OpinionTable>) classInfo.getList();
            sendMessage(Constants.REVIEW_INFORMATION, Constants.REVIEW_INFORMATION, list);

        } else if (flag == Constants.RESULT_INFORMATION) {
            //noinspection unchecked
            ArrayList<OpinionTable> list = (ArrayList<OpinionTable>) classInfo.getList();
            sendMessage(Constants.RESULT_INFORMATION, Constants.RESULT_INFORMATION, list);

        } else if (flag == Constants.GRADE_INFORMATION) {
            //noinspection unchecked
            ArrayList<GradeInfo> list = (ArrayList<GradeInfo>) classInfo.getList();
            sendMessage(Constants.GRADE_INFORMATION, Constants.GRADE_INFORMATION, list);

        } else if (flag == Constants.BIRTHDAY_INFORMATION) {
            //noinspection unchecked
            ArrayList<BirthdayInfo> birthdayInfos = (ArrayList<BirthdayInfo>) classInfo.getList();
            sendMessage(Constants.BIRTHDAY_INFORMATION, Constants.BIRTHDAY_INFORMATION, birthdayInfos);

        } else if (flag == Constants.BLESS_INFORMATION) {
            //noinspection unchecked
            ArrayList<DynamicNews> dns = (ArrayList<DynamicNews>) classInfo.getList();
            sendMessage(Constants.BLESS_INFORMATION, Constants.BLESS_INFORMATION, dns);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.class_title:
                this.finish();
                break;
        }
    }

    //设置标题
    public void setClassTitle(ClassInfo classInfo, TextView tv) {
        tv.setText(classInfo.getClassTitle());
    }

    public void findView() {
        mClass_title = (TextView) findViewById(R.id.class_title);
        mClass_title.setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.information_content_listView);

    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(final Message msg) {
            switch (msg.what) {
                case Constants.REVIEW_INFORMATION:
                    mContents.clear();
                    //noinspection unchecked
                    final ArrayList<OpinionTable> opinionTables = (ArrayList<OpinionTable>) msg.obj;
                    new Thread() {
                        @Override
                        public void run() {
                            if (opinionTables != null && opinionTables.size() != 0) {
                                for (OpinionTable opinionTable : opinionTables) {
                                    String contentId = opinionTable.getContentId();
                                    String date = opinionTable.getField(OpinionTable.FIELD_POSTTIME);
                                    //noinspection unchecked
                                    ArrayList<ContentTable> contentTables = (ArrayList<ContentTable>) DataOperation.queryTable(ContentTable.TABLE_NAME, ContentTable.FIELD_NEWSID, contentId);
                                    if (contentTables != null && contentTables.size() != 0) {
                                        String content = contentTables.get(0).getField(ContentTable.FIELD_SUBSTANCE);
                                        DynamicNews dn = new DynamicNews();
                                        dn.setContentId(contentId);
                                        dn.setContent(content);
                                        dn.setDate(date);
                                        mContents.add(dn);
                                    }
                                }
                            }
                            sendMessage(Constants.REVIEW_INFORMATION, Constants.LOADED, null);
                        }
                    }.start();
                    break;
                case Constants.RESULT_INFORMATION:
                    mContents.clear();
                    //noinspection unchecked
                    final ArrayList<OpinionTable> results = (ArrayList<OpinionTable>) msg.obj;
                    new Thread() {
                        @Override
                        public void run() {
                            if (results != null && results.size() != 0) {
                                for (OpinionTable opinionTable : results) {
                                    String contentId = opinionTable.getContentId();
                                    String date = opinionTable.getField(OpinionTable.FIELD_PASSTIME);
                                    String isPassed = opinionTable.getField(OpinionTable.FIELD_ISPASSED);
                                    if (isPassed.equals("1")) {
                                        isPassed = "通过审核";
                                    } else if (isPassed.equals("0")) {
                                        isPassed = "未通过审核";
                                    }
                                    String content = "";
                                    //noinspection unchecked
                                    ArrayList<ContentTable> contentTables = (ArrayList<ContentTable>) DataOperation.queryTable(ContentTable.TABLE_NAME, ContentTable.FIELD_NEWSID, contentId);
                                    if (contentTables != null && contentTables.size() != 0) {
                                        content = contentTables.get(0).getField(ContentTable.FIELD_SUBSTANCE);
                                        DynamicNews dn = new DynamicNews();
                                        dn.setTitle(isPassed);
                                        dn.setContentId(contentId);
                                        dn.setContent(content);
                                        dn.setDate(date);
                                        mContents.add(dn);
                                    }
                                }
                            }
                            sendMessage(Constants.RESULT_INFORMATION, Constants.LOADED, null);
                        }
                    }.start();
                    break;
                case Constants.GRADE_INFORMATION:
                    toOtherActivity(GradeActivity.class);
                    break;
                case Constants.BIRTHDAY_INFORMATION:
                    toOtherActivity(InteractiveActivity.class);
                    break;
                case Constants.BLESS_INFORMATION:
                    toOtherActivity(ReceiveBlessListActivity.class);
                    break;
                case Constants.LOADED:
                    initView(msg.arg1);
                    break;
                case Constants.COMMITED:
                    //noinspection unchecked
                    final ArrayList<DynamicNews> dns = (ArrayList<DynamicNews>) msg.obj;
                    mContents.removeAll(dns);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(InformationContentActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.UNCOMMIT:
                    Toast.makeText(InformationContentActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    public void sendMessage(int fromFlag, int toFlag, ArrayList<?> list) {
        Message message = Message.obtain();
        message.what = toFlag;
        message.arg1 = fromFlag;
        if (list != null) {
            message.obj = list;
        }
        mHandler.sendMessage(message);
    }

    public void toOtherActivity(Class activityClass) {
        Intent intent = new Intent(InformationContentActivity.this, activityClass);
        startActivity(intent);
        InformationContentActivity.this.finish();
    }

    public void initView(final int flag) {
        mAdapter = new CommonAdapter<DynamicNews>(this, mContents, R.layout.information_content_item) {
            @Override
            public void convert(ViewHolder viewHolder, final DynamicNews item) {
                if (flag == Constants.REVIEW_INFORMATION) {
                    viewHolder.getView(R.id.review_commit_layout).setVisibility(View.VISIBLE);
                    mReview_radio = viewHolder.getView(R.id.review_radio);
                    mReview_radio_true = viewHolder.getView(R.id.review_radio_true);
                    mReview_radio_false = viewHolder.getView(R.id.review_radio_false);
                    //获取RadioGroup选中的值
                    mReview_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            if (checkedId == mReview_radio_true.getId()) {
                                mIsPassed = Constants.OPINION_PASSED;
                            } else if (checkedId == mReview_radio_false.getId()) {
                                mIsPassed = Constants.OPINION_NOT_PASSED;
                            }
                        }
                    });

                    //提交审核意见
                    final String contentId = item.getContentId();
                    viewHolder.getView(R.id.review_commit_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final ArrayList<DynamicNews> dns = new ArrayList<>();
                            dns.add(item);
                            new Thread() {
                                @Override
                                public void run() {
                                    //noinspection unchecked
                                    ArrayList<OpinionTable> opinionTables = (ArrayList<OpinionTable>) DataOperation.queryTable(OpinionTable.TABLE_NAME,OpinionTable.CONTENTID,contentId);
                                    if (opinionTables != null && opinionTables.size() != 0) {
                                        OpinionTable opinionTable = opinionTables.get(0);
                                        opinionTable.putField(OpinionTable.FIELD_ISPASSED, mIsPassed);
                                        opinionTable.putField(OpinionTable.FIELD_PASSTIME, FileUtils.getCurrentTimeStr("yyyy-MM-dd HH:mm:ss"));
                                        if (DataOperation.insertOrUpdateTable(opinionTable)) {
                                            sendMessage(0, Constants.COMMITED, dns);
                                        } else {
                                            sendMessage(0, Constants.UNCOMMIT, dns);
                                        }

                                    }
                                }
                            }.start();
                        }
                    });
                } else if (flag == Constants.RESULT_INFORMATION) {
                    viewHolder.getView(R.id.review_commit_layout).setVisibility(View.GONE);
                    TextView review_operation_item = viewHolder.getView(R.id.review_operation_item);
                    review_operation_item.setText(item.getTitle());
                    review_operation_item.setTextColor(Color.RED);
                }
                viewHolder.setText(R.id.information_postTime__item, item.getDate());
                viewHolder.setText(R.id.information_content_item, item.getContent());
            }
        };

        mListView.setAdapter(mAdapter);
    }

}
