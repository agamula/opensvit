package ua.opensvit.data;

public class PlayerInfo {
    private long playerPosition;
    private boolean isPlaying;
    private long notifyTime;
    private boolean mForceStart;

    public void setPlayerPosition(long playPosition) {
        this.playerPosition = playPosition;
    }

    public long getPlayerPosition() {
        return playerPosition;
    }

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
}