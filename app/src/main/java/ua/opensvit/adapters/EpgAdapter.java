package ua.opensvit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ua.opensvit.R;
import ua.opensvit.data.epg.EpgItem;
import ua.opensvit.data.epg.ProgramItem;

public class EpgAdapter extends BaseAdapter{

    private final EpgItem epgItem;
    private final Context context;
    private final LayoutInflater inflater;

    public EpgAdapter(EpgItem epgItem, Context context) {
        this.epgItem = epgItem;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 1 + epgItem.getUnmodifiablePrograms().size();
    }

    @Override
    public Object getItem(int position) {
        return position == 0 ? context.getString(R.string.play_online) : epgItem
                .getUnmodifiablePrograms().get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static boolean calculatePlayOnlineView(int position) {
        return position == 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean isPlayOnlineView = calculatePlayOnlineView(position);
        if(convertView == null) {
            switch (position) {
                case 0:
                    convertView = inflater.inflate(R.layout.play_now_item, parent, false);
                    break;
                default:
                    convertView = inflater.inflate(R.layout.program_item, parent, false);
                    break;
            }
            convertView.setTag(isPlayOnlineView);
        } else {
            boolean wasPlayOnlineView = (boolean) convertView.getTag();
            if(wasPlayOnlineView != isPlayOnlineView) {
                switch (position) {
                    case 0:
                        convertView = inflater.inflate(R.layout.play_now_item, parent, false);
                        break;
                    default:
                        convertView = inflater.inflate(R.layout.program_item, parent, false);
                        break;
                }
                convertView.setTag(isPlayOnlineView);
            }
        }

        if(isPlayOnlineView) {
            ((TextView)convertView.findViewById(R.id.play_now)).setText(getItem(position).toString());
        } else {
            ProgramItem programItem = (ProgramItem) getItem(position);
            ((TextView)convertView.findViewById(R.id.program_time)).setText(programItem.getTime());
            ((TextView)convertView.findViewById(R.id.program_name)).setText(programItem.getTitle());
        }

        return convertView;
    }
}
