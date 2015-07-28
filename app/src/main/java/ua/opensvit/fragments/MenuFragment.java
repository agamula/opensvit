package ua.opensvit.fragments;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.fragments.MainActivity;
import ua.opensvit.adapters.ChannelListAdapter;
import ua.opensvit.api.OpenWorldApi1;
import ua.opensvit.data.GetUrlItem;
import ua.opensvit.data.InfoAbout;
import ua.opensvit.data.channels.Channel;
import ua.opensvit.data.menu.TvMenuInfo;
import ua.opensvit.data.menu.TvMenuItem;
import ua.opensvit.fragments.player.VitamioVideoFragment;
import ua.opensvit.loaders.RunnableLoader;

public class MenuFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>,
        OpenWorldApi1.ResultListener,
        ExpandableListView.OnChildClickListener {

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
        VideoStreamApp.getInstance().setMenuInfo(mMenuInfo);
        mExpandableListView = (ExpandableListView) view.findViewById(R.id.menu_list);
        getLoaderManager().initLoader(LOAD_MENUS_ID, null, this);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        if (id == LOAD_MENUS_ID) {
            return new RunnableLoader(getActivity(), new Runnable() {
                @Override
                public void run() {
                    try {
                        OpenWorldApi1 api1 = VideoStreamApp.getInstance().getApi1();
                        List<TvMenuItem> tvMenuItems = mMenuInfo.getUnmodifiableTVItems();
                        List<String> groupsList = new ArrayList<>();
                        for (int i = 0; i < tvMenuItems.size(); i++) {
                            groupsList.add(tvMenuItems.get(i).getName());
                        }

                        List<List<Channel>> channels = api1.macGetChannels(tvMenuItems);

                        mExpListAdapter = new ChannelListAdapter(getActivity(), groupsList,
                                channels, api1);
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
        try {
            VideoStreamApp app = VideoStreamApp.getInstance();
            OpenWorldApi1 api1 = app.getApi1();
            api1.macGetChannelIp(this, channel.getId(), this);
            /*api1.macGetArchiveUrl(MenuFragment.this, channel.getId(), System.currentTimeMillis(),
                    new OpenWorldApi1.ResultListener() {
                        @Override
                        public void onResult(Object res) {
                            if(res == null) {
                                return;
                            }
                            GetUrlItem urlItem = (GetUrlItem) res;
                            Toast.makeText(getActivity(), urlItem.getUrl(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String result) {

                        }
                    });*/
            //TODO not working
            /*api1.macGetChannelOsd(MenuFragment.this, channel.getId(), app.getMenuInfo()
                    .getService(), System.currentTimeMillis(), new OpenWorldApi1.ResultListener() {
                @Override
                public void onResult(Object res) {

                }

                @Override
                public void onError(String result) {

                }
            });*/
            /*api1.macGetCreepingLine(MenuFragment.this, app.getMenuInfo().getService(), channel
                    .getId(), new OpenWorldApi1.ResultListener() {
                @Override
                public void onResult(Object res) {
                    if(res == null) {
                        return;
                    }
                    CreepingLineItem lineItem = (CreepingLineItem) res;
                    Toast.makeText(getActivity(), lineItem.getText(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String result) {

                }
            });*/

            /*long now = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
            api1.macGetEpg(MenuFragment.this, channel.getId(), app.getMenuInfo().getService(),
                    now - 24 * 60 * 60 * 1000, now, 10, 0, new OpenWorldApi1.ResultListener() {
                @Override
                public void onResult(Object res) {
                    if(res == null) {
                        return;
                    }
                    EpgItem lineItem = (EpgItem) res;
                    Toast.makeText(getActivity(), lineItem.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String result) {

                }
            });*/
            /*api1.macGetFilms(MenuFragment.this, 1, 10, 0, new OpenWorldApi1.ResultListener() {
                        @Override
                        public void onResult(Object res) {
                            if(res == null) {
                                return;
                            }
                            EpgItem lineItem = (EpgItem) res;
                            Toast.makeText(getActivity(), lineItem.toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String result) {

                        }
                    });*/
            /*api1.macGetFilm(MenuFragment.this, channel.getId(), new OpenWorldApi1.ResultListener
                    () {
                        @Override
                        public void onResult(Object res) {
                            if(res == null) {
                                return;
                            }
                            EpgItem lineItem = (EpgItem) res;
                            Toast.makeText(getActivity(), lineItem.toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String result) {

                        }
                    });*/
            /*api1.macGetImages(MenuFragment.this, new OpenWorldApi1.ResultListener
                    () {
                @Override
                public void onResult(Object res) {
                    if (res == null) {
                        return;
                    }
                    ImageInfo imageInfo = (ImageInfo) res;
                    Toast.makeText(getActivity(), "size: " + imageInfo.getUnmodifiableImages()
                            .size(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String result) {

                }
            });*/
            //TODO save info from api
            /*
            api1.macI18n(MenuFragment.this, "en", new OpenWorldApi1.ResultListener
                    () {
                @Override
                public void onResult(Object res) {
                    if (res == null) {
                        return;
                    }
                    ImageInfo imageInfo = (ImageInfo) res;
                    Toast.makeText(getActivity(), "size: " + imageInfo.getUnmodifiableImages()
                            .size(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String result) {

                }
            });*/
            /*api1.macInfoAbout(MenuFragment.this, new OpenWorldApi1.ResultListener
                    () {
                @Override
                public void onResult(Object res) {
                    if (res == null) {
                        return;
                    }
                    InfoAbout imageInfo = (InfoAbout) res;
                    Toast.makeText(getActivity(), imageInfo.getJava(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String result) {

                }
            });*/
            //TODO make work
            /*
            api1.macOrderFilm(MenuFragment.this, VideoStreamApp.getInstance().getMenuInfo().getService(),
                    120, new
                    OpenWorldApi1
                    .ResultListener
                    () {
                @Override
                public void onResult(Object res) {
                    if (res == null) {
                        return;
                    }
                    InfoAbout imageInfo = (InfoAbout) res;
                    Toast.makeText(getActivity(), imageInfo.getJava(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String result) {

                }
            });*/
            /*api1.resetPin(MenuFragment.this, 10, 120, new
                    OpenWorldApi1.ResultListener() {
                        @Override
                        public void onResult(Object res) {
                            if (res == null) {
                                return;
                            }
                            InfoAbout imageInfo = (InfoAbout) res;
                            Toast.makeText(getActivity(), imageInfo.getJava(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String result) {

                        }
                    });*/
            //TODO make work
            /*api1.macUpdateProfile(MenuFragment.this, 0, 0, "en", "", "", "", "", "", "", "", 2,
             new
                    OpenWorldApi1.ResultListener() {
                        @Override
                        public void onResult(Object res) {
                            if (res == null) {
                                return;
                            }
                            Toast.makeText(getActivity(), res.toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String result) {

                        }
                    });*/
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onResult(Object res) {
        if (res == null) {
            Toast.makeText(getActivity(), getString(R.string.load_failed_message), Toast
                    .LENGTH_SHORT).show();
            return;
        }
        GetUrlItem urlItem = (GetUrlItem) res;
        String ip = urlItem.getUrl();
        MainActivity.startFragment(getActivity(), VitamioVideoFragment.newInstance(ip));
    }

    @Override
    public void onError(String result) {
        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
    }
}
