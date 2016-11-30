package com.geekband.huzhouapp.nav;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.activity.BaseActivity;
import com.geekband.huzhouapp.vo.DynamicNews;

/**
 * Created by Administrator on 2016/8/12
 */
public class BlessMessageActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_message);

        TextView gift_message_back_textBtn = (TextView) findViewById(R.id.gift_message_back_textBtn);
        gift_message_back_textBtn.setOnClickListener(BlessMessageActivity.this);

        TextView giftMessage = (TextView) findViewById(R.id.giftMessage);
        DynamicNews dynamicNews = getIntent().getParcelableExtra("giftMessage");
        giftMessage.setText(dynamicNews.getContent());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gift_message_back_textBtn:
                Intent intent = new Intent(this, ReceiveBlessListActivity.class);
                startActivity(intent);
                this.finish();
                break;
        }
    }
}
