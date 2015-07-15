package ua.opensvit.http;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import ua.opensvit.VideoStreamApp;

public class OkHttpAsyncTask extends AsyncTask<Void, Void, Void> implements
        OkHttpClientRunnable.OnLoadResultListener {

    private final ProgressBar mProgress;
    private final OkHttpClientRunnable mOkHttpClientRunnable;
    private String mResult;
    private boolean mSuccess;
    private OnLoadFinishedListener mOnLoadFinishedListener;

    public OkHttpAsyncTask(ProgressBar mProgress, OkHttpClientRunnable
            mOkHttpClientRunnable) {
        this.mProgress = mProgress;
        this.mOkHttpClientRunnable = mOkHttpClientRunnable;
    }

    public void setOnLoadFinishedListener(OnLoadFinishedListener mOnLoadFinishedListener) {
        this.mOnLoadFinishedListener = mOnLoadFinishedListener;
    }

    @Override
    protected final void onPreExecute() {
        super.onPreExecute();
        if(mProgress != null) {
            mProgress.setVisibility(View.VISIBLE);
        }
        mOkHttpClientRunnable.setOnLoadResultListener(this);
    }

    @Override
    protected Void doInBackground(Void... params) {
        mOkHttpClientRunnable.run();
        return null;
    }

    @Override
    protected final void onPostExecute(Void res) {
        super.onPostExecute(res);
        if(mProgress != null) {
            mProgress.setVisibility(View.GONE);
        }
        if(mOnLoadFinishedListener != null) {
            if (mSuccess) {
                mOnLoadFinishedListener.onLoadFinished(mResult);
            } else {
                mOnLoadFinishedListener.onLoadError(mResult);
            }
        }
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
