package ua.ic.levtv_ott;

public class Channel
{
    public String color = null;
    boolean favorits = false;
    int id = 0;
    String logo = null;
    String name = null;
    public String rgb = null;
    public boolean state = false;

    public Channel(String paramString1, String paramString2, Object paramObject1, Object paramObject2)
    {
        this.name = paramString1;
        this.logo = paramString2;
        this.favorits = ((Boolean)paramObject1).booleanValue();
        this.id = ((Integer)paramObject2).intValue();
    }

    public boolean getFavorits()
    {
        return this.favorits;
    }

    public int getId()
    {
        return this.id;
    }

    public String getLogo()
    {
        return this.logo;
    }

    public String getName()
    {
        return this.name;
    }

    public void setFavorits(boolean paramBoolean)
    {
        this.favorits = paramBoolean;
    }
}
