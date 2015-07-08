package ua.opensvit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

public class EpgListApapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<ArrayList<EpgItem>> epgItems;
    private ArrayList<String> groups;
    private LayoutInflater inflater;

    public EpgListApapter(Context paramContext, ArrayList<String> paramArrayList, ArrayList<ArrayList<EpgItem>> paramArrayList1) {
        this.context = paramContext;
        this.groups = paramArrayList;
        this.epgItems = paramArrayList1;
        this.inflater = LayoutInflater.from(paramContext);
    }

    public Object getChild(int paramInt1, int paramInt2) {
        return ((ArrayList) this.epgItems.get(paramInt1)).get(paramInt2);
    }

    public long getChildId(int paramInt1, int paramInt2) {
        this.context.isRestricted();
        return paramInt1 * 10024 + paramInt2;
    }

    public View getChildView(int paramInt1, int paramInt2, boolean paramBoolean, View paramView, ViewGroup paramViewGroup) {
        final EpgItem localEpgItem = (EpgItem) getChild(paramInt1, paramInt2);
        if (paramView == null) {
            paramView = this.inflater.inflate(R.layout.epg_child_row, paramViewGroup, false);
        }
        TextView childTitleTextView = (TextView) paramView.findViewById(R.id.child_title);
        if (childTitleTextView != null) {
            childTitleTextView.setText(localEpgItem.getTitle());
        }
        TextView childTimeTextView = (TextView) paramView.findViewById(R.id.child_time);
        if (childTimeTextView != null) {
            childTimeTextView.setText(localEpgItem.getTime());
        }

        ImageView playImageView = (ImageView) paramView.findViewById(R.id.imageView_play);
        if (playImageView != null) {
            playImageView.setImageResource(R.drawable.ic_null_point);
            if (localEpgItem.getIsPlayNow()) {
                playImageView.setImageResource(R.drawable.ic_play);
            } else if (localEpgItem.getIsArchive()) {
                playImageView.setImageResource(R.drawable.ic_archive_icon);
            }
        }
        paramView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (localEpgItem.getIsArchive()) {
                    System.out.println("isArchive");
                    try {
                        String archiveUrl = VideoStreamApplication.getInstance().getDbApi().GetArchiveUrl
                                (Integer.valueOf(VideoStreamApplication.getInstance().getChId()), Integer.valueOf(localEpgItem.getTimestamp()));
                        if (archiveUrl.contains("error")) {
                            Toast.makeText(VideoStreamApplication.getInstance().getUserPage(), "No " +
                                    "entry in the archive", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent localIntent = new Intent();
                        localIntent.setClass(VideoStreamApplication.getInstance().getUserPage(), VideoViewPlayer.class);
                        localIntent.putExtra("ch_path", archiveUrl);
                        localIntent.putExtra("ch_name", localEpgItem.getTitle());
                        localIntent.putExtra("ch_id", VideoStreamApplication.getInstance().getChId());
                        localIntent.putExtra("type", 2);
                        localIntent.putExtra("service_id", VideoStreamApplication.getInstance().getUserPage().iptvServiceId);
                        localIntent.putExtra("timestamp", localEpgItem.getTimestamp());
                        VideoStreamApplication.getInstance().getUserPage().startActivity(localIntent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return paramView;
    }

    public int getChildrenCount(int paramInt) {
        return ((ArrayList) this.epgItems.get(paramInt)).size();
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
        String groupName = (String) getGroup(paramInt);
        TextView localTextView = (TextView) paramView.findViewById(R.id.childname);
        if (paramViewGroup != null) {
            localTextView.setText(groupName);
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
