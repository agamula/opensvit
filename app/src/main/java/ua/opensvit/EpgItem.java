package ua.opensvit;

public class EpgItem
{
    boolean archive = false;
    public String color = null;
    boolean isPlayNow = false;
    public String rgb = null;
    int start_time = 0;
    public boolean state = false;
    public String time = null;
    int timestamp = 0;
    public String title = null;

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
}
