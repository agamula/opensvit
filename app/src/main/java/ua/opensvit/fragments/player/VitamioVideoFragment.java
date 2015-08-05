package ua.opensvit.fragments.player;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.leakcanary.RefWatcher;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.vov.vitamio.MediaFormat;
import ua.opensvit.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.services.NextProgramNotifyService;
import ua.opensvit.utils.DateUtils;
import ua.opensvit.widgets.RespondedLayout;

public class VitamioVideoFragment extends VitamioVideoBaseFragment implements MediaPlayer
        .OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnTimedTextListener {

    public static VitamioVideoFragment newInstance(String url, int channelId, int serviceId, long
            timestamp) {
        VitamioVideoFragment fragment = new VitamioVideoFragment();
        fragment.setArguments(fragment.getArgsBundle(url, channelId, serviceId, timestamp));
        return fragment;
    }

    private ProgressBar mProgress;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgress = (ProgressBar) getActivity().findViewById(R.id.progress);
        mProgress.setVisibility(View.VISIBLE);

        final VideoView mVideoView = getVideoView();
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);

        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnTimedTextListener(this);

        onPostViewCreated();
    }

    @Override
    public int getLayoutId() {
        return R.layout.videoview;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        String a = "asdas";
        String b = a + "asd";
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        String a = "asdas";
        String b = a + "asd";
    }

    @Override
    public void onTimedText(String s) {
        String a = "asdas";
        String b = a + "asd";
    }

    @Override
    public void onTimedTextUpdate(byte[] bytes, int i, int i1) {
        String a = "asdas";
        String b = a + "asd";
    }
}
