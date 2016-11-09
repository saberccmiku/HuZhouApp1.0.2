package com.geekband.huzhouapp.nav;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.database.dto.DataOperation;
import com.database.pojo.AlbumTable;
import com.database.pojo.Document;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.application.MyApplication;
import com.geekband.huzhouapp.baseadapter.CommonAdapter;
import com.geekband.huzhouapp.baseadapter.ViewHolder;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.FileUtil;
import com.geekband.huzhouapp.utils.FileUtils;
import com.geekband.huzhouapp.utils.SelectPicPopupWindow;
import com.geekband.huzhouapp.utils.UriToPathUtils;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/16
 */
public class GalleryActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private TextView mGallery_back_textBtn;
    private ImageButton mCreate_gallery_btn;
    private RelativeLayout mGallery_layout;
    private ImageButton mLocal_gallery_imageView;
    private ImageButton mUp_photo_imageView;
    private ImageButton mCreate_imageView;
    private boolean isCreate;
    private ProgressBar mGallery_progress;
    private GridView mGallery_gridView;
    private SelectPicPopupWindow mPicPopupWindow;
    private Uri photoUri;
    private ProgressDialog mPd;
    /**
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /**
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    private LinearLayout mSelected_layout;
    private Button mUp_btn;
    private ArrayList<AlbumTable> mAlbumInfoList;
    private ArrayList<String> mImageThumbUrls;

    private final String IMAGE_TYPE = "image/*";
    private final int IMAGE_CODE = 0;   //这里的IMAGE_CODE是自己任意定义的
    private BitmapUtils mBitmapUtils;
    private Spinner mSpinner_select_gallery;
    private ArrayList<String> mGalleryList;
    private GridView mSelected_image;
    private ArrayList<String> mPathList;
    private CommonAdapter<String> mSelectViewAdapter;
    private GridView mSelected_image_grid;
    private final String IMAGE_TAG = "addMoreImage";
    private ArrayAdapter<String> mArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        isCreate = true;
        mGalleryList = new ArrayList<>();
        mPathList = new ArrayList<>();
        mBitmapUtils = BitmapHelper.getBitmapUtils(this, null, 0, 0);
        //控件信息
        getWidget();
        //加载相册
        new LocalTask().execute();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPd != null) {
            mPd.dismiss();
        }
    }

    private void initAlbum() {
        mGallery_gridView.setAdapter(
                new CommonAdapter<AlbumTable>(this, mAlbumInfoList, R.layout.item_album) {
                    @Override
                    public void convert(ViewHolder viewHolder, AlbumTable item) {
                        viewHolder.setText(R.id.item_albumName, item.getField(AlbumTable.FIELD_NAME));
                        viewHolder.setText(R.id.item_albumCount, String.valueOf(item.getAccessaryFileUrlList().size()));
                        ImageView imageView = viewHolder.getView(R.id.item_albumImage);
                        if (item.getAccessaryFileUrlList() != null && item.getAccessaryFileUrlList().size() != 0) {
                            mBitmapUtils.display(imageView, item.getAccessaryFileUrlList().get(0));
                        }
                    }
                });
        mGallery_gridView.setOnItemClickListener(this);
    }

    private void getWidget() {
        mGallery_back_textBtn = (TextView) findViewById(R.id.gallery_back_textBtn);
        mCreate_gallery_btn = (ImageButton) findViewById(R.id.create_gallery_btn);

        mGallery_layout = (RelativeLayout) findViewById(R.id.gallery_layout);
        mLocal_gallery_imageView = (ImageButton) findViewById(R.id.local_gallery_imageView);
        mUp_photo_imageView = (ImageButton) findViewById(R.id.up_photo_imageView);
        mCreate_imageView = (ImageButton) findViewById(R.id.create_imageView);

        mGallery_progress = (ProgressBar) findViewById(R.id.gallery_progress);
        mGallery_gridView = (GridView) findViewById(R.id.gallery_gridView);

        mSelected_layout = (LinearLayout) findViewById(R.id.selected_layout);
        mSpinner_select_gallery = (Spinner) findViewById(R.id.spinner_select_gallery);
        mSpinner_select_gallery.setBackgroundResource(R.drawable.abc_spinner_ab_pressed_holo_light);
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mGalleryList);
        mSpinner_select_gallery.setAdapter(mArrayAdapter);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


//        mSelected_pic = (ImageView) findViewById(R.id.selected_pic);
        mUp_btn = (Button) findViewById(R.id.up_btn);
        mSelected_image = (GridView) findViewById(R.id.selected_image_grid);
        mSelected_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //给上传更多图片添加点击事件
                if (position == mPathList.indexOf(IMAGE_TAG)) {
                    pickPhoto();
                }
            }
        });


        mGallery_back_textBtn.setOnClickListener(this);
        mCreate_gallery_btn.setOnClickListener(this);
        mLocal_gallery_imageView.setOnClickListener(this);
        mUp_photo_imageView.setOnClickListener(this);
        mCreate_imageView.setOnClickListener(this);
        mUp_btn.setOnClickListener(this);
        mGallery_gridView.setOnItemClickListener(this);
        mGallery_gridView.setOnItemLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gallery_back_textBtn:
                //处理界面跳转
                this.finish();
                break;
            case R.id.create_gallery_btn:
                if (isCreate) {
                    mCreate_gallery_btn.setBackgroundResource(R.mipmap.create_cancle_btn);
                    mGallery_layout.setVisibility(View.VISIBLE);
                    mPathList.clear();
                    isCreate = false;
                } else {
                    mCreate_gallery_btn.setBackgroundResource(R.mipmap.create_gallery_btn);
                    mGallery_layout.setVisibility(View.GONE);
                    mSelected_layout.setVisibility(View.GONE);
                    mGallery_gridView.setVisibility(View.VISIBLE);
                    new LocalTask().execute();
                    isCreate = true;
                }
                break;
            case R.id.local_gallery_imageView:
                Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
                getAlbum.setType(IMAGE_TYPE);
                startActivity(getAlbum);
                break;
            case R.id.up_photo_imageView:

                mGallery_gridView.setVisibility(View.GONE);
                mSelected_layout.setVisibility(View.VISIBLE);
                initSelectView();
                mPicPopupWindow = new SelectPicPopupWindow(GalleryActivity.this, itemsOnClick);
                mPicPopupWindow.showAtLocation(findViewById(R.id.gallery_main),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.create_imageView:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("创建相册");
                View view = LinearLayout.inflate(this, R.layout.view_create_gallery, null);
                builder.setView(view);
                final EditText galleryNameEdit = (EditText) view.findViewById(R.id.galleryNameEdit);
                final EditText galleryDescriptionEdit = (EditText) view.findViewById(R.id.galleryDescriptionEdit);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String galleryName = galleryNameEdit.getText().toString().trim();
                        String galleryDescription = galleryDescriptionEdit.getText().toString().trim();
                        new CreateGallery().execute(galleryName, galleryDescription);
                    }
                }).show();
                break;

            case R.id.up_btn:
                Object galleryClass = mSpinner_select_gallery.getSelectedItem();

                if (galleryClass != null) {
                    new UpTask().execute(galleryClass.toString());
                } else {
                    Toast.makeText(GalleryActivity.this, "您还未创建相册，请先创建相册", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 隐藏弹出窗口
            mPicPopupWindow.dismiss();

            switch (v.getId()) {
                case R.id.takePhotoBtn:// 拍照
                    takePhoto();
                    break;
                case R.id.pickPhotoBtn:// 相册选择图片
                    pickPhoto();
                    break;
                case R.id.cancelBtn:// 取消
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        //判断是否开启相机权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }else {
            // 执行拍照前，应该先判断SD卡是否存在
            String SDState = Environment.getExternalStorageState();
            if (SDState.equals(Environment.MEDIA_MOUNTED)) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                /***
                 * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
                 * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
                 * 如果不使用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
                 */
                ContentValues values = new ContentValues();
                photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
            } else {
                Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
            }
        }
    }

    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
