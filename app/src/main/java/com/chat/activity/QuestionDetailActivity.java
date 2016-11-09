package com.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.adapter.pojo.Answer;
import com.chat.adapter.pojo.Message;
import com.chat.adapter.pojo.Question;
import com.database.dto.DataOperation;
import com.database.pojo.EnquiryTable;
import com.database.pojo.ReplyTable;
import com.database.pojo.UserTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.DataOperationHelper;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

public class QuestionDetailActivity extends FragmentActivity implements View.OnClickListener
{
	private ImageButton btn_back;
	private TextView tv_title;
	private ListView lv_questionDetailListView;
	private Button btn_answer;
	private ViewGroup vg_detail; //包含lv_questionDetailListView
	private ViewGroup vg_progress;
	private ViewGroup vg_answer;
	
	private QuestionDetailListAdapter questionDetailListAdapter;
	
	@Override
	protected void onCreate(Bundle saveInstanceState)
	{
		super.onCreate(saveInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_questiondetail);
		findView();
		initVar();
		initView();
		initListener();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		runAsyncTask(AsyncDataLoader.TASK_UPDATELISTVIEW); //刷新ListView
	}
	
	private void findView()
	{
		btn_back = (ImageButton) findViewById(R.id.btn_questiondetail_back);
		tv_title = (TextView) findViewById(R.id.tv_questiondetail_title);
		lv_questionDetailListView = (ListView) findViewById(R.id.lv_questiondetail_questionList);
		btn_answer = (Button) findViewById(R.id.btn_questiondetail_answer);
		vg_detail = (ViewGroup) findViewById(R.id.vg_questiondetail_detail);
		vg_progress = (ViewGroup) findViewById(R.id.vg_questiondetail_progress);
		vg_answer = (ViewGroup) findViewById(R.id.vg_questiondetail_answer);
	}
	
