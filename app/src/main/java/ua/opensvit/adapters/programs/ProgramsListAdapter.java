package ua.opensvit.adapters.programs;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import io.vov.vitamio.MediaMetadataRetriever;
import ua.opensvit.R;
import ua.opensvit.data.GetUrlItem;
import ua.opensvit.data.epg.ProgramItem;

public class ProgramsListAdapter extends BaseAdapter {

    private final List<ProgramItem> mPrograms;
    private final List<GetUrlItem> mProgramUrls;
    private final Activity mActivity;

    public ProgramsListAdapter(Activity activity, List<ProgramItem> mPrograms, List<GetUrlItem>
            mProgramUrls) {
        this.mPrograms = mPrograms;
        this.mProgramUrls = mProgramUrls;
        this.mActivity = activity;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.layout_program, parent,
                    false);
        }

        final Pair<ProgramItem, GetUrlItem> mDataObj = (Pair<ProgramItem, GetUrlItem>) getItem(position);

        final ImageView mProgramThumbnail = (ImageView) convertView.findViewById(R.id
                .program_thumbnail);

        if (mProgramThumbnail.getDrawable() == null) {
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    try {
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever(mActivity);
                        retriever.setDataSource(mActivity, Uri.parse(mDataObj.second.getUrl()));
                        //retriever.extractMetadata(key)
                        return retriever.getFrameAtTime(-1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    if (bitmap != null) {
                        mProgramThumbnail.setImageBitmap(bitmap);
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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
        }

        return convertView;
    }
}