//        Intent intent = new Intent();
//        // 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
//使用intent调用系统提供的相册功能，使用startActivityForResult是为了获取用户选择的图片

        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.setType(IMAGE_TYPE);
        startActivityForResult(getAlbum, IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {        //此处的 RESULT_OK 是系统自定义得一个常量
            return;
        }

        doPhoto(requestCode, data);
//        // 点击取消按钮
//        if (resultCode == RESULT_CANCELED) {
//            return;
//        }
//
//        // 可以使用同一个方法，这里分开写为了防止以后扩展不同的需求
//        switch (requestCode) {
//            case SELECT_PIC_BY_PICK_PHOTO:// 如果是直接从相册获取
//                doPhoto(requestCode, data);
//                break;
//            case SELECT_PIC_BY_TACK_PHOTO:// 如果是调用相机拍照时
//                doPhoto(requestCode, data);
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 选择图片后，获取图片的路径
     */
    private void doPhoto(int requestCode, Intent data) {

        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getContentResolver();

        //此处的用于判断接收的Activity是不是你想要的那个
        if (requestCode == IMAGE_CODE) {

            Uri originalUri = data.getData();        //获得图片的uri
            String picPath = UriToPathUtils.getPath(this, originalUri);
            //上传更多按钮显示
            mPathList.add(picPath);
            if (mPathList.contains(IMAGE_TAG)) {
                mPathList.remove(IMAGE_TAG);
            }
            mPathList.add(IMAGE_TAG);
            mSelectViewAdapter.notifyDataSetChanged();
        }

    }


    private void initSelectView() {
        mSelected_image_grid = (GridView) findViewById(R.id.selected_image_grid);
        mSelectViewAdapter = new CommonAdapter<String>(this, mPathList, R.layout.item_camera_list) {
            @Override
            public void convert(ViewHolder viewHolder, String item) {
                if (item.equals(IMAGE_TAG)) {
                    ImageView imageView = viewHolder.getView(R.id.imageView_camera);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    viewHolder.setImage(R.id.imageView_camera, R.drawable.icon_add_nomal);
                } else {
                    mBitmapUtils.display(viewHolder.getView(R.id.imageView_camera), item);
                }
            }
        };
        mSelected_image_grid.setAdapter(mSelectViewAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(Constants.IMAGE_CONTENT_ID, mAlbumInfoList.get(position).getContentId());
        this.startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showDeleteDialog(position);
        return true;
    }

    class LocalTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            mGallery_progress.setVisibility(View.VISIBLE);
            mGallery_gridView.setVisibility(View.GONE);
        }

        @Override
        protected Integer doInBackground(String... params) {

            //相册信息
            String userId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, null);
            //noinspection unchecked
            mAlbumInfoList = (ArrayList<AlbumTable>) DataOperation.queryTable(AlbumTable.TABLE_NAME, AlbumTable.FIELD_USERID, userId);
            if (mAlbumInfoList != null && mAlbumInfoList.size() != 0) {

                //封面图地址
                mImageThumbUrls = new ArrayList<>();
                mGalleryList.clear();
                for (AlbumTable albumTable : mAlbumInfoList) {
                    ArrayList<String> urlList = (ArrayList<String>) albumTable.getAccessaryFileUrlList();
                    if (urlList != null && urlList.size() != 0) {
                        mImageThumbUrls.add(urlList.get(0));
                    }
                    //System.out.println("相册名称：" + (albumTable.getField(AlbumTable.FIELD_NAME)));
                    mGalleryList.add(albumTable.getField(AlbumTable.FIELD_NAME));
                }
                return 1;
            }

            return 2;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer == 1) {
                //为了防止异步加载数据spinner选择值为空，故将setAdapter放在异步加载之后
                mArrayAdapter.notifyDataSetChanged();
                mGallery_progress.setVisibility(View.GONE);
                mGallery_gridView.setVisibility(View.VISIBLE);
                //初始化相册GridView
                initAlbum();
            } else {
                Toast.makeText(GalleryActivity.this, "暂无数据或者还未同步到服务器", Toast.LENGTH_SHORT).show();
            }
        }

    }

    class UpTask extends AsyncTask<String, Integer, Integer> {


        @Override
        protected void onPreExecute() {
            mGallery_progress.setVisibility(View.VISIBLE);
            mPd = ProgressDialog.show(GalleryActivity.this, null, "正在上传...");
        }

        @Override
        protected Integer doInBackground(String... params) {
            //noinspection unchecked
            ArrayList<AlbumTable> albums = (ArrayList<AlbumTable>) DataOperation.queryTable(AlbumTable.TABLE_NAME, AlbumTable.FIELD_NAME, params[0]);
            if (albums != null && albums.size() != 0) {
                //判断原相册是否有附件，有则下载到本地
                ArrayList<String> fileUrls = (ArrayList<String>) albums.get(0).getAccessaryFileUrlList();
                if (fileUrls != null && fileUrls.size() != 0) {
                    ArrayList<String> localFileUrls = FileUtil.loadImageByUrl(Constants.GALLERY_DIRECTORY_NAME, fileUrls);
                    mPathList.addAll(localFileUrls);
                    try {
                        boolean isSuccess = insertPic(albums.get(0));
                        if (isSuccess) {
                            return 1;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //删除本独文件缓存
                        FileUtil.clearFolder(Constants.GALLERY_DIRECTORY_NAME);
                    }

                } else {
                    boolean isSuccess = insertPic(albums.get(0));
                    if (isSuccess) {
                        return 1;
                    }
                }
            }

            return 2;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mPd.dismiss();
            mGallery_progress.setVisibility(View.GONE);
            if (integer == 1) {
                mSelected_layout.setVisibility(View.GONE);
                mGallery_gridView.setVisibility(View.VISIBLE);
                mPathList.clear();
                new LocalTask().execute();
            } else if (integer == 2) {
                Toast.makeText(GalleryActivity.this, "添加照片失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean insertPic(AlbumTable albumTable) {
        if (mPathList != null && mPathList.size() != 0) {
            if (mPathList.contains(IMAGE_TAG)) {
                mPathList.remove(IMAGE_TAG);
            }
            Document[] documents = new Document[mPathList.size()];
            for (int i = 0; i < mPathList.size(); i++) {
//                Log.i("新添加的照片mPathList:", mPathList.get(i));
                documents[i] = new Document(mPathList.get(i));
            }
//            return DataOperation.insertOrUpdateTable(albumTable, documents);
            return com.geekband.huzhouapp.post.DataOperation.insertOrUpdateTable(albumTable, documents);
        }
        return false;
    }


    public void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除相册");
        builder.setMessage("删除的相册不可恢复，您确认删除？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteGalleryTask().execute(position);
                    }
                }

        );
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //取消删除

                    }
                }
        );

        builder.create().show();
    }

    class DeleteGalleryTask extends AsyncTask<Integer, Integer, Integer> {
        private ProgressDialog mPd;

        @Override
        protected void onPreExecute() {
            mPd = ProgressDialog.show(GalleryActivity.this, null, "正在删除...");
        }

        @Override
        protected Integer doInBackground(Integer... params) {

            AlbumTable selectAlbum = mAlbumInfoList.get(params[0]);
            DataOperation.deleteTable(selectAlbum);
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mPd.dismiss();
            //刷新界面
            new LocalTask().execute();
        }

    }

    private class CreateGallery extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            mPd = ProgressDialog.show(GalleryActivity.this, null, "正在创建相册...");
        }

        @Override
        protected Integer doInBackground(String... params) {
            String galleryName = params[0];
            //判断是否有同名相册，有则不允许创建
            String userId = MyApplication.sSharedPreferences.getString(Constants.AUTO_LOGIN, null);
            //noinspection unchecked
            ArrayList<AlbumTable> albumTables = (ArrayList<AlbumTable>) DataOperation.queryTable(AlbumTable.TABLE_NAME, AlbumTable.FIELD_USERID, userId);
            if (albumTables != null && albumTables.size() != 0) {
                for (AlbumTable albumTable : albumTables) {
                    String albumName = albumTable.getField(AlbumTable.FIELD_NAME);
                    if (albumName.equals(galleryName)) {
                        return 2;
                    }
                }
            }
            String galleryDescription = params[1];
            AlbumTable albumTable = new AlbumTable();
            albumTable.putField(AlbumTable.FIELD_USERID, userId);
            albumTable.putField(AlbumTable.FIELD_NAME, galleryName);
            albumTable.putField(AlbumTable.FIELD_DESCRIPTION, galleryDescription);
            albumTable.putField(AlbumTable.FIELD_DATETIME, FileUtils.getCurrentTimeStr("yyyy-MM-dd HH:mm:ss"));
            DataOperation.insertOrUpdateTable(albumTable, new Document[]{});
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mPd.dismiss();
            if (integer == 1) {
                new LocalTask().execute();
            } else if (integer == 2) {
                Toast.makeText(GalleryActivity.this, "相册已存在", Toast.LENGTH_LONG).show();
            }
        }
    }

}