package com.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chat.adapter.pojo.Message;
import com.database.pojo.ReplyTable;
import com.database.pojo.UserTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

public class MessageListAdapter extends BaseAdapter
{
	private Context context;
	private List<Message> messageList;
	private BitmapUtils bitmapUtils;
	
	public MessageListAdapter(Context context, ListView listView)
	{
		this.context = context;
		this.messageList = new ArrayList<>();
		bitmapUtils = BitmapHelper.getBitmapUtils(context, listView, R.drawable.head_default, R.drawable.head_default);
	}
	
	public void showProgress(int position)
	{
		
	}
	
	public List<Message> getMessageList()
	{
		return this.messageList;
	}
	
	@Override
	public int getCount()
	{
		return messageList.size();
	}

	@Override
	public Message getItem(int position)
	{
		return messageList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}
	
	@Override
	public int getViewTypeCount()
	{
		return 2;
	}
	
	@Override
	public int getItemViewType(int position)
	{
		return getItem(position).getMessageType();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder vh;
		int messageType = getItemViewType(position);
		
		if(convertView == null)
		{
			vh = new ViewHolder();
			switch(messageType)
			{
				case Message.TYPE_SEND_TEXT:
				{
					convertView = LayoutInflater.from(context).inflate(R.layout.item_messagedetail_sendmessage, parent, false);
					vh.iv_sendMessage_userHead = (ImageView) convertView.findViewById(R.id.iv_sendmessage_userHead);
					vh.iv_sendMessage_userName = (TextView) convertView.findViewById(R.id.tv_sendmessage_userName);
					vh.tv_sendMessage_content = (TextView) convertView.findViewById(R.id.tv_sendmessage_content);
					vh.tv_sendMessage_time = (TextView) convertView.findViewById(R.id.tv_sendmessage_time);
					vh.pb_sendMessage_progress = (ProgressBar) convertView.findViewById(R.id.pb_sendmessage_progress);
				}break;
				
				case Message.TYPE_RECEIVE_TEXT:
				{
					convertView = LayoutInflater.from(context).inflate(R.layout.item_messagedetail_receivemessage, parent, false);
					vh.iv_receiveMessage_userHead = (ImageView) convertView.findViewById(R.id.iv_receivemessage_userHead);
					vh.tv_receiveMessage_userName = (TextView) convertView.findViewById(R.id.tv_receivemessage_userName);
					vh.tv_receiveMessage_content = (TextView) convertView.findViewById(R.id.tv_receivemessage_content);
					vh.tv_receiveMessage_time = (TextView) convertView.findViewById(R.id.tv_receivemessage_time);
				}break;
			}
			convertView.setTag(vh);
		}
		else
		{
			vh = (ViewHolder) convertView.getTag();
		}
		
		switch(messageType)
		{
			case Message.TYPE_SEND_TEXT:
			{
				String headIconUrl = "";
				if(getItem(position).getMessageSenderInfo().getAccessaryFileUrlList()!=null && getItem(position).getMessageSenderInfo().getAccessaryFileUrlList().size()!=0 )
				{
					headIconUrl = getItem(position).getMessageSenderInfo().getAccessaryFileUrlList().get(0);
				}
				bitmapUtils.display(vh.iv_sendMessage_userHead, headIconUrl);
				
				vh.iv_sendMessage_userName.setText(getItem(position).getMessageSenderInfo().getField(UserTable.FIELD_REALNAME));
				vh.tv_sendMessage_content.setText(getItem(position).getMessageInfo().getField(ReplyTable.FIELD_CONTENT));
				vh.tv_sendMessage_time.setText(getItem(position).getMessageInfo().getField(ReplyTable.FIELD_T_TIME));
				vh.pb_sendMessage_progress.setOnClickListener(null);
			}break;
			
			case Message.TYPE_RECEIVE_TEXT:
			{
				String headIconUrl = "";
				if(getItem(position).getMessageSenderInfo().getAccessaryFileUrlList()!=null && getItem(position).getMessageSenderInfo().getAccessaryFileUrlList().size()!=0 )
				{
					headIconUrl = getItem(position).getMessageSenderInfo().getAccessaryFileUrlList().get(0);
				}
				bitmapUtils.display(vh.iv_receiveMessage_userHead, headIconUrl);
				
				vh.tv_receiveMessage_userName.setText(getItem(position).getMessageSenderInfo().getField(UserTable.FIELD_REALNAME));
				vh.tv_receiveMessage_content.setText(getItem(position).getMessageInfo().getField(ReplyTable.FIELD_CONTENT));
				vh.tv_receiveMessage_time.setText(getItem(position).getMessageInfo().getField(ReplyTable.FIELD_T_TIME));
			}break;
		}
		
		return convertView;
	}
	
	private class ViewHolder
	{
		private ImageView iv_sendMessage_userHead;
		private TextView tv_sendMessage_content;
		private TextView tv_sendMessage_time;
		private TextView iv_sendMessage_userName;
		private ProgressBar pb_sendMessage_progress;
		
		private ImageView iv_receiveMessage_userHead;
		private TextView tv_receiveMessage_userName;
		private TextView tv_receiveMessage_content;
		private TextView tv_receiveMessage_time;
	}
}
