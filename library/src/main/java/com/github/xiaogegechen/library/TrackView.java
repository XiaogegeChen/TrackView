package com.github.xiaogegechen.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * 自定义仿知乎全屏可自由拖拽按钮
 */
public class TrackView extends View {

    private static final String TAG = "TrackView";

    // 默认大小
    private static final int SIZE_DEFAULT = 50;

    // 默认判定为CANCEL模式的时长
    private static final int CANCEL_INTERVAL_DEFAULT = 500;

    // 松手后回到侧边的默认时长
    private static final int GO_TO_BOUNDARY_INTERVAL_DEFAULT = 100;

    // 合上的默认时长
    private static final int CLOSE_INTERVAL_DEFAULT = 500;

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

    // 默认内部文字颜色
    private static final int IN_TEXT_COLOR_DEFAULT = Color.BLACK;

    // 默认内部文字大小sp
    private static final int IN_TEXT_SIZE_DEFAULT = 16;

    // 默认内部文字
    private static final String IN_TEXT_DEFAULT = "";

    // 外部轮廓的画笔
    private Paint mOutPaint;

    // 外部轮廓的路径
    private Path mOutPath;

    // 内部线的画笔
    private Paint mInPaint;

    // 内部填涂色的画笔
    private Paint mInContentPaint;

    // 内部填涂色的路径
    private Path mInContentPath;

    // 内部文字的画笔
    private Paint mInTextPaint;

    // 内部的path
    private Path mInPath;

    // 左右两个半圆的外界矩形
    private RectF mLeftRectF;
    private RectF mRightRectF;

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
    private int mInnerTextColor;
    private int mInnerTextSize;
    private String mInnerText;

    // 边界属性, 单位是pixel
    private int mBlankLeft;
    private int mBlankRight;
    private int mBlankTop;
    private int mBlankBottom;

    // 上一次的触摸点的位置
    private int mLastX = 0;
    private int mLastY = 0;

    // 上一次view的宽度
    private int mLastWidth = 0;

    // 在这个view内部，触摸位置距离view左上角的距离
    private int mDisX;
    private int mDisY;

    // 一个事件序列开始时的时间，就是这个事件序列ACTION_DOWN时的时间
    private long mDownTime;

    // 一个事件序列结束时的时间，就是这个事件序列ACTION_UP时的时间
    private long mUpTime;

    // 根据当前这个事件序列判定的模式
    private Mode mMode = Mode.NONE;

    // 当前的位置
    private Position mPosition = Position.LEFT;

    // 动画监听器
    private AnimatorListenerAdapter mOpenAnimatorListenerAdapter;
    private ValueAnimator.AnimatorUpdateListener mOpenAnimatorUpdateListener;
    private AnimatorListenerAdapter mCloseAnimatorListenerAdapter;
    private ValueAnimator.AnimatorUpdateListener mCloseAnimatorUpdateListener;

    // 是否需要调整位置，因为初始的时候如果没有触摸事件，该view不受边界约束，
    // 因此需要在渲染结束后进行微调，这个变量记录是否进行了微调。
    private boolean mAlreadyAdjust = false;

    // 是否正在动画
    private boolean mIsInAnimation = false;

    // 当前是否是圆形
    private boolean mIsClosed = false;

    // 是否可以执行展开和闭合的动画，针对刚开始高就大于宽的情况，
    // 它是不应该有展开和闭合的能力的
    private boolean mCanDoAnimation = false;

