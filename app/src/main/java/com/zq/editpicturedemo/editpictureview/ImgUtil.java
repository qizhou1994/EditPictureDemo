package com.zq.editpicturedemo.editpictureview;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * @author qizhou
 * @2019/11/21
 * @description ImgUtil
 */
public class ImgUtil {

    public static Bitmap getBitmapFormResources(Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static Drawable getDrawableFromResources(Context context, int resId) {
        return context.getResources().getDrawable(resId);
    }

    public static Drawable getDrawbleFormBitmap(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Bitmap getBitmapFormDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE
                        ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        //设置绘画的边界，此处表示完整绘制
        drawable.draw(canvas);
        return bitmap;
    }

    private static final String TAG = "zq/ImgUtil";
    /**
     * xml svg 转bitmap
     *
     * @param context
     * @param drawableId
     * @return
     */
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        int sum = 0;
        //转黑白
        // 获取位图的宽
        int width = bitmap.getWidth();
        // 获取位图的高
        int height = bitmap.getHeight();
        // 通过位图的大小创建像素点数组
//        int[] pixels = new int[width * height];
//        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);


        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        long sumi = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                long grey = bitmap.getPixel(i,j);
                long red = ((grey & 0x00FF0000) >> 16);
                long green = ((grey & 0x0000FF00) >> 8);
                long blue = (grey & 0x000000FF);

//                Log.e(TAG, "grey = "+ grey+",red = " + red+",green = " + green+",blue = " + blue);
                // rgb颜色相同就是黑白图片了 取平均值只是一个方案
                grey = (red + green + blue) / 3;
//                pixels[width * i + j] = (int) (alpha | (grey << 16) | (grey << 8) | grey);
                sumi = sumi+grey ;
//                Log.e(TAG, "2grey = "+ grey+",red = " + red+",green = " + green+",blue = " + blue);
            }
        }


        double avg = sumi*1f/(height)/(width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                int grey = bitmap.getPixel(i,j);;
                int aph = ((grey & 0xFF000000) >> 24);
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                // rgb颜色相同就是黑白图片了 取平均值只是一个方案
                grey = (red + green + blue) / 3;
                if(grey>(sumi*1f/(height)/(width))||aph<0) {

                    grey = -1;
                    bitmap.setPixel(i, j, Color.BLACK);

                }else {
                    grey = 0;
                    bitmap.setPixel(i, j, Color.TRANSPARENT);
                }

            }
        }

        return bitmap;
    }


    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId,int width,int height){
        Bitmap bitmap = getBitmapFromVectorDrawable( context,  drawableId);

        return scaleBitmap(bitmap,width,height);
    }
/***/
    /**
     * 图片去色,返回灰度图片
     *
     * @param bmpOriginal 传入的图片
     * @return 去色后的图片
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param origin    原图
     * @param newWidth  新图的宽
     * @param newHeight 新图的高
     * @return new Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        // 使用后乘
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBm = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBm;
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBm = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBm.equals(origin)) {
            return newBm;
        }
        origin.recycle();
        return newBm;
    }

    /**
     * 裁剪
     *
     * @param bitmap 原图
     * @return 裁剪后的图像
     */
    public static Bitmap cropBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
        cropWidth /= 2;
        int cropHeight = (int) (cropWidth / 1.2);
        return Bitmap.createBitmap(bitmap, w / 3, 0, cropWidth, cropHeight, null, false);
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBm = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBm.equals(origin)) {
            return newBm;
        }
        origin.recycle();
        return newBm;
    }

    /**
     * 偏移效果
     *
     * @param origin 原图
     * @return 偏移后的bitmap
     */
    public static Bitmap skewBitmap(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.postSkew(-0.6f, -0.3f);
        Bitmap newBm = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBm.equals(origin)) {
            return newBm;
        }
        origin.recycle();
        return newBm;
    }
}
