package cn.xwplay.common.util;

import java.util.Random;

public class RandomUtil {

    public static String randomNum4() {
        var random = new Random();
        // 生成四位随机数字
        return (random.nextInt(9000) + 1000)+"";
    }
}
