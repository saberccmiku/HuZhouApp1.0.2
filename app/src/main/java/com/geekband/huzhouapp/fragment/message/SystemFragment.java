package com.geekband.huzhouapp.fragment.message;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.geekband.huzhouapp.chat.activity.ChatActivity;
import com.geekband.huzhouapp.chat.adapter.NotificationListAdapter;
import com.geekband.huzhouapp.chat.adapter.pojo.Message;
import com.geekband.huzhouapp.utils.DataOperation;
import com.geekband.huzhouapp.vo.pojo.EnquiryTable;
import com.geekband.huzhouapp.vo.pojo.ReplyTable;
import com.geekband.huzhouapp.vo.pojo.UserTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.utils.Constants;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/12
 */
public class SystemFragment extends Fragment {

    private View v_rootView;
    private ListView lv_notificationListView;

    private NotificationListAdapter notificationListAdapter;

    public static SystemFragment newInstance(){
        return new SystemFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        v_rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        findView();
        initVar();
        initView();
        initListener();

        return v_rootView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        runAsyncTask(AsyncDataLoader.TASK_INITLISTVIEW);
    }

    private void findView()
    {
        lv_notificationListView = (ListView) v_rootView.findViewById(R.id.lv_notification_notificationList);
    }

    private void initView()
    {
        lv_notificationListView.setAdapter(notificationListAdapter);
    }

    private void initVar()
    {
        notificationListAdapter = new NotificationListAdapter(getActivity(), lv_notificationListView);
    }

