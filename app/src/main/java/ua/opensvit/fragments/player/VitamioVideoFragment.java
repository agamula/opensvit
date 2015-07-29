package ua.opensvit.fragments.player;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;

import ua.opensvit.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VitamioVideoFragment extends Fragment implements MediaPlayer
        .OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnTimedTextListener, MediaPlayer.OnPreparedListener, MediaController.OnShownListener {

    private static final String URL_TAG = "url";

    public VitamioVideoFragment() {
    }

    public static VitamioVideoFragment newInstance(String url) {
        VitamioVideoFragment fragment = new VitamioVideoFragment();
        Bundle args = new Bundle();
        args.putString(URL_TAG, url);
        fragment.setArguments(args);
        return fragment;
    }

    private VideoView mVideoView;
    private String mPath;
    private boolean mShown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.videoview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.mVideoView = ((VideoView) view.findViewById(R.id.surface_view));
        mPath = getArguments().getString(URL_TAG);
        this.mVideoView.setVideoQuality(16);
        this.mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0.0F);
        MediaController controller = new MediaController(getActivity());
        controller.setInstantSeeking(false);
        controller.setOnShownListener(this);

        mVideoView.setMediaController(controller);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnTimedTextListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setVideoURI(Uri.parse(mPath));
        mVideoView.requestFocus();
        mShown = false;
    }

    private long mPosition;

    @Override
    public void onPause() {
        mPosition = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mPosition > 0) {
            mVideoView.seekTo(mPosition);
            mPosition = 0;
        }
        super.onResume();
        mVideoView.start();
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
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        String a = "asdas";
        String b = a + "asd";
        return false;
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
        mediaPlayer.setPlaybackSpeed(1.0f);
    }

    @Override
    public void onShown() {
        if (!mShown) {
            try {
                Field f = VideoView.class.getDeclaredField("mMediaController");
                f.setAccessible(true);
                MediaController mediaController = (MediaController) f.get(mVideoView);
                f = MediaController.class.getDeclaredField("mFileName");
                f.setAccessible(true);
                TextView mCurrentTime = (TextView) f.get(mediaController);
                mCurrentTime.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mShown = true;
        }
    }
}
