package ua.opensvit.data;

public class PlayerInfo {
    private boolean isPlaying;
    private long notifyTime;
    private boolean mForceStart;
    private String videoPath;

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public long getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(long notifyTime) {
        this.notifyTime = notifyTime;
    }

    public void setForceStart(boolean mForceStart) {
        this.mForceStart = mForceStart;
    }

    public boolean isForceStart() {
        return mForceStart;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoPath() {
        return videoPath;
    }
}
