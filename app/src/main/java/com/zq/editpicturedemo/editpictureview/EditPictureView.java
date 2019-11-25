package com.zq.editpicturedemo.editpictureview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.zq.editpicturedemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qizhou
 * @2019/11/21
 * @description EditPictureView
 */
public class EditPictureView extends View implements EditPictureContract {

    /**
     * 默认行
     */
    private int row;
    /**
     * 默认列
     */
    private int rank;
    /**
     * 默认模块的宽高
     */
    private int width;
    private int height;
    private Paint paint;
    /**
     * 画图的形状大小
     */
    private Rect rect;

    /**
     * 实际的图形所占位置的形状大小
     */
    private RectF rectf;

    /**
     * 数据源
     */
    private List<PictureData> dataSourceList;
    private Canvas canvas;

    /**
     * 当前编辑的子图
     */
    private PictureData pictureDataEdit;

    private boolean isableTouch = true;

    //记录两指同时放在屏幕上时，中心点的横坐标值
    private float centerPointx;
    //记录两指同时放在屏幕上时，中心点的纵坐标值
    private float centerPointy;
    //记录上次手指移动时的横坐标
    private float lastXMove = -1;
    //记录上次手指移动时的纵坐标
    private float lastYMove = -1;
    //记录手指在横坐标方向上的移动距离
    private float movedDistancex;
    //记录手指在纵坐标方向上的移动距离
    private float movedDistancey;
    //记录图片在矩阵上的总缩放比例
    private float totalRatio;
    //记录手指移动的距离所造成的缩放比例
    private float scaledRatio;
    //记录图片初始化时的缩放比例
    private float initRatio;
    //记录上次两指之间的距离
    private double lastFingerDis;


    public EditPictureView(Context context) {
        super(context);
        initAttr(context, null);
    }

