package com.geekband.huzhouapp.chat.fragment;

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

import com.geekband.huzhouapp.chat.activity.OthersQuestionActivity;
import com.geekband.huzhouapp.chat.activity.QuestionDetailActivity;
import com.geekband.huzhouapp.chat.adapter.pojo.Question;
import com.geekband.huzhouapp.utils.DataOperation;
import com.geekband.huzhouapp.vo.pojo.CategoriesTable;
import com.geekband.huzhouapp.vo.pojo.EnquiryTable;
import com.geekband.huzhouapp.vo.pojo.ReplyTable;
import com.geekband.huzhouapp.vo.pojo.UserTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.custom.PagingLoadListView;
import com.geekband.huzhouapp.custom.RefreshButton;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.geekband.huzhouapp.utils.DataOperationHelper;
import com.lidroid.xutils.BitmapUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OthersQuestionFragment extends Fragment {
    private View v_rootView;
    private PagingLoadListView lv_questionListView;
    private ViewGroup vg_progress;
    private ViewGroup vg_errorTip;
    private ViewGroup vg_emptyTip;
    private RefreshButton btn_refresh;

    private OthersQuestionActivity parentActivity;
    private OthersQuestionListAdapter othersQuestionListAdapter;
    private CategoriesTable questionCategory;
    //用于标识Fragment是从状态值中获取数据还是从网络上请求数据
    private boolean isInit;
    //用于Fragment恢复状态值
    private Bundle fragmentStateValue;
    //用于Fragment生命周期结束时终止正在进行的后台任务
    private HashMap<Integer, AsyncDataLoader> asyncTasks;
    //用于分页加载
    private int currentPage = 1;
    private int pageSize = 10;


    public OthersQuestionFragment(CategoriesTable questionCategory) {
        this.questionCategory = questionCategory;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isInit = true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        parentActivity = (OthersQuestionActivity) activity;
        fragmentStateValue = parentActivity.getBundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v_rootView = inflater.inflate(R.layout.fragment_othersquestion, container, false);

        findView();
        initVar();
        initView();
        initListener();

        if (isInit) {
            btn_refresh.beginRefresh();
            isInit = false;
        } else {
            if (fragmentStateValue.getSerializable(String.valueOf(OthersQuestionFragment.this.hashCode())) != null) {
                lv_questionListView.setVisibility(View.VISIBLE);
                othersQuestionListAdapter.getQuestionList().addAll(((SavedState) (fragmentStateValue.getSerializable(String.valueOf(OthersQuestionFragment.this.hashCode())))).questionList);
                othersQuestionListAdapter.notifyDataSetChanged();
            }
        }

        return v_rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /**
         * 终止所有正在执行的后台Task
         */
        for (Integer key : asyncTasks.keySet()) {
            asyncTasks.get(key).cancel(true);
        }
    }

    private void findView() {
        lv_questionListView = (PagingLoadListView) v_rootView.findViewById(R.id.lv_othersquestion_questionList);
        vg_progress = (ViewGroup) v_rootView.findViewById(R.id.vg_othersquestion_progress);
        vg_errorTip = (ViewGroup) v_rootView.findViewById(R.id.vg_othersquestion_errorTip);
        vg_emptyTip = (ViewGroup) v_rootView.findViewById(R.id.vg_othersquestion_emptyTip);
        btn_refresh = (RefreshButton) v_rootView.findViewById(R.id.vg_othersquestion_refresh);
    }

    private void initView() {
        lv_questionListView.setAdapter(othersQuestionListAdapter);

        SavedState savedState = (SavedState) fragmentStateValue.getSerializable(String.valueOf(OthersQuestionFragment.this.hashCode()));
        if (savedState != null) {
            lv_questionListView.setVisibility(savedState.visibility_lv_questionListView);
            vg_progress.setVisibility(savedState.visibility_vg_progress);
            vg_errorTip.setVisibility(savedState.visibility_vg_errorTip);
            vg_emptyTip.setVisibility(savedState.visibility_vg_emptyTip);
        } else {
            lv_questionListView.setVisibility(View.GONE);
            vg_progress.setVisibility(View.GONE);
            vg_errorTip.setVisibility(View.GONE);
            vg_emptyTip.setVisibility(View.GONE);
        }
    }

    private void initVar() {
        othersQuestionListAdapter = new OthersQuestionListAdapter(getActivity(), lv_questionListView);
        asyncTasks = new HashMap<>();
    }

    private void initListener() {
        lv_questionListView.setOnLoadListener(new PagingLoadListView.OnLoadListener() {
            @Override
            public void onLoad() {
                runAsyncTask(AsyncDataLoader.TASK_ADDITEM);
            }
        });

        lv_questionListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = lv_questionListView.getHeaderViewsCount();
                int itemViewsCount = othersQuestionListAdapter.getCount();
                if (position > headerViewsCount - 1 && position < headerViewsCount + itemViewsCount) {
                    position = position + headerViewsCount;
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), QuestionDetailActivity.class);
                    intent.putExtra(QuestionDetailActivity.ARGS_QUESTION, othersQuestionListAdapter.getItem(position));
                    startActivity(intent);
                }
            }
        });

        btn_refresh.setRefreshListener(new RefreshButton.RefreshListener() {
            @Override
            public void onRefresh() {
                runAsyncTask(AsyncDataLoader.TASK_INITLISTVIEW);
            }
        });
    }

    private void runAsyncTask(int task, Object... params) {
        AsyncDataLoader asyncTask = new AsyncDataLoader(task, params);
        asyncTask.execute();

        //将所有正在运行中的后台Task记录下来
        asyncTasks.put(asyncTask.hashCode(), asyncTask);
        for (Object object : asyncTasks.keySet().toArray()) {
            if (asyncTasks.get(object).isEnd()) asyncTasks.remove(object); //移除已经不处于运行状态中的后台Task
        }
    }

    private class SavedState implements Serializable {
        private ArrayList<Question> questionList;
        public int visibility_lv_questionListView;
        public int visibility_vg_progress;
        public int visibility_vg_errorTip;
        public int visibility_vg_emptyTip;
    }

    private static class OthersQuestionListAdapter extends BaseAdapter {
        private Context context;
        private BitmapUtils bitmapUtils;
        private ArrayList<Question> questionList;

        public OthersQuestionListAdapter(Context context, ListView listView) {
            this.context = context;
            questionList = new ArrayList<>();
            bitmapUtils = BitmapHelper.getBitmapUtils(context, listView, R.drawable.head_default, R.drawable.head_default);
        }

        public ArrayList<Question> getQuestionList() {
            return questionList;
        }

        /**
         * 对结果数据源进行排序(按照问题的发布时间早晚，发布时间越新的问题越靠前)
         */
        public void sort() {
            for (int j = 0; j < questionList.size() - 1; j++) {
                for (int i = questionList.size() - 1; i > j; i--) {
                    if (questionList.get(i).compareTo(questionList.get(i - 1)) == 1) {
                        Question temp = questionList.get(i);
                        questionList.set(i, questionList.get(i - 1));
                        questionList.set(i - 1, temp);
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return questionList.size();
        }

        @Override
        public Question getItem(int position) {
            return questionList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_othersquestion_question, parent, false);
                vh.iv_askerIcon = (ImageView) convertView.findViewById(R.id.iv_othersquestion_askerIcon);
                vh.tv_askerName = (TextView) convertView.findViewById(R.id.tv_othersquestion_askerName);
                vh.tv_askContent = (TextView) convertView.findViewById(R.id.tv_othersquestion_askContent);
                vh.tv_replierName = (TextView) convertView.findViewById(R.id.tv_othersquestion_replierName);
                vh.tv_answerContent = (TextView) convertView.findViewById(R.id.tv_othersquestion_answerContent);
                vh.tv_replierNum = (TextView) convertView.findViewById(R.id.tv_othersquestion_replierNum);
                vh.vg_answer = (ViewGroup) convertView.findViewById(R.id.vg_othersquestion_answer);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            String headIconUrl = "";
            if (getItem(position).getAskerInfo().getAccessaryFileUrlList() != null && getItem(position).getAskerInfo().getAccessaryFileUrlList().size() != 0) {
                headIconUrl = getItem(position).getAskerInfo().getAccessaryFileUrlList().get(0);
            }
            bitmapUtils.display(vh.iv_askerIcon, headIconUrl);

            vh.tv_askerName.setText(getItem(position).getAskerInfo().getField(UserTable.FIELD_REALNAME));
            vh.tv_askContent.setText(getItem(position).getAskInfo().getField(EnquiryTable.FIELD_CONTENT));
            vh.tv_replierNum.setText("" + getItem(position).getAnswerInfoList().size());
            if (getItem(position).getAnswerInfoList().size() != 0) {
                vh.vg_answer.setVisibility(View.VISIBLE);
                vh.tv_replierName.setText(getItem(position).getAnswerInfoList().get(0).getReplierInfo().getField(UserTable.FIELD_REALNAME));
                vh.tv_answerContent.setText(getItem(position).getAnswerInfoList().get(0).getAnswerInfo().getField(ReplyTable.FIELD_CONTENT));
            } else {
                vh.vg_answer.setVisibility(View.GONE);
            }

            return convertView;
        }

        private class ViewHolder {
            private ImageView iv_askerIcon;
            private TextView tv_askerName;
            private TextView tv_askContent;
            private TextView tv_replierNum;
            private TextView tv_replierName;
            private TextView tv_answerContent;
            private ViewGroup vg_answer;
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
        private boolean isEnd;

        public AsyncDataLoader(int task, Object... params) {
            this.task = task;
            this.params = params;
        }

        public boolean isEnd() {
            return isEnd;
        }

        @Override
        protected void onPreExecute() {
            isEnd = false;

            switch (task) {
                case TASK_INITLISTVIEW: {
                    if (lv_questionListView.getVisibility() == View.GONE)
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

        ArrayList<Question> questionList = new ArrayList<>();

        @Override
        protected Integer doInBackground(Object... params) {
            switch (task) {
                case TASK_INITLISTVIEW: {
                    try {
                        List<EnquiryTable> askList = null;

                        /*if (task == TASK_INITLISTVIEW)
                        {
                            if(questionCategory.getContentId().equals("")) askList = (List<EnquiryTable>) DataOperation.queryTable(EnquiryTable.TABLE_NAME);
                            else askList = (List<EnquiryTable>) DataOperation.queryTable(EnquiryTable.TABLE_NAME, EnquiryTable.FIELD_CATEGORYID, questionCategory.getContentId());
                            loadStartIndex = 0 + initLoadItemCount;

                            T.l("当前问题分类信息"+questionCategory+"当前问题分类 问题列表"+askList);
                        } else if (task == TASK_ADDITEM)
                        {
                            if(questionCategory.getContentId().equals("")) askList = (List<EnquiryTable>) DataOperation.queryTable(EnquiryTable.TABLE_NAME);
                            else askList = (List<EnquiryTable>) DataOperation.queryTable(EnquiryTable.TABLE_NAME, EnquiryTable.FIELD_CATEGORYID, questionCategory.getContentId());
                            loadStartIndex += loadNewItemCount;
                        }*/

                        /*if (task == TASK_INITLISTVIEW)
                        {
                            askList = (List<EnquiryTable>) DataOperation.queryTable(EnquiryTable.TABLE_NAME, 0, initLoadItemCount);
                            loadStartIndex = 0 + initLoadItemCount;
                        } else if (task == TASK_ADDITEM)
                        {
                            askList = (List<EnquiryTable>) DataOperation.queryTable(EnquiryTable.TABLE_NAME, loadStartIndex, loadNewItemCount);
                            loadStartIndex += loadNewItemCount;
                        }*/

                        if (questionCategory.getContentId().equals("")) {
                            askList = (List<EnquiryTable>) DataOperation.queryTable(EnquiryTable.TABLE_NAME, currentPage, pageSize);
                        } else {
                            Map<String, String> map = new HashMap<>();
                            map.put(EnquiryTable.FIELD_CATEGORYID, questionCategory.getContentId());
                            askList = (List<EnquiryTable>) DataOperation.queryTable(EnquiryTable.TABLE_NAME, currentPage, pageSize, map);
                        }
                        currentPage++;
                        if (askList != null) {
                            for (EnquiryTable ask : askList) //每一条问题
                            {
                                ArrayList<UserTable> list = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, ask.getField(EnquiryTable.FIELD_USERNO));
                                if (list != null && list.size() != 0)
                                    questionList.add(new Question(list.get(0), ask, DataOperationHelper.queryQuestionAnswerList(ask.getContentId())));
                            }
                        }

                        return TASK_INITLISTVIEW_RESULT_SUCCESS;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return TASK_INITLISTVIEW_RESULT_ERROR;
                }


                case TASK_ADDITEM: {
                    try {
                        List<EnquiryTable> askList = null;

                        if (questionCategory.getContentId().equals("")) {
                            askList = (List<EnquiryTable>) DataOperation.queryTable(EnquiryTable.TABLE_NAME, currentPage, pageSize);
                        } else {
                            Map<String, String> map = new HashMap<>();
                            map.put(EnquiryTable.FIELD_CATEGORYID, questionCategory.getContentId());
                            askList = (List<EnquiryTable>) DataOperation.queryTable(EnquiryTable.TABLE_NAME, currentPage, pageSize, map);

                        }
                        currentPage++;
                        if (askList != null) {
                            for (EnquiryTable ask : askList) //每一条问题
                            {
                                ArrayList<UserTable> list = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, ask.getField(EnquiryTable.FIELD_USERNO));
                                if (list != null && list.size() != 0)
                                    questionList.add(new Question(list.get(0), ask, DataOperationHelper.queryQuestionAnswerList(ask.getContentId())));
                            }
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
                    othersQuestionListAdapter.getQuestionList().clear();
                    othersQuestionListAdapter.getQuestionList().addAll(questionList);
                    othersQuestionListAdapter.sort();
                    othersQuestionListAdapter.notifyDataSetChanged();
                    questionList.clear();
                    btn_refresh.refreshComplete();

                    if (othersQuestionListAdapter.getQuestionList().size() != 0) {
                        lv_questionListView.setVisibility(View.VISIBLE);
                        vg_progress.setVisibility(View.GONE);
                        vg_errorTip.setVisibility(View.GONE);
                        vg_emptyTip.setVisibility(View.GONE);
                    } else {
                        lv_questionListView.setVisibility(View.GONE);
                        vg_progress.setVisibility(View.GONE);
                        vg_errorTip.setVisibility(View.GONE);
                        vg_emptyTip.setVisibility(View.VISIBLE);
                    }


                    SavedState savedState = new SavedState();
                    savedState.questionList = othersQuestionListAdapter.getQuestionList();
                    savedState.visibility_lv_questionListView = lv_questionListView.getVisibility();
                    savedState.visibility_vg_progress = vg_progress.getVisibility();
                    savedState.visibility_vg_errorTip = vg_errorTip.getVisibility();
                    savedState.visibility_vg_emptyTip = vg_emptyTip.getVisibility();
                    fragmentStateValue.putSerializable(String.valueOf(OthersQuestionFragment.this.hashCode()), savedState);
                }
                break;

                case TASK_INITLISTVIEW_RESULT_ERROR: {
                    btn_refresh.refreshComplete();

                    if (othersQuestionListAdapter.getQuestionList().size() == 0) {
                        lv_questionListView.setVisibility(View.GONE);
                        vg_errorTip.setVisibility(View.VISIBLE);
                    } else {
                        lv_questionListView.setVisibility(View.VISIBLE);
                        vg_errorTip.setVisibility(View.GONE);
                    }
                    vg_progress.setVisibility(View.GONE);
                }
                break;

                case TASK_ADDITEM_RESULT_SUCCESS: {
                    othersQuestionListAdapter.getQuestionList().addAll(questionList);
                    othersQuestionListAdapter.sort();
                    othersQuestionListAdapter.notifyDataSetChanged();
                    questionList.clear();
                    lv_questionListView.loadComplete();
                }
                break;

                case TASK_ADDITEM_RESULT_ERROR: {

                }
                break;
            }

            isEnd = true;
        }
    }
}
