package ua.opensvit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;

import java.io.IOException;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.fragments.MainActivity;
import ua.opensvit.activities.fragments.PlayActivity;
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
        mProgressBar = (ProgressBar) view.findViewById(R.id.check_progress);
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
        if (mac.isActive()) {
            MainActivity.startFragmentWithoutBack(getActivity(), TvTypesFragment.newInstance(mac));
        } else {
            MainActivity.startFragmentWithoutBack(getActivity(), new MainFragment());
        }
    }

    @Override
    public void onError(String result) {
        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
                getActivity().startActivity(new Intent(getActivity(), PlayActivity.class));
            }
        }, 700);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = VideoStreamApp.getInstance().getRefWatcher();
        refWatcher.watch(this);
    }
}
