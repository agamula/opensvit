package ua.opensvit.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewRecyclable extends ImageView{
    private Bitmap bitmap;

    public ImageViewRecyclable(Context context) {
        super(context);
    }

    public ImageViewRecyclable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewRecyclable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if (bitmap != null) {
            bitmap.recycle();
            System.gc();
        }
        this.bitmap = bm;
    }
}