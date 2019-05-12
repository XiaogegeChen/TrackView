package com.github.xiaogegechen.library;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 自定义仿知乎全屏可自由拖拽按钮
 */
public class TrackView extends View {

    // 默认大小
    private static final int SIZE_DEFAULT = 50;

    // 默认判定为CANCEL模式的时长
    private static final int CANCEL_INTERVAL_DEFAULT = 500;

    // 默认的外围padding
    private static final int PADDING_DEFAULT = 3;

    // 默认控件活动边界留白
    private static final int BLANK_LEFT_DEFAULT = 10;
    private static final int BLANK_RIGHT_DEFAULT = 10;
    private static final int BLANK_TOP_DEFAULT = 10;
    private static final int BLANK_BOTTOM_DEFAULT = 10;

    // 默认外围线条的颜色
    private static final int OUT_STROKE_COLOR_DEFAULT = Color.BLACK;

    // 默认的外围线宽
    private static final int OUT_STROKE_WIDTH_DEFAULT = 1;

    // 默认的内部线宽
    private static final int IN_STROKE_WIDTH_DEFAULT = 1;

    // 默认内部线条的颜色
    private static final int IN_STROKE_COLOR_DEFAULT = Color.BLACK;

    // mInnerDistance 的默认值
    private static final int IN_DISTANCE_DEFAULT = 5;

    //mInnerLength 的默认值
    private static final int IN_LENGTH_DEFAULT = 10;

    // 默认内部填充的颜色
    private static final int IN_CONTENT_COLOR_DEFAULT = Color.WHITE;

    // 外部圆的画笔
    private Paint mOutPaint;

    // 内部线的画笔
    private Paint mInPaint;

    // 内部填涂色的画笔
    private Paint mInContentPaint;

    // 内部的path
    private Path mInPath;

    // 外部的属性
    private int mOutHeight;
    private int mOutWidth;
    private int mOutStrokeColor;
    private int mOutStrokeWidth;

    // 内部的属性
    private int mInnerDistance;
    private int mInnerLength;
    private int mInnerStrokeColor;
    private int mInnerStrokeWidth;
    private int mInnerContentColor;

    // 边界属性, 单位是pixel
    private int mBlankLeft;
    private int mBlankRight;
    private int mBlankTop;
    private int mBlankBottom;

    // 上一次的位置
    private int mLastX = 0;
    private int mLastY = 0;

    // 在这个view内部，触摸位置距离view左上角的距离
    private int mDisX;
    private int mDisY;

    // 一个事件序列开始时的时间，就是这个事件序列ACTION_DOWN时的时间
    private long mDownTime;

    // 一个事件序列结束时的时间，就是这个事件序列ACTION_UP时的时间
    private long mUpTime;

    // 根据当前这个事件序列判定的模式
    private Mode mMode = Mode.NONE;

    // 屏幕像素
    private int mScreenWidthInPixel;
    private int mScreenHeightInPixel;

    private Context mContext;

    public TrackView(Context context) {
        this (context,null);
    }

