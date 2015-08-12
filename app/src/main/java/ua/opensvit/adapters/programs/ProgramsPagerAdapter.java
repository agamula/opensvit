package ua.opensvit.adapters.programs;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.api.OpenWorldApi1;
import ua.opensvit.data.GetUrlItem;
import ua.opensvit.data.ParcelableArray;
import ua.opensvit.data.epg.ProgramItem;
import ua.opensvit.fragments.ProgramsFragment;

public class ProgramsPagerAdapter extends PagerAdapter {

    private final SparseArray<ParcelableArray<ProgramItem>> programs;
    private final List<String> mDayNames;
    private final SparseArray<List<GetUrlItem>> mGetUrls;
    private final WeakReference<ProgramsFragment> weakFragment;
    private final int mChannelId;

    public ProgramsPagerAdapter(SparseArray<ParcelableArray<ProgramItem>> programs,
                                List<String> mDayNames, ProgramsFragment fragment, int channelId) {
        this.programs = programs;
        this.mDayNames = mDayNames;
        this.weakFragment = new WeakReference<>(fragment);
        this.mGetUrls = new SparseArray<>(programs.size());
        this.mChannelId = channelId;
    }

    @Override
    public int getCount() {
        return programs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        if (weakFragment.get() == null || weakFragment.get().getActivity() == null) {
            return null;
        }

        final ProgramsFragment fragment = weakFragment.get();

        final View itemView = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.layout_pager_item,
                container, false);

        final ListView mProgramsList = (ListView) itemView.findViewById(R.id.programs_list);
        if (mGetUrls.indexOfKey(programs.keyAt(position)) < 0) {
            new AsyncTask<Void, Void, Void>() {

                private OpenWorldApi1 mApi;
                private ParcelableArray<ProgramItem> mPrograms;
                private List<GetUrlItem> mUrls;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mPrograms = programs.valueAt(position);
                    int size = mPrograms.size();
                    mUrls = new ArrayList<>(size);
                    mGetUrls.put(programs.keyAt(position), mUrls);
                    for (int i = 0; i < size; i++) {
                        mUrls.add(null);
                    }
                    mApi = VideoStreamApp.getInstance().getApi1();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    for (int i = 0; i < mPrograms.size(); i++) {
                        final int position = i;
                        mApi.macGetArchiveUrlRunnable(mChannelId, mPrograms.get(i).getTimestamp()
                                , new OpenWorldApi1.ResultListener() {
                            @Override
                            public void onResult(Object res) {
                                if (res != null) {
                                    mUrls.set(position, (GetUrlItem) res);
                                }
                            }

                            @Override
                            public void onError(String result) {

                            }
                        }).run();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    setAdapter(fragment, itemView, mProgramsList, position);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else {
            setAdapter(fragment, itemView, mProgramsList, position);
        }

        container.addView(itemView);
        return itemView;
    }

    private void setAdapter(final ProgramsFragment fragment, final View mitemView, ListView
            mPrograms, final int position) {
        /*

        ParcelableArray<ProgramItem> programsSparse = programs.valueAt(0);
        final List<GetUrlItem> mUrls = mGetUrls.valueAt(0);

        ExecutorService service = Executors.newSingleThreadExecutor();

        / *service.execute(new Runnable() {
            @Override
            public void run() {
                new CopyHttpTask((SeekBar) mitemView.findViewById(R.id.seekbar), mUrls.get(11)
                        .getUrl())
                        .execute();
            }
        });*/

        if(position == getCount() - 1) {
            fragment.getLoadAdapterProgress().setVisibility(View.GONE);
        }

        int key = programs.keyAt(position);

        mPrograms.setAdapter(new ProgramsListAdapter(fragment.getActivity(), programs.get(key).toList
                (), mGetUrls.get(key), position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mDayNames.get(position);
    }
}
