package com.geekband.huzhouapp.nav;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.geekband.huzhouapp.utils.DataOperation;
import com.geekband.huzhouapp.vo.pojo.Document;
import com.geekband.huzhouapp.vo.pojo.UserTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.activity.MainActivity;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.baseadapter.CommonAdapter;
import com.geekband.huzhouapp.baseadapter.ViewHolder;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.DataUtils;
import com.geekband.huzhouapp.vo.ManageInfo;
import com.geekband.huzhouapp.vo.UserBaseInfo;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/19
 */
public class ManageActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView mManage_listView;
    private ProgressBar mManage_progress;
    private TextView mManage_back_textBtn;
    private TextView mUpdate_manage_btn;
    private boolean isUpdate;
    private ArrayList<ManageInfo> mManageList;
    private ScrollView mScrollView;
    private TextView mPrivate_info;
    private UserBaseInfo mUserBaseInfo;
    private UserTable mUserTable;
    private TextView mManage_hint;
    private CommonAdapter<ManageInfo> mCommonAdapter;
    private ProgressDialog mPd;
    private String mUserName;
    private String mRealName;
    private String mPhoneNum;
    private String mEmailAddress;
    private String mSex;
    private String mPoliceNum;
    private String mEducation;
    private String mToWorkTime;
    private String mToPoliceTime;
    private String mRank;
    private String mPolicePost;
    private String mIDcard;
    private EditText mUpdate_realName;
    private EditText mUpdate_phoneNum;
    private EditText mUpdate_emailAddress;
    private EditText mUpdate_sex;
    private EditText mUpdate_police_num;
    private EditText mUpdate_education;
    private EditText mUpdate_work_time;
    private EditText mUpdate_police_time;
    private EditText mUpdate_rank;
    private EditText mUpdate_post;
    private EditText mUpdate_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        isUpdate = true;

        initView();

        new LocalTask().execute();

    }

    private void initView() {
        mManage_back_textBtn = (TextView) findViewById(R.id.manage_back_textBtn);
        mUpdate_manage_btn = (TextView) findViewById(R.id.update_manage_btn);
        mPrivate_info = (TextView) findViewById(R.id.private_info);

        mManage_progress = (ProgressBar) findViewById(R.id.manage_progress);
        mManage_listView = (ListView) findViewById(R.id.manage_listView);

        mScrollView = (ScrollView) findViewById(R.id.update_layout);

        mUpdate_realName = (EditText) findViewById(R.id.update_realName);
        mUpdate_phoneNum = (EditText) findViewById(R.id.update_phoneNum);
        mUpdate_emailAddress = (EditText) findViewById(R.id.update_emailAddress);
        mUpdate_sex = (EditText) findViewById(R.id.update_sex);
        mUpdate_police_num = (EditText) findViewById(R.id.update_police_num);
        mUpdate_work_time = (EditText) findViewById(R.id.update_work_time);
        mUpdate_police_time = (EditText) findViewById(R.id.update_police_time);
        mUpdate_education = (EditText) findViewById(R.id.update_education);
        mUpdate_rank = (EditText) findViewById(R.id.update_rank);
        mUpdate_post = (EditText) findViewById(R.id.update_post);
        mUpdate_card = (EditText) findViewById(R.id.update_card);
        mManage_hint = (TextView) findViewById(R.id.manage_hint);

        mManage_listView.setOnItemClickListener(this);
        mManage_back_textBtn.setOnClickListener(this);
        mUpdate_manage_btn.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //获取用户信息
        new NetTask().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.manage_back_textBtn:
                if (!isUpdate) {
                    //加载浏览布局
                    mManage_listView.setVisibility(View.VISIBLE);
                    mScrollView.setVisibility(View.GONE);
                    mCommonAdapter.notifyDataSetChanged();

                    //更改btn
                    mPrivate_info.setText("个人信息");
                    mManage_back_textBtn.setText("返回");
                    mUpdate_manage_btn.setText("编辑");
                    isUpdate = true;
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    this.finish();
                    isUpdate = false;
                }

                break;
            case R.id.update_manage_btn:
                if (isUpdate) {
                    if (mUserBaseInfo != null) {
                        //加载编辑布局
                        mManage_listView.setVisibility(View.GONE);
                        mScrollView.setVisibility(View.VISIBLE);
                        mUpdate_realName.setText(mRealName);
                        mUpdate_phoneNum.setText(mPhoneNum);
                        mUpdate_emailAddress.setText(mEmailAddress);
                        mUpdate_sex.setText(mSex);
                        mUpdate_police_num.setText(mPoliceNum);
                        mUpdate_education.setText(mEducation);
                        mUpdate_work_time.setText(mToWorkTime);
                        mUpdate_police_time.setText(mToPoliceTime);
                        mUpdate_rank.setText(mRank);
                        mUpdate_post.setText(mPolicePost);
                        mUpdate_card.setText(mIDcard);
                        //更改btn
                        mPrivate_info.setText("编辑信息");
                        mManage_back_textBtn.setText("取消");
                        mUpdate_manage_btn.setText("保存");
                        isUpdate = false;
                    } else {
                        Toast.makeText(this, "请登录官网补充相关资料", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //保存修改后的信息
                    new saveInfo().execute();
                    //标记
                    isUpdate = true;

                }
                break;

        }
    }

    //查询用户详细信息
    class LocalTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            mManage_progress.setVisibility(View.VISIBLE);
            mManage_listView.setVisibility(View.GONE);
            mScrollView.setVisibility(View.GONE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                mUserBaseInfo = MyApplication.sDbUtils.findFirst(UserBaseInfo.class);

            } catch (DbException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mManage_progress.setVisibility(View.GONE);
            if (mUserBaseInfo != null) {
                mManage_listView.setVisibility(View.VISIBLE);
                //初始化用户信息
                mUserName = mUserBaseInfo.getUserName();
                mRealName = mUserBaseInfo.getRealName();
                mPhoneNum = mUserBaseInfo.getPhoneNum();
                mEmailAddress = mUserBaseInfo.getEmailAddress();
                mSex = mUserBaseInfo.getSex();
                mPoliceNum = mUserBaseInfo.getPoliceNum();
                mEducation = mUserBaseInfo.getEducation();
                mToWorkTime = mUserBaseInfo.getToWorkTime();
                mToPoliceTime = mUserBaseInfo.getToPoliceTime();
                mRank = mUserBaseInfo.getRank();
                mPolicePost = mUserBaseInfo.getPolicePost();
                mIDcard = mUserBaseInfo.getIDcard();


                mManageList = new ArrayList<>();
                mManageList.add(new ManageInfo("用户名称", mUserName, R.drawable.app_ailistview_item_three_back));
                mManageList.add(new ManageInfo("真实姓名", mRealName, R.drawable.app_ailistview_item_three_back));
                mManageList.add(new ManageInfo("手机号码", mPhoneNum, R.drawable.app_ailistview_item_three_back));
                mManageList.add(new ManageInfo("邮箱地址", mEmailAddress, R.drawable.app_ailistview_item_three_back));

                mManageList.add(new ManageInfo("性别", mSex, R.drawable.app_ailistview_item_three_back));
                mManageList.add(new ManageInfo("警号", mPoliceNum, R.drawable.app_ailistview_item_three_back));
                mManageList.add(new ManageInfo("教育程度", mEducation, R.drawable.app_ailistview_item_three_back));
                mManageList.add(new ManageInfo("参加工作时间", mToWorkTime, R.drawable.app_ailistview_item_three_back));
                mManageList.add(new ManageInfo("参加公安时间", mToPoliceTime, R.drawable.app_ailistview_item_three_back));
                mManageList.add(new ManageInfo("警衔", mRank, R.drawable.app_ailistview_item_three_back));
                mManageList.add(new ManageInfo("警务", mPolicePost, R.drawable.app_ailistview_item_three_back));
                mManageList.add(new ManageInfo("身份证号码", mIDcard, R.drawable.app_ailistview_item_three_back));


                mCommonAdapter = new CommonAdapter<ManageInfo>(ManageActivity.this, mManageList, R.layout.item_manage_text) {
                    @Override
                    public void convert(ViewHolder viewHolder, ManageInfo item) {
                        viewHolder.setText(R.id.manage_class, item.getTitle());
                        viewHolder.setText(R.id.manage_content, item.getContent());
                        viewHolder.setImage(R.id.manage_image, item.getImageId());
                    }
                };

                mManage_listView.setAdapter(mCommonAdapter);
                isUpdate = true;
            } else {
                mManage_hint.setVisibility(View.VISIBLE);
            }


        }


    }

    class UpdateTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            mPd = ProgressDialog.show(ManageActivity.this, null, "正在保存信息...");
        }

        @Override
        protected Integer doInBackground(String... params) {
            DataOperation.insertOrUpdateTable(mUserTable, (Document) null);
            String contentId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, null);
            DataUtils.saveUserBaseInfo(contentId);
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mPd.dismiss();
            //加载更新后的浏览布局
            mScrollView.setVisibility(View.GONE);
            //获取用户信息
            new LocalTask().execute();
            //更改btn
            mPrivate_info.setText("个人信息");
            mManage_back_textBtn.setText("返回");
            mUpdate_manage_btn.setText("编辑");
            isUpdate = true;
        }
    }

    /**
     * 保存修改后的信息
     */

    public void updateInfo() {
        //保存修改后的资料信息
        //userTable
        if (!mUpdate_realName.getText().toString().equals(mRealName)) {
            mUserTable.putField(UserTable.FIELD_REALNAME, mUpdate_realName.getText().toString());
        }
        if (!mUpdate_phoneNum.getText().toString().equals(mPhoneNum)) {
            mUserTable.putField(UserTable.FIELD_TELEPHONE, mUpdate_phoneNum.getText().toString());
        }
        if (!mUpdate_emailAddress.getText().toString().equals(mEmailAddress)) {
            mUserTable.putField(UserTable.FIELD_EMAIL, mUpdate_emailAddress.getText().toString());
        }

        if (!mUpdate_sex.getText().toString().equals(mSex)) {
            mUserTable.putField(UserTable.FIELD_SEX, mUpdate_sex.getText().toString());
        }
        if (!mUpdate_police_num.getText().toString().equals(mPoliceNum)) {
            mUserTable.putField(UserTable.FIELD_POLICENUM, mUpdate_police_num.getText().toString());
        }
        if (!mUpdate_education.getText().toString().equals(mEducation)) {
            mUserTable.putField(UserTable.FIELD_EDUCATION, mUpdate_education.getText().toString());
        }
        //--
        if (!mUpdate_work_time.getText().toString().equals(mToWorkTime)) {
            mUserTable.putField(UserTable.FIELD_TOWORKTIME, mUpdate_work_time.getText().toString());
        }
        if (!mUpdate_police_time.getText().toString().equals(mToPoliceTime)) {
            mUserTable.putField(UserTable.FIELD_TOPOLICETIME, mUpdate_police_time.getText().toString());
        }
        if (!mUpdate_rank.getText().toString().equals(mRank)) {
            mUserTable.putField(UserTable.FIELD_RANK, mUpdate_rank.getText().toString());
        }
        if (!mUpdate_post.getText().toString().equals(mPolicePost)) {
            //System.out.println("修改后的信息："+mUpdate_post.getText().toString());
            mUserTable.putField(UserTable.FIELD_POLICEPOST, mUpdate_post.getText().toString());
        }
        if (!mUpdate_card.getText().toString().equals(mIDcard)) {
            mUserTable.putField(UserTable.FIELD_IDCARDNO, mUpdate_card.getText().toString());
        }
    }

    class saveInfo extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            String contentId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, null);
            //noinspection unchecked
            ArrayList<UserTable> userTables = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, contentId);
            if (userTables != null && userTables.size() != 0) {
                mUserTable = userTables.get(0);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            //编辑信息
            if (mUserTable != null ) {
                updateInfo();
                //更新信息
                new UpdateTask().execute();
            } else {
                Toast.makeText(ManageActivity.this, "链接服务器失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class NetTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... params) {
            String contentId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, null);
            DataUtils.saveUserBaseInfo(contentId);
            return null;
        }
    }


}
