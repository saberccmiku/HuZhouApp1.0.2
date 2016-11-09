package com.geekband.huzhouapp.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.geekband.huzhouapp.R;

public class PagingLoadListView extends ListView
{
	private View footer;
	private ViewGroup vg_load;
	private ViewGroup vg_loading;

	public PagingLoadListView(Context context)
	{
		super(context);
		initView(context);
	}

	public PagingLoadListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public PagingLoadListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context)
	{
		footer = LayoutInflater.from(context).inflate(R.layout.layout_listviewfooter, null);
		vg_load = (ViewGroup) footer.findViewById(R.id.vg_footer_load);
		vg_loading = (ViewGroup) footer.findViewById(R.id.vg_footer_loading);

		vg_load.setVisibility(View.VISIBLE);
		vg_loading.setVisibility(View.GONE);

		vg_load.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				loadBegin();
				if(onLoadListener!=null) onLoadListener.onLoad();
			}
		});

		this.addFooterView(footer);
	}

	public void loadBegin()
	{
		vg_load.setVisibility(View.GONE);
		vg_loading.setVisibility(View.VISIBLE);
	}

	public void loadComplete()
	{
		vg_load.setVisibility(View.VISIBLE);
		vg_loading.setVisibility(View.GONE);
	}

	private OnLoadListener onLoadListener;
	public void setOnLoadListener(OnLoadListener onLoadListener)
	{
		this.onLoadListener = onLoadListener;
	}
	public interface OnLoadListener
	{
		void onLoad();
	}
}
