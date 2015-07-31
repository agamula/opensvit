package ua.opensvit.http;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import ua.opensvit.R;

public class OkHttpLoader extends AsyncTaskLoader<String> {
    private final OkHttpAsyncTask mTask;
    private String mResult;

    public OkHttpLoader(Context context, OkHttpAsyncTask mTask) {
        super(context);
        this.mTask = mTask;
    }

    @Override
    public String loadInBackground() {
        mTask.doInBackground();
        return null;
    }

    @Override
    public void deliverResult(String data) {
        if (isReset()) {
            return;
        }
        mTask.onPostExecute(null);
        data = mTask.getResult();
        super.deliverResult(data);
        this.mResult = data;
    }

    @Override
    protected void onStartLoading() {
        mTask.onPreExecute();
        if (mResult != null) {
            deliverResult(mResult);
        }

        if (takeContentChanged() || mResult == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();

        if (mResult != null) {
            releaseResources(mResult);
            mResult = null;
        }
    }

    @Override
    public void onCanceled(String data) {
        super.onCanceled(data);
        releaseResources(data);
    }

    private void releaseResources(String data) {
        data = null;
    }
}