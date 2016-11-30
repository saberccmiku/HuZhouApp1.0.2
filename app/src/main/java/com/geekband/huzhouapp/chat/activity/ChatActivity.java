package com.geekband.huzhouapp.chat.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geekband.huzhouapp.chat.adapter.MessageListAdapter;
import com.geekband.huzhouapp.chat.adapter.pojo.Message;
import com.geekband.huzhouapp.vo.pojo.UserTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.utils.DataOperationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends Activity implements View.OnClickListener
{
	private TextView tv_title;
	private ListView lv_messageListView;
	private ImageButton btn_back;
	private EditText et_content;
	private Button btn_send;
	private ViewGroup vg_messageList;
	private ViewGroup vg_progress;
	private ViewGroup vg_send;

	private MessageListAdapter messageListAdapter;
	private List<UserTable> userList;
	private UserTable currentUser;
	private String title;
	private int currentChatMode;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		findView();
		initVar();
		initView();
		initListener();
		runAsyncTask(AsyncDataLoader.TASK_INITLISTVIEW); //更新listView
	}
	
	private void findView()
	{
		tv_title = (TextView) findViewById(R.id.tv_chat_title);
		lv_messageListView = (ListView) findViewById(R.id.lv_chat_messageList);
		btn_back = (ImageButton) findViewById(R.id.btn_chat_back);
		et_content = (EditText) findViewById(R.id.et_chat_content);
		btn_send = (Button) findViewById(R.id.btn_chat_send);
		vg_messageList = (ViewGroup) findViewById(R.id.vg_chat_messageList);
		vg_progress = (ViewGroup) findViewById(R.id.vg_chat_progress);
		vg_send = (ViewGroup) findViewById(R.id.vg_chat_send);
	}
	
	private void initView()
	{
		tv_title.setText(title);
		lv_messageListView.setAdapter(messageListAdapter);
	}
	
	private void initVar()
	{
		messageListAdapter = new MessageListAdapter(this, lv_messageListView);
		
		Bundle args = getIntent().getExtras();
		if(args!=null)
		{
			title = args.getString(ARGS_TITLE);
			currentChatMode = args.getInt(ARGS_CHATMODE);
		}
	}
	
	private void initListener()
	{
		btn_back.setOnClickListener(this);
		btn_send.setOnClickListener(this);
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
			case R.id.btn_chat_back: //返回
			{
				finish();
			}break;
			
			case R.id.btn_chat_send: //发送消息
			{
				String content = et_content.getText().toString();
				if(content!=null && !"".equals(content))
				{
					runAsyncTask(AsyncDataLoader.TASK_SENDMSG, content);
				}
				else
				{
					Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
				}
			}break;
		}
	}
	
	private boolean isHasCurrentUser(UserTable user, List<UserTable> userList)
	{
		if(userList!=null)
		{
			for (UserTable user1 : userList)
			{
				if(user1.equals(user))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	private class AsyncDataLoader extends AsyncTask<Object, Integer, Integer>
	{
		private static final int TASK_INITLISTVIEW = 1;
		private static final int TASK_RESULT_INITLISTVIEW_SUCCES = 1;
		private static final int TASK_RESULT_INITLISTVIEW_ERROR = -1;
		private static final int TASK_SENDMSG = 2;
		private static final int TASK_RESULT_SENDMSG_SUCCES = 2;
		private static final int TASK_RESULT_SENDMSG_ERROR = -2;
		
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
				case TASK_INITLISTVIEW:
				{
					vg_progress.setVisibility(View.VISIBLE);
					vg_messageList.setVisibility(View.GONE);
				}break;
				
				case TASK_SENDMSG:
				{
					
				}break;
			}
		}

		List<Message> messageList = new ArrayList<>();
		Message willSendMessage;
		@Override
		protected Integer doInBackground(Object... params)
		{
			switch(task)
			{
				case TASK_INITLISTVIEW:
				{
					try
					{
						//当前用户
						currentUser = DataOperationHelper.queryCurrentUser();
						
						//该Activity被启动时传入的参数
						Bundle args = getIntent().getExtras();
						if(args!=null)
						{
							//传入的Message参数
							//ChatActivity的逻辑时，获取到聊天双方的第一条消息后，就可以参照第一条消息，链式搜索到其下的所有聊天消息
							//所以外部只需传入首条Message参数，ChatActivity就可显示出聊天双方的所有聊天消息记录
							 //找出该消息下的所有子回复
							Message message = (Message) args.getSerializable(ARGS_FIRSTMESSAGE);
							messageList.addAll(DataOperationHelper.queryChildMessageList(message));
							
							//外部传入的 参与聊天的用户列表 参数
							userList = (List<UserTable>) args.getSerializable(ARGS_USERLIST);
						}
						
						return TASK_RESULT_INITLISTVIEW_SUCCES;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
					return TASK_RESULT_INITLISTVIEW_ERROR;
				}
				
				case TASK_SENDMSG: //发送消息(当前用户对当前聊天的其他人发送消息)
				{
					try
					{
						//要发送的消息内容
						String content = (String) this.params[0];
						//当前时间
						String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis()));
						//该消息回复的[对象]
						//由于ChatActivity的消息数据录入逻辑，当当前聊天还没有产生首条聊天消息时，消息的回复对象则为UserTable或EnquiryTable
						//只有当首条消息产生时，回复对象才是ReplyTable
						String replyToNo = null;
						if(messageListAdapter.getMessageList().size()>=1) replyToNo = messageListAdapter.getMessageList().get(messageListAdapter.getMessageList().size()-1).getMessageInfo().getContentId();
						if(replyToNo==null) replyToNo = getIntent().getExtras().getString(ARGS_REPLYNO);
						
						willSendMessage = DataOperationHelper.uploadMessage(currentUser, replyToNo, content, time);
						
						return TASK_RESULT_SENDMSG_SUCCES;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
					return TASK_RESULT_SENDMSG_ERROR;
					
					/*switch(currentChatMode)
					{
						case CHATMODE_QUESTION:
						{
							try
							{
								String content = (String) this.params[0];
								String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis()));
								String replyToNo = null;
								if(messageListAdapter.getMessageList().size()>=2) replyToNo = messageListAdapter.getMessageList().get(messageListAdapter.getMessageList().size()-1).getMessageInfo().getContentId();
								if(replyToNo==null) replyToNo = getIntent().getExtras().getString(ARGS_REPLYNO);
								
								willSendMessage = DataOperationHelper.uploadMessage(currentUser, replyToNo, content, time);
								
								return TASK_RESULT_SENDMSG_SUCCES;
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
							
							return TASK_RESULT_SENDMSG_ERROR;
						}
						
						case CHATMODE_SINGLE:
						{
							try
							{
								//要发送的消息内容
								String content = (String) this.params[0];
								//当前时间
								String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis()));
								//该消息回复的[对象]
								//由于ChatActivity的消息数据录入逻辑，当当前聊天还没有产生首条聊天消息时，消息的回复对象则为UserTable或EnquiryTable
								//只有当首条消息产生时，回复对象才是ReplyTable
								String replyToNo = null;
								if(messageListAdapter.getMessageList().size()>=1) replyToNo = messageListAdapter.getMessageList().get(messageListAdapter.getMessageList().size()-1).getMessageInfo().getContentId();
								if(replyToNo==null) replyToNo = getIntent().getExtras().getString(ARGS_REPLYNO);
								
								willSendMessage = DataOperationHelper.uploadMessage(currentUser, replyToNo, content, time);
								
								return TASK_RESULT_SENDMSG_SUCCES;
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
							
							return TASK_RESULT_SENDMSG_ERROR;
						}
					}*/
					
				}
			}
			
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer taskResult)
		{
			switch (taskResult)
			{
				case TASK_RESULT_INITLISTVIEW_SUCCES:
				{
					messageListAdapter.getMessageList().clear();
					messageListAdapter.getMessageList().addAll(messageList);
					messageListAdapter.notifyDataSetChanged();
					messageList.clear();
					if(isHasCurrentUser(currentUser, userList)) vg_send.setVisibility(View.VISIBLE); //若当前聊天 当前用户自己未参与，则不显示发送聊天消息的按钮
					else vg_send.setVisibility(View.GONE);

					vg_progress.setVisibility(View.GONE);
					vg_messageList.setVisibility(View.VISIBLE);
				}break;

				case TASK_RESULT_INITLISTVIEW_ERROR:
				{
					Toast.makeText(ChatActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
					vg_progress.setVisibility(View.GONE);
					vg_messageList.setVisibility(View.VISIBLE);
				}break;
				
				case TASK_RESULT_SENDMSG_SUCCES:
				{
					messageListAdapter.getMessageList().add(willSendMessage);
					messageListAdapter.notifyDataSetChanged();
					lv_messageListView.setSelection(messageListAdapter.getCount()-1);
					et_content.setText("");
				}break;
				
				case TASK_RESULT_SENDMSG_ERROR:
				{
					Toast.makeText(ChatActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
				}break;
			}
		}
	}
	
	public static final String ARGS_FIRSTMESSAGE = "FIRSTMESSAGE";
	public static final String ARGS_REPLYNO = "REPLYNO";
	public static final String ARGS_USERLIST = "USERLIST";
	public static final String ARGS_TITLE = "TITLE";
	public static final String ARGS_CHATMODE = "CHATMODE";
	
	public static final int CHATMODE_SINGLE = 0;
	public static final int CHATMODE_QUESTION = 1;
}
