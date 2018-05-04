package com.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 时间处理工具类
 *
 */
public class DateUtil {
    private static final Log logger = LogFactory.getLog(DateUtil.class);
    private static final int MODIFY_TRUNCATE = 0;
    private static final int MODIFY_ROUND = 1;
    private static final int MODIFY_CEILING = 2;
    public static final int SEMI_MONTH = 1001;
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_TIMEZONE = "GMT+8";

    private static final int[][] fields = {
            {Calendar.MILLISECOND},
            {Calendar.SECOND},
            {Calendar.MINUTE},
            {Calendar.HOUR_OF_DAY, Calendar.HOUR},
            {Calendar.DATE, Calendar.DAY_OF_MONTH, Calendar.AM_PM
            /* Calendar.DAY_OF_YEAR, Calendar.DAY_OF_WEEK, Calendar.DAY_OF_WEEK_IN_MONTH */
            },
            {Calendar.MONTH, DateUtil.SEMI_MONTH},
            {Calendar.YEAR},
            {Calendar.ERA}};

    /**
     * 获取两个日期的时间差，单位：天
     *
     * @param dateStart
     * @param dateEnd
     * @return 天数
     */
    public static long getDayDiff(Date dateStart, Date dateEnd) {
        return (dateEnd.getTime() - dateStart.getTime()) / 86400000;
    }

    /**
     * 将日期转化为yyyy-MM-dd HH:mm:ss格式的字符串
     *
     * @param date
     * @return yyyy-MM-dd HH:mm:ss格式的字符串
     */
    public static String toDateTimeString(Date date) {
        return format(date, DATE_TIME_FORMAT, DATE_TIMEZONE);
    }

    /**
     * 将日期转化为yyyy-MM-dd格式的字符串
     *
     * @param date
     * @return yyyy-MM-dd格式的字符串
     */
    public static String toDateString(Date date) {
        return format(date, DATE_FORMAT, DATE_TIMEZONE);
    }

    /**
     * 将日期转化为HH:mm:ss格式的字符串
     *
     * @param date
     * @return HH:mm:ss格式的字符串
     */
    public static String toTimeString(Date date) {
        return format(date, TIME_FORMAT, DATE_TIMEZONE);
    }

    /**
     * 返回该日期的星期，eg:星期四
     *
     * @param date
     * @return eg:星期四
     */
    public static String toWeekDay(Date date) {
        return format(date, "EEEE");
    }

    /**
     * 将日期转化为指定格式的字符串，Timezone为GMT+8
     *
     * @param date
     * @param format
     * @return 指定格式的字符串
     */
    public static String format(Date date, String format) {
        return format(date, format, DATE_TIMEZONE);
    }

    /**
     * 将日期转化为指定格式的字符串
     *
     * @param date
     * @param format
     * @param timezone
     * @return
     */
    public static String format(Date date, String format, String timezone) {
        if (date == null) return null;
        DateFormat dateFormat = new SimpleDateFormat(format);

        if (StringUtils.isNotEmpty(timezone)) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        }

