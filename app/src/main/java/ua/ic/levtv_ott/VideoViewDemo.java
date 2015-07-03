package ua.ic.levtv_ott;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoViewDemo extends Activity {
    String ch_path;

    public VideoViewDemo() {
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        this.ch_path = getIntent().getExtras().getString("ch_path");
        setContentView(R.layout.videoviewdemo);
        VideoView videoView = (VideoView) findViewById(R.id.VideoView25);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoPath("http://s08.savefrom.net/media/119149540/503edda5a18de0e441eaa2517176e7df/Интернационал+(The+Internationale+-+Russian+lyrics).mp4");
        videoView.start();
    }
}
