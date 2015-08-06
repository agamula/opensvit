package ua.opensvit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;

import java.io.IOException;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.MainActivity;
import ua.opensvit.api.OpenWorldApi1;
import ua.opensvit.data.authorization.mac.AuthorizationInfoMac;

public class LoginPasswordFragment extends Fragment implements OpenWorldApi1.ResultListener{

    private EditText mUserName;
    private EditText mPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_password, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserName = (EditText) view.findViewById(R.id.username);
        mPassword = (EditText) view.findViewById(R.id.password);

        view.findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Editable mUserNameText = mUserName.getText();
                    if(mUserNameText.length() == 0) {
                        Toast.makeText(getActivity(), getString(R.string.input_username), Toast
                                .LENGTH_SHORT).show();
                        mUserName.requestFocus();
                        return;
                    }
                    Editable mPasswordText = mPassword.getText();
                    if(mPasswordText.length() == 0) {
                        Toast.makeText(getActivity(), getString(R.string.input_password), Toast
                                .LENGTH_SHORT).show();
                        mPassword.requestFocus();
                        return;
                    }
                    VideoStreamApp.getInstance().getApi1().macAuth(LoginPasswordFragment.this,
                            mUserNameText.toString(), mPasswordText.toString(),
                            LoginPasswordFragment.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResult(Object res) {
        AuthorizationInfoMac infoMac = (AuthorizationInfoMac) res;
        if(infoMac.isAuthenticated()) {
            MainActivity.startFragmentWithoutBack(getActivity(), TvTypesFragment.newInstance
                     (infoMac));
        } else {
            mUserName.setText("");
            mPassword.setText("");
            mUserName.requestFocus();
            Toast.makeText(getActivity(), getString(R.string.user_password_not_found), Toast
                    .LENGTH_SHORT).show();
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
