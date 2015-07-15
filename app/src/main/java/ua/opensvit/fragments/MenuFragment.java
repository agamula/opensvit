package ua.opensvit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.fragments.MainActivity;
import ua.opensvit.adapters.ChannelListAdapter;
import ua.opensvit.api.OpenWorldApi;
import ua.opensvit.data.iptv.channels.Channel;
import ua.opensvit.data.iptv.channels.ChannelsInfo;
import ua.opensvit.data.iptv.menu.TvMenuInfo;
import ua.opensvit.data.iptv.menu.TvMenuItem;
import ua.opensvit.loaders.RunnableLoader;

public class MenuFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>,
        ExpandableListView.OnChildClickListener{

    private static final String MENU_INFO_TAG = "menu_info";
    public static final int LOAD_MENUS_ID = 0;

    public static MenuFragment newInstance(TvMenuInfo menuInfo) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putParcelable(MENU_INFO_TAG, menuInfo);
        fragment.setArguments(args);
        return fragment;
    }

    private TvMenuInfo mMenuInfo;
    private ExpandableListAdapter mExpListAdapter;
    private ExpandableListView mExpandableListView;
    private View mProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgress = view.findViewById(R.id.load_progress);
        mProgress.setVisibility(View.VISIBLE);
        mMenuInfo = getArguments().getParcelable(MENU_INFO_TAG);
        mExpandableListView = (ExpandableListView) view.findViewById(R.id.menu_list);
        getLoaderManager().initLoader(LOAD_MENUS_ID, null, this);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        if(id == LOAD_MENUS_ID) {
            return new RunnableLoader(getActivity(), new Runnable() {
                @Override
                public void run() {
                    try {
                        OpenWorldApi api = VideoStreamApp.getInstance().getApi();
                        List<TvMenuItem> tvMenuItems = mMenuInfo.getUnmodifiableTVItems();
                        ArrayList<String> groupsList = new ArrayList<>();
                        for (int i = 0; i < tvMenuItems.size(); i++) {
                            groupsList.add(tvMenuItems.get(i).getName());
                        }

                        ArrayList<ArrayList<Channel>> channels = new ArrayList<>(groupsList.size());
                        for (int i = 0; i < groupsList.size(); i++) {
                            ChannelsInfo channelsInfo = api.getChannels(tvMenuItems.get(i).getId());
                            ArrayList<Channel> channelArrayList = new ArrayList<>();
                            channelArrayList.addAll(channelsInfo.getUnmodifiableChannels());
                            channels.add(channelArrayList);
                        }

                        mExpListAdapter = new ChannelListAdapter(getActivity(), groupsList,
                                channels, api);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        mProgress.setVisibility(View.GONE);
        mExpandableListView.setAdapter(mExpListAdapter);
        mExpandableListView.setOnChildClickListener(this);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int
            childPosition, long id) {
        Channel channel = (Channel) mExpListAdapter.getChild(groupPosition, childPosition);
        VideoStreamApp app = VideoStreamApp.getInstance();
        try {
            OpenWorldApi api = app.getApi();
            //api.keepAliveTime(true);
            String ip = api.getChannelIp(channel.getId());
            boolean availability = api.checkAvailability(ip);
            Toast.makeText(app.getApplicationContext(), ip, Toast.LENGTH_SHORT).show();
            MainActivity.startFragment(getActivity(), PlayVideoFragment.newInstance(ip));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
