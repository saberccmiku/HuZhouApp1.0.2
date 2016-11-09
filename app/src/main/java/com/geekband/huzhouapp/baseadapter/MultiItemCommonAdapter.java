package com.geekband.huzhouapp.baseadapter;

import android.content.Context;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/25
 */
public abstract class MultiItemCommonAdapter<T> extends CommonRecyclerAdapter {

    protected MultiItemTypeSupport<T> mMultiItemTypeSupport;
    private Context mContext;
    private ArrayList<T> mDataList;

    public MultiItemCommonAdapter(Context context, ArrayList<T> dataList ,MultiItemTypeSupport<T> multiItemTypeSupport) {
        //noinspection unchecked
        super(context, -1, dataList);
        mContext = context;
        mMultiItemTypeSupport =multiItemTypeSupport;
        mDataList = dataList;
    }

    @Override
    public int getItemViewType(int position) {
        return mMultiItemTypeSupport.getItemViewType(position,mDataList.get(position));
    }

    @Override
    public CommonRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = mMultiItemTypeSupport.getLayoutId(viewType);
        return CommonRecyclerViewHolder.getViewHolder(mContext,layoutId,parent);
    }
}
