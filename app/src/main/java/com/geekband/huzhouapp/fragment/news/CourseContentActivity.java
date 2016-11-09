package com.geekband.huzhouapp.fragment.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.vo.CourseInfo;

/**
 * Created by Administrator on 2016/5/29
 */
public class CourseContentActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_content);

        Intent intent = getIntent();
        CourseInfo courseInfo = intent.getParcelableExtra(Constants.COURSE_CONTENT);

        if (courseInfo != null) {
            TextView course_name = (TextView) findViewById(R.id.course_name);
            TextView course_type = (TextView) findViewById(R.id.course_type);
            TextView course_time = (TextView) findViewById(R.id.course_time);
            TextView course_intro = (TextView) findViewById(R.id.course_intro);
            WebView webView = (WebView) findViewById(R.id.course_detailed);

            course_name.setText(courseInfo.getTitle());
            course_type.setText(courseInfo.getType());
            course_time.setText(courseInfo.getTime());
            course_intro.setText(courseInfo.getIntro());


            // 获取webView的相关配置
            WebSettings webSettings = webView.getSettings();
            //webView的缓存模式
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            //自动加载图片
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setBuiltInZoomControls(true);
            //自动缩放
            webSettings.setSupportZoom(true);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setBlockNetworkImage(false);
            webSettings.setDomStorageEnabled(true);

            webView.setBackgroundColor(0);
            webView.getBackground().setAlpha(0);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            String htmlStr = "<html><head><style>img{width:100%;height:auto; !important;}</style></head>" + "<body>" + courseInfo.getDetailed() + "</body>" + "</html>";
            webView.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null);

            webView.setVisibility(View.VISIBLE);
        }
    }

}
