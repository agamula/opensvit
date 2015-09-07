package ua.opensvit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.MainActivity;
import ua.opensvit.adapters.EpgAdapter;
import ua.opensvit.api.OpenWorldApi1;
import ua.opensvit.data.GetUrlItem;
import ua.opensvit.data.constants.LoaderConstants;
import ua.opensvit.data.epg.EpgItem;
import ua.opensvit.data.epg.ProgramItem;
import ua.opensvit.fragments.player.VitamioVideoFragment;
import ua.opensvit.loaders.RunnableLoader;
import ua.opensvit.utils.DateUtils;

public class EpgFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>,
        OpenWorldApi1.ResultListener, AdapterView.OnItemClickListener {
    private static final String CHANNEL_ID_TAG = "channelId";
    private static final String ONLINE_URL_TAG = "onlineUrl";
    private static final String SERVICE_TAG = "service";
    private static final String START_UT_TAG = "startUT";
    private static final String END_UT_TAG = "endUT";
    private static final String PER_PAGE_TAG = "perPage";
    private static final String PAGE_TAG = "page";

    private ListView mPrograms;
    private EpgItem epgItem;
    private int channelId;
    private int serviceId;
    private ProgressBar mProgress;
    private String onlineUrl;
    private VideoStreamApp mApp;

    public EpgFragment() {
    }

    public static EpgFragment newInstance(int channelId, int serviceId, String onlineUrl) {
        EpgFragment res = new EpgFragment();
        Bundle args = new Bundle();
        args.putInt(CHANNEL_ID_TAG, channelId);
        args.putInt(SERVICE_TAG, serviceId);
        args.putString(ONLINE_URL_TAG, onlineUrl);
        res.setArguments(args);
        return res;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_of_programs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mApp = VideoStreamApp.getInstance();

        mProgress = (ProgressBar) getActivity().findViewById(R.id.progress);
        mProgress.setVisibility(View.VISIBLE);

        mPrograms = (ListView) view.findViewById(R.id.programs);
        mPrograms.setOnItemClickListener(this);

        onlineUrl = getArguments().getString(ONLINE_URL_TAG);

        channelId = getArguments().getInt(CHANNEL_ID_TAG);
        serviceId = getArguments().getInt(SERVICE_TAG);

        Calendar calendar = Calendar.getInstance(DateUtils.getTimeZone());
        long now = calendar.getTimeInMillis();
        long endUt = TimeUnit.MILLISECONDS.toSeconds(now);
        long startUt = TimeUnit.MILLISECONDS.toSeconds(now - TimeUnit.DAYS.toMillis(1));
        int perPage = 0;
        int page = -1;

        Bundle args = new Bundle();
        args.putInt(CHANNEL_ID_TAG, channelId);
        args.putInt(SERVICE_TAG, serviceId);
        args.putLong(START_UT_TAG, startUt);
        args.putLong(END_UT_TAG, endUt);
        args.putInt(PER_PAGE_TAG, perPage);
        args.putInt(PAGE_TAG, page);

        getLoaderManager().initLoader(LoaderConstants.LOAD_EPG_LOADER_ID, args, this);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        final Loader<String> res;
        switch (id) {
            case LoaderConstants.LOAD_EPG_LOADER_ID:
                int channelId = args.getInt(CHANNEL_ID_TAG);
                int serviceId = args.getInt(SERVICE_TAG);
                long startUt = args.getLong(START_UT_TAG);
                long endUt = args.getLong(END_UT_TAG);
                int perPage = args.getInt(PER_PAGE_TAG);
                int page = args.getInt(PAGE_TAG);

                res = new RunnableLoader();
                ((RunnableLoader) res).setRunnable(mApp.getApi1()
                        .macGetEpgRunnable(channelId, serviceId, startUt, endUt, perPage, page,
                                this));
                break;
            default:
                res = null;
                break;
        }
        return res;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        switch (loader.getId()) {
            case LoaderConstants.LOAD_EPG_LOADER_ID:
                epgItem = (EpgItem) VideoStreamApp.getInstance().getTempLoaderObject
                        (LoaderConstants.LOAD_EPG_LOADER_ID);
                mProgress.setVisibility(View.GONE);
                mPrograms.setAdapter(new EpgAdapter(epgItem, getActivity()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onResult(Object res) {
        if (res != null) {
            VideoStreamApp.getInstance().setTempLoaderObject(LoaderConstants.LOAD_EPG_LOADER_ID, res);
        }
    }

    @Override
    public void onError(String result) {
        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (EpgAdapter.calculatePlayOnlineView(position)) {
            MainActivity.startFragment(getActivity(), VitamioVideoFragment.newInstance(onlineUrl,
                    channelId, mApp.getMenuInfo().getService(), TimeUnit.MILLISECONDS.toSeconds(System
                            .currentTimeMillis()), 0, 0));
        } else {
            final ProgramItem programItem = (ProgramItem) parent.getAdapter().getItem(position);
            if (true/*programItem.isArchive()*/) {
                try {
                    mApp.getApi1().macGetArchiveUrl(this, channelId,
                            programItem.getTimestamp(), new OpenWorldApi1.ResultListener() {
                                @Override
                                public void onResult(Object res) {
                                    if (res == null) {
                                        return;
                                    }
                                    VideoStreamApp.getInstance().setFirstNotOnline(true);
                                    GetUrlItem urlItem = (GetUrlItem) res;
                                    MainActivity.startFragment(getActivity(),
                                            VitamioVideoFragment.newInstance(urlItem.getUrl(),
                                                    channelId, mApp.getMenuInfo().getService(),
                                                    programItem.getTimestamp(), 0, 0));
                                }

                                @Override
                                public void onError(String result) {
                                    Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        mPrograms.setOnItemClickListener(null);
        getLoaderManager().destroyLoader(LoaderConstants.LOAD_EPG_LOADER_ID);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = VideoStreamApp.getInstance().getRefWatcher();
        refWatcher.watch(this);
    }
}
