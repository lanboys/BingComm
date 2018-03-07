package com.bing.lan.comm.load;

import android.content.Context;
import android.widget.ImageView;

public interface IBaseLoaderStrategy {

    void loadImage(Context ctx, ImageView imageView, String url);

    void loadImage(Context ctx, ImageView imageView, String url, int reqWidth, int reqHeight);

    void loadSmallImage(Context ctx, ImageView imageView, String url);

    void loadBigImage(Context ctx, ImageView imageView, String url);

    void loadImageFile(Context ctx, ImageView imageView, String imageUrl , GlideLoadStrategy.FileDownloadCallBack fileDownloadCallBack, int reqWidth, int reqHeight);
}