    private void initListener()
    {
        lv_notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                runAsyncTask(AsyncDataLoader.TASK_OPENNOTIFICATION ,position); //打开通知(打开通知消息对应的聊天页面)
            }
        });
    }

    private void runAsyncTask(int task, Object... params)
    {
        new AsyncDataLoader(task, params).execute();
    }

    private class AsyncDataLoader extends AsyncTask<Object, Integer, Integer>
    {
        private static final int TASK_INITLISTVIEW = 1;
        private static final int TASK_INITLISTVIEW_RESULT_SUCCESS = 1;
        private static final int TASK_INITLISTVIEW_RESULT_ERROR = -1;
        private static final int TASK_OPENNOTIFICATION = 2;
        private static final int TASK_OPENNOTIFICATION_RESULT_SUCCESS = 2;
        private static final int TASK_OPENNOTIFICATION_RESULT_ERROR = -2;

        private int task;
        private Object[] params;

        public AsyncDataLoader(int task, Object... params)
        {
            this.task = task;
            this.params = params;
        }

        @Override
        protected void onPreExecute()
        {
            switch (task)
            {
                case TASK_INITLISTVIEW:
                {
                    lv_notificationListView.setVisibility(View.GONE);
                }break;

                case TASK_OPENNOTIFICATION:
                {

                }break;
            }
        }

        @Override
        protected Integer doInBackground(Object... params)
        {
            switch (task)
            {
                case TASK_INITLISTVIEW:
                {
                    try
                    {
                        //当前用户
                        UserTable currentUser = (UserTable) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, "")).get(0);
                        //别的用户给当前用户发的私聊消息
                        ArrayList<ReplyTable> replyList1 = (ArrayList<ReplyTable>) DataOperation.queryTable(ReplyTable.TABLE_NAME, ReplyTable.FIELD_REPLYTONO, currentUser.getContentId());
                        //当前用户主动给别的用户发的消息(包括私聊、和对问题的回答)
                        ArrayList<ReplyTable> replyList2 = (ArrayList<ReplyTable>) DataOperation.queryTable(ReplyTable.TABLE_NAME, ReplyTable.FIELD_USERNO, currentUser.getContentId());
                        //当前用户的问题列表
                        ArrayList<EnquiryTable> currentUserQuestionList = (ArrayList<EnquiryTable>) DataOperation.queryTable(EnquiryTable.TABLE_NAME, EnquiryTable.FIELD_USERNO, currentUser.getContentId());

                        notificationListAdapter.getNotificationList().clear();
                        if(replyList1!=null)
                        {
                            for (ReplyTable replyTable : replyList1)
                            {
                                notificationListAdapter.getNotificationList().add(new Message(
                                        (UserTable) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, replyTable.getField(ReplyTable.FIELD_USERNO)).get(0),
                                        replyTable,
                                        Message.TYPE_RECEIVE_TEXT));
                            }
                        }

                        if(replyList2!=null)
                        {
                            for (ReplyTable replyTable : replyList2)
                            {
                                //判断该条回复的回复对象是否是 专家用户 或者 问题，如果回复对象不属于这些，那么就不列入消息列表(消息列表的每个item只存放一条聊天的消息头，而只有回复对象为专家用户、问题的的回复，才属于一条聊天的消息头)
                                ArrayList<UserTable> result1 = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, replyTable.getField(ReplyTable.FIELD_REPLYTONO));
                                ArrayList<EnquiryTable> result2 = (ArrayList<EnquiryTable>) DataOperation.queryTable(EnquiryTable.TABLE_NAME, EnquiryTable.CONTENTID, replyTable.getField(ReplyTable.FIELD_REPLYTONO));
                                if( (result1==null || (result1!=null && result1.size()==0)) &&
                                        (result2==null || (result2!=null && result2.size()==0)) )
                                    continue;

                                notificationListAdapter.getNotificationList().add(new Message(
                                        (UserTable) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, replyTable.getField(ReplyTable.FIELD_USERNO)).get(0),
                                        replyTable,
                                        Message.TYPE_SEND_TEXT));
                            }
                        }

                        if(currentUserQuestionList!=null)
                        {
                            for (EnquiryTable enquiryTable : currentUserQuestionList)
                            {
                                //别的用户主动给当前用户发的消息(对当前用户所提问题的回答)
                                ArrayList<ReplyTable> replyList3 = (ArrayList<ReplyTable>) DataOperation.queryTable(ReplyTable.TABLE_NAME, ReplyTable.FIELD_REPLYTONO, enquiryTable.getContentId());
                                for (ReplyTable replyTable : replyList3)
                                {
                                    notificationListAdapter.getNotificationList().add(new Message(
                                            (UserTable) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, replyTable.getField(ReplyTable.FIELD_USERNO)).get(0),
                                            replyTable,
                                            Message.TYPE_RECEIVE_TEXT));
                                }
                            }
                        }

                        return TASK_INITLISTVIEW_RESULT_SUCCESS;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        return TASK_INITLISTVIEW_RESULT_ERROR;
                    }
                }

                case TASK_OPENNOTIFICATION:
                {
                    try
                    {
                        int position = (int) this.params[0];
                        String currentUserContentId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, "");
                        Intent intent = new Intent(getActivity(), ChatActivity.class);

                        //该通知对应的聊天包含的用户列表
                        ArrayList<UserTable> userList = new ArrayList<>();
                        //添加聊天双方
                        //消息发送者对应的用户是一个
                        userList.add(notificationListAdapter.getItem(position).getMessageSenderInfo());
                        //消息发送对象对应的用户是一个
                        String replyObjectContentId = notificationListAdapter.getItem(position).getMessageInfo().getField(ReplyTable.FIELD_REPLYTONO);
                        ArrayList<UserTable> replyObjectUserList = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, replyObjectContentId);
                        if(replyObjectUserList!=null && replyObjectUserList.size()!=0) //如果回复对象是UserTable
                        {
                            userList.add(replyObjectUserList.get(0));

                            String title_userName = currentUserContentId.equals(replyObjectContentId)?notificationListAdapter.getItem(position).getMessageSenderInfo().getField(UserTable.FIELD_USERNAME):replyObjectUserList.get(0).getField(UserTable.FIELD_USERNAME);
                            intent.putExtra(ChatActivity.ARGS_TITLE, "与"+title_userName+"的对话");
                            intent.putExtra(ChatActivity.ARGS_CHATMODE, ChatActivity.CHATMODE_SINGLE);
                        }
                        else //如果回复对象是EnquiryTable
                        {
                            EnquiryTable enquiry = (EnquiryTable) DataOperation.queryTable(EnquiryTable.TABLE_NAME, EnquiryTable.CONTENTID, replyObjectContentId).get(0);
                            UserTable replyObjecUser = (UserTable) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, enquiry.getField(EnquiryTable.FIELD_USERNO)).get(0);
                            userList.add(replyObjecUser);

                            String title_userName = replyObjecUser.getField(UserTable.FIELD_USERNAME);
                            intent.putExtra(ChatActivity.ARGS_TITLE, title_userName+"的提问");
                            intent.putExtra(ChatActivity.ARGS_CHATMODE, ChatActivity.CHATMODE_QUESTION);
                        }

                        intent.putExtra(ChatActivity.ARGS_FIRSTMESSAGE, notificationListAdapter.getItem(position));
                        intent.putExtra(ChatActivity.ARGS_USERLIST, userList);
                        startActivity(intent);

                        return TASK_OPENNOTIFICATION_RESULT_SUCCESS;
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        return TASK_OPENNOTIFICATION_RESULT_ERROR;
                    }
                }
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer taskResult)
        {
            switch (taskResult)
            {
                case TASK_INITLISTVIEW_RESULT_SUCCESS:
                {
                    notificationListAdapter.notifyDataSetChanged();
                    lv_notificationListView.setVisibility(View.VISIBLE);
                }break;

                case TASK_INITLISTVIEW_RESULT_ERROR:
                {

                }break;

                case TASK_OPENNOTIFICATION_RESULT_SUCCESS:
                {

                }break;

                case TASK_OPENNOTIFICATION_RESULT_ERROR:
                {

                }break;
            }
        }
    }

}