    public EditPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public EditPictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    public EditPictureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray attributes = context.obtainStyledAttributes(attributeSet,
                    R.styleable.EditPictureView);
            rank = attributes.getInt(R.styleable.EditPictureView_normal_rank, 3);
            row = attributes.getInt(R.styleable.EditPictureView_normal_row, 3);
        }
        isableTouch = true;
    }
    private boolean isFirst;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isFirst) {
            initFirst(canvas);
            isFirst = false;
        } else {
            ondrawCancas(canvas);
        }
    }

    /**
     * 当有数据的时候，主要的画板操作
     *
     * @param canvas
     */
    private void ondrawCancas(Canvas canvas) {
        RectF rectf = new RectF();
        canvas.save();

        for (PictureData pictureData : dataSourceList) {
            rectf.left = (int) pictureData.getLeft();
            rectf.top = (int) pictureData.getTop();
            rectf.right = (int) pictureData.getRight();
            rectf.bottom = (int) pictureData.getBottom();
            if (pictureData.getEditType() == EditPictureContract.STATUS_EDIT_EDIT) {
                if (pictureData != null) {
                    //设置周围的线
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setPathEffect(new DashPathEffect(new float[]{100, 8}, 1));

                    if (pictureData.getStatus() == EditPictureContract.STATUS_ZOOM_IN ||
                            pictureData.getStatus() == EditPictureContract.STATUS_ZOOM_OUT) {
                        zoom(canvas);
                    } else if (pictureData.getStatus() == EditPictureContract.STATUS_ROTATE) {
                        //处理旋转
                    }
                    if (pictureData.getMatrix() != null) {
                        //todo 修改边框
                        //按照比例扩大边框
                        canvas.drawRect(pictureData.getLeft(),
                                pictureData.getTop() + 1,
                                pictureData.getRight() + pictureData.getBitmap().getWidth() * (pictureData.getTotalScale() - 1),
                                pictureData.getBottom() + pictureData.getBitmap().getHeight() * (pictureData.getTotalScale() - 1),
                                paint);


                        canvas.drawBitmap(pictureData.getBitmap(), pictureData.getMatrix(), paint);
                    } else {
                        canvas.drawRect(pictureData.getLeft(),
                                pictureData.getTop() + 1, pictureData.getRight(), pictureData.getBottom(),
                                paint);
                        canvas.drawBitmap(pictureData.getBitmap(), rect, rectf, paint);
                    }
                }
            } else {
                if (pictureData.getMatrix() != null) {
                    canvas.drawBitmap(pictureData.getBitmap(), pictureData.getMatrix(), paint);
                } else {
                    canvas.drawBitmap(pictureData.getBitmap(), rect, rectf, paint);
                }
            }

        }
        canvas.restore();
    }



    @Override
    public void initTouchParameter() {
        //记录图片在矩阵上的总缩放比例
        if(pictureDataEdit!=null&&pictureDataEdit.getTotalScale()!=0){
            totalRatio = pictureDataEdit.getTotalScale();
        }else {
            totalRatio = 1f;
        }

        //记录手指移动的距离所造成的缩放比例
        scaledRatio = 1f;
        //记录图片初始化时的缩放比例
        initRatio = 1f;
        //记录上次两指之间的距离
        lastFingerDis = 1;
        lastXMove = -1;
        lastYMove = -1;
    }

    @Override
    public void disableTouch() {
        isableTouch = false;
    }

    @Override
    public void ableTouch() {
        isableTouch = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isableTouch) {
            if (event.getPointerCount() == EditPictureContract.TOUCH_MODE_ONE_FINGER) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initTouchParameter();
                        if (pictureDataEdit == null || pictureDataEdit.getStatus() != EditPictureContract.STATUS_MOVE) {
                            if (clickSecondData(event.getX(), event.getY())) {
                                if (pictureDataEdit != null) {
                                    pictureDataEdit.setStatus(EditPictureContract.STATUS_MOVE);
                                }
                            } else {
                                if (pictureDataEdit != null) {
                                    pictureDataEdit.setStatus(EditPictureContract.STATUS_OTHER);
                                }
                            }
                            lastXMove = event.getX();
                            lastYMove = event.getY();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        move(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        if (pictureDataEdit != null) {
                            pictureDataEdit.setStatus(EditPictureContract.STATUS_OTHER);
                        }
                        initTouchParameter();
                        break;
                    default:
                        ;
                }
            } else if (event.getPointerCount() == EditPictureContract.TOUCH_MODE_TWO_FINGER) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initTouchParameter();
                        // 当有两个手指按在屏幕上时，计算两指之间的距离
                        lastFingerDis = distanceBetweenFingers(event);
                        if (pictureDataEdit != null) {
                            pictureDataEdit.setStatus(STATUS_ZOOM_OUT);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (lastFingerDis == 1) {
                            lastFingerDis = distanceBetweenFingers(event);
                        }
                        if (pictureDataEdit != null) {

                            // 有两个手指按在屏幕上移动时，为缩放状态
                            centerPointBetweenFingers(event);
                            double fingerDis = distanceBetweenFingers(event);

                            //旋转
                            if (fingerDis > lastFingerDis) {
                                pictureDataEdit.setStatus(STATUS_ZOOM_OUT);
                            } else if (fingerDis == lastFingerDis) {

                            } else {
                                pictureDataEdit.setStatus(STATUS_ZOOM_IN);
                            }

                            // 进行缩放倍数检查，最大只允许将图片放大4倍，最小可以缩小到初始化比例
                            if ((pictureDataEdit.getStatus() == STATUS_ZOOM_OUT && totalRatio < 4 * initRatio)
                                    || (pictureDataEdit.getStatus() == STATUS_ZOOM_IN && totalRatio > initRatio)) {
                                scaledRatio = (float) (fingerDis / lastFingerDis);
                                totalRatio = totalRatio * scaledRatio;
                                if (totalRatio > 4 * initRatio) {
                                    totalRatio = 4 * initRatio;
                                } else if (totalRatio < initRatio) {
                                    totalRatio = initRatio;
                                }
                                lastFingerDis = fingerDis;
                                // 调用onDraw()方法绘制图片
                                invalidate();

                            } else if (pictureDataEdit.getStatus() == EditPictureContract.STATUS_ROTATE) {
                                rotate(event);
                            } else {
                                Log.e("pictureDataEdit", "???  = ");
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (pictureDataEdit != null) {
                            pictureDataEdit.setStatus(EditPictureContract.STATUS_INIT);
                        }
                        initTouchParameter();
                        break;
                    default:
                        ;
                }
            }

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void zoom(Canvas canvas) {
        if (pictureDataEdit == null) {
            return;
        }

        Matrix matrix = pictureDataEdit.getMatrix();
        if (matrix == null) {
            matrix = new Matrix();
        }
        matrix.reset();
        // 将图片按总缩放比例进行缩放
        matrix.setScale(totalRatio, totalRatio);

        float translatex = pictureDataEdit.getLeft();
        float translatey = pictureDataEdit.getTop();

        // 缩放后对图片进行偏移，以保证缩放后中心点位置不变
        matrix.postTranslate(translatex, translatey);


        pictureDataEdit.setMatrix(matrix);
        pictureDataEdit.setTotalScale(totalRatio);
    }


    /**
     * 移动的操作
     */
    @Override
    public void move(float x, float y) {
        if (pictureDataEdit != null && pictureDataEdit.getStatus() == EditPictureContract.STATUS_MOVE) {
            if (pictureDataEdit.getEditType() == EditPictureContract.STATUS_EDIT_EDIT) {
                pictureDataEdit.setLeft(pictureDataEdit.getLeft() + (x - lastXMove));
                pictureDataEdit.setRight(pictureDataEdit.getRight() + (x - lastXMove));
                pictureDataEdit.setTop(pictureDataEdit.getTop() + (y - lastYMove));
                pictureDataEdit.setBottom(pictureDataEdit.getBottom() + (y - lastYMove));
                if (pictureDataEdit.getMatrix() != null) {
                    pictureDataEdit.getMatrix().setScale(pictureDataEdit.getTotalScale(), pictureDataEdit.getTotalScale());
                    pictureDataEdit.getMatrix().postTranslate(pictureDataEdit.getLeft(), pictureDataEdit.getTop());
                }

                lastXMove = x;
                lastYMove = y;
                invalidate();
            }
        }
    }

    /**
     * 一个模块点击两次的判断
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean clickSecondData(float x, float y) {
        boolean isclickSecondData = false;
        if (dataSourceList != null) {
            float totalSc = 1;
            for (PictureData pictureData : dataSourceList) {
                totalSc = 1;
                if (pictureData.getMatrix() != null) {
                    totalSc = pictureData.getTotalScale();
                }
                if (pictureData.getLeft() <= x && pictureData.getRight() * totalSc >= x
                        && pictureData.getTop() <= y && pictureData.getBottom() * totalSc >= y) {
                    if (pictureData.getEditType() == EditPictureContract.STATUS_EDIT_NOT) {
                        isclickSecondData = false;
                        pictureData.setEditType(EditPictureContract.STATUS_EDIT_PREPARE);
                    } else if (pictureData.getEditType() == EditPictureContract.STATUS_EDIT_PREPARE) {
                        pictureData.setEditType(EditPictureContract.STATUS_EDIT_EDIT);
                        if (pictureDataEdit != null) {
                            pictureDataEdit.setStatus(EditPictureContract.STATUS_INIT);
                        }
                        pictureDataEdit = pictureData;
                        isclickSecondData = true;
                    } else {
                        isclickSecondData = true;
                    }
                } else if ((pictureData.getEditType() == EditPictureContract.STATUS_EDIT_EDIT || pictureData.getEditType() == EditPictureContract.STATUS_EDIT_PREPARE)) {
                    if (isclickSecondData) {
                        pictureData.setEditType(EditPictureContract.STATUS_EDIT_NOT);
                    }
                }
            }
            for (PictureData pictureData : dataSourceList) {
                if (!pictureData.equals(pictureDataEdit) && isclickSecondData) {
                    if (pictureData.getEditType() == EditPictureContract.STATUS_EDIT_EDIT || pictureData.getEditType() == EditPictureContract.STATUS_EDIT_PREPARE) {
                        pictureData.setEditType(EditPictureContract.STATUS_EDIT_NOT);
                    }
                }
            }
            invalidate();
        }
        return isclickSecondData;
    }


    /**
     * 设置数据源
     */
    @Override
    public void setDataSourceList(List<Bitmap> list) {
        if (list != null) {
            if (dataSourceList == null) {
                dataSourceList = new ArrayList<>();
                for (Bitmap bitmap : list) {
                    PictureData pictureData = new PictureData();
                    pictureData.setBitmap(bitmap);
                    dataSourceList.add(pictureData);
                }
            }
            isFirst = true;
        }
        invalidate();
    }

    @Override
    public void rotate(MotionEvent motionEvent) {
        float xPoint0 = motionEvent.getX(0);
        float yPoint0 = motionEvent.getY(0);
        float xPoint1 = motionEvent.getX(1);
        float yPoint1 = motionEvent.getY(1);

        float centerPointX1 = (xPoint0 + xPoint1) / 2;
        float centerPointY1 = (yPoint0 + yPoint1) / 2;

    }

    /**
     * 初始化布局
     */
    private void initFirst(Canvas canvas) {
        if (dataSourceList == null) {
            return;
        }
        if (this.canvas == null) {
            this.canvas = canvas;
        }
        if (this.paint == null) {
            this.paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(1f);
        }
        if (this.rect == null) {
            this.rect = new Rect();
            this.width = getScreenWidth() / rank;
            this.height = getScreenHeight() / row;
            this.rect.left = 0;
            this.rect.right = width;
            this.rect.top = 0;
            this.rect.bottom = height;
        }
        rectf = new RectF();
        rectf.set(rect);
        if (dataSourceList != null) {
            canvas.save();
            for (PictureData pictureData : dataSourceList) {
                Bitmap bitmap = ImgUtil.scaleBitmap(pictureData.getBitmap(), rect.width(), rect.width());
                pictureData.setBitmap(bitmap);
                canvas.drawBitmap(pictureData.getBitmap(), rect, rectf, paint);
                //设置大小
                pictureData.setLeft(rectf.left);
                pictureData.setRight(rectf.right);
                pictureData.setTop(rectf.top);
                pictureData.setBottom(rectf.bottom);
                if ((rectf.right + width) <= getScreenWidth()) {
                    //换列
                    rectf.left = rectf.right;
                    rectf.right += width;
                } else {
                    //换行
                    rectf.left = 0;
                    rectf.right = width;
                    if ((rectf.bottom + height) <= getScreenHeight()) {
                        rectf.top = rectf.bottom;
                        rectf.bottom += height;
                    } else {
                        break;
                    }
                }
            }
            canvas.restore();
        }
    }


    /**
     * 得到屏幕宽度
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return getWidth();
    }

    /**
     * 得到屏幕高度
     *
     * @return
     */
    private int getScreenHeight() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return getHeight();
    }

    private void calPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private float calRotation(MotionEvent event) {
        double deltax = (event.getX(0) - event.getX(1));
        double deltay = (event.getY(0) - event.getY(1));
        double radius = Math.atan2(deltay, deltax);
        return (float) Math.toDegrees(radius);
    }

    /**
     * 计算两个手指之间中心点的坐标。
     *
     * @param event
     */
    private void centerPointBetweenFingers(MotionEvent event) {
        float xPoint0 = event.getX(0);
        float yPoint0 = event.getY(0);
        float xPoint1 = event.getX(1);
        float yPoint1 = event.getY(1);
        centerPointx = (xPoint0 + xPoint1) / 2;
        centerPointy = (yPoint0 + yPoint1) / 2;
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    /**
     * 计算两个手指之间的距离。
     *
     * @param event
     * @return 两个手指之间的距离
     */
    private double distanceBetweenFingers(MotionEvent event) {
        float disx = Math.abs(event.getX(0) - event.getX(1));
        float disy = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt(disx * disx + disy * disy);
    }

    // 圆心坐标
    private float mPointx = 0, mPointy = 0;

    private int flag = 0;
    // 半径
    private int mRadius = 0;
    // 旋转角度
    private int mAngle = 0;
    private int beginAngle = 0, currentAngle = 0;


}
