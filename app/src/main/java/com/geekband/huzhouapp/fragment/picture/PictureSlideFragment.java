package com.geekband.huzhouapp.fragment.picture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geekband.huzhouapp.R;
import com.geekband.huzhouapp.utils.BitmapHelper;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2016/5/24
 */
public class PictureSlideFragment extends Fragment {

    private int mIndex;
    private ArrayList<String> mList;
    private BitmapUtils mBitmapUtils;
    private PhotoView mIv_main_pic;

    public static PictureSlideFragment newInstance(int index, ArrayList<String> list) {
        PictureSlideFragment f = new PictureSlideFragment();
        f.mList = list;
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndex = getArguments() != null ? getArguments().getInt("index") : 1;
        mBitmapUtils = BitmapHelper.getBitmapUtils(getActivity(), null, 0, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        mIv_main_pic = (PhotoView) view.findViewById(R.id.iv_main_pic);
        TextView pic_count = (TextView) view.findViewById(R.id.pic_count);
        String displayText = mIndex + 1 + "/" + mList.size();
        pic_count.setText(displayText);

        mBitmapUtils.display(mIv_main_pic, mList.get(mIndex));

        new PhotoViewAttacher(mIv_main_pic);
        return view;
    }

}
