package ua.opensvit.utils;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ua.opensvit.data.CreepingLineItem;
import ua.opensvit.data.GetUrlItem;
import ua.opensvit.data.InfoAbout;
import ua.opensvit.data.authorization.AuthorizationInfoBase;
import ua.opensvit.data.authorization.UserProfileBase;
import ua.opensvit.data.authorization.login_password.AuthorizationInfo;
import ua.opensvit.data.authorization.login_password.UserInfo;
import ua.opensvit.data.authorization.mac.AuthorizationInfoMac;
import ua.opensvit.data.authorization.mac.UserProfileMac;
import ua.opensvit.data.channels.Channel;
import ua.opensvit.data.channels.ChannelsInfo;
import ua.opensvit.data.epg.EpgItem;
import ua.opensvit.data.epg.ProgramItem;
import ua.opensvit.data.images.ImageInfo;
import ua.opensvit.data.images.ImageItem;
import ua.opensvit.data.menu.TvMenuInfo;
import ua.opensvit.data.menu.TvMenuItem;
import ua.opensvit.data.osd.OsdItem;
import ua.opensvit.data.osd.ProgramDurationItem;

public class ParseUtils {
    private ParseUtils() {
    }

    private static JSONObject sTempJson;

    public static boolean parseAuthBase(AuthorizationInfoBase res, String json) {
        boolean isAuthenticated = false;
        try {
            sTempJson = new JSONObject(json);
            if (sTempJson.has(AuthorizationInfoBase.ERROR)) {
                res.setError(sTempJson.getString(AuthorizationInfoBase.ERROR));
            } else if (sTempJson.getBoolean(AuthorizationInfoBase.IS_AUTHENTICATED)) {
                res.setIsAuthenticated(true);
                isAuthenticated = true;
                if (sTempJson.getBoolean(AuthorizationInfoBase.IS_ACTIVE)) {
                    res.setIsActive(true);
                }
            }

            if (isAuthenticated) {
                JSONObject userProfileObj = sTempJson.getJSONObject(UserProfileBase.JSON_NAME);
                UserProfileBase userProfileBase = res.getUserProfileBase();
                userProfileBase.setTransparency(userProfileObj.getInt(UserProfileBase.TRANSPARENCY));
                userProfileBase.setId(userProfileObj.getInt(UserProfileBase.ID));
                userProfileBase.setReminder(userProfileObj.getInt(UserProfileBase.REMINDER));
                userProfileBase.setVolume(userProfileObj.getInt(UserProfileBase.VOLUME));
                userProfileBase.setRatio(userProfileObj.getString(UserProfileBase.RATIO));
                userProfileBase.setResolution(userProfileObj.getString(UserProfileBase.RESOLUTION));
                userProfileBase.setLanguage(userProfileObj.getString(UserProfileBase.LANGUAGE));
                userProfileBase.setStartPage(userProfileObj.getString(UserProfileBase.START_PAGE));
                userProfileBase.setType(userProfileObj.getString(UserProfileBase.TYPE));
                userProfileBase.setSkin(userProfileObj.getString(UserProfileBase.SKIN));
                userProfileBase.setShowWelcome(userProfileObj.getBoolean(UserProfileBase
                        .SHOW_WELCOME));
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean parseAuthLoginPassword(AuthorizationInfo res, String json) {
        boolean isAuthenticated = parseAuthBase(res, json);
        if (isAuthenticated) {
            try {
                JSONObject userInfoObj = sTempJson.getJSONObject(UserInfo.JSON_NAME);
                UserInfo userInfo = new UserInfo();
                userInfo.setBalance(userInfoObj.getInt(UserInfo.BALANCE));
                userInfo.setName(userInfoObj.getString(UserInfo.NAME));
                res.setUserInfo(userInfo);
            } catch (JSONException e) {
                e.printStackTrace();
                isAuthenticated = false;
            }
        }
        return isAuthenticated;
    }

    public static boolean parseAuthMac(AuthorizationInfoMac res, String json) {
        boolean isAuthenticated = parseAuthBase(res, json);
        if (isAuthenticated) {
            try {
                res.setSession(sTempJson.getString(AuthorizationInfoMac.J_SESSION));
                JSONObject userProfileObj = sTempJson.getJSONObject(UserProfileMac.JSON_NAME);
                UserProfileMac userProfileMac = (UserProfileMac) res.getUserProfileBase();
                if (userProfileObj.has(UserProfileMac.NETWORK_PATH)) {
                    userProfileMac.setNetworkPath(userProfileObj.getString(UserProfileMac
                            .NETWORK_PATH));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                isAuthenticated = false;
            }
        }
        return isAuthenticated;
    }

    public static EpgItem parseEpg(String json) {
        EpgItem res = new EpgItem();
        try {
            JSONObject jsonObj = new JSONObject(json);
            res.setDay(jsonObj.getInt(EpgItem.DAY));
            res.setSuccess(jsonObj.getBoolean(EpgItem.SUCCESS));
            res.setDayOfWeek(jsonObj.getInt(EpgItem.DAY_OF_WEEK));
            if (jsonObj.has(EpgItem.DESCRIPTION)) {
                res.setDescription(jsonObj.getString(EpgItem.DESCRIPTION));
            } else {
                JSONArray programsArr = jsonObj.getJSONObject(ProgramItem
                        .JSON_PARENT).getJSONArray(ProgramItem.JSON_NAME);
                for (int i = 0; i < programsArr.length(); i++) {
                    JSONObject programObj = programsArr.getJSONObject(i);
                    ProgramItem programItem = new ProgramItem();
                    programItem.setIsArchive(programObj.getBoolean(ProgramItem.IS_ARCHIVE));
                    programItem.setTime(programObj.getString(ProgramItem.TIME));
                    programItem.setTimestamp(programObj.getLong(ProgramItem.TIMESTAMP));
                    programItem.setTitle(programObj.getString(ProgramItem.TITLE));
                    res.addProgram(programItem);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            res = null;
        }
        return res;
    }

    public static TvMenuInfo parseTvMenuInfo(String tvInfoJsonString) {
        TvMenuInfo res = new TvMenuInfo();
        try {
            JSONObject jsonObj = new JSONObject(tvInfoJsonString);
            if (jsonObj.has(TvMenuInfo.SUCCESS)) {
                boolean isSuccess = jsonObj.getBoolean(TvMenuInfo.SUCCESS);
                res.setSuccess(isSuccess);
                if (isSuccess) {
                    if (jsonObj.has(TvMenuInfo.SERVICE)) {
                        res.setService(jsonObj.getInt(TvMenuInfo.SERVICE));
                    }
                    JSONArray ipTvItemsArr = jsonObj.getJSONArray(TvMenuItem.JSON_NAME);
                    for (int i = 0; i < ipTvItemsArr.length(); i++) {
                        JSONObject tvItemObj = ipTvItemsArr.getJSONObject(i);
                        TvMenuItem item = new TvMenuItem();
                        item.setId(tvItemObj.getInt(TvMenuItem.ID));
                        item.setName(tvItemObj.getString(TvMenuItem.NAME));
                        res.addItem(item);
                    }
                }
            } else if (jsonObj.has(TvMenuInfo.ERROR)) {
                String error = jsonObj.getString(TvMenuInfo.ERROR);
                res.setError(error);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            res.setSuccess(false);
        }
        return res;
    }

    public static ChannelsInfo parseChannelsInfo(String result) {
        ChannelsInfo res = new ChannelsInfo();
        try {
            JSONObject jsonObj = new JSONObject(result);
            boolean isSuccess = jsonObj.getBoolean(ChannelsInfo.SUCCESS);
            res.setSuccess(isSuccess);
            if (isSuccess) {
                res.setTotal(jsonObj.getInt(ChannelsInfo.TOTAL));
                JSONArray channelsArr = jsonObj.getJSONArray(Channel.JSON_NAME);
                for (int i = 0; i < channelsArr.length(); i++) {
                    JSONObject channelObj = channelsArr.getJSONObject(i);
                    Channel channel = new Channel();
                    channel.setId(channelObj.getInt(Channel.ID));
                    channel.setName(channelObj.getString(Channel.NAME));
                    channel.setLogo(channelObj.getString(Channel.LOGO));
                    channel.setFavorits(channelObj.getBoolean(Channel.FAVORITS));
                    if (channelObj.has(Channel.ARCHIVE)) {
                        channel.setArchive(channelObj.getString(Channel.ARCHIVE));
                    }
                    res.addChannel(channel);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            res.setSuccess(false);
        }
        return res;
    }

    public static GetUrlItem parseGetUrl(String json) {
        GetUrlItem res = new GetUrlItem();
        try {
            JSONObject jsonObj = new JSONObject(json);
            if (jsonObj.has(GetUrlItem.IP)) {
                res.setUrl(jsonObj.getString(GetUrlItem.IP));
            } else {
                res.setUrl(jsonObj.getString(GetUrlItem.URL));
            }
            res.setHasInfoLine(jsonObj.getBoolean(GetUrlItem.HAS_INFO_LINE));
            res.setSuccess(jsonObj.getBoolean(GetUrlItem.SUCCESS));
        } catch (JSONException e) {
            e.printStackTrace();
            res = null;
        }
        return res;
    }

    public static OsdItem parseOsd(String json) {
        OsdItem res = new OsdItem();
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray programsArr = jsonObj.getJSONArray(ProgramDurationItem.JSON_NAME);
            for (int i = 0; i < programsArr.length(); i++) {
                JSONObject programObj = programsArr.getJSONObject(i);
                ProgramDurationItem programDurationItem = new ProgramDurationItem();
                programDurationItem.setAbsTimeElapsedInPercent(programObj.getInt(ProgramDurationItem.DURATION));
                programDurationItem.setTitle(programObj.getString(ProgramDurationItem.TITLE));
                programDurationItem.setStart(programObj.getString(ProgramDurationItem.START));
                programDurationItem.setEnd(programObj.getString(ProgramDurationItem.END));
                res.addProgram(programDurationItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            res = null;
        }
        return res;
    }

    public static CreepingLineItem parseCreepingLine(String json) {
        CreepingLineItem res = new CreepingLineItem();
        try {
            JSONObject jsonObj = new JSONObject(json);
            res.setSuccess(jsonObj.getBoolean(CreepingLineItem.SUCCESS));
            res.setText(jsonObj.getString(CreepingLineItem.TEXT));
        } catch (JSONException e) {
            e.printStackTrace();
            res = null;
        }
        return res;
    }

    public static ImageInfo parseImageInfo(String json) {
        ImageInfo res = new ImageInfo();
        try {
            JSONObject jsonObj = new JSONObject(json);
            res.setSuccess(jsonObj.getBoolean(ImageInfo.SUCCESS));
            JSONArray imageItemsObj = jsonObj.getJSONArray(ImageItem.JSON_NAME);
            for (int i = 0; i < imageItemsObj.length(); i++) {
                ImageItem imageItem = new ImageItem();
                imageItem.setUrl(imageItemsObj.getString(i));
                res.addImageItem(imageItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            res = null;
        }
        return res;
    }

    public static InfoAbout parseInfoAbout(String json) {
        InfoAbout res = new InfoAbout();
        try {
            JSONObject jsonObj = new JSONObject(json);
            res.setJava(jsonObj.getString(InfoAbout.JAVA));
        } catch (JSONException e) {
            e.printStackTrace();
            res = null;
        }
        return res;
    }
}
