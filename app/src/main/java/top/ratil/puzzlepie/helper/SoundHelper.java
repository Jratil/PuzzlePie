package top.ratil.puzzlepie.helper;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RequiresApi;
import top.ratil.puzzlepie.R;

public class SoundHelper {

    private static SoundPool soundPool;
    private static int soundId;

    private static final SoundHelper instance = new SoundHelper();

    public SoundHelper() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static SoundHelper init(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build();

        soundId = soundPool.load(context, R.raw.click_voice, 1);
        return instance;
    }

    public void playSound(boolean flag) {
        if (flag)
            soundPool.play(soundId, 1, 1, 0, 0, 1);
    }
}