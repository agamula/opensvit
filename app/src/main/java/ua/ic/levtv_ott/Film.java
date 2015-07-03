package ua.ic.levtv_ott;

public class Film
{
    String ganre = null;
    int id = 0;
    String logo = null;
    String name = null;
    String origin = null;
    int year = 0;

    public Film(String paramString1, String paramString2, Object paramObject1, Object paramObject2, String paramString3, String paramString4)
    {
        this.name = paramString1;
        this.logo = paramString2;
        this.ganre = paramString3;
        this.year = ((Integer)paramObject2).intValue();
        this.origin = paramString4;
        this.id = ((Integer)paramObject1).intValue();
    }

    public String getGanre()
    {
        return this.ganre;
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

    public String getOrigin()
    {
        return this.origin;
    }

    public int getYear()
    {
        return this.year;
    }
}
