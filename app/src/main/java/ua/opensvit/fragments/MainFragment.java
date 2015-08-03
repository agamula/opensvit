package ua.opensvit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;

import java.io.IOException;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.fragments.MainActivity;
import ua.opensvit.api.OpenWorldApi1;
import ua.opensvit.data.authorization.AuthorizationInfoBase;

public class MainFragment extends Fragment implements OpenWorldApi1.ResultListener{
    public static final String AUTHORIZATION_INFO_TAG = "authorizationInfo";

    private View mLoginView;
    private View mDemoLoginView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoginView = view.findViewById(R.id.login);
        mDemoLoginView = view.findViewById(R.id.demo_login);

        mLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.startFragmentWithoutBack(getActivity(), new LoginPasswordFragment());
            }
        });

        mDemoLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    VideoStreamApp app = VideoStreamApp.getInstance();
                    //app.setIsMac(false);
                    //ApiUtils.getBaseUrl();
                    String demoLogin = "310807";
                    String demoPassword = "123321";
                    OpenWorldApi1 api1 = app.getApi1();
                    //api1.auth(MainFragment.this, demoLogin, demoPassword, MainFragment.this);
                    api1.macAuth(MainFragment.this, demoLogin, demoPassword, MainFragment.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResult(Object res) {
        AuthorizationInfoBase authorizationInfo = (AuthorizationInfoBase)res;
        if (authorizationInfo.getError() != null) {
            Toast.makeText(getActivity(), authorizationInfo.getError(), Toast.LENGTH_SHORT).show();
            return;
        }
        if (authorizationInfo.isAuthenticated()) {
            MainActivity.startFragmentWithoutBack(getActivity(), TvTypesFragment.newInstance
                    (authorizationInfo));
        }
    }

    @Override
    public void onError(String result) {
        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = VideoStreamApp.getInstance().getRefWatcher();
        refWatcher.watch(this);
    }
}
