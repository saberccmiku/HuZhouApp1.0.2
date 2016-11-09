package com.geekband.huzhouapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/24
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private ArrayList<String> mImageList;
    private BitmapUtils mBitmapUtils;


    public ImageAdapter(Context context, ArrayList<String> list) {
        mContext = context;
        mImageList = list;
        mBitmapUtils = BitmapHelper.getBitmapUtils(mContext, null, 0, 0);
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_camera_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        mBitmapUtils.display(holder.mImageView, mImageList.get(position));
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder  {

        private ImageView mImageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView_camera);

        }


    }

    //自定义item监听事件

}