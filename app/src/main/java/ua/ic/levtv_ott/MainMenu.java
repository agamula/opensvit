package ua.ic.levtv_ott;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainMenu extends ListActivity {
    public MainMenu() {
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setListAdapter(new ArrayAdapter<>(this, R.layout.display_item, new String[]{"TV", "VoD",
                "About"}));
        onContentChanged();
        getListView().setFocusable(false);
    }

    @Override
    protected void onListItemClick(ListView paramListView, View paramView, int position, long
            paramLong) {
        super.onListItemClick(paramListView, paramView, position, paramLong);
        switch (position) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(this, TvMenuPage.class);
                String login = getIntent().getExtras().getString("user_login");
                intent.putExtra("user_password", getIntent().getExtras().getString("user_password"));
                intent.putExtra("user_login", login);
                startActivity(intent);
                break;
            case 1:
                Toast.makeText(this, "No access to service", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                intent = new Intent();
                intent.setClass(this, IcAbout.class);
                startActivity(intent);
                break;
        }
    }
}
