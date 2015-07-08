package ua.opensvit.api;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ua.opensvit.data.ApiConstants;
import ua.opensvit.data.authorization.AuthorizationInfo;
import ua.opensvit.data.authorization.UserInfo;
import ua.opensvit.data.authorization.UserProfile;
import ua.opensvit.utils.ApiUtils;

public class OpenWorldApi {
    private String applicationPathVod = new String("http://195.22.112.90:34000/levtvsv_pc/");
    SyncHttpClient gets = new SyncHttpClient();
    String httpOut = new String();
    public Object kostyl;
    String pub_error = new String();
    private final String mApiPath;

    public OpenWorldApi() {
        mApiPath = ApiUtils.getBaseUrl();
    }

    public String GetArchiveUrl(int param1, int param2) throws IOException, JSONException {
        String url = this.mApiPath + "/ws/GetArchiveUrl?id=" + param1 + "&timestamp=" +
                param2;
        this.httpOut = this.gets.get(url);
        JSONObject res = new JSONObject(this.httpOut);
        if (res.has("success")) {
            return res.get("url").toString();
        }
        return "error";
    }

    public String GetChannelIp(int param) throws IOException, JSONException {

        String url = this.mApiPath + "/ws/GetChannelIp?id=" + param;
        this.httpOut = this.gets.get(url);
        JSONObject localJSONObject = new JSONObject(this.httpOut);
        String res = null;
        if (localJSONObject.getBoolean("success")) {
            res = (String) localJSONObject.get("ip");
        }
        return res;
    }

    public LevtvStruct GetEpg(int param1, int param2, int param3, int param4)
            throws IOException, JSONException {
        LevtvStruct res = new LevtvStruct();
        String url = this.mApiPath + "/ws/GetEpg?serviceId=" + param1 +
                "&channelId=" + param2 + "&perPage=1&page=" + param4;
        this.httpOut = this.gets.get(url);

        JSONObject jsonObj = new JSONObject(this.httpOut);
        if (jsonObj.getBoolean("success")) {
            res.Iptv_epg.day = jsonObj.getInt("day");
            res.Iptv_epg.dayOfWeek = jsonObj.getInt("dayOfWeek");
            if (!jsonObj.has("description")) {
                res.Iptv_epg.total = jsonObj.getInt("total");
                url = this.mApiPath + "/ws/GetEpg?serviceId=" + param1 + "&channelId=" +
                        param2 + "&perPage=" + res.Iptv_epg.total + "&page=" + param4;
                this.httpOut = this.gets.get(url);

            }
            return res;
        } else {
            return null;
        }
    }

