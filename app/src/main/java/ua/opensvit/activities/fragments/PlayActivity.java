package ua.opensvit.activities.fragments;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import ua.opensvit.R;

public class PlayActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener{

    private VideoView mVideoView;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!LibsChecker.checkVitamioLibs(this)) {
            return;
        }
        setContentView(R.layout.activity_play);
        this.mVideoView = ((VideoView) findViewById(R.id.surface_view));
        mPath = new File(Environment.getExternalStorageDirectory(), "San_Diego_Clip.ts")
                .getAbsolutePath();
        //this.mVideoView.setVideoQuality(16);
        //this.mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0.0F);
        MediaController controller = new MediaController(this);
        //controller.setFileName("File name");
        //controller.setAnchorView(view);
        //controller.setInstantSeeking(true);
        //controller.setEnabled(true);

        mVideoView.setVideoURI(Uri.parse(mPath));
        mVideoView.setMediaController(controller);
        //mVideoView.setOnBufferingUpdateListener(this);
        //mVideoView.setOnCompletionListener(this);
        //mVideoView.setOnInfoListener(this);
        //mVideoView.setOnTimedTextListener(this);
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setPlaybackSpeed(1.0f);
    }
}
