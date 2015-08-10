package ua.opensvit.adapters.programs;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SeekBar;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.SeekableByteChannel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.ZipInputStream;

import io.vov.vitamio.MediaMetadataRetriever;
import io.vov.vitamio.ThumbnailUtils;
import io.vov.vitamio.provider.MediaStore;
import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.api.OpenWorldApi1;
import ua.opensvit.data.GetUrlItem;
import ua.opensvit.data.ParcelableArray;
import ua.opensvit.data.epg.ProgramItem;
import ua.opensvit.fragments.ProgramsFragment;
import ua.opensvit.http.CopyHttpTask;

public class ProgramsPagerAdapter extends PagerAdapter {

    private final SparseArray<ParcelableArray<ProgramItem>> programs;
    private final List<String> mDayNames;
    private final SparseArray<List<GetUrlItem>> mGetUrls;
    private final Activity mActivity;
    private final int mChannelId;

    public ProgramsPagerAdapter(SparseArray<ParcelableArray<ProgramItem>> programs,
                                List<String> mDayNames, Activity mActivity, int channelId) {
        this.programs = programs;
        this.mDayNames = mDayNames;
        this.mActivity = mActivity;
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
        final View itemView = LayoutInflater.from(mActivity).inflate(R.layout.layout_pager_item,
                container, false);

        final ListView mProgramsList = (ListView) itemView.findViewById(R.id.program_list);
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
                    mActivity.findViewById(R.id.progress).setVisibility(View.VISIBLE);
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
                    mActivity.findViewById(R.id.progress).setVisibility(View.GONE);
                    setAdapter(itemView, mProgramsList, position);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else {
            setAdapter(itemView, mProgramsList, position);
        }

        container.addView(itemView);
        return itemView;
    }

    private void setAdapter(final View mitemView, ListView mPrograms, final int position) {
        if (position != 0) {
            return;
        }

        ParcelableArray<ProgramItem> programsSparse = programs.valueAt(0);
        final List<GetUrlItem> mUrls = mGetUrls.valueAt(0);

        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(new Runnable() {
            @Override
            public void run() {
                File f = Environment.getExternalStorageDirectory();
                String sanDiegoExtSt = new File(f, "San_Diego_Clip.ts").getAbsolutePath();
                Bitmap b = ThumbnailUtils.createVideoThumbnail(mActivity.getApplication(),
                        mUrls.get(3).getUrl(), MediaStore.Video.Thumbnails.MINI_KIND);
                SystemClock.sleep(5000);
            }
        });

        service.execute(new Runnable() {
            @Override
            public void run() {
                new CopyHttpTask((SeekBar) mitemView.findViewById(R.id.seekbar), mUrls.get(3).getUrl())
                        .execute();
            }
        });

        //mPograms.setAdapter(new ProgramsListAdapter(mActivity, programs.valueAt(position).toList
        //        (), mGetUrls.valueAt(position)));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mDayNames.get(position);
    }
}
