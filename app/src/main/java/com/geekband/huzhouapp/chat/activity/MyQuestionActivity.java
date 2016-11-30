package com.geekband.huzhouapp.chat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.geekband.huzhouapp.chat.adapter.pojo.Question;
import com.geekband.huzhouapp.vo.pojo.EnquiryTable;
import com.geekband.huzhouapp.vo.pojo.UserTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.custom.PagingLoadListView;
import com.geekband.huzhouapp.custom.RefreshButton;
import com.geekband.huzhouapp.utils.DataOperationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 当前用户的问题列表Activity
 * 
 */
public class MyQuestionActivity extends Activity implements View.OnClickListener
{
	private ImageButton btn_back;
	private PagingLoadListView lv_questionListView;
	private TextView tv_title;
	private ViewGroup vg_emptyTip;
	private ViewGroup vg_errorTip;
	private ViewGroup vg_progress;
	private RefreshButton btn_refresh;
	
	private QuestionListAdapter questionListAdapter;
	private int questionType; //根据问题类型加载不同的数据
	private final int initLoadItemCount = 10; //初始加载的item个数
	private final int loadNewItemCount = 10; //分页加载每次新加载的item个数
	private int loadStartIndex; //从服务器端获取数据时指定的起始下标(也就是分页加载每次更新时取loadStartIndex~loadNewItemCount之间的数据)
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_myquestion);
		findView();
		initVar();
		initView();
		initListener();
		
		btn_refresh.beginRefresh();
	}
	
	private void findView()
	{
		btn_back = (ImageButton) findViewById(R.id.btn_myquestion_back);
		lv_questionListView = (PagingLoadListView) findViewById(R.id.lv_myquestion_questionList);
		tv_title = (TextView) findViewById(R.id.tv_myquestion_title);
		vg_emptyTip = (ViewGroup) findViewById(R.id.vg_myquestion_emptyTip);
		vg_errorTip = (ViewGroup) findViewById(R.id.vg_myquestion_errorTip);
		vg_progress = (ViewGroup) findViewById(R.id.vg_myquestion_progress);
		btn_refresh = (RefreshButton) findViewById(R.id.vg_myquestion_refresh);
	}
	
	private void initView()
	{
		lv_questionListView.setAdapter(questionListAdapter);
		lv_questionListView.setVisibility(View.GONE);
		vg_emptyTip.setVisibility(View.GONE);
		vg_errorTip.setVisibility(View.GONE);
		switch(questionType)
		{
			case QUESTION_TYPE_MYASK: tv_title.setText("我的提问"); break;
			case QUESTION_TYPE_MYANSWER: tv_title.setText("我的回答"); break;
		}
	}
	
	private void initVar()
	{
		questionListAdapter = new QuestionListAdapter(this);
		
		if(getIntent().getExtras()!=null)
		{
			questionType = getIntent().getExtras().getInt(ARGS_QUESTIONTYPE);
		}
	}
	
	private void initListener()
	{
		lv_questionListView.setOnLoadListener(new PagingLoadListView.OnLoadListener()
		{
			@Override
			public void onLoad()
			{
				runAsyncTask(AsyncDataLoader.TASK_ADDITEM);
			}
		});
		btn_back.setOnClickListener(this);
		btn_refresh.setRefreshListener(new RefreshButton.RefreshListener()
		{
			@Override
			public void onRefresh()
			{
				runAsyncTask(AsyncDataLoader.TASK_INITLISTVIEW);
			}
		});
		
		lv_questionListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				int headerViewsCount = lv_questionListView.getHeaderViewsCount();
				int itemViewsCount = questionListAdapter.getCount();
				//如果有头布局和尾布局，则计算正确的item位置
				if(position>headerViewsCount-1 && position<headerViewsCount+itemViewsCount)
				{
					position = position+headerViewsCount;
					Intent intent = new Intent();
					intent.setClass(MyQuestionActivity.this, QuestionDetailActivity.class);
					intent.putExtra(QuestionDetailActivity.ARGS_QUESTION, (Question) parent.getAdapter().getItem(position));
					startActivity(intent);
				}
			}
		});
	}
	
	private void runAsyncTask(int task, Object... params)
	{
		new AsyncDataLoader(task, params).execute();
	}
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btn_myquestion_back:
			{
				finish();
			}break;
		}
	}
	
	private static class QuestionListAdapter extends BaseAdapter
	{
		private Context context;
		
		private List<Question> questionList;
		
		public QuestionListAdapter(Context context)
		{
			this.context = context;
			questionList = new ArrayList<>();
		}
		
		public List<Question> getQuestionList()
		{
			return this.questionList;
		}
		
		public void sort()
		{
			for (int j = 0; j < questionList.size()-1; j++)
			{
				for (int i = questionList.size()-1; i > j; i--)
				{
					if(questionList.get(i).compareTo(questionList.get(i-1)) == 1)
					{
						Question temp = questionList.get(i);
						questionList.set(i, questionList.get(i-1));
						questionList.set(i-1, temp);
					}
				}
			}
		}
		
		@Override
		public int getCount()
		{
			return questionList.size();
		}

		@Override
		public Question getItem(int position)
		{
			return questionList.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder vh;
			if(convertView==null)
			{
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_myquestion_question, parent, false);
				vh.tv_title = (TextView) convertView.findViewById(R.id.tv_question_title);
				vh.tv_time = (TextView) convertView.findViewById(R.id.tv_question_time);
				vh.tv_answerNum = (TextView) convertView.findViewById(R.id.tv_question_answerNum);
				vh.tv_isReslove = (TextView) convertView.findViewById(R.id.tv_question_isResolved);
				convertView.setTag(vh);
			}
			else
			{
				vh = (ViewHolder) convertView.getTag();
			}
			
			vh.tv_title.setText(getItem(position).getAskInfo().getField(EnquiryTable.FIELD_CONTENT));
			vh.tv_time.setText(getItem(position).getAskInfo().getField(EnquiryTable.FIELD_APPLYTIME));
			vh.tv_answerNum.setText(""+getItem(position).getAnswerInfoList().size());
			vh.tv_isReslove.setText("");
			/*if(getItem(position).isResolve())
			{
				vh.tv_isReslove.setTextColor(0xFF02DF82);
				vh.tv_isReslove.setText("已解决");
			}
			else
			{
				vh.tv_isReslove.setTextColor(0xFFFF5809);
				vh.tv_isReslove.setText("未解决");
			}*/
			
			return convertView;
		}
		
		private class ViewHolder
		{
			private TextView tv_title;
			private TextView tv_time;
			private TextView tv_answerNum;
			private TextView tv_isReslove;
		}
	}

	private class AsyncDataLoader extends AsyncTask<Object, Integer, Integer>
	{
		public static final int TASK_INITLISTVIEW = 1;
		public static final int TASK_INITLISTVIEW_RESULT_SUCCESS = 1;
		public static final int TASK_INITLISTVIEW_RESULT_ERROR = -1;
		public static final int TASK_ADDITEM = 2;
		public static final int TASK_ADDITEM_RESULT_SUCCESS = 2;
		public static final int TASK_ADDITEM_RESULT_ERROR = -2;
		
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
					if(lv_questionListView.getVisibility()==View.GONE) vg_progress.setVisibility(View.VISIBLE);
					vg_emptyTip.setVisibility(View.GONE);
					vg_errorTip.setVisibility(View.GONE);
				}break;
				
				case TASK_ADDITEM:
				{
					
				}break;
			}
		}
		
		List<Question> questionList = new ArrayList<>();
		@Override
		protected Integer doInBackground(Object... params)
		{
			switch (task)
			{
				case TASK_INITLISTVIEW:
				{
					try
					{
						switch(questionType)
						{
							case QUESTION_TYPE_MYASK:
							{
								//当前用户(即提问者)
								UserTable asker = DataOperationHelper.queryCurrentUser();
								questionList = DataOperationHelper.queryUserAskQuestionList(asker, 0, initLoadItemCount);
								loadStartIndex = 0+initLoadItemCount;
							}break;
							
							case QUESTION_TYPE_MYANSWER:
							{
								//当前用户(即回答者)
								UserTable replier = DataOperationHelper.queryCurrentUser();
								questionList = DataOperationHelper.queryUserAnswerQuestionList(replier);
								/*questionList = DataOperationHelper.queryUserAnswerQuestionList(replier, 1, initItemCount);
								loadStartIndex = initItemCount+1;*/
							}break;
						}
						
						return TASK_INITLISTVIEW_RESULT_SUCCESS;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
					return TASK_INITLISTVIEW_RESULT_ERROR;
				}
				
				case TASK_ADDITEM:
				{
					try
					{
						switch(questionType)
						{
							case QUESTION_TYPE_MYASK:
							{
								UserTable asker = DataOperationHelper.queryCurrentUser();
								questionList = DataOperationHelper.queryUserAskQuestionList(asker, loadStartIndex, loadNewItemCount);
								loadStartIndex += loadNewItemCount;
							}break;
							
							/*case QUESTION_TYPE_MYANSWER:
							{
								UserTable replier = DataOperationHelper.queryCurrentUser();
								questionList = DataOperationHelper.queryUserAnswerQuestionList(replier, loadStartIndex, loadNewItemCount);
								loadStartIndex += loadNewItemCount;
							}break;*/
						}
						
						return TASK_ADDITEM_RESULT_SUCCESS;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
					return TASK_ADDITEM_RESULT_ERROR;
				}
				
			}
			
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer taskResult)
		{
			switch (taskResult)
			{
				case TASK_INITLISTVIEW_RESULT_SUCCESS: //数据正常获取到了
				{
					if(questionList.size()!=0) //匹配到的数据不为空
					{
						questionListAdapter.getQuestionList().clear();
						questionListAdapter.getQuestionList().addAll(questionList);
						questionListAdapter.sort();
						questionList.clear();
						questionListAdapter.notifyDataSetChanged();
						btn_refresh.refreshComplete();

						lv_questionListView.setVisibility(View.VISIBLE);
						vg_progress.setVisibility(View.GONE);
						vg_emptyTip.setVisibility(View.GONE);
						vg_errorTip.setVisibility(View.GONE);
					}
					else //匹配到的数据为空
					{
						btn_refresh.refreshComplete();
						
						lv_questionListView.setVisibility(View.GONE);
						vg_progress.setVisibility(View.GONE);
						vg_emptyTip.setVisibility(View.VISIBLE);
						vg_errorTip.setVisibility(View.GONE);
						//ToastUitl.showShort(MyQuestionActivity.this, "刷新完成");
					}
					
				}break;
				
				case TASK_INITLISTVIEW_RESULT_ERROR: //数据没有正常获取到
				{
					btn_refresh.refreshComplete();
					
					//当刷新数据出现异常时：
					//若当前列表中不存在数据，则直接用error tip占据屏幕
					//若当前列表中存在数据，则不用error tip占据屏幕，只是提示有异常
					//(防止情况：用户起初刷新到了数据，后面再刷新时出现了异常；此时如果用error tip占据屏幕就会顶掉原来的数据)
					if(questionListAdapter.getQuestionList().size()==0)
					{
						lv_questionListView.setVisibility(View.GONE);
						vg_errorTip.setVisibility(View.VISIBLE);
						//ToastUitl.showShort(MyQuestionActivity.this, "刷新完成");
					}
					else
					{
						lv_questionListView.setVisibility(View.VISIBLE);
						vg_errorTip.setVisibility(View.GONE);
					}
					vg_progress.setVisibility(View.GONE);
					vg_emptyTip.setVisibility(View.GONE);
				}break;
				
				case TASK_ADDITEM_RESULT_SUCCESS:
				{
					questionListAdapter.getQuestionList().addAll(questionList);
					questionListAdapter.sort();
					questionList.clear();
					questionListAdapter.notifyDataSetChanged();
					lv_questionListView.loadComplete();
				}break;
				
				case TASK_ADDITEM_RESULT_ERROR:
				{
					
				}break;
			}
		}
	}
	
	public static final String ARGS_QUESTIONTYPE = "QUESTIONTYPE";
	
	public static final int QUESTION_TYPE_MYASK = 0;
	public static final int QUESTION_TYPE_MYANSWER = 1;
}
