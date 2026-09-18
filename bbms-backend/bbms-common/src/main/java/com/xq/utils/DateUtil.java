package com.xq.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期时间工具类
 */
public class DateUtil {

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * Date转LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(DEFAULT_ZONE).toLocalDateTime();
    }

    /**
     * LocalDateTime转Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(DEFAULT_ZONE).toInstant());
    }

    /**
     * 给Date增加天数
     */
    public static Date plusDays(Date date, long days) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = toLocalDateTime(date);
        localDateTime = localDateTime.plusDays(days);
        return toDate(localDateTime);
    }

    /**
     * 给Date减少天数
     */
    public static Date minusDays(Date date, long days) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = toLocalDateTime(date);
        localDateTime = localDateTime.minusDays(days);
        return toDate(localDateTime);
    }

    /**
     * 计算两个Date之间的天数差
     */
    public static long daysBetween(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }
        LocalDateTime startLdt = toLocalDateTime(start);
        LocalDateTime endLdt = toLocalDateTime(end);
        return java.time.temporal.ChronoUnit.DAYS.between(startLdt, endLdt);
    }

    /**
     * 计算两个Date之间的小时差
     */
    public static long hoursBetween(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }
        LocalDateTime startLdt = toLocalDateTime(start);
        LocalDateTime endLdt = toLocalDateTime(end);
        return ChronoUnit.HOURS.between(startLdt, endLdt);
    }
    /**
     * 给Date减少秒数
     */
    public static Date minusSeconds(Date date, long seconds) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = toLocalDateTime(date);
        localDateTime = localDateTime.minusSeconds(seconds);
        return toDate(localDateTime);
    }

    /**
     * 给Date增加秒数
     */
    public static Date plusSeconds(Date date, long seconds) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = toLocalDateTime(date);
        localDateTime = localDateTime.plusSeconds(seconds);
        return toDate(localDateTime);
    }

    /**
     * 给Date减少分钟数
     */
    public static Date minusMinutes(Date date, long minutes) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = toLocalDateTime(date);
        localDateTime = localDateTime.minusMinutes(minutes);
        return toDate(localDateTime);
    }

    /**
     * 给Date增加分钟数
     */
    public static Date plusMinutes(Date date, long minutes) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = toLocalDateTime(date);
        localDateTime = localDateTime.plusMinutes(minutes);
        return toDate(localDateTime);
    }

    /**
     * 获取当前时间
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 格式化Date为字符串
     */
    public static String format(Date date) {
        return format(date, DEFAULT_PATTERN);
    }

    /**
     * 格式化Date为字符串（指定格式）
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    /**
     * 解析字符串为Date
     */
    public static Date parse(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            return formatter.parse(dateStr);
        } catch (Exception e) {
            throw new RuntimeException("日期解析失败: " + dateStr, e);
        }
    }

    /**
     * 解析字符串为Date（默认格式）
     */
    public static Date parse(String dateStr) {
        return parse(dateStr, DEFAULT_PATTERN);
    }
}
