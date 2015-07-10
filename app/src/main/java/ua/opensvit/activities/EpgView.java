package ua.opensvit.activities;

import android.annotation.SuppressLint;
import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ua.opensvit.VideoStreamApp;
import ua.opensvit.adapters.EpgListApapter;
import ua.opensvit.api.OpenWorldApi;
import ua.opensvit.data.EpgItem;

@SuppressLint({"SimpleDateFormat"})
public class EpgView extends ExpandableListActivity {
    OpenWorldApi api = new OpenWorldApi();
    Boolean archive;
    int chId;
    String chName;
    String data = new String("1");
    String date = new String("1");
    int day = 356;
    String disc = null;
    String epgEnd;
    String epgGo;
    ArrayList<EpgItem> epgItem = new ArrayList();
    ArrayList<ArrayList<EpgItem>> epgItems = new ArrayList();
    private EpgListApapter expListAdapter = null;
    TvMenuPage firstAndLast;
    ArrayList<String> groupNames = new ArrayList();
    int nowProg = -1;
    int thisDay = -1;

    public EpgView() {
    }

    private void getEpgNew() throws JSONException {
        new JSONObject();
        JSONArray localJSONArray = new JSONObject(this.epgGo + "}").getJSONObject("items").getJSONArray("programs");
        this.nowProg = this.epgItem.size();
        int i = 0;
        if (i >= localJSONArray.length()) {
            return;
        }
        JSONObject localJSONObject = localJSONArray.getJSONObject(i);
        int j = localJSONObject.getInt("timestamp");
        this.date = new SimpleDateFormat("dd").format(new Date(j * 1000L));
        if (this.day != Integer.parseInt(this.date)) {
            this.day = Integer.parseInt(this.date);
            if (this.epgItem.size() > 0) {
                this.epgItems.add(this.epgItem);
            }
            this.data = new SimpleDateFormat("dd/MM/yyyy").format(new Date(j * 1000L));
            this.groupNames.add(this.data);
            this.epgItem = new ArrayList();
        }
        if (i == 0) {
        }
        for (boolean bool = true; ; bool = false) {
            this.epgItem.add(new EpgItem((String) localJSONObject.get("title"), (String) localJSONObject.get("time"), Integer.valueOf(localJSONObject.getInt("startTime")), Integer.valueOf(localJSONObject.getInt("timestamp")), Boolean.valueOf(bool), Boolean.valueOf(false)));
            if (i == localJSONArray.length() - 1) {
                this.epgItems.add(this.epgItem);
            }
            i += 1;
            break;
        }
    }

    private void getEpgOld() throws JSONException {
        new JSONObject();
        JSONArray localJSONArray = new JSONObject(this.epgEnd).getJSONObject("items").getJSONArray("programs");
        int i = 0;
        for (; ; ) {
            if (i >= localJSONArray.length()) {
                return;
            }
            JSONObject localJSONObject = localJSONArray.getJSONObject(i);
            int j = localJSONObject.getInt("timestamp");
            if (j > System.currentTimeMillis() / 1000L - 172800L) {
                this.date = new SimpleDateFormat("dd").format(new Date(j * 1000L));
                if (this.day != Integer.parseInt(this.date)) {
                    this.day = Integer.parseInt(this.date);
                    if (this.epgItem.size() > 0) {
                        this.epgItems.add(this.epgItem);
                    }
                    this.thisDay += 1;
                    this.nowProg = 0;
                    this.data = new SimpleDateFormat("dd/MM/yyyy").format(new Date(j * 1000L));
                    this.groupNames.add(this.data);
                    this.epgItem = new ArrayList();
                }
                this.nowProg += 1;
                this.epgItem.add(new EpgItem((String) localJSONObject.get("title"), (String) localJSONObject.get("time"), Integer.valueOf(localJSONObject.getInt("startTime")), Integer.valueOf(localJSONObject.getInt("timestamp")), Boolean.valueOf(false), this.archive));
                localJSONArray.length();
            }
            i += 1;
        }
    }

    private void getViewEpg() throws JSONException {
        getEpgOld();
        getEpgNew();
        this.expListAdapter = new EpgListApapter(this, this.groupNames, this.epgItems);
        setListAdapter(this.expListAdapter);
        getExpandableListView().expandGroup(this.thisDay);
        getExpandableListView().setSelection(this.thisDay);
        if (this.nowProg >= 2) {
            getExpandableListView().setSelectedChild(this.thisDay, this.nowProg - 1, true);
            getExpandableListView().setSelectedChild(this.thisDay, this.nowProg, true);
        }
    }

    public boolean onChildClick(ExpandableListView paramExpandableListView, View paramView, int paramInt1, int paramInt2, long paramLong) {
        if ((paramInt2 == this.nowProg) && (paramInt1 == this.thisDay)) {
            return false;
        }
        try {
            TvMenuPage.playChannel(this.chId, this.chName, this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        this.api = VideoStreamApp.getInstance().getApi();
        this.chId = getIntent().getExtras().getInt("ch_id");
        this.chName = getIntent().getExtras().getString("ch_name");
        this.epgGo = getIntent().getExtras().getString("epg_go");
        this.epgEnd = getIntent().getExtras().getString("epg_end");
        this.archive = Boolean.valueOf(getIntent().getExtras().getBoolean("archive", false));
        try {
            getViewEpg();
            return;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
