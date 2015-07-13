package ua.opensvit.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.news.MainActivity;
import ua.opensvit.api.OpenWorldApi;
import ua.opensvit.data.authorization.login_password.AuthorizationInfo;
import ua.opensvit.data.authorization.mac.AuthorizationInfoMac;
import ua.opensvit.utils.ApiUtils;

public class MainFragment extends Fragment {
    public static final String AUTHORIZATION_INFO_TAG = "authorizationInfo";

    private View mLoginView;
    private View mDemoLoginView;
    private OpenWorldApi api;
    private VideoStreamApp app;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoginView = view.findViewById(R.id.login);
        mDemoLoginView = view.findViewById(R.id.demo_login);

        app = VideoStreamApp.getInstance();
        api = app.getApi();

        mLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    app.setIsMac(true);
                    app.setIsTest(true);
                    ApiUtils.getBaseUrl();
                    FragmentActivity activity = getActivity();
                    AuthorizationInfoMac authorizationInfo = api.getAuthorizationInfo();
                    if (authorizationInfo.getError() != null) {
                        Toast.makeText(activity, authorizationInfo.getError(), Toast.LENGTH_SHORT).show();
                    }
                    if (authorizationInfo.isActive() && authorizationInfo
                            .isAuthenticated()) {
                        Fragment tvTypesFragment = TvTypesFragment.newInstance(authorizationInfo);
                        MainActivity.startFragment(activity, tvTypesFragment);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mDemoLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    app.setIsMac(false);
                    app.setIsTest(false);
                    ApiUtils.getBaseUrl();
                    FragmentActivity activity = getActivity();
                    AuthorizationInfo authorizationInfo = api.getAuthorizationInfo("310807", "123321");
                    if (authorizationInfo.getError() != null) {
                        Toast.makeText(activity, authorizationInfo.getError(), Toast.LENGTH_SHORT).show();
                    }
                    if (authorizationInfo.isActive() && authorizationInfo
                            .isAuthenticated()) {
                        Fragment tvTypesFragment = TvTypesFragment.newInstance(authorizationInfo);
                        MainActivity.startFragment(activity, tvTypesFragment);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
