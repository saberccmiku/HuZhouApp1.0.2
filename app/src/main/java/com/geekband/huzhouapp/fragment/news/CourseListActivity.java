package com.geekband.huzhouapp.fragment.news;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.geekband.huzhouapp.vo.pojo.CourseTable;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.activity.BaseActivity;
import com.geekband.huzhouapp.baseadapter.CommonAdapter;
import com.geekband.huzhouapp.baseadapter.ViewHolder;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.DataUtils;
import com.geekband.huzhouapp.vo.CourseInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/29
 */
public class CourseListActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ProgressBar mCourse_progress;
    private ListView mCourse_listView;
    private ArrayList<CourseInfo> mMyCourseList;
    private Button mCourseList_backBtn;
    private TextView mCourse_center;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        mCourse_listView = (ListView) findViewById(R.id.course_listView);
        mCourse_progress = (ProgressBar) findViewById(R.id.course_progress);
        mCourseList_backBtn = (Button) findViewById(R.id.courseList_backBtn);
        mCourse_center = (TextView) findViewById(R.id.course_center);


        mCourseList_backBtn.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent.getAction().equals("allCourse")) {
            mCourse_center.setText("全部课程");
            new AllCoursesTask().execute();
        } else if (intent.getAction().equals("myCourse")) {
            mCourse_center.setText("已选课程");
            new MyCourseTask().execute();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        //加载网络数据
//        new NetTask().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CourseInfo courseInfo = mMyCourseList.get(position);
        Intent intent = new Intent(this, CourseContentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.COURSE_CONTENT, courseInfo);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.courseList_backBtn:
                //startActivity(new Intent(this, MainActivity.class));
                this.finish();
                break;
        }
    }


    private class AllCoursesTask extends AsyncTask<String, Integer, Integer> {
        ArrayList<CourseInfo> allCoursesList;

        @Override
        protected void onPreExecute() {
            mCourse_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            ArrayList<CourseTable> courseTables = DataUtils.getAllCourses();
            allCoursesList = new ArrayList<>();
            if (courseTables != null && courseTables.size() != 0) {
                for (int i = 0;i<courseTables.size();i++) {
                    CourseInfo courseInfo = new CourseInfo();

                    String title = courseTables.get(i).getField(CourseTable.FIELD_COURSENAME);
                    //选修必修
                    String type = courseTables.get(i).getField(CourseTable.FIELD_COURSETYPE);
                    //bs.必修视频 xs.选修视频 bw.必修文档 xw.选修文档
                    String typeStr = "";
                    switch (type){
                        case Constants.BS:
                            typeStr = Constants.BS_STR;
                            break;
                        case Constants.XS:
                            typeStr = Constants.XS_STR;
                            break;
                        case Constants.BW:
                            typeStr = Constants.BW_STR;
                            break;
                        case Constants.XW:
                            typeStr = Constants.XW_STR;
                            break;
                    }

                    //课程简介
                    String intro = courseTables.get(i).getField(CourseTable.FIELD_COURSEINTRO);
                    //详细内容
                    String point = courseTables.get(i).getField(CourseTable.FIELD_POINT);
                    //学习时长
                    String time = courseTables.get(i).getField(CourseTable.FIELD_NEEDTIME);
                    //详细内容
                    String detailed = courseTables.get(i).getField(CourseTable.FIELD_DETAILED);
                    courseInfo.setId(i+1);
                    courseInfo.setTitle(title);
                    courseInfo.setType(typeStr);
                    courseInfo.setIntro(intro);
                    courseInfo.setPoint(point);
                    courseInfo.setTime(time);
                    courseInfo.setDetailed(detailed);

                    allCoursesList.add(courseInfo);
                }
            }


            if (mMyCourseList != null) {
                mMyCourseList.clear();
                mMyCourseList .addAll(allCoursesList) ;
            }else {
                mMyCourseList = allCoursesList;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mCourse_progress.setVisibility(View.GONE);
            if (mMyCourseList!=null){
                initCourseList();
            }
        }
    }

    private class MyCourseTask extends AsyncTask<String, Integer, Integer> {
        ArrayList<CourseInfo> courseInfoList;
        @Override
        protected void onPreExecute() {
            mCourse_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            courseInfoList = DataUtils.bindCourseInfo();
            if (mMyCourseList!=null){
                mMyCourseList.clear();
                mMyCourseList.addAll(courseInfoList);
            }else {
                mMyCourseList = courseInfoList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mCourse_progress.setVisibility(View.GONE);
            if (mMyCourseList!=null) {
                initCourseList();
            }
        }
    }


    private void initCourseList() {
        mCourse_listView.setAdapter(new CommonAdapter<CourseInfo>(this, mMyCourseList, R.layout.item_course_list) {
            @Override
            public void convert(ViewHolder viewHolder, CourseInfo item) {
                viewHolder.setText(R.id.course_list_id, String.valueOf(item.getId()));
                viewHolder.setText(R.id.course_list_title, item.getTitle());
                viewHolder.setText(R.id.course_list_type, item.getType());
                viewHolder.setText(R.id.course_list_time, item.getTime());
            }
        });
        mCourse_listView.setOnItemClickListener(this);
    }
}
