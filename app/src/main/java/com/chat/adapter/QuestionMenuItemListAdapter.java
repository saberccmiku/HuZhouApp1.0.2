package com.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.adapter.pojo.QuestionMenuItem;
import com.geekband.huzhouapp.R;

import java.util.ArrayList;
import java.util.List;

public class QuestionMenuItemListAdapter extends BaseAdapter
{
	private Context context;
	private List<QuestionMenuItem> questionMenuItemList;
	
	public QuestionMenuItemListAdapter(Context context)
	{
		this.context = context;
		questionMenuItemList = new ArrayList<>();
	}
	
	public List<QuestionMenuItem> getQuestionMenuItemList()
	{
		return questionMenuItemList;
	}
	
	public void addItem(QuestionMenuItem questionMenuItem)
	{
		questionMenuItemList.add(questionMenuItem);
		notifyDataSetChanged();
	}
	
	public void addItemList(List<QuestionMenuItem> questionMenuItemList)
	{
		this.questionMenuItemList.addAll(questionMenuItemList);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount()
	{
		return questionMenuItemList.size();
	}

	@Override
	public QuestionMenuItem getItem(int position)
	{
		return questionMenuItemList.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_questionmenu_menuitem, parent, false);
			vh.iv_itemIcon = (ImageView) convertView.findViewById(R.id.iv_questionmenu_itemIcon);
			vh.tv_itemTitle = (TextView) convertView.findViewById(R.id.tv_questionmenu_itemTitle);
			convertView.setTag(vh);
		}
		else
		{
			vh = (ViewHolder) convertView.getTag();
		}
		
		vh.iv_itemIcon.setImageResource(getItem(position).getIconId());
		vh.tv_itemTitle.setText(getItem(position).getTitle());
		
		return convertView;
	}
	
	private class ViewHolder
	{
		private ImageView iv_itemIcon;
		private TextView tv_itemTitle;
	}
}
