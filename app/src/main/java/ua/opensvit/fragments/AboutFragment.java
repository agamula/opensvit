package ua.opensvit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.leakcanary.RefWatcher;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;

public class AboutFragment extends Fragment {

    private TextView mAbout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ic_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAbout = (TextView) view.findViewById(R.id.textAbout);
        mAbout.setText(getString(R.string.about_text));
        Linkify.addLinks(mAbout, Linkify.ALL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = VideoStreamApp.getInstance().getRefWatcher();
        refWatcher.watch(this);
    }
}
