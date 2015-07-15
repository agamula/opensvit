package ua.opensvit.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import ua.opensvit.R;
import ua.opensvit.data.constants.TriangleViewConstants;

public class TriangleTextView extends View {

    private int mCorner;
    private int mTextDirection;
    private ColorStateList mTextColor;
    private float mTextSize;
    private Paint mTextPaint;
    private Paint mBackgroundPaint;
    private boolean mBackgroundSet;
    private ColorStateList mBackgroundColor;
    private String mText;
    private Path mTextPath;

    public TriangleTextView(Context context) {
        this(context, null);
    }

    public TriangleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        mBackgroundSet = true;
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        mBackgroundSet = true;
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        mBackgroundSet = true;
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        mBackgroundSet = true;
    }

    private void initTextColor() {
        int color = mTextColor.getColorForState(getDrawableState(), 0);
        mTextPaint.setColor(color);
    }

    private void init(AttributeSet attrs) {
        Context context = getContext();
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.TriangleTextView, 0, 0);

        String typefacePath = null;

        for(int i = 0; i < arr.getIndexCount(); i++) {
            int index = arr.getIndex(i);

            switch (index) {
                case R.styleable.TriangleTextView_triangle_corner:
                    mCorner = arr.getInt(index, TriangleViewConstants.TriangleCorner.LEFT_TOP);
                    break;
                case R.styleable.TriangleTextView_triangle_text_direction:
                    mTextDirection = arr.getInt(index, TriangleViewConstants.TextDirection.LTR);
                    break;
                case R.styleable.TriangleTextView_triangle_text_color:
                    mTextColor = arr.getColorStateList(index);
                    break;
                case R.styleable.TriangleTextView_triangle_text_size:
                    mTextSize = arr.getDimensionPixelSize(index, context.getResources()
                            .getDimensionPixelSize(R.dimen.text_size_default));
                    break;
                case R.styleable.TriangleTextView_triangle_text_font:
                    typefacePath = arr.getString(index);
                    break;
                case R.styleable.TriangleTextView_triangle_text:
                    mText = arr.getString(index);
                    break;
            }
        }

        Typeface tf = Typeface.createFromAsset(context.getAssets(), typefacePath);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTypeface(tf);

        mTextPath = new Path();

        arr.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!mBackgroundSet) {
            switch (mCorner) {
                case TriangleViewConstants.TriangleCorner.LEFT_TOP:
                    break;
                case TriangleViewConstants.TriangleCorner.LEFT_BOTTOM:
                    break;
                case TriangleViewConstants.TriangleCorner.RIGHT_TOP:
                    break;
                case TriangleViewConstants.TriangleCorner.RIGHT_BOTTOM:
                    break;
            }
        }

        initTextColor();

        mTextPath.reset();

        canvas.drawTextOnPath(mText, mTextPath, 0, 0, mTextPaint);
        //TODO implement
    }
}
