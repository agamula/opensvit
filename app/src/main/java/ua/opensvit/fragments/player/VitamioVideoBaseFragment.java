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
import android.widget.TextView;

import com.squareup.leakcanary.RefWatcher;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.vov.vitamio.MediaFormat;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.services.NextProgramNotifyService;
import ua.opensvit.utils.DateUtils;
import ua.opensvit.widgets.RespondedLayout;

public abstract class VitamioVideoBaseFragment extends Fragment implements MediaPlayer
        .OnInfoListener, MediaPlayer.OnPreparedListener, MediaController
        .OnShownListener {
    private static final String URL_TAG = "url";
    private static final int MESSAGE_START_NOTIFY_SERVICE = 1;

    public abstract int getLayoutId();

    public final Bundle getArgsBundle(String url, int channelId, int serviceId, long
            timestamp) {
        Bundle args = new Bundle();
        args.putString(URL_TAG, url);
        args.putInt(NextProgramNotifyService.CHANNEL_ID, channelId);
        args.putInt(NextProgramNotifyService.SERVICE_ID, serviceId);
        args.putLong(NextProgramNotifyService.TIMESTAMP, timestamp);
        return args;
    }

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

    private static class InternalHandler extends Handler {

        InternalHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_START_NOTIFY_SERVICE) {
                VideoStreamApp mApp = VideoStreamApp.getInstance();
                ObjectForNotify notifyObj = (ObjectForNotify) msg.obj;
                Intent intent = createNotifyIntent(mApp, notifyObj.channelId, notifyObj.serviceId,
                        notifyObj.timestamp);
                mApp.getApplicationContext().startService(intent);
            }
        }
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

    private int channelId, serviceId;
    private long timestamp;

    protected int getChannelId() {
        return channelId;
    }

    protected int getServiceId() {
        return serviceId;
    }

    protected long getTimestamp() {
        return timestamp;
    }

    private VideoView mVideoView;

    protected VideoView getVideoView() {
        return mVideoView;
    }

    private RespondedLayout mRespondedLayout;

    protected RespondedLayout getRespondedLayout() {
        return mRespondedLayout;
    }

    private TextView mNextProgramText;
    private String mPath;
    private boolean mShown;
    private int requestCode;
    private boolean mVideoViewNotExist;
    private static final Handler DELAY_HANDLER = new InternalHandler(Looper.getMainLooper());

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
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
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

        mVideoView = (VideoView) view.findViewById(R.id.surface_view);

        if(mVideoView == null) {
            mVideoViewNotExist = true;
        } else {
            mVideoView.setOnInfoListener(this);
            mVideoView.setOnPreparedListener(this);

            MediaController controller = (MediaController) view.findViewById(R.id.media_controller);//new
            // MediaController(getActivity());
            controller.setInstantSeeking(false);
            controller.setOnShownListener(this);
            mVideoView.setMediaController(controller);
            mRespondedLayout = (RespondedLayout) view.findViewById(R.id.video_responded);
        }
    }

    protected void onPostViewCreated() {
        if(mVideoViewNotExist) {
            return;
        }
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

    private void stopSchedulingAlarm() {
        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pe = createPendingIntent();
        manager.cancel(pe);
        pe.cancel();
    }

    private void stopNextProgramNotifier() {
        stopSchedulingAlarm();
        getActivity().getApplicationContext().unregisterReceiver(mReceiver);
    }

    private long mPosition;

    @Override
    public void onPause() {
        if(mVideoViewNotExist) {
            super.onPause();
            return;
        }
        mPosition = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
        super.onPause();
        stopNextProgramNotifier();
    }

    @Override
    public void onResume() {
        if(mVideoViewNotExist) {
            super.onResume();
            return;
        }

        boolean isPaused = mPosition > 0;

        if (isPaused) {
            mVideoView.seekTo(mPosition);
            mPosition = 0;
        }
        super.onResume();
        mVideoView.start();
        scheduleAlarmShowNextProgram(isPaused);
        getActivity().getApplicationContext().registerReceiver(mReceiver, new IntentFilter
                (NextProgramNotifyService.BROADCAST_NAME));
    }

    @Override
    public final boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        String a = "asdas";
        String b = a + "asd";
        return false;
    }

    private SparseArray<MediaFormat> audioFormats;

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setPlaybackSpeed(1.0f);
        audioFormats = mVideoView.getAudioTrackMap("utf-8");
    }

    protected SparseArray<MediaFormat> getAudioFormats() {
        return audioFormats;
    }

    @Override
    public final void onShown() {
        if (!mShown) {
            try {
                Field f = VideoView.class.getDeclaredField("mMediaController");
                f.setAccessible(true);
                MediaController mediaController = (MediaController) f.get(mVideoView);
                f = MediaController.class.getDeclaredField("mFileName");
                f.setAccessible(true);
                TextView mCurrentTime = (TextView) f.get(mediaController);
                if(mCurrentTime != null) {
                    mCurrentTime.setVisibility(View.GONE);
                }
                mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_ORIGIN, 0); //zoom = full screen
            } catch (Exception e) {
                e.printStackTrace();
            }
            mShown = true;
        }
    }

    @Override
    public final void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = VideoStreamApp.getInstance().getRefWatcher();
        refWatcher.watch(this);
    }
}
