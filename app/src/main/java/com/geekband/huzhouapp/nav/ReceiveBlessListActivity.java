package com.geekband.huzhouapp.nav;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.geekband.huzhouapp.utils.DataOperation;
import com.geekband.huzhouapp.vo.pojo.CommonTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.activity.BaseActivity;
import com.geekband.huzhouapp.baseadapter.CommonAdapter;
import com.geekband.huzhouapp.baseadapter.ViewHolder;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.geekband.huzhouapp.utils.DataUtils;
import com.geekband.huzhouapp.vo.DynamicNews;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/12
 */
public class ReceiveBlessListActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private static final int READ_INFORMATION = 1;
    private static final int UNSUCCESSFUL = 2;
    private static final int SUCCESSFUL = 3;
    private ListView mListView;
    private BitmapUtils mBitmapUtils;
    private ArrayList<DynamicNews> mDynamicNewses;
    private ProgressBar mGift_list_progress;
    private TextView mGift_list_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_gift);

        mDynamicNewses = new ArrayList<>();
        findView();

        if (getIntent().getAction()!=null&&getIntent().getAction().equals("messageToActivity")){
            mDynamicNewses = getIntent().getParcelableArrayListExtra("giftMessageToActivity");
            initView();
        }else {
            new GiftMessageTask().execute();
        }

    }

    private void initView() {
            if (mDynamicNewses!=null&&mDynamicNewses.size()!=0){
                CommonAdapter<DynamicNews> commonAdapter = new CommonAdapter<DynamicNews>(this,mDynamicNewses,R.layout.item_birthday) {
                    @Override
                    public void convert(ViewHolder viewHolder, DynamicNews item) {
                        mBitmapUtils.display(viewHolder.getView(R.id.avatar_birthday),item.getPicUrl());
                        viewHolder.setText(R.id.name_birthday, item.getWriter());
                        viewHolder.setText(R.id.content_birthday,"给您发来生日祝福，点击查看吧");
                    }
                };
                mListView.setAdapter(commonAdapter);
            }


    }

    private void findView() {

        Button blessList_backBtn = (Button) findViewById(R.id.blessList_backBtn);
        blessList_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiveBlessListActivity.this.finish();
            }
        });

        mBitmapUtils = BitmapHelper.getBitmapUtils(this, mListView, R.drawable.head_default, R.drawable.head_default);
        mListView = (ListView) findViewById(R.id.receiver_gift_list);
        mListView.setOnItemClickListener(this);

        mGift_list_progress = (ProgressBar) findViewById(R.id.gift_list_progress);
        mGift_list_text = (TextView) findViewById(R.id.gift_list_text);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //标记已读信息
        Message message = Message.obtain();
        message.obj = position;
        message.what = READ_INFORMATION;
        mHandler.sendMessage(message);
        //跳转详情界面
        Intent intent = new Intent();
        intent.setClass(this, BlessMessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("giftMessage",mDynamicNewses.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
        ReceiveBlessListActivity.this.finish();
    }

    class GiftMessageTask extends AsyncTask<String,Integer,Integer>{

        @Override
        protected void onPreExecute() {
            mGift_list_progress.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            mDynamicNewses = DataUtils.getGiftInfo();
            if (mDynamicNewses!=null&&mDynamicNewses.size()!=0){
                return 1;
            }
            return 2;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mGift_list_progress.setVisibility(View.GONE);
            if (integer==1){
                mGift_list_text.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                initView();
            }else if (integer==2){
                mGift_list_text.setText("暂无信息...");
                mGift_list_text.setVisibility(View.VISIBLE);
            }

        }
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(final Message msg) {
            switch (msg.what){
                case READ_INFORMATION:
                    final int position = (int) msg.obj;
                    new Thread(){
                        @Override
                        public void run() {
                            //noinspection unchecked
                            ArrayList<CommonTable> commonTables = (ArrayList<CommonTable>) DataOperation.queryTable
                                    (CommonTable.TABLE_NAME,CommonTable.CONTENTID,mDynamicNewses.get(position).getContentId());
                            if (commonTables!=null&&commonTables.size()!=0) {
                                CommonTable commonTable = commonTables.get(0);
                                //“0”表示未读，“1”表示已读
                                commonTable.putField(CommonTable.FIELD_ISPASSED, "1");
                                if (DataOperation.insertOrUpdateTable(commonTable)){
                                    Message message = Message.obtain();
                                    message.what = SUCCESSFUL;
                                    mHandler.sendMessage(message);
                                }else {
                                    Message message = Message.obtain();
                                    message.what = UNSUCCESSFUL;
                                    mHandler.sendMessage(message);
                                }
                            }
                        }
                    }.start();
                    break;

                case SUCCESSFUL:
                    break;
                case UNSUCCESSFUL:
                    break;
            }
            return false;
        }
    });


}
