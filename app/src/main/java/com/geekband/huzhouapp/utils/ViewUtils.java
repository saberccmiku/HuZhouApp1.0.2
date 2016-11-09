package com.geekband.huzhouapp.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.geekband.huzhouapp.application.MyApplication;

/**
 * Created by Administrator on 2016/6/20.
 */
public class ViewUtils {
    /**
     * spinner获取焦点时隐藏hint
     * @return
     */
    public static View.OnFocusChangeListener getFocusChangeListener() {
        View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                String hint;
                if (hasFocus) {
                    hint = editText.getHint().toString();
                    editText.setTag(hint);
                    editText.setHint(null);
                } else {
                    hint = editText.getTag().toString();
                    editText.setHint(hint);
                }
            }
        };
        return mFocusChangeListener;
    }

     //隐藏输入法
        public static void hideInputMethod(Context context,View view) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
}
