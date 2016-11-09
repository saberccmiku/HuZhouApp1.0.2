package com.geekband.huzhouapp.fragment.news;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.activity.MainActivity;
import com.geekband.huzhouapp.activity.NewsContentActivity;
import com.geekband.huzhouapp.adapter.NewsRvAdapter;
import com.geekband.huzhouapp.utils.Constants;
import com.geekband.huzhouapp.utils.DataUtils;
import com.geekband.huzhouapp.vo.DynamicNews;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/12
 * 即时新闻模块
 */
public class BreakingNewsFragment extends Fragment implements
        OnItemClickListener, RecyclerAdapterWithHF.OnItemClickListener {

    private MainActivity mMainActivity;

    public static final int UPDATE_BANNER = 0;//更新轮播图片
    public static final int LOAD_LOCAL_NEWS = 1;//加载本地数据库新闻
    private ConvenientBanner convenientBanner;//顶部广告栏控件
    //轮播新闻列表
    private ArrayList<String> mImageList;
    private ArrayList<DynamicNews> mRollingNewses;
    //动态新闻列表
    private ArrayList<DynamicNews> mLocalNewsList;

    //标记第一次启动程序
    boolean isFirstEnter = true;
    private RecyclerView mRecyclerView;
    private PtrClassicFrameLayout mPtrClassicFrameLayout;
    private RecyclerAdapterWithHF mAdapterWithHF;
    //当前页是第一页
    private int currentPage = 1;
    //每页新闻数量
    private int pageSize = 10;
    private View mView;


    public static BreakingNewsFragment newInstance() {
        return new BreakingNewsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_breaking_news, null);
        convenientBanner = (ConvenientBanner) mView.findViewById(R.id.convenientBanner);
        //加载本地数据
        configPtr();
        //加载轮播新闻
        new BannerThread().start();
        return mView;
    }


    private void initBannerView() {
        initImageLoader();
        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, mImageList)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设

                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                        //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                .setOnItemClickListener(this);

    }

    //初始化网络图片缓存库
    private void initImageLoader() {
        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                showImageForEmptyUri(R.drawable.ic_default_adimage)
                .cacheInMemory(true).cacheOnDisc(true).build();
        if (getActivity() != null) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    getActivity().getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .discCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO).build();
            ImageLoader.getInstance().init(config);
        }
    }


    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();

        //开始自动翻页
        convenientBanner.startTurning(5000);

    }

    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), NewsContentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.ROLLING, mRollingNewses.get(position));
        intent.setAction(Constants.ROLLING);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    //刷新服务器新闻
    class ReLoadTask extends AsyncTask<String, Integer, Integer> {
        ArrayList<DynamicNews> dns;
        @Override
        protected Integer doInBackground(String... params) {
            dns = DataUtils.getNewsList(Constants.UNROLLING, pageSize, currentPage);
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mLocalNewsList.clear();
            mLocalNewsList.addAll(dns);
            mAdapterWithHF.notifyDataSetChanged();
            mPtrClassicFrameLayout.refreshComplete();
            mPtrClassicFrameLayout.setLoadMoreEnable(true);
        }
    }


    //加载更多新闻
    class LoadMoreNewsTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            mLocalNewsList.addAll(DataUtils.getNewsList(Constants.UNROLLING, pageSize, currentPage));
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mAdapterWithHF.notifyDataSetChanged();
            mPtrClassicFrameLayout.loadMoreComplete(true);
        }
    }

    //加载轮播新闻
    class BannerThread extends Thread {
        @Override
        public void run() {
            ArrayList<DynamicNews> dynamicNewses = DataUtils.getNewsList(Constants.ROLLING, pageSize, currentPage);
            //发送更新界面的请求
            Message message = Message.obtain();
            message.what = UPDATE_BANNER;
            message.obj = dynamicNewses;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 处理相关信息
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_BANNER:
                    //noinspection unchecked
                    mRollingNewses = (ArrayList<DynamicNews>) msg.obj;
                    mImageList = new ArrayList<>();
                    //加载轮播新闻
                    if (mRollingNewses != null) {
                        for (DynamicNews dynamicNews : mRollingNewses) {
                            mImageList.add(dynamicNews.getPicUrl());
                        }
                        //更新轮播新闻界面
                        initBannerView();
                    }
                    break;
                case LOAD_LOCAL_NEWS:
                    //noinspection unchecked
                    mLocalNewsList = (ArrayList<DynamicNews>) msg.obj;
                    mAdapterWithHF.notifyDataSetChanged();
                    mPtrClassicFrameLayout.refreshComplete();
                    mPtrClassicFrameLayout.setLoadMoreEnable(true);
                    isFirstEnter = false;
                    break;
                default:
                    break;

            }
            return false;
        }
    });

    //初始化本地新闻列表
    private void configPtr() {
        mLocalNewsList = new ArrayList<>();
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.breaking_news_list);
        mPtrClassicFrameLayout = (PtrClassicFrameLayout) mView.findViewById(R.id.rotate_header_list_view_frame);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(layoutManager);
            //RecyclerView自定义Adapter
            NewsRvAdapter rvAdapter = new NewsRvAdapter(mLocalNewsList, getActivity());
            mAdapterWithHF = new RecyclerAdapterWithHF(rvAdapter);
            mRecyclerView.setAdapter(mAdapterWithHF);
            mAdapterWithHF.setOnItemClickListener(this);

            if (mPtrClassicFrameLayout != null) {
                //下拉刷新支持时间
                mPtrClassicFrameLayout.setLastUpdateTimeRelateObject(this);
                //下拉刷新其他基本设置
                mPtrClassicFrameLayout.setResistance(1.7f);
                mPtrClassicFrameLayout.setRatioOfHeaderHeightToRefresh(1.2f);
                mPtrClassicFrameLayout.setDurationToClose(200);
                mPtrClassicFrameLayout.setDurationToCloseHeader(1000);
                // default is false
                mPtrClassicFrameLayout.setPullToRefresh(false);
                // default is true
                mPtrClassicFrameLayout.setKeepHeaderWhenRefresh(true);

                //进入activity自动下拉刷新
                mPtrClassicFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrClassicFrameLayout.autoRefresh();
                    }
                }, 100);

                //下拉刷新
                mPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
                    @Override
                    public void onRefreshBegin(PtrFrameLayout frame) {
                        //重载数据
                        currentPage = 1;
                        new ReLoadTask().execute();
                    }
                });

                //上拉加载
                mPtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void loadMore() {
                        //加载更多数据
                        currentPage += 1;
                        new LoadMoreNewsTask().execute();

                    }
                });
            }

        }
    }


    //点击进入服务器信息详情内容界面
    @Override
    public void onItemClick(RecyclerAdapterWithHF adapter, RecyclerView.ViewHolder vh,
                            int position) {
        DynamicNews localNews = mLocalNewsList.get(position);
        if (localNews == null) {
            mLocalNewsList.remove(position);
        }
        Intent intent = new Intent(getActivity(), NewsContentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.UNROLLING, localNews);
        intent.putExtras(bundle);
        intent.setAction(Constants.UNROLLING);
        startActivity(intent);
    }

}
