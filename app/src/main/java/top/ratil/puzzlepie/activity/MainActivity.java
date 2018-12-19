package top.ratil.puzzlepie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cazaea.sweetalert.SweetAlertDialog;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import top.ratil.puzzlepie.R;
import top.ratil.puzzlepie.helper.SoundHelper;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.sound_pic)
    ImageView soundPic;

    @BindView(R.id.start_game)
    Button startGame;

    @BindView(R.id.highest_record)
    Button hishestRecord;

    @BindView(R.id.game_help)
    Button gameHelp;

    @BindView(R.id.exit_game)
    Button exitGame;

    private static boolean okSound = true;
    private SoundHelper sound;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.translucent(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);

        sound = SoundHelper.init(this);
    }

    @OnClick(R.id.sound_pic)
    void soundChange() {
        if (okSound) {
            okSound = false;
            soundPic.setImageResource(R.drawable.icon_close_sound);
        } else {
            okSound = true;
            soundPic.setImageResource(R.drawable.icon_sound);
        }
    }

    @OnClick({R.id.start_game})
    void startGame() {
        sound.playSound(okSound);
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.putExtra("okSound", okSound);
        startActivity(intent);
    }

    @OnClick(R.id.highest_record)
    void highestRecord() {
        sound.playSound(okSound);
        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.game_help)
    void gameHelp() {
        sound.playSound(okSound);
        new SweetAlertDialog(this)
                .setTitleText("游戏帮助")
                .setContentText("\n没有啥帮助\n" +
                        "点击特殊方块周围方块与之交换\n" +
                        "直到拼图完成\n")
                .setConfirmText("知道了")
                .show();
    }

    @OnClick(R.id.exit_game)
    void exitGame() {
        sound.playSound(okSound);
        new SweetAlertDialog(this)
                .setTitleText("退出")
                .setContentText("\n是否退出游戏？\n")
                .setConfirmText("确定")
                .setCancelText("取消")
                .setConfirmClickListener(sweetAlertDialog -> {
                    finish();
                }).show();
    }
}
