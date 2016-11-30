package com.geekband.huzhouapp.nav;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.geekband.huzhouapp.utils.DataOperation;
import com.geekband.huzhouapp.vo.pojo.CommonTable;
import com.geekband.huzhouapp.vo.pojo.ContentTable;
import com.geekband.huzhouapp.vo.pojo.Document;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.activity.BaseActivity;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.FileUtils;

/**
 * Created by Administrator on 2016/7/8
 */
public class SendGiftActivity extends BaseActivity implements View.OnClickListener {

    private EditText mGift_edit;
    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);
        initView();

    }

    private void initView() {
        TextView gift_back_textBtn = (TextView) findViewById(R.id.gift_back_textBtn);
        mGift_edit = (EditText) findViewById(R.id.gift_edit);
        TextView gift_send_btn = (TextView) findViewById(R.id.gift_send_btn);
        gift_send_btn.setOnClickListener(this);
        gift_back_textBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gift_back_textBtn:
                this.finish();
                break;
            case R.id.gift_send_btn:
                String contentStr = mGift_edit.getText().toString();
                String receiverId = getIntent().getStringExtra(Constants.BIRTHDAY_GIFT);
                String senderId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, null);
                new SendGiftTask().execute(contentStr, receiverId, senderId);


        }
    }

    class SendGiftTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            mPd = ProgressDialog.show(SendGiftActivity.this, null, "正在发送");
        }

        @Override
        protected Integer doInBackground(String... params) {
            String contentStr = params[0];
            String receiverId = params[1];
            String senderId = params[2];
            String postDate = FileUtils.getCurrentTimeStr("yyyy-MM-dd HH:mm:ss");
            //插入一张通用信息表
            CommonTable commonTable = new CommonTable();
            commonTable.putField(CommonTable.FIELD_WRITERID, senderId);
            commonTable.putField(CommonTable.FIELD_DATETIME, postDate);
            commonTable.putField(CommonTable.FIELD_AUDITOR, receiverId);
            commonTable.putField(CommonTable.FIELD_ISPASSED, "0");
            DataOperation.insertOrUpdateTable(commonTable, (Document) null);

            //获取通用表并插入数据
            ContentTable contentTable = new ContentTable();
            //区分日期
            String divDate = FileUtils.getCurrentTimeStr("yyyy-MM-dd");
            contentTable.putField(ContentTable.FIELD_SUBSTANCE, contentStr);
            contentTable.putField(ContentTable.FIELD_NEWSID, commonTable.getContentId());
            contentTable.putField(ContentTable.FIELD_DIVI, divDate);
            //这里可以选择放入贺卡
            DataOperation.insertOrUpdateTable(contentTable, (Document) null);
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mPd.dismiss();
            SendGiftActivity.this.finish();

        }
    }
}