    // 起始宽度和终止宽度
    private int mOriginWidth =0;
    private int mEndWidth =0;

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
        mInnerDistance = ta.getDimensionPixelSize (R.styleable.TrackView_inner_distance, dp2px (IN_DISTANCE_DEFAULT));
        mInnerLength = ta.getDimensionPixelSize (R.styleable.TrackView_inner_length, dp2px (IN_LENGTH_DEFAULT));
        mInnerStrokeColor = ta.getColor (R.styleable.TrackView_inner_stroke_color, IN_STROKE_COLOR_DEFAULT);
        mInnerStrokeWidth = ta.getDimensionPixelSize (R.styleable.TrackView_inner_stroke_width, dp2px (IN_STROKE_WIDTH_DEFAULT));
        mInnerContentColor = ta.getColor (R.styleable.TrackView_inner_content_color, IN_CONTENT_COLOR_DEFAULT);
        mInnerTextColor = ta.getColor (R.styleable.TrackView_inner_text_color, IN_TEXT_COLOR_DEFAULT);
        mInnerTextSize = ta.getDimensionPixelSize (R.styleable.TrackView_inner_text_size, sp2px (IN_TEXT_SIZE_DEFAULT));
        mInnerText = ta.getString (R.styleable.TrackView_inner_text);
        if(mInnerText == null){
            mInnerText = IN_TEXT_DEFAULT;
        }

        mOutStrokeColor = ta.getColor (R.styleable.TrackView_out_stroke_color, OUT_STROKE_COLOR_DEFAULT);
        mOutStrokeWidth = ta.getDimensionPixelSize (R.styleable.TrackView_out_stroke_width, dp2px (OUT_STROKE_WIDTH_DEFAULT));

        mBlankLeft = ta.getDimensionPixelSize (R.styleable.TrackView_blank_left, dp2px (BLANK_LEFT_DEFAULT));
        mBlankRight = ta.getDimensionPixelSize (R.styleable.TrackView_blank_right, dp2px (BLANK_RIGHT_DEFAULT));
        mBlankTop = ta.getDimensionPixelSize (R.styleable.TrackView_blank_top, dp2px (BLANK_TOP_DEFAULT));
        mBlankBottom = ta.getDimensionPixelSize (R.styleable.TrackView_blank_bottom, dp2px (BLANK_BOTTOM_DEFAULT));

        // 回收typedArray
        ta.recycle ();

        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 外部形状的尺寸
        mOutWidth = getWidth () - getPaddingLeft () - getPaddingRight ();
        mOutHeight = getHeight () - getPaddingBottom () -getPaddingTop ();

