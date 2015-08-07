package ua.opensvit.fragments;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;
import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.MainActivity;
import ua.opensvit.adapters.programs.ProgramsPagerAdapter;
import ua.opensvit.api.OpenWorldApi1;
import ua.opensvit.data.ParcelableArray;
import ua.opensvit.data.PlayerInfo;
import ua.opensvit.data.constants.LoaderConstants;
import ua.opensvit.data.epg.EpgItem;
import ua.opensvit.data.epg.ProgramItem;
import ua.opensvit.fragments.player.VitamioVideoBaseFragment;
import ua.opensvit.fragments.player.VitamioVideoFragment;
import ua.opensvit.loaders.RunnableLoader;
import ua.opensvit.services.NextProgramNotifyService;
import ua.opensvit.utils.DateUtils;
import ua.opensvit.utils.WindowUtils;
import ua.opensvit.widgets.RespondedLayout;

public class ProgramsFragment extends VitamioVideoBaseFragment implements LoaderManager
        .LoaderCallbacks<String> {

    private static final String VIDEO_WIDTH = "video_width";
    private static final String VIDEO_HEIGHT = "video_height";

    private static final String START_UT_TAG = "startUT";
    private static final String END_UT_TAG = "endUT";
    private static final String PER_PAGE_TAG = "perPage";
    private static final String PAGE_TAG = "page";

    public static String NEXT_URL;


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
    private List<String> mDayNames;
    private int mVideoWidth, mVideoHeight;
    private EpgItem epgItem;
    private int mCountDays;
    private PagerTabStrip mTabStrip;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        Bundle args = getArguments();
        mVideoWidth = args.getInt(VIDEO_WIDTH);
        mVideoHeight = args.getInt(VIDEO_HEIGHT);

        mPager = (ViewPager) view.findViewById(R.id.program_list);
        mTabStrip = (PagerTabStrip) view.findViewById(R.id.day_names);

        mPrograms = new SparseArray<>();

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

        Calendar calendar = Calendar.getInstance(DateUtils.getTimeZone());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        mCountDays = getResources().getInteger(R.integer.count_days_back);

        int channelId = getChannelId();
        int serviceId = getServiceId();

        mCountLoaded = 0;

        for (int i = 0; i < mCountDays; i++) {
            long now = calendar.getTimeInMillis();
            long endUt = TimeUnit.MILLISECONDS.toSeconds(now);

            long dayMillis = TimeUnit.DAYS.toMillis(1);

            long startUt = TimeUnit.MILLISECONDS.toSeconds(now - dayMillis + 1);
            int perPage = 0;
            int page = -1;

            args = new Bundle();
            args.putInt(NextProgramNotifyService.CHANNEL_ID, channelId);
            args.putInt(NextProgramNotifyService.SERVICE_ID, serviceId);
            args.putLong(START_UT_TAG, startUt);
            args.putLong(END_UT_TAG, endUt);
            args.putInt(PER_PAGE_TAG, perPage);
            args.putInt(PAGE_TAG, page);

            getLoaderManager().initLoader(LoaderConstants.LOAD_PROGRAMS_LOADER_ID + i, args, this);
            calendar.setTimeInMillis(calendar.getTimeInMillis() - dayMillis);
        }
        mDayNames = new ArrayList<>(mCountDays);
        for (int i = 0; i < mCountDays; i++) {
            mDayNames.add(null);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        super.onPrepared(mediaPlayer);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        final Loader<String> res;
        if (id >= LoaderConstants.LOAD_PROGRAMS_LOADER_ID && id < LoaderConstants
                .LOAD_PROGRAMS_LOADER_ID + mCountDays) {
            int channelId = args.getInt(NextProgramNotifyService.CHANNEL_ID);
            int serviceId = args.getInt(NextProgramNotifyService.SERVICE_ID);
            long startUt = args.getLong(START_UT_TAG);
            long endUt = args.getLong(END_UT_TAG);
            int perPage = args.getInt(PER_PAGE_TAG);
            int page = args.getInt(PAGE_TAG);

            RunnableLoader loader = new RunnableLoader();
            loader.setRunnable(VideoStreamApp.getInstance().getApi1().macGetEpgRunnable
                    (channelId, serviceId, startUt, endUt, perPage, page, new LoadResultListener
                            (id - LoaderConstants.LOAD_PROGRAMS_LOADER_ID)));
            res = loader;
        } else {
            switch (id) {
                default:
                    res = null;
            }
        }
        return res;
    }

    private int mCountLoaded;

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        int loaderId = loader.getId();
        if (loaderId >= LoaderConstants.LOAD_PROGRAMS_LOADER_ID && loaderId < LoaderConstants
                .LOAD_PROGRAMS_LOADER_ID + mCountDays) {
            epgItem = (EpgItem) VideoStreamApp.getInstance().getTempLoaderObject(loaderId);
            mCountLoaded++;

            int loadPosition = LoaderConstants.LOAD_PROGRAMS_LOADER_ID + mCountDays - loaderId - 1;

            List<ProgramItem> programItems = epgItem.getUnmodifiablePrograms();
            ProgramItem programItemsArr[] = new ProgramItem[programItems.size()];
            programItems.toArray(programItemsArr);
            mPrograms.put(loadPosition, new ParcelableArray<>(programItemsArr));

            if (loadPosition == mCountDays - 1) {
                mDayNames.set(loadPosition, getResources().getString(R.string.today));
            } else {
                Calendar calendar = Calendar.getInstance(DateUtils.getTimeZone());
                //calendar.set(Calendar.DAY_OF_WEEK, epgItem.getDayOfWeek());
                calendar.setTimeInMillis(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis
                        (mCountDays - 1 - loadPosition));
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                Date d = new Date();
                d.setTime(calendar.getTimeInMillis());
                mDayNames.set(loadPosition, sdf.format(d));
            }

            if (mCountLoaded == mCountDays) {
                mProgress.setVisibility(View.GONE);
                mPager.setAdapter(new ProgramsPagerAdapter(mPrograms, mDayNames, getActivity(),
                        getChannelId()));
                mPager.setCurrentItem(mDayNames.size() - 1);
                mTabStrip.setTabIndicatorColorResource(R.color.color_login);
            }
        } else {
            switch (loader.getId()) {
                default:
                    break;
            }
        }
    }

    private class LoadResultListener implements OpenWorldApi1.ResultListener {

        private final int position;

        LoadResultListener(int position) {
            this.position = position;
        }

        @Override
        public void onResult(Object res) {
            if (res != null) {
                VideoStreamApp.getInstance().setTempLoaderObject(LoaderConstants
                        .LOAD_PROGRAMS_LOADER_ID + position, res);
            }
        }

        @Override
        public void onError(String result) {

        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
