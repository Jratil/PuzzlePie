package top.ratil.puzzlepie.util;

import android.content.Context;

public class DisplayUtils {

    /**
     * 将px转换为dip
     * @param context context
     * @param px px大小
     * @return 转换后的dip
     */
    public static int px2dip(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 将dip转px
     * @param context context
     * @param dip dip大小
     * @return 转换后的px
     */
    public static int dip2px(Context context, float dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }
}
