package ua.opensvit.data;

public class EpgItem
{
    private final boolean archive;
    private String color;
    private boolean isPlayNow;
    private final int start_time;
    private final String time;
    private final int timestamp;
    private final String title;

    public EpgItem(String paramString1, String paramString2, Object paramObject1, Object paramObject2, Boolean paramBoolean1, Boolean paramBoolean2)
    {
        this.title = paramString1;
        this.time = paramString2;
        this.isPlayNow = paramBoolean1.booleanValue();
        this.start_time = ((Integer)paramObject1).intValue();
        this.timestamp = ((Integer)paramObject2).intValue();
        this.archive = paramBoolean2.booleanValue();
    }

    public boolean getIsArchive()
    {
        return this.archive;
    }

    public boolean getIsPlayNow()
    {
        return this.isPlayNow;
    }

    public int getStartTime()
    {
        return this.start_time;
    }

    public String getTime()
    {
        return this.time;
    }

    public int getTimestamp()
    {
        return this.timestamp;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setIsPlayNow(boolean isPlayNow) {
        this.isPlayNow = isPlayNow;
    }
}
