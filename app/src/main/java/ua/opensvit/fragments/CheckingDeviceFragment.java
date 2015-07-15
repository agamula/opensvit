package ua.opensvit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.io.IOException;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.fragments.MainActivity;
import ua.opensvit.api.OpenWorldApi1;
import ua.opensvit.data.authorization.mac.AuthorizationInfoMac;

public class CheckingDeviceFragment extends Fragment implements OpenWorldApi1.ResultListener {

    private OpenWorldApi1 api1;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checking_device, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = (ProgressBar)view.findViewById(R.id.check_progress);
        api1 = VideoStreamApp.getInstance().getApi1();

        try {
            api1.macAuth(mProgressBar, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResult(Object res) {
        AuthorizationInfoMac mac = (AuthorizationInfoMac) res;
        if(mac.isActive()) {
            MainActivity.startFragment(getActivity(), TvTypesFragment1.newInstance(mac));
        } else {
            MainActivity.startFragment(getActivity(), new MainFragment());
        }
    }
}
