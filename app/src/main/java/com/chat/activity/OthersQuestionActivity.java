package com.chat.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.chat.fragment.OthersQuestionFragment;
import com.database.dto.DataOperation;
import com.database.pojo.CategoriesTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.activity.BaseActivity;
import com.geekband.huzhouapp.utils.Constants;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

public class OthersQuestionActivity extends BaseActivity implements View.OnClickListener
{
	private ImageButton btn_back;
	private ViewGroup vg_content;
	private TabPageIndicator tpi_categories;
	private ViewPager vp_pager;
	private ViewGroup vg_progress;
	private ViewGroup vg_errorTip;
	private ViewGroup vg_emptyTip;
	private Button btn_refresh;
	private Button btn_createQuestion;

	private OthersQuestionPagerAdapter othersQuestionPagerAdapter;
	private Bundle args;

	@Override
	protected void onCreate(Bundle saveInstanceState)
	{
		super.onCreate(saveInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_othersquestion);
		findView();
		initVar();
		initView();
		initListener();
		runAsyncTask(AsyncDataLoader.TASK_INITCATEGORY);
	}

	private void findView()
	{
		btn_back = (ImageButton) findViewById(R.id.btn_othersquestion_back);
		vg_content = (ViewGroup) findViewById(R.id.vg_othersquestion_content);
		tpi_categories = (TabPageIndicator) findViewById(R.id.tpi_othersquestion_categories);
		vp_pager = (ViewPager) findViewById(R.id.vp_othersquestion_pager);
		vg_progress = (ViewGroup) findViewById(R.id.vg_othersquestion_progress);
		vg_errorTip = (ViewGroup) findViewById(R.id.vg_othersquestion_errorTip);
		vg_emptyTip = (ViewGroup) findViewById(R.id.vg_othersquestion_emptyTip);
		btn_refresh = (Button) findViewById(R.id.btn_othersquestion_refresh);
		btn_createQuestion = (Button) findViewById(R.id.btn_othersquestion_createQuestion);
	}

	private void initView()
	{
		vp_pager.setAdapter(othersQuestionPagerAdapter);
		vp_pager.setOffscreenPageLimit(0);
		tpi_categories.setViewPager(vp_pager);

		vg_content.setVisibility(View.GONE);
		vg_progress.setVisibility(View.GONE);
		vg_errorTip.setVisibility(View.GONE);
		vg_emptyTip.setVisibility(View.GONE);
	}

	private void initVar()
	{
		othersQuestionPagerAdapter = new OthersQuestionPagerAdapter(getSupportFragmentManager());

		args = new Bundle();
	}

	private void initListener()
	{
		btn_back.setOnClickListener(this);
		btn_refresh.setOnClickListener(this);
		btn_createQuestion.setOnClickListener(this);
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
			case R.id.btn_othersquestion_back:
			{
				finish();
			}break;

			case R.id.btn_othersquestion_refresh:
			{
				runAsyncTask(AsyncDataLoader.TASK_INITCATEGORY);
			}break;

			case R.id.btn_othersquestion_createQuestion: //提问
			{
				Intent intent = new Intent(this, CreateQuestionActivity.class);
				startActivity(intent);
			}break;
		}
	}

	private void runAsyncTask(int task, Object... params)
	{
		new AsyncDataLoader(task, params).execute();
	}

	private static class OthersQuestionPagerAdapter extends FragmentPagerAdapter
	{
		private ArrayList<CategoriesTable> questionCategoryList = new ArrayList<>();

		public OthersQuestionPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		public void addAll(ArrayList<CategoriesTable> expertCategoryList)
		{
			this.questionCategoryList.clear();
			CategoriesTable category = new CategoriesTable();
			category.putField(CategoriesTable.FIELD_NAME, "全部");
			this.questionCategoryList.add(category);
			this.questionCategoryList.addAll(expertCategoryList);
		}

		@Override
		public int getCount()
		{
			return questionCategoryList.size();
		}

		@Override
		public Fragment getItem(int position)
		{
			return new OthersQuestionFragment(questionCategoryList.get(position));
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return questionCategoryList.get(position).getField(CategoriesTable.FIELD_NAME);
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
					othersQuestionPagerAdapter.addAll(expertCategoryList);
					othersQuestionPagerAdapter.notifyDataSetChanged();
					tpi_categories.notifyDataSetChanged();
					expertCategoryList.clear();

					if(othersQuestionPagerAdapter.getCount()!=0)
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
