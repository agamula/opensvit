package ua.opensvit.adapters;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;

import ua.opensvit.R;
import ua.opensvit.api.OpenWorldApi;
import ua.opensvit.data.iptv.channels.Channel;

@SuppressLint({"NewApi"})
public class ChannelListAdapter extends BaseExpandableListAdapter {
    private OpenWorldApi api;
    private ArrayList<ArrayList<Channel>> channels;
    private Context context;
    private ArrayList<String> groups;
    private LayoutInflater inflater;

    public ChannelListAdapter(Context paramContext, ArrayList<String> groups,
                              ArrayList<ArrayList<Channel>> channels, OpenWorldApi api) {
        this.context = paramContext;
        this.groups = groups;
        this.channels = channels;
        this.api = api;
        this.inflater = LayoutInflater.from(paramContext);
    }

    public Object getChild(int groupPosition, int childPosition) {
        return channels.get(groupPosition).get(childPosition);
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View
            convertView, final ViewGroup parent) {
        final Channel channel = (Channel) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.child_row, parent, false);
        }
        int screenWidth, screenHeight;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= 11) {
            Point p = new Point();
            windowManager.getDefaultDisplay().getSize(p);
            screenWidth = p.x;
            screenHeight = p.y;
        } else {
            Display display = windowManager.getDefaultDisplay();
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);

        try {
            //String imageUrl = this.api.getApiPath() + "/" + localChannel.getLogo();
            //imageView.setImageDrawable(grabImageFromUrl(imageUrl));
        } catch (Exception e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.ic_star);
        }
        imageView.getLayoutParams().height = ((int) (screenHeight * 0.07D));
        imageView.getLayoutParams().width = ((int) (screenWidth * 0.07D));
        TextView channelTextView = (TextView) convertView.findViewById(R.id.childname);
        if (channelTextView != null) {
            channelTextView.setText(channel.getName());
        }
        final ImageView imageView2 = (ImageView) convertView.findViewById(R.id.imageView2);
        setImageFavoriteResource(imageView2, channel);
        imageView2.getLayoutParams().height = ((int) (screenWidth * 0.07D));
        imageView2.getLayoutParams().width = ((int) (screenHeight * 0.07D));
        convertView.findViewById(R.id.frame2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean success;
                try {
                    success = ChannelListAdapter.this.api.toggleIptvFavorites(channel.getId
                            ());
                    if(success) {
                        channel.setFavorits(!channel.isFavorits());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    success = false;
                }
                if(success) {
                    setImageFavoriteResource(imageView2, channel);
                }
            }
        });

        return convertView;
    }

    private void setImageFavoriteResource(ImageView imageView, Channel channel) {
        imageView.setImageResource(channel.isFavorits() ? R.drawable.ic_star : R.drawable
                .ic_star_false);
    }

    public int getChildrenCount(int groupPosition) {
        return channels.get(groupPosition).size();
    }

    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    public int getGroupCount() {
        return groups.size();
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

    public boolean isChildSelectable(int groupPosition, int chilfPosition) {
        return true;
    }

    public void onGroupCollapsed(int groupPosition) {
    }

    public void onGroupExpanded(int groupPosition) {
    }

    public long getGroupId(int groupPosition) {
        return groupPosition * 1024;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return getGroupId(groupPosition) + childPosition;
    }

    private Drawable grabImageFromUrl(String paramString)
            throws Exception {
        return Drawable.createFromStream((InputStream) new URL(paramString).getContent(), "src");
    }
}
