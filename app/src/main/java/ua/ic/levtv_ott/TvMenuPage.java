package ua.ic.levtv_ott;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;

import ua.ic.levtv.library.LevtvDbApi;
import ua.ic.levtv.library.LevtvStruct;

public class TvMenuPage extends ExpandableListActivity {
    LevtvDbApi api = new LevtvDbApi();
    String[] channelsArh;
    int context;
    String[] epgArh;
    private ChannelListAdapter expListAdapter;
    Vector<LevtvStruct> iptvChannels = new Vector(0);
    LevtvStruct iptvEpg;
    LevtvStruct iptvMenu;
    int iptvMeunuItemsCount;
    int iptvServiceId;
    ListView lv1;
    String[] lvArrNew;

    public TvMenuPage() {
    }

    private void getNoEpgToast() {
        Toast.makeText(this, new String("Unfortunately EPG for this channel now available"), Toast.LENGTH_SHORT)
                .show();
    }

    private void showChannelPopUp(final int paramInt1, final int paramInt2) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle((String) ((LevtvStruct) this.iptvChannels.get(paramInt1)).Iptv_channels.IptvChanelsItems.name.get(paramInt2));
        localBuilder.setMessage("Please select action");
        localBuilder.setNegativeButton("Play", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                try {
                    LevtvStruct struct = TvMenuPage.this.iptvChannels.get(paramInt1);
                    TvMenuPage.this.playChannel(struct.Iptv_channels.IptvChanelsItems.id
                            .get(paramInt2), struct.Iptv_channels
                            .IptvChanelsItems.name.get(paramInt2));
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        localBuilder.setPositiveButton("EPG", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                LevtvStruct struct = TvMenuPage.this.iptvChannels.get(paramInt1);
                TvMenuPage.this.getEpgChannel(struct.Iptv_channels.IptvChanelsItems.id.get
                        (paramInt2), struct.Iptv_channels.IptvChanelsItems.name
                        .get(paramInt2), struct.Iptv_channels.IptvChanelsItems.archive.get
                        (paramInt2));
            }
        });
        localBuilder.create().show();
    }

    public void ganre_viev() throws IOException {
        ArrayList localArrayList1 = new ArrayList();
        for (int i = 0; i < iptvMeunuItemsCount; i++) {
            localArrayList1.add(lvArrNew[i]);
        }

        ArrayList localArrayList2 = new ArrayList(localArrayList1.size());
        for (int i = 0; i < localArrayList1.size(); i++) {
            LevtvStruct struct = this.api.getFilms(this.iptvMenu.Iptv_menu_str.IPTVMenuItems.id.get
                    (i));
            List<Film> films = new ArrayList<>();
            for (int j = 0; j < struct.Films_struct.total; j++) {
                films.add(new Film(struct.Films_struct
                        .IptvFilmsItems.name.get(j), struct.Films_struct.IptvFilmsItems.logo.get
                        (j), struct.Films_struct.IptvFilmsItems.id.get(j), struct.Films_struct
                        .IptvFilmsItems.year.get(j), "0", struct.Films_struct
                        .IptvFilmsItems.origin.get(j)));
            }
            localArrayList2.add(films);
        }

        this.expListAdapter = new ChannelListAdapter(this, localArrayList1, localArrayList2, this.api);
        setListAdapter(this.expListAdapter);
    }


    public void getEpgChannel(int paramInt, String paramString, Boolean paramBoolean) {
        MyApplication.getInstance().setChId(paramInt);
        LevtvStruct struct = null;
        try {
            struct = this.api.GetEpg(Integer.valueOf(this.iptvServiceId), Integer
                            .valueOf
                                    (paramInt),
                    Integer.valueOf(0), Integer.valueOf(-1));
        } catch (IOException e) {
            e.printStackTrace();
            struct = null;
        } catch (JSONException e) {
            e.printStackTrace();
            struct = null;
        }
        if (struct == null) {
            try {
                struct = this.api.GetEpg(Integer.valueOf(this.iptvServiceId), Integer.valueOf(paramInt), Integer.valueOf(0), Integer.valueOf(0));
            } catch (IOException e) {
                e.printStackTrace();
                struct = null;
            } catch (JSONException e) {
                e.printStackTrace();
                struct = null;
            }
        }
        if (struct != null) {
            Intent intent = new Intent();
            intent.setClass(this, EpgView.class);
            //TODO make go and end string
            String go = "go";
            String end = "end";
            intent.putExtra("epg_go", go);
            intent.putExtra("epg_end", end);
            intent.putExtra("ch_name", paramString);
            intent.putExtra("ch_id", paramInt);
            intent.putExtra("archive", paramBoolean);
            startActivity(intent);
        } else {
            getNoEpgToast();
        }
    }

    public boolean onChildClick(ExpandableListView paramExpandableListView, View paramView, int paramInt1, int paramInt2, long paramLong) {
        System.out.println(paramInt1);
        showChannelPopUp(paramInt1, paramInt2);
        return true;
    }

    public void onContentChanged() {
        super.onContentChanged();
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        String login = getIntent().getExtras().getString("user_login");
        String password = getIntent().getExtras().getString("user_password");
        MyApplication.getInstance().setDbApi(this.api);
        MyApplication.getInstance().setUserPage(this);
        try {
            this.iptvMenu = new LevtvStruct();
            this.api.getAuth(login, password);
            this.api.KeepAlive();
            this.iptvMenu = this.api.getIptvMenu();
            this.iptvServiceId = this.iptvMenu.Iptv_menu_str.service;
            this.iptvMeunuItemsCount = this.iptvMenu.Iptv_menu_str.IPTVMenuItems.id.size();
            this.lvArrNew = new String[this.iptvMenu.Iptv_menu_str.IPTVMenuItems.id.size()];
            if (this.iptvMenu.Iptv_menu_str.success) {
                System.out.println(this.iptvMenu.Iptv_menu_str.IPTVMenuItems.id.size());
                for (int i = 0; i < this.iptvMenu.Iptv_menu_str.IPTVMenuItems.id.size(); i++) {
                    this.lvArrNew[i] = this.iptvMenu.Iptv_menu_str.IPTVMenuItems.name.get(i);
                }
                ganre_viev();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playChannel(int paramInt, String paramString)
            throws IOException, JSONException {
        MyApplication.getInstance().setChId(paramInt);
        Intent localIntent = new Intent();
        localIntent.setClass(this, VideoViewPlayer.class);
        try {
            localIntent.putExtra("ch_path", this.api.GetChannelIp(Integer.valueOf(paramInt)));
            localIntent.putExtra("ch_name", paramString);
            localIntent.putExtra("ch_id", paramInt);
            localIntent.putExtra("type", 0);
            localIntent.putExtra("service_id", Integer.valueOf(this.iptvServiceId));
            startActivity(localIntent);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
