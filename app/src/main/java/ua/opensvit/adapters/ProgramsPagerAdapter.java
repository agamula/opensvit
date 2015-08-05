package ua.opensvit.adapters;

import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import ua.opensvit.data.ParcelableArray;
import ua.opensvit.data.epg.ProgramItem;

public class ProgramsPagerAdapter extends PagerAdapter {

    private final SparseArray<ParcelableArray<ProgramItem>> programs;

    public ProgramsPagerAdapter(SparseArray<ParcelableArray<ProgramItem>> programs) {
        this.programs = programs;
    }

    @Override
    public int getCount() {
        return programs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}