    public TrackView(Context context, @Nullable AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public TrackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        mContext = context;

        // 可点击
        setClickable (true);

        TypedArray ta = context.obtainStyledAttributes (attrs, R.styleable.TrackView);

        // 拿到属性值
        mInnerDistance = ta.getDimensionPixelSize (R.styleable.TrackView_inner_distance, IN_DISTANCE_DEFAULT);
        mInnerLength = ta.getDimensionPixelSize (R.styleable.TrackView_inner_length, IN_LENGTH_DEFAULT);
        mInnerStrokeColor = ta.getColor (R.styleable.TrackView_inner_stroke_color, IN_STROKE_COLOR_DEFAULT);
        mInnerStrokeWidth = ta.getDimensionPixelSize (R.styleable.TrackView_inner_stroke_width, IN_STROKE_WIDTH_DEFAULT);
        mInnerContentColor = ta.getColor (R.styleable.TrackView_inner_content_color, IN_CONTENT_COLOR_DEFAULT);

        mOutStrokeColor = ta.getColor (R.styleable.TrackView_out_stroke_color, OUT_STROKE_COLOR_DEFAULT);
        mOutStrokeWidth = ta.getDimensionPixelSize (R.styleable.TrackView_out_stroke_width, OUT_STROKE_WIDTH_DEFAULT);

        mBlankLeft = dp2px (ta.getDimensionPixelSize (R.styleable.TrackView_blank_left, BLANK_LEFT_DEFAULT));
        mBlankRight = dp2px (ta.getDimensionPixelSize (R.styleable.TrackView_blank_right, BLANK_RIGHT_DEFAULT));
        mBlankTop = dp2px (ta.getDimensionPixelSize (R.styleable.TrackView_blank_top, BLANK_TOP_DEFAULT));
        mBlankBottom = dp2px (ta.getDimensionPixelSize (R.styleable.TrackView_blank_bottom, BLANK_BOTTOM_DEFAULT));

        // 回收typedArray
        ta.recycle ();

        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw (canvas);

        // 外部圆的尺寸
        mOutWidth = getWidth () - getPaddingLeft () - getPaddingRight () - dp2px (PADDING_DEFAULT);
        mOutHeight = getHeight () - getPaddingBottom () -getPaddingTop () - dp2px (PADDING_DEFAULT);

        /**
         *  *                 *
         *     *           *  mInLength
         *        *     *
         *           *
         *
         *       mInDistance
         *
         *  *                 *
         *     *           *
         *        *     *
         *           *
         *
         */
        mInPath.moveTo ((float)((getWidth () / 2) - (mInnerLength / Math.sqrt (2))),
                (float)(getHeight () / 2 - (mInnerLength / Math.sqrt (2) + mInnerDistance) / 2));

        mInPath.lineTo ((float) getWidth () / 2,
                (float)(getHeight () / 2 - (mInnerLength / Math.sqrt (2) + mInnerDistance) / 2 + mInnerLength / Math.sqrt (2)));

        mInPath.lineTo ((float)((getWidth () / 2) + (mInnerLength / Math.sqrt (2))),
                (float)(getHeight () / 2 - (mInnerLength / Math.sqrt (2) + mInnerDistance) / 2));

        mInPath.moveTo ((float)((getWidth () / 2) - (mInnerLength / Math.sqrt (2))),
                (float)(getHeight () / 2 - (mInnerLength / Math.sqrt (2) + mInnerDistance) / 2 + mInnerDistance));

        mInPath.lineTo ((float) getWidth () / 2,
                (float)((float)(getHeight () / 2) - (mInnerLength / Math.sqrt (2) + mInnerDistance) / 2 + mInnerLength / Math.sqrt (2) + mInnerDistance));

        mInPath.lineTo ((float)((getWidth () / 2) + (mInnerLength / Math.sqrt (2))),
                (float)(getHeight () / 2 - (mInnerLength / Math.sqrt (2) + mInnerDistance) / 2 + mInnerDistance));

        canvas.drawCircle (getWidth () >> 1, getHeight () >> 1, Math.min (mOutWidth >> 1, mOutHeight >> 1), mOutPaint);
        canvas.drawCircle (getWidth () >> 1, getHeight () >> 1, Math.min (mOutWidth >> 1, mOutHeight >> 1), mInContentPaint);
        canvas.drawPath (mInPath, mInPaint);
    }

