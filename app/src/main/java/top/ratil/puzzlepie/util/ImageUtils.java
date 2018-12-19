package top.ratil.puzzlepie.util;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    public final static int PATH_TYPE_ASSETS = 0;
    public final static int PATH_TYPR_FIEL = 1;

    /**
     * 将图片切割成小块
     *
     * @param parentImage 要切割的大图
     * @param xPiece      x方向的数量
     * @param yPiece      y方向的数量
     * @return 切割后的bitmapList
     */
    public static List<Bitmap> split(Bitmap parentImage, int xPiece, int yPiece) {
        List<Bitmap> bitmaps = new ArrayList<Bitmap>(xPiece * yPiece);
        int pWidth = parentImage.getWidth();
        int pHeight = parentImage.getHeight();
        int childWidth = pWidth / xPiece;
        int childHeight = pHeight / yPiece;

        for (int i = 0; i < xPiece * yPiece; i++) {
            int x = childWidth * (i % xPiece);
            int y = childHeight * (i / yPiece);
            Bitmap bitmap = Bitmap.createBitmap(parentImage, x, y, childWidth, childHeight);
            bitmaps.add(bitmap);
        }
        return bitmaps;
    }

    public static String getBitmapPath(Context context, int type) {
        if (type == ImageUtils.PATH_TYPE_ASSETS) {
//            String imagePath = context.getAssets().open("images/")
        }
        return null;
    }
}
