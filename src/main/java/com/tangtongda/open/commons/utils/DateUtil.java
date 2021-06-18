package com.tangtongda.open.commons.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * java.util.Date Util
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2021/3/31
 */
public class DateUtil {
  private static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

  private DateUtil() {}

  /**
   * get the start datetime of day
   *
   * @param date the datetime param
   * @return start time string
   */
  public static String getStartOfDay(Date date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(NORM_DATETIME_PATTERN);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return simpleDateFormat.format(calendar.getTime());
  }

  /**
   * get the end datetime of day
   *
   * @param date the datetime param
   * @return end time string
   */
  public static String getEndOfDay(Date date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(NORM_DATETIME_PATTERN);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return simpleDateFormat.format(calendar.getTime());
  }

  /**
   * get the start datetime of day
   *
   * @param date the datetime param
   * @return start time
   */
  public static Date getStartDateOfDay(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  /**
   * get the end datetime of day
   *
   * @param date the datetime param
   * @return end time
   */
  public static Date getEndDateOfDay(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return calendar.getTime();
  }

  /**
   * get the start datetime of month
   *
   * @param date the datetime param
   * @return start time string
   */
  public static String getStartOfMonth(Date date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(NORM_DATETIME_PATTERN);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return simpleDateFormat.format(calendar.getTime());
  }

  /**
   * get the end datetime of month
   *
   * @param date the datetime param
   * @return end time string
   */
  public static String getEndOfMonth(Date date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(NORM_DATETIME_PATTERN);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return simpleDateFormat.format(calendar.getTime());
  }

  /**
   * get the start datetime of month
   *
   * @param date the datetime param
   * @return start time
   */
  public static Date getStartDateOfMonth(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  /**
   * get the end datetime of month
   *
   * @param date the datetime param
   * @return end time
   */
  public static Date getEndDateOfMonth(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  /**
   * get the start datetime of year
   *
   * @param date the datetime param
   * @return start time string
   */
  public static String getStartOfYear(Date date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(NORM_DATETIME_PATTERN);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.MONTH, calendar.getMinimum(Calendar.MONTH));
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return simpleDateFormat.format(calendar.getTime());
  }

  /**
   * get the end datetime of year
   *
   * @param date the datetime param
   * @return end time string
   */
  public static String getEndOfYear(Date date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(NORM_DATETIME_PATTERN);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.MONTH, calendar.getMaximum(Calendar.MONTH));
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return simpleDateFormat.format(calendar.getTime());
  }

  /**
   * get the start datetime of year
   *
   * @param date the datetime param
   * @return start time
   */
  public static Date getStartDateOfYear(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.MONTH, calendar.getMinimum(Calendar.MONTH));
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  /**
   * get the end datetime of year
   *
   * @param date the datetime param
   * @return end time
   */
  public static Date getEndDateOfYear(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.MONTH, calendar.getMaximum(Calendar.MONTH));
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  /**
   * calculate days between two datetime
   *
   * @param startTime start time
   * @param endTime end time
   * @return D-value: days
   */
  public static double calculateDays(Date startTime, Date endTime) {
    BigDecimal bigDecimal =
        BigDecimal.valueOf((endTime.getTime() - startTime.getTime()) / 86400000L);
    return bigDecimal.setScale(2, BigDecimal.ROUND_UP).doubleValue();
  }
}
