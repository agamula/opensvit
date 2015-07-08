package ua.opensvit;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.widget.VideoView;

import java.io.IOException;

import org.json.JSONException;

import ua.levtv.library.LevtvDbApi;
import ua.levtv.library.LevtvStruct;

public class VideoViewPlayer extends Activity {
    private LevtvDbApi api = new LevtvDbApi();
    private int ch_id;
    private String ch_name;
    private VideoView mVideoView;
    private LevtvStruct osd;
    private String path = "http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8";
    private int service_id;
    private int type;

    public VideoViewPlayer() {
    }

    public void onConfigurationChanged(Configuration paramConfiguration) {
        if (this.mVideoView != null) {
            this.mVideoView.setVideoLayout(2, 0.0F);
        }
        super.onConfigurationChanged(paramConfiguration);
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.videoview);

        this.path = getIntent().getExtras().getString("ch_path");
        this.ch_name = getIntent().getExtras().getString("ch_name");
        this.ch_id = getIntent().getExtras().getInt("ch_id");
        this.service_id = getIntent().getExtras().getInt("service_id");
        this.type = getIntent().getExtras().getInt("type");
        int i = getIntent().getExtras().getInt("timestamp");
        this.api = VideoStreamApplication.getInstance().getDbApi();
        if (this.type == 0) {
        }
        this.osd = new LevtvStruct();
        this.osd.Osd_struct.success = false;

        try {
            this.osd = this.api.getOsd(Integer.valueOf(this.ch_id), Integer.valueOf(this.service_id));
            if (LibsChecker.checkVitamioLibs(this)) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.mVideoView = ((VideoView) findViewById(R.id.surface_view));
        this.mVideoView.setVideoPath(this.path);
        this.mVideoView.setVideoQuality(16);
        this.mVideoView.setVideoLayout(2, 0.0F);
        //TODO uncomment
        /*if (this.osd.Osd_struct.success) {
            this.mVideoView.setMediaController(new MediaController(this, this.ch_name, (String) this.osd.Osd_struct.IptvOsdItems.title.firstElement(), (String) this.osd.Osd_struct.IptvOsdItems.title.lastElement(), (String) this.osd.Osd_struct.IptvOsdItems.start.firstElement(), (String) this.osd.Osd_struct.IptvOsdItems.start.lastElement(), Boolean.valueOf(this.osd.Osd_struct.success), Integer.valueOf(0), MyApplication.getInstance().getDbApi(), Integer.valueOf(MyApplication.getInstance().getChId()), Integer.valueOf(this.service_id), Integer.valueOf(0)));
        }
        if (this.type == 1) {
            this.mVideoView.setMediaController(new MediaController(this, this.ch_name, "0", "", "", "", Boolean.valueOf(this.osd.Osd_struct.success), Integer.valueOf(1), MyApplication.getInstance().getDbApi(), Integer.valueOf(MyApplication.getInstance().getChId()), Integer.valueOf(this.service_id), Integer.valueOf(0)));
        } else if (this.type == 2) {
            this.mVideoView.setMediaController(new MediaController(this, this.ch_name, "", "", "", "", Boolean.valueOf(this.osd.Osd_struct.success), Integer.valueOf(2), MyApplication.getInstance().getDbApi(), Integer.valueOf(MyApplication.getInstance().getChId()), Integer.valueOf(this.service_id), Integer.valueOf(i)));
        } else {
            this.mVideoView.setMediaController(new MediaController(this, this.ch_name, "", "", "", "", Boolean.valueOf(this.osd.Osd_struct.success), Integer.valueOf(0), MyApplication.getInstance().getDbApi(), Integer.valueOf(MyApplication.getInstance().getChId()), Integer.valueOf(this.service_id), Integer.valueOf(i)));
        } */
    }
}