    public void KeepAlive(boolean keepAlive) {
        gets.keepAlive(keepAlive);
        new Thread() {
            /* Error */
            public void run() {

                SyncHttpClient client = null;
                try {
                    client = gets.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                if (client != null) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        client.get(mApiPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // Byte code:
                //   0: new 27	ua/ic/levtv/library/SyncHttpClient
                //   3: dup
                //   4: invokespecial 28	ua/ic/levtv/library/SyncHttpClient:<init>	()V
                //   7: astore_1
                //   8: aload_0
                //   9: getfield 15	ua/ic/levtv/library/LevtvDbApi$1:this$0	Lua/ic/levtv/library/LevtvDbApi;
                //   12: getfield 32	ua/ic/levtv/library/LevtvDbApi:gets	Lua/ic/levtv/library/SyncHttpClient;
                //   15: invokevirtual 36	ua/ic/levtv/library/SyncHttpClient:clone	()Lua/ic/levtv/library/SyncHttpClient;
                //   18: astore_2
                //   19: aload_2
                //   20: astore_1
                //   21: ldc2_w 37
                //   24: invokestatic 42	ua/ic/levtv/library/LevtvDbApi$1:sleep	(J)V
                //   27: new 44	java/lang/String
                //   30: dup
                //   31: new 46	java/lang/StringBuilder
                //   34: dup
                //   35: aload_0
                //   36: getfield 15	ua/ic/levtv/library/LevtvDbApi$1:this$0	Lua/ic/levtv/library/LevtvDbApi;
                //   39: getfield 50	ua/ic/levtv/library/LevtvDbApi:mApiPath	Ljava/lang/String;
                //   42: invokestatic 54	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
                //   45: invokespecial 57	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
                //   48: ldc 59
                //   50: invokevirtual 63	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   53: invokevirtual 67	java/lang/StringBuilder:toString	()Ljava/lang/String;
                //   56: invokespecial 68	java/lang/String:<init>	(Ljava/lang/String;)V
                //   59: astore_2
                //   60: aload_1
                //   61: aload_2
                //   62: invokevirtual 72	ua/ic/levtv/library/SyncHttpClient:get	(Ljava/lang/String;)Ljava/lang/String;
                //   65: pop
                //   66: goto -45 -> 21
                //   69: astore_2
                //   70: aload_2
                //   71: invokevirtual 75	java/io/IOException:printStackTrace	()V
                //   74: goto -53 -> 21
                //   77: astore_1
                //   78: aload_1
                //   79: invokevirtual 76	java/lang/InterruptedException:printStackTrace	()V
                //   82: return
                //   83: astore_2
                //   84: aload_2
                //   85: invokevirtual 77	java/lang/CloneNotSupportedException:printStackTrace	()V
                //   88: goto -67 -> 21
                // Local variable table:
                //   start	length	slot	name	signature
                //   0	91	0	this	1
                //   7	54	1	localObject1	Object
                //   77	2	1	localInterruptedException	InterruptedException
                //   18	44	2	localObject2	Object
                //   69	2	2	localIOException	IOException
                //   83	2	2	localCloneNotSupportedException	CloneNotSupportedException
                // Exception table:
                //   from	to	target	type
                //   60	66	69	java/io/IOException
                //   21	60	77	java/lang/InterruptedException
                //   60	66	77	java/lang/InterruptedException
                //   70	74	77	java/lang/InterruptedException
                //   8	19	83	java/lang/CloneNotSupportedException
            }
        }.start();
    }

    public String OrderFilm(int param) throws IOException, JSONException {
        return "http://193.53.89.7/vod/Amelia.mp4";
    }

    public boolean ToggleIptvFavorites(int param) throws IOException, JSONException {
        String url = this.mApiPath + "/ws/ToggleIptvFavorites?iptvId=" + param;
        this.httpOut = this.gets.get(url);
        return new JSONObject(this.httpOut).getBoolean("success");
    }

    public AuthorizationInfo getAuth(String paramString1, String paramString2)
            throws IOException {
        AuthorizationInfo res = new AuthorizationInfo();
        String url = ApiUtils.getApiUrl(ApiConstants.LOGIN_URL, paramString1, paramString2);
        this.httpOut = this.gets.get(url);
        boolean isAuthenticated = false;
        try {
            JSONObject jsonObj = new JSONObject(this.httpOut);
            if (jsonObj.has(AuthorizationInfo.ERROR)) {
                res.setError(jsonObj.getString(AuthorizationInfo.ERROR));
            } else if (jsonObj.getBoolean(AuthorizationInfo.IS_ACTIVE)) {
                res.setIsActive(true);
                if (jsonObj.getBoolean(AuthorizationInfo.IS_AUTHENTICATED)) {
                    res.setIsAuthenticated(true);
                    isAuthenticated = true;
                }
            }
            if (!isAuthenticated) {
                return res;
            }
            JSONObject userInfoObj = jsonObj.getJSONObject(UserInfo.JSON_NAME);
            UserInfo userInfo = new UserInfo();
            userInfo.setBalance(userInfoObj.getInt(UserInfo.BALANCE));
            userInfo.setName(userInfoObj.getString(UserInfo.NAME));
            res.setUserInfo(userInfo);
            JSONObject userProfileObj = jsonObj.getJSONObject(UserProfile.JSON_NAME);
            UserProfile userProfile = new UserProfile();
            userProfile.setTransparency(userProfileObj.getInt(UserProfile.TRANSPARENCY));
            userProfile.setId(userProfileObj.getInt(UserProfile.ID));
            userProfile.setReminder(userProfileObj.getInt(UserProfile.REMINDER));
            userProfile.setVolume(userProfileObj.getInt(UserProfile.VOLUME));
            userProfile.setRatio(userProfileObj.getString(UserProfile.RATIO));
            userProfile.setResolution(userProfileObj.getString(UserProfile.RESOLUTION));
            userProfile.setLanguage(userProfileObj.getString(UserProfile.LANGUAGE));
            userProfile.setStartPage(userProfileObj.getString(UserProfile.START_PAGE));
            userProfile.setType(userProfileObj.getString(UserProfile.TYPE));
            userProfile.setSkin(userProfileObj.getString(UserProfile.SKIN));
            userProfile.setShowWelcome(userProfileObj.getBoolean(UserProfile.SHOW_WELCOME));
            res.setUserProfile(userProfile);
        } catch (JSONException e) {
            e.printStackTrace();
            return res;
        }
        return res;
    }

    public void getAuthVod() {
        String str = new String(this.applicationPathVod + "/ws/AuthStb?sn=CS1630K8080A3211116000357&mac=00:1a:d0:07:a1:62");
        try {
            this.httpOut = this.gets.get(str);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LevtvStruct getFilm(int paramInt) throws JSONException {
        LevtvStruct res = new LevtvStruct();
        String url = this.applicationPathVod + "/ws/GetFilm?id=" + paramInt;
        try {
            this.httpOut = this.gets.get(url);
            JSONObject jsonObj = new JSONObject(this.httpOut);
            if (jsonObj.getBoolean("success")) {
                res.Films_struct.success = true;
                res.Film_struct.id = jsonObj.getInt("id");
                res.Film_struct.year = jsonObj.getInt("year");
                res.Film_struct.price = jsonObj.getInt("price");
                res.Film_struct.duration = jsonObj.getInt("duration");
                res.Film_struct.actor = jsonObj.getString("actor");
                res.Film_struct.country = jsonObj.getString("country");
                res.Film_struct.genre = jsonObj.getString("genre");
                res.Film_struct.logo = jsonObj.getString("logo");
                res.Film_struct.name = jsonObj.getString("name");
                res.Film_struct.origin = jsonObj.getString("origin");
                res.Film_struct.description = jsonObj.getString("description");
            }
        } catch (IOException e) {
            e.printStackTrace();
            res.Films_struct.success = false;
            System.out.println("is Active false");
        }
        return res;
    }

    public LevtvStruct getFilms(int param) throws IOException {
        LevtvStruct res = new LevtvStruct();
        String url = this.applicationPathVod + "ws/GetFilms?perPage=0&page=0&genreId=" +
                param;
        this.httpOut = this.gets.get(url);
        try {
            JSONObject jsonObj = new JSONObject(this.httpOut);
            if (jsonObj.getBoolean("success")) {
                res.Films_struct.success = true;
                res.Films_struct.total = jsonObj.getInt("total");

                JSONArray itemsArr = jsonObj.getJSONArray("items");
                for (int i = 0; i < itemsArr.length(); i++) {
                    JSONObject localJSONObject = itemsArr.getJSONObject(i);
                    res.Films_struct.IptvFilmsItems.id.addElement(Integer.valueOf(localJSONObject.getInt("id")));
                    res.Films_struct.IptvFilmsItems.name.addElement(localJSONObject.getString("name"));
                    res.Films_struct.IptvFilmsItems.logo.addElement(localJSONObject.getString("logo"));
                    res.Films_struct.IptvFilmsItems.year.addElement(Integer.valueOf(localJSONObject.getInt("year")));
                    res.Films_struct.IptvFilmsItems.origin.addElement(localJSONObject.getString("origin"));
                }
            } else {
                res.Films_struct.success = false;
                System.out.println("is Active false");
            }
        } catch (JSONException paramObject) {
            paramObject.printStackTrace();
            res.Films_struct.success = false;
            return res;
        }
        return res;
    }

    public LevtvStruct getIptvChannels(String param) throws IOException {
        LevtvStruct res = new LevtvStruct();
        String url = this.mApiPath +
                "/ws/GetChannels?perPage=0&page=0&genreId=" + param;
        this.httpOut = this.gets.get(url);
        try {
            JSONObject jsonObj = new JSONObject(this.httpOut);
            if (jsonObj.getBoolean("success")) {
                res.Iptv_channels.success = true;
                res.Iptv_channels.total = jsonObj.getInt("total");
                JSONArray jsonArr = jsonObj.getJSONArray("items");
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject localJSONObject = jsonArr.getJSONObject(i);
                    res.Iptv_channels.IptvChanelsItems.id.addElement(Integer.valueOf(
                            localJSONObject.getInt("id")));
                    res.Iptv_channels.IptvChanelsItems.name.addElement(localJSONObject.getString
                            ("name"));
                    if (localJSONObject.has("logo")) {
                        res.Iptv_channels.IptvChanelsItems.logo.addElement("/images/" +
                                localJSONObject.getString("logo"));
                    }
                    res.Iptv_channels.IptvChanelsItems.favorite.addElement(Boolean.valueOf
                            (localJSONObject.getBoolean("favorite")));
                    res.Iptv_channels.IptvChanelsItems.allowed.addElement(Boolean.valueOf
                            (localJSONObject.getBoolean("allowed")));
                    if (!localJSONObject.has("archive")) {
                        res.Iptv_channels.IptvChanelsItems.archive.addElement(Boolean.valueOf
                                (false));
                        continue;
                    }
                    res.Iptv_channels.IptvChanelsItems.archive.addElement(Boolean.valueOf
                            (localJSONObject.getBoolean("archive")));
                    res.Iptv_channels.IptvChanelsItems.logo.addElement("/lev.png");
                }
            } else {
                res.Iptv_channels.success = false;
                System.out.println("is Active false");
            }
        } catch (JSONException paramObject) {
            paramObject.printStackTrace();
            res.Iptv_channels.success = false;
            return res;
        }
        return res;
    }

    public LevtvStruct getIptvMenu() throws IOException {
        LevtvStruct res = new LevtvStruct();
        String url = this.mApiPath + "/ws/AuthService?type=iptv";
        this.httpOut = this.gets.get(url);
        try {
            JSONObject jsonObj = new JSONObject(this.httpOut);
            if (jsonObj.getBoolean("success")) {
                res.Iptv_menu_str.success = true;
                res.Iptv_menu_str.service = jsonObj.getInt("service");
                JSONArray jsonArr = jsonObj.getJSONArray("items");
                System.out.println(jsonArr.length());
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject localJSONObject = jsonArr.getJSONObject(i);
                    res.Iptv_menu_str.IPTVMenuItems.id.addElement(Integer.valueOf
                            (localJSONObject.getInt("id")));
                    res.Iptv_menu_str.IPTVMenuItems.name.addElement(localJSONObject
                            .getString("name"));
                }
            } else {
                res.Iptv_menu_str.success = false;
                System.out.println("is Active false");
            }

        } catch (JSONException localJSONException) {
            localJSONException.printStackTrace();
            res.Iptv_menu_str.success = false;
            return res;
        }
        return res;
    }

    public void getKeepAliveVod() {
        new Thread() {
            /* Error */
            public void run() {
                // Byte code:
                //   0: new 27	ua/ic/levtv/library/SyncHttpClient
                //   3: dup
                //   4: invokespecial 28	ua/ic/levtv/library/SyncHttpClient:<init>	()V
                //   7: astore_1
                //   8: aload_0
                //   9: getfield 15	ua/ic/levtv/library/LevtvDbApi$2:this$0	Lua/ic/levtv/library/LevtvDbApi;
                //   12: getfield 32	ua/ic/levtv/library/LevtvDbApi:gets	Lua/ic/levtv/library/SyncHttpClient;
                //   15: invokevirtual 36	ua/ic/levtv/library/SyncHttpClient:clone	()Lua/ic/levtv/library/SyncHttpClient;
                //   18: astore_2
                //   19: aload_2
                //   20: astore_1
                //   21: ldc2_w 37
                //   24: invokestatic 42	ua/ic/levtv/library/LevtvDbApi$2:sleep	(J)V
                //   27: new 44	java/lang/String
                //   30: dup
                //   31: new 46	java/lang/StringBuilder
                //   34: dup
                //   35: aload_0
                //   36: getfield 15	ua/ic/levtv/library/LevtvDbApi$2:this$0	Lua/ic/levtv/library/LevtvDbApi;
                //   39: getfield 50	ua/ic/levtv/library/LevtvDbApi:applicationPathVod	Ljava/lang/String;
                //   42: invokestatic 54	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
                //   45: invokespecial 57	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
                //   48: ldc 59
                //   50: invokevirtual 63	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   53: invokevirtual 67	java/lang/StringBuilder:toString	()Ljava/lang/String;
                //   56: invokespecial 68	java/lang/String:<init>	(Ljava/lang/String;)V
                //   59: astore_2
                //   60: aload_1
                //   61: aload_2
                //   62: invokevirtual 72	ua/ic/levtv/library/SyncHttpClient:get	(Ljava/lang/String;)Ljava/lang/String;
                //   65: pop
                //   66: goto -45 -> 21
                //   69: astore_2
                //   70: aload_2
                //   71: invokevirtual 75	java/io/IOException:printStackTrace	()V
                //   74: goto -53 -> 21
                //   77: astore_1
                //   78: aload_1
                //   79: invokevirtual 76	java/lang/InterruptedException:printStackTrace	()V
                //   82: return
                //   83: astore_2
                //   84: aload_2
                //   85: invokevirtual 77	java/lang/CloneNotSupportedException:printStackTrace	()V
                //   88: goto -67 -> 21
                // Local variable table:
                //   start	length	slot	name	signature
                //   0	91	0	this	2
                //   7	54	1	localObject1	Object
                //   77	2	1	localInterruptedException	InterruptedException
                //   18	44	2	localObject2	Object
                //   69	2	2	localIOException	IOException
                //   83	2	2	localCloneNotSupportedException	CloneNotSupportedException
                // Exception table:
                //   from	to	target	type
                //   60	66	69	java/io/IOException
                //   21	60	77	java/lang/InterruptedException
                //   60	66	77	java/lang/InterruptedException
                //   70	74	77	java/lang/InterruptedException
                //   8	19	83	java/lang/CloneNotSupportedException
            }
        }.start();
    }

    public LevtvStruct getOsd(int param1, int param2) throws IOException, JSONException {
        LevtvStruct res = new LevtvStruct();
        String url = this.mApiPath + "/ws/GetChannelOsd?channelId=" + param1 +
                "&serviceId=" + param2;
        this.httpOut = this.gets.get(url);
        JSONObject jsonObj = new JSONObject(this.httpOut);
        res.Osd_struct.success = false;
        if (jsonObj.has("description")) {
            res.Osd_struct.success = false;
        }
        if (jsonObj.has("programs")) {
            res.Osd_struct.success = true;
            JSONArray jsonArr = jsonObj.getJSONArray("programs");
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject paramObject2 = jsonArr.getJSONObject(i);
                res.Osd_struct.IptvOsdItems.currTime.addElement(Integer.valueOf(paramObject2.getInt("currTime")));
                res.Osd_struct.IptvOsdItems.duration.addElement(Integer.valueOf(paramObject2.getInt("duration")));
                res.Osd_struct.IptvOsdItems.durationTime.addElement(Integer.valueOf(paramObject2.getInt("durationTime")));
                res.Osd_struct.IptvOsdItems.end.addElement(paramObject2.getString("end"));
                res.Osd_struct.IptvOsdItems.start.addElement(paramObject2.getString("start"));
                res.Osd_struct.IptvOsdItems.title.addElement(paramObject2.getString("title"));
            }
        }
        return res;
    }

    public LevtvStruct getOsdArch(String param1, String param2, Integer paramInteger)
            throws IOException, JSONException {
        LevtvStruct res = new LevtvStruct();
        String url = this.mApiPath + "/ws/GetChannelOsd?channelId=" +
                param1 + "&serviceId=" + param2 + "&timestamp=" + paramInteger;
        this.httpOut = this.gets.get(url);
        JSONObject jsonObj = new JSONObject(this.httpOut);
        res.Osd_struct.success = false;
        if (jsonObj.has("description")) {
            res.Osd_struct.success = false;
        } else {
            res.Osd_struct.success = true;
            if (jsonObj.has("programs")) {
                res.Osd_struct.success = true;
                JSONArray jsonArr = jsonObj.getJSONArray("programs");
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObject = jsonArr.getJSONObject(i);
                    res.Osd_struct.IptvOsdItems.currTime.addElement(Integer.valueOf(jsonObject.getInt
                            ("currTime")));
                    res.Osd_struct.IptvOsdItems.duration.addElement(Integer.valueOf(jsonObject.getInt("duration")));
                    res.Osd_struct.IptvOsdItems.durationTime.addElement(Integer.valueOf(jsonObject.getInt("durationTime")));
                    res.Osd_struct.IptvOsdItems.end.addElement(jsonObject.getString("end"));
                    res.Osd_struct.IptvOsdItems.start.addElement(jsonObject.getString("start"));
                    res.Osd_struct.IptvOsdItems.title.addElement(jsonObject.getString("title"));
                }
            }
        }
        return res;
    }

