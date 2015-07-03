package ua.ic.levtv.library;

import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import io.vov.vitamio.utils.IOUtils;

public class SyncHttpClient
        implements Cloneable {
    CookieStore cookieStore = new BasicCookieStore();
    DefaultHttpClient httpclient = new DefaultHttpClient();

    public SyncHttpClient() {
    }

    private static String convertStreamToString(InputStream paramInputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(paramInputStream));
        String s = null;
        StringBuilder res = new StringBuilder();
        try {
            while ((s = reader.readLine()) != null) {
                res.append(s).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return res.toString();
    }

    private boolean mSetAlive;

    public void keepAlive(final boolean keepAlive) {
        mSetAlive = true;
        System.setProperty("http.keepAlive", "" + keepAlive);
        httpclient.setReuseStrategy(new ConnectionReuseStrategy() {
            @Override
            public boolean keepAlive(HttpResponse httpResponse, HttpContext httpContext) {
                return keepAlive;
            }
        });
    }

    public SyncHttpClient clone()
            throws CloneNotSupportedException {
        return (SyncHttpClient) super.clone();
    }

    public String get(String paramString) throws IOException {
        if(!mSetAlive) {
            httpclient.setReuseStrategy(null);
        }
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        this.httpclient.setCookieStore(this.cookieStore);
        String res = null;
        HttpGet httpGet = new HttpGet(paramString);
        try {
            HttpResponse response = this.httpclient.execute(httpGet);
            Log.i("Praeda", response.getStatusLine().toString());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = entity.getContent();
                res = convertStreamToString(inputStream);
                inputStream.close();
                this.cookieStore = this.httpclient.getCookieStore();
            }
        } catch (Exception e) {
            e.printStackTrace();
            res = null;
        }
        return res;
    }
}
