package ua.opensvit.api;

import android.os.StrictMode;
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
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

public class SyncHttpClient
        implements Cloneable {
    CookieStore cookieStore = new BasicCookieStore();
    DefaultHttpClient httpclient = new DefaultHttpClient();

    public SyncHttpClient() {
    }

    private static String convertStreamToString(InputStream paramInputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(paramInputStream));
        String s;
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

    public SyncHttpClient clone()
            throws CloneNotSupportedException {
        return (SyncHttpClient) super.clone();
    }

    public String get(String paramString) throws IOException {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        //this.httpclient.setCookieStore(this.cookieStore);
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
