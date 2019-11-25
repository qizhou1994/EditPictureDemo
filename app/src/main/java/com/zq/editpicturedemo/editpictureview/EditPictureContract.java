package com.zq.editpicturedemo.editpictureview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

import java.util.List;

/**
 * @author qizhou
 * @2019/11/21
 * @description IEditPictureContract
 */
public interface EditPictureContract {
    /**
     * 初始化
     */
    int STATUS_INIT = 0;

    /**
     * 缩小
     */
    int STATUS_ZOOM_IN = 1;

    /**
     * 放大
     */
    int STATUS_ZOOM_OUT = 2;

    /**
     * 移动
     */
    int STATUS_MOVE = 3;

    /**
     * 其他可编辑
     */
    int STATUS_OTHER = 4;
    int STATUS_ROTATE = 8;


    int STATUS_EDIT_PREPARE = 5;
    int STATUS_EDIT_EDIT = 6;
    int STATUS_EDIT_NOT = 7;

    int TOUCH_MODE_ONE_FINGER = 1;
    int TOUCH_MODE_TWO_FINGER = 2;

    /**
     * 移动的操作
     * @param x
     * @param y
     */
    void move(float x, float y);

    /**
     * 判断是否点击两次
     * @param x
     * @param y
     * @return
     */
    boolean clickSecondData(float x, float y);

    /**
     * 设置源码list
     * @param list
     */
    void setDataSourceList(List<Bitmap> list);

    /**
     * 缩放大小
     */
    void zoom(Canvas canvas);

    /**
     * 旋转
     */
    void rotate(MotionEvent motionEvent);

    /**
     * 初始化触摸参数
     */
    void initTouchParameter();

    /**
     * 触摸失效
     */
    void disableTouch();

    /**
     * 触摸生效
     */
    void ableTouch();
}
