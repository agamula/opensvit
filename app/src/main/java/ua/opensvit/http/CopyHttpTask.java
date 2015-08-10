package ua.opensvit.http;

import android.os.AsyncTask;
import android.widget.SeekBar;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

public class CopyHttpTask extends AsyncTask<Void, Integer, Void> implements OnProgressChangedListener {

    private final SeekBar mSeekBar;
    private final String url;

    public CopyHttpTask(SeekBar mSeekBar, String url) {
        this.mSeekBar = mSeekBar;
        this.url = url;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            int ind1 = url.indexOf("!");
            int ind2 = url.lastIndexOf(".");

            String clientUrl = url.substring(0, ind1) + URLEncoder.encode(url.substring(ind1,
                    ind2), "UTF-8") + url.substring(ind2);

            Request.Builder builder = new Request.Builder()
                    .url(clientUrl);

            builder = builder.addHeader("User-Agent", OkHttpClientRunnable.USER_AGENT);

            Request request = builder.build() ;

            Response response = OkHttpClientRunnable.getCLIENT().newCall(request).execute();
            CopyUtils.copy(response.body().byteStream(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mSeekBar.setProgress(values[0]);
    }

    @Override
    public void onProgressChanged(int newProgress) {
        publishProgress(newProgress);
    }
}

interface OnProgressChangedListener {
    void onProgressChanged(int newProgress);
}
