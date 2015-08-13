package ua.opensvit.http;

import android.os.AsyncTask;
import android.widget.SeekBar;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CopyHttpTask extends AsyncTask<Void, Integer, Void> implements OnProgressChangedListener {

    private final SeekBar mSeekBar;
    private final String url;
    private final long timestamp;

    public CopyHttpTask(SeekBar mSeekBar, String url, long timestamp) {
        this.mSeekBar = mSeekBar;
        this.url = url;
        this.timestamp = timestamp;
    }

    @Override
    protected Void doInBackground(Void... params) {
        executeWork();
        return null;
    }

    public void executeWork() {
        try {
            int ind1 = url.indexOf("!");
            int ind2 = url.lastIndexOf(".");

            String lastPath = url.substring(ind2);
            String cachePathName = timestamp + "_" + lastPath.substring(lastPath.indexOf("=") + 1,
                    lastPath.length());

            String clientUrl = url.substring(0, ind1) + URLEncoder.encode(url.substring(ind1,
                    ind2), "UTF-8") + url.substring(ind2);

            Request.Builder builder = new Request.Builder()
                    .url(clientUrl);

            Map<String, String> headers = new HashMap<>();
            OkHttpClientRunnable.populateHeaders(headers);

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder = builder.addHeader(entry.getKey(), entry.getValue());
            }

            Request request = builder.build();

            Response response = OkHttpClientRunnable.getCLIENT().newCall(request).execute();
            CopyUtils.copy(response.body().byteStream(), cachePathName, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mSeekBar != null) {
            mSeekBar.setProgress(values[0]);
        }
    }

    @Override
    public void onProgressChanged(int newProgress) {
        publishProgress(newProgress);
    }
}

interface OnProgressChangedListener {
    void onProgressChanged(int newProgress);
}
