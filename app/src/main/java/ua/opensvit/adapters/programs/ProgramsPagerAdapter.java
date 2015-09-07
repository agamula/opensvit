package ua.opensvit.adapters.programs;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.vov.vitamio.MediaMetadataRetriever;
import ua.opensvit.R;
import ua.opensvit.VideoStreamApp;
import ua.opensvit.api.OpenWorldApi1;
import ua.opensvit.data.GetUrlItem;
import ua.opensvit.data.ParcelableArray;
import ua.opensvit.data.epg.ProgramItem;
import ua.opensvit.fragments.ProgramsFragment;
import ua.opensvit.fragments.player.VitamioVideoBaseFragment;

public class ProgramsPagerAdapter extends PagerAdapter implements AdapterView.OnItemClickListener {

    private final SparseArray<ParcelableArray<ProgramItem>> programs;
    private final List<String> mDayNames;
    private final SparseArray<List<GetUrlItem>> mGetUrls;
    private final WeakReference<ProgramsFragment> weakFragment;
    private final int mChannelId;
    private final GetUrlItem mFirstProgramUrl, mSecondUrl;
    private final ProgramItem mFirstProgramItem;

    public ProgramsPagerAdapter(SparseArray<ParcelableArray<ProgramItem>> programs,
                                List<String> mDayNames, ProgramsFragment fragment, int channelId,
                                ProgramItem mFirstProgramItem, GetUrlItem mFirstProgramUrl,
                                GetUrlItem mSecondUrl) {
        this.programs = programs;
        this.mDayNames = mDayNames;
        this.weakFragment = new WeakReference<>(fragment);
        this.mGetUrls = new SparseArray<>(programs.size());
        this.mChannelId = channelId;
        this.mFirstProgramItem = mFirstProgramItem;
        this.mFirstProgramUrl = mFirstProgramUrl;
        this.mSecondUrl = mSecondUrl;
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
        if (fragment != null) {

            final View itemView = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.layout_pager_item,
                    container, false);

            final ListView mProgramsList = (ListView) itemView.findViewById(R.id.programs_list);
            if (false) {
            /*if (mGetUrls.indexOfKey(programs.keyAt(position)) < 0) {
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
*/
            } else {
                setAdapter(fragment, itemView, mProgramsList, position);
            }

            container.addView(itemView);
            return itemView;
        } else {
            return null;
        }
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

        if (position == getCount() - 1) {
            fragment.getLoadAdapterProgress().setVisibility(View.GONE);
        }

        final List<ProgramItem> programItems = programs.valueAt(position).toList();
        /*MediaMetadataRetriever mRetriever = weakFragment.get().getRetriever();
        String duration = mRetriever.extractMetadata(MediaMetadataRetriever
                .METADATA_KEY_DURATION);
        duration = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SERVICE_PROVIDER);
        duration = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_FILENAME);
        duration = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SERVICE_NAME);
        duration = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TRACK);
        duration = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_CODEC);
        duration = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS);
        duration = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);

        final Bitmap bm;
        List<Bitmap> bitmaps = new ArrayList<>(2);

        long microsecondsInSeconds = TimeUnit.SECONDS.toMicros(1);

        if (curTime == 0) {
            curTime = SystemClock.uptimeMillis();
            bm = mRetriever.getFrameAtTime(-10 * microsecondsInSeconds);
            bitmaps.add(mRetriever.getFrameAtTime(-15 * microsecondsInSeconds));
            bitmaps.add(mRetriever.getFrameAtTime(-5 * microsecondsInSeconds));
        } else {
            bm = mRetriever.getFrameAtTime(-1000 * (SystemClock.uptimeMillis() - curTime) - 10 *
                    microsecondsInSeconds);
        }
        try {
            String fileName = "file";
            if (count == 0) {

                for (int i = 0; i < bitmaps.size(); i++) {
                    Bitmap bm1 = bitmaps.get(i);
                    bm1.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File
                            (Environment.getExternalStorageDirectory(), fileName + i + ".jpg")));
                    bm1.recycle();
                }
                fileName = "" + count++;
            } else {
                fileName = "" + count++;
            }
            bm.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(Environment
                    .getExternalStorageDirectory(), fileName + ".jpg")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bm.recycle();
        System.gc();
*/

        mPrograms.setAdapter(new ProgramsListAdapter(fragment.getActivity(), programItems,
                mFirstProgramUrl, position, mFirstProgramItem));
        mPrograms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (weakFragment.get() != null) {
                    ProgramsFragment fragment = weakFragment.get();
                    fragment.setVideoPath(position % 2 == 0 ? mFirstProgramUrl.getUrl() :
                                    mSecondUrl.getUrl(), (programItems.get(position)
                            .getTimestamp() - mFirstProgramItem.getTimestamp()) * 1000);
                }
            }
        });
    }

    private static int count = 0;
    private static long curTime = 0;

    @Override
    public CharSequence getPageTitle(int position) {
        return mDayNames.get(position);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
