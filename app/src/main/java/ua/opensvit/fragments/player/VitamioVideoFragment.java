package ua.opensvit.fragments.player;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import ua.opensvit.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.utils.WindowUtils;
import ua.opensvit.widgets.RespondedLayout;

public class VitamioVideoFragment extends VitamioVideoBaseFragment implements MediaPlayer
        .OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnTimedTextListener {

    private static final String VIDEO_WIDTH = "video_width";
    private static final String VIDEO_HEIGHT = "video_height";

    public static VitamioVideoFragment newInstance(String url, int channelId, int serviceId, long
            timestamp, int videoWidth, int videoHeight) {
        VitamioVideoFragment fragment = new VitamioVideoFragment();
        Bundle bundle = fragment.getArgsBundle(url, channelId, serviceId, timestamp);
        bundle.putInt(VIDEO_WIDTH, videoWidth);
        bundle.putInt(VIDEO_HEIGHT, videoHeight);
        fragment.setArguments(bundle);
        return fragment;
    }

    private ProgressBar mProgress;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgress = (ProgressBar) view.findViewById(R.id.load_video_program_progress);
        mProgress.setVisibility(View.VISIBLE);

        final VideoView mVideoView = getVideoView();

        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnTimedTextListener(this);

        RespondedLayout respondedLayout = getRespondedLayout();
        respondedLayout.setOnLayoutHappenedListener(new RespondedLayout.OnLayoutHappenedListener() {
            @Override
            public void onLayoutHappened(RespondedLayout layout) {
                final VideoView mVideoView = getVideoView();
                mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
                layout.setReactOnLayout(false);
                onPostViewCreated();
            }
        });

        respondedLayout.setReactOnLayout(true);
        respondedLayout.requestLayout();
    }

    @Override
    public void onResume() {
        if (mForceBack) {
            getActivity().onBackPressed();
            return;
        }
        super.onResume();
    }

    @Override
    protected void onPreShowView(View view) {
        super.onPreShowView(view);
        if (view.getResources().getConfiguration().orientation == Configuration
                .ORIENTATION_PORTRAIT) {
            mForceBack = true;
        } else {
            mForceBack = false;
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
    }

    private boolean mForceBack;

    @Override
    public int getLayoutId() {
        return R.layout.videoview;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        String a = "asdas";
        String b = a + "asd";
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        String a = "asdas";
        String b = a + "asd";
    }

    @Override
    public void onTimedText(String s) {
        String a = "asdas";
        String b = a + "asd";
    }

    @Override
    public void onTimedTextUpdate(byte[] bytes, int i, int i1) {
        String a = "asdas";
        String b = a + "asd";
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        super.onPrepared(mediaPlayer);
        VideoView videoView = getVideoView();
        videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_ZOOM, 0);
        mProgress.setVisibility(View.GONE);
        videoView.start();
    }
}
