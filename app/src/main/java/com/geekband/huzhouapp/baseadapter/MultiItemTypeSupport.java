package com.geekband.huzhouapp.baseadapter;

/**
 * 多种ItemViewType
 * 多种ItemViewType，一般我们的写法是：
 *复写getItemViewType，根据我们的bean去返回不同的类型 onCreateViewHolder中根据itemView去生成不同的ViewHolder
 * Created by Administrator on 2016/6/25.
 */
public interface MultiItemTypeSupport<T> {
    int getLayoutId(int itemType);
    int getItemViewType(int position,T item);
}
