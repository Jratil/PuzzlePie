package top.ratil.puzzlepie.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import top.ratil.puzzlepie.R;

import java.io.File;
import java.io.IOException;
import java.util.Properties;


public class CustomDialog extends DialogFragment {

    private static final String TAG = "CustomDialog";
    private CustomView defaultPicView;

    private CustomView customPicView;

    private Button notChange;

    public interface PicClickListener {
        void picClick(Bitmap bitmap);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_fragment_pic, container);
        defaultPicView = view.findViewById(R.id.default_pic_view);
        customPicView = view.findViewById(R.id.dialog_pic_view);
        notChange = view.findViewById(R.id.button_not_change);

        initView();

        notChange.setOnClickListener(view1 -> {
            getDialog().dismiss();
        });
        return view;
    }

    private void initView() {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int dialogWidth = (int) (metrics.widthPixels * 0.9);

        int picWidth = (dialogWidth - 20 * 3) / 2;
        int picHeight = picWidth;

        //每个图片的设置
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(picWidth, picHeight);
        imageParams.setMargins(20, 20, 0, 0);

        Properties properties = new Properties();
        File picPath = null;
        try {
            //添加默认的两张图片进去
            //如果要添加图片，只要把获取所有的默认图片，然后遍历添加进去就好
            int num = 0;
            for (int i = 0; i < 2; i++) {
                ImageView defaultPic = new ImageView(getActivity());
                defaultPic.setLayoutParams(imageParams);
                defaultPic.setAdjustViewBounds(true);
                defaultPic.setScaleType(ImageView.ScaleType.FIT_XY);

                final Bitmap defaultBitmap =
                        BitmapFactory.decodeStream(getActivity().getAssets().open("images/puzzle_pic/pic" + (i + 1) + ".png"));
                Glide.with(getActivity())
                        .load(defaultBitmap)
                        .into(defaultPic);
                defaultPic.setOnClickListener(view -> {
                    PicClickListener listener = (PicClickListener) getActivity();
                    listener.picClick(defaultBitmap);
                    getDialog().dismiss();
                });
                defaultPicView.addViewToLayout(defaultPic, i, imageParams);
                num++;
            }
            int viewHeight = (num + 1) / 2 * picHeight + ((num + 1) / 2 + 1) * 20;
            LinearLayout.LayoutParams defaultViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeight);
            defaultPicView.setLayoutParams(defaultViewParams);

            properties.load(getActivity().getAssets().open("properties/path.properties"));
            String imagesPath = properties.getProperty("images_path");
            picPath = new File(Environment.getExternalStorageDirectory(), imagesPath);

            if (!picPath.getParentFile().exists()) {
                picPath.getParentFile().mkdirs();
            }
        } catch (IOException e) {
            Log.i(TAG, "initView: 读取图片出错！\n" + e.getMessage());
        }

        File[] files = picPath.listFiles();
        if (files != null) {
            int num = 0;
            for (int i = 0; i < files.length; i++) {
                if (i == 10) {
                    break;
                }
                File file = files[files.length - 1 - i];
                if (file.isFile()) {
                    int index = file.getPath().lastIndexOf(".");
                    if (index <= 0) {
                        continue;
                    }
                    String suffix = file.getPath().substring(index);

                    if (!suffix.toLowerCase().equals(".jpg")
                            && !suffix.toLowerCase().equals(".jpeg")
                            && !suffix.toLowerCase().equals(".bmp")
                            && !suffix.toLowerCase().equals(".png")) {
                        continue;
                    }
                }

                ImageView imageView = new ImageView(getActivity());
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(imageParams);
                imageView.setOnClickListener(view -> {
                    PicClickListener listener = (PicClickListener) getActivity();
                    listener.picClick(BitmapFactory.decodeFile(file.getPath()));
                    getDialog().dismiss();
                });

                Glide.with(getActivity())
                        .load(file.getPath())
                        .into(imageView);
                customPicView.addViewToLayout(imageView, i, imageParams);
                num++;
            }

            int customViewHeight = (num + 1) / 2 * picHeight + ((num + 1) / 2 + 1) * 20;
            LinearLayout.LayoutParams customView =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, customViewHeight);
            customPicView.setLayoutParams(customView);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_fragment_pic);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            dialog.getWindow().setLayout((int) (metrics.widthPixels * 0.9), (int) (metrics.heightPixels * 0.9));
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        }
    }
}
