package com.fym.utils.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Created by fengyiming on 2017/11/9.
 */
public class DateUtils {

    /**
     * 获取当前时间的毫秒数
     *
     * @return
     */
    public static Long now() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 将毫秒数转化为时间
     *
     * @param mills
     * @return
     */
    public static LocalDateTime getTime(long mills) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(mills), ZoneId.systemDefault());
    }

    /**
     * 判断当前时间是否为30分钟或者整点(59分59秒认为也是整点)
     * @param localTime
     * @return
     */
    public static boolean isHalfOrZeroHour(LocalTime localTime) {
        return 0 == localTime.getMinute() || 30 == localTime.getMinute() || (59 == localTime.getMinute() && 59 == localTime.getSecond()) ;
    }
}
