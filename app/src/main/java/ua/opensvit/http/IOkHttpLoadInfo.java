package ua.opensvit.http;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.Util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public interface IOkHttpLoadInfo {
    RequestType requestType();

    RequestBody requestBody();

    String newUrl(String oldUrl);

    class GetLoaderCreateInfo implements IOkHttpLoadInfo {

        private Map<String, String> mParams = new HashMap<>();

        @Override
        public RequestBody requestBody() {
            return null;
        }

        @Override
        public RequestType requestType() {
            return RequestType.GET;
        }

        @Override
        public String newUrl(String oldUrl) {
            StringBuilder builder = new StringBuilder(oldUrl);
            boolean isFirst = true;
            for (String key : mParams.keySet()) {
                if (isFirst) {
                    builder.append("?").append(key).append("=").append(mParams.get(key));
                    isFirst = false;
                } else {
                    builder.append("&").append(key).append("=").append(mParams.get(key));
                }
            }
            return builder.toString();
        }

        public void addParam(String paramName, String paramValue) {
            mParams.put(paramName, paramValue);
        }
    }

    class HeadLoaderCreateInfo implements IOkHttpLoadInfo {
        @Override
        public RequestBody requestBody() {
            return null;
        }

        @Override
        public RequestType requestType() {
            return RequestType.HEAD;
        }

        @Override
        public String newUrl(String oldUrl) {
            return oldUrl;
        }
    }

    interface OnLoadProgressListener {
        void onProgress(long progress, long max);
        void onError();
    }

    class WithBodyLoaderCreateInfo implements IOkHttpLoadInfo {

        private final RequestType mRequestType;
        private final String mContent;
        private OnLoadProgressListener mOnLoadProgressListener;

        public WithBodyLoaderCreateInfo(RequestType mRequestType, String mContent) {
            this.mRequestType = mRequestType;
            this.mContent = mContent;
        }

        private boolean isFileName;

        public void setIsFileName(boolean isFileName) {
            this.isFileName = isFileName;
        }

        public void setOnLoadProgressListener(OnLoadProgressListener mOnLoadProgressListener) {
            this.mOnLoadProgressListener = mOnLoadProgressListener;
        }

        @Override
        public RequestBody requestBody() {
            if (isFileName) {
                final File contentFile = new File(mContent);
                final long length = contentFile.length();

                return new RequestBody() {

                    private static final int SEGMENT_SIZE = 2048;

                    @Override
                    public MediaType contentType() {
                        return MediaType.parse("application/zip");
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
                        Source source = null;
                        try {
                            source = Okio.source(contentFile);
                            long total = 0;
                            long read;

                            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                                total += read;
                                sink.flush();
                                if(mOnLoadProgressListener != null) {
                                    mOnLoadProgressListener.onProgress(total, length);
                                }
                            }
                        } catch (IOException e) {
                            if(mOnLoadProgressListener != null) {
                                mOnLoadProgressListener.onError();
                            }
                            throw new IOException(e);
                        } finally {
                            Util.closeQuietly(source);
                        }
                    }

                    @Override
                    public long contentLength() {
                        return length;
                    }
                };
            } else {
                return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), mContent);
            }
        }

        @Override
        public RequestType requestType() {
            return mRequestType;
        }

        @Override
        public String newUrl(String oldUrl) {
            return oldUrl;
        }
    }
}