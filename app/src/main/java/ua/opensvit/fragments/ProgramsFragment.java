package ua.opensvit.fragments;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.concurrent.TimeUnit;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;
import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.MainActivity;
import ua.opensvit.adapters.programs.ProgramsPagerAdapter;
import ua.opensvit.data.ParcelableArray;
import ua.opensvit.data.PlayerInfo;
import ua.opensvit.data.epg.ProgramItem;
import ua.opensvit.fragments.player.VitamioVideoBaseFragment;
import ua.opensvit.fragments.player.VitamioVideoFragment;
import ua.opensvit.utils.WindowUtils;
import ua.opensvit.widgets.RespondedLayout;

public class ProgramsFragment extends VitamioVideoBaseFragment {

    private static final String VIDEO_WIDTH = "video_width";
    private static final String VIDEO_HEIGHT = "video_height";

    public ProgramsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static ProgramsFragment newInstance(String url, int channelId, int serviceId, int
            videoWidth, int videoHeight) {
        ProgramsFragment programsFragment = new ProgramsFragment();
        Bundle bundle = programsFragment.getArgsBundle(url, channelId, serviceId, TimeUnit
                .MILLISECONDS.toSeconds(System.currentTimeMillis()));
        bundle.putInt(VIDEO_WIDTH, videoWidth);
        bundle.putInt(VIDEO_HEIGHT, videoHeight);
        programsFragment.setArguments(bundle);
        return programsFragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_programs;
    }

    private ProgressBar mProgress;
    private ViewPager mPager;
    private SparseArray<ParcelableArray<ProgramItem>> mPrograms;
    private int mVideoWidth, mVideoHeight;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        mVideoWidth = args.getInt(VIDEO_WIDTH);
        mVideoHeight = args.getInt(VIDEO_HEIGHT);

        mPager = (ViewPager) view.findViewById(R.id.program_list);

        mPrograms = new SparseArray<>();
        //mpager seadapter

        RespondedLayout respondedLayout = getRespondedLayout();

        if (respondedLayout != null) {
            mProgress = (ProgressBar) view.findViewById(R.id.load_video_program_progress);
            mProgress.setVisibility(View.VISIBLE);

            respondedLayout.setOnLayoutHappenedListener(new RespondedLayout.OnLayoutHappenedListener() {
                @Override
                public void onLayoutHappened(RespondedLayout layout) {
                    final VideoView mVideoView = getVideoView();
                    mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
                    Pair<Integer, Integer> screenSizes = WindowUtils.getScreenSizes();
                    int width = screenSizes.first;
                    int scHeight = screenSizes.second;
                    int height = (int) ((float) (width * mVideoHeight) / mVideoWidth);
                    if (scHeight < height) {
                        height = scHeight;
                        width = (int) ((float) (height * mVideoWidth) / mVideoHeight);
                    }
                    View parentView = (View) mVideoView.getParent();
                    ViewGroup.LayoutParams pars = parentView.getLayoutParams();
                    pars.width = width;
                    pars.height = height;
                    pars = mVideoView.getLayoutParams();
                    pars.width = width;
                    pars.height = height;
                    layout.setReactOnLayout(false);
                    onPostViewCreated();
                }
            });
            respondedLayout.setReactOnLayout(true);
            respondedLayout.requestLayout();
            VideoStreamApp.getInstance().getPlayerInfo().setForceStart(true);
        } else {
            PlayerInfo playerInfo = VideoStreamApp.getInstance().getPlayerInfo();
            if (playerInfo.isPlaying() && playerInfo.isForceStart()) {
                MainActivity.startFragment(getActivity(), VitamioVideoFragment.newInstance(getPath(),
                        getChannelId(), getServiceId(), getTimestamp(), mVideoWidth, mVideoHeight));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        super.onPrepared(mediaPlayer);
        mProgress.setVisibility(View.GONE);
    }
}