        if(mOutHeight >= mOutWidth){

            // 只画圆
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
            mInPath.moveTo ((float) (mOutHeight/2 + getPaddingLeft ()-mInnerLength / Math.sqrt (2)), (float) (getPaddingTop ()+mOutHeight/2-(mInnerDistance+mInnerLength / Math.sqrt (2))/2));
            mInPath.lineTo ((float) (mOutHeight/2 + getPaddingLeft ()), (float) (mInnerLength / Math.sqrt (2)+getPaddingTop ()+mOutHeight/2-(mInnerDistance+mInnerLength / Math.sqrt (2))/2));
            mInPath.lineTo ((float) (mOutHeight/2 + getPaddingLeft ()-mInnerLength / Math.sqrt (2)+mInnerLength*Math.sqrt (2)), (float) (getPaddingTop ()+mOutHeight/2-(mInnerDistance+mInnerLength / Math.sqrt (2))/2));
            mInPath.moveTo ((float) (mOutHeight/2 + getPaddingLeft ()-mInnerLength / Math.sqrt (2)), (float) (getPaddingTop ()+mOutHeight/2-(mInnerDistance+mInnerLength / Math.sqrt (2))/2+mInnerDistance));
            mInPath.lineTo ((float) (mOutHeight/2 + getPaddingLeft ()), (float) (mInnerLength / Math.sqrt (2)+getPaddingTop ()+mOutHeight/2-(mInnerDistance+mInnerLength / Math.sqrt (2))/2+mInnerDistance));
            mInPath.lineTo ((float) (mOutHeight/2 + getPaddingLeft ()-mInnerLength / Math.sqrt (2)+mInnerLength*Math.sqrt (2)), (float) (getPaddingTop ()+mOutHeight/2-(mInnerDistance+mInnerLength / Math.sqrt (2))/2+mInnerDistance));

            canvas.drawCircle (getPaddingLeft ()+mOutWidth/2, getPaddingTop ()+mOutWidth/2, mOutWidth/2, mOutPaint);
            canvas.drawCircle (getPaddingLeft ()+mOutWidth/2, getPaddingTop ()+mOutWidth/2, mOutWidth/2, mInContentPaint);
            canvas.drawPath (mInPath, mInPaint);
        }else{
            mCanDoAnimation = true;

            // 拿到起始宽度和终止宽度
            if(mOriginWidth == 0){
                mOriginWidth = getWidth ();
            }

            if(mEndWidth == 0){
                mEndWidth = getWidth () - (mOutWidth - mOutHeight);
            }

            mLeftRectF.set (getPaddingLeft (), getPaddingTop (), getPaddingLeft ()+mOutHeight, getPaddingTop ()+mOutHeight);
            mRightRectF.set (getPaddingLeft ()+mOutWidth-mOutHeight, getPaddingTop (), getPaddingLeft ()+mOutWidth, getPaddingTop ()+mOutHeight);

            // 整个view的填充色
            mInContentPath.moveTo (mOutHeight/2 + getPaddingLeft ()+mOutWidth-mOutHeight, mOutHeight+getPaddingTop ());
            mInContentPath.lineTo (mOutHeight/2+getPaddingLeft (), mOutHeight+getPaddingTop ());
            mInContentPath.arcTo (mLeftRectF, 90, 180);
            mInContentPath.lineTo (mOutWidth+getPaddingLeft ()-mOutHeight/2, getPaddingTop ());
            mInContentPath.arcTo (mRightRectF, 270, 180);
            canvas.drawPath (mInContentPath, mInContentPaint);

            // 外轮廓路径
            mOutPath.moveTo (mOutHeight/2 + getPaddingLeft ()+mOutWidth-mOutHeight, mOutHeight+getPaddingTop ());
            mOutPath.lineTo (mOutHeight/2+getPaddingLeft (), mOutHeight+getPaddingTop ());
            mOutPath.arcTo (mLeftRectF, 90, 180);
            mOutPath.lineTo (mOutWidth+getPaddingLeft ()-mOutHeight/2, getPaddingTop ());
            if(!mIsInAnimation){
                //闭合并画出文字
                mOutPath.arcTo (mRightRectF, 270, 180);
                float x = mOutWidth / 2 + getPaddingLeft ();
                float y = mOutHeight / 2 + getPaddingTop () - mInTextPaint.getFontMetrics ().top / 2 - mInTextPaint.getFontMetrics ().bottom / 2;
                canvas.drawText (mInnerText, x,y,mInTextPaint);
            }
            canvas.drawPath (mOutPath, mOutPaint);

            //内部图案路径
            mInPath.moveTo ((float) (mOutHeight/2 + getPaddingLeft ()-mInnerLength / Math.sqrt (2)), (float) (getPaddingTop ()+mOutHeight/2-(mInnerDistance+mInnerLength / Math.sqrt (2))/2));
            mInPath.lineTo ((float) (mOutHeight/2 + getPaddingLeft ()), (float) (mInnerLength / Math.sqrt (2)+getPaddingTop ()+mOutHeight/2-(mInnerDistance+mInnerLength / Math.sqrt (2))/2));
            mInPath.lineTo ((float) (mOutHeight/2 + getPaddingLeft ()-mInnerLength / Math.sqrt (2)+mInnerLength*Math.sqrt (2)), (float) (getPaddingTop ()+mOutHeight/2-(mInnerDistance+mInnerLength / Math.sqrt (2))/2));
            mInPath.moveTo ((float) (mOutHeight/2 + getPaddingLeft ()-mInnerLength / Math.sqrt (2)), (float) (getPaddingTop ()+mOutHeight/2-(mInnerDistance+mInnerLength / Math.sqrt (2))/2+mInnerDistance));
            mInPath.lineTo ((float) (mOutHeight/2 + getPaddingLeft ()), (float) (mInnerLength / Math.sqrt (2)+getPaddingTop ()+mOutHeight/2-(mInnerDistance+mInnerLength / Math.sqrt (2))/2+mInnerDistance));
            mInPath.lineTo ((float) (mOutHeight/2 + getPaddingLeft ()-mInnerLength / Math.sqrt (2)+mInnerLength*Math.sqrt (2)), (float) (getPaddingTop ()+mOutHeight/2-(mInnerDistance+mInnerLength / Math.sqrt (2))/2+mInnerDistance));
            canvas.drawPath (mInPath, mInPaint);
        }

