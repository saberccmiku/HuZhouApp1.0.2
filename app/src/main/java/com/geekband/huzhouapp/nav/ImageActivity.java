package com.geekband.huzhouapp.nav;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.database.dto.DataOperation;
import com.database.pojo.AlbumTable;
import com.database.pojo.Document;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.baseadapter.CommonAdapter;
import com.geekband.huzhouapp.baseadapter.ViewHolder;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.FileUtil;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/24
 */
public class ImageActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private ArrayList<String> mImageList;
    // private ImageView mTop_imageView;
    private BitmapUtils mBitmapUtils;
    private CommonAdapter<String> mCommonAdapter;
    private AlbumTable mAlbumTable;
    private int mSelectedPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iamge);
        mImageList = new ArrayList<>();
        //mTop_imageView = (ImageView) findViewById(R.id.top_imageView);
        mBitmapUtils = BitmapHelper.getBitmapUtils(this, null, 0, 0);
        new LoadImageTask().execute();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent mIntent = new Intent(ImageActivity.this, AlbumActivity.class);
        if (mImageList != null) {
            mIntent.setAction("albumList");
            mIntent.putStringArrayListExtra("albumList", mImageList);
            mIntent.putExtra("position", position);
            startActivity(mIntent);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mSelectedPosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
        builder.setTitle("删除图片");
        builder.setMessage("删除的图片不可恢复，您确定删除？");
        builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeleteAlbumTask().execute();
            }
        });
        builder.setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();

        return true;
    }

    class LoadImageTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            Intent intent = getIntent();
            String contentId = intent.getStringExtra(Constants.IMAGE_CONTENT_ID);
            //noinspection unchecked
            ArrayList<AlbumTable> albumTables = (ArrayList<AlbumTable>) DataOperation.queryTable(AlbumTable.TABLE_NAME, AlbumTable.CONTENTID, contentId);
            if (albumTables != null && albumTables.size() != 0) {
                mAlbumTable = albumTables.get(0);
                mImageList = (ArrayList<String>) mAlbumTable.getAccessaryFileUrlList();
                return 1;
            }
            return 2;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            initView();
        }
    }

    private void initView() {

        GridView gridView_camera = (GridView) findViewById(R.id.gridView_image);
        mBitmapUtils = BitmapHelper.getBitmapUtils(this, gridView_camera, 0, 0);
        mCommonAdapter = new CommonAdapter<String>(this, mImageList, R.layout.item_camera_list) {
            @Override
            public void convert(ViewHolder viewHolder, String item) {
                mBitmapUtils.display(viewHolder.getView(R.id.imageView_camera), item);
            }
        };
        gridView_camera.setAdapter(mCommonAdapter);
        gridView_camera.setOnItemClickListener(this);
        gridView_camera.setOnItemLongClickListener(this);

    }

        private class DeleteAlbumTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            mImageList.remove(mSelectedPosition);
            mCommonAdapter.notifyDataSetChanged();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                ArrayList<String> localFileUrls = FileUtil.loadImageByUrl(Constants.GALLERY_DIRECTORY_NAME, mImageList);
                //更新服务器数据
                if (insertPic(mAlbumTable, localFileUrls)) {
                    return 1;//服务器更新成功
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //删除本独文件缓存
                FileUtil.clearFolder(Constants.GALLERY_DIRECTORY_NAME);
            }
            return 2;//服务器更新失败
        }


        @Override
        protected void onPostExecute(Integer integer) {
            if (integer == 2) {
                Toast.makeText(ImageActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean insertPic(AlbumTable albumTable, ArrayList<String> mPathList) {
        if (mPathList != null && mPathList.size() != 0) {
            Document[] documents = new Document[mPathList.size()];
            for (int i = 0; i < mPathList.size(); i++) {
//                Log.i("新添加的照片mPathList:", mPathList.get(i));
                documents[i] = new Document(mPathList.get(i));
            }
            return DataOperation.insertOrUpdateTable(albumTable, documents);
        } else if (mPathList != null && mPathList.size() == 0) {
            AlbumTable newAlbum = new AlbumTable();
            String userId = albumTable.getField(AlbumTable.FIELD_USERID);
            String albumName = albumTable.getField(AlbumTable.FIELD_NAME);
            String dateTime = albumTable.getField(AlbumTable.FIELD_DATETIME);
            String description = albumTable.getField(AlbumTable.FIELD_DESCRIPTION);
            newAlbum.putField(AlbumTable.FIELD_USERID, userId);
            newAlbum.putField(AlbumTable.FIELD_NAME, albumName);
            newAlbum.putField(AlbumTable.FIELD_DATETIME, dateTime);
            newAlbum.putField(AlbumTable.FIELD_DESCRIPTION, description);
            return DataOperation.insertOrUpdateTable(newAlbum) && DataOperation.deleteTable(albumTable);
        }
        return false;
    }

}
