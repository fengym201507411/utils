package com.fym.utils.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by fengyiming on 2018/3/30.
 */
public class MathUtils {

    /**
     * 100的BigDecimal定义
     */
    private final static BigDecimal ONE_HUN = new BigDecimal("100");

    /**
     * 计算百分比默认小数位
     */
    private final static int DEFAULT_SCALE = 4;

    /**
     * 计算价格默认小数位
     */
    private final static int DEFAULT_PRICE_SCALE = 2;

    /**
     * 判断不为空而且大于等于0且不能超过2位小数
     *
     * @param price
     * @return
     */
    public static boolean isPrice(BigDecimal price) {
        return price != null && BigDecimal.ZERO.compareTo(price) <= 0 && price.scale() <= 2;
    }

    /**
     * 判断不为空而且0-1之间且不能超过2位小数
     *
     * @param price
     * @return
     */
    public static boolean isDiscount(BigDecimal price) {
        return price != null && BigDecimal.ZERO.compareTo(price) <= 0 && BigDecimal.ONE.compareTo(price) >= 0 && price.scale() <= 2;
    }

    /**
     * 判断不为空而且0-1之间且不能有小数
     *
     * @param price
     * @return
     */
    public static boolean isInteger(BigDecimal price) {
        return price != null && BigDecimal.ZERO.compareTo(price) <= 0 && price.scale() == 0;
    }

    /**
     * 计算两个int值之间的比率，结果乘以100
     *
     * @param num
     * @param otherNumber
     * @return
     */
    public static BigDecimal divide(Integer num, Integer otherNumber) {
        return divide(num, otherNumber, DEFAULT_SCALE);
    }

    /**
     * 计算两个int值之间的比率，结果乘以100
     *
     * @param num
     * @param otherNumber
     * @return
     */
    public static BigDecimal divide(Integer num, Integer otherNumber, int scale) {
        return divide(new BigDecimal(num.toString()), new BigDecimal(otherNumber.toString()), scale);
    }

    /**
     * 计算两个BigDecimal值之间的比率，结果乘以100
     *
     * @param num
     * @param otherNumber
     * @return
     */
    public static BigDecimal divide(BigDecimal num, BigDecimal otherNumber) {
        return divide(num, otherNumber, DEFAULT_SCALE);
    }

    /**
     * 计算两个BigDecimal值之间的比率，结果乘以100
     *
     * @param num
     * @param otherNumber
     * @return
     */
    public static BigDecimal divide(BigDecimal num, BigDecimal otherNumber, int scale) {
        if (BigDecimal.ZERO.compareTo(otherNumber) == 0) {
            return BigDecimal.ZERO;
        }
        if (scale < 0) {
            scale = DEFAULT_SCALE;
        }
        return num.divide(otherNumber, scale, BigDecimal.ROUND_HALF_DOWN).multiply(ONE_HUN);
    }

    /**
     * 格式化价格类型的值，保证保留两位小数
     */
    public static BigDecimal formatPrice(BigDecimal value){
        if(value == null || value.scale() <= DEFAULT_PRICE_SCALE){
            return value;
        }
        return value.setScale(DEFAULT_PRICE_SCALE, RoundingMode.HALF_DOWN);
    }
}
