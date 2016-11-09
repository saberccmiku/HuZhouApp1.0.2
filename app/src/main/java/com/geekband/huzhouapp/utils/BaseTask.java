package com.geekband.huzhouapp.utils;

import android.os.AsyncTask;

/**
 * Created by Administrator on 2016/6/13
 */
public  class BaseTask  extends AsyncTask<String ,Integer,Integer>{

    private TaskListener mTaskListener;

    public BaseTask(TaskListener taskListener) {
        mTaskListener = taskListener;
    }

        @Override
        protected void onPreExecute() {
            mTaskListener.preTask();
        }

        @Override
       protected Integer doInBackground(String... params) {
            mTaskListener.doInTask();
           return null;
       }

        @Override
        protected void onPostExecute(Integer integer) {
            mTaskListener.postTask();
        }


    interface TaskListener {
         void preTask();
         void postTask();
         void doInTask();
    }
}
