package ua.opensvit.adapters.programs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.vov.vitamio.MediaMetadataRetriever;
import ua.opensvit.R;
import ua.opensvit.data.GetUrlItem;
import ua.opensvit.data.epg.ProgramItem;
import ua.opensvit.http.CopyHttpTask;
import ua.opensvit.http.CopyUtils;
import ua.opensvit.widgets.RespondedLayout;

public class ProgramsListAdapter extends BaseAdapter {

    private final List<ProgramItem> mPrograms;
    private final List<GetUrlItem> mProgramUrls;
    private final Activity mActivity;
    private final int mPagePosition;

    public ProgramsListAdapter(Activity activity, List<ProgramItem> mPrograms, List<GetUrlItem>
            mProgramUrls, int mPagePosition) {
        this.mPrograms = mPrograms;
        this.mProgramUrls = mProgramUrls;
        this.mActivity = activity;
        this.mPagePosition = mPagePosition;
    }

    @Override
    public int getCount() {
        return mPrograms != null ? mPrograms.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return new Pair<>(mPrograms.get(position), mProgramUrls.get(position));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private int mHeight;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Pair<ProgramItem, GetUrlItem> mDataObj = (Pair<ProgramItem, GetUrlItem>) getItem(position);

        if (convertView == null) {
            final View resView = LayoutInflater.from(mActivity).inflate(R.layout.layout_program,
                    parent, false);
            convertView = resView;
            if (mHeight == 0) {
                resView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    public void onLayoutChange(View v, int left, int top, int right,
                                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        resView.removeOnLayoutChangeListener(this);
                        mHeight = bottom;

                        initContent(resView, position, mDataObj);
                    }
                });
            }
        }

        final View resView = convertView;

        if (mHeight != 0) {
            initContent(resView, position, mDataObj);
        }

            /*
            The metadata key to retrieve the information about the album title of the
            data source.
            ========
            public static final String METADATA_KEY_ALBUM = "album";
            ========
            The metadata key to retrieve the main creator of the set/album, if
            different from artist. e.g. "Various Artists" for compilation albums.
            ========
            public static final String METADATA_KEY_ALBUM_ARTIST = "album_artist";
            ========
            The metadata key to retrieve the information about the artist of the data
            source.
            ========
            public static final String METADATA_KEY_ARTIST = "artist";
            ========
            The metadata key to retrieve the any additional description of the file.
            ========
            public static final String METADATA_KEY_COMMENT = "comment";
            ========
            The metadata key to retrieve the information about the author of the data
            source.
            ========
            public static final String METADATA_KEY_AUTHOR = "author";
            ========
            The metadata key to retrieve the information about the composer of the data
            source.
            ========
            public static final String METADATA_KEY_COMPOSER = "composer";
            ========
            The metadata key to retrieve the name of copyright holder.
            ========
            public static final String METADATA_KEY_COPYRIGHT = "copyright";
            ========
            The metadata key to retrieve the date when the file was created, preferably
            in ISO 8601.
            ========
            public static final String METADATA_KEY_CREATION_TIME = "creation_time";
            ========
            The metadata key to retrieve the date when the work was created, preferably
            in ISO 8601.
            ========
            public static final String METADATA_KEY_DATE = "date";
            ========
            The metadata key to retrieve the number of a subset, e.g. disc in a
            multi-disc collection.
            ========
            public static final String METADATA_KEY_DISC = "disc";
            ========
            The metadata key to retrieve the name/settings of the software/hardware
            that produced the file.
            ========
            public static final String METADATA_KEY_ENCODER = "encoder";
            ========
            The metadata key to retrieve the person/group who created the file.
            ========
            public static final String METADATA_KEY_ENCODED_BY = "encoded_by";
            ========
            The metadata key to retrieve the original name of the file.
            ========
            public static final String METADATA_KEY_FILENAME = "filename";
            ========
            The metadata key to retrieve the content type or genre of the data source.
            ========
            public static final String METADATA_KEY_GENRE = "genre";
            ========
            The metadata key to retrieve the main language in which the work is
            performed, preferably in ISO 639-2 format. Multiple languages can be
            specified by separating them with commas.
            ========
            public static final String METADATA_KEY_LANGUAGE = "language";
            ========
            The metadata key to retrieve the artist who performed the work, if
            different from artist. E.g for "Also sprach Zarathustra", artist would be
            "Richard Strauss" and performer "London Philharmonic Orchestra".
            ========
            public static final String METADATA_KEY_PERFORMER = "performer";
            ========
            The metadata key to retrieve the name of the label/publisher.
            ========
            public static final String METADATA_KEY_PUBLISHER = "publisher";
            ========
            The metadata key to retrieve the name of the service in broadcasting
            (channel name).
            ========
            public static final String METADATA_KEY_SERVICE_NAME = "service_name";
            ========
            The metadata key to retrieve the name of the service provider in
            broadcasting.
            ========
            public static final String METADATA_KEY_SERVICE_PROVIDER = "service_provider";
            ========
            The metadata key to retrieve the data source title.
            ========
            public static final String METADATA_KEY_TITLE = "title";
            ========
            The metadata key to retrieve the number of this work in the set, can be in
            form current/total.
            ========
            public static final String METADATA_KEY_TRACK = "track";
            ========
            The metadata key to retrieve the total bitrate of the bitrate variant that
            the current stream is part of.
            ========
            public static final String METADATA_KEY_VARIANT_BITRATE = "bitrate";
            ========
            The metadata key to retrieve the playback duration of the data source.
            ========
            public static final String METADATA_KEY_DURATION = "duration";
            ========
            The metadata key to retrieve the audio codec of the work.
            ========
            public static final String METADATA_KEY_AUDIO_CODEC = "audio_codec";
            ========
            The metadata key to retrieve the video codec of the work.
            ========
            public static final String METADATA_KEY_VIDEO_CODEC = "video_codec";
            ========
            This key retrieves the video rotation angle in degrees, if available. The
            video rotation angle may be 0, 90, 180, or 270 degrees.
            ========
            public static final String METADATA_KEY_VIDEO_ROTATION = "rotate";
            ========
            If the media contains video, this key retrieves its width.
            ========
            public static final String METADATA_KEY_VIDEO_WIDTH = "width";
            ========
            If the media contains video, this key retrieves its height.
            ========
            public static final String METADATA_KEY_VIDEO_HEIGHT = "height";
            ========
            The metadata key to retrieve the number of tracks, such as audio, video,
            text, in the data source, such as a mp4 or 3gpp file.
            ========
            public static final String METADATA_KEY_NUM_TRACKS = "num_tracks";
            ========
            If this key exists the media contains audio content. if has audio, return
            1.
            ========
            public static final String METADATA_KEY_HAS_AUDIO = "has_audio";
            ========
            If this key exists the media contains video content. if has video, return
            1.
            ========
            public static final String METADATA_KEY_HAS_VIDEO = "has_video";
             */

        return convertView;
    }

    private void initContent(View resView, int position, Pair<ProgramItem, GetUrlItem> mDataObj) {
        ImageView mProgramThumbnail = (ImageView) resView.findViewById(R.id
                .program_thumbnail);
        /*

        download(position, mDataObj.second.getUrl(), mDataObj.first.getTimestamp(),
                mProgramThumbnail, mActivity);*/

        ((TextView) resView.findViewById(R.id.program_name)).setText(mDataObj.first.getTitle());
    }

    private void download(int position, String url, long timestamp, ImageView imageView, Activity
            activity) {
        if (cancelPotentialDownload(url, imageView)) {
            BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, activity, url,
                    timestamp, mHeight, new CopyHttpTask(null, url, timestamp));
            DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
            imageView.setImageDrawable(downloadedDrawable);
            task.executeOnExecutor(service);
        }
    }

    private static ExecutorService service = Executors.newSingleThreadExecutor();

    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    static class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {

        private final String url;
        private final WeakReference<ImageView> imageViewReference;
        private final WeakReference<Activity> activityReference;
        private final int height;
        private final CopyHttpTask mCopyHttpTask;
        private final long timestamp;

        public BitmapDownloaderTask(ImageView imageView, Activity activity, String url, long
                timestamp, int height, CopyHttpTask mCopyHttpTask) {
            imageViewReference = new WeakReference<>(imageView);
            this.height = height;
            activityReference = new WeakReference<>(activity);
            this.url = url;
            this.mCopyHttpTask = mCopyHttpTask;
            this.timestamp = timestamp;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap res = null;
            if (activityReference.get() != null) {
                try {
                    Context context = activityReference.get();
                    File cacheFile = CopyUtils.getCacheFile(CopyUtils.extractFileName(url,
                            timestamp));
                    if (!cacheFile.exists()) {
                        mCopyHttpTask.executeWork();
                    }

                    if (context != null) {
                        MediaMetadataRetriever mRetriever = new MediaMetadataRetriever(context);
                        try {
                            mRetriever.setDataSource(new FileInputStream(cacheFile).getFD());
                            res = mRetriever.getFrameAtTime(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mRetriever.release();
                    }
                    if (res != null && res.getHeight() != height && height != 0) {
                        int width = (res.getWidth() * height) / res.getHeight();
                        Bitmap bitmap = Bitmap.createScaledBitmap(res, width, height, false);
                        res.recycle();
                        System.gc();
                        res = bitmap;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            SystemClock.sleep(100);
            return res;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
                // Change bitmap only if this process is still associated with it
                if (this == bitmapDownloaderTask) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(Color.WHITE);
            bitmapDownloaderTaskReference =
                    new WeakReference<>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }
}
