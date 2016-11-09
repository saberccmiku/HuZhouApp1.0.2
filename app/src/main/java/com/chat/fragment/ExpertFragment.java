package com.chat.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.activity.ChatActivity;
import com.chat.activity.ExpertActivity;
import com.chat.adapter.pojo.Expert;
import com.chat.adapter.pojo.Message;
import com.database.dto.DataOperation;
import com.database.pojo.CategoriesTable;
import com.database.pojo.ExpertTable;
import com.database.pojo.ReplyTable;
import com.database.pojo.UserInfoTable;
import com.database.pojo.UserTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.custom.PagingLoadListView;
import com.geekband.huzhouapp.custom.RefreshButton;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.DataOperationHelper;
import com.lidroid.xutils.BitmapUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpertFragment extends Fragment {
    private ExpertActivity parentActivity;
    private View v_rootView;
    private PagingLoadListView lv_expertListView;
    private ViewGroup vg_progress;
    private ViewGroup vg_errorTip;
    private ViewGroup vg_emptyTip;
    private RefreshButton btn_refresh;

    private ExpertListAdapter expertListAdapter;
    private CategoriesTable expertCategory;
    private boolean isInit;
    private Bundle fragmentStateValue;
    private int currentPage = 1;
    private int pageSize = 6;

    public ExpertFragment(CategoriesTable expertCategory) {
        this.expertCategory = expertCategory;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isInit = true; //判断是否是第一次创建该Fragment
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        parentActivity = (ExpertActivity) activity;
        fragmentStateValue = parentActivity.getBundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v_rootView = inflater.inflate(R.layout.fragment_expert, container, false);

        findView();
        initVar();
        initView();
        initListener();

        //是否(自动刷新)初始化数据
        if (isInit) //只有当是第一次创建当前Fragment时；才需要自动刷新一次
        {
            btn_refresh.beginRefresh(); //实现vg_refresh的刷新方法后，在这里刷新数据
            isInit = false;
        }
        //下次再执行到这里的生命周期时，不是再从服务器上获取数据，而是从状态值中获取数据来恢复自己
        //因为此时数据已经获取到并且保存到状态值中了，所以无需再自动从服务器上读取
        else {
            //读取状态值，恢复自身状态
            if (fragmentStateValue.getSerializable(String.valueOf(ExpertFragment.this.hashCode())) != null) {
                lv_expertListView.setVisibility(View.VISIBLE);
                expertListAdapter.getExpertList().addAll(((SavedState) (fragmentStateValue.getSerializable(String.valueOf(ExpertFragment.this.hashCode())))).expertList);
                expertListAdapter.notifyDataSetChanged();
            }
        }

        return v_rootView;
    }

    private void findView() {
        lv_expertListView = (PagingLoadListView) v_rootView.findViewById(R.id.lv_expert_expertlist);
        vg_progress = (ViewGroup) v_rootView.findViewById(R.id.vg_expert_progress);
        vg_errorTip = (ViewGroup) v_rootView.findViewById(R.id.vg_expert_errorTip);
        vg_emptyTip = (ViewGroup) v_rootView.findViewById(R.id.vg_expert_emptyTip);
        btn_refresh = (RefreshButton) v_rootView.findViewById(R.id.vg_expert_refresh);
    }

    private void initView() {
        lv_expertListView.setAdapter(expertListAdapter);

        SavedState savedState = (SavedState) fragmentStateValue.getSerializable(String.valueOf(ExpertFragment.this.hashCode()));
        if (savedState != null) {
            lv_expertListView.setVisibility(savedState.visibility_lv_expertListView);
            vg_progress.setVisibility(savedState.visibility_vg_progress);
            vg_errorTip.setVisibility(savedState.visibility_vg_errorTip);
            vg_emptyTip.setVisibility(savedState.visibility_vg_emptyTip);
        } else {
            lv_expertListView.setVisibility(View.GONE);
            vg_progress.setVisibility(View.GONE);
            vg_errorTip.setVisibility(View.GONE);
            vg_emptyTip.setVisibility(View.GONE);
        }
    }

    private void initVar() {
        expertListAdapter = new ExpertListAdapter(getActivity(), lv_expertListView);
    }

    private void initListener() {
        lv_expertListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "查看详情", Toast.LENGTH_SHORT).show();
            }
        });

        lv_expertListView.setOnLoadListener(new PagingLoadListView.OnLoadListener() {
            @Override
            public void onLoad() {
                currentPage++;
                runAsyncTask(AsyncDataLoader.TASK_ADDITEM);
            }
        });

        btn_refresh.setRefreshListener(new RefreshButton.RefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                runAsyncTask(AsyncDataLoader.TASK_INITLISTVIEW);
            }
        });
    }

    private void runAsyncTask(int task, Object... params) {
        new AsyncDataLoader(task, params).execute();
    }

    private class SavedState implements Serializable {
        public ArrayList<Expert> expertList;
        public int visibility_lv_expertListView;
        public int visibility_vg_progress;
        public int visibility_vg_errorTip;
        public int visibility_vg_emptyTip;
    }

    private static class ExpertListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Expert> expertList;
        private BitmapUtils bitmapUtils;

        public ExpertListAdapter(Context context, ListView listView) {
            this.context = context;
            expertList = new ArrayList<>();
            bitmapUtils = BitmapHelper.getBitmapUtils(context, listView, R.drawable.head_default, R.drawable.head_default);
        }

        private void runAsyncTask(int task, Object... params) {
            new AsyncDataLoader(task, params).execute();
        }

        public ArrayList<Expert> getExpertList() {
            return this.expertList;
        }

        @Override
        public int getCount() {
            return expertList.size();
        }

        @Override
        public Expert getItem(int position) {
            return expertList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_expert_expert, parent, false);
                vh.iv_head = (ImageView) convertView.findViewById(R.id.iv_expert_head);
                vh.tv_name = (TextView) convertView.findViewById(R.id.tv_expert_name);
                vh.tv_individualTitle = (TextView) convertView.findViewById(R.id.tv_expert_individualTitle);
                vh.tv_introduction = (TextView) convertView.findViewById(R.id.tv_expert_introduction);
                vh.tv_consult = (TextView) convertView.findViewById(R.id.tv_expert_consult);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            ExpertTable expert = getItem(position).getExpert();
            UserTable expertInfo = getItem(position).getExpertInfo();
            UserInfoTable expertUserInfo = getItem(position).getExpertUserInfo();

            String headIconUrl = "";
            if (expertInfo != null && expertInfo.getAccessaryFileUrlList() != null && expertInfo.getAccessaryFileUrlList().size() != 0) {
                headIconUrl = expertInfo.getAccessaryFileUrlList().get(0);
            }
            bitmapUtils.display(vh.iv_head, headIconUrl);

            if (expert != null) vh.tv_name.setText(expert.getField(ExpertTable.FIELD_REALNAME));
            //vh.tv_individualTitle.setText(""); //个人头衔，暂时为空
            if (expert != null)
                vh.tv_introduction.setText(expert.getField(ExpertTable.FIELD_INTRODUCTION));
            vh.tv_consult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runAsyncTask(ExpertFragment.ExpertListAdapter.AsyncDataLoader.TASK_OPENCHAT, position); //打开与所选专家的双人聊天界面
                }
            });

            return convertView;
        }

        private class ViewHolder {
            private ImageView iv_head;
            private TextView tv_name;
            private TextView tv_sex;
            private TextView tv_age;
            private TextView tv_address;
            private TextView tv_individualTitle;
            private TextView tv_lastLoginTime;
            private TextView tv_introduction;
            private TextView tv_consult;
        }

        private class AsyncDataLoader extends AsyncTask<Object, Integer, Integer> {
            public static final int TASK_OPENCHAT = 1;
            public static final int TASK_OPENCHAT_RESULT_SUCCESS = 1;
            public static final int TASK_OPENCHAT_RESULT_ERROR_1 = -1;
            public static final int TASK_OPENCHAT_RESULT_ERROR_2 = -2;

            private int task;
            private Object[] params;

            public AsyncDataLoader(int task, Object... params) {
                this.task = task;
                this.params = params;
            }

            @Override
            protected void onPreExecute() {
                switch (task) {
                    case TASK_OPENCHAT: {

                    }
                    break;
                }
            }

            @Override
            protected Integer doInBackground(Object... params) {
                switch (task) {
                    case TASK_OPENCHAT: {
                        try {
                            //点击的item的position
                            int position = (Integer) this.params[0];

                            //对方(专家)
                            UserTable expertUserData = getItem(position).getExpertInfo();
                            //自己
                            UserTable currentUserData = (UserTable) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, "")).get(0);

                            //若当前咨询的专家是自己，则不能自己向自己发起聊天
                            if (expertUserData.equals(currentUserData)) {
                                return TASK_OPENCHAT_RESULT_ERROR_2;
                            }

                            //聊天双方的首条消息
                            Message firstMessage = null;
                            HashMap<String, String> fieldList = new HashMap<>();
                            fieldList.put(ReplyTable.FIELD_REPLYTONO, expertUserData.getContentId());
                            fieldList.put(ReplyTable.FIELD_USERNO, currentUserData.getContentId());
                            List<ReplyTable> reply = (List<ReplyTable>) DataOperation.queryTable(ReplyTable.TABLE_NAME, fieldList);
                            if (reply != null && reply.size() != 0) {
                                ReplyTable firstReply = reply.get(0);
                                firstMessage = new Message(currentUserData, firstReply, Message.TYPE_SEND_TEXT);
                            } else {
                                fieldList = new HashMap<>();
                                fieldList.put(ReplyTable.FIELD_REPLYTONO, currentUserData.getContentId());
                                fieldList.put(ReplyTable.FIELD_USERNO, expertUserData.getContentId());
                                reply = (List<ReplyTable>) DataOperation.queryTable(ReplyTable.TABLE_NAME, fieldList);
                                if (reply != null && reply.size() != 0) {
                                    ReplyTable firstReply = reply.get(0);
                                    firstMessage = new Message(expertUserData, firstReply, Message.TYPE_RECEIVE_TEXT);
                                }
                            }

                            //参与聊天的用户列表
                            List<UserTable> userList = new ArrayList<>();
                            userList.add(expertUserData);
                            userList.add(currentUserData);

                            //打开聊天界面
                            Intent intent = new Intent();
                            intent.setClass(context, ChatActivity.class);
                            if (firstMessage != null)
                                intent.putExtra(ChatActivity.ARGS_FIRSTMESSAGE, firstMessage);
                            else
                                intent.putExtra(ChatActivity.ARGS_REPLYNO, expertUserData.getContentId());
                            intent.putExtra(ChatActivity.ARGS_USERLIST, (ArrayList<UserTable>) userList);
                            intent.putExtra(ChatActivity.ARGS_TITLE, "与" + expertUserData.getField(UserTable.FIELD_REALNAME) + "的对话");
                            intent.putExtra(ChatActivity.ARGS_CHATMODE, ChatActivity.CHATMODE_SINGLE);
                            context.startActivity(intent);

                            return TASK_OPENCHAT_RESULT_SUCCESS;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return TASK_OPENCHAT_RESULT_ERROR_1;
                        }
                    }
                }

                return 0;
            }

            @Override
            protected void onPostExecute(Integer taskResult) {
                switch (taskResult) {
                    case TASK_OPENCHAT_RESULT_SUCCESS: {

                    }
                    break;


                    case TASK_OPENCHAT_RESULT_ERROR_1: {

                    }
                    break;

                    case TASK_OPENCHAT_RESULT_ERROR_2: {
                        Toast.makeText(context, "您不能自己咨询自己哦", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }
    }

    private class AsyncDataLoader extends AsyncTask<Object, Integer, Integer> {
        public static final int TASK_INITLISTVIEW = 1;
        public static final int TASK_INITLISTVIEW_RESULT_SUCCESS = 1;
        public static final int TASK_INITLISTVIEW_RESULT_ERROR = -1;
        public static final int TASK_ADDITEM = 2;
        public static final int TASK_ADDITEM_RESULT_SUCCESS = 2;
        public static final int TASK_ADDITEM_RESULT_ERROR = -2;

        private int task;
        private Object[] params;

        public AsyncDataLoader(int task, Object... params) {
            this.task = task;
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            switch (task) {
                case TASK_INITLISTVIEW: {
                    if (lv_expertListView.getVisibility() == View.GONE)
                        vg_progress.setVisibility(View.VISIBLE);
                    vg_errorTip.setVisibility(View.GONE);
                    vg_emptyTip.setVisibility(View.GONE);
                }
                break;

                case TASK_ADDITEM: {

                }
                break;
            }
        }

        ArrayList<Expert> expertList = new ArrayList<>();

        @Override
        protected Integer doInBackground(Object... params) {
            switch (task) {
                case TASK_INITLISTVIEW: {
                    try {
                        if (expertCategory.getContentId().equals("")) {
                            expertList = DataOperationHelper.queryExpertList(currentPage, pageSize);
                        } else {
                            expertList = DataOperationHelper.queryExpertList(expertCategory.getContentId(), 0, pageSize);
                        }


                        return TASK_INITLISTVIEW_RESULT_SUCCESS;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return TASK_INITLISTVIEW_RESULT_ERROR;
                }

                case TASK_ADDITEM: {
                    try {
                        if (expertCategory.getContentId().equals("")) {
                            expertList = DataOperationHelper.queryExpertList(currentPage, pageSize);
                        } else {
                            expertList = DataOperationHelper.queryExpertList(expertCategory.getContentId(), currentPage, pageSize);
                        }

                        return TASK_ADDITEM_RESULT_SUCCESS;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return TASK_ADDITEM_RESULT_ERROR;
                }
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer taskResult) {
            switch (taskResult) {
                case TASK_INITLISTVIEW_RESULT_SUCCESS: {
                    expertListAdapter.getExpertList().clear();
                    expertListAdapter.getExpertList().addAll(expertList);
                    expertList.clear();
                    expertListAdapter.notifyDataSetChanged();
                    btn_refresh.refreshComplete();

                    if (expertListAdapter.getExpertList().size() != 0) {
                        lv_expertListView.setVisibility(View.VISIBLE);
                        vg_progress.setVisibility(View.GONE);
                        vg_errorTip.setVisibility(View.GONE);
                        vg_emptyTip.setVisibility(View.GONE);
                    } else {
                        lv_expertListView.setVisibility(View.GONE);
                        vg_progress.setVisibility(View.GONE);
                        vg_errorTip.setVisibility(View.GONE);
                        vg_emptyTip.setVisibility(View.VISIBLE);
                    }


                    com.chat.fragment.ExpertFragment.SavedState savedState = new com.chat.fragment.ExpertFragment.SavedState();
                    savedState.expertList = expertListAdapter.getExpertList();
                    savedState.visibility_lv_expertListView = lv_expertListView.getVisibility();
                    savedState.visibility_vg_progress = vg_progress.getVisibility();
                    savedState.visibility_vg_errorTip = vg_errorTip.getVisibility();
                    savedState.visibility_vg_emptyTip = vg_emptyTip.getVisibility();
                    //将Fragment中的状态数据保存到其父Activity中
                    //然后可以在Fragment的生命周期中从其父Activity中获取数据恢复自己的状态
                    //这里使用hashCode作为每个Fragment的标识符，因为hashCode对每个Fragment都是唯一且不可变的
                    fragmentStateValue.putSerializable(String.valueOf(ExpertFragment.this.hashCode()), savedState);
                }
                break;

                case TASK_INITLISTVIEW_RESULT_ERROR: {
                    btn_refresh.refreshComplete();

                    if (expertListAdapter.getExpertList().size() == 0) {
                        lv_expertListView.setVisibility(View.GONE);
                        vg_errorTip.setVisibility(View.VISIBLE);
                    } else {
                        lv_expertListView.setVisibility(View.VISIBLE);
                        vg_errorTip.setVisibility(View.GONE);
                    }
                    vg_progress.setVisibility(View.GONE);
                }
                break;

                case TASK_ADDITEM_RESULT_SUCCESS: {
                    expertListAdapter.getExpertList().addAll(expertList);
                    //expertListAdapter.sort();
                    expertListAdapter.notifyDataSetChanged();
                    expertList.clear();
                    lv_expertListView.loadComplete();
                }
                break;

                case TASK_ADDITEM_RESULT_ERROR: {

                }
                break;
            }
        }
    }
}
