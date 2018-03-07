package com.bing.lan.comm.date;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Author: yxhuang
 * Date: 2017/4/1
 * Email: yxhuang@gmail.com
 */

public class TimeUtil {

    public enum TimePattern {
        ALL("yyyy-MM-dd HH:mm:ss"), //年月日时分秒
        YEAR_MONTH_DAY("yyyy-MM-dd"), //年月日
        HOURS_MINS("HH:mm"), //时分
        MONTH_DAY_HOUR_MIN("MM-dd HH:mm"), //月日时分
        YEAR_MONTH("yyyy-MM"), //年月
        YEAR_MONTH_DAY_HOUR_MIN("yyyy-MM-dd HH:mm"); //年月日时分

        private final String pattern;

        TimePattern(String pattern) {
            this.pattern = pattern;
        }

        public String getTimePattern() {
            return pattern;
        }
    }

    /**
     * 转换成时间
     */
    public static String time(String originTime) {
        if (!TextUtils.isEmpty(originTime)) {
            return timeByPattern(Long.valueOf(originTime), TimePattern.ALL);
        }
        return "";
    }

    /**
     * 转换成时间
     */
    public static String LongToTime(long originTime) {
        if (originTime != 0L) {
            return timeByPattern(originTime, TimePattern.ALL);
        }
        return "";
    }

    /**
     * 转换成时间
     */
    public static String timeByPattern(Long originTime, TimePattern pattern) {
        if (originTime != 0L) {
            SimpleDateFormat format = new SimpleDateFormat(pattern.getTimePattern());
            return format.format(originTime);
        }
        return "";
    }

    /**
     * yyyy-MM-dd HH:mm:ss 转换 yyyy-MM-dd
     */
    public static String NoHour(String time) {
        Date d;
        String newTime = "";
        try {
            d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
            newTime = new SimpleDateFormat("yyyy-MM-dd").format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newTime;
    }

    /**
     * 获取当前日期的前七天
     */
    public static String[] getCurrentDayBeforeSevenDay(int dayNum) {
        String[] days = new String[dayNum];
        Calendar calendar = Calendar.getInstance();
        for (int i = dayNum - 1; i > -1; i--) {
            calendar.add(Calendar.DATE, -1);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);

            days[i] = month + "月" + day + "日";

            //PayLog.i("TimeUtil", "getCurrentDayBeforeSevenDay i " + i + "  " + days[i] );
        }

        return days;
    }
}