    @Override
    public boolean performClick() {
        return super.performClick ();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getRawX ();
        int y = (int) event.getRawY ();

        switch(event.getAction ()){
            case MotionEvent.ACTION_DOWN:

                // 重置这个开始的时间
                mDownTime = System.currentTimeMillis ();

                mDisX = (int) event.getX ();
                mDisY = (int) event.getY ();

                break;
            case MotionEvent.ACTION_UP:

                // 重置这个结束的时间
                mUpTime = System.currentTimeMillis ();

                // 设置当前的模式
                if(mMode != Mode.MOVE){
                    if(mUpTime - mDownTime >= CANCEL_INTERVAL_DEFAULT){
                        mMode = Mode.CANCEL;
                    }else{
                        mMode = Mode.CLICK;
                    }
                }

                // 根据当前的模式设置是否调用点击事件
                if(mMode == Mode.CLICK){
                    performClick ();
                }

                // 回到侧面
                if(event.getRawX () < mScreenWidthInPixel / 2){

                    // 回到最左侧
                    setTranslationX (getTranslationX () + (-1 * (x - mDisX - mBlankLeft)));
                    x = x - (x - mDisX - mBlankLeft);
                }else{

                    // 回到最右侧
                    setTranslationX (getTranslationX () + ((mScreenWidthInPixel - mBlankRight) - (Math.min (mOutWidth, mOutHeight) - mDisX + x)));
                    x = x + (mScreenWidthInPixel - mBlankRight) - (Math.min (mOutWidth, mOutHeight) - mDisX + x);
                }

                // 这个事件序列结束，重置当前的模式
                mMode = Mode.NONE;

                break;
            case MotionEvent.ACTION_MOVE:

                // 只要触发了ACTION_MOVE就设置为move模式
                mMode = Mode.MOVE;

                int dx;
                int dy;

                // 预测量的边距
                int preXLeft = x - mDisX;
                int preXRight = mScreenWidthInPixel - (x + Math.min (mOutWidth, mOutHeight) - mDisX);
                int preYUp = y - mDisY;
                int preYDown = mScreenHeightInPixel - (y + Math.min (mOutWidth, mOutHeight) - mDisY);

                // 处理X坐标
                if(preXLeft <= mBlankLeft){

                    // 超出左边界
                    dx = x - mLastX + mBlankLeft - preXLeft;
                    x = x + mBlankLeft - preXLeft;
                }else if(preXRight <= mBlankRight){

                    // 超出右边界
                    dx = x - mLastX - (mBlankRight - preXRight);
                    x = x - (mBlankRight - preXRight);
                }else{

                    // 正常
                    dx = x - mLastX;
                }

                // 处理Y坐标
                if (preYUp <= mBlankTop) {

                    // 超出上边界
                    dy = y - mLastY + mBlankTop - preYUp;
                    y = y + mBlankTop - preYUp;
                }else if(preYDown <= mBlankBottom){

                    // 超出下边界
                    dy = y - mLastY - (mBlankBottom - preYDown);
                    y = y - (mBlankBottom - preYDown);
                }else {

                    // 正常
                    dy = y - mLastY;
                }

                setTranslationX (getTranslationX () + dx);
                setTranslationY (getTranslationY () + dy);

                break;
        }

        // 更新位置
        mLastX = x;
        mLastY = y;

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure (widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize (widthMeasureSpec);
        int widthMode = MeasureSpec.getMode (widthMeasureSpec);

        int heightSize = MeasureSpec.getSize (heightMeasureSpec);
        int heightMode = MeasureSpec.getMode (heightMeasureSpec);

        if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){

            // 使用默认大小
            setMeasuredDimension (SIZE_DEFAULT, SIZE_DEFAULT);
        }else if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.EXACTLY){
            setMeasuredDimension (SIZE_DEFAULT, heightSize);
        } else if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension (widthSize, SIZE_DEFAULT);
        }
    }

    /**
     * 该控件的三种模式,只要触发了ACTION_MOVE就是MOVE模式
     * 没有触发ACTION_MOVE但是从ACTION_DOWN开始超过了500ms就是CANCEL模式
     * 未超过就是CLICK模式
     */
    private enum Mode{
        // 取消，不执行任何逻辑
        CANCEL,

        // 点击，执行点击事件
        CLICK,

        // 移动模式，随手移动
        MOVE,

        // 无模式，就是复位后的状态
        NONE
    }

    private void init() {

        mScreenWidthInPixel = getScreenParamsInPixel ()[0];
        mScreenHeightInPixel = getScreenParamsInPixel ()[1];

        mOutPaint = new Paint ();
        mInPaint = new Paint ();
        mInContentPaint = new Paint ();
        mInPath = new Path ();

        mOutPaint.setStyle (Paint.Style.STROKE);
        mOutPaint.setColor (mOutStrokeColor);
        mOutPaint.setStrokeWidth (dp2px(mOutStrokeWidth));

        mInPaint.setStyle (Paint.Style.STROKE);
        mInPaint.setColor (mInnerStrokeColor);
        mInPaint.setStrokeWidth(dp2px (mInnerStrokeWidth));

        mInContentPaint.setStyle (Paint.Style.FILL);
        mInContentPaint.setColor (mInnerContentColor);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    private int[] getScreenParamsInPixel(){
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）

        return new int[]{width, height};
    }
}
