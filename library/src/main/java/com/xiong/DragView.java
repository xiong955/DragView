package com.xiong;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
 * update 2018/11/1
 *
 * @version 1.0.2
 * @author: xiong
 */
public class DragView extends RelativeLayout implements View.OnTouchListener {

    // 控件偏移坐标
    private int xDelta;
    private int yDelta;

    private int xDistance;
    private int yDistance;
    // 拖动控件宽高
    private ImageView mImage;
    private int mImageWidth;
    private int mImageHeight;
    // 载体布局宽高
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
    private int marginLeft;
    private int marginTop;
    private int marginRight;
    private int marginBottom;

    private boolean isRunning;
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
            mAnimationTime = typedArray.getInt(R.styleable.DragView_animationTime, 2000);
            marginLeft = typedArray.getInt(R.styleable.DragView_marginLeft, 0);
            marginTop = typedArray.getInt(R.styleable.DragView_marginTop, 0);
            marginRight = typedArray.getInt(R.styleable.DragView_marginRight, 0);
            marginBottom = typedArray.getInt(R.styleable.DragView_marginBottom, 0);
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
        LayoutParams lp = (LayoutParams) mImage.getLayoutParams();
        if (marginLeft != 0) {
            lp.leftMargin = marginLeft;
        } else if (marginRight != 0) {
            lp.leftMargin = mLayoutWidth - mImageWidth - marginRight;
        }
        if (marginTop != 0) {
            lp.topMargin = marginTop;
        } else if (marginBottom != 0) {
            lp.topMargin = mLayoutHeight - mImageHeight - marginBottom;
        }
        mImage.setLayoutParams(lp);
    }

    public boolean onTouch(View view, MotionEvent event) {
        if (isRunning) {
            return true;
        }

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
        if (xDistance == 0 || yDistance == 0) {
            return;
        }
        if (yDistance < 100) {
            lastY = 0;
            valueAnimatorY(ValueAnimator.ofInt(yDistance, lastY));
            yDistance = lastY;
        } else if (mLayoutHeight - yDistance - mImageHeight < 100) {
            lastY = mLayoutHeight - mImageHeight;
            valueAnimatorY(ValueAnimator.ofInt(yDistance, lastY));
            yDistance = lastY;
        } else if (xDistance + mImageWidth / 2 <= mLayoutWidth / 2) {
            lastX = 0;
            valueAnimatorX(ValueAnimator.ofInt(xDistance, lastX));
            xDistance = lastX;
        } else {
            lastX = mLayoutWidth - mImageWidth;
            valueAnimatorX(ValueAnimator.ofInt(xDistance, lastX));
            xDistance = lastX;
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
                isRunning = true;
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isRunning = false;
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
                isRunning = true;
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isRunning = false;
            }
        });
        valueAnimator.start();
    }

    public boolean getWelt() {
        return mWelt;
    }

    public void setWelt(boolean welt) {
        this.mWelt = welt;
        if (welt) {
            moveNearEdge();
        }
    }

    public boolean getBounce() {
        return mBounce;
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