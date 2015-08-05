package ua.opensvit.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class RespondedLayout extends LinearLayout{

    public interface OnMeasureHappenedListener {
        boolean onMeasureHappened(RespondedLayout layout);
    }

    public interface OnLayoutHappenedListener {
        void onLayoutHappened(RespondedLayout layout);
    }

    public interface OnInvalidateListener {
        boolean onInvalidate(RespondedLayout layout);
    }

    private OnMeasureHappenedListener onMeasureHappenedListener;
    private OnLayoutHappenedListener onLayoutHappenedListener;
    private OnInvalidateListener onInvalidateListener;
    private boolean measureFinished = false;
    private boolean reactOnLayout = true;

    public void setOnMeasureHappenedListener(OnMeasureHappenedListener onMeasureHappenedListener) {
        this.onMeasureHappenedListener = onMeasureHappenedListener;
    }

    public void setOnLayoutHappenedListener(OnLayoutHappenedListener onLayoutHappenedListener) {
        this.onLayoutHappenedListener = onLayoutHappenedListener;
    }

    public void setOnInvalidateListener(OnInvalidateListener onInvalidateListener) {
        this.onInvalidateListener = onInvalidateListener;
    }

    public void setReactOnLayout(boolean reactOnLayout) {
        this.reactOnLayout = reactOnLayout;
    }

    public RespondedLayout(Context context) {
        super(context);
    }

    public RespondedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RespondedLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(widthMeasureSpec != 0 && heightMeasureSpec != 0) {
            if(onMeasureHappenedListener != null) {
                if(!measureFinished) {
                    measureFinished = onMeasureHappenedListener.onMeasureHappened(this);
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(reactOnLayout && onLayoutHappenedListener != null) {
            onLayoutHappenedListener.onLayoutHappened(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(onInvalidateListener != null) {
            if(!onInvalidateListener.onInvalidate(this)) {
                onInvalidateListener = null;
            }
        }
    }
}
