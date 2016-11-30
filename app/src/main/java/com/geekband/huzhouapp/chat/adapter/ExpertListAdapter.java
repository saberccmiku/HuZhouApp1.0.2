package com.geekband.huzhouapp.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geekband.huzhouapp.chat.activity.ChatActivity;
import com.geekband.huzhouapp.chat.adapter.pojo.Expert;
import com.geekband.huzhouapp.chat.adapter.pojo.Message;
import com.geekband.huzhouapp.utils.DataOperation;
import com.geekband.huzhouapp.vo.pojo.ExpertTable;
import com.geekband.huzhouapp.vo.pojo.ReplyTable;
import com.geekband.huzhouapp.vo.pojo.UserInfoTable;
import com.geekband.huzhouapp.vo.pojo.UserTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.geekband.huzhouapp.utils.Constants;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpertListAdapter extends BaseAdapter
{
	private Context context;
	private List<Expert> expertList;
	private BitmapUtils bitmapUtils;
	
	public ExpertListAdapter(Context context, ListView listView)
	{
		this.context = context;
		expertList = new ArrayList<>();
		bitmapUtils = BitmapHelper.getBitmapUtils(context, listView, R.drawable.head_default, R.drawable.head_default);
	}
	
	private void runAsyncTask(int task, Object... params)
	{
		new AsyncDataLoader(task, params).execute();
	}
	
	public List<Expert> getExpertList()
	{
		return this.expertList;
	}
	
	@Override
	public int getCount()
	{
		return expertList.size();
	}

	@Override
	public Expert getItem(int position)
	{
		return expertList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		ViewHolder vh;
		if(convertView==null)
		{
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_expert_expert, parent, false);
			vh.iv_head = (ImageView) convertView.findViewById(R.id.iv_expert_head);
			vh.tv_name = (TextView) convertView.findViewById(R.id.tv_expert_name);
			vh.tv_individualTitle = (TextView) convertView.findViewById(R.id.tv_expert_individualTitle);
			vh.tv_introduction = (TextView) convertView.findViewById(R.id.tv_expert_introduction);
			vh.tv_consult = (TextView) convertView.findViewById(R.id.tv_expert_consult);
			convertView.setTag(vh);
		}
		else
		{
			vh = (ViewHolder) convertView.getTag();
		}
		
		ExpertTable expert = getItem(position).getExpert();
		UserTable expertInfo = getItem(position).getExpertInfo();
		UserInfoTable expertUserInfo = getItem(position).getExpertUserInfo();
		
		String headIconUrl = "";
		if (expertInfo!=null) {
			if (expertInfo.getAccessaryFileUrlList() != null && expertInfo.getAccessaryFileUrlList().size() != 0) {
				headIconUrl = expertInfo.getAccessaryFileUrlList().get(0);
			}
		}
		bitmapUtils.display(vh.iv_head, headIconUrl);
		
		if(expertInfo!=null) vh.tv_name.setText(expertInfo.getField(UserTable.FIELD_REALNAME));
		//vh.tv_individualTitle.setText(""); //个人头衔，暂时为空
		if(expert!=null) vh.tv_introduction.setText(expert.getField(ExpertTable.FIELD_INTRODUCTION));
		vh.tv_consult.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				runAsyncTask(AsyncDataLoader.TASK_OPENCHAT, position); //打开与所选专家的双人聊天界面
			}
		});
		
		return convertView;
	}
	
	private class ViewHolder
	{
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
	
	private class AsyncDataLoader extends AsyncTask<Object, Integer, Integer>
	{
		private static final int TASK_OPENCHAT = 1;
		private static final int TASK_OPENCHAT_RESULT_SUCCESS = 1;
		private static final int TASK_OPENCHAT_RESULT_ERROR_1 = -1;
		private static final int TASK_OPENCHAT_RESULT_ERROR_2 = -2;
		
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
				case TASK_OPENCHAT:
				{
					
				}break;
			}
		}

		@Override
		protected Integer doInBackground(Object... params)
		{
			switch (task)
			{
				case TASK_OPENCHAT:
				{
					try
					{
						//点击的item的position
						int position = (Integer) this.params[0];
						
						//对方(专家)
						UserTable expertUserData = getItem(position).getExpertInfo();
						//自己
						UserTable currentUserData = (UserTable) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, "")).get(0);
						
						//若当前咨询的专家是自己，则不能自己向自己发起聊天
						if(expertUserData.equals(currentUserData))
						{
							return TASK_OPENCHAT_RESULT_ERROR_2;
						}
						
						//聊天双方的首条消息
						Message firstMessage = null;
						HashMap<String , String> fieldList = new HashMap<>();
						fieldList.put(ReplyTable.FIELD_REPLYTONO, expertUserData.getContentId());
						fieldList.put(ReplyTable.FIELD_USERNO, currentUserData.getContentId());
						List<ReplyTable> reply = (List<ReplyTable>) DataOperation.queryTable(ReplyTable.TABLE_NAME, fieldList);
						if(reply!=null && reply.size()!=0)
						{
							ReplyTable firstReply = reply.get(0);
							firstMessage = new Message(currentUserData, firstReply, Message.TYPE_SEND_TEXT);
						}
						else
						{
							fieldList = new HashMap<>();
							fieldList.put(ReplyTable.FIELD_REPLYTONO, currentUserData.getContentId());
							fieldList.put(ReplyTable.FIELD_USERNO, expertUserData.getContentId());
							reply = (List<ReplyTable>) DataOperation.queryTable(ReplyTable.TABLE_NAME, fieldList);
							if(reply!=null && reply.size()!=0)
							{
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
						if(firstMessage!=null) intent.putExtra(ChatActivity.ARGS_FIRSTMESSAGE, firstMessage);
						else intent.putExtra(ChatActivity.ARGS_REPLYNO, expertUserData.getContentId());
						intent.putExtra(ChatActivity.ARGS_USERLIST, (ArrayList<UserTable>)userList);
						intent.putExtra(ChatActivity.ARGS_TITLE, "与"+expertUserData.getField(UserTable.FIELD_REALNAME)+"的对话");
						intent.putExtra(ChatActivity.ARGS_CHATMODE, ChatActivity.CHATMODE_SINGLE);
						context.startActivity(intent);
						
						return TASK_OPENCHAT_RESULT_SUCCESS;
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return TASK_OPENCHAT_RESULT_ERROR_1;
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
				case TASK_OPENCHAT_RESULT_SUCCESS:
				{
					
				}break;
				

				case TASK_OPENCHAT_RESULT_ERROR_1:
				{
					
				}break;
				
				case TASK_OPENCHAT_RESULT_ERROR_2:
				{
					Toast.makeText(context, "您不能自己咨询自己哦", Toast.LENGTH_SHORT).show();
				}break;
			}
		}
	}
}
