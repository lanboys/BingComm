package com.bing.lan.comm.mvp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bing.lan.comm.R;
import com.bing.lan.comm.app.AppConfig;
import com.bing.lan.comm.app.AppUtil;
import com.bing.lan.comm.utils.SPUtil;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;
import static android.support.v7.app.AppCompatDelegate.setDefaultNightMode;

/**
 * @author 蓝兵
 */
public class CommActivity extends PermissionActivity {

    /**
     * 日间和夜景模式切换
     */
    protected void switchNightMode() {
        boolean isNight = !SPUtil.getBoolean(AppConfig.DAY_NIGHT_MODE);
        setDefaultNightMode(isNight ? MODE_NIGHT_YES : MODE_NIGHT_NO);
        SPUtil.putBoolean(AppConfig.DAY_NIGHT_MODE, isNight);
        recreate();
    }

    /**
     * 判断当前的模式
     *
     * @return true表示夜景模式
     */
    protected boolean isNightMode() {
        return SPUtil.getBoolean(AppConfig.DAY_NIGHT_MODE);
    }

    public void startActivity(Class<? extends BaseActivity> clazz, boolean isFinish, boolean isAnim) {
        AppUtil.startActivity(this, clazz, isFinish, false);
        if (isAnim) {
            // overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        }
    }

    public void startActivity(Intent intent, boolean isFinish, boolean isAnim) {
        AppUtil.startActivity(this, intent, isFinish, false);
        if (isAnim) {
            // overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        }
    }

    /**
     * 设置页面的toolbar
     *
     * @param toolBar        toolBar
     * @param title          标题
     * @param finishActivity 是否设置结束activity事件
     * @param resId          返回箭头图标 0 默认图标  >0 设置resId
     */
    protected void setToolBar(Toolbar toolBar, String title, boolean finishActivity, int resId) {

        //自定标题
        TextView toolBarTitle = (TextView) toolBar.findViewById(R.id.toolbar_title);
        if (title != null && toolBarTitle != null) {
            toolBarTitle.setText(title);
            toolBarTitle.setTextColor(Color.WHITE);

            //ViewGroup.LayoutParams layoutParams = toolBarTitle.getLayoutParams();
            //
            //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutParams;
            //params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            //params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            //params.addRule(RelativeLayout.RIGHT_OF, 0);
        }
        //不想有标题，title 传 ""
        toolBar.setTitle("");
        setSupportActionBar(toolBar);//不调用 setTitle() 会用默认标题，类名？
        // toolBar.setIcon(R.mipmap.ic_launcher);// 设置应用图标
        toolBar.setTitleTextColor(Color.WHITE);

        if (finishActivity) {

            // 全部更改为自定义的toolbar
            ImageView iv_toolbar_back = (ImageView) toolBar.findViewById(R.id.iv_toolbar_back);

            iv_toolbar_back.setVisibility(View.VISIBLE);
            if (resId > 0) {
                iv_toolbar_back.setImageResource(resId);
            }

            iv_toolbar_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            // ActionBar actionBar = getSupportActionBar();
            // if (actionBar != null) {
            //     //将默认的 返回箭头 显示出来
            //     actionBar.setDisplayHomeAsUpEnabled(true);
            //     // 返回箭头的图标
            //     if (resId > 0) {
            //         actionBar.setHomeAsUpIndicator(resId);
            //     } else {
            //         actionBar.setHomeAsUpIndicator(R.drawable.iv_back);
            //     }
            // }
            // //给箭头添加监听器
            // toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            //     @Override
            //     public void onRecyclerViewItemClick(View v) {
            //         onBackPressed();
            //     }
            // });
        }
    }
}
