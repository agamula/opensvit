package ua.opensvit.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import ua.opensvit.VideoStreamApp;

public class RunnableLoader extends AsyncTaskLoader<String> {

    private Runnable mRunnable;

    public RunnableLoader() {
        super(VideoStreamApp.getInstance().getApplicationContext());
    }

    public void setRunnable(Runnable mRunnable) {
        this.mRunnable = mRunnable;
    }

    private String res;

    @Override
    public void deliverResult(String data) {
        super.deliverResult(data);
        if (isReset()) {
            // An async query came in while the loader is stopped
            clearData();
            return;
        }
        if (isStarted()) {
            super.deliverResult(res);
        }
    }

    private void clearData() {
        if (res != null) {
            res = null;
            mRunnable = null;
        }
    }

    public Runnable getRunnable() {
        return mRunnable;
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
        clearData();
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        clearData();
    }

    @Override
    public String loadInBackground() {
        res = "";
        mRunnable.run();
        return res;
    }
}
