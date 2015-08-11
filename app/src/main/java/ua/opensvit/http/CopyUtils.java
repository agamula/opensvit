package ua.opensvit.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ua.opensvit.VideoStreamApp;

public class CopyUtils {
    private CopyUtils() {

    }

    private static final int BUFFER_SIZE = 4096;
    private static final String FILE_EXT = ".ts";

    public static File getCacheFile(String fileName) {
        return new File(VideoStreamApp.getInstance().getCacheDirectory(), fileName + FILE_EXT);
    }

    public static String extractFileName(String url, long timestamp) {
        int ind2 = url.lastIndexOf(".");

        String lastPath = url.substring(ind2);
        String cachePathName = timestamp + "_" + lastPath.substring(lastPath.indexOf("=") + 1,
                lastPath.length());
        return cachePathName;
    }

    public static void copy(InputStream is, String fileName, OnProgressChangedListener
            progressChangedListener) {
        byte bytes[] = new byte[BUFFER_SIZE];

        try {
            File f = getCacheFile(fileName);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            OutputStream stream = new FileOutputStream(f);
            int count = 0;
            int available = is.available() + 1;
            int countWritten = 0;
            for (; (count = is.read(bytes)) != -1; ) {
                if (countWritten >= 400000) {
                    break;
                }
                stream.write(bytes, 0, count);
                countWritten += count;
                if (progressChangedListener != null) {
                    progressChangedListener.onProgressChanged((int) (100 * ((float) countWritten) /
                            available));
                }
            }
            stream.flush();
            stream.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
