package ua.opensvit.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.IOException;
import java.util.List;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.api.OpenWorldApi1;
import ua.opensvit.data.channels.Channel;
import ua.opensvit.utils.ApiUtils;
import ua.opensvit.utils.ImageLoaderUtils;

@SuppressLint({"NewApi"})
public class ChannelListAdapter extends BaseExpandableListAdapter implements OpenWorldApi1.ResultListener {
    private OpenWorldApi1 api;
    private List<List<Channel>> channels;
    private Context context;
    private List<String> groups;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    private int mSelectedGroup;
    private int mSelectedChild;

    public ChannelListAdapter(List<String> groups,
                              List<List<Channel>> channels, OpenWorldApi1 api, Context context) {
        this.groups = groups;
        this.channels = channels;
        this.api = api;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(android.R.drawable.screen_background_light_transparent)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .resetViewBeforeLoading(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .build();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public Object getChild(int groupPosition, int childPosition) {
        return channels.get(groupPosition).get(childPosition);
    }

    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View
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
            ImageLoader.getInstance().displayImage(ImageLoaderUtils.wrapUrlForImageLoader(ApiUtils
                    .getBaseUrl() + "/" + channel.getLogo()), imageView, options);
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
                try {
                    mSelectedGroup = groupPosition;
                    mSelectedChild = childPosition;
                    ChannelListAdapter.this.api.macToggleIpTvFavorites(null, channel.getId
                            (), ChannelListAdapter.this);
                } catch (IOException e) {
                    e.printStackTrace();
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

    private void fixFavoriteState(int groupPosition, int childPosition) {
        Channel selectedChannel = channels.get(groupPosition).get(childPosition);
        for (int i = 0; i < channels.size(); i++) {
            int selectedIndex = channels.get(i).indexOf(selectedChannel);
            if (groups.get(i).equals(context.getString(R.string.selected))) {
                if (selectedIndex != -1) {
                    channels.get(i).remove(selectedIndex);
                } else {
                    Channel channel = new Channel();
                    channel.set(selectedChannel);
                    channel.setFavorits(false);
                    channels.get(i).add(selectedChannel);
                }
            } else {
                if (selectedIndex != -1) {
                    Channel channel = channels.get(i).get(selectedIndex);
                    channel.setFavorits(!channel.isFavorits());
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onResult(Object res) {
        boolean mSuccess = (boolean) res;
        if (mSuccess) {
            fixFavoriteState(mSelectedGroup, mSelectedChild);
        }
    }

    @Override
    public void onError(String result) {
        Toast.makeText(VideoStreamApp.getInstance().getApplicationContext(), result, Toast
                .LENGTH_SHORT).show();
    }
}