        // 如果需要微调的话，进行微调
        if(!mAlreadyAdjust){

            // 就近靠边
            setTranslationX (mBlankLeft - getX ());
            mAlreadyAdjust = true;
        }
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

                    // 点击事件不需要移动
                    performClick ();
                } else{
                    if(x > mBlankLeft && x < (mScreenWidthInPixel - mBlankRight)
                            && mPosition != Position.LEFT
                            && mPosition != Position.RIGHT){

                        // 离开点在边界之外不需要移动，已经在边界不需要移动
                        // 回到侧面
                        if(event.getRawX () < mScreenWidthInPixel / 2){

                            // 回到最左侧
                            ObjectAnimator animator = ObjectAnimator.ofFloat (this,
                                    "TranslationX",
                                    getX (),
                                    getTranslationX () + (-1 * (x - mDisX - mBlankLeft))
                            );
                            animator.setDuration (GO_TO_BOUNDARY_INTERVAL_DEFAULT);

                            // 监听动画生命周期
                            animator.addListener (new AnimatorListenerAdapter () {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mPosition = Position.LEFT;
                                }

                                @Override
                                public void onAnimationStart(Animator animation) {
                                    mPosition = Position.FLYING;
                                }
                            });
                            animator.start ();

                        }else{

                            // 回到最右侧
                            ObjectAnimator animator = ObjectAnimator.ofFloat (this,
                                    "TranslationX",
                                    getX (),
                                    getTranslationX () + ((mScreenWidthInPixel - mBlankRight) - (getWidth () - mDisX + x))
                            );

                            // 监听动画生命周期
                            animator.addListener (new AnimatorListenerAdapter () {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mPosition = Position.RIGHT;
                                }

                                @Override
                                public void onAnimationStart(Animator animation) {
                                    mPosition = Position.FLYING;
                                }
                            });
                            animator.setDuration (GO_TO_BOUNDARY_INTERVAL_DEFAULT);
                            animator.start ();

                        }
                    }
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
                int preXRight = mScreenWidthInPixel - (x + getWidth () - mDisX);
                int preYUp = y - mDisY;
                int preYDown = mScreenHeightInPixel - (y + Math.min (mOutWidth, mOutHeight) - mDisY);

                // 处理X坐标
                if(preXLeft <= mBlankLeft){

                    // 超出左边界
                    mPosition = Position.LEFT;
                    dx = x - mLastX + mBlankLeft - preXLeft;
                    x = x + mBlankLeft - preXLeft;
                }else if(preXRight <= mBlankRight){

                    // 超出右边界
                    mPosition = Position.RIGHT;
                    dx = x - mLastX - (mBlankRight - preXRight);
                    x = x - (mBlankRight - preXRight);
                }else{

                    // 正常
                    mPosition = Position.FLYING;
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

    // 合上
    public void close(){

        // 不能执行动画，不处理
        if(!mCanDoAnimation){
            return;
        }

        // 不是合上状态不处理，正在动画不处理
        if(mIsClosed || mIsInAnimation){
            return;
        }

        // 正在移动不处理
        if(mPosition == Position.FLYING){
            return;
        }

        ValueAnimator animator = ValueAnimator.ofInt (mOriginWidth, mEndWidth);
        animator.setDuration (CLOSE_INTERVAL_DEFAULT);

        // 监听动画进度
        if (mCloseAnimatorUpdateListener == null) {
            mCloseAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener () {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewGroup.LayoutParams params = getLayoutParams ();
                    params.height = getHeight ();
                    params.width = (Integer) animation.getAnimatedValue ();

                    //如果在右侧的话需要先改变位置，再改变大小
                    if(mPosition == Position.RIGHT){
                        setTranslationX (getTranslationX () + (mLastWidth - params.width));
                        mLastWidth = params.width;
                    }

                    // 改变view大小
                    setLayoutParams (params);
                }
            };
        }
        animator.addUpdateListener (mCloseAnimatorUpdateListener);

        // 监听动画生命周期
        if (mCloseAnimatorListenerAdapter == null) {
            mCloseAnimatorListenerAdapter = new AnimatorListenerAdapter () {

                @Override
                public void onAnimationStart(Animator animation) {
                    mLastWidth = getWidth ();
                    mIsInAnimation = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsInAnimation = false;
                    mIsClosed = true;

                    // 重置方便下次使用
                    mLastWidth = 0;
                }
            };
        }
        animator.addListener(mCloseAnimatorListenerAdapter);
        animator.start ();
    }

    // 展开
    public void open(){

        // 不能执行动画，不处理
        if(!mCanDoAnimation){
            return;
        }

        // 不是合上状态不处理，正在动画不处理
        if(!mIsClosed || mIsInAnimation){
            return;
        }

        // 正在移动不处理
        if(mPosition == Position.FLYING){
            return;
        }

        // 监听动画进度
        ValueAnimator animator = ValueAnimator.ofInt (mEndWidth, mOriginWidth);
        animator.setDuration (CLOSE_INTERVAL_DEFAULT);
        if (mOpenAnimatorUpdateListener == null) {
            mOpenAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener () {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewGroup.LayoutParams params = getLayoutParams ();
                    params.height = getHeight ();
                    params.width = (Integer) animation.getAnimatedValue ();

                    //如果在右侧的话需要先改变位置，再改变大小
                    if(mPosition == Position.RIGHT){
                        setTranslationX (getTranslationX () + (mLastWidth - params.width));
                        mLastWidth = params.width;
                    }

                    // 改变view大小
                    setLayoutParams (params);
                }
            };
        }
        animator.addUpdateListener (mOpenAnimatorUpdateListener);

        // 监听动画生命周期
        if (mOpenAnimatorListenerAdapter == null) {
            mOpenAnimatorListenerAdapter = new AnimatorListenerAdapter () {

                @Override
                public void onAnimationStart(Animator animation) {
                    mIsInAnimation = true;
                    mLastWidth = getWidth ();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsInAnimation = false;
                    mIsClosed = false;

                    // 重置方便下次使用
                    mLastWidth = 0;
                }
            };
        }
        animator.addListener (mOpenAnimatorListenerAdapter);
        animator.start ();
    }

    // 设置文字
    public void setText(String text){
        mInnerText = text;
        invalidate ();
    }

    public Position getPosition(){
        return mPosition;
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

    /**
     * view所处的位置
     * 上下边界暂时不考虑
     */
    public enum Position{
        LEFT,
        RIGHT,

        // 正在滑翔
        FLYING
    }

    private void init() {

        mScreenWidthInPixel = getScreenParamsInPixel ()[0];
        mScreenHeightInPixel = getScreenParamsInPixel ()[1];

        mOutPaint = new Paint ();
        mInPaint = new Paint ();
        mInContentPaint = new Paint ();
        mInTextPaint = new Paint ();

        mOutPath = new Path ();
        mInPath = new Path ();
        mInContentPath = new Path ();

        mLeftRectF = new RectF ();
        mRightRectF = new RectF ();

        mOutPaint.setStyle (Paint.Style.STROKE);
        mOutPaint.setColor (mOutStrokeColor);
        mOutPaint.setStrokeWidth (mOutStrokeWidth);
        mOutPaint.setAntiAlias(true);

        mInPaint.setStyle (Paint.Style.STROKE);
        mInPaint.setColor (mInnerStrokeColor);
        mInPaint.setStrokeWidth(mInnerStrokeWidth);
        mInPaint.setAntiAlias(true);

        mInContentPaint.setStyle (Paint.Style.FILL);
        mInContentPaint.setColor (mInnerContentColor);

        mInTextPaint.setTextAlign (Paint.Align.CENTER);
        mInTextPaint.setStyle (Paint.Style.STROKE);
        mInTextPaint.setColor (mInnerTextColor);
        mInTextPaint.setTextSize (mInnerTextSize);
        mInTextPaint.setAntiAlias(true);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
     */
    private int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
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