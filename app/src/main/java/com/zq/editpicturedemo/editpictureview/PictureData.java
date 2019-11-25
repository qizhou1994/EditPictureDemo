package com.zq.editpicturedemo.editpictureview;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * @author qizhou
 * @2019/11/21
 * @description PictureData
 */
public class PictureData {


    private float left;

    private float right;

    private float top;

    private float bottom;

    private Bitmap bitmap;

    private int status;

    private int shape;

    private int editType;

    private Matrix matrix;

    private float totalScale;


    public float getTotalScale() {
        return totalScale;
    }

    public void setTotalScale(float totalScale) {
        this.totalScale = totalScale;
    }

    public PictureData(){
        editType = EditPictureContract.STATUS_EDIT_NOT;
        status = EditPictureContract.STATUS_INIT;
    }


    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public int getEditType() {
        return editType;
    }

    public void setEditType(int editType) {
        this.editType = editType;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }


}
