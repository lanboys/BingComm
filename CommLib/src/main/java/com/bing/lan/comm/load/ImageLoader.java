package com.bing.lan.comm.load;

import android.content.Context;
import android.widget.ImageView;

import com.bing.lan.comm.app.AppUtil;

/**
 * @author 蓝兵
 * @time 2017/2/23  21:55
 */
public class ImageLoader {

    private static volatile ImageLoader instance = null;
    private IBaseLoaderStrategy mStrategy;

    private ImageLoader() {
        mStrategy = new GlideLoadStrategy();
    }

    public static void init(Context context) {
        //GlideLoadStrategy.init(context);
    }

    public static ImageLoader getInstance() {

        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    public void loadImage(ImageView imageView, String url) {
        // mStrategy.loadImage(imageView.getContext(), imageView, url);
        mStrategy.loadImage(AppUtil.getApplication(), imageView, url);
    }

    public void loadImage(ImageView imageView, String url, int reqWidth, int reqHeight) {
        mStrategy.loadImage(AppUtil.getApplication(), imageView, url, reqWidth, reqHeight);
        // mStrategy.loadImage(imageView.getContext(), imageView, url, reqWidth, reqHeight);
    }

    public void loadBigImage(ImageView imageView, String url) {
        mStrategy.loadBigImage(AppUtil.getApplication(), imageView, url);
        // mStrategy.loadBigImage(imageView.getContext(), imageView, url);
    }

    public void loadSmallImage(ImageView imageView, String url) {
        mStrategy.loadSmallImage(AppUtil.getApplication(), imageView, url);
        // mStrategy.loadSmallImage(imageView.getContext(), imageView, url);
    }

    public void loadImageFile(ImageView imageView, String url, GlideLoadStrategy.FileDownloadCallBack fileDownloadCallBack,
            int reqWidth, int reqHeight) {
        mStrategy.loadImageFile(AppUtil.getApplication(), imageView, url, fileDownloadCallBack, reqWidth, reqHeight);
        // mStrategy.loadImage(imageView.getContext(), imageView, url, reqWidth, reqHeight);
    }
}
