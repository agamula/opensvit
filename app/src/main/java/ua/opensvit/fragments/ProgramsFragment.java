package ua.opensvit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import io.vov.vitamio.MediaFormat;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;
import ua.opensvit.R;
import ua.opensvit.adapters.ProgramsPagerAdapter;
import ua.opensvit.data.ParcelableArray;
import ua.opensvit.data.epg.ProgramItem;
import ua.opensvit.fragments.player.VitamioVideoBaseFragment;

public class ProgramsFragment extends VitamioVideoBaseFragment {

    public ProgramsFragment() {
    }

    private static final String PROGRAMS_TAG = "programs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static ProgramsFragment newInstance(String url, int channelId, int serviceId, long
            timestamp, SparseArray<ParcelableArray<ProgramItem>> programs) {
        ProgramsFragment programsFragment = new ProgramsFragment();
        Bundle bundle = programsFragment.getArgsBundle(url, channelId, serviceId, timestamp);
        bundle.putSparseParcelableArray(PROGRAMS_TAG, programs);
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgress = (ProgressBar) view.findViewById(R.id.load_video_program_progress);
        mProgress.setVisibility(View.VISIBLE);

        mPager = (ViewPager) view.findViewById(R.id.program_list);

        mPrograms = getArguments().getSparseParcelableArray(PROGRAMS_TAG);
        mPager.setAdapter(new ProgramsPagerAdapter(mPrograms));

        final VideoView mVideoView = getVideoView();
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
        onPostViewCreated();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        super.onPrepared(mediaPlayer);
        mProgress.setVisibility(View.GONE);
    }
}
