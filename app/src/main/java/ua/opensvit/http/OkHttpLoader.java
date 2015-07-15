package ua.opensvit.http;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import ua.opensvit.R;

public class OkHttpLoader extends AsyncTaskLoader<String> implements OkHttpClientRunnable.OnLoadResultListener{
    private String mResult = null;
    private final OkHttpClientRunnable mRunnable;

    public OkHttpLoader(Context context, OkHttpClientRunnable mRunnable) {
        super(context);
        this.mRunnable = mRunnable;
    }

    @Override
    public String loadInBackground() {
        mRunnable.setOnLoadResultListener(this);
        mRunnable.run();
        return mResult;
    }

    @Override
    public void deliverResult(String data) {
        super.deliverResult(data);
        if (isReset()) {
            return;
        }
        this.mResult = data;
    }

    @Override
    protected void onStartLoading() {
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

    @Override
    public void onLoadResult(boolean isSuccess, String result) {
        mResult = result;
        if(!isSuccess && result.equals(getContext().getString(R.string.load_failed_message))) {
            mResult = null;
        }
    }
}