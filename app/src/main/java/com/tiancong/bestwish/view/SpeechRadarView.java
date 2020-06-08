package com.tiancong.bestwish.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.tiancong.bestwish.R;

public class SpeechRadarView extends View {


    private static final long ANIMATION_CIRCLE_TIMEOUT = 2000;
    private static final long ANIMATION_LOADING_TIMEOUT = 1000;
    private ValueAnimator mShakeAnimatorTimer;

    private int mFixedRadius = 0;
    private int mMaxRadius = 0;
    private TextPaint mPaint;

    private AnimationCircle[] mAnimationCircle = new AnimationCircle[2];
    private float mBullketStrokeWidthSize;

    private AnimatorSet mAnimatorTimerSet = null;
    private AnimatorSet mNextAnimatorTimerSet = null;
    private ValueAnimator mTransformAnimatorTimer;

    private int ANIMATION_MAIN_COLOR = R.color.springgreen;
    private static final int MAIN_COLOR = R.color.antiquewhite;


    public static final int STATE_LOADING = 0;
    public static final int STATE_LISTENING = 1;

    private int mCurrentState = STATE_LOADING;
    private int mNextState = STATE_LOADING; //过渡值

    private int LOADING_STOKE_WIDTH = 0;

    private int LOADING_START_ANGLE = 90;
    private int mCurrentAngle =  LOADING_START_ANGLE;

    private int mTransformLoadingColor = Color.TRANSPARENT;
    private int mTransformListenningColor = Color.TRANSPARENT;

    private boolean isPlaying = false;

    private float mShakeRatio = 0;
    private float mNextShakeRatio = 0;
    private long mStartShakeTime  = 0;
    private int mMaxShakeRange = 100;

    public SpeechRadarView(Context context) {
        this(context,null);
    }

