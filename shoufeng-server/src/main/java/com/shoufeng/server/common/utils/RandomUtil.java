package com.shoufeng.server.common.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author shoufeng
 */
public class RandomUtil {

    //随机数一般是用Random来获取，但是当在多任务的并行化编程时，问题就出现了。
    // 因为Random是基于时间作为种子来生成伪随机数的，而如果程序在多核并行时，在同一时间内的多个核中取到的时间是一样的
    // 这样一来，生成的伪随机数就有可能会有一样的。
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    /**
     * 生成订单编号
     * 时间戳+四位随机数
     *
     * @return
     */
    public static String generateOrderCode() {
        //时间戳+N位随机数流水号
        return DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSS", Locale.CHINA) + generateNumber(4);
    }

    //N位随机数流水号
    public static String generateNumber(final int num) {
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= num; i++) {
            sb.append(RANDOM.nextInt(9));
        }
        return sb.toString();
    }

}
