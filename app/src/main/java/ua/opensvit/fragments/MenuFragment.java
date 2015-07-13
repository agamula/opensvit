package ua.opensvit.fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.adapters.ChannelListAdapter;
import ua.opensvit.api.OpenWorldApi;
import ua.opensvit.data.Film;
import ua.opensvit.data.iptv.base.TvMenuInfo;
import ua.opensvit.data.iptv.base.TvMenuItem;
import ua.opensvit.data.iptv.films.FilmItem;
import ua.opensvit.data.iptv.films.FilmsInfo;
import ua.opensvit.loaders.RunnableLoader;

public class MenuFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>{

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

                        ArrayList localArrayList2 = new ArrayList(groupsList.size());
                        for (int i = 0; i < groupsList.size(); i++) {
                            FilmsInfo filmsInfo = api.getFilms(tvMenuItems.get(i).getId());
                            List<Film> films = new ArrayList<>();
                            List<FilmItem> filmItems = filmsInfo.getUnmodifiableFilms();
                            for (int j = 0; j < filmsInfo.getTotal(); j++) {
                                FilmItem filmItem = filmItems.get(j);
                                Film film = new Film(filmItem.getName(), filmItem.getLogo(), filmItem.getId(),
                                        filmItem.getYear(), filmItem.getGenre(), filmItem.getOrigin());
                                films.add(film);
                            }
                            localArrayList2.add(films);
                        }

                        mExpListAdapter = new ChannelListAdapter(getActivity(), groupsList,
                                localArrayList2, api);
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
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
