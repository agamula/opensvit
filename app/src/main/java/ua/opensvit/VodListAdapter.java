package ua.opensvit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import ua.opensvit.api.OpenWorldApi;
import ua.opensvit.data.Film;

@SuppressLint({"NewApi"})
public class VodListAdapter extends BaseExpandableListAdapter {
    private OpenWorldApi api;
    private ArrayList<ArrayList<Film>> channels;
    private Context context;
    private ArrayList<String> groups;
    private LayoutInflater inflater;

    public VodListAdapter(Context paramContext, ArrayList<String> paramArrayList, ArrayList<ArrayList<Film>> paramArrayList1, OpenWorldApi paramOpenWorldApi) {
        this.context = paramContext;
        this.groups = paramArrayList;
        this.channels = paramArrayList1;
        this.api = paramOpenWorldApi;
        this.inflater = LayoutInflater.from(paramContext);
    }

    private Drawable grabImageFromUrl(String paramString)
            throws Exception {
        return Drawable.createFromStream((InputStream) new URL(paramString).getContent(), "src");
    }

    public Object getChild(int paramInt1, int paramInt2) {
        this.context.getTheme();
        return ((ArrayList) this.channels.get(paramInt1)).get(paramInt2);
    }

    public long getChildId(int paramInt1, int paramInt2) {
        return paramInt1 * 1024 + paramInt2;
    }

    public View getChildView(int paramInt1, int paramInt2, boolean paramBoolean, View paramView, ViewGroup paramViewGroup) {
        Object localObject1 = (Film) getChild(paramInt1, paramInt2);
        if (paramView == null) {
            paramView = this.inflater.inflate(R.layout.vod_child_row, paramViewGroup, false);
        }
        TextView filmName = (TextView) paramView.findViewById(R.id.filmname);
        if (filmName != null) {
            filmName.setText(((Film) localObject1).getName());
        }
        TextView originalName = (TextView) paramView.findViewById(R.id.originalname);
        if (originalName != null) {
            originalName.setText(((Film) localObject1).getOrigin());
        }
        TextView year = (TextView) paramView.findViewById(R.id.year);
        if (year != null) {
            year.setText(String.valueOf(((Film) localObject1).getYear()));
        }

        try {
            ImageView logoView = (ImageView) paramView.findViewById(R.id.imageView1);
            String logoUrl = this.api.getApplicationPathVod() + "/" + ((Film) localObject1).getLogo();
            logoView.setImageDrawable(grabImageFromUrl(logoUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Point screenSize = new Point();
        WindowManager localWindowManager = (WindowManager) VideoStreamApplication.getInstance()
                .getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = localWindowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 11) {
            display.getSize(screenSize);
        } else {
            screenSize.set(display.getWidth(), display.getHeight());
        }
        ViewGroup.LayoutParams params = paramViewGroup.getLayoutParams();
        params.width = ((int) (screenSize.x * 0.2D));
        params.height = ((int) (screenSize.y * 0.2D));
        return paramView;
    }

    public int getChildrenCount(int paramInt) {
        return ((ArrayList) this.channels.get(paramInt)).size();
    }

    public Object getGroup(int paramInt) {
        return this.groups.get(paramInt);
    }

    public int getGroupCount() {
        return this.groups.size();
    }

    public long getGroupId(int paramInt) {
        return paramInt * 1024;
    }

    public View getGroupView(int paramInt, boolean paramBoolean, View paramView, ViewGroup paramViewGroup) {
        if (paramView == null) {
            paramView = this.inflater.inflate(R.layout.vod_group_row, paramViewGroup, false);
        }
        TextView localTextView = (TextView) paramView.findViewById(R.id.childname);
        if (localTextView != null) {
            localTextView.setText((String) getGroup(paramInt));
        }
        return paramView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int paramInt1, int paramInt2) {
        return true;
    }

    public void onGroupCollapsed(int paramInt) {
    }

    public void onGroupExpanded(int paramInt) {
    }
}
