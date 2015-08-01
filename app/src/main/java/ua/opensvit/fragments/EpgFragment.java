package ua.opensvit.fragments;

import android.os.Bundle;
import android.os.SystemClock;
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

import java.io.IOException;
import java.util.Calendar;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.fragments.MainActivity;
import ua.opensvit.adapters.EpgAdapter;
import ua.opensvit.api.OpenWorldApi1;
import ua.opensvit.data.GetUrlItem;
import ua.opensvit.data.epg.EpgItem;
import ua.opensvit.data.epg.ProgramItem;
import ua.opensvit.fragments.player.VitamioVideoFragment;
import ua.opensvit.loaders.RunnableLoader;

public class EpgFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>, OpenWorldApi1.ResultListener, AdapterView.OnItemClickListener {
    private static final String CHANNEL_ID_TAG = "channelId";
    private static final String ONLINE_URL_TAG = "onlineUrl";
    private static final int LOAD_PROGRAMS_LOADER_ID = 1;

    private static final long DAY = 24 * 60 * 60 * 1000;

    private ListView mPrograms;
    private EpgItem epgItem;
    private int channelId;
    private int serviceId;
    private long startUt;
    private long endUt;
    private int perPage;
    private int page;
    private ProgressBar mProgress;
    private String onlineUrl;
    private VideoStreamApp mApp;

    public EpgFragment() {
    }

    public static EpgFragment newInstance(int channelId, String onlineUrl) {
        EpgFragment res = new EpgFragment();
        Bundle args = new Bundle();
        args.putInt(CHANNEL_ID_TAG, channelId);
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

        channelId = getArguments().getInt(CHANNEL_ID_TAG);
        onlineUrl = getArguments().getString(ONLINE_URL_TAG);

        serviceId = VideoStreamApp.getInstance().getMenuInfo().getService();
        perPage = 0;
        page = -1;
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        endUt = now / 1000;
        startUt = (now - DAY) / 1000;

        getLoaderManager().initLoader(LOAD_PROGRAMS_LOADER_ID, null, this);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        final Loader<String> res;
        switch (id) {
            case LOAD_PROGRAMS_LOADER_ID:
                res = new RunnableLoader(getActivity(), mApp.getApi1()
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
            case LOAD_PROGRAMS_LOADER_ID:
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
            epgItem = (EpgItem) res;
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
                    channelId, mApp.getMenuInfo().getService(), System.currentTimeMillis() / 1000));
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
                                                    programItem.getTimestamp()));
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
}
