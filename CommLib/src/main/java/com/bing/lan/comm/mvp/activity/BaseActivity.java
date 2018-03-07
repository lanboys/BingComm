package com.bing.lan.comm.mvp.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bing.lan.comm.R;
import com.bing.lan.comm.app.AppUtil;
import com.bing.lan.comm.app.BaseApplication;
import com.bing.lan.comm.photoSelect.OnPhotoSelectListener;
import com.bing.lan.comm.photoSelect.PhotoSelectCropUtil;
import com.bing.lan.comm.photoSelect.PhotoSelectSource;
import com.bing.lan.comm.theme.ImmersionUtil;
import com.bing.lan.comm.toast.RxToast;
import com.bing.lan.comm.utils.ProgressDialogUtil;
import com.bing.lan.comm.utils.SoftInputUtil;
import com.ganxin.library.LoadDataLayout;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author 蓝兵
 */
public abstract class BaseActivity<T extends IBaseActivityContract.IBaseActivityPresenter>
        extends CommActivity
        implements IBaseActivityContract.IBaseActivityView<T>, OnPhotoSelectListener,
        SwipeRefreshLayout.OnRefreshListener {

    // @Inject
    // protected LogUtil log;
    // protected final LogUtil log = LogUtil.getLogUtil(getClass(), LogUtil.LOG_VERBOSE);
    protected Unbinder mViewBind;
    // @Inject
    protected T mPresenter;
    protected PhotoSelectCropUtil mSelectPhotoUtil;
    private ProgressDialogUtil mProgressDialog;

    // protected ActivityComponent getActivityComponent() {
    //     return DaggerActivityComponent.builder()
    //             .activityModule(new ActivityModule(this, getIntent()))
    //             .build();
    // }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全部竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //部分手机的沉浸式, api < 19 的沉浸式设置方法
        initImmersion();
        //透明状态栏
        initTranslucentStatus();
        //初始化布局
        initWindowUI();
        //启动di
        mPresenter = initPresenter();
        // startInject(getActivityComponent());

        //初始化View 和 数据
        initViewAndData(getIntent());

        //友盟(注意: 16的模拟器一用就卡死 )
        // MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //友盟

        // //选择照片
        // if (isHaveSelectPhoto()) {
        //     //mSelectPhotoUtil = new PhotoSelectUtil(this);
        //     mSelectPhotoUtil = new PhotoSelectCropUtil(this);
        //     mSelectPhotoUtil.setPhotoSelectListener(this);
        // }

        if (isMonitorNetwork()) {

            boolean isNetworkAvailable = BaseApplication.netWorkStatus.isNetworkAvailable;
            String netWorkTip = BaseApplication.netWorkStatus.netWorkTip;

            if (!isNetworkAvailable) {
                showInfo(netWorkTip);
            }
        }

        //获取权限
        if (isCheckPermissions() && getPermissionArrId() != 0) {
            requestPermissions(BASE_PERMISSION_REQUEST_CODE, getPermissionArrId());
        } else {
            readyStartPresenter();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mViewBind != null) {
            mViewBind.unbind();
            mViewBind = null;
        }
        //解绑
        if (mPresenter != null) {
            mPresenter.onDetachView();
        }

        //AppUtil.MemoryLeakCheck(this);
    }

    // protected abstract void startInject(ActivityComponent activityComponent);
    // protected void startInject(ActivityComponent activityComponent) {
    //
    // }

    protected abstract T initPresenter();

    protected void requestBasePermissionSucceed() {
        super.requestBasePermissionSucceed();
        readyStartPresenter();
    }

    protected abstract int getLayoutResId();

    protected abstract void initViewAndData(Intent intent);

    /**
     * 权限请求成功时调用
     */
    protected abstract void readyStartPresenter();

    protected void initWindowUI() {
        //初始化布局
        setContentView(getLayoutResId());
        //绑定控件
        mViewBind = ButterKnife.bind(this);
    }

    /**
     * 请求状态栏透明
     */
    protected boolean isTranslucentStatus() {
        return false;
    }

    /**
     * 请求沉浸式
     */
    protected boolean isImmersion() {
        // isImmersion = true;
        return false;
    }

    private void initTranslucentStatus() {

        if (!isTranslucentStatus())
            return;
        ImmersionUtil.initTranslucentStatus(this);
    }

    // api < 19 的沉浸式设置方法
    private void initImmersion() {
        if (!isImmersion())
            return;
        //
        ImmersionUtil.initImmersionSmallApi19(this);
    }

    private void initImmersion(boolean hasFocus) {
        if (!isImmersion())
            return;
        if (hasFocus) {
            ImmersionUtil.initImmersion(this);
        }
    }

    /**
     * 获取焦点或失去焦点时调用
     *
     * @param hasFocus 获取焦点返回true,否则返回false
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        initImmersion(hasFocus);
    }

    @Override
    public void showError(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }

        RxToast.error(AppUtil.getAppContext(), msg, Toast.LENGTH_SHORT, true).show();
    }

    @Override
    public void showInfo(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }

        RxToast.info(AppUtil.getAppContext(), msg, Toast.LENGTH_SHORT, true).show();
    }

    @Override
    public void showTip(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.pop_tip_layout, null);
        TextView tip = (TextView) view.findViewById(R.id.tv_tip);
        tip.setText(msg);

        //背景颜色
        // view.setBackgroundColor(Color.WHITE);
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.PopupWindowTipAnimation);
        //显示（自定义位置）随便new一个view 填下去 或者如下写法
        try {
            popupWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content),
                    Gravity.NO_GRAVITY  /* | Gravity.CENTER_VERTICAL*/, 0, 250);
        } catch (Exception e) {
            log.e("showTip():  " + e.getLocalizedMessage());
        }
    }

    @Override
    public void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        // Toast.makeText(AppUtil.getAppContext(), msg, Toast.LENGTH_SHORT).show();
        // RxToast.success(AppUtil.getAppContext(), msg, Toast.LENGTH_SHORT, false).show();
        RxToast.normal(AppUtil.getAppContext(), msg).show();
        // RxToast.error(mContext, "这是一个提示错误的Toast！", Toast.LENGTH_SHORT, true).show();
        // RxToast.success(mContext, "这是一个提示成功的Toast!", Toast.LENGTH_SHORT, true).show();
        // RxToast.info(mContext, "这是一个提示信息的Toast.", Toast.LENGTH_SHORT, true).show();
        // RxToast.warning(mContext, "这是一个提示警告的Toast.", Toast.LENGTH_SHORT, true).show();
        // RxToast.normal(mContext, "这是一个普通的没有ICON的Toast").show();
        // Drawable icon = getResources().getDrawable(R.drawable.set);
        // RxToast.normal(mContext, "这是一个普通的包含ICON的Toast", icon).show();

    }

    @Override
    public void showProgressDialog(String msg) {
        if (mProgressDialog != null) {
            mProgressDialog.setMessage(msg == null ? "" : msg);
        } else {
            mProgressDialog = new ProgressDialogUtil(this);
            mProgressDialog.setMessage(msg == null ? "" : msg);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            //mProgressDialog = null;
        }
    }

    @Override
    public T getPresenter() {
        return mPresenter;
    }

    /**
     * 要设置了toolbar(调用setToolBar()) 才能显示出来菜单项
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getMenuId() <= 0) {
            return super.onCreateOptionsMenu(menu);
        }
        getMenuInflater().inflate(getMenuId(), menu);
        return true;
    }

    /**
     * 加载图片
     */
    // protected void loadImage(Object path, ImageView imageView) {
    //     mPresenter.loadImage(path, imageView);
    // }
    protected int getMenuId() {
        return 0;
    }

    // 拍照返回
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSelectPhotoUtil != null) {
            mSelectPhotoUtil.onSelectActivityResult(requestCode, resultCode, data);
        }
    }

    //选择照片
    protected void selectPhoto(ImageView imageView) {
        selectPhoto(imageView, PhotoSelectSource.SELECT_ALL);
    }

    //选择照片
    protected void selectPhoto(ImageView imageView, @PhotoSelectSource.PhotoFlavour int type) {
        SoftInputUtil.closeSoftInput(this);
        if (mSelectPhotoUtil == null) {
            // if (isHaveSelectPhoto()) {
            //mSelectPhotoUtil = new PhotoSelectUtil(this);
            mSelectPhotoUtil = new PhotoSelectCropUtil(this);
            mSelectPhotoUtil.setPhotoSelectListener(this);
            // }
        }

        if (type == PhotoSelectSource.SELECT_CAMERA) {
            //拍照
            mSelectPhotoUtil.selectPhotoFromCamera(imageView);
        } else if (type == PhotoSelectSource.SELECT_ALBUM) {
            //相册
            mSelectPhotoUtil.selectPhotoFromAlbum(imageView);
        } else if (type == PhotoSelectSource.SELECT_ALL) {
            //用户选择
            mSelectPhotoUtil.showSelectPhotoDialog(imageView);
        }
    }

    @Override
    public void onPhotoSelect(ImageView imageView, File source) {
        //Toast.makeText(this, "上传图片", Toast.LENGTH_SHORT).show();

        // File file = new File(source.getPath());
        // mPresenter.onPhotoSelect(file);

    }

    // protected boolean isHaveSelectPhoto() {
    //     return true;
    // }

    protected boolean isMonitorNetwork() {
        return true;
    }

    // @Override
    // public void onResume() {
    //     super.onResume();
    //友盟
    // MobclickAgent.onResume(this);
    //友盟
    // }

    // @Override
    // public void onPause() {
    //     super.onPause();
    //友盟
    // MobclickAgent.onPause(this);
    //友盟
    // }

    @Override
    public void onRefresh() {

    }

    @Override
    public void setLoadDataLayoutStatus(@LoadDataLayout.Flavour int state) {

    }

    @Override
    public void closeRefreshing() {
        //if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
        //    mSwipeRefreshLayout.setRefreshing(false);
        //}
    }
}
