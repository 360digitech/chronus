package com.qihoo.finance.chronus.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by xiongpu on 2017/6/27.
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    /**
     * yyyy-MM-dd
     *
     * @param formatTxt
     * @return
     */
    public static String getCurrentDateStr(String formatTxt) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern(formatTxt);
        String current = today.format(format);
        return current;
    }


    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentDateStrByYMDHMS() {
        return getCurrentDateStrByDef("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentDateStrByDef(String fmt) {
        fmt = StringUtils.isBlank(fmt) ? "yyyy-MM-dd HH:mm:ss" : fmt;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt);
        return simpleDateFormat.format(new Date());
    }


    public static Date now(){
        return new Date();
    }

    public static LocalDateTime getLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate getLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalTime getLocalTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    public static int getYear(Date date) {
        return getLocalDateTime(date).getYear();
    }

    public static int getMonth(Date date) {
        return getLocalDateTime(date).getMonthValue();
    }

    public static int getDayOfMonth(Date date) {
        return getLocalDateTime(date).getDayOfMonth();
    }

    public static int getDayOfYear(Date date) {
        return getLocalDateTime(date).getDayOfYear();
    }

    public static int getDayOfWeek(Date date) {
        return getLocalDateTime(date).getDayOfWeek().getValue();
    }

    public static int getHour(Date date) {
        return getLocalDateTime(date).getHour();
    }

    public static int getMinute(Date date) {
        return getLocalDateTime(date).getMinute();
    }

    public static int getSecond(Date date) {
        return getLocalDateTime(date).getSecond();
    }

    public static long getLong(Date date) {
        return date.getTime();
    }

    public static Date toDate(String dateStr) {
        return toDate(dateStr, "yyyy-MM-dd");
    }

    public static Date toDateWithTime(String dateTimeStr){
        return toDate(dateTimeStr, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date toDate(long date){
        return new Date(date);
    }

    public static Date toDate(String dateStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("日期转换异常！", e);
        }
    }

    public static String toDateText(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static String toDateText(Date date) {
        return toDateText(date, "yyyy-MM-dd");
    }

    public static String toDateTimeText(Date date) {
        return toDateText(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String longToDateText(Long longTypeDate){
        return toDateText(toDate(longTypeDate));
    }

    public static String longToDateTimeText(Long longTypeDate){
        return toDateTimeText(toDate(longTypeDate));
    }
}

