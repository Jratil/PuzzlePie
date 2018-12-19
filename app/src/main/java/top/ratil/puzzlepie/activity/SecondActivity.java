package top.ratil.puzzlepie.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;
import com.bumptech.glide.Glide;
import com.cazaea.sweetalert.SweetAlertDialog;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.model.TResult;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.sdsmdg.tastytoast.TastyToast;
import top.ratil.puzzlepie.R;
import top.ratil.puzzlepie.helper.LevelHelper;
import top.ratil.puzzlepie.helper.RecordHelper;
import top.ratil.puzzlepie.helper.SoundHelper;
import top.ratil.puzzlepie.manager.EventManager;
import top.ratil.puzzlepie.util.ArrayUtils;
import top.ratil.puzzlepie.util.DisplayUtils;
import top.ratil.puzzlepie.util.ImageUtils;
import top.ratil.puzzlepie.util.PhotoUtils;
import top.ratil.puzzlepie.view.CustomDialog;
import top.ratil.puzzlepie.view.CustomView;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SecondActivity extends TakePhotoActivity implements View.OnClickListener, CustomDialog.PicClickListener {

    @BindView(R.id.parent_view)
    RelativeLayout parent_layout;

    @BindView(R.id.custom_view)
    CustomView customView;

    @BindView(R.id.use_time_text_view)
    TextView timeTextView;

    @BindView(R.id.reset_button)
    Button resetButton;

    @BindView(R.id.level_button)
    Button levelButton;

    @BindView(R.id.puzzle_pic)
    QMUIRadiusImageView puzzlePic;

    @BindView(R.id.select_pic)
    Button selectPic;

    @BindView(R.id.history_pic)
    Button historyPic;

    private ImageView startView;

    private int count = 3;

    private int[] order;

    //图片内边距
    private final static int PADDING = LevelHelper.PADDING;

    private final static String[] LEVELS = LevelHelper.getAllLevels();

    private boolean firstTouch = true;

    private static Bitmap lastPic;

    private static Bitmap initBitmap;

    private static boolean okSound;
    private SoundHelper soundHelper;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.translucent(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);

        Intent intent = getIntent();
        okSound = intent.getBooleanExtra("okSound", true);

        resetButton.setOnClickListener(this);
        levelButton.setOnClickListener(this);

        soundHelper = SoundHelper.init(this);

        //初始化图片
        try {
            InputStream in = getAssets().open("images/puzzle_pic/pic1.png");
            initBitmap = BitmapFactory.decodeStream(in);
            Glide.with(this)
                    .load(initBitmap)
                    .into(puzzlePic);

            initPic(initBitmap);
        } catch (IOException e) {
            Log.e("SecondActivity:", "onCreate: 初始化加载图片出错！\n" + e.getMessage());
        }
    }

    /**
     * 初始化图片等
     * @param initBitmap 需要使用的图片
     */
    private void initPic(Bitmap initBitmap) {
        //初始化一个随机数组
        order = upsetOrder(count);
        List<Bitmap> bitmapList = ImageUtils.split(initBitmap, count, count);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //获取屏幕的宽度px
        int screenWidth = metrics.widthPixels;
        //获取每张图片应该设置的宽度px
        int picWidth = (screenWidth - DisplayUtils.dip2px(this, 40) - (count + 1) * PADDING) / count;

        //customView的配置，布局的宽度，单位为px
        int layoutWidth = picWidth * count + (count + 1) * PADDING;
        //设置宽高
        LinearLayout.LayoutParams customLayout = new LinearLayout.LayoutParams(layoutWidth, layoutWidth);
        //设置左右外边距
        int marginWidth = (screenWidth - layoutWidth) / 2;
        customLayout.setMarginStart(marginWidth);
        customLayout.setMarginEnd(marginWidth);
        customView.setLayoutParams(customLayout);

        //每个方块使用的布局，正方形
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(picWidth, picWidth);
        imageParams.setMargins(3, 3, 0, 0);
        for (int i = 0; i < count * count; i++) {
            ImageView imageView = new ImageView(this);
            //imageView的配置，设置宽高
            imageView.setLayoutParams(imageParams);
            // 设置图片拉伸已填充整个view
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //按顺序设置好图片的id，作用是为了表示方块在拼图中的位置
            imageView.setId(i);
            //将最后一张图的方块设置为startView
            if (order[i] == count * count - 1) {
                startView = imageView;
                try {
                    Glide.with(this)
                            .load(BitmapFactory.decodeStream(getAssets().open("images/last_pic.png")))
                            .into(imageView);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                lastPic = bitmapList.get(order[i]);
            } else {
                Glide.with(this)
                        .asBitmap()
                        .load(bitmapList.get(order[i]))
                        .into(imageView);
            }

            imageView.setOnClickListener(view -> {
                //如果能够交换，防止动画未完成就进行了下一次交换而出错
                if (EventManager.okChange) {
                    soundHelper.playSound(okSound);
                    //交换，如果交换成功，则开启新线程
                    // 行是否完成的判断
                    if (EventManager.moveView(this.startView, view)) {
                        if (firstTouch) {
                            //计时
                            timeHandler.post(timeRunnable);
                            firstTouch = false;
                        }
                        //判断是否完成拼图
                        if (puzzleOk(this.startView, view)) {
                            timeHandler.removeCallbacks(timeRunnable);
                            //TastyToast.makeText(getApplicationContext(), "成功啦! 成功啦!", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                            new PromptDialog(this)
                                    .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                                    .setTitleText("恭喜!")
                                    .setContentText("所用时间为" + timeTextView.getText())
                                    .setPositiveListener("确定", PromptDialog::dismiss)
                                    .show();
                            new Thread(() -> new Handler().postDelayed(() -> {
                                //将最后一张空白图片复原为原始图片
                                Glide.with(this)
                                        .asBitmap()
                                        .load(lastPic)
                                        .into(startView);
                                startView = null;
                                //将完成时间添加到记录
                                addRecord(Double.parseDouble(timeTextView.getText().toString().substring(0, timeTextView.length() - 2)));
                            }, 500)).run();
                        }
                    }
                }
            });
            customView.addViewToLayout(imageView, i, imageParams);
        }
    }

    /*
     * 用于上面拼图开始后修改时间
     * 开启新线程来修改时间
     */
    private static Handler timeHandler = new Handler();
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            String text = timeTextView.getText().toString();
            DecimalFormat df = new DecimalFormat("#0.0");
            double time = Double.valueOf(df.format(Double.valueOf(text.substring(0, text.length() - 2))));
            timeTextView.setText(String.format(getResources().getString(R.string.use_time), df.format(time + 0.1)));
            timeHandler.postDelayed(this, 98);
        }
    };

    /**
     * 打乱数组顺序， 用来对图片进行排序
     *
     * @param count 每行的数量
     * @return 打乱后的数组
     */
    private int[] upsetOrder(int count) {
        int[] orderArray = ArrayUtils.buildArray(count * count, 0);
        final int LEFT = 1;
        final int TOP = 2;
        final int RIGHT = 3;
        final int BOTTOM = 4;

        //数组的长度
        int length = count * count;

        for (int i = 0; i < count * count * 10; i++) {
            // 用来存储可能移动的所有步数，然后打乱，移动的时候从中选第一个是数get(0)，如果该步数不能移动则remove然后重新get(0)
            // 这样就不会和随机产生的步数一样，不能移动那个步数但还是产生那个步数
            List<Integer> stepList = ArrayUtils.buildList(count - 1, 1);
            Collections.shuffle(stepList);

            //数组中最大的数在数组中的位置, 下标从0开始
            int position = ArrayUtils.arrayIndex(length - 1, orderArray);
            //最大数所在的行数，从1开始
            int row = position / count + 1;
            //最大数所在的列数，从1开始
            int col = position % count + 1;
            //随机生成的方向，一共四个方向
            int direction = (int) Math.floor(Math.random() * 4) + 1;
            //随机生成的步数，最大为 count-1 步
            int stepNum = stepList.get(0);
            //Log.i("生成的方向随机数", "upsetOrder: " + direction);
            //Log.i("生成的方向步数", "upsetOrder: " + stepNum);
            //Log.i("生成的初始数组", "upsetOrder: " + stepList.toString());
            switch (direction) {
                case LEFT:
                    //在第一个位置不能往左移动
                    if (col == 1) {
                        break;
                    }
                    //如果向左移距离太远
                    while (col - stepNum < 1) {
                        stepList.remove(0);
                        stepNum = stepList.get(0);
                    }
                    //Log.i("向左移了", "--" + stepNum);
                    //交换两个数
                    orderArray = ArrayUtils.swapNum(position, position - stepNum, orderArray, LEFT);
                    break;
                case TOP:
                    if (row == 1) {
                        break;
                    }
                    while (row - stepNum < 1) {
                        stepList.remove(0);
                        stepNum = stepList.get(0);
                    }
                    //Log.i("向上移了", "--" + stepNum);
                    //交换两个数
                    orderArray = ArrayUtils.swapNum(position, position - count * stepNum, orderArray, TOP);
                    break;
                case RIGHT:
                    if (col == count) {
                        break;
                    }
                    while (col + stepNum > count) {
                        stepList.remove(0);
                        stepNum = stepList.get(0);
                    }
                    //Log.i("向右移了", "--" + stepNum);
                    //交换两个数
                    orderArray = ArrayUtils.swapNum(position, position + stepNum, orderArray, RIGHT);
                    break;
                case BOTTOM:
                    if (row == count) {
                        break;
                    }
                    while (row + stepNum > count) {
                        stepList.remove(0);
                        stepNum = stepList.get(0);
                    }
                    //Log.i("向下移了", "--" + stepNum);
                    //交换两个数
                    orderArray = ArrayUtils.swapNum(position, position + count * stepNum, orderArray, BOTTOM);
            }
            /*控制台打印每次交换顺序后的数组;
            Log.i("第" + i + "次结果:", "upsetOrder: " + Arrays.toString(orderArray));
            在控制台打印数组的矩阵，和拼图的方块一一对应
            Log.i("顺序", "---------------------------");
            for (int j = 0; j < count * count; j++) {
                System.out.print(orderArray[j] + "   \t");
                if ((j + 1) % count == 0) {
                    System.out.print("\n");
                }
            }*/
        }
        return orderArray;
    }

    /**
     * 交换两个view的id
     * 判断是否完成
     *
     * @param startView 原始view
     * @param touchView 按下view
     * @return 是否完成
     */
    private boolean puzzleOk(View startView, View touchView) {
        //获取view的id
        int startId = startView.getId();
        int touchId = touchView.getId();

        //将初始数组的两个和view对应位置的数进行交换
        int temp = order[startId];
        order[startId] = order[touchId];
        order[touchId] = temp;
        //再交换两个view的id，使view移动后id的数对应在图片中的位置
        startView.setId(touchId);
        touchView.setId(startId);
        //在控制台打印数组的矩阵，和拼图的方块一一对应
        //Log.i("顺序", "---------------------------");
        //for (int i = 0; i < count * count; i++) {
        //    System.out.print(order[i] + "   \t");
        //    if ((i + 1) % count == 0) {
        //        System.out.print("\n");
        //    }
        //}
        //创建一个用来对比的顺序数组
        int[] okArray = ArrayUtils.buildArray(count * count, 0);
        return Arrays.toString(order).equals(Arrays.toString(okArray));
    }

    /**
     * 重置游戏
     */
    private void resetGame() {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setCancelable(false);
        dialog.setTitleText("Loading");
        dialog.show();

        new Thread(() -> new Handler().postDelayed(() -> {
            timeHandler.removeCallbacks(timeRunnable);
            firstTouch = true;
            timeTextView.setText("0 s");
            customView.removeAllViews();

            //将左下角图片换掉
            Glide.with(this)
                    .load(initBitmap)
                    .into(puzzlePic);

            initPic(initBitmap);
            dialog.cancel();
        }, 1000)).run();
    }

    /**
     * 游戏难度的选择
     */
    private void levelSelect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ColorDialog dialog = new ColorDialog(this);
        builder.setTitle("选择难度")
                .setItems(LEVELS, (dialogInterface, i) -> {
                    switch (LEVELS[i]) {
                        case LevelHelper.LEVEL_SIMPLE:
                            this.count = 3;
                            break;
                        case LevelHelper.LEVEL_MIDDLE:
                            this.count = 4;
                            break;
                        case LevelHelper.LEVEL_DIFFICULT:
                            this.count = 5;
                            break;
                    }
                    resetGame();
                })
                .show();
    }

    /**
     * 将游戏的分数添加进去
     *
     * @param fraction
     */
    private void addRecord(double fraction) {
        RecordHelper recordHelper = new RecordHelper(this);
        SQLiteDatabase db = recordHelper.getWritableDatabase();
        recordHelper.addRecord(db, count, fraction);
    }


    /**
     * 点击监听
     *
     * @param view 点击的view
     */
    @Override
    public void onClick(View view) {
        soundHelper.playSound(okSound);
        switch (view.getId()) {
            case R.id.reset_button:
                resetGame();
                break;
            case R.id.level_button:
                levelSelect();
                break;
        }
    }

    @OnClick(R.id.history_pic)
    void historyPic() {
        soundHelper.playSound(okSound);
        CustomDialog dialog = new CustomDialog();
        dialog.show(getFragmentManager(), null);
    }

    @OnClick(R.id.select_pic)
    void selectPic() {
        soundHelper.playSound(okSound);
        try {
            PhotoUtils.onClick(this, getTakePhoto());
        } catch (IOException e) {
            Log.e("SecondActivity", "selectPic: 从相册选取照片出错！" + e.getMessage());
        }
    }

    /**
     * 选择历史图片中图片的点击监听
     *
     * @param bitmap 点击的图片
     */
    @Override
    public void picClick(Bitmap bitmap) {
        soundHelper.playSound(okSound);
        initBitmap = bitmap;
        resetGame();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //移除timeHandler运行的线程
        timeHandler.removeCallbacks(timeRunnable);
        //将firstTouch重置
        firstTouch = true;
        finish();
    }

    /**
     * 更换图片成功
     *
     * @param result 更换的结果
     */
    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        initBitmap = BitmapFactory.decodeFile(result.getImage().getOriginalPath());
        resetGame();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        TastyToast.makeText(this, "更换图片出错啦！", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        TastyToast.makeText(getApplicationContext(), "没有更换图片哦！", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
    }
}