package com.geekband.huzhouapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.database.dto.DataOperation;
import com.database.pojo.UserTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.utils.Constants;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/12
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private Button mLogin_btn;
    private EditText mLogin_user_et;
    private EditText mLogin_password_et;
    private UserTable mUserTable;
    private ProgressBar mLogin_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLogin_btn = (Button) findViewById(R.id.login_btn);
        mLogin_progress = (ProgressBar) findViewById(R.id.login_progress);
        mLogin_user_et = (EditText) findViewById(R.id.login_user_et);
        mLogin_password_et = (EditText) findViewById(R.id.login_password_et);
        mLogin_btn.setOnClickListener(this);
//        //隐藏输入法
//        ViewUtils.hideInputMethod(this,mLogin_password_et);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                new MyTask().execute();
                break;
        }
    }


    /**
     * @param et 输入信息
     * @return 用户名
     */
    public String getUserName(EditText et) {
        return et.getText().toString();
    }

    /**
     * @param et 输入信息
     * @return 用户密码
     */
    public String getUserPassword(EditText et) {
        return et.getText().toString();
    }

    public boolean isLogin(EditText name_et, EditText password_et) {
        //noinspection unchecked
        ArrayList<UserTable> userTables = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.FIELD_USERNAME, getUserName(name_et));
        if (userTables != null && userTables.size() != 0) {
            mUserTable = userTables.get(0);
            String password = mUserTable.getField(UserTable.FIELD_PASSWORD);
            return password.equals(getUserPassword(password_et));
        }
        return false;
    }

        class MyTask extends AsyncTask<String, Integer, Integer> {
            @Override
            protected void onPreExecute() {
                mLogin_progress.setVisibility(View.VISIBLE);
            }

            @Override
            protected Integer doInBackground(String... params) {

                if (mLogin_user_et != null && mLogin_password_et != null) {
                    if (isLogin(mLogin_user_et, mLogin_password_et)) {
                        //标记自动登录,以用户contentId作为标记
                        SharedPreferences.Editor editor = MyApplication.sSharedPreferences.edit();
                        editor.putString(Constants.AUTO_LOGIN, mUserTable.getContentId());
                        editor.putString(Constants.USER_REAL_NAME,mUserTable.getField(UserTable.FIELD_REALNAME));
                        ArrayList<String> avatars = (ArrayList<String>) mUserTable.getAccessaryFileUrlList();
                        if (avatars!=null&&avatars.size()!=0){
                            editor.putString(Constants.AVATAR_URL,avatars.get(0));
                        }else {
                            editor.putString(Constants.AVATAR_URL,null);
                        }
                        editor.apply();

                        return 1; //登录成功
                    } else {
                        return 2;//用户或者密码错误
                    }
                } else {
                    return 3;//用户名或者密码为空
                }

            }

            @Override
            protected void onPostExecute(Integer integer) {
                mLogin_progress.setVisibility(View.GONE);
                if (integer == 1) {
                    Intent loginIntent = new Intent();
                    loginIntent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                    LoginActivity.this.finish();
                } else if (integer == 2) {
                    Toast.makeText(LoginActivity.this, "用户名或者密码错误", Toast.LENGTH_SHORT).show();
                } else if (integer == 3) {
                    Toast.makeText(LoginActivity.this, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        }

    private void saveAvatarUrl() {
        String contentId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN,null);
        ArrayList<?> userTables = DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, contentId);
        if (userTables!=null&&userTables.size()!=0) {
            UserTable userTable = (UserTable) userTables.get(0);
            ArrayList<String> imageUrls = (ArrayList<String>) userTable.getAccessaryFileUrlList();
            if (imageUrls!=null&&imageUrls.size()!=0){
                String imageUrl = imageUrls.get(0);
                SharedPreferences.Editor editor = MyApplication.sSharedPreferences.edit();
                editor.putString(Constants.AVATAR_URL,imageUrl);
                editor.apply();
            }
        }
    }

    @Override
        protected void onPause () {
            super.onPause();

        }
    }
