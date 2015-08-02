package ua.opensvit.utils;

import com.nostra13.universalimageloader.core.download.ImageDownloader;

public class ImageLoaderUtils {

    public static String wrapUrlForImageLoader(String imageUrl) {
        String url;
        if (imageUrl.indexOf("http") >= 0) {
            url = imageUrl;
        } else {
            url = ImageDownloader.Scheme.FILE.wrap(imageUrl);
        }

        return url;
    }
}
