package ua.opensvit.widgets;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import java.util.Map;

public class VideoView extends io.vov.vitamio.widget.VideoView{
    public VideoView(Context context) {
        super(context);
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
    }

    @Override
    public void setVideoURI(Uri uri, Map<String, String> headers) {
        super.setVideoURI(uri, headers);
    }
}
