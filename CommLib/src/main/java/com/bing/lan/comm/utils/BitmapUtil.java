package com.bing.lan.comm.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.SystemClock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * bitmap相关操作工具类
 *
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush.utils
 * @date 2016/11/28  09:45
 */

public class BitmapUtil {

    protected static final LogUtil log = LogUtil.getLogUtil(BitmapUtil.class, LogUtil.LOG_VERBOSE);

    /**
     * 计算图片的压缩比率
     *
     * @param options   参数
     * @param reqWidth  目标的宽度
     * @param reqHeight 目标的高度
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        // 一个为0 就按原来比例
        if (reqHeight == 0 || reqWidth == 0) {
            return inSampleSize;
        }

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 从Resources中加载图片
     */
    public static Bitmap decodeSampledBitmapFromResource(
            Resources res, int resId, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置成了true,不占用内存，只获取bitmap宽高
        options.inJustDecodeBounds = true;
        // 读取图片长款
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        // 载入一个稍大的缩略图
        Bitmap src = BitmapFactory.decodeResource(res, resId, options);
        // 进一步得到目标大小的缩略图
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
    }

    /**
     * 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     */
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight, int inSampleSize) {
        //如果inSampleSize是2的倍数，也就说这个src已经是我们想要的缩略图了，直接返回即可。
        if (inSampleSize % 2 == 0) {
            return src;
        }
        // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，
        // 我们这里是缩小图片，所以直接设置为false
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    /**
     * 按原比例 从SD卡加载图片
     */
    public static Bitmap decodeSampledBitmapFromFile(String pathName) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        int bitmapAngle = getBitmapDegree(pathName);
        if (bitmapAngle != 0) {
            //对图片进行旋转校验 如果图片已经出现旋转了  现在开始复位并返回
            src = rotateBitmapByAngle(src, bitmapAngle);
            log.d("decodeSampledBitmapFromFile(): 照片旋转角度:  " + bitmapAngle);
        }

        return src;
    }

    /**
     * 根据给定的宽高 从SD卡上加载图片
     */

    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        log.e("decodeSampledBitmapFromFile():  options.inSampleSize " + options.inSampleSize);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);

        int bitmapAngle = getBitmapDegree(pathName);
        if (bitmapAngle != 0) {
            //对图片进行旋转校验 如果图片已经出现旋转了  现在开始复位并返回
            src = rotateBitmapByAngle(src, bitmapAngle);
            log.d("decodeSampledBitmapFromFile(): 照片旋转角度:  " + bitmapAngle);
        }

        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
    }

    /**
     * @param bitmap
     * @return 字节
     */
    public static long getBitmapSize(Bitmap bitmap) {

        if (bitmap == null) {
            log.e("getBitmapSize(): bitmap 为 null");
            return 0;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    public static String savePicToSdcard(Bitmap bitmap, String path, String fileName) {
        String filePath = "";
        if (bitmap == null) {
            return filePath;
        } else {

            filePath = path + fileName;
            File destFile = new File(filePath);
            OutputStream os = null;
            try {
                os = new FileOutputStream(destFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
            } catch (IOException e) {
                filePath = "";
            }
        }
        return filePath;
    }

    public static File savePicToSdcard(Bitmap bitmap, File destFile, int quality) {
        if (bitmap == null) {
            throw new NullPointerException("bitmap==null");
        }

        OutputStream os = null;
        try {
            os = new FileOutputStream(destFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, os);
            os.flush();
            os.close();
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (IOException e) {
            log.e("savePicToSdcard():  " + e.getLocalizedMessage());
        }

        return destFile;
    }

    /**
     * 原比例压缩压缩
     */
    public static File compressImage(File srcFile, File destFile, int maxSize) {
        final Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(srcFile.getAbsolutePath());
        BitmapUtil.compressImage(bitmap, destFile, maxSize);
        return destFile;
    }

    /**
     * 按比例压缩压缩
     */
    public static File compressImage(File srcFile, File destFile, int reqWidth, int reqHeight, int maxSize) {
        final Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(srcFile.getAbsolutePath(), reqWidth, reqHeight);
        BitmapUtil.compressImage(bitmap, destFile, maxSize);
        return destFile;
    }

    /**
     * 质量压缩方法 ,将bitmap---->文件
     *
     * @param bitmap
     * @param maxSize 单位 kb    1kb = 1024 byte = 1024 X 8 bit
     */
    public static File compressImage(Bitmap bitmap, File destFile, int maxSize) {

        long startTime = SystemClock.currentThreadTimeMillis();
        log.i("compressImage() startTime: " + startTime);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 90;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

        while (baos.toByteArray().length / 1024 > maxSize && quality > 10) {
            baos.reset();
            quality -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            log.i("compressImage(): 压缩 quality: " + quality);
        }

        try {
            baos.writeTo(new FileOutputStream(destFile));
        } catch (Exception e) {
            log.e("compressImage():  " + e.getLocalizedMessage());
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                log.e("compressImage():  " + e.getLocalizedMessage());
            }
        }

        // if (!bitmap.isRecycled()) {
        //     bitmap.recycle();
        // }
        long endTime = SystemClock.currentThreadTimeMillis();
        log.i("compressImage() endTime: " + endTime);
        log.i("compressImage() 压缩用时: " + (endTime - startTime));
        try {
            log.i("compressImage() 压缩后大小: " + FileUtil.formatFileSize(FileUtil.getFileSize(destFile)));
        } catch (Exception e) {
            log.e("compressImage():  " + e.getLocalizedMessage());
        }

        return destFile;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int maxSize) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > maxSize) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        try {
            baos.flush();
            baos.close();
            isBm.close();
        } catch (IOException e) {
            log.e("compressImage():  " + e.getLocalizedMessage());
        }
        // if (!image.isRecycled()) {
        //     image.recycle();
        // }

        return bitmap;
    }

    /**
     * 将图片路径Uri所表示的图片转换成指定大小的照片显示出来
     */
    public static Bitmap getThumbnail(Bitmap srcBmp, int reqWidth, int reqHeight) {
        Bitmap dstBmp;
        if (srcBmp.getWidth() < reqWidth && srcBmp.getHeight() < reqHeight) {
            dstBmp = ThumbnailUtils.extractThumbnail(srcBmp, reqWidth, reqHeight);
            // Otherwise the ratio between measures is calculated to fit requested thumbnail's one
        } else {
            int x = 0, y = 0, width = srcBmp.getWidth(), height = srcBmp.getHeight();
            float ratio = ((float) reqWidth / (float) reqHeight) * ((float) srcBmp.getHeight() / (float) srcBmp.getWidth());
            if (ratio < 1) {
                x = (int) (srcBmp.getWidth() - srcBmp.getWidth() * ratio) / 2;
                width = (int) (srcBmp.getWidth() * ratio);
            } else {
                y = (int) (srcBmp.getHeight() - srcBmp.getHeight() / ratio) / 2;
                height = (int) (srcBmp.getHeight() / ratio);
            }
            dstBmp = Bitmap.createBitmap(srcBmp, x, y, width, height);
        }
        return dstBmp;
    }

    /**
     * 获取位图的方向	0	90	180	  270
     *
     * @param path 图片的本地的文件地址
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
            log.e("getBitmapDegree():  " + e.getLocalizedMessage());
        }
        return degree;
    }

    /**
     * 当程序员发现如果返回的图片出现了旋转 可以通过该方法进行复位
     */
    public static Bitmap rotateBitmapByAngle(Bitmap bmp, int degress) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degress);
        Bitmap result = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        return result;
    }
}
