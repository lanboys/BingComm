package com.bing.lan.comm.api.interceptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 将 Cookie 添加到 Header
 */
public class AddCookiesInterceptor implements Interceptor {

    private static final String COOKIE_PREF = "cookie_perfs";
    private Context mContext;

    public AddCookiesInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        String cookie = getCookie(request.url().toString(), request.url().host());
        if (!TextUtils.isEmpty(cookie)) {
            builder.addHeader("Cookie", cookie);
            Logger.i("添加 Cookie " + cookie);
        } else {
            Logger.i("没有 Cookie,不做添加操作");
        }

        Response response = chain.proceed(builder.build());
        return response;
    }

    private String getCookie(String url, String host) {
        SharedPreferences sp = mContext.getSharedPreferences(COOKIE_PREF, Context.MODE_PRIVATE);
        if (!TextUtils.isEmpty(url) && sp.contains(url) && !TextUtils.isEmpty(sp.getString(url, ""))) {
            return sp.getString(url, "");
        }

        if (!TextUtils.isEmpty(host) && sp.contains(host) && !TextUtils.isEmpty(sp.getString(host, ""))) {
            String string = sp.getString(host, "");
            return string;
        }

        return null;
    }
}
