package ua.opensvit.fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.news.MainActivity;
import ua.opensvit.api.OpenWorldApi;
import ua.opensvit.data.authorization.AuthorizationInfoBase;
import ua.opensvit.data.iptv.menu.TvMenuInfo;

public class TvTypesFragment extends ListFragment {

    public TvTypesFragment() {
    }

    public static TvTypesFragment newInstance(AuthorizationInfoBase authorizationInfo) {
        TvTypesFragment fragment = new TvTypesFragment();
        Bundle args = new Bundle();
        args.putParcelable(MainFragment.AUTHORIZATION_INFO_TAG, authorizationInfo);
        fragment.setArguments(args);
        return fragment;
    }

    private AuthorizationInfoBase mAuthorizationInfo;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuthorizationInfo = getArguments().getParcelable(MainFragment.AUTHORIZATION_INFO_TAG);
        String tvTypes[] = getResources().getStringArray(R.array.tv_types);
        getListView().setDivider(new ColorDrawable());
        setListAdapter(new ArrayAdapter<>(getActivity(), R.layout.fragment_tv_type_item, R
                .id.tv_type_text, tvTypes));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final TvMenuInfo menuInfo;
        OpenWorldApi api = VideoStreamApp.getInstance().getApi();
        try {
            switch (position) {
                case 0:
                    menuInfo = api.getTvMenu();
                    break;
                case 1:
                    menuInfo = api.getVodMenu();
                    break;
                case 2:
                    MainActivity.startFragment(getActivity(), new AboutFragment());
                    return;
                default:
                    menuInfo = null;
            }
            String errMsg;

            if(menuInfo == null) {
                errMsg = getString(R.string.unknown_service);
            } else {
                errMsg = menuInfo.getError();
            }

            if(errMsg != null) {
                Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
            } else {
                MainActivity.startFragment(getActivity(), MenuFragment.newInstance(menuInfo));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
