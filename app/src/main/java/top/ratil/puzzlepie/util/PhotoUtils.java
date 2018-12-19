package top.ratil.puzzlepie.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static android.content.ContentValues.TAG;

public class PhotoUtils {


    public static PhotoUtils of() {
        return new PhotoUtils();
    }

    public static void onClick(Context context, TakePhoto photo) throws IOException {
        Properties properties = new Properties();
        properties.load(context.getAssets().open("properties/path.properties"));

        String imagesPath = properties.getProperty("images_path");
        File file = new File(Environment.getExternalStorageDirectory(),
                imagesPath + System.currentTimeMillis() + ".jpg");

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        Uri uri = Uri.fromFile(file);
        //压缩图片
        CompressConfig compressConfig = new CompressConfig.Builder()
                .setMaxSize(102400)
                .setMaxPixel(800)
                .enableQualityCompress(true)
                .create();

        photo.onEnableCompress(compressConfig, true);
        photo.onPickFromGalleryWithCrop(uri, getCropOptions());
    }

    private static CropOptions getCropOptions() {
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setAspectX(800).setAspectY(800);
        builder.setWithOwnCrop(true);
        return builder.create();
    }
}
