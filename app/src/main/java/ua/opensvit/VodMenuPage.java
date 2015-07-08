package ua.opensvit;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import ua.levtv.library.LevtvDbApi;
import ua.levtv.library.LevtvStruct;

public class VodMenuPage extends ExpandableListActivity {
    LevtvStruct VodMenu;
    int VodMeunuItemsCount;
    LevtvDbApi api = new LevtvDbApi();
    String[] channelsArh;
    int context;
    String[] epgArh;
    private VodListAdapter expListAdapter;
    LevtvStruct iptvEpg;
    Vector<LevtvStruct> iptvFilms = new Vector(0);
    int iptvServiceId;
    ListView lv1;
    String[] lvArrNew;

    public VodMenuPage() {
    }

    private void getNoEpgToast() {
        Toast.makeText(this, new String("Unfortunately EPG for this channel now available"), Toast.LENGTH_SHORT)
                .show();
    }

    public void ganre_viev() throws IOException {
        ArrayList localArrayList1 = new ArrayList();
        for (int i = 0; i < VodMeunuItemsCount; i++) {
            localArrayList1.add(lvArrNew[i]);
        }

        ArrayList localArrayList2 = new ArrayList(localArrayList1.size());
        for (int i = 0; i < localArrayList1.size(); i++) {
            LevtvStruct struct = this.api.getFilms(this.VodMenu.Iptv_menu_str.IPTVMenuItems.id.get
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

        this.expListAdapter = new VodListAdapter(this, localArrayList1, localArrayList2, this.api);
        setListAdapter(this.expListAdapter);
    }

    public boolean onChildClick(ExpandableListView paramExpandableListView, View paramView, int paramInt1, int paramInt2, long paramLong) {
        Intent intent = new Intent();
        intent.setClass(this, FilmAct.class);
        intent.putExtra("ch_id", (Serializable) ((LevtvStruct) this.iptvFilms.get(paramInt1)).Films_struct.IptvFilmsItems.id.get(paramInt2));
        startActivity(intent);
        return true;
    }

    public void onContentChanged() {
        super.onContentChanged();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String login = getIntent().getExtras().getString("user_login");
        String password = getIntent().getExtras().getString("user_password");
        VideoStreamApplication.getInstance().setDbApi(this.api);
        VideoStreamApplication.getInstance().setVodPage(this);
        try {
            this.api.getAuth(login, password);
            this.VodMenu = new LevtvStruct();
            this.api.KeepAlive(true);
            this.VodMenu = this.api.getVodMenu();
            this.iptvServiceId = this.VodMenu.Iptv_menu_str.service;
            this.VodMeunuItemsCount = this.VodMenu.Iptv_menu_str.IPTVMenuItems.id.size();
            this.lvArrNew = new String[this.VodMenu.Iptv_menu_str.IPTVMenuItems.id.size()];
            if (this.VodMenu.Iptv_menu_str.success) {
                System.out.println(this.VodMenu.Iptv_menu_str.IPTVMenuItems.id.size());
                for (int i = 0; i < this.VodMenu.Iptv_menu_str.IPTVMenuItems.id.size(); i++) {
                    this.lvArrNew[i] = this.VodMenu.Iptv_menu_str.IPTVMenuItems.name.get(i);
                }
            }
            ganre_viev();
            api.KeepAlive(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
