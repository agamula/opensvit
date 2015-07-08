package ua.opensvit;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class IcAbout
        extends Activity {
    public IcAbout() {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ic_about);
        TextView paramBundle = (TextView) findViewById(R.id.textAbout);
        paramBundle.setText("Developed by Inform Consult (UA).\n\nUrl: http://www.iconsult.com" +
                ".ua \n\nUrl: http://www.levtv.net \n\nE-mail: Info@iconsult.com.ua\n\nVer.: 0" +
                ".17. LevTvSvit(prototype)\n\nInform Consult 2013(c)");
        Linkify.addLinks(paramBundle, 15);
    }
}
