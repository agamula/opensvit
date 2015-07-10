package ua.opensvit.activities;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.opensvit.VideoStreamApp;
import ua.opensvit.activities.FilmAct;
import ua.opensvit.adapters.VodListAdapter;
import ua.opensvit.api.OpenWorldApi;
import ua.opensvit.data.Film;
import ua.opensvit.data.iptv.base.TvMenuInfo;
import ua.opensvit.data.iptv.base.TvMenuItem;
import ua.opensvit.data.iptv.films.FilmItem;
import ua.opensvit.data.iptv.films.FilmsInfo;

public class VodMenuPage extends ExpandableListActivity {
    private TvMenuInfo VodMenu;
    private VodListAdapter expListAdapter;
    private OpenWorldApi api;

    public VodMenuPage() {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        api = VideoStreamApp.getInstance().getApi();
        try {
            this.api.KeepAlive(true);
            this.VodMenu = this.api.getVodMenu();
            if (this.VodMenu.isSuccess()) {
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

    public void ganre_viev() throws IOException {
        ArrayList<String> tvGroupNames = new ArrayList<>();
        List<TvMenuItem> tvItems = VodMenu.getUnmodifiableTVItems();
        for (int i = 0; i < tvItems.size(); i++) {
            tvGroupNames.add(tvItems.get(i).getName());
        }

        ArrayList localArrayList2 = new ArrayList(tvGroupNames.size());
        for (int i = 0; i < tvGroupNames.size(); i++) {
            FilmsInfo filmsInfo = this.api.getFilms(tvItems.get(i).getId());
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

        this.expListAdapter = new VodListAdapter(this, tvGroupNames, localArrayList2, this.api);
        setListAdapter(this.expListAdapter);
    }

    public boolean onChildClick(ExpandableListView paramExpandableListView, View paramView, int paramInt1, int paramInt2, long paramLong) {
        Intent intent = new Intent();
        intent.setClass(this, FilmAct.class);
        //TODO uncomment
        //intent.putExtra("ch_id", (Serializable) ((LevtvStruct) this.iptvFilms.get(paramInt1))
        //        .Films_struct.IptvFilmsItems.id.get(paramInt2));
        startActivity(intent);
        return true;
    }
}
