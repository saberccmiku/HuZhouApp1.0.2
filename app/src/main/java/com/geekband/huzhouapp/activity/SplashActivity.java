package com.geekband.huzhouapp.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.database.dto.DataOperation;
import com.database.pojo.AppVersionTable;
import com.database.pojo.BaseTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.custom.progress.number.NumberProgressBar;
import com.geekband.huzhouapp.custom.text.shimmer.Shimmer;
import com.geekband.huzhouapp.custom.text.shimmer.ShimmerTextView;
import com.geekband.huzhouapp.utils.AnimationView;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.FileUtil;
import com.geekband.huzhouapp.utils.LinkNet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/12
 */
public class SplashActivity extends Activity {

    private NumberProgressBar loadingSeekBar;
    private ImageView loading_animation;
    private ShimmerTextView app_name;
    private Shimmer mShimmer;
    private final int NEED_UPDATE = 0;
    private final int UPDATE_PROGRESS = 1;
    private final int LOADED = 2;

    /**
     * 处理ui信息
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case NEED_UPDATE:
                    BaseTable versionTable = (BaseTable) msg.obj;
                    showUpdateDialog(versionTable);
                    break;
                case UPDATE_PROGRESS:
                    int downLoadedLength = (int) msg.obj;
                    loadingSeekBar.setProgress(downLoadedLength);
                    break;
                default:
                    break;
            }
            return false;
        }
    });


    private void showUpdateDialog(final BaseTable versionTable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("版本更新");
        builder.setMessage(versionTable.getField(AppVersionTable.FIELD_DETAIL));
        builder.setPositiveButton("欣然接受", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadingSeekBar.setVisibility(View.VISIBLE);
                downloadApk(versionTable.getAccessaryFileUrlList().get(0), SplashActivity.this);
            }
        });

        builder.setNegativeButton("残忍拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //登陆
                toLogin();
            }
        });

        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 250) {
            if (resultCode == RESULT_CANCELED) {
                //登陆
                toLogin();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //获取控件
        findView();
        //加载动画
        loadAnimation();

    }

    private void findView() {
        loadingSeekBar = (NumberProgressBar) findViewById(R.id.loading_progress);
        loadingSeekBar.setMax(100);
        loading_animation = (ImageView) findViewById(R.id.loading_animation);
        app_name = (ShimmerTextView) findViewById(R.id.app_name);
        //设置山闪动文字的渐变颜色
        app_name.setTextColor(Color.WHITE);
        app_name.setReflectionColor(Color.YELLOW);
    }

    private void loadAnimation() {
        ImageView guide_picture = (ImageView) findViewById(R.id.guide_picture);
        guide_picture.setImageResource(R.mipmap.welcome);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in_scale);
        guide_picture.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                AnimationView.shimmerAnimation(mShimmer, app_name);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //加载网络数据,判断是否有网络
                if (LinkNet.isNetworkAvailable(SplashActivity.this)) {
//            无服务器测试登录调试
//            startActivity(new Intent(SplashActivity.this, MainActivity.class));
//            SplashActivity.this.finish();
                    initLoadingAnimation();
                    checkUpdate();

                } else {
                    String isAuto = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, null);
                    if (isAuto != null) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        SplashActivity.this.finish();
                    } else {
                        Toast.makeText(SplashActivity.this, "当前网络不可用，部分功能无法运行", Toast.LENGTH_SHORT).show();
                    }
                }

                overridePendingTransition(R.anim.welcome_fade_in, R.anim.welcome_fade_out);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    /**
     * 检测版本更新
     */
    private void checkUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //noinspection unchecked
                ArrayList<BaseTable> versionTables = (ArrayList<BaseTable>) DataOperation.queryTable(AppVersionTable.TABLE_NAME);
                if (versionTables != null && versionTables.size() != 0) {
                    int position = 0;
                    if (versionTables.size() > 1) {
                        for (int i = 0; i < versionTables.size() - 1; i++) {
                            String firstTime = versionTables.get(i).getField(AppVersionTable.FIELD_TIME);
                            long firstMillion = getMillionTime(firstTime);
                            String secondTime = versionTables.get(i + 1).getField(AppVersionTable.FIELD_TIME);
                            long secondMillion = getMillionTime(secondTime);
                            if (firstMillion > secondMillion) {
                                position = i;
                            } else {
                                position = i + 1;
                            }
                        }
                    } else {
                        position = 0;
                    }
                    BaseTable versionTable = versionTables.get(position);
                    String app_vn = versionTable.getField(AppVersionTable.FIELD_VN);
                    String currentVersionName = getVersionName();

                    if (!app_vn.equals(currentVersionName)) {
                        Message message = Message.obtain();
                        sendMessage(message, NEED_UPDATE, versionTable);
                    } else {
                        //没有最新版本，登陆
                        toLogin();
                    }
                } else {
                    //暂未发布版本，登陆
                    toLogin();
                }

            }
        }).start();

    }

    /**
     * 检测版本动画
     */
    private void initLoadingAnimation() {
        loading_animation.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(loading_animation, "rotation", 0, 360);
        animator.setDuration(500);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();

    }

    /**
     * 获取当前版本号
     *
     * @return
     */
    public String getVersionName() {
        //获取包管理器
        PackageManager pm = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return null;
    }

    //登录
    public void toLogin() {

        // 判断之前是否登录过，如果已经登录过就直接进入主界面，不再登录认证。如果退出登录过，那么进入登录界面登录进入

        String isAuto = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, null);
        Intent intent = new Intent();
        if (isAuto != null) {
            intent.setClass(SplashActivity.this, MainActivity.class);
        } else {
            intent.setClass(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        SplashActivity.this.finish();

    }


    public void installApk(File apkFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        startActivityForResult(intent, 250);
    }

    public long getMillionTime(String time) {
        long timeMillion = 0;
        String subTime = time.substring(0, time.lastIndexOf("."));
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        try {
            timeMillion = sf.parse(subTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeMillion;
    }

    //apk下载b并安装
    public void downloadApk(final String urlStr, final SplashActivity splashActivity) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //下载
                InputStream in = null;
                OutputStream out = null;
                int downedFileLength = 0;
                URL url;
                try {
                    url = new URL(urlStr);
                    in = url.openStream();
                    if (in == null) {
                        throw new Exception("获取下载文件失败！");
                    }

                    //写入到本地文件
                    String fileType = "apk";
                    String fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1) + ".apk";
                    File localFile = FileUtil.saveFile(fileType, fileName);
                    out = new FileOutputStream(localFile);

                    //文件总长度
                    float totalLength = url.openConnection().getContentLength();
                    byte[] buffer = new byte[1024 * 2];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                        downedFileLength += len;
                        //发送更新进度条信息
                        sendMessage(mHandler.obtainMessage(), UPDATE_PROGRESS, (int) ((downedFileLength / totalLength) * 100));
                        out.flush();
                    }
                    //发送下载完毕信息
                    sendMessage(mHandler.obtainMessage(), LOADED, downedFileLength);
                    //下载完毕安装
                    splashActivity.installApk(localFile);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                        if (out != null) {
                            out.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

    }

    //自定义发送消息
    public void sendMessage(Message message, int type, Object obj) {
        message.what = type;
        message.obj = obj;
        mHandler.sendMessage(message);
    }
}
