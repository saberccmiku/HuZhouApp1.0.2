package com.chat.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chat.adapter.pojo.Message;
import com.database.dto.DataOperation;
import com.database.pojo.EnquiryTable;
import com.database.pojo.ReplyTable;
import com.database.pojo.UserTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.geekband.huzhouapp.utils.Constants;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

public class NotificationListAdapter extends BaseAdapter
{
	private Context context;
	private List<Message> notificationList;
	private BitmapUtils bitmapUtils;
	
	public NotificationListAdapter(Context context, ListView listView)
	{
		this.context = context;
		notificationList = new ArrayList<>();
		bitmapUtils = BitmapHelper.getBitmapUtils(context, listView, R.drawable.head_default, R.drawable.head_default);
	}
	
	public List<Message> getNotificationList()
	{
		return this.notificationList;
	}
	
	private void runAsyncTask(int task, Object... params)
	{
		new AsyncDataLoader(task, params).execute();
	}
	
	@Override
	public int getCount()
	{
		return notificationList.size();
	}

	@Override
	public Message getItem(int position)
	{
		return notificationList.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_notification_notification, parent, false);
			vh = new ViewHolder();
			vh.iv_userHead = (ImageView) convertView.findViewById(R.id.iv_notification_userHead);
			vh.tv_userName = (TextView) convertView.findViewById(R.id.tv_notification_userName);
			vh.tv_content = (TextView) convertView.findViewById(R.id.tv_notification_content);
			vh.tv_time = (TextView) convertView.findViewById(R.id.tv_notification_time);
			convertView.setTag(vh);
		}
		else
		{
			vh = (ViewHolder) convertView.getTag();
		}
		
		//通知列表listView 消息的图标，显示为聊天双方的、对方的头像 消息的标题，显示为对方的昵称
		runAsyncTask(AsyncDataLoader.TASK_INITVIEW, position, vh.iv_userHead, vh.tv_userName);
		vh.tv_content.setText(getItem(position).getMessageInfo().getField(ReplyTable.FIELD_CONTENT));
		vh.tv_time.setText(getItem(position).getMessageInfo().getField(ReplyTable.FIELD_T_TIME));
		
		return convertView;
	}
	
	private class ViewHolder
	{
		private ImageView iv_userHead;
		private TextView tv_userName;
		private TextView tv_content;
		private TextView tv_time;
	}
	
	private class AsyncDataLoader extends AsyncTask<Object, Integer, Integer>
	{
		private static final int TASK_INITVIEW = 1;
		private static final int TASK_INITVIEW_RESULT_SUCCESS = 1;
		private static final int TASK_INITVIEW_RESULT_ERROR = -1;
		
		private int task;
		private Object[] params;
		
		public AsyncDataLoader(int task, Object...params)
		{
			this.task = task;
			this.params = params;
		}
		
		ImageView iv_userHead;
		TextView tv_userName;
		String headIconUrl = "";
		String userName = "";
		@Override
		protected void onPreExecute()
		{
			iv_userHead = (ImageView) this.params[1];
			tv_userName = (TextView) this.params[2];
			
			switch (task)
			{
				case TASK_INITVIEW:
				{
					bitmapUtils.display(iv_userHead, "");
				}break;
			}
		}
		
		@Override
		protected Integer doInBackground(Object... params)
		{
			switch (task)
			{
				case TASK_INITVIEW:
				{
					try
					{
						int position = (int) this.params[0];
						
						//一个通知对应一个聊天，私聊包含自己和对方
						//获取对方用户对应的UserTable
						//如果首条消息发送者是对方，则直接可以获取到对方用户UserTable；
						//如果首条消息发送者是自己，那么需要先获取该条消息的发送对象(也就是对方用户)，再获取到对方用户UserTable；
						//当消息发送对象是[问题]时，则对方用户就是该问题的提问者；
						UserTable user = getItem(position).getMessageSenderInfo();
						String currentUserContentId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, "");
						if(user.getContentId().equals(currentUserContentId)) //如果消息发送者是自己
						{
							//当消息发送者是自己时，如果该消息回复对象是[用户]，则直接获取对方用户UserTable
							ArrayList<UserTable> uList = (ArrayList<UserTable>) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, getItem(position).getMessageInfo().getField(ReplyTable.FIELD_REPLYTONO));
							if(uList!=null && uList.size()!=0)
							{
								user = uList.get(0);
							}
							//如果该消息回复对象不是[用户] 则是[问题]
							else
							{
								//获取该问题的提问者，也就是对方用户UserTable
								ArrayList<EnquiryTable> eList = (ArrayList<EnquiryTable>) DataOperation.queryTable(EnquiryTable.TABLE_NAME, EnquiryTable.CONTENTID, getItem(position).getMessageInfo().getField(ReplyTable.FIELD_REPLYTONO));
								user = (UserTable) DataOperation.queryTable(UserTable.TABLE_NAME, UserTable.CONTENTID, eList.get(0).getField(EnquiryTable.FIELD_USERNO)).get(0);
							}
						}
						
						//获取对方用户的头像URL
						if(user.getAccessaryFileUrlList()!=null && user.getAccessaryFileUrlList().size()!=0 )
						{
							headIconUrl = user.getAccessaryFileUrlList().get(0);
						}
						//获取对方用户的昵称
						userName = user.getField(UserTable.FIELD_USERNAME);
						
						return TASK_INITVIEW_RESULT_SUCCESS;
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return TASK_INITVIEW_RESULT_ERROR;
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
				//更新通知ListView的Item
				case TASK_INITVIEW_RESULT_SUCCESS:
				{
					bitmapUtils.display(iv_userHead, headIconUrl);
					tv_userName.setText(userName);
				}break;
				
				case TASK_INITVIEW_RESULT_ERROR:
				{
					
				}break;
			}
		}
	}
}
