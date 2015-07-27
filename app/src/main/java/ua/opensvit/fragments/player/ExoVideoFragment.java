package ua.opensvit.fragments.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.extractor.ts.TsExtractor;
import com.google.android.exoplayer.metadata.GeobMetadata;
import com.google.android.exoplayer.metadata.PrivMetadata;
import com.google.android.exoplayer.metadata.TxxxMetadata;
import com.google.android.exoplayer.text.CaptionStyleCompat;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.SubtitleLayout;
import com.google.android.exoplayer.util.DebugTextViewHelper;
import com.google.android.exoplayer.util.Util;

import java.util.List;
import java.util.Map;

import ua.opensvit.R;
import ua.opensvit.player.EventLogger;
import ua.opensvit.player.ExoPlayerImpl;
import ua.opensvit.player.ExtractorRendererBuilder;
import ua.opensvit.player.HlsRendererBuilder;

public class ExoVideoFragment extends Fragment implements SurfaceHolder.Callback,
        AudioCapabilitiesReceiver.Listener, View.OnClickListener, ExoPlayerImpl.Listener,
        ExoPlayerImpl.CaptionListener, ExoPlayerImpl.Id3MetadataListener {

    private static final String TAG = "PlayerFragment";
    private static final String URL_TAG = "url_tag";

    public ExoVideoFragment() {

    }

    public static ExoVideoFragment newInstance(String url) {
        ExoVideoFragment fragment = new ExoVideoFragment();
        Bundle args = new Bundle();
        args.putString(URL_TAG, url);
        fragment.setArguments(args);
        return fragment;
    }

    private View shutterView;
    private LinearLayout debugRootView;
    private AspectRatioFrameLayout videoFrame;
    private SurfaceView surfaceView;
    private TextView debugTextView;
    private TextView playerStateTextView;
    private SubtitleLayout subtitleLayout;
    //private Button retryButton;

    private String mUrl;
    private boolean playerNeedsPrepare;
    private long playerPosition;

    private MediaController mediaController;
    private AudioCapabilities audioCapabilities;
    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private ExoPlayerImpl player;
    private EventLogger eventLogger;
    private DebugTextViewHelper debugViewHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUrl = getArguments().getString(URL_TAG, "");
        return inflater.inflate(R.layout.fragment_video_new, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View root = view.findViewById(R.id.root);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.performClick();
                }
                return true;
            }
        });
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                    return mediaController.dispatchKeyEvent(event);
                }
                return false;
            }
        });
        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getActivity()
                .getApplicationContext(), this);

        shutterView = view.findViewById(R.id.shutter);
        debugRootView = (LinearLayout) view.findViewById(R.id.controls_root);

        videoFrame = (AspectRatioFrameLayout) view.findViewById(R.id.video_frame);
        surfaceView = (SurfaceView) view.findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
        debugTextView = (TextView) view.findViewById(R.id.debug_text_view);

        playerStateTextView = (TextView) view.findViewById(R.id.player_state_view);
        subtitleLayout = (SubtitleLayout) view.findViewById(R.id.subtitles);

        mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(root);

        //retryButton = (Button) view.findViewById(R.id.retry_button);
        //retryButton.setOnClickListener(this);
    }

    private void configureSubtitleView() {
        CaptionStyleCompat captionStyle;
        float captionFontScale;
        if (Util.SDK_INT >= 19) {
            captionStyle = getUserCaptionStyleV19();
            captionFontScale = getUserCaptionFontScaleV19();
        } else {
            captionStyle = CaptionStyleCompat.DEFAULT;
            captionFontScale = 1.0f;
        }
        subtitleLayout.setStyle(captionStyle);
        subtitleLayout.setFontScale(captionFontScale);
    }

    @TargetApi(19)
    private float getUserCaptionFontScaleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) getActivity().getSystemService(Context.CAPTIONING_SERVICE);
        return captioningManager.getFontScale();
    }

    @TargetApi(19)
    private CaptionStyleCompat getUserCaptionStyleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) getActivity().getSystemService(Context.CAPTIONING_SERVICE);
        return CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle());
    }

    @Override
    public void onResume() {
        super.onResume();
        configureSubtitleView();

        // The player will be prepared on receiving audio capabilities.
        audioCapabilitiesReceiver.register();
        //player.selectTrack(DemoPlayer.TYPE_VIDEO, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        player.setBackgrounded(true);
        audioCapabilitiesReceiver.unregister();
        shutterView.setVisibility(View.VISIBLE);
    }

    private void toggleControlsVisibility() {
        if (mediaController.isShowing()) {
            mediaController.hide();
            debugRootView.setVisibility(View.GONE);
        } else {
            showControls();
        }
    }

    private void showControls() {
        mediaController.show(0);
        debugRootView.setVisibility(View.VISIBLE);
        //HttpDataSource dataSource = new DefaultHttpDataSource(null, )
    }

    private ExoPlayerImpl.RendererBuilder getRendererBuilder() {
        String userAgent = System.getProperty("http.agent");
        /*switch (contentType) {
            case TYPE_SS:
                return new SmoothStreamingRendererBuilder(this, userAgent, contentUri.toString(),
                        new SmoothStreamingTestMediaDrmCallback());
            case TYPE_DASH:
                return new DashRendererBuilder(this, userAgent, contentUri.toString(),
                        new WidevineTestMediaDrmCallback(contentId), audioCapabilities);
            case TYPE_HLS:                                                                */
        if (false) {
            return new HlsRendererBuilder(getActivity(), userAgent, Uri.parse(mUrl).toString(),
                    audioCapabilities);
        }
            /*case TYPE_M4A: // There are no file format differences between M4A and MP4.
            case TYPE_MP4:
                return new ExtractorRendererBuilder(this, userAgent, contentUri, new Mp4Extractor());
            case TYPE_MP3:
                return new ExtractorRendererBuilder(this, userAgent, contentUri, new Mp3Extractor
                ());
            case TYPE_TS:*/
        return new ExtractorRendererBuilder(getActivity(), userAgent, Uri.parse(mUrl),
                new TsExtractor(0, audioCapabilities));
            /*case TYPE_AAC:
                return new ExtractorRendererBuilder(this, userAgent, contentUri, new AdtsExtractor());
            case TYPE_FMP4:
                return new ExtractorRendererBuilder(this, userAgent, contentUri,
                        new FragmentedMp4Extractor());
            case TYPE_WEBM:
            case TYPE_MKV:
                return new ExtractorRendererBuilder(this, userAgent, contentUri, new WebmExtractor());
            default:
                throw new IllegalStateException("Unsupported type: " + contentType);
        }     */
    }

    private void preparePlayer() {
        if (player == null) {
            player = new ExoPlayerImpl(getRendererBuilder());
            player.addListener(this);
            player.setCaptionListener(this);
            player.setMetadataListener(this);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;
            mediaController.setMediaPlayer(player.getPlayerControl());
            mediaController.setEnabled(true);
            eventLogger = new EventLogger();
            eventLogger.startSession();
            player.addListener(eventLogger);
            player.setInfoListener(eventLogger);
            player.setInternalErrorListener(eventLogger);
            debugViewHelper = new DebugTextViewHelper(player, debugTextView);
            debugViewHelper.start();
        }
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(true);
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
            updateButtonVisibilities();
        }
    }

    private void updateButtonVisibilities() {
        //TODO change visibility of controls
        //retryButton.setVisibility(playerNeedsPrepare ? View.VISIBLE : View.GONE);
        //videoButton.setVisibility(haveTracks(DemoPlayer.TYPE_VIDEO) ? View.VISIBLE : View.GONE);
        //audioButton.setVisibility(haveTracks(DemoPlayer.TYPE_AUDIO) ? View.VISIBLE : View.GONE);
        //textButton.setVisibility(haveTracks(DemoPlayer.TYPE_TEXT) ? View.VISIBLE : View.GONE);
    }

    private boolean haveTracks(int type) {
        return player != null && player.getTrackCount(type) > 0;
    }

    private void releasePlayer() {
        if (player != null) {
            debugViewHelper.stop();
            debugViewHelper = null;
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
            eventLogger.endSession();
            eventLogger = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        boolean audioCapabilitiesChanged = !audioCapabilities.equals(this.audioCapabilities);
        if (player == null || audioCapabilitiesChanged) {
            this.audioCapabilities = audioCapabilities;
            releasePlayer();
            preparePlayer();
        } else if (player != null) {
            player.setBackgrounded(false);
        }
    }

    @Override
    public void onClick(View view) {
        //if (view == retryButton) {
        //    preparePlayer();
        //}
    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            showControls();
        }
        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";
                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                break;
            default:
                text += "unknown";
                break;
        }
        playerStateTextView.setText(text);
        updateButtonVisibilities();
    }

    @Override
    public void onError(Exception e) {
        playerNeedsPrepare = true;
        updateButtonVisibilities();
        showControls();
    }

    @Override
    public void onVideoSizeChanged(int width, int height, float pixelWidthAspectRatio) {
        shutterView.setVisibility(View.GONE);
        videoFrame.setAspectRatio(height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }

    @Override
    public void onCues(List<Cue> cues) {
        subtitleLayout.setCues(cues);
    }

    @Override
    public void onId3Metadata(Map<String, Object> metadata) {
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            if (TxxxMetadata.TYPE.equals(entry.getKey())) {
                TxxxMetadata txxxMetadata = (TxxxMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: description=%s, value=%s",
                        TxxxMetadata.TYPE, txxxMetadata.description, txxxMetadata.value));
            } else if (PrivMetadata.TYPE.equals(entry.getKey())) {
                PrivMetadata privMetadata = (PrivMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: owner=%s",
                        PrivMetadata.TYPE, privMetadata.owner));
            } else if (GeobMetadata.TYPE.equals(entry.getKey())) {
                GeobMetadata geobMetadata = (GeobMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s",
                        GeobMetadata.TYPE, geobMetadata.mimeType, geobMetadata.filename,
                        geobMetadata.description));
            } else {
                Log.i(TAG, String.format("ID3 TimedMetadata %s", entry.getKey()));
            }
        }
    }
}
