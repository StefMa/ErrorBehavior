package guru.stefma.errorbehavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

public class ErrorBehavior extends CoordinatorLayout.Behavior<View> {

    private int mErrorId;

    public ErrorBehavior(@NonNull Context context) {
        this(context, null);
    }

    public ErrorBehavior(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.ErrorBehavior, 0, 0);
        mErrorId = typedArray.getResourceId(R.styleable.ErrorBehavior_error_id, -1);
        typedArray.recycle();
    }

    public void setErrorId(@IdRes int errorId) {
        mErrorId = errorId;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        if (mErrorId != -1 &&
                parent.isPointInChildBounds(child, (int) ev.getX(), (int) ev.getY())) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                View view = findIdInHierarchy((ViewGroup) child.getRootView());
                if (view != null) {
                    view.startAnimation(createAnimation());
                }
            }
        }
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Nullable
    private View findIdInHierarchy(@NonNull ViewGroup rootViewOfView) {
        int childCount = rootViewOfView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = rootViewOfView.getChildAt(i);
            if (child.getId() == mErrorId) {
                // We found the errorId. Return it!
                return child;
            } else if (child instanceof ViewGroup) {
                // Maybe another ViewGroup contains our errorId?
                return findIdInHierarchy((ViewGroup) child);
            }
        }
        return null;
    }

    private Animation createAnimation() {
        int startAndEndXDelta = 10;
        int errorXDelta = startAndEndXDelta * 2;
        int startAndEndDuration = 23;
        int errorDuration = startAndEndDuration * 2;
        int offset = startAndEndDuration;
        int errorRepeatCount = 2;

        AnimationSet animationSet = new AnimationSet(false);

        Animation animation = new TranslateAnimation(0, startAndEndXDelta, 0, 0);
        animation.setDuration(startAndEndDuration);
        animationSet.addAnimation(animation);

        for (int i = 0; i < errorRepeatCount; i++) {
            Animation leftAnimation = new TranslateAnimation(0, -errorXDelta, 0, 0);
            leftAnimation.setDuration(errorDuration);
            leftAnimation.setStartOffset(offset);
            animationSet.addAnimation(leftAnimation);

            offset += errorDuration;

            Animation backAnimation = new TranslateAnimation(0, errorXDelta, 0, 0);
            backAnimation.setDuration(errorDuration);
            backAnimation.setStartOffset(offset);
            animationSet.addAnimation(backAnimation);

            offset += errorDuration;
        }

        Animation backToStart = new TranslateAnimation(0, -startAndEndXDelta, 0, 0);
        backToStart.setDuration(startAndEndDuration);
        backToStart.setStartOffset(offset);
        animationSet.addAnimation(backToStart);

        return animationSet;
    }
}
