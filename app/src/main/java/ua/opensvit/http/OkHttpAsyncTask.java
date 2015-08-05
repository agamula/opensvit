package ua.opensvit.http;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

public class OkHttpAsyncTask extends AsyncTask<Void, Void, Void> implements
        OkHttpClientRunnable.OnLoadResultListener {

    private final WeakReference<ProgressBar> mProgress;
    private final OkHttpClientRunnable mOkHttpClientRunnable;
    private String mResult;
    private boolean mSuccess;
    private OnLoadFinishedListener mOnLoadFinishedListener;

    public OkHttpAsyncTask(ProgressBar mProgress, OkHttpClientRunnable
            mOkHttpClientRunnable) {
        if (mProgress != null) {
            this.mProgress = new WeakReference<>(mProgress);
        } else {
            this.mProgress = null;
        }
        this.mOkHttpClientRunnable = mOkHttpClientRunnable;
    }

    public void setOnLoadFinishedListener(OnLoadFinishedListener mOnLoadFinishedListener) {
        this.mOnLoadFinishedListener = mOnLoadFinishedListener;
    }

    @Override
    protected final void onPreExecute() {
        super.onPreExecute();
        if (mProgress != null && mProgress.get() != null) {
            mProgress.get().setVisibility(View.VISIBLE);
        }
        mOkHttpClientRunnable.setOnLoadResultListener(this);
    }

    @Override
    protected Void doInBackground(Void... params) {
        mOkHttpClientRunnable.run();
        //SystemClock.sleep(5000);
        return null;
    }

    public String getResult() {
        return mResult;
    }

    @Override
    protected final void onPostExecute(Void res) {
        super.onPostExecute(res);
        if (mProgress != null && mProgress.get() != null) {
            mProgress.get().setVisibility(View.GONE);
        }
        if (mOnLoadFinishedListener != null) {
            if (mSuccess) {
                mOnLoadFinishedListener.onLoadFinished(mResult);
            } else {
                mOnLoadFinishedListener.onLoadError(mResult);
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnLoadFinishedListener {
        void onLoadFinished(String result);

        void onLoadError(String errMsg);
    }

    @Override
    public void onLoadResult(boolean isSuccess, String result) {
        this.mResult = result;
        this.mSuccess = isSuccess;
    }
}