    public LevtvStruct getVodMenu() throws IOException {
        LevtvStruct res = new LevtvStruct();
        String url = this.applicationPathVod + "/ws/VodMenu";
        this.httpOut = this.gets.get(url);
        try {
            JSONObject jsonObj = new JSONObject(this.httpOut);
            if (jsonObj.getBoolean("success")) {
                res.Iptv_menu_str.success = true;
                res.Iptv_menu_str.service = 1;
                JSONArray jsonArr = jsonObj.getJSONArray("items");
                System.out.println(jsonArr.length());
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject localJSONObject = jsonArr.getJSONObject(i);
                    res.Iptv_menu_str.IPTVMenuItems.id.addElement(Integer.valueOf
                            (localJSONObject.getInt("id")));
                    res.Iptv_menu_str.IPTVMenuItems.name.addElement(localJSONObject.getString
                            ("name"));
                    if (!res.Iptv_menu_str.success) {
                        break;
                    }
                }
            } else {
                res.Iptv_menu_str.success = false;
                System.out.println("is Active false");
            }
        } catch (JSONException localJSONException) {
            localJSONException.printStackTrace();
            res.Iptv_menu_str.success = false;
            return res;
        }
        return res;
    }

    public String getApiPath() {
        return mApiPath;
    }

    public String getApplicationPathVod() {
        return applicationPathVod;
    }
}
