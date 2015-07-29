package ua.opensvit.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ua.opensvit.data.epg.EpgItem;
import ua.opensvit.data.epg.ProgramItem;

public class ParseUtils {
    private ParseUtils() {
    }

    public static EpgItem parseEpg(String json) {
        EpgItem res = new EpgItem();
        try {
            JSONObject localJSONObject = new JSONObject(json);
            res.setDay(localJSONObject.getInt(EpgItem.DAY));
            res.setSuccess(localJSONObject.getBoolean(EpgItem.SUCCESS));
            res.setDayOfWeek(localJSONObject.getInt(EpgItem.DAY_OF_WEEK));
            if(localJSONObject.has(EpgItem.DESCRIPTION)) {
                res.setDescription(localJSONObject.getString(EpgItem.DESCRIPTION));
            } else {
                JSONArray programsArr = localJSONObject.getJSONObject(ProgramItem
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
}
