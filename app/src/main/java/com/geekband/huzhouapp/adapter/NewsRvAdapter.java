package com.geekband.huzhouapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.geekband.huzhouapp.vo.DynamicNews;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/12
 */
public class NewsRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<DynamicNews> mLocalNewses;
    private LayoutInflater mLayoutInflater;
    private BitmapUtils mBitmapUtils;

    public NewsRvAdapter(ArrayList<DynamicNews> localNewses, Context context) {
        mLocalNewses = localNewses;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mBitmapUtils = BitmapHelper.getBitmapUtils(mContext, null, R.drawable.default_news_pic, R.drawable.default_news_pic);

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mItem_newsImage;
        private final TextView mItem_newsTitle;
        private final TextView mItem_newsDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            mItem_newsImage = (ImageView) itemView.findViewById(R.id.item_newsImage);
            mItem_newsTitle = (TextView) itemView.findViewById(R.id.item_newsTitle);
            mItem_newsDate = (TextView) itemView.findViewById(R.id.item_newsDate);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mLayoutInflater.inflate(R.layout.item_card_view_news, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        if (mLocalNewses.get(position) == null) {
            mLocalNewses.remove(position);
        }

        mBitmapUtils.display(viewHolder.mItem_newsImage, mLocalNewses.get(position).getPicUrl());

        viewHolder.mItem_newsTitle.setText(mLocalNewses.get(position).getTitle());
        viewHolder.mItem_newsDate.setText(mLocalNewses.get(position).getDate());
    }


    @Override
    public int getItemCount() {
        return mLocalNewses.size();
    }
}
