package ua.opensvit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONException;

import ua.opensvit.api.OpenWorldApi;
import ua.opensvit.api.LevtvStruct;

@SuppressLint({"NewApi"})
public class FilmAct extends Activity {
    OpenWorldApi api = new OpenWorldApi();
    int chId;
    String filmName = new String();
    int type = 1;

    public FilmAct() {
    }

    private Drawable grabImageFromUrl(String paramString)
            throws Exception {
        return Drawable.createFromStream((InputStream) new URL(paramString).getContent(), "src");
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.vod_activitivod);
        this.api = VideoStreamApp.getInstance().getApi();
        this.chId = getIntent().getExtras().getInt("ch_id");
        LevtvStruct struct = null;
        try {
            struct = this.api.getFilm(this.chId);
            if(struct == null) {
                return;
            }
            String imageUrl = this.api.getApplicationPathVod() + "/" + struct
                    .Film_struct.logo;
            WindowManager localWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            final int screenWidth, screenHeight;

            if (Build.VERSION.SDK_INT >= 11) {
                Point p = new Point();
                localWindowManager.getDefaultDisplay().getSize(p);
                screenWidth = p.x;
                screenHeight = p.y;
            } else {
                Display display = localWindowManager.getDefaultDisplay();
                screenWidth = display.getWidth();
                screenHeight = display.getHeight();
            }

            this.filmName = struct.Film_struct.name;
            ((TextView) findViewById(R.id.FilmTitle)).setText(filmName);
            TextView filmYearTextView = (TextView) findViewById(R.id.FilmYear);
            StringBuilder builder = new StringBuilder(String.valueOf(struct.Film_struct.origin)).append
                    ("(");
            builder.append(String.valueOf(struct.Film_struct.year) + ")");
            filmYearTextView.setText(builder.toString());
            ((TextView) findViewById(R.id.FilmAct)).setText(struct.Film_struct.actor);
            ((TextView) findViewById(R.id.FilmDesc)).setText(struct.Film_struct.description);
            TextView filmPriceTextView = (TextView) findViewById(R.id.FilmPrice);
            builder = new StringBuilder("Price: ");
            builder.append(String.valueOf(struct.Film_struct.price));
            filmPriceTextView.setText(builder.toString());
            TextView htmlTextView = (TextView) findViewById(R.id.htmlTextView);
            htmlTextView.setText("Pay and Play");
            htmlTextView.setClickable(true);
            htmlTextView.setFocusable(true);
            htmlTextView.setFocusableInTouchMode(true);
            htmlTextView.setBackgroundColor(-7829368);
            htmlTextView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramAnonymousView) {
                    Intent intent = new Intent();
                    intent.setClass(FilmAct.this, VideoViewPlayer.class);
                    try {
                        intent.putExtra("ch_path", FilmAct.this.api.OrderFilm(Integer.valueOf(FilmAct
                                .this.chId)));
                        intent.putExtra("ch_name", FilmAct.this.filmName);
                        intent.putExtra("ch_id", 0);
                        intent.putExtra("service_id", 0);
                        intent.putExtra("type", FilmAct.this.type);
                        FilmAct.this.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            ImageView imageView = (ImageView) findViewById(R.id.imageLogoVod);
            try {
                imageView.setImageDrawable(grabImageFromUrl(imageUrl));
            } catch (Exception e) {
                e.printStackTrace();
            }
            imageView.getLayoutParams().height = ((int) (screenHeight * 0.4D));
            imageView.getLayoutParams().width = ((int) (screenWidth * 0.4D));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
