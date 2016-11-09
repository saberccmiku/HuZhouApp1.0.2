package com.geekband.huzhouapp.utils;

import android.content.Context;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;

/**
 * Created by Administrator on 2016/9/2
 */
public abstract class PtrClassicFrameLayoutConfig {
    public void ptrConfig(Context context ,RecyclerAdapterWithHF adapterWithHF, final PtrClassicFrameLayout ptr){
        ptr.setLastUpdateTimeRelateObject(context);
        ptr.setResistance(1.7f);
        ptr.setRatioOfHeaderHeightToRefresh(1.2f);
        ptr.setDurationToClose(200);
        ptr.setDurationToCloseHeader(1000);
        ptr.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptr.autoRefresh();
            }
        }, 100);

        ptr.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                new Thread(){
                    @Override
                    public void run() {
                        refreshData();
                    }
                }.start();
            }
        });
    }

    abstract void refreshData();
}
