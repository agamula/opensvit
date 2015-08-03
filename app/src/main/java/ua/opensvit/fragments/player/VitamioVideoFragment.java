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

import ua.opensvit.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.services.NextProgramNotifyService;
import ua.opensvit.utils.DateUtils;

public class VitamioVideoFragment extends Fragment implements MediaPlayer
        .OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnTimedTextListener, MediaPlayer.OnPreparedListener, MediaController.OnShownListener {

    private static final String URL_TAG = "url";
    private static final int MESSAGE_START_NOTIFY_SERVICE = 1;

    public VitamioVideoFragment() {
    }

    public static VitamioVideoFragment newInstance(String url, int channelId, int serviceId, long
            timestamp) {
        VitamioVideoFragment fragment = new VitamioVideoFragment();
        Bundle args = new Bundle();
        args.putString(URL_TAG, url);
        args.putInt(NextProgramNotifyService.CHANNEL_ID, channelId);
        args.putInt(NextProgramNotifyService.SERVICE_ID, serviceId);
        args.putLong(NextProgramNotifyService.TIMESTAMP, timestamp);
        fragment.setArguments(args);
        return fragment;
    }

    private VideoView mVideoView;
    private TextView mNextProgramText;
    private String mPath;
    private boolean mShown;
    private int channelId, serviceId;
    private long timestamp;
    private ProgressBar mProgress;
    private int requestCode;

    private static final Handler DELAY_HANDLER = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_START_NOTIFY_SERVICE) {
                VideoStreamApp mApp = VideoStreamApp.getInstance();
                ObjectForNotify notifObj = (ObjectForNotify) msg.obj;
                Intent intent = createNotifyIntent(mApp, notifObj.channelId, notifObj.serviceId,
                        notifObj.timestamp);
                mApp.getApplicationContext().startService(intent);
            }
        }
    };

    private static class ObjectForNotify {
        public final int channelId;
        public final int serviceId;
        public final long timestamp;

        ObjectForNotify(int channelId, int serviceId, long timestamp) {
            this.channelId = channelId;
            this.serviceId = serviceId;
            this.timestamp = timestamp;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NextProgramNotifyService.BROADCAST_NAME)) {
                mNextProgramText.setVisibility(View.GONE);
                String text = createTextToShow(intent.getStringExtra(NextProgramNotifyService
                        .PARAM_TIME_TILL), intent.getStringExtra(NextProgramNotifyService
                        .PARAM_NEXT_PROGRAM_NAME), intent.getIntExtra(NextProgramNotifyService
                        .PARAM_TILL_AFTER_TILL_END, 0));

                if (text != null) {
                    mNextProgramText.setVisibility(View.VISIBLE);
                    mNextProgramText.setText(text);
                    if (mNextProgramText.getAnimation() != null) {
                        mNextProgramText.getAnimation().start();
                    } else {
                        Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim
                                .anim_move_next_program);
                        a.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mNextProgramText.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        mNextProgramText.setAnimation(a);
                    }
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.videoview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();

        channelId = args.getInt(NextProgramNotifyService.CHANNEL_ID);
        serviceId = args.getInt(NextProgramNotifyService.SERVICE_ID);
        timestamp = args.getLong(NextProgramNotifyService.TIMESTAMP);
        mPath = args.getString(URL_TAG);

        mNextProgramText = (TextView) view.findViewById(R.id.next_program_text);
        mProgress = (ProgressBar) getActivity().findViewById(R.id.progress);
        mProgress.setVisibility(View.VISIBLE);

        mVideoView = ((VideoView) view.findViewById(R.id.surface_view));
        mVideoView.setVideoQuality(16);
        mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0.0F);
        MediaController controller = new MediaController(getActivity());
        controller.setInstantSeeking(false);
        controller.setOnShownListener(this);

        mVideoView.setMediaController(controller);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnTimedTextListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setVideoURI(Uri.parse(mPath));
        mVideoView.requestFocus();
        mShown = false;
    }

    private void scheduleAlarmShowNextProgram(boolean isPaused) {
        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        long nowTime = SystemClock.elapsedRealtime();
        long timeBetweenAlarms = TimeUnit.MINUTES.toMillis(getResources().getInteger(R.integer
                .time_between_show_programs_minutes));

        long delay = 0;

        if (!isPaused) {
            delay = TimeUnit.SECONDS.toMillis(getResources().getInteger(R.integer
                    .time_till_display_notify_string_seconds));
        }
        Message msg = DELAY_HANDLER.obtainMessage(MESSAGE_START_NOTIFY_SERVICE);
        msg.obj = new ObjectForNotify(channelId, serviceId, timestamp);

        DELAY_HANDLER.sendMessageDelayed(msg, delay);

        nowTime += timeBetweenAlarms;

        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, nowTime, timeBetweenAlarms,
                createPendingIntent());
    }

    private PendingIntent createPendingIntent() {
        VideoStreamApp app = VideoStreamApp.getInstance();

        Intent intent = createNotifyIntent(app, channelId, serviceId, timestamp);

        if (requestCode == 0) {
            requestCode = new Random().nextInt(100);
        }

        PendingIntent pe = PendingIntent.getService(app.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pe;
    }

    private static Intent createNotifyIntent(VideoStreamApp app, int channelId, int serviceId,
                                             long timestamp) {
        Intent intent = new Intent(app.getApplicationContext(),
                NextProgramNotifyService.class);
        intent.putExtra(NextProgramNotifyService.CHANNEL_ID, channelId);
        intent.putExtra(NextProgramNotifyService.SERVICE_ID, serviceId);
        intent.putExtra(NextProgramNotifyService.TIMESTAMP, timestamp);
        intent.putExtra(NextProgramNotifyService.PARAM_NOW_TIME, Calendar.getInstance(DateUtils
                .getTimeZone()).getTimeInMillis());
        return intent;
    }

    private void stopSchedulingAlarm() {
        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pe = createPendingIntent();
        manager.cancel(pe);
        pe.cancel();
    }

    private long mPosition;

    @Override
    public void onPause() {
        mPosition = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
        super.onPause();
        stopNextProgramNotifier();
    }

    private void stopNextProgramNotifier() {
        stopSchedulingAlarm();
        getActivity().getApplicationContext().unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        boolean isPaused = mPosition > 0;

        if (isPaused) {
            mVideoView.seekTo(mPosition);
            mPosition = 0;
        }
        super.onResume();
        mVideoView.start();
        //mProgress.setVisibility(View.GONE);
        scheduleAlarmShowNextProgram(isPaused);
        getActivity().getApplicationContext().registerReceiver(mReceiver, new IntentFilter
                (NextProgramNotifyService.BROADCAST_NAME));
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
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        String a = "asdas";
        String b = a + "asd";
        return false;
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

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setPlaybackSpeed(1.0f);
        mProgress.setVisibility(View.GONE);
    }

    private String createTextToShow(String paramTimeTill, String paramNextProgramName, int
            tillOrdinal) {
        NextProgramNotifyService.Till till = NextProgramNotifyService.Till.values()[tillOrdinal];
        String res = null;
        switch (till) {
            case Till:
                res = String.format(getString(R.string.till), paramTimeTill, paramNextProgramName);
                break;
            case EndedAfter:
                res = String.format(getString(R.string.ended_after), paramNextProgramName, paramTimeTill);
                break;
            case EndedAs:
                res = String.format(getString(R.string.ended_as), paramNextProgramName, paramTimeTill);
                break;
        }
        return res;
    }

    @Override
    public void onShown() {
        if (!mShown) {
            try {
                Field f = VideoView.class.getDeclaredField("mMediaController");
                f.setAccessible(true);
                MediaController mediaController = (MediaController) f.get(mVideoView);
                f = MediaController.class.getDeclaredField("mFileName");
                f.setAccessible(true);
                TextView mCurrentTime = (TextView) f.get(mediaController);
                mCurrentTime.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mShown = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = VideoStreamApp.getInstance().getRefWatcher();
        refWatcher.watch(this);
    }
}