    public SpeechRadarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SpeechRadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaint();
    }

    private void setState(int state) {
        if(this.mNextState==state) {
            return;
        }
        this.mNextState = state;

    }

    private void initPaint() {
        // 实例化画笔并打开抗锯齿
        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG );
        mPaint.setAntiAlias(true);
        mPaint.setPathEffect(new CornerPathEffect(10)); //设置线条类型
        mPaint.setStrokeWidth(dip2px(1));
        mPaint.setTextSize(dip2px((12)));
        mPaint.setStyle(Paint.Style.STROKE);

        mBullketStrokeWidthSize = (int) dip2px(7);
        LOADING_STOKE_WIDTH = (int) dip2px(5);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if(widthMode!=MeasureSpec.EXACTLY){
            width = (int) dip2px(210);
        }
        if(heightMode!=MeasureSpec.EXACTLY){
            height = (int) dip2px(210);
        }
        setMeasuredDimension(width,height);

    }

    public float dip2px(int dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getResources().getDisplayMetrics());
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int width = getWidth();
        int height = getHeight();

        if(width==0 || height==0) return;

        int centerX = width/2;
        int centerY = height/2;

        int diameter = Math.min(width, height) / 2;
        mFixedRadius = diameter/2;
        mMaxRadius = diameter;
        initAnimationCircle();

        if(!isInEditMode() && !isPlaying) return;

        int layerId = saveLayer(canvas, centerX, centerY);

        if(mNextState==mCurrentState){

            if(mCurrentState==STATE_LISTENING) {
                drawAnimationCircle(canvas);
                drawFixCircle(canvas,MAIN_COLOR);
                drawFlashBullket(canvas,Color.WHITE,mShakeRatio);
                mShakeRatio = 0;
            }else if(mCurrentState==STATE_LOADING){
                drawLoadingArc(canvas,MAIN_COLOR);
                drawFlashBullket(canvas,MAIN_COLOR,0);
            }
        }else {
            if(this.mNextState==STATE_LISTENING){

                drawLoadingArc(canvas,mTransformLoadingColor);
                drawFixCircle(canvas,mTransformListenningColor);
                drawFlashBullket(canvas,Color.WHITE,0);

            }else{
                drawFixCircle(canvas, mTransformListenningColor);
                drawLoadingArc(canvas,mTransformLoadingColor);
                drawFlashBullket(canvas,MAIN_COLOR,0);
            }
        }

        restoreLayer(canvas,layerId);

    }

    private void drawLoadingArc(Canvas canvas,int color) {
        int oldColor = mPaint.getColor();
        Paint.Style style = mPaint.getStyle();
        float strokeWidth = mPaint.getStrokeWidth();


        mPaint.setStrokeWidth(LOADING_STOKE_WIDTH);
        int innerOffset = LOADING_STOKE_WIDTH/2;
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);

        canvas.drawArc(new RectF(-mFixedRadius+innerOffset,-mFixedRadius+innerOffset,mFixedRadius-innerOffset,mFixedRadius-innerOffset),mCurrentAngle,270,false,mPaint);


        mPaint.setColor(oldColor);
        mPaint.setStyle(style);
        mPaint.setStrokeWidth(strokeWidth);
    }

    private void drawFlashBullket(Canvas canvas,int color,float fraction) {
        int bullketZoneWidth = mFixedRadius;
        int bullketZoneHeight = mFixedRadius * 2 / 3;
        int minHeight = (int) (bullketZoneHeight/ 3f);
        int maxRangeHeight = (int) (bullketZoneHeight*2/3f);
        drawFlashBullket(canvas, bullketZoneWidth,color, minHeight,  (maxRangeHeight *fraction));
    }

    private void drawFlashBullket(Canvas canvas,int width,int color,int height,float delta) {


        int offset = (int) ((width- mBullketStrokeWidthSize *4)/3);
        int oldColor = mPaint.getColor();
        float strokeWidth = mPaint.getStrokeWidth();

        if(delta<0f){
            delta = 0f;
        }

        mPaint.setColor(color);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mBullketStrokeWidthSize);
        for (int i=0;i<4;i++) {
            int startX = (int) (i*(offset+ mBullketStrokeWidthSize) - width / 2 + mBullketStrokeWidthSize /2);
            if(i==0|| i==3) {
                canvas.drawLine(startX, -height/2+delta*1/3, startX, height/2+delta*1/3, mPaint);
            }else{
                canvas.drawLine(startX, -(height/2+delta*2/3), startX, (height/2+delta*2/3), mPaint);
            }
        }

        mPaint.setColor(oldColor);
        mPaint.setStrokeWidth(strokeWidth);
    }

    private void drawAnimationCircle(Canvas canvas) {
        for (int i=0;i<mAnimationCircle.length;i++) {
            AnimationCircle circle = mAnimationCircle[i];
            if(circle.radius>mFixedRadius) {
                drawCircle(canvas, circle.color, circle.radius);
            }
        }
    }

    private void initAnimationCircle() {
        for (int i=0;i<mAnimationCircle.length;i++) {
            if(mAnimationCircle[i]==null) {
                if (i == 0) {
                    mAnimationCircle[i] = new AnimationCircle(mMaxRadius, mFixedRadius, 0x88FF8C14);
                } else {
                    mAnimationCircle[i] = new AnimationCircle(mMaxRadius, mFixedRadius, 0x99FF8C14);
                }
            }else{
                if(mAnimationCircle[i].token!=mMaxRadius){
                    mAnimationCircle[i].radius = mFixedRadius;
                    mAnimationCircle[i].token = mMaxRadius;
                }
            }

        }
    }


    private void drawCircle(Canvas canvas,int color,int radius) {
        int oldColor = mPaint.getColor();
        Paint.Style style = mPaint.getStyle();
        float strokeWidth = mPaint.getStrokeWidth();


        mPaint.setStrokeWidth(0);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0,0,radius,mPaint);


        mPaint.setColor(oldColor);
        mPaint.setStyle(style);
        mPaint.setStrokeWidth(strokeWidth);
    }

    private void restoreLayer(Canvas canvas, int save) {
        canvas.restoreToCount(save);
    }

    private int saveLayer(Canvas canvas, int centerX, int centerY) {
        int save = canvas.save();
        canvas.translate(centerX,centerY);
        return save;
    }

    private void drawFixCircle(Canvas canvas,int color) {
        drawCircle(canvas,color,mFixedRadius);
    }

    public void startPlay(final int state){
        post(new Runnable() {
            @Override
            public void run() {

                setState(state);
                if(!isPlaying){
                    mCurrentState=mNextState;
                }
                isPlaying = true;
                if(mNextState==mCurrentState) {
                    if (state == STATE_LISTENING) {
                        startListenningAnim();
                    } else if (state == STATE_LOADING) {
                        startLoadingAnim();
                    }
                }else{
                    startTransformAnim();
                }
            }
        });
    }

    public void startLoadingAnim(){

        if(mAnimatorTimerSet!=null){
            mAnimatorTimerSet.cancel();
        }

        mAnimatorTimerSet = getAnimatorLoadingSet();
        if(mAnimatorTimerSet!=null) {
            mAnimatorTimerSet.start();
        }
    }
    private void startTransformAnim() {
        if(mNextAnimatorTimerSet!=null){
            mNextAnimatorTimerSet.cancel();
        }
        if(mTransformAnimatorTimer!=null){
            mTransformAnimatorTimer.cancel();
        }
        mTransformAnimatorTimer = buildTransformAnimatorTimer(mCurrentState, mNextState);

        if(mNextState==STATE_LISTENING) {
            mNextAnimatorTimerSet = getAnimatorCircleSet();
        }else{
            mNextAnimatorTimerSet = getAnimatorLoadingSet();
        }

        if(mTransformAnimatorTimer!=null) {
            mTransformAnimatorTimer.start();
        }
        if(mNextAnimatorTimerSet!=null) {
            mNextAnimatorTimerSet.start();
        }
    }

    public void startListenningAnim(){
        if(mAnimatorTimerSet!=null){
            mAnimatorTimerSet.cancel();
        }
        AnimatorSet animatorTimerSet = getAnimatorCircleSet();
        if (animatorTimerSet == null) return;

        mAnimatorTimerSet = animatorTimerSet;
        mAnimatorTimerSet.start();
    }

    @Nullable
    private AnimatorSet getAnimatorCircleSet() {
        AnimatorSet animatorTimerSet = new AnimatorSet();
        ValueAnimator firstAnimatorTimer = buildCircleAnimatorTimer(mAnimationCircle[0]);
        ValueAnimator secondAnimatorTimer = buildCircleAnimatorTimer(mAnimationCircle[1]);
        if (firstAnimatorTimer == null || secondAnimatorTimer==null) return null;
        secondAnimatorTimer.setStartDelay(ANIMATION_CIRCLE_TIMEOUT /2);
        animatorTimerSet.playTogether(firstAnimatorTimer,secondAnimatorTimer);
        return animatorTimerSet;
    }



    @Nullable
    private AnimatorSet getAnimatorLoadingSet() {
        ValueAnimator valueAnimator = buildLoadingAnimatorTimer();
        if(valueAnimator==null) return null;
        AnimatorSet animatorTimerSet = new AnimatorSet();
        animatorTimerSet.play(valueAnimator);
        return animatorTimerSet;
    }

    @Nullable
    private ValueAnimator buildCircleAnimatorTimer(final AnimationCircle circle) {
        if(mFixedRadius<=0 || circle==null) return null;
        ValueAnimator animatorTimer = ValueAnimator.ofFloat(mFixedRadius,getWidth()/2);
        animatorTimer.setDuration(ANIMATION_CIRCLE_TIMEOUT);
        animatorTimer.setRepeatCount(ValueAnimator.INFINITE);
        animatorTimer.setInterpolator(new LinearInterpolator());
        animatorTimer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float dx = (float) animation.getAnimatedValue();
                float fraction = 1- animation.getAnimatedFraction();

                int radius = (int) dx;
                int color = argb((int) (Color.alpha(ANIMATION_MAIN_COLOR)*fraction),Color.red(ANIMATION_MAIN_COLOR),Color.green(ANIMATION_MAIN_COLOR),Color.blue(ANIMATION_MAIN_COLOR));

                if(mCurrentState!=mNextState){
                    color = Color.TRANSPARENT;
                }
                if(circle.radius!=radius || circle.color!=color){
                    circle.radius = radius;
                    circle.color = color;
                    postInvalidate();
                }

            }
        });
        return animatorTimer;
    }

    @Nullable
    private ValueAnimator buildLoadingAnimatorTimer() {
        if(mFixedRadius<=0) return null;
        ValueAnimator animatorTimer = ValueAnimator.ofFloat(0,1);
        animatorTimer.setDuration(ANIMATION_LOADING_TIMEOUT);
        animatorTimer.setRepeatCount(ValueAnimator.INFINITE);
        animatorTimer.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorTimer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                int angle = (int) (LOADING_START_ANGLE +  fraction*360);
                if(mCurrentAngle!=angle) {
                    mCurrentAngle = angle;
                    postInvalidate();
                }
            }
        });
        return animatorTimer;
    }


    @Nullable
    private ValueAnimator buildTransformAnimatorTimer(final int currentState,final int nextState) {
        if(mFixedRadius<=0) return null;

        final int alpha = Color.alpha(MAIN_COLOR);
        final int red = Color.red(MAIN_COLOR);
        final int green = Color.green(MAIN_COLOR);
        final int blue = Color.blue(MAIN_COLOR);


        ValueAnimator animatorTimer = ValueAnimator.ofFloat(currentState,nextState);
        animatorTimer.setDuration(ANIMATION_LOADING_TIMEOUT);
        animatorTimer.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorTimer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                if(mCurrentState!=mNextState) {
                    mTransformListenningColor   = argb((int) (alpha*animatedValue),red,green,blue);
                    mTransformLoadingColor      = argb((int) (alpha*(1-animatedValue)),red,green,blue);
                    postInvalidate();
                }

            }
        });

        animatorTimer.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                resetAnimationState();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                resetAnimationState();
            }
        });
        return animatorTimer;
    }

    private void resetAnimationState() {
        mCurrentState = mNextState;

        if(mAnimatorTimerSet!=null ){
            if(mAnimatorTimerSet!=mNextAnimatorTimerSet) {
                mAnimatorTimerSet.cancel();
            }
        }
        mAnimatorTimerSet = mNextAnimatorTimerSet;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPlay();

    }

    public void stopPlay(){
        isPlaying = false;
        mCurrentAngle =  LOADING_START_ANGLE;
        try {
            if (mAnimatorTimerSet != null) {
                mAnimatorTimerSet.cancel();
            }
            if (mNextAnimatorTimerSet != null) {
                mNextAnimatorTimerSet.cancel();
            }
            if (mShakeAnimatorTimer != null) {
                mShakeAnimatorTimer.cancel();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        resetAnimationCircle();
        postInvalidate();
    }

    private void resetAnimationCircle() {
        for (AnimationCircle circle : mAnimationCircle){
            if(circle!=null){
                circle.radius = mFixedRadius;
            }
        }
    }

    public static int argb(
            @IntRange(from = 0, to = 255) int alpha,
            @IntRange(from = 0, to = 255) int red,
            @IntRange(from = 0, to = 255) int green,
            @IntRange(from = 0, to = 255) int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public boolean isPlaying() {
        return isPlaying;
    }


    private void updateShakeRatio(final float ratio) {
        long currentTimeMillis = System.currentTimeMillis();
        if(currentTimeMillis-mStartShakeTime>=150) {
            mNextShakeRatio = ratio;
            if(mShakeRatio!=mNextShakeRatio){
                startShakeAnimation();
            }
            mStartShakeTime = currentTimeMillis;
        }
    }


    private void startShakeAnimation() {

        if(mShakeAnimatorTimer!=null){
            mShakeAnimatorTimer.cancel();
        }

        mShakeAnimatorTimer = ValueAnimator.ofFloat(mShakeRatio,mNextShakeRatio);
        mShakeAnimatorTimer.setDuration(100);
        mShakeAnimatorTimer.setInterpolator(new AccelerateDecelerateInterpolator());
        mShakeAnimatorTimer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float ratio = (float) animation.getAnimatedValue();
                if(mShakeRatio!=ratio) {
                    mShakeRatio = ratio;
                    postInvalidate();
                }
            }
        });

        mShakeAnimatorTimer.start();

    }

    public void setMaxShakeRange(int maxShakeRange) {
        this.mMaxShakeRange = maxShakeRange;
        if(this.mMaxShakeRange<=0) this.mMaxShakeRange = 100;
    }

    public void updateShakeValue(int volume) {

        if(this.getVisibility()!=View.VISIBLE || !isAttachedToWindow()) return;
        if( !isPlaying) return;

        float ratio = volume*1.0f/this.mMaxShakeRange;

        if(ratio<1f/4){
            ratio = 0;
        }
        if(ratio>=1f/4 && ratio<2f/4){
            ratio = 1f/4;
        }
        if(ratio>=2f/4 && ratio<3f/4){
            ratio = 2f/4;
        }

        if(ratio>=3f/4){
            ratio = 1f;
        }
        updateShakeRatio(ratio);
    }

    public boolean isAttachedToWindow() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
            return super.isAttachedToWindow();
        }else{
            return getWindowToken()!=null;
        }
    }


    private static class AnimationCircle{
        private int radius;
        private int color;
        private int token;

        AnimationCircle(int token ,int radius,int color){
            this.radius  =radius;
            this.color   = color;
            this.token   = token;
        }
    }
}
