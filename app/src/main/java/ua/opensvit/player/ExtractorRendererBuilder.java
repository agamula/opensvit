package ua.opensvit.player;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.drm.DrmSessionManager;
import com.google.android.exoplayer.drm.MediaDrmCallback;
import com.google.android.exoplayer.drm.StreamingDrmSessionManager;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.extractor.Extractor;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.text.TextTrackRenderer;
import com.google.android.exoplayer.text.tx3g.Tx3gParser;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;


import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaDrm;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import java.util.HashMap;
import java.util.UUID;

import ua.opensvit.http.OkHttpClientRunnable;


public class ExtractorRendererBuilder implements ExoPlayerImpl.RendererBuilder, MediaDrmCallback {


    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 160;


    private final Context context;
    private final String userAgent;
    private final Uri uri;
    private final Extractor extractor;


    public ExtractorRendererBuilder(Context context, String userAgent, Uri uri, Extractor extractor) {
        this.context = context;
        this.userAgent = userAgent;
        this.uri = uri;
        this.extractor = extractor;
    }


    @Override
    public void buildRenderers(ExoPlayerImpl player, ExoPlayerImpl.RendererBuilderCallback callback) {
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);


        // Build the video and audio renderers.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(player.getMainHandler(),
                null);
        DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, extractor,
                allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

        Handler playerHandler = player.getMainHandler();

        try {
            HashMap<String, String> optionalParams = new HashMap<>();
            optionalParams.put("User-Agent", OkHttpClientRunnable.USER_AGENT);
            DrmSessionManager drmSessionManager = StreamingDrmSessionManager.newWidevineInstance
                    (playerHandler.getLooper(), this, optionalParams, playerHandler, player);

            MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(sampleSource,
                    drmSessionManager, true, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 5000, null, player
                    .getMainHandler(),
                    player, 50) {
                @Override
                protected void doSomeWork(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
                    super.doSomeWork(positionUs, elapsedRealtimeUs);
                }
            };
            MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                    drmSessionManager, true, player.getMainHandler(), player) {
                @Override
                protected void doSomeWork(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
                    super.doSomeWork(positionUs, elapsedRealtimeUs);
                }
            };
            TrackRenderer textRenderer = new TextTrackRenderer(sampleSource, player,
                    player.getMainHandler().getLooper(), new Tx3gParser());


            // Invoke the callback.
            TrackRenderer[] renderers = new TrackRenderer[ExoPlayerImpl.RENDERER_COUNT];
            renderers[ExoPlayerImpl.TYPE_VIDEO] = videoRenderer;
            //renderers[DemoPlayer.TYPE_AUDIO] = audioRenderer;
            renderers[ExoPlayerImpl.TYPE_TEXT] = textRenderer;
            callback.onRenderers(null, null, renderers, bandwidthMeter);
        } catch (UnsupportedDrmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] executeProvisionRequest(UUID uuid, MediaDrm.ProvisionRequest provisionRequest) throws Exception {
        if(Build.VERSION.SDK_INT >= 18) {
            return provisionRequest.getData();
        } else {
            return new byte[0];
        }
    }

    @Override
    public byte[] executeKeyRequest(UUID uuid, MediaDrm.KeyRequest keyRequest) throws Exception {
        if(Build.VERSION.SDK_INT >= 18) {
            return keyRequest.getData();
        } else {
            return new byte[0];
        }
    }
}
