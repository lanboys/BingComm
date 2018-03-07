package com.bing.lan.comm.mvp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bing.lan.comm.utils.LogUtil;

/**
 * @author 蓝兵
 */
public class LifecycleFragment extends Fragment {

    protected final LogUtil log = LogUtil.getLogUtil(getClass(), LogUtil.LOG_VERBOSE);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.d("onCreate(): ");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        log.d("onViewCreated(): ");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log.d("onActivityCreated(): ");
    }

    @Override
    public void onStart() {
        super.onStart();
        log.d("onStart(): ");
    }

    @Override
    public void onResume() {
        super.onResume();
        log.d("onResume(): ");
    }    @Override
    public void onStop() {
        super.onStop();
    }
}
