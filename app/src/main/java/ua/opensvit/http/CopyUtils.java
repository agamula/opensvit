package ua.opensvit.http;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class CopyUtils {
    private CopyUtils() {

    }

    private static final int BUFFER_SIZE = 4096;
    private static final String CACHE_NAME = "video.ts";

    public static void copy(InputStream is, OnProgressChangedListener progressChangedListener) {
        byte bytes[] = new byte[BUFFER_SIZE];

        try {
            File f = new File(Environment.getExternalStorageDirectory(), CACHE_NAME);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            OutputStream stream = new FileOutputStream(f);
            int count = 0;
            int available = is.available() + 1;
            int countWritten = 0;
            for (; (count = is.read(bytes)) != -1; ) {
                stream.write(bytes, 0, count);
                countWritten += count;
                progressChangedListener.onProgressChanged((int) (100 * ((float) countWritten) /
                        available));
            }
            stream.flush();
            stream.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
