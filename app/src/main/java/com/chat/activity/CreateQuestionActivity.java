package com.chat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.adapter.pojo.Question;
import com.database.dto.DataOperation;
import com.database.pojo.CategoriesTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.DataOperationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateQuestionActivity extends Activity implements View.OnClickListener
{
	private ImageButton btn_back;
	private TextView tv_title;
	private EditText et_content;
	private Button btn_submit;

	private int selectedCategory;
	private ArrayList<CategoriesTable> categories;
	private Spinner mSpinner;

	@Override
	protected void onCreate(Bundle saveInstanceState)
	{
		super.onCreate(saveInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_createquestion);
		findView();
		initVar();
		initView();
		initListener();
		runAsyncTask(AsyncDataLoader.TASK_INITCATEGORY);
	}
	
	private void findView()
	{
		btn_back = (ImageButton) findViewById(R.id.btn_createquestion_back);
		tv_title = (TextView) findViewById(R.id.tv_createquestion_title);
		et_content = (EditText) findViewById(R.id.et_createQuestion_content);
		btn_submit = (Button) findViewById(R.id.btn_createquestion_submin);
		mSpinner = (Spinner) findViewById(R.id.spinner_question_type);
	}
	
	private void initView()
	{
		tv_title.setText("发布新问题");

		categories = new ArrayList<>();
		mSpinner.setBackgroundResource(R.drawable.abc_spinner_ab_pressed_holo_light);
	}
	
	private void initVar()
	{
		selectedCategory = 0;
	}
	
	private void initListener()
	{
		btn_back.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
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
			case R.id.btn_createquestion_back:
			{
				finish();
			}break;

			case R.id.btn_createquestion_submin:
			{
				//问题内容
				String content = et_content.getText().toString();
				//问题类型
				String type = mSpinner.getSelectedItem().toString();

				if(content!=null && !"".equals(content))
				{
					runAsyncTask(AsyncDataLoader.TASK_CREATEQUESTION, content,type);
				}
				else
				{
					Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
				}
			}break;

		}
	}
	
	private class AsyncDataLoader extends AsyncTask<Object, Integer, Integer>
	{
		private static final int TASK_CREATEQUESTION = 1;
		private static final int TASK_CREATEQUESTION_RESULT_SUCCESS = 1;
		private static final int TASK_CREATEQUESTION_RESULT_ERROR = -1;
		public static final int TASK_INITCATEGORY = 2;
		public static final int TASK_INITCATEGORY_RESULT_SUCCESS = 2;
		public static final int TASK_INITCATEGORY_RESULT_ERROR = -2;
		
		private int task;
		private Object[] params;
		private ProgressDialog mPd;

		public AsyncDataLoader(int task, Object...params)
		{
			this.task = task;
			this.params = params;
		}

		@Override
		protected void onPreExecute()
		{
			btn_submit.setEnabled(false);
			switch (task)
			{
				case TASK_CREATEQUESTION: //显示问题发布进度条对话框，并执行问题发布任务
				{
					mPd = ProgressDialog.show(CreateQuestionActivity.this, null, "正在上上传...");
					
				}break;

				case TASK_INITCATEGORY:
				{

				}break;
			}
		}
		
		Question question;
		@Override
		protected Integer doInBackground(Object... params)
		{
			switch (task)
			{
				case TASK_CREATEQUESTION:
				{
					try
					{
						//问题的内容
						String content = (String) this.params[0];
						//问题类型
						String type = (String) this.params[1];
						
						question = DataOperationHelper.uploadQuestion(
								DataOperationHelper.queryCurrentUser(),
								content,
								new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis())),
								type);
						
						if(question!=null) return TASK_CREATEQUESTION_RESULT_SUCCESS;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
					return TASK_CREATEQUESTION_RESULT_ERROR;
				}

				case TASK_INITCATEGORY:
				{
					try
					{
						categories = (ArrayList<CategoriesTable>) DataOperation.queryTable(CategoriesTable.TABLE_NAME, CategoriesTable.FIELD_PARENTID, Constants.WXZJ_CATEGORIES_ID);

						return TASK_INITCATEGORY_RESULT_SUCCESS;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}

					return TASK_CREATEQUESTION_RESULT_ERROR;
				}
			}
			
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer taskResult)
		{

			switch (taskResult)
			{
				case TASK_CREATEQUESTION_RESULT_SUCCESS:
				{
					mPd.setMessage("上传成功！");
					mPd.dismiss();
					et_content.setText(null);
					//问题创建成功后，关闭当前Activity，并跳转到新创建的问题的详情页面
					Intent intent = new Intent();
					intent.setClass(CreateQuestionActivity.this, QuestionDetailActivity.class);
					intent.putExtra(QuestionDetailActivity.ARGS_QUESTION, question);
					startActivity(intent);
					finish();
				}break;
				
				case TASK_CREATEQUESTION_RESULT_ERROR:
				{
					mPd.setMessage("上传失败！");
					mPd.dismiss();
				}break;

				case TASK_INITCATEGORY_RESULT_SUCCESS:
				{
					String[] categoryArr = new String[categories.size()];
					for (int i=0;i<categories.size();i++)
					{
						categoryArr[i] = categories.get(i).getField(CategoriesTable.FIELD_NAME);
					}
					ArrayAdapter arrayAdapter = new ArrayAdapter<>(CreateQuestionActivity.this, android.R.layout.simple_spinner_item, categoryArr);
					arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					mSpinner.setAdapter(arrayAdapter);
					btn_submit.setEnabled(true);
				}break;

				case TASK_INITCATEGORY_RESULT_ERROR:
				{

				}break;
			}
		}
	}
}
