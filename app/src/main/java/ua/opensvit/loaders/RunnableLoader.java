package ua.opensvit.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class RunnableLoader extends AsyncTaskLoader<String> {

    private final Runnable mRunnable;

    public RunnableLoader(Context context, Runnable mRunnable) {
        super(context);
        this.mRunnable = mRunnable;
    }

    private String res;

    @Override
    public void deliverResult(String data) {
        super.deliverResult(data);
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (res != null) {
                res = null;
            }
            return;
        }
        if (isStarted()) {
            super.deliverResult(res);
        }
    }

    @Override
    protected void onStartLoading() {
        if (res != null) {
            deliverResult(res);
        }

        if (takeContentChanged() || res == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(String data) {
        if (res != null) {
            res = null;
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (res != null) {
            res = null;
        }
    }

    @Override
    public String loadInBackground() {
        res = "";
        mRunnable.run();
        return res;
    }
}
