package com.geekband.huzhouapp.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.utils.DensityUtil;

public class RefreshButton extends LinearLayout implements View.OnClickListener
{
	private ImageView icon;
	
	private boolean isComplete;
	private boolean isStopAnimation;
	
	private final int ICON_WIDTH = 50; //图标的宽度，单位dp
	private final int ICON_HEIGHT = 50; //图标的高度，单位dp
	
	public RefreshButton(Context context)
	{
		super(context);
		init(context);
	}
	
	public RefreshButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}
	
	public RefreshButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context)
	{
		icon = new ImageView(context);
		icon.setLayoutParams(new LayoutParams(DensityUtil.dipToPx(getContext(), ICON_WIDTH), DensityUtil.dipToPx(getContext(), ICON_HEIGHT)));
		icon.setImageResource(R.drawable.refresh);
		addView(icon);
		
		isComplete = true;
		
		setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		beginRefresh();
	}
	
	private void startAnimation()
	{
		final RotateAnimation rotateAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(500);
		rotateAnimation.setFillAfter(true);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		rotateAnimation.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation)
			{
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation)
			{
				if(!isStopAnimation) icon.startAnimation(rotateAnimation);
			}
		});
		icon.startAnimation(rotateAnimation);
		isStopAnimation = false;
	}
	
	private void stopAnimation()
	{
		icon.clearAnimation();
		isStopAnimation = true;
	}
	
	/**
	 * 开始刷新；
	 */
	public void beginRefresh()
	{
		//停止了才能开始
		if(isComplete) 
		{
			if(refreshListener!=null) refreshListener.onRefresh();
			startAnimation();
			isComplete = false;
		}
	}
	
	/**
	 * 刷新完成；
	 */
	public void refreshComplete()
	{
		//开始了才能停止
		//调用过beginRefresh方法启动开始刷新任务后，才能使用refreshComplete方法停止刷新任务
		//因为该方法可能被外部没有启动beginRefresh任务的情况下调用，这种情况如果不做判断会导致出错
		if(!isComplete)
		{
			stopAnimation();
			isComplete = true;
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void setStytle(Drawable background, Drawable icon)
	{
		setBackground(background);
		this.icon.setImageDrawable(icon);
	}
	
	private RefreshListener refreshListener;
	public interface RefreshListener
	{
		public void onRefresh();
	}
	
	public void setRefreshListener(RefreshListener refreshListener)
	{
		this.refreshListener = refreshListener;
	}
}
