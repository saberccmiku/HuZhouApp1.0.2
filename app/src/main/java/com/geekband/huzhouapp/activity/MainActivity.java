package com.geekband.huzhouapp.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.database.dto.DataOperation;
import com.database.pojo.Document;
import com.database.pojo.UserTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.custom.CircleImage;
import com.geekband.huzhouapp.fragment.advice.AdviceFragment;
import com.geekband.huzhouapp.fragment.message.MessageFragment;
import com.geekband.huzhouapp.fragment.news.NewsFragment;
import com.geekband.huzhouapp.nav.CameraActivity;
import com.geekband.huzhouapp.nav.GalleryActivity;
import com.geekband.huzhouapp.nav.InteractiveActivity;
import com.geekband.huzhouapp.nav.ManageActivity;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.FileUtil;
import com.geekband.huzhouapp.utils.FileUtils;
import com.geekband.huzhouapp.utils.GetLocalBitmap;
import com.geekband.huzhouapp.utils.SelectPicPopupWindow;
import com.geekband.huzhouapp.vo.CourseInfo;
import com.geekband.huzhouapp.vo.UserBaseInfo;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static final String UP_IMAGE_NOW = "正在上传图片，请稍候...";
    public static final String CLEAR_CACHE_NOW = "正在清理相关缓存，请稍等...";
    private ImageButton mNewsImageButton;
    private ImageButton mInformImageButton;
    private ImageButton mAdviceImageButton;
    private FragmentManager mFragmentManager;

    private NewsFragment mNewsFragment;
    private MessageFragment mMessageFragment;
    private AdviceFragment mAdviceFragment;
    private NavigationView mNavigationView;
    private CircleImage mAvatar_imageBtn;
    private SelectPicPopupWindow mPicPopupWindow;
    private static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称
    private String urlPath;            // 图片本地路径
    private static ProgressDialog pd;// 等待进度圈
    private static final int REQUESTCODE_PICK = 0;        // 相册选图标记
    private static final int REQUESTCODE_TAKE = 1;        // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;    // 图片裁切标记


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取控件
        findView();
        //设置监听事件
        setListener();
        //初始化headerView
        initHeaderView();

        mFragmentManager = getSupportFragmentManager();
        // 第一次启动时选中第0个tab
        setTabSelection(0);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setListener() {
        mNavigationView.setNavigationItemSelectedListener(this);
        mNewsImageButton.setOnClickListener(this);
        mInformImageButton.setOnClickListener(this);
        mAdviceImageButton.setOnClickListener(this);
    }

    private void findView() {
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNewsImageButton = (ImageButton) findViewById(R.id.newsImageButton);
        mInformImageButton = (ImageButton) findViewById(R.id.messageButton);
        mAdviceImageButton = (ImageButton) findViewById(R.id.adviceImageButton);

        mNewsImageButton.setBackgroundResource(R.mipmap.app_tab3_hover);
        mInformImageButton.setBackgroundResource(R.mipmap.app_tab2);
        mAdviceImageButton.setBackgroundResource(R.mipmap.app_tab4);

    }

    private void initHeaderView() {
        View headerView = mNavigationView.getHeaderView(0);
        mAvatar_imageBtn = (CircleImage) headerView.findViewById(R.id.avatar_imageBtn);
        mAvatar_imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicPopupWindow = new SelectPicPopupWindow(MainActivity.this, itemsOnClick);
                mPicPopupWindow.showAtLocation(findViewById(R.id.drawer_layout),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
        TextView userName_text = (TextView) headerView.findViewById(R.id.userName_text);
        String nameStr = getString(R.string.welcome)+MyApplication.sSharedPreferences.getString(Constants.USER_REAL_NAME, null)+"!";
        userName_text.setText(nameStr);
        userName_text.setTextColor(Color.RED);

        String avatarPath = MyApplication.sSharedPreferences.getString(Constants.AVATAR_IMAGE, null);
        if (avatarPath != null) {
            mAvatar_imageBtn.setImageBitmap(GetLocalBitmap.convertToBitmap(
                    avatarPath, 100, 100));
        }else {
            String avatarUrl = MyApplication.sSharedPreferences.getString(Constants.AVATAR_URL,null);
            System.out.println("avatarUrl头像的地址为"+avatarUrl);
            BitmapUtils bitmapUtils = BitmapHelper.getBitmapUtils(MainActivity.this,null,R.drawable.head_default,R.drawable.head_default);
            bitmapUtils.display(mAvatar_imageBtn,avatarUrl);
        }

    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
////            exitProgram();
//        }
//    }
//
//    //点击back退出进程，进入stop状态
//    public void exitProgram() {
//        Intent startMain = new Intent(Intent.ACTION_MAIN);
//        startMain.addCategory(Intent.CATEGORY_HOME);
//        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(startMain);
//        android.os.Process.killProcess(android.os.Process.myPid());
//    }

    /**
     * back键不退出程序，最小化程序
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode()==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            //"android.intent.action.MAIN"
            intent.setAction(Intent.ACTION_MAIN);
            //"android.intent.category.HOME"
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = new Intent();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            intent.setClass(this, CameraActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            intent.setClass(this, GalleryActivity.class);
            startActivity(intent);
        } else if (id == R.id.login_up) {
            //取消自动登录
            SharedPreferences.Editor editor = MyApplication.sSharedPreferences.edit();
            editor.putString(Constants.AUTO_LOGIN, null);
            editor.apply();
            new ClearDataTask().execute();
        } else if (id == R.id.nav_manage) {
            intent.setClass(this, ManageActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
           new ClearCacheTask().execute();

        } else if (id == R.id.nav_send) {
            intent.setClass(this, InteractiveActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 为弹出窗口实现监听类
     */

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPicPopupWindow.dismiss();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Constants.IMAGE_TYPE);
                    startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            String fileName = FileUtils.getCurrentTimeStr()+"avatars.jpg";
            urlPath = FileUtil.saveFile(Constants.AVATAR_DIRECTORY_NAME,fileName, photo);
            //保存地址值方便以后登录直接获取该图片
            SharedPreferences.Editor editor = MyApplication.sSharedPreferences.edit();
            editor.putString(Constants.AVATAR_IMAGE, urlPath);
            editor.apply();
            mAvatar_imageBtn.setImageDrawable(drawable);

            //启动服务器上传
            if (urlPath != null || urlPath != "") {
                new UpAvatarImageTask().execute(urlPath);
            }

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newsImageButton:
                setTabSelection(0);
                mNewsImageButton.setBackgroundResource(R.mipmap.app_tab3_hover);
                mInformImageButton.setBackgroundResource(R.mipmap.app_tab2);
                mAdviceImageButton.setBackgroundResource(R.mipmap.app_tab4);
                break;
            case R.id.messageButton:
                setTabSelection(1);
                mNewsImageButton.setBackgroundResource(R.mipmap.app_tab3);
                mInformImageButton.setBackgroundResource(R.mipmap.app_tab2_hover);
                mAdviceImageButton.setBackgroundResource(R.mipmap.app_tab4);
                break;
            case R.id.adviceImageButton:
                setTabSelection(2);
                mNewsImageButton.setBackgroundResource(R.mipmap.app_tab3);
                mInformImageButton.setBackgroundResource(R.mipmap.app_tab2);
                mAdviceImageButton.setBackgroundResource(R.mipmap.app_tab4_hover);
                break;

        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index 每个tab页对应的下标。0表示新闻，1表示消息，2表示咨询。
     */
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态

        // 开启一个Fragment事务
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                if (mNewsFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mNewsFragment = new NewsFragment();
                    transaction.add(R.id.contentFragment, mNewsFragment);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mNewsFragment);
                }
                break;
            case 1:
                // 当点击了联系人tab时，改变控件的图片和文字颜色

                if (mMessageFragment == null) {
                    // Fragment为空，则创建一个并添加到界面上
                    mMessageFragment = new MessageFragment();
                    transaction.add(R.id.contentFragment, mMessageFragment);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mMessageFragment);
                }
                break;
            case 2:
                // 当点击了动态tab时，改变控件的图片和文字颜色

                if (mAdviceFragment == null) {
                    // 如果Fragment为空，则创建一个并添加到界面上
                    mAdviceFragment = new AdviceFragment();
                    transaction.add(R.id.contentFragment, mAdviceFragment);
                } else {
                    // 如果Fragment不为空，则直接将它显示出来
                    transaction.show(mAdviceFragment);
                }
                break;

        }
        transaction.commit();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (mNewsFragment != null) {
            transaction.hide(mNewsFragment);
        }
        if (mMessageFragment != null) {
            transaction.hide(mMessageFragment);
        }
        if (mAdviceFragment != null) {
            transaction.hide(mAdviceFragment);
        }

    }


    class UpAvatarImageTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            // 新线程后台上传服务端,这里会产生窗体泄露异常，在activity关闭之前先关闭dialog，
            // activity关闭会调用onPause所以在这里面关闭dialog方法为AlertDialog.dismiss()
            pd = ProgressDialog.show(MainActivity.this, null, UP_IMAGE_NOW);
        }

        @Override
        protected Integer doInBackground(String... params) {
            String contentId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN,null);
            ArrayList<?> userTables = DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, contentId);
            if (userTables!=null&&userTables.size()!=0){
                UserTable userTable = (UserTable) userTables.get(0);
                if (userTable!=null) {
                    DataOperation.insertOrUpdateTable(userTable, new Document[]{new Document(params[0])});
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            //关闭dialog
            pd.dismiss();

        }
    }

    class ClearDataTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(MainActivity.this, null, CLEAR_CACHE_NOW);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                MyApplication.sDbUtils.deleteAll(UserBaseInfo.class);
                MyApplication.sDbUtils.deleteAll(CourseInfo.class);
                //MyApplication.sDbUtils.deleteAll(AlbumInfo.class);
                MyApplication.sDbUtils.deleteAll(CourseInfo.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            pd.dismiss();
            //清除通知栏信息
            MyApplication.mNotificationManager.cancelAll();
            //清理完毕回到登录界面
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    }

    class ClearCacheTask extends AsyncTask<String,Integer,Integer>{
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(MainActivity.this,null,"正在清理缓存...");
        }

        @Override
        protected Integer doInBackground(String... params) {
            FileUtil.clearFolder("");
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            pd.dismiss();
        }
    }

}
