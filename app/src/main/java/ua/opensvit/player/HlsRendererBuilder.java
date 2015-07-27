package ua.opensvit.player;

import com.google.android.exoplayer.DefaultLoadControl;
import com.google.android.exoplayer.hls.HlsPlaylist;

import com.google.android.exoplayer.LoadControl;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.chunk.VideoFormatSelectorUtil;
import com.google.android.exoplayer.hls.HlsChunkSource;
import com.google.android.exoplayer.hls.HlsMasterPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylistParser;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.metadata.Id3Parser;
import com.google.android.exoplayer.metadata.MetadataTrackRenderer;
import com.google.android.exoplayer.text.eia608.Eia608TrackRenderer;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.ManifestFetcher;
import com.google.android.exoplayer.util.ManifestFetcher.ManifestCallback;


import android.content.Context;
import android.media.MediaCodec;
import android.os.Handler;
import android.util.Log;


import java.io.IOException;
import java.util.Map;

public class HlsRendererBuilder implements ExoPlayerImpl.RendererBuilder,
        ManifestCallback<HlsPlaylist>, ManifestFetcher.EventListener {


    private static final int BUFFER_SEGMENT_SIZE = 256 * 1024;
    private static final int BUFFER_SEGMENTS = 64;


    private final Context context;
    private final String userAgent;
    private final String url;
    private final AudioCapabilities audioCapabilities;


    private ExoPlayerImpl player;
    private ExoPlayerImpl.RendererBuilderCallback callback;


    public HlsRendererBuilder(Context context, String userAgent, String url,
                              AudioCapabilities audioCapabilities) {
        this.context = context;
        this.userAgent = userAgent;
        this.url = url;
        this.audioCapabilities = audioCapabilities;
    }


    @Override
    public void buildRenderers(ExoPlayerImpl player, ExoPlayerImpl.RendererBuilderCallback callback) {
        this.player = player;
        this.callback = callback;
        HlsPlaylistParser parser = new HlsPlaylistParser();
        ManifestFetcher<HlsPlaylist> playlistFetcher = new ManifestFetcher<>(url,
                new DefaultUriDataSource(context, userAgent), parser, player.getMainHandler(), this);
        playlistFetcher.singleLoad(player.getMainHandler().getLooper(), this);
    }


    @Override
    public void onSingleManifestError(IOException e) {
        callback.onRenderersError(e);
    }


    @Override
    public void onSingleManifest(HlsPlaylist manifest) {
        Handler mainHandler = player.getMainHandler();
        LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(BUFFER_SEGMENT_SIZE));
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();


        int[] variantIndices = null;
        if (manifest instanceof HlsMasterPlaylist) {
            HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) manifest;
            try {
                variantIndices = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay(
                        context, masterPlaylist.variants, null, false);
            } catch (DecoderQueryException e) {
                callback.onRenderersError(e);
                return;
            }
        }


        DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);
        HlsChunkSource chunkSource = new HlsChunkSource(dataSource, url, manifest, bandwidthMeter,
                variantIndices, HlsChunkSource.ADAPTIVE_MODE_SPLICE, audioCapabilities);
        HlsSampleSource sampleSource = new HlsSampleSource(chunkSource, loadControl,
                BUFFER_SEGMENTS * BUFFER_SEGMENT_SIZE, true, mainHandler, player, ExoPlayerImpl.TYPE_VIDEO);
        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(sampleSource,
                MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 5000, mainHandler, player, 50);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);


        MetadataTrackRenderer<Map<String, Object>> id3Renderer =
                new MetadataTrackRenderer<>(sampleSource, new Id3Parser(), player, mainHandler.getLooper());


        Eia608TrackRenderer closedCaptionRenderer = new Eia608TrackRenderer(sampleSource, player,
                mainHandler.getLooper());


        TrackRenderer[] renderers = new TrackRenderer[ExoPlayerImpl.RENDERER_COUNT];
        renderers[ExoPlayerImpl.TYPE_VIDEO] = videoRenderer;
        renderers[ExoPlayerImpl.TYPE_AUDIO] = audioRenderer;
        renderers[ExoPlayerImpl.TYPE_METADATA] = id3Renderer;
        renderers[ExoPlayerImpl.TYPE_TEXT] = closedCaptionRenderer;
        callback.onRenderers(null, null, renderers, bandwidthMeter);
    }


    @Override
    public void onManifestRefreshStarted() {
        Log.e("HSL Loading", "Started");
    }

    @Override
    public void onManifestRefreshed() {
        Log.e("HSL Loading", "Refreshed");
    }

    @Override
    public void onManifestError(IOException e) {
        Log.e("HSL Loading", "Finished");
    }
}