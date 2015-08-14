package ua.opensvit.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;

import java.lang.ref.WeakReference;

public class InteractHandler extends Handler {

    private final Handler.Callback mCallback;

    public InteractHandler(Looper looper, Handler.Callback callback) {
        super(looper);
        this.mCallback = callback;
    }

    @Override
    public void handleMessage(Message msg) {
        Pair<WeakReference<?>, Object> obj = (Pair<WeakReference<?>, Object>) msg.obj;
        if (obj.first.get() != null) {
            msg.obj = obj.second;
            mCallback.handleMessage(msg);
        }
    }
}