	private void initView()
	{
		tv_title.setText(questionDetailListAdapter.getQuestion().getAskerInfo().getField(UserTable.FIELD_REALNAME)+"的提问"); //设置标题
		lv_questionDetailListView.setAdapter(questionDetailListAdapter); //设置问答列表
		if(questionDetailListAdapter.getQuestion().getAskerInfo().getField(UserTable.FIELD_USERNAME).equals(MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, ""))) vg_answer.setVisibility(View.GONE); //判断用户名
		else vg_answer.setVisibility(View.VISIBLE);
		btn_answer.setText("我来回答");
	}
	
	private void initVar()
	{
		Bundle args = getIntent().getExtras();
		if(args!=null)
		{
			Question question = (Question) args.getSerializable(ARGS_QUESTION);
			questionDetailListAdapter = new QuestionDetailListAdapter(this, lv_questionDetailListView, question);
		}
	}
	
	private void initListener()
	{
		btn_back.setOnClickListener(this);
		btn_answer.setOnClickListener(this);
		
		lv_questionDetailListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) //查看每条回答的详情
			{
				if(position!=0)
				{
					Answer answer = questionDetailListAdapter.getQuestion().getAnswerInfoList().get(position-1);
					Message message = new Message(answer.getReplierInfo(), answer.getAnswerInfo(), Message.TYPE_RECEIVE_TEXT);
					List<UserTable> userList = new ArrayList<>();
					userList.add(answer.getReplierInfo());
					userList.add(questionDetailListAdapter.getQuestion().getAskerInfo());
					
					Intent intent = new Intent();
					intent.setClass(QuestionDetailActivity.this, ChatActivity.class);
					intent.putExtra(ChatActivity.ARGS_FIRSTMESSAGE, message);
					intent.putExtra(ChatActivity.ARGS_USERLIST, (ArrayList<UserTable>)userList);
					intent.putExtra(ChatActivity.ARGS_TITLE, answer.getReplierInfo().getField(UserTable.FIELD_REALNAME)+"的回答");
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
			case R.id.btn_questiondetail_back:
			{
				finish();
			}break;
			
			case R.id.btn_questiondetail_answer: //回答提问
			{
				runAsyncTask(AsyncDataLoader.TASK_ANSWER);
			}break;
		}
	}
	
	private class QuestionDetailListAdapter extends BaseAdapter
	{
		private Context context;
		private BitmapUtils bitmapUtils;
		private Question question;
		private SparseArray<View> convertViewList;
		
		public QuestionDetailListAdapter(Context context, ListView listView, Question question)
		{
			this.context = context;
			bitmapUtils = BitmapHelper.getBitmapUtils(context, listView, R.drawable.head_default, R.drawable.head_default);
			this.question = question;
			convertViewList = new SparseArray<>();
		}
		
		public Question getQuestion()
		{
			return question;
		}
		
		@Override
		public int getCount()
		{
			if(question==null) return 0;
			else return question.getAnswerInfoList().size()+1;
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}
		
		@Override
		public int getItemViewType(int position)
		{
			if(position==0) return 0;
			else return 1;
		}
		
		@Override
		public int getViewTypeCount()
		{
			return 2;
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
			int type = getItemViewType(position);
			
			if(convertView == null)
			{
				vh = new ViewHolder();
				switch(type)
				{
					case 0:
					{
						convertView = LayoutInflater.from(context).inflate(R.layout.item_questiondetail_ask, parent, false);
						vh.iv_ask_askerIcon = (ImageView) convertView.findViewById(R.id.iv_questiondetail_ask_askerIcon);
						vh.tv_ask_content = (TextView) convertView.findViewById(R.id.tv_questiondetail_ask_content);
						vh.tv_ask_time = (TextView) convertView.findViewById(R.id.tv_questiondetail_ask_time);
						vh.tv_ask_answerNum = (TextView) convertView.findViewById(R.id.tv_questiondetail_ask_answerNum);
						vh.vg_ask_divider = (ViewGroup) convertView.findViewById(R.id.vg_questiondetail_ask_divider);
					}break;
					
					case 1:
					{
						convertView = LayoutInflater.from(context).inflate(R.layout.item_questiondetail_answer, parent, false);
						vh.iv_answer_replierIcon = (ImageView) convertView.findViewById(R.id.iv_questiondetail_answer_replierIcon);
						vh.tv_answer_replierName = (TextView) convertView.findViewById(R.id.tv_questiondetail_answer_replierName);
						vh.tv_answer_content = (TextView) convertView.findViewById(R.id.tv_questiondetail_answer_content);
						vh.tv_answer_time = (TextView) convertView.findViewById(R.id.tv_questiondetail_answer_time);
					}break;
				}
				convertViewList.append(position, convertView);
				convertView.setTag(vh);
			}
			else
			{
				vh = (ViewHolder) convertView.getTag();
			}
			
			switch(type)
			{
				case 0:
				{
					String headIconUrl = "";
					if(question.getAskerInfo().getAccessaryFileUrlList()!=null && question.getAskerInfo().getAccessaryFileUrlList().size()!=0 )
					{
						headIconUrl = question.getAskerInfo().getAccessaryFileUrlList().get(0);
					}
					bitmapUtils.display(vh.iv_ask_askerIcon, headIconUrl);
					
					vh.tv_ask_content.setText(question.getAskInfo().getField(EnquiryTable.FIELD_CONTENT));
					vh.tv_ask_time.setText(question.getAskInfo().getField(EnquiryTable.FIELD_APPLYTIME));
					vh.tv_ask_answerNum.setText(""+question.getAnswerInfoList().size());
					
					if(getCount()>1) vh.vg_ask_divider.setVisibility(View.VISIBLE);
					else vh.vg_ask_divider.setVisibility(View.GONE);
				}break;
				
				case 1:
				{
					String headIconUrl = "";
					if(question.getAnswerInfoList().get(position-1).getReplierInfo().getAccessaryFileUrlList()!=null && question.getAnswerInfoList().get(position-1).getReplierInfo().getAccessaryFileUrlList().size()!=0 )
					{
						headIconUrl = question.getAnswerInfoList().get(position-1).getReplierInfo().getAccessaryFileUrlList().get(0);
					}
					bitmapUtils.display(vh.iv_answer_replierIcon, headIconUrl);
					
					vh.tv_answer_replierName.setText(question.getAnswerInfoList().get(position-1).getReplierInfo().getField(UserTable.FIELD_REALNAME));
					vh.tv_answer_content.setText(question.getAnswerInfoList().get(position-1).getAnswerInfo().getField(ReplyTable.FIELD_CONTENT));
					vh.tv_answer_time.setText(question.getAnswerInfoList().get(position-1).getAnswerInfo().getField(ReplyTable.FIELD_T_TIME));
				}break;
			}
			
			return convertView;
		}
		
		private class ViewHolder
		{
			private ImageView iv_ask_askerIcon;
			private TextView tv_ask_content;
			private TextView tv_ask_time;
			private TextView tv_ask_answerNum;
			private ViewGroup vg_ask_divider;
			
			private ImageView iv_answer_replierIcon;
			private TextView tv_answer_replierName;
			private TextView tv_answer_content;
			private TextView tv_answer_time;
		}
	}
	
	private class AsyncDataLoader extends AsyncTask<Object, Integer, Integer>
	{
		private static final int TASK_ANSWER = 1;
		private static final int TASK_ANSWER_RESULT_SUCCESS = 1;
		private static final int TASK_ANSWER_RESULT_ERROR = -1;
		private static final int TASK_UPDATELISTVIEW = 2;
		private static final int TASK_UPDATELISTVIEW_RESULT_SUCCESS = 2;
		private static final int TASK_UPDATELISTVIEW_RESULT_ERROR = -2;
		
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
			switch(task)
			{
				case TASK_ANSWER:
				{
					
				}break;
				
				case TASK_UPDATELISTVIEW:
				{
					vg_progress.setVisibility(View.VISIBLE);
					vg_detail.setVisibility(View.GONE);
				}break;
			}
		}
		
		UserTable currentUser; //当前用户UserTable
		ArrayList<Answer> answerList = new ArrayList<>();
		@Override
		protected Integer doInBackground(Object... params)
		{
			switch(task)
			{
				case TASK_ANSWER:
				{
					try
					{
						UserTable currentUser = DataOperationHelper.queryCurrentUser();
						
						//找到当前用户对当前问题的第一条回答
						//当前用户对当前问题的第一条回答
						Message firstMessage = null;
						//当前问题的所有首条回答列表
						List<ReplyTable> replyList = (List<ReplyTable>) DataOperation.queryTable(ReplyTable.TABLE_NAME, ReplyTable.FIELD_REPLYTONO, questionDetailListAdapter.getQuestion().getAskInfo().getContentId());
						if(replyList!=null)
						{
							for (ReplyTable reply : replyList)
							{
								//判断回答列表中是否有当前用户
								if(currentUser.equals(DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, reply.getField(ReplyTable.FIELD_USERNO)).get(0)))
								{
									//列表中有当前用户的回答
									firstMessage = new Message(currentUser, reply, Message.TYPE_SEND_TEXT);
								}
							}
						}
						/*if(firstMessage==null)
						{
							firstMessage = DataOperationHelper.uploadMessage(
									questionDetailListAdapter.getQuestion().getAskerInfo(), 
									questionDetailListAdapter.getQuestion().getAskInfo().getContentId(), 
									questionDetailListAdapter.getQuestion().getAskInfo().getField(EnquiryTable.FIELD_CONTENT), 
									questionDetailListAdapter.getQuestion().getAskInfo().getField(EnquiryTable.FIELD_APPLYTIME));
						}*/
						
						//参与聊天的用户列表
						List<UserTable> userList = new ArrayList<>();
						UserTable asker = questionDetailListAdapter.getQuestion().getAskerInfo();
						userList.add(currentUser);
						userList.add(asker);
						
						//启动ChatActivity
						Intent intent = new Intent();
						intent.setClass(QuestionDetailActivity.this, ChatActivity.class);
						if(firstMessage!=null) intent.putExtra(ChatActivity.ARGS_FIRSTMESSAGE, firstMessage);
						else intent.putExtra(ChatActivity.ARGS_REPLYNO, questionDetailListAdapter.getQuestion().getAskInfo().getContentId());
						intent.putExtra(ChatActivity.ARGS_USERLIST, (ArrayList<UserTable>)userList);
						intent.putExtra(ChatActivity.ARGS_TITLE, questionDetailListAdapter.getQuestion().getAskerInfo().getField(UserTable.FIELD_REALNAME)+"的提问");
						intent.putExtra(ChatActivity.ARGS_CHATMODE, ChatActivity.CHATMODE_QUESTION);
						startActivity(intent);
						
						return TASK_ANSWER_RESULT_SUCCESS;
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return TASK_ANSWER_RESULT_ERROR;
					}
				}
				
				case TASK_UPDATELISTVIEW:
				{
					try
					{
						currentUser = DataOperationHelper.queryCurrentUser();
						answerList = DataOperationHelper.queryQuestionAnswerList(questionDetailListAdapter.getQuestion().getAskInfo().getContentId());
						
						return TASK_UPDATELISTVIEW_RESULT_SUCCESS;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
					return TASK_UPDATELISTVIEW_RESULT_ERROR;
				}
			}
			
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer taskResult)
		{
			switch(taskResult)
			{
				case TASK_ANSWER_RESULT_SUCCESS:
				{
					
				}break;
				
				case TASK_ANSWER_RESULT_ERROR:
				{
					Toast.makeText(QuestionDetailActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
				}break;
				
				case TASK_UPDATELISTVIEW_RESULT_SUCCESS:
				{
					vg_progress.setVisibility(View.GONE);
					vg_detail.setVisibility(View.VISIBLE);
					
					questionDetailListAdapter.getQuestion().getAnswerInfoList().clear();
					questionDetailListAdapter.getQuestion().getAnswerInfoList().addAll(answerList);
					answerList.clear();
					questionDetailListAdapter.notifyDataSetChanged();
					
					//判断当前问题是否是当前用户发布的
					//自己不能回答自己发布的问题
					//如果是自己发布的问题，则不显示回答按钮；否则显示回答按钮
					if(currentUser!=null && !currentUser.equals(questionDetailListAdapter.getQuestion().getAskerInfo()) ) vg_answer.setVisibility(View.VISIBLE);
					else vg_answer.setVisibility(View.GONE);
					
					//判断当前问题的回答列表中是否有自己的回答
					//如果有，则做一些特殊设置
					List<Answer> list = questionDetailListAdapter.getQuestion().getAnswerInfoList();
					for (int i = 0; i < list.size(); i++)
					{
						if(list.get(i).getReplierInfo().equals(currentUser))
						{
							btn_answer.setText("继续回答"); //把回答按钮的文字从"我来回答"改为"继续回答"
							
							/*new Timer().schedule(new TimerTask()
							{
								@Override
								public void run()
								{
									try
									{
										Thread.sleep(1000);
										if(questionDetailListAdapter.convertViewList!=null) T.l(questionDetailListAdapter.convertViewList);
										else cancel();
									}
									catch (InterruptedException e)
									{
										e.printStackTrace();
									}
								}
							}, 0, 1);*/
							
							//questionDetailListAdapter.convertViewList.get(i+1).setBackgroundColor(0x000); //把自己回答的item的背景置为醒目色
							break;
						}
					}
				}break;
				
				case TASK_UPDATELISTVIEW_RESULT_ERROR:
				{
					vg_progress.setVisibility(View.GONE);
					vg_detail.setVisibility(View.VISIBLE);
					Toast.makeText(QuestionDetailActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
				}break;
			}
		}
	}
	
	public static final String ARGS_QUESTION = "QUESTION";
}
