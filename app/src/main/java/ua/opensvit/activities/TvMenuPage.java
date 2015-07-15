package ua.opensvit.activities;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import ua.opensvit.VideoStreamApp;
import ua.opensvit.adapters.ChannelListAdapter;
import ua.opensvit.api.OpenWorldApi;
import ua.opensvit.api.LevtvStruct;
import ua.opensvit.data.Film;
import ua.opensvit.data.iptv.menu.TvMenuInfo;
import ua.opensvit.data.iptv.menu.TvMenuItem;
import ua.opensvit.data.iptv.films.FilmItem;
import ua.opensvit.data.iptv.films.FilmsInfo;

public class TvMenuPage extends ExpandableListActivity {
    private OpenWorldApi api;
    private ChannelListAdapter expListAdapter;
    private TvMenuInfo tvMenuInfo;

    public TvMenuPage() {
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        api = VideoStreamApp.getInstance().getApi();
        try {
            this.api.KeepAlive(true);
            tvMenuInfo = this.api.getTvMenu();
            //this.iptvServiceId = this.iptvMenu.Iptv_menu_str.service;
            VideoStreamApp.getInstance().setIpTvServiceId(tvMenuInfo.getService());
            //this.iptvMeunuItemsCount = this.iptvMenu.Iptv_menu_str.IPTVMenuItems.id.size();
            if (tvMenuInfo.isSuccess()) {
                ganre_viev();
            }
            api.KeepAlive(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getNoEpgToast() {
        Toast.makeText(this, new String("Unfortunately EPG for this channel now available"), Toast.LENGTH_SHORT)
                .show();
    }

    private void showChannelPopUp(final int groupPosition, final int channelPosition) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        //TODO uncomment
        //localBuilder.setTitle((String) ((LevtvStruct) this.iptvChannels.get(groupPosition))
         //       .Iptv_channels.IptvChanelsItems.name.get(channelPosition));
        localBuilder.setMessage("Please select action");
        localBuilder.setNegativeButton("Play", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                try {
                    LevtvStruct struct = new LevtvStruct();//TvMenuPage.this.iptvChannels.get
                            //(groupPosition);
                    playChannel(struct.Iptv_channels.IptvChanelsItems.id
                            .get(channelPosition), struct.Iptv_channels
                            .IptvChanelsItems.name.get(channelPosition), TvMenuPage.this);
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
                LevtvStruct struct = new LevtvStruct();//TvMenuPage.this.iptvChannels.get
                // (groupPosition);
                TvMenuPage.this.getEpgChannel(struct.Iptv_channels.IptvChanelsItems.id.get
                        (channelPosition), struct.Iptv_channels.IptvChanelsItems.name
                        .get(channelPosition), struct.Iptv_channels.IptvChanelsItems.archive.get
                        (channelPosition));
            }
        });
        localBuilder.create().show();
    }

    public void ganre_viev() throws IOException {
        List<TvMenuItem> tvMenuItems = tvMenuInfo.getUnmodifiableTVItems();
        ArrayList<String> groupsList = new ArrayList<>();
        for (int i = 0; i < tvMenuItems.size(); i++) {
            groupsList.add(tvMenuItems.get(i).getName());
        }

        ArrayList localArrayList2 = new ArrayList(groupsList.size());
        for (int i = 0; i < groupsList.size(); i++) {
            FilmsInfo filmsInfo = this.api.getFilms(tvMenuItems.get(i).getId());
            List<Film> films = new ArrayList<>();
            List<FilmItem> filmItems = filmsInfo.getUnmodifiableFilms();
            for (int j = 0; j < filmsInfo.getTotal(); j++) {
                FilmItem filmItem = filmItems.get(j);
                Film film = new Film(filmItem.getName(), filmItem.getLogo(), filmItem.getId(),
                        filmItem.getYear(), filmItem.getGenre(), filmItem.getOrigin());
                films.add(film);
            }
            localArrayList2.add(films);
        }

        this.expListAdapter = new ChannelListAdapter(this, groupsList, localArrayList2, null);
        setListAdapter(this.expListAdapter);
    }


    public void getEpgChannel(int paramInt, String paramString, Boolean paramBoolean) {
        VideoStreamApp.getInstance().setChannelId(paramInt);
        LevtvStruct struct = null;
        try {
            struct = this.api.GetEpg(Integer.valueOf(tvMenuInfo.getService()), Integer
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
                struct = this.api.GetEpg(tvMenuInfo.getService(), Integer.valueOf(paramInt),
                        Integer
                        .valueOf(0), Integer.valueOf(0));
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

    public boolean onChildClick(ExpandableListView paramExpandableListView, View paramView, int
            groupPosition, int childPosition, long paramLong) {
        showChannelPopUp(groupPosition, childPosition);
        return true;
    }

    public static void playChannel(int paramInt, String paramString, Context context)
            throws IOException, JSONException {

        VideoStreamApp app = VideoStreamApp.getInstance();

        app.setChannelId(paramInt);
        Intent localIntent = new Intent(context, VideoViewPlayer.class);
        try {
            localIntent.putExtra("ch_path", app.getApi().getChannelIp(Integer.valueOf
                    (paramInt)));
            localIntent.putExtra("ch_name", paramString);
            localIntent.putExtra("ch_id", paramInt);
            localIntent.putExtra("type", 0);
            localIntent.putExtra("service_id", Integer.valueOf(app.getIpTvServiceId()));
            context.startActivity(localIntent);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
