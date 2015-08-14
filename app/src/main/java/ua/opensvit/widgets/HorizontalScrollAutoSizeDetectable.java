package ua.opensvit.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import ua.opensvit.R;

public class HorizontalScrollAutoSizeDetectable extends HorizontalScrollView {
    public HorizontalScrollAutoSizeDetectable(Context context) {
        super(context);
        init(null);
    }

    public HorizontalScrollAutoSizeDetectable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HorizontalScrollAutoSizeDetectable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private LinearLayout layout;

    @Override
    public void addView(View child) {
        if (getChildCount() == 0) {
            layout = new LinearLayout(getContext());
            super.addView(layout);
            layout.addView(child, new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            layout.addView(child, new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    public View getTextViewChildAt(int position) {
        return layout.getChildAt(position);
    }

    public int getTextViewChildCount() {
        return layout.getChildCount();
    }

    private int mMaxChildSize, mMinChildSize;

    private void init(AttributeSet attrs) {
        Context context = getContext();
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.HorizontalScrollAutoSizeDetectable, 0, 0);
        mMaxChildSize = (int) arr.getDimension(R.styleable
                        .HorizontalScrollAutoSizeDetectable_max_child_size,
                getResources().getDimension(R.dimen.default_horizontal_scroll_child_max_size));
        mMinChildSize = (int) arr.getDimension(R.styleable
                        .HorizontalScrollAutoSizeDetectable_min_child_size,
                getResources().getDimension(R.dimen.default_horizontal_scroll_child_min_size));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (layout != null) {
            final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            if (widthMode == MeasureSpec.UNSPECIFIED) {
                return;
            }

            int measuredWidth = 0, measuredHeight = 0;

            int countViews = layout.getChildCount();

            int specWidth = MeasureSpec.getSize(widthMeasureSpec);
            int oneWidth = specWidth / countViews;

            for (int i = 0; i < countViews; i++) {
                View child = layout.getChildAt(i);
                final int widthSpec = MeasureSpec.makeMeasureSpec(
                        specWidth, MeasureSpec.UNSPECIFIED);
                final int heightSpec = MeasureSpec.makeMeasureSpec(
                        0, MeasureSpec.UNSPECIFIED);
                child.measure(widthSpec, heightSpec);
                int width = child.getMeasuredWidth();
                int newWidth = width;
                if (width > mMaxChildSize) {
                    newWidth = mMaxChildSize;
                } else if (width < mMinChildSize) {
                    newWidth = mMinChildSize;
                }

                if (oneWidth > mMaxChildSize) {
                    newWidth = mMaxChildSize;
                } else if (oneWidth < mMinChildSize) {
                    newWidth = mMinChildSize;
                }

                measuredHeight = Math.max(child.getMeasuredHeight(), measuredHeight);

                if (width != newWidth) {
                    final int widthSpec1 = MeasureSpec.makeMeasureSpec(
                            newWidth, MeasureSpec.EXACTLY);
                    child.measure(widthSpec1, heightSpec);
                    child.getLayoutParams().width = child.getMeasuredWidth();
                    measuredWidth += child.getLayoutParams().width;
                } else {
                    measuredWidth += width;
                }
            }

            int widthSpec = MeasureSpec.makeMeasureSpec(
                    measuredWidth, MeasureSpec.EXACTLY);
            int heightSpec = MeasureSpec.makeMeasureSpec(
                    measuredHeight, MeasureSpec.EXACTLY);

            layout.measure(widthSpec, heightSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
