package com.bing.lan.comm.mvp.fragment;

import android.content.Intent;

import com.bing.lan.comm.app.AppUtil;
import com.bing.lan.comm.mvp.activity.BaseActivity;

/**
 * Created by 520 on 2017/7/13.
 */

public class CommFragment extends LifecycleFragment {
    protected String mTitle;

    /**
     * 获取标题
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        mTitle = title;
    }
    public void startActivity(Class<? extends BaseActivity> clazz, boolean isFinish, boolean isAnim) {
        AppUtil.startActivity(getActivity(), clazz, isFinish, false);
    }

    public void startActivity(Intent intent, boolean isFinish, boolean isAnim) {
        AppUtil.startActivity(getActivity(),intent, isFinish, false);
        if (isAnim) {
            // overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        }
    }

    /**
     * 默认false
     */
    public void startActivity(Class<? extends BaseActivity> clazz) {
        startActivity(clazz, false, true);
    }
}
