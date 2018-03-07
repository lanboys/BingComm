package com.bing.lan.comm.mvp;

import com.bing.lan.comm.app.AppConfig;
import com.bing.lan.comm.utils.LogUtil;
import com.ganxin.library.LoadDataLayout;
import com.google.gson.JsonParseException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import retrofit2.HttpException;

/**
 * @author 蓝兵
 * @time 2017/1/12  18:31
 */
public abstract class BasePresenter<
        T extends IBaseContract.IBaseView,
        M extends IBaseContract.IBaseModule>
        implements IBaseContract.IBasePresenter<T, M> {

    protected LogUtil log = LogUtil.getLogUtil(getClass(), 1);

    // 会不会内存泄露
    protected M mModule;
    protected T mView;
    // protected boolean isHaveData;

    @Override
    public void setModule(M module) {
        mModule = module;
    }

    @Override
    public void onAttachView(T view) {
        mView = view;
    }

    @Override
    public void onDetachView() {
        mModule.releaseTask();
        mView = null;
    }

    @Override
    public void requestData(int action, Object... parameter) {
        mModule.requestData(action, this, parameter);
    }

    @Override
    public void onLoading(int action) {
        log.i("onLoading(): 有相同任务正在执行,请稍后再试;  action: " + action);
    }

    @Override
    public void onError(int action, Throwable e) {
        if (AppConfig.LOG_DEBUG) {// 测试环境显示
            onException(action, e);
        }
        mView.setLoadDataLayoutStatus(LoadDataLayout.ERROR);
        mModule.refreshTask(action);
        finishAction();
    }

    /**
     * 请求异常
     */
    private void onException(int action, Throwable e) {
        if (e == null) {
            return;
        }
        if (e instanceof NullPointerException) {//Null is not a valid element
            mView.showError(" 空指针 NullPointerException");
        } else if (e instanceof java.net.SocketTimeoutException) {//failed to connect to /192.168.2.186 (port 8888) after 15000ms
            mView.showError("网络超时,请检查网络或代理..");
        } else if (e instanceof ConnectException) {
            mView.showError("网络连接异常,请检查网络或代理..");//添加了代理会抛这个异常
        } else if (e instanceof HttpException) {
            mView.showError("服务器异常..");//服务器500 抛这个异常
        } else if (e instanceof InterruptedIOException) {   //  连接超时
            mView.showError("I/O中断异常..");
        } else if (e instanceof UnknownHostException) {   //  未知域名异常
            mView.showError("未知域名异常..");
        } else if (e instanceof javax.net.ssl.SSLHandshakeException
                || e instanceof java.security.cert.CertPathValidatorException) {   //  SSL证书异常
            mView.showError("SSL证书异常..");
        } else if (e instanceof java.net.ProtocolException
                || e instanceof java.io.IOException) {   //  I/O流意外结束
            mView.showError("I/O异常或流意外结束..");
        } else if (e instanceof JsonParseException
                || e instanceof com.alibaba.fastjson.JSONException
                || e instanceof org.json.JSONException
                || e instanceof ParseException) {   //  解析错误
            mView.showError("json解析异常..");
        } else {
            mView.showError("未知错误..");
        }
    }

    @Override
    public void onCompleted(int action) {
        mModule.refreshTask(action);
        finishAction();
    }

    @Override
    public void onNetError(int action, String tip) {
        mView.setLoadDataLayoutStatus(LoadDataLayout.NO_NETWORK);
        mView.showInfo(tip);
        finishAction();

        onCompleted(action);
    }

    protected void finishAction() {
        mView.dismissProgressDialog();
        mView.closeRefreshing();
    }
}
