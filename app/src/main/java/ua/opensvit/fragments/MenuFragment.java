package ua.opensvit.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.fragments.MainActivity;
import ua.opensvit.adapters.ChannelListAdapter;
import ua.opensvit.adapters.ChannelListData;
import ua.opensvit.api.OpenWorldApi1;
import ua.opensvit.data.GetUrlItem;
import ua.opensvit.data.channels.Channel;
import ua.opensvit.data.menu.TvMenuInfo;
import ua.opensvit.data.menu.TvMenuItem;
import ua.opensvit.loaders.RunnableLoader;

public class MenuFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    private static final String MENU_INFO_TAG = "menu_info";
    public static final int LOAD_MENUS_ID = 0;

    public static MenuFragment newInstance(TvMenuInfo menuInfo) {
        VideoStreamApp.getInstance().setMenuInfo(menuInfo);

        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putParcelable(MENU_INFO_TAG, menuInfo);
        fragment.setArguments(args);
        return fragment;
    }

    private TvMenuInfo mMenuInfo;
    private ExpandableListView mExpandableListView;
    private View mProgress;
    private WeakReference<MenuFragment> weakFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgress = view.findViewById(R.id.load_progress);
        mProgress.setVisibility(View.VISIBLE);
        mMenuInfo = getArguments().getParcelable(MENU_INFO_TAG);
        mExpandableListView = (ExpandableListView) view.findViewById(R.id.menu_list);

        weakFragment = new WeakReference<>(this);

        Bundle args = new Bundle();
        args.putParcelable(MENU_INFO_TAG, mMenuInfo);
        mListener = new GetIpAndShowEpgListener(weakFragment);

        getLoaderManager().initLoader(LOAD_MENUS_ID, args, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mExpandableListView.setOnChildClickListener(mListener);
    }

    @Override
    public void onPause() {
        mExpandableListView.setOnChildClickListener(null);
        super.onPause();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        if (id == LOAD_MENUS_ID) {
            final TvMenuInfo menuInfo = args.getParcelable(MENU_INFO_TAG);
            RunnableLoader loader = new RunnableLoader();
            loader.setRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        VideoStreamApp mApp = VideoStreamApp.getInstance();
                        OpenWorldApi1 api1 = mApp.getApi1();
                        List<TvMenuItem> tvMenuItems = menuInfo.getUnmodifiableTVItems();
                        List<String> groupsList = new ArrayList<>();
                        for (int i = 0; i < tvMenuItems.size(); i++) {
                            groupsList.add(tvMenuItems.get(i).getName());
                        }

                        List<List<Channel>> channels = api1.macGetChannels(tvMenuItems);

                        mApp.setTempLoaderObject(LOAD_MENUS_ID, new ChannelListData(groupsList,
                                channels));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return loader;
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        switch (loader.getId()) {
            case LOAD_MENUS_ID:
                MenuFragment fragment = weakFragment.get();
                if (fragment != null) {
                    fragment.mProgress.setVisibility(View.GONE);
                    VideoStreamApp mApp = VideoStreamApp.getInstance();
                    ChannelListData mExpListData = (ChannelListData) mApp.getTempLoaderObject
                            (LOAD_MENUS_ID);
                    ExpandableListAdapter mExpListAdapter = new ChannelListAdapter(mExpListData
                            .groups, mExpListData.channels, mApp.getApi1(), fragment.getActivity());
                    fragment.mExpandableListView.setAdapter(mExpListAdapter);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private GetIpAndShowEpgListener mListener;

    private static class GetIpAndShowEpgListener implements ExpandableListView.OnChildClickListener,
            OpenWorldApi1.ResultListener {

        private final WeakReference<MenuFragment> weakFragment;
        private AsyncTask mPressTask;

        public GetIpAndShowEpgListener(WeakReference<MenuFragment> weakFragment) {
            this.weakFragment = weakFragment;
        }

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Channel mChannel = (Channel) parent.getExpandableListAdapter().getChild
                    (groupPosition, childPosition);
            MenuFragment fragment = weakFragment.get();
            if (fragment != null) {
                try {
                    VideoStreamApp app = VideoStreamApp.getInstance();
                    OpenWorldApi1 api1 = app.getApi1();
                    app.setChannelId(mChannel.getId());
                    mPressTask = api1.macGetChannelIp(fragment, mChannel.getId(), this);

                    //TODO implement loading channels and start programs fragment
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }
            }
            return false;
        }

        public void onDestroyView() {
            if (mPressTask != null && mPressTask.getStatus() != AsyncTask.Status.FINISHED) {
                mPressTask.cancel(true);
                mPressTask = null;
            }
        }

        @Override
        public void onResult(Object res) {
            MenuFragment fragment = weakFragment.get();
            if (fragment != null) {
                if (res == null) {
                    Toast.makeText(fragment.getActivity(), fragment.getString(R.string
                            .load_failed_message), Toast.LENGTH_SHORT).show();
                    return;
                }
                GetUrlItem urlItem = (GetUrlItem) res;
                String ip = urlItem.getUrl();
                MainActivity.startFragment(fragment.getActivity(), EpgFragment.newInstance
                        (VideoStreamApp.getInstance().getChannelId(), fragment.mMenuInfo.getService()
                                , ip));
            }
        }

        @Override
        public void onError(String result) {
            if (weakFragment.get() != null) {
                Toast.makeText(weakFragment.get().getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        mListener.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = VideoStreamApp.getInstance().getRefWatcher();
        refWatcher.watch(this);
    }
}
