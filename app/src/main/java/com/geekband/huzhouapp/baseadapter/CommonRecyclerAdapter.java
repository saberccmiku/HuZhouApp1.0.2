package com.geekband.huzhouapp.baseadapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/25
 */
public abstract class CommonRecyclerAdapter<T> extends RecyclerView.Adapter<CommonRecyclerViewHolder> {
    private Context mContext;
    private int mLayoutId;
    private ArrayList<T> mDataList;
    private LayoutInflater mLayoutInflater;

    public CommonRecyclerAdapter(Context context, int layoutId, ArrayList<T> dataList) {
        mContext = context;
        mLayoutId = layoutId;
        mDataList = dataList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    @Override
    public CommonRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return CommonRecyclerViewHolder.getViewHolder(mContext,mLayoutId,parent);
    }

    @Override
    public void onBindViewHolder(CommonRecyclerViewHolder holder, int position) {
       convertView(holder,mDataList.get(position));

    }

    public abstract void convertView(CommonRecyclerViewHolder holder, T t) ;


    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
