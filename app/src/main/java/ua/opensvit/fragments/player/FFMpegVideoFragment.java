package ua.opensvit.fragments.player;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.appunite.ffmpeg.FFmpegError;
import com.appunite.ffmpeg.FFmpegListener;
import com.appunite.ffmpeg.FFmpegPlayer;
import com.appunite.ffmpeg.FFmpegStreamInfo;
import com.appunite.ffmpeg.FFmpegSurfaceView;
import com.appunite.ffmpeg.NotPlayingException;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ua.opensvit.R;
import ua.opensvit.http.OkHttpClientRunnable;

public class FFMpegVideoFragment extends Fragment implements FFmpegListener, AdapterView
        .OnItemSelectedListener, View.OnClickListener {

    private static final String URL_TAG = "url";

    private static final String[] PROJECTION = new String[]{"title", BaseColumns._ID};
    private static final int PROJECTION_ID = 1;


    public static FFMpegVideoFragment newInstance(String url) {
        FFMpegVideoFragment fragment = new FFMpegVideoFragment();
        Bundle args = new Bundle();
        args.putString(URL_TAG, url);
        fragment.setArguments(args);
        return fragment;
    }

    private FFmpegSurfaceView mVideoView;
    private FFmpegPlayer mMpegPlayer;

    private String url;
    private int mAudioStreamNo = FFmpegPlayer.UNKNOWN_STREAM;
    private int mSubtitleStreamNo = FFmpegPlayer.NO_STREAM;

    private Button mPlayPauseButton;

    private Spinner mLanguageSpinner;
    private int mLanguageSpinnerSelectedPosition = 0;
    private Spinner mSubtitleSpinner;
    private int mSubtitleSpinnerSelectedPosition = 0;

    private View mControlsView;
    private View mStreamsView;
    private View mLoadingView;

    private SimpleCursorAdapter mLanguageAdapter;
    private SimpleCursorAdapter mSubtitleAdapter;

    private boolean mPlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getActivity().getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DITHER);

        super.onCreate(savedInstanceState);

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.clearFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        window.setBackgroundDrawable(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ffmpeg_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Activity activity = getActivity();

        Bundle args = getArguments();
        url = args.getString(URL_TAG);
        //url = new File(Environment.getExternalStorageDirectory(), "ddmsrec.mp4")
        // .getAbsolutePath();

        mControlsView = view.findViewById(R.id.controls);
        mStreamsView = view.findViewById(R.id.streams);
        mLoadingView = activity.findViewById(R.id.progress);

        mLoadingView.setVisibility(View.VISIBLE);

        mPlayPauseButton = (Button) view.findViewById(R.id.play_pause);
        mPlayPauseButton.setOnClickListener(this);

        mLanguageSpinner = (Spinner) view.findViewById(R.id.language_spinner);
        mSubtitleSpinner = (Spinner) view.findViewById(R.id.subtitle_spinner);

        mLanguageAdapter = new SimpleCursorAdapter(activity,
                android.R.layout.simple_spinner_item, null, PROJECTION,
                new int[]{android.R.id.text1}, 0);
        mLanguageAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mLanguageSpinner.setAdapter(mLanguageAdapter);
        mLanguageSpinner.setOnItemSelectedListener(this);

        mSubtitleAdapter = new SimpleCursorAdapter(activity,
                android.R.layout.simple_spinner_item, null, PROJECTION,
                new int[]{android.R.id.text1}, 0);
        mSubtitleAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSubtitleSpinner.setAdapter(mSubtitleAdapter);
        mSubtitleSpinner.setOnItemSelectedListener(this);


        mVideoView = (FFmpegSurfaceView) view.findViewById(R.id.video_view);
        mMpegPlayer = new FFmpegPlayer(mVideoView, activity);
        mMpegPlayer.setMpegListener(this);
        setDataSource();
    }

    public void resumePause() {
        if (mPlay) {
            mMpegPlayer.pause();
        } else {
            displaySystemMenu(true);
            mMpegPlayer.resume();
        }
        mPlay = !mPlay;
    }

    private void setDataSource() {
        Map<String, String> headers = new HashMap<>();
        OkHttpClientRunnable.populateHeaders(headers);

        this.mControlsView.setVisibility(View.VISIBLE);
        this.mStreamsView.setVisibility(View.VISIBLE);
        this.mLoadingView.setVisibility(View.GONE);

        this.mPlayPauseButton
                .setBackgroundResource(android.R.drawable.ic_media_play);
        this.mPlayPauseButton.setEnabled(true);
        mPlay = false;

        mMpegPlayer.setDataSource(wrapUrlForImageLoader(url), headers, FFmpegPlayer
                        .UNKNOWN_STREAM, mAudioStreamNo,
                mSubtitleStreamNo);
    }

    public static String wrapUrlForImageLoader(String imageUrl) {
        String url;
        if (imageUrl.indexOf("http") >= 0) {
            url = imageUrl;
        } else {
            url = ImageDownloader.Scheme.FILE.wrap(imageUrl);
        }

        return url;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mMpegPlayer.setMpegListener(null);
        this.mMpegPlayer.stop();
        stop();
    }

    private void stop() {
        this.mLoadingView.setVisibility(View.GONE);
        this.mControlsView.setVisibility(View.GONE);
        this.mStreamsView.setVisibility(View.GONE);
    }

    private void setDataSourceAndResumeState() {
        setDataSource();
        //mMpegPlayer.seek(mCurrentTimeUs);
        mMpegPlayer.resume();
    }

    private void displaySystemMenu(boolean visible) {
        if (Build.VERSION.SDK_INT >= 14) {
            displaySystemMenu14(visible);
        } else if (Build.VERSION.SDK_INT >= 11) {
            displaySystemMenu11(visible);
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(11)
    private void displaySystemMenu11(boolean visible) {
        if (visible) {
            this.mVideoView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
        } else {
            this.mVideoView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
        }
    }

    @TargetApi(14)
    private void displaySystemMenu14(boolean visible) {
        if (visible) {
            this.mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            this.mVideoView
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }


    @Override
    public void onFFDataSourceLoaded(FFmpegError err, FFmpegStreamInfo[] streams) {
        if (err != null) {
            String format = getResources().getString(
                    R.string.vitamio_videoview_error_text_unknown);
            String message = String.format(format, err.getMessage());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.app_name)
                    .setMessage(message)
                    .setOnCancelListener(
                            new DialogInterface.OnCancelListener() {

                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    getActivity().finish();
                                }
                            }).show();
            return;
        }

        mPlayPauseButton.setBackgroundResource(android.R.drawable.ic_media_play);
        mPlayPauseButton.setEnabled(true);

        MatrixCursor audio = new MatrixCursor(PROJECTION);
        MatrixCursor subtitles = new MatrixCursor(PROJECTION);
        subtitles.addRow(new Object[]{"None", FFmpegPlayer.NO_STREAM});
        for (FFmpegStreamInfo streamInfo : streams) {
            FFmpegStreamInfo.CodecType mediaType = streamInfo.getMediaType();
            Locale locale = streamInfo.getLanguage();
            String languageName = locale == null ? getString(
                    R.string.unknown) : locale.getDisplayLanguage();
            if (FFmpegStreamInfo.CodecType.AUDIO.equals(mediaType)) {
                audio.addRow(new Object[]{languageName, streamInfo.getStreamNumber()});
            } else if (FFmpegStreamInfo.CodecType.SUBTITLE.equals(mediaType)) {
                subtitles.addRow(new Object[]{languageName, streamInfo.getStreamNumber()});
            }
        }
        mLanguageAdapter.swapCursor(audio);
        mSubtitleAdapter.swapCursor(subtitles);
        mPlayPauseButton.performClick();
    }

    @Override
    public void onFFResume(NotPlayingException e) {
        this.mPlayPauseButton
                .setBackgroundResource(android.R.drawable.ic_media_pause);
        this.mPlayPauseButton.setEnabled(true);

        displaySystemMenu(false);
        mPlay = true;
    }

    @Override
    public void onFFPause(NotPlayingException e) {
        this.mPlayPauseButton
                .setBackgroundResource(android.R.drawable.ic_media_play);
        this.mPlayPauseButton.setEnabled(true);
        mPlay = false;
    }

    @Override
    public void onFFStop() {

    }

    @Override
    public void onFFUpdateTime(long l, long l1, boolean b) {

    }

    @Override
    public void onFFSeeked(NotPlayingException e) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
        Cursor c = (Cursor) parentView
                .getItemAtPosition(position);
        if (parentView == mLanguageSpinner) {
            if (mLanguageSpinnerSelectedPosition != position) {
                mLanguageSpinnerSelectedPosition = position;
                mAudioStreamNo = c.getInt(PROJECTION_ID);
                setDataSourceAndResumeState();
            }
        } else if (parentView == mSubtitleSpinner) {
            if (mSubtitleSpinnerSelectedPosition != position) {
                mSubtitleSpinnerSelectedPosition = position;
                mSubtitleStreamNo = c.getInt(PROJECTION_ID);
                setDataSourceAndResumeState();
            }
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        resumePause();
    }
}
