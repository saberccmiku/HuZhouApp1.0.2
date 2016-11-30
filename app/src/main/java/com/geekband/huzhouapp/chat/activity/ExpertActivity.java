package com.geekband.huzhouapp.chat.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.geekband.huzhouapp.chat.fragment.ExpertFragment;
import com.geekband.huzhouapp.utils.DataOperation;
import com.geekband.huzhouapp.vo.pojo.CategoriesTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.utils.Constants;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;


public class ExpertActivity extends FragmentActivity implements View.OnClickListener
{
	private ImageButton btn_back;
	private ViewGroup vg_content;
	private TabPageIndicator tpi_categories;
	private ViewPager vp_pager;
	private ViewGroup vg_progress;
	private ViewGroup vg_errorTip;
	private ViewGroup vg_emptyTip;
	private Button btn_refresh;

	private ExpertPagerAdapter expertPagerAdapter;
	private Bundle args;

	@Override
	protected void onCreate(Bundle saveInstanceState)
	{
		super.onCreate(saveInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(getActionBar()!=null) getActionBar().hide();
		setContentView(R.layout.activity_expert);
		findView();
		initVar();
		initView();
		initListener();

		runAsyncTask(AsyncDataLoader.TASK_INITCATEGORY);


	}

	private void findView()
	{
		btn_back = (ImageButton) findViewById(R.id.btn_expert_back);
		vg_content = (ViewGroup) findViewById(R.id.vg_expert_content);
		tpi_categories = (TabPageIndicator) findViewById(R.id.tpi_expert_categories);
		vp_pager = (ViewPager) findViewById(R.id.vp_expert_pager);
		vg_progress = (ViewGroup) findViewById(R.id.vg_expert_progress);
		vg_errorTip = (ViewGroup) findViewById(R.id.vg_expert_errorTip);
		vg_emptyTip = (ViewGroup) findViewById(R.id.vg_expert_emptyTip);
		btn_refresh = (Button) findViewById(R.id.btn_expert_refresh);
	}

	private void initView()
	{
		vp_pager.setAdapter(expertPagerAdapter);
		vp_pager.setOffscreenPageLimit(0);
		tpi_categories.setViewPager(vp_pager);

		vg_content.setVisibility(View.GONE);
		vg_progress.setVisibility(View.GONE);
		vg_errorTip.setVisibility(View.GONE);
		vg_emptyTip.setVisibility(View.GONE);

	}

	private void initVar()
	{
		expertPagerAdapter = new ExpertPagerAdapter(getSupportFragmentManager());

		args = new Bundle();
	}

	private void initListener()
	{
		btn_back.setOnClickListener(this);
		btn_refresh.setOnClickListener(this);
	}

	public Bundle getBundle()
	{
		return args;
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btn_expert_back:
			{
				finish();
			}break;

			case R.id.btn_expert_refresh:
			{
				runAsyncTask(AsyncDataLoader.TASK_INITCATEGORY);
			}break;
		}
	}

	private void runAsyncTask(int task, Object... params)
	{
		new AsyncDataLoader(task, params).execute();
	}

	private static class ExpertPagerAdapter extends FragmentPagerAdapter
	{
		private ArrayList<CategoriesTable> expertCategoryList = new ArrayList<>();

		public ExpertPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		public void addAll(ArrayList<CategoriesTable> expertCategoryList)
		{
			this.expertCategoryList.clear();
			CategoriesTable category = new CategoriesTable();
			category.putField(CategoriesTable.FIELD_NAME, "全部");
			this.expertCategoryList.add(category);
			this.expertCategoryList.addAll(expertCategoryList);
		}

		@Override
		public int getCount()
		{
			return expertCategoryList.size();
		}

		@Override
		public Fragment getItem(int position)
		{
			return new ExpertFragment(expertCategoryList.get(position));
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return expertCategoryList.get(position).getField(CategoriesTable.FIELD_NAME);
		}
	}

	private class AsyncDataLoader extends AsyncTask<Object, Integer, Integer>
	{
		public static final int TASK_INITCATEGORY = 1;
		public static final int TASK_INITCATEGORY_RESULT_SUCCESS = 1;
		public static final int TASK_INITCATEGORY_RESULT_ERROR = -1;

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
				case TASK_INITCATEGORY:
				{
					vg_content.setVisibility(View.GONE);
					vg_progress.setVisibility(View.VISIBLE);
					vg_errorTip.setVisibility(View.GONE);
					vg_emptyTip.setVisibility(View.GONE);
				}break;
			}
		}

		ArrayList<CategoriesTable> expertCategoryList = new ArrayList<>();
		@Override
		protected Integer doInBackground(Object... params)
		{
			switch (task)
			{
				case TASK_INITCATEGORY:
				{
					try
					{
						//noinspection unchecked
						expertCategoryList = (ArrayList<CategoriesTable>) DataOperation.queryTable(CategoriesTable.TABLE_NAME, CategoriesTable.FIELD_PARENTID, Constants.WXZJ_CATEGORIES_ID);

						return TASK_INITCATEGORY_RESULT_SUCCESS;
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return TASK_INITCATEGORY_RESULT_ERROR;
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
				case TASK_INITCATEGORY_RESULT_SUCCESS:
				{
					Log.i("TAG",expertCategoryList.toString());
					expertPagerAdapter.addAll(expertCategoryList);
					expertPagerAdapter.notifyDataSetChanged();
					tpi_categories.notifyDataSetChanged();
					expertCategoryList.clear();

					Intent intent = getIntent();
					int position = intent.getIntExtra("wxzj_class",0);
					vp_pager.setCurrentItem(position);

					if(expertPagerAdapter.getCount()!=0)
					{
						vg_content.setVisibility(View.VISIBLE);
						vg_progress.setVisibility(View.GONE);
						vg_errorTip.setVisibility(View.GONE);
						vg_emptyTip.setVisibility(View.GONE);
					}
					else
					{
						vg_content.setVisibility(View.GONE);
						vg_progress.setVisibility(View.GONE);
						vg_errorTip.setVisibility(View.GONE);
						vg_emptyTip.setVisibility(View.VISIBLE);
					}
				}break;


				case TASK_INITCATEGORY_RESULT_ERROR:
				{
					vg_content.setVisibility(View.GONE);
					vg_progress.setVisibility(View.GONE);
					vg_errorTip.setVisibility(View.VISIBLE);
					vg_emptyTip.setVisibility(View.GONE);
				}break;
			}
		}
	}
}
