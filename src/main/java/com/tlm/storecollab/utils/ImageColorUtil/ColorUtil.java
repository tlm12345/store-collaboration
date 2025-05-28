package com.tlm.storecollab.utils.ImageColorUtil;

import java.awt.*;

/**
 * 图片颜色处理工具类
 * @author tlm
 */
public class ColorUtil {

    /**
     * 计算颜色之间的相似度
     * @param color1
     * @param color2
     * @return
     */
    public static double calculateSimilarity(Color color1, Color color2) {
        // 计算两个颜色之间的相似度，使用欧几里得距离来计算相似度

        int redDifference = color1.getRed() - color2.getRed();
        int greenDifference = color1.getGreen() - color2.getGreen();
        int blueDifference = color1.getBlue() - color2.getBlue();

        return Math.sqrt(
            Math.pow(redDifference, 2) +
            Math.pow(greenDifference, 2) +
            Math.pow(blueDifference, 2)
        );
    }
    
    public static double calculateSimilarity(Color color, String hexColor) {
        Color parsedColor = Color.decode(hexColor);
        return calculateSimilarity(color, parsedColor);
    }
    
    public static double calculateSimilarity(String hexColor, Color color) {
        Color parsedColor = Color.decode(hexColor);
        return calculateSimilarity(parsedColor, color);
    }
    
    public static double calculateSimilarity(String hexColor1, String hexColor2) {
        Color color1 = Color.decode(hexColor1);
        Color color2 = Color.decode(hexColor2);
        return calculateSimilarity(color1, color2);
    }

}