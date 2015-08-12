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

    private int mVideoWidth, mVideoHeight;

    private ProgressBar mProgress;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgress = (ProgressBar) view.findViewById(R.id.load_video_program_progress);
        mProgress.setVisibility(View.VISIBLE);

        Bundle args = getArguments();

        mVideoWidth = args.getInt(VIDEO_WIDTH);
        mVideoHeight = args.getInt(VIDEO_HEIGHT);

        final VideoView mVideoView = getVideoView();
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);

        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnTimedTextListener(this);

        RespondedLayout respondedLayout = getRespondedLayout();
        respondedLayout.setOnLayoutHappenedListener(new RespondedLayout.OnLayoutHappenedListener() {
            @Override
            public void onLayoutHappened(RespondedLayout layout) {
                final VideoView mVideoView = getVideoView();
                mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
                Pair<Integer, Integer> screenSizes = WindowUtils.getScreenSizes();
                int width = screenSizes.first;
                int scHeight = screenSizes.second;
                int height = (int) ((float) (width * mVideoHeight) / mVideoWidth);
                int margin = 0;
                if (scHeight < height) {
                    height = scHeight;
                    if (!STRETCH) {
                        width = (int) ((float) (height * mVideoWidth) / mVideoHeight);
                        margin = (screenSizes.first - width) / 2;
                    }
                }
                View parentView = (View) mVideoView.getParent();
                LinearLayout.LayoutParams pars = (LinearLayout.LayoutParams) parentView.getLayoutParams();
                pars.width = width;
                pars.height = height;
                if (margin != 0) {
                    pars.leftMargin = pars.rightMargin = margin;
                }
                ViewGroup.LayoutParams parsVideoView = mVideoView.getLayoutParams();
                parsVideoView.width = width;
                parsVideoView.height = height;
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

    private static boolean STRETCH = false;

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
        getVideoView().setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
        mProgress.setVisibility(View.GONE);
        getVideoView().start();
    }
}
