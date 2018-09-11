package com.xiong;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 拖动控件
 * 2018/09/07
 * @author: xiong
 * @version 1.0
 */
public class DragView extends RelativeLayout implements View.OnTouchListener {

    // 控件偏移坐标
    private int xDelta;
    private int yDelta;

    private int xDistance;
    private int yDistance;
    // 控件 && 宽高
    private ImageView mImage;
    private int mImageWidth;
    private int mImageHeight;
    // 载体 && 宽高
    private ViewGroup mViewGroup;
    private int mLayoutWidth;
    private int mLayoutHeight;
    // 按下 && 抬起时间
    private long mDownTime;
    private long mUpTime;
    // 是否移动过
    private boolean isMove;

    // 控件属性
    private int width;
    private int height;
    private int mForeImage;
    private String mBackColor;
    private boolean mWelt;
    private boolean mBounce;
    private int mAnimationTime;

    private LayoutParams layoutParams;

    private OnClickListener OnClickListener;

    public DragView(Context context) {
        this(context, null);
    }

    public DragView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DragView);
        if (typedArray != null) {
            mForeImage = typedArray.getResourceId(R.styleable.DragView_foregroundImage, 0);
            mBackColor = typedArray.getString(R.styleable.DragView_backgroundColor);
            width = typedArray.getInt(R.styleable.DragView_dragWidth, 40);
            height = typedArray.getInt(R.styleable.DragView_dragHeight, 40);
            mWelt = typedArray.getBoolean(R.styleable.DragView_welt, false);
            mBounce = typedArray.getBoolean(R.styleable.DragView_bounce, false);
            mAnimationTime = typedArray.getInt(R.styleable.DragView_animationTime, 1000);
            typedArray.recycle();
        }

        mViewGroup = this;
        mImage = new ImageView(getContext());
        if (mBackColor != null && mBackColor.length() == 7) {
            mImage.setBackgroundColor(Color.parseColor(mBackColor));
        }
        if (mForeImage != 0) {
            mImage.setImageResource(mForeImage);
        }
        mImage.setOnTouchListener(this);
        LayoutParams lp = new LayoutParams(
                width, height);
        mViewGroup.addView(mImage, lp);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mImageWidth = mImage.getWidth();
        mImageHeight = mImage.getHeight();
        mLayoutWidth = mViewGroup.getWidth();
        mLayoutHeight = mViewGroup.getHeight();
    }

    public boolean onTouch(View view, MotionEvent event) {

        final int x = (int) event.getRawX();
        final int y = (int) event.getRawY();

        layoutParams = (LayoutParams) view
                .getLayoutParams();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                mDownTime = System.currentTimeMillis();
                xDelta = x - layoutParams.leftMargin;
                yDelta = y - layoutParams.topMargin;
                break;

            case MotionEvent.ACTION_MOVE:
                isMove = true;
                xDistance = x - xDelta;
                yDistance = y - yDelta;

                // 靠右||下,不超过最右||下
                if (mLayoutWidth - xDistance < mImageWidth) {
                    xDistance = mLayoutWidth - mImageWidth;
                }
                if (mLayoutHeight - yDistance < mImageHeight) {
                    yDistance = mLayoutHeight - mImageHeight;
                }

                // 靠左||上,不超过最左||上
                if (xDistance < 0) {
                    xDistance = 0;
                }
                if (yDistance < 0) {
                    yDistance = 0;
                }
                layoutParams.leftMargin = xDistance;
                layoutParams.topMargin = yDistance;
                view.setLayoutParams(layoutParams);
                break;

            case MotionEvent.ACTION_UP:
                // 如果抬起时间-按下时间<1s && 坐标没有改变(没有移动控件)就是点击事件
                mUpTime = System.currentTimeMillis();
                if (mUpTime - mDownTime < 1000 && !isMove) {
                    OnClickListener.onClick();
                }
                if (isMove && mWelt) {
                    moveNearEdge();
                }
                isMove = false;
                break;
        }
        mViewGroup.invalidate();
        return true;
    }

    /**
     * 移至最近的边沿
     */
    private void moveNearEdge() {
        int lastX, lastY;
        if (yDistance < 100) {
            lastY = 0;
            valueAnimatorY(ValueAnimator.ofInt(yDistance, lastY));
        } else if (mLayoutHeight - yDistance < 100) {
            lastY = mLayoutHeight - mImageHeight;
            valueAnimatorY(ValueAnimator.ofInt(yDistance, lastY));
        } else if (xDistance + mImageWidth / 2 <= mLayoutWidth / 2) {
            lastX = 0;
            valueAnimatorX(ValueAnimator.ofInt(xDistance, lastX));
        } else {
            lastX = mLayoutWidth - mImageWidth;
            valueAnimatorX(ValueAnimator.ofInt(xDistance, lastX));
        }
    }

    private void valueAnimatorX(ValueAnimator valueAnimator) {
        valueAnimator.setDuration(mAnimationTime);
        valueAnimator.setRepeatCount(0);
        if (mBounce) {
            valueAnimator.setInterpolator(new BounceInterpolator());
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.leftMargin = (int) animation.getAnimatedValue();
                mImage.setLayoutParams(layoutParams);
            }
        });
        valueAnimator.start();
    }

    private void valueAnimatorY(ValueAnimator valueAnimator) {
        valueAnimator.setDuration(mAnimationTime);
        valueAnimator.setRepeatCount(0);
        if (mBounce) {
            valueAnimator.setInterpolator(new BounceInterpolator());
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.topMargin = (int) animation.getAnimatedValue();
                mImage.setLayoutParams(layoutParams);
            }
        });
        valueAnimator.start();
    }

    public void setWelt(boolean welt) {
        this.mWelt = welt;
        if (welt) {
            moveNearEdge();
        }
    }

    public void setBounce(boolean bounce) {
        this.mBounce = bounce;
    }

    public void setAnimationTime(int AnimationTime) {
        this.mAnimationTime = AnimationTime;
    }



    /* 接口回调 */
    public interface OnClickListener {
        void onClick();
    }

    public void onClickListener(OnClickListener OnClickListener) {
        this.OnClickListener = OnClickListener;
    }
    /* 结束 */
}