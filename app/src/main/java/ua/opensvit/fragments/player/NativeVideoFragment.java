package ua.opensvit.fragments.player;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

//import io.vov.vitamio.MediaPlayer;
//import io.vov.vitamio.widget.MediaController;
//import io.vov.vitamio.widget.VideoView;
import ua.opensvit.R;

public class NativeVideoFragment extends Fragment{

    private static final String URL_TAG = "url";

    public NativeVideoFragment() {
    }

    public static NativeVideoFragment newInstance(String url) {
        NativeVideoFragment fragment = new NativeVideoFragment();
        Bundle args = new Bundle();
        args.putString(URL_TAG, url);
        fragment.setArguments(args);
        return fragment;
    }

    private VideoView mVideoView;
    private String mPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.videoviewdemo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //this.mVideoView = ((VideoView) view.findViewById(R.id.surface_view));
        this.mVideoView = ((VideoView) view.findViewById(R.id.VideoView25));
        mPath = getArguments().getString(URL_TAG);
        //this.mVideoView.setVideoQuality(16);
        //this.mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0.0F);
        MediaController controller = new MediaController(getActivity());
        /*controller.setFileName("File name");
        controller.setAnchorView(view);
        controller.setInstantSeeking(true);
        controller.setEnabled(true);*/

        mVideoView.setVideoURI(Uri.parse(mPath));
        mVideoView.setMediaController(controller);
        mVideoView.requestFocus();


        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                //mediaPlayer.setPlaybackSpeed(1.0f);
                mediaPlayer.start();
            }
        });
    }

    private long mPosition;

    @Override
    public void onPause() {
        //mPosition = mVideoView.getCurrentPosition();
        //mVideoView.stopPlayback();
        super.onPause();
    }

    @Override
    public void onResume() {
        //if (mPosition > 0) {
        //    mVideoView.seekTo(mPosition);
        //    mPosition = 0;
        //}
        super.onResume();
        //mVideoView.start();
    }
}
