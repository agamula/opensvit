package ua.opensvit.activities;

import android.app.Activity;
import android.os.SystemClock;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.squareup.leakcanary.RefWatcher;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.adapters.ChannelListAdapter;
import ua.opensvit.adapters.ChannelListData;
import ua.opensvit.data.channels.Channel;
import ua.opensvit.loaders.RunnableLoader;
import ua.opensvit.utils.ChannelFactory;

public class TestActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<String>, ExpandableListView.OnChildClickListener {

    private static final int LOAD_MENUS_ID = 0;
    private ExpandableListView mExpandableListView;
    private WeakReference<TestActivity> weakActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_menu);
        mExpandableListView = (ExpandableListView) findViewById(R.id.menu_list);

        weakActivity = new WeakReference<>(this);
        mExpandableListView.setOnChildClickListener(this);

        getSupportLoaderManager().initLoader(LOAD_MENUS_ID, null, new LoaderManagerCallbacks
                (weakActivity));
    }

    private static final class LoaderManagerCallbacks implements LoaderManager
            .LoaderCallbacks<String> {

        private final WeakReference<TestActivity> weakActivity;

        public LoaderManagerCallbacks(WeakReference<TestActivity> weakActivity) {
            this.weakActivity = weakActivity;
        }

        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            final Loader<String> res;
            switch (id) {
                case LOAD_MENUS_ID:
                    RunnableLoader loader = new RunnableLoader();
                    loader.setRunnable(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int countGroups = 2;
                                List<String> groupsList = new ArrayList<>(countGroups);
                                for (int i = 0; i < countGroups; i++) {
                                    groupsList.add("GROUP" + i);
                                }

                                List<List<Channel>> channels = new ArrayList<>();

                                for (int i = 0; i < countGroups; i++) {
                                    List<Channel> channels1 = new ArrayList<>();
                                    for (int j = 0; j < 5; j++) {
                                        channels1.add(ChannelFactory.getInstance().createChannel());
                                    }
                                    channels.add(channels1);
                                }

                                VideoStreamApp.getInstance().setTempLoaderObject(LOAD_MENUS_ID, new ChannelListData
                                        (groupsList,
                                                channels));
                                SystemClock.sleep(5000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    res = loader;
                    break;
                default:
                    res = null;
            }
            return res;
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {
            TestActivity fragment = weakActivity.get();
            if (fragment != null) {
                VideoStreamApp mApp = VideoStreamApp.getInstance();
                ChannelListData mExpListData = (ChannelListData) mApp.getTempLoaderObject(LOAD_MENUS_ID);
                ChannelListAdapter mExpListAdapter = new ChannelListAdapter(mExpListData.groups,
                        mExpListData.channels, mApp.getApi1(), fragment);
                fragment.mExpandableListView.setAdapter(mExpListAdapter);
            }
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {

        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        final Loader<String> res;
        switch (id) {
            case LOAD_MENUS_ID:
                RunnableLoader loader = new RunnableLoader();
                loader.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int countGroups = 2;
                            List<String> groupsList = new ArrayList<>(countGroups);
                            for (int i = 0; i < countGroups; i++) {
                                groupsList.add("GROUP" + i);
                            }

                            List<List<Channel>> channels = new ArrayList<>();

                            for (int i = 0; i < countGroups; i++) {
                                List<Channel> channels1 = new ArrayList<>();
                                for (int j = 0; j < 5; j++) {
                                    channels1.add(ChannelFactory.getInstance().createChannel());
                                }
                                channels.add(channels1);
                            }

                            VideoStreamApp.getInstance().setTempLoaderObject(LOAD_MENUS_ID, new ChannelListData
                                    (groupsList,
                                            channels));
                            SystemClock.sleep(5000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                res = loader;
                break;
            default:
                res = null;
        }
        return res;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Activity fragment = weakActivity.get();
        if (fragment != null) {
            VideoStreamApp mApp = VideoStreamApp.getInstance();
            ChannelListData mExpListData = (ChannelListData) mApp.getTempLoaderObject(LOAD_MENUS_ID);
            ChannelListAdapter mExpListAdapter = new ChannelListAdapter(mExpListData.groups,
                    mExpListData.channels, mApp.getApi1(), fragment);
            mExpandableListView.setAdapter(mExpListAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = VideoStreamApp.getInstance().getRefWatcher();
        refWatcher.watch(this);
    }
}
