package com.yatang.xc.xcr.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.jocerly.jcannotation.utils.JCLoger;

/**
 * 加载了按下特效的TextView
 * Created by dengjiang on 2017/5/15.
 */

public class PressTextView extends AppCompatTextView {
    private ObjectAnimator animatorX, animatorY, animatorXUP, animatorYUP;
    private AnimatorSet animSet, animSetUP;
    private PressTextView pressTextView;
    private static long ANOMATOR_TIME = 100;
    private static float SCALE = 0.8f;
    private boolean isEnable = true;

    public PressTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        pressTextView = this;
        animSetUP = new AnimatorSet();
        animSet = new AnimatorSet();
    }

    public void setEnabled(boolean enable) {
        isEnable = enable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    AnimatorDown();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    AnimatorUp(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    AnimatorUp(false);
                    break;
            }
            return super.onTouchEvent(event);
        }
        return false;
    }

    /**
     * 播放一个放大的动画.
     */
    private void AnimatorUp(final boolean click) {
        if (!isEnabled()) {
            return;
        }
        animatorXUP = ObjectAnimator.ofFloat(this, "scaleX", SCALE, 1f);
        animatorYUP = ObjectAnimator.ofFloat(this, "scaleY", SCALE, 1f);
        animSetUP.removeAllListeners();
        animSetUP.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (click) {
                    try {
                        callSuperPerformClick();
                    } catch (Exception e) {
                        JCLoger.debug("PressTextView"+e.toString());
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animSetUP.play(animatorXUP).with(animatorYUP);
        animSetUP.setDuration(ANOMATOR_TIME);
        animSetUP.start();
    }

    /**
     * 播放一个缩小的动画.
     */
    private void AnimatorDown() {
        if (!isEnabled()) {
            return;
        }
        animatorX = ObjectAnimator.ofFloat(this, "scaleX", 1f, SCALE);
        animatorY = ObjectAnimator.ofFloat(this, "scaleY", 1f, SCALE);
        animSet.play(animatorX).with(animatorY);
        animSet.setDuration(ANOMATOR_TIME);
        animSet.start();
    }

    @Override
    public boolean performClick() {
        AnimatorUp(true);
        return false;
    }

    private void callSuperPerformClick() {
        super.performClick();
    }
}