        return dateFormat.format(date);
    }


    /**
     * 将字符串转换为日期对象，format为yyyy-MM-dd，TimeZone为GMT+8
     *
     * @param dateString
     * @return
     */
    public static Date parseDay(String dateString) {
        return parse(dateString, DATE_FORMAT, DATE_TIMEZONE);
    }

    /**
     * 将字符串转换为日期对象，format为yyyy-MM-dd HH:mm:ss，TimeZone为GMT+8
     *
     * @param dateString
     * @return
     */
    public static Date parseDateTime(String dateString) {
        return parse(dateString, DATE_TIME_FORMAT, DATE_TIMEZONE);
    }

    /**
     * 将字符串转换为日期对象， TimeZone为GMT+8
     *
     * @param dateString
     * @param format
     * @return
     */
    public static Date parse(String dateString, String format) {
        return parse(dateString, format, DATE_TIMEZONE);
    }

    /**
     * 将字符串转换为日期对象
     *
     * @param dateString
     * @param format
     * @param timezone
     * @return
     */
    public static Date parse(String dateString, String format, String timezone) {
        if (StringUtils.isEmpty(dateString)) return null;

        DateFormat dateFormat = new SimpleDateFormat(format);
        if (StringUtils.isNotEmpty(timezone)) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        }
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException("date:" + dateString + ", format:" + format, e);
        }
    }

    /**
     * 判断是否为yyyy-MM-dd HH:mm:ss的字符串
     *
     * @param str
     * @return
     */
    public static boolean isDateTimeString(String str) {
        return isDateTime(str, DATE_TIME_FORMAT);
    }

    /**
     * 判断是否为yyyy-MM-dd的字符串
     *
     * @param str
     * @return
     */
    public static boolean isDayString(String str) {
        return isDateTime(str, DATE_FORMAT);
    }

    /**
     * 判断是否日期格式的字符串
     *
     * @param str 需要判断的日期字符串
     * @param format 时间格式
     * @return
     */
    private static boolean isDateTime(String str, String format) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(format)) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            sdf.parse(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 修改时间
     *
     * @param date
     * @param calendarField
     * @param amount
     * @return
     */
    public static Date addTime(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
		if (amount == 0) {
			return date;
		}
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * 日期增加毫秒数
     *
     * @param date
     * @param amount 毫秒数
     * @return
     */
    public static Date addMilliseconds(Date date, int amount) {
        return addTime(date, Calendar.MILLISECOND, amount);
    }

    /**
     * 日期增加秒
     *
     * @param date
     * @param amount 秒数
     * @return
     */
    public static Date addSeconds(Date date, int amount) {
        return addTime(date, Calendar.SECOND, amount);
    }

    /**
     * 日期增加分钟数
     *
     * @param date
     * @param amount 分钟数
     * @return
     */
    public static Date addMinutes(Date date, int amount) {
        return addTime(date, Calendar.MINUTE, amount);
    }

    /**
     * 日期增加小时数
     *
     * @param date
     * @param amount 小时数
     * @return
     */
    public static Date addHours(Date date, int amount) {
        return addTime(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * 日期增加天数
     *
     * @param date
     * @param amount 天数
     * @return
     */
    public static Date addDays(Date date, int amount) {
        return addTime(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * 日期增加星期数
     *
     * @param date
     * @param amount 星期数
     * @return
     */
    public static Date addWeeks(Date date, int amount) {
        return addTime(date, Calendar.WEEK_OF_YEAR, amount);
    }

    /**
     * 日期增加月份
     *
     * @param date
     * @param amount 月份数
     * @return
     */
    public static Date addMonths(Date date, int amount) {
        return addTime(date, Calendar.MONTH, amount);
    }

    /**
     * 日期增加年
     *
     * @param date
     * @param amount 年数
     * @return
     */
    public static Date addYears(Date date, int amount) {
        return addTime(date, Calendar.YEAR, amount);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
//        long subsec = cal1.getTimeInMillis() - cal2.getTimeInMillis();
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * 判断两日期是否是同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return isSameDay(cal1, cal2);
    }

    /**
     * 返回两个时间中的更大的时间
     *
     * @param date1
     * @param date2
     * @return Date
     */
    public static Date maxDate(Date date1, Date date2) {
		if (date1 == null && date2 != null) {
            return date2;
        } else if (date1 != null && date2 == null) {
            return date1;
		} else if (date1 != null && date2 != null) {
            return date1.before(date2) ? date2 : date1;
		} else {
			return null;
        }
    }

    private static void modify(Calendar val, int field, int modType) {
        if (val.get(Calendar.YEAR) > 280000000) {
            throw new ArithmeticException("Calendar value too large for accurate calculations");
        }

        if (field == Calendar.MILLISECOND) {
            return;
        }

        Date date = val.getTime();
        long time = date.getTime();
        boolean done = false;

        // truncate milliseconds
        int millisecs = val.get(Calendar.MILLISECOND);
        if (MODIFY_TRUNCATE == modType || millisecs < 500) {
            time = time - millisecs;
        }
        if (field == Calendar.SECOND) {
            done = true;
        }

        // truncate seconds
        int seconds = val.get(Calendar.SECOND);
        if (!done && (MODIFY_TRUNCATE == modType || seconds < 30)) {
            time = time - (seconds * 1000L);
        }
        if (field == Calendar.MINUTE) {
            done = true;
        }

        // truncate minutes
        int minutes = val.get(Calendar.MINUTE);
        if (!done && (MODIFY_TRUNCATE == modType || minutes < 30)) {
            time = time - (minutes * 60000L);
        }

        // reset time
        if (date.getTime() != time) {
            date.setTime(time);
            val.setTime(date);
        }
        // ----------------- Fix for LANG-59 ----------------------- END ----------------

        boolean roundUp = false;
        for (int[] aField : fields) {
            for (int element : aField) {
                if (element == field) {
                    //This is our field... we stop looping
                    if (modType == MODIFY_CEILING || (modType == MODIFY_ROUND && roundUp)) {
                        if (field == DateUtil.SEMI_MONTH) {
                            //This is a special case that's hard to generalize
                            //If the date is 1, we round up to 16, otherwise
                            //  we subtract 15 days and add 1 month
                            if (val.get(Calendar.DATE) == 1) {
                                val.add(Calendar.DATE, 15);
                            } else {
                                val.add(Calendar.DATE, -15);
                                val.add(Calendar.MONTH, 1);
                            }
// ----------------- Fix for LANG-440 ---------------------- START ---------------
                        } else if (field == Calendar.AM_PM) {
                            // This is a special case
                            // If the time is 0, we round up to 12, otherwise
                            //  we subtract 12 hours and add 1 day
                            if (val.get(Calendar.HOUR_OF_DAY) == 0) {
                                val.add(Calendar.HOUR_OF_DAY, 12);
                            } else {
                                val.add(Calendar.HOUR_OF_DAY, -12);
                                val.add(Calendar.DATE, 1);
                            }
// ----------------- Fix for LANG-440 ---------------------- END ---------------
                        } else {
                            //We need at add one to this field since the
                            //  last number causes us to round up
                            val.add(aField[0], 1);
                        }
                    }
                    return;
                }
            }
            //We have various fields that are not easy roundings
            int offset = 0;
            boolean offsetSet = false;
            //These are special types of fields that require different rounding rules
            switch (field) {
                case DateUtil.SEMI_MONTH:
                    if (aField[0] == Calendar.DATE) {
                        //If we're going to drop the DATE field's value,
                        //  we want to do this our own way.
                        //We need to subtrace 1 since the date has a minimum of 1
                        offset = val.get(Calendar.DATE) - 1;
                        //If we're above 15 days adjustment, that means we're in the
                        //  bottom half of the month and should stay accordingly.
                        if (offset >= 15) {
                            offset -= 15;
                        }
                        //Record whether we're in the top or bottom half of that range
                        roundUp = offset > 7;
                        offsetSet = true;
                    }
                    break;
                case Calendar.AM_PM:
                    if (aField[0] == Calendar.HOUR_OF_DAY) {
                        //If we're going to drop the HOUR field's value,
                        //  we want to do this our own way.
                        offset = val.get(Calendar.HOUR_OF_DAY);
                        if (offset >= 12) {
                            offset -= 12;
                        }
                        roundUp = offset >= 6;
                        offsetSet = true;
                    }
                    break;
            }
            if (!offsetSet) {
                int min = val.getActualMinimum(aField[0]);
                int max = val.getActualMaximum(aField[0]);
                //Calculate the offset from the minimum allowed value
                offset = val.get(aField[0]) - min;
                //Set roundUp if this is more than half way between the minimum and maximum
                roundUp = offset > ((max - min) / 2);
            }
            //We need to remove this field
            if (offset != 0) {
                val.set(aField[0], val.get(aField[0]) - offset);
            }
        }
        throw new IllegalArgumentException("The field " + field + " is not supported");

    }

    /**
     * 截取时间
     *
     * @param date  要截取的时间
     * @param field 截取的时间域  (Calendar.DAY_OF_MONTH,Calendar.HOUR ... )
     * @return 截取后的时间
     */
    public static Date truncate(Date date, int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, MODIFY_TRUNCATE);
        return gval.getTime();
    }

    /**
     * 截取时间后进行比较大小
     *
     * @param date1
     * @param date2
     * @param field 要截取的域 (Calendar.DAY_OF_MONTH,Calendar.HOUR ... )
     * @return 负整数(第1个日期比第2个早), 零(两日期相等), 正整数(第1个比第2个晚)
     */
    public static int truncatedCompareTo(Date date1, Date date2, int field) {
        Date truncatedDate1 = truncate(date1, field);
        Date truncatedDate2 = truncate(date2, field);
        return truncatedDate1.compareTo(truncatedDate2);
    }

    /**
     * 截取时间后比较两日期是否相等
     *
     * @param date1
     * @param date2
     * @param field 要截取的域 (Calendar.DAY_OF_MONTH,Calendar.HOUR ... )
     * @return true(截取后的两日期相等), false(截取后的两日期不相等)
     */
    public static boolean truncatedEquals(Date date1, Date date2, int field) {
        return truncatedCompareTo(date1, date2, field) == 0;
    }

    /**
     * 获取指定日期对应月的最后一天的日期,不clear时分秒
     *
     * @return eg:crt time: 2016-03-09 09:32:10 return: 2016-03-31 09:32:10
     */
    public static Date theLastDayInMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    /**
     * 获取date对应的月中第一天
     *
     * @param date
     * @return eg:date: 2016-03-09 09:32:10 return: 2016-03-01 00:00:00
     */
    public static Date firstDayInMonth(Date date) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(DATE_TIMEZONE));
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }
    
    /**
     * 获取date所在周的周一
     * @param date
     * @return
     */
	public static Date getMondayInWeek(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一

		int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

    /**
     * 取得系统时间前一天的日期,时分秒被clear为最初时间
     *
     * @return eg:crt time: 2016-03-09 09:32:10 return: 2016-03-08 00:00:00
     */
    public static Date lastDayOfCrtTime() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(DATE_TIMEZONE));
        c.add(Calendar.DAY_OF_MONTH, -1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    public static XMLGregorianCalendar getXMLGregorianCalendar(Date date) {
        if (date == null) return null;
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        XMLGregorianCalendar gc = null;
        try {
            gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (Exception e) {
            logger.error("", e);
        }
        return gc;
    }

    /**
     * 按天，小时，分钟对日期进行分割，返回分割后的时间数组
     *
     * @param startDate
     * @param endDate
     * @param amount
     * @param timeType
     * @return
     */
    public static Date[] splitDate(Date startDate, Date endDate, int amount, int timeType) {
        if (startDate == null || endDate == null) return new Date[0];
        if (startDate.after(endDate)) {
            Date temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        long seconds = amount;
        switch (timeType) {
            case Calendar.DAY_OF_MONTH:
                seconds = amount * 3600 * 24;
                break;
            case Calendar.HOUR:
                seconds = amount * 3600;
                break;
            case Calendar.MINUTE:
                seconds = amount * 60;
                break;
            case Calendar.SECOND:
                seconds = amount;
                break;
            default:
                seconds = amount * 3600 * 24;
                break;
        }
        long dis = (endDate.getTime() - startDate.getTime()) / 1000;
        long num = (dis + seconds - 1) / seconds;
        Date[] lRet = new Date[(int) (++num)];
        lRet[0] = startDate;

        for (int i = 1; i < lRet.length - 1; i++) {
            long temp = startDate.getTime() / 1000 + seconds * i;
            lRet[i] = new Date(temp * 1000);
        }
        lRet[lRet.length - 1] = endDate;
        return lRet;
    }

    /**
     * 取当前日期与2012-1-1的差
     * date yymmdd
     *
     * @return 天数
     */
    public static long getDay(String date) {
        Date now = parse(date, "yyMMdd");
        if (now == null) {
            now = Calendar.getInstance().getTime();
        } else {
            now.setSeconds(30);
        }
        Calendar c = Calendar.getInstance();
        c.set(2012, 0, 1, 0, 0, 0);

        return getDayDiff(c.getTime(), now);
    }

	/**
	 * 将java.util.Date转换为 Unix Timestamp
	 * 
	 * @param date
	 * @return
	 */
	public static long toUnixTimestamp(Date date) {
		return date == null ? 0L : date.getTime() / 1000;
	}

	/**
	 * 得到两个时间的差值
	 * @param beginTime 开始时间
	 * @param endTime  结束时间
	 * @param calendarField Calendar地fields
	 * @return
	 */
	public static long timeRange(Date beginTime, Date endTime, int calendarField) {
		if (endTime == null || beginTime == null) {
			return Long.MIN_VALUE;
		}
		long actPassTime = endTime.getTime() - beginTime.getTime();
		switch (calendarField) {
		case Calendar.MILLISECOND:
			break;
		case Calendar.SECOND:
			actPassTime /= 1000L;// 秒
			break;
		case Calendar.MINUTE:
			actPassTime /= 1000L * 60;// 秒
			break;
		case Calendar.HOUR:
			actPassTime /= 1000L * 60 * 60;// 秒
			break;
		case Calendar.DAY_OF_YEAR:
			actPassTime /= 1000L * 60 * 60 * 24;// 天
			break;
		default:
			throw new RuntimeException("Not Support Field:" + calendarField);
		}
		return actPassTime;
	}
	
	/**
	 * 将Unix Timestamp转换为java.util.Date
	 * 
	 * @param time
	 * @return
	 */
	public static Date fromUnixTimestamp(long time) {
		return new Date(time * 1000);
	}
    
    public static class DateIterator implements Iterator {
        private final Calendar endFinal;
        private final Calendar spot;
        private final int field;
        private final int range; //range 时间跨度

        /**
         * Constructs a DateIterator that ranges from one date to another.
         *
         * @param startFinal start date (inclusive)
         * @param endFinal   end date (inclusive)
         */
        public DateIterator(Calendar startFinal, Calendar endFinal) {
            this(startFinal, endFinal, Calendar.DATE, 1);
        }

        public DateIterator(Date startFinal, Date endFinal) {
            this(startFinal, endFinal, Calendar.DATE, 1);
        }

        public DateIterator(Calendar startFinal, Calendar endFinal, int calField, int range) {
            super();
            this.field = calField;
            this.range = range;
            this.endFinal = endFinal;
            spot = startFinal;
            spot.add(this.field, -range);
        }

        public DateIterator(Date startFinal, Date endFinal, int calField, int range) {
            super();
            this.field = calField;
            this.range = range;

            this.endFinal = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.DATE_TIMEZONE));
            this.endFinal.setTime(endFinal);

            this.spot = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.DATE_TIMEZONE));
            this.spot.setTime(startFinal);

            this.spot.add(this.field, -range);
        }

        /**
         * Has the iterator not reached the end date yet?
         *
         * @return <code>true</code> if the iterator has yet to reach the end date
         */
        @Override
		public boolean hasNext() {
            return spot.before(endFinal);
        }

        /**
         * Return the next calendar in the iteration
         *
         * @return Object calendar for the next date
         */
        @Override
		public Object next() {
            if (spot.after(endFinal)) {
                throw new NoSuchElementException();
            }
            spot.add(this.field, this.range);

            return spot.before(endFinal) ? spot.clone() : endFinal.clone();
        }

        /**
         * 返回下一个时间，java.util.Data
         *
         * @return 下一个时间，java.util.Data
         */
        public Date nextTime() {
            return ((Calendar) next()).getTime();
        }

        /**
         * Always throws UnsupportedOperationException.
         *
         * @throws UnsupportedOperationException
         * @see java.util.Iterator#remove()
         */
        @Override
		public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    public static void main(String[] args) {
    	
        Date d1 = new Date();
        d1 = DateUtil.getMondayInWeek(d1);
        System.out.println(DateUtil.format(d1, "yyyy-MM-dd HH:mm:ss"));
    }
}

