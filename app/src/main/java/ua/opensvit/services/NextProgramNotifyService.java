package ua.opensvit.services;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import ua.opensvit.VideoStreamApp;
import ua.opensvit.data.osd.OsdItem;
import ua.opensvit.data.osd.ProgramItem;
import ua.opensvit.http.OkHttpClientRunnable;
import ua.opensvit.utils.DateUtils;

public class NextProgramNotifyService extends IntentService implements OkHttpClientRunnable.OnLoadResultListener {

    public static final String CHANNEL_ID = "channelId";
    public static final String SERVICE_ID = "serviceId";
    public static final String TIMESTAMP = "timestamp";
    public static final String BROADCAST_NAME = "next_program_broadcast";
    public static final String PARAM_TIME_TILL = "param_time_till_next";
    public static final String PARAM_NEXT_PROGRAM_NAME = "param_next_program_name";
    public static final String PARAM_TILL_AFTER_TILL_END = "param_after_till";
    public static final String PARAM_NOW_TIME = "param_now_time";

    public NextProgramNotifyService() {
        super("Notify Next Program Service");
    }

    private VideoStreamApp mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = VideoStreamApp.getInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int channelId = intent.getIntExtra(CHANNEL_ID, 0);
        int serviceId = intent.getIntExtra(SERVICE_ID, 0);
        long timestamp = intent.getLongExtra(TIMESTAMP, 0);

        if (mApp.isFirstNotOnline()) {
            timestamp += TimeUnit.MINUTES.toSeconds(1);
            mApp.setFirstNotOnline(false);
        } else {
            Calendar calendar = Calendar.getInstance(DateUtils.getTimeZone());
            long diff = TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis() - intent
                    .getLongExtra(PARAM_NOW_TIME, 0));
            timestamp += diff;
        }

        OkHttpClientRunnable runnable = mApp.getApi1().macGetChannelOsd(channelId, serviceId,
                timestamp);
        runnable.setOnLoadResultListener(this);
        runnable.run();
    }

    public enum Till {
        Till, EndedAfter, EndedAs;
    }

    @Override
    public void onLoadResult(boolean isSuccess, String result) {
        if (isSuccess) {
            OsdItem res = new OsdItem();
            try {
                JSONObject localJSONObject = new JSONObject(result);
                JSONArray programsArr = localJSONObject.getJSONArray(ua.opensvit.data.osd
                        .ProgramItem.JSON_NAME);
                for (int i = 0; i < programsArr.length(); i++) {
                    JSONObject programObj = programsArr.getJSONObject(i);
                    ua.opensvit.data.osd.ProgramItem programItem = new ua.opensvit.data.osd
                            .ProgramItem();
                    programItem.setAbsTimeElapsedInPercent(programObj.getInt(ua.opensvit.data.osd
                            .ProgramItem.DURATION));
                    programItem.setTitle(programObj.getString(ua.opensvit.data.osd
                            .ProgramItem.TITLE));
                    programItem.setStart(programObj.getString(ua.opensvit.data.osd
                            .ProgramItem.START));
                    programItem.setEnd(programObj.getString(ua.opensvit.data.osd
                            .ProgramItem.END));
                    res.addProgram(programItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                res = null;
            }

            if (res != null) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                format.setTimeZone(DateUtils.getTimeZone());
                List<ProgramItem> programItems = res.getUnmodifiablePrograms();

                if (programItems != null && !programItems.isEmpty()) {
                    String timeTill = null;
                    String nextProgramName = null;
                    Till till = null;

                    ProgramItem programItem = programItems.get(0);
                    try {
                        Date startDate = format.parse(programItem.getStart());
                        Date endDate = format.parse(programItem.getEnd());
                        if (programItem.getAbsTimeElapsedInPercent() >= 100) {
                            if (programItems.size() > 1) {
                                programItem = programItems.get(1);
                                startDate = format.parse(programItem.getStart());
                                Date curDate = new Date((long) (startDate.getTime() + (endDate
                                        .getTime() - startDate.getTime()) * (programItem
                                        .getAbsTimeElapsedInPercent()) / 100f));
                                nextProgramName = programItem.getTitle();
                                long diffTime = endDate.getTime() - curDate.getTime();
                                timeTill = createTimeText(diffTime);
                                till = Till.EndedAfter;
                            } else {
                                Date curDate = new Date((long) (startDate.getTime() + (endDate
                                        .getTime() - startDate.getTime()) * (programItem
                                        .getAbsTimeElapsedInPercent()) / 100f));
                                nextProgramName = programItem.getTitle();
                                long diffTime = curDate.getTime() - endDate.getTime();
                                timeTill = createTimeText(diffTime);
                                till = Till.EndedAs;
                            }
                        } else {
                            Date curDate = new Date((long) (startDate.getTime() + (endDate
                                    .getTime() - startDate.getTime()) * (programItem
                                    .getAbsTimeElapsedInPercent()) / 100f));
                            if (programItems.size() > 1) {
                                programItem = programItems.get(1);
                                startDate = format.parse(programItem.getStart());
                                long diffTime = startDate.getTime() - curDate.getTime();
                                timeTill = createTimeText(diffTime);
                                nextProgramName = programItem.getTitle();
                                till = Till.Till;
                            } else {
                                nextProgramName = programItem.getTitle();
                                long diffTime = endDate.getTime() - curDate.getTime();
                                timeTill = createTimeText(diffTime);
                                till = Till.EndedAfter;
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        till = null;
                    }

                    if (till != null) {
                        Intent intent = new Intent(BROADCAST_NAME);
                        intent.putExtra(PARAM_TIME_TILL, timeTill);
                        intent.putExtra(PARAM_NEXT_PROGRAM_NAME, nextProgramName);
                        intent.putExtra(PARAM_TILL_AFTER_TILL_END, till.ordinal());
                        mApp.getApplicationContext().sendBroadcast(intent);
                    }
                }
            }
        }
    }

    private String createTimeText(long diffTime) {
        long hours = TimeUnit.MILLISECONDS.toHours(diffTime);
        diffTime -= TimeUnit.HOURS.toMillis(hours);
        return String.format("%02d h %02d m", hours, TimeUnit
                .MILLISECONDS.toMinutes(diffTime));
    }
}
