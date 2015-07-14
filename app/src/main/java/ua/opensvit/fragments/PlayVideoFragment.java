package ua.opensvit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import ua.opensvit.R;

public class PlayVideoFragment extends Fragment{

    private static final String URL_TAG = "url";

    public PlayVideoFragment() {
    }

    public static PlayVideoFragment newInstance(String url) {
        PlayVideoFragment fragment = new PlayVideoFragment();
        Bundle args = new Bundle();
        args.putString(URL_TAG, url);
        fragment.setArguments(args);
        return fragment;
    }

    private VideoView mVideoView;
    private String mPath;

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
        controller.setFileName("File name");
        controller.setAnchorView(view);
        controller.setInstantSeeking(true);
        controller.setEnabled(true);
        this.mVideoView.setMediaController(controller);
        this.mVideoView.setVideoPath(mPath);
        this.mVideoView.start();
    }
}
