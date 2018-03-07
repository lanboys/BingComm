package com.bing.lan.comm.api.interceptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 从 Response 中获取 set-cookie 字段的值，并保存在本地
 */
public class SaveCookiesInterceptor implements Interceptor {

    private static final String COOKIE_PREF = "cookie_perfs";
    private Context mContext;

    public SaveCookiesInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (!response.headers("set-cookie").isEmpty()) {
            List<String> cookies = response.headers("set-cookie");
            String cookie = encodeCookie(cookies);
            saveCookie(request.url().toString(), request.url().host(), cookie);
            Logger.i("保存 Cookie " + cookie);
        } else {
            Logger.i("没有Cookie,不做保存操作 ");
        }
        return response;
    }

    // 整合 cookie 为唯一字符串
    private String encodeCookie(List<String> cookies) {
        StringBuilder builder = new StringBuilder();
        Set<String> set = new HashSet<>();
        for (String cookie : cookies) {
            String[] arr = cookie.split(";");
            for (String s : arr) {
                if (set.contains(s)) {
                    continue;
                }
                set.add(s);
            }
        }

        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String cookie = iterator.next();
            builder.append(cookie).append(";");
        }

        int last = builder.lastIndexOf(";");
        if (builder.length() - 1 == last) {
            builder.deleteCharAt(last);
        }

        return builder.toString();
    }

    // 保存 cookie 到本地
    private void saveCookie(String url, String host, String cookies) {
        SharedPreferences sp = mContext.getSharedPreferences(COOKIE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url is null");
        } else {
            editor.putString(url, cookies);
        }

        if (!TextUtils.isEmpty(host)) {
            editor.putString(host, cookies);
        }

        editor.apply();
    }
}
