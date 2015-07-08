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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;

import ua.levtv.library.LevtvDbApi;

@SuppressLint({"NewApi"})
public class ChannelListAdapter extends BaseExpandableListAdapter {
    private LevtvDbApi api;
    private ArrayList<ArrayList<Channel>> channels;
    private Context context;
    private ArrayList<String> groups;
    private LayoutInflater inflater;

    public ChannelListAdapter(Context paramContext, ArrayList<String> paramArrayList, ArrayList<ArrayList<Channel>> paramArrayList1, LevtvDbApi paramLevtvDbApi) {
        this.context = paramContext;
        this.groups = paramArrayList;
        this.channels = paramArrayList1;
        this.api = paramLevtvDbApi;
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

    public View getChildView(int paramInt1, int paramInt2, boolean paramBoolean, View paramView, final ViewGroup paramViewGroup) {
        final Channel localChannel = (Channel) getChild(paramInt1, paramInt2);
        if (paramView == null) {
            paramView = this.inflater.inflate(R.layout.child_row, paramViewGroup, false);
        }
        int screenWidth, screenHeight;
        WindowManager localWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        String imageUrl;
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
        imageUrl = this.api.getApplicationPath() + "/" + localChannel.logo;


        for (; ; ) {
            ((FrameLayout) paramView.findViewById(R.id.frame1)).setBackgroundColor(-1);
            ImageView imageView = (ImageView) paramView.findViewById(R.id.imageView1);

            try {
                imageView.setImageDrawable(grabImageFromUrl(imageUrl));
                imageView.getLayoutParams().height = ((int) (screenHeight * 0.07D));
                imageView.getLayoutParams().width = ((int) (screenWidth * 0.07D));
                TextView textView = (TextView) paramView.findViewById(R.id.childname);
                if (textView != null) {
                    textView.setText(localChannel.getName());
                }
                final ImageView imageView2 = (ImageView) paramView.findViewById(R.id.imageView2);
                if (!localChannel.getFavorits()) {
                    imageView2.setImageResource(R.drawable.ic_star_false);
                    imageView2.getLayoutParams().height = ((int) (screenWidth * 0.07D));
                    imageView2.getLayoutParams().width = ((int) (screenHeight * 0.07D));
                    ((FrameLayout) paramView.findViewById(R.id.frame2)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View paramAnonymousView) {
                            boolean isFavorite = false;
                            try {
                                ChannelListAdapter.this.api.ToggleIptvFavorites(Integer.valueOf(localChannel.getId()));
                                if (localChannel.getFavorits()) {
                                    isFavorite = true;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            imageView2.setImageResource(isFavorite ? R.drawable.ic_star : R
                                    .drawable.ic_star_false);
                            localChannel.setFavorits(isFavorite);
                        }
                    });
                    return paramView;
                }
            } catch (Exception e) {
                e.printStackTrace();
                imageView.setImageResource(R.drawable.ic_star);
            }
        }
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
            paramView = this.inflater.inflate(R.layout.group_row, paramViewGroup, false);
        }
        String groupText = (String) getGroup(paramInt);
        TextView localTextView = (TextView) paramView.findViewById(R.id.childname);
        if (paramViewGroup != null) {
            localTextView.setText(groupText);
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
