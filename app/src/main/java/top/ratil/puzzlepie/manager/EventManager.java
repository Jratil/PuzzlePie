package top.ratil.puzzlepie.manager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

public class EventManager {

    /**
     * 全局，用于判断之前的 view 的位移动画是否完成
     * true 为动画已完成，可以获取下一个按下的view
     * false 为动画未完成，不会获取下一个view
     */
    public static boolean okChange = true;

    /**
     * 用来交换时间的完成
     * @param startView 最初始的view
     * @param touchView 按下的view（就是要交换的view）
     */
    public static boolean moveView(View startView, View touchView) {

        //如果按下的view为startView（最初的view）， 不交换
        if (startView == null || startView == touchView) {
            return false;
        }

        //默认 view 的 x 坐标
        float startX = startView.getX();
        //默认 view 的 y 坐标
        float startY = startView.getY();
        //按下 view 的 x 坐标
        float touchX = touchView.getX();
        //按下 view 的 y 坐标
        float touchY = touchView.getY();
        //view 的宽度
        float width = startView.getWidth();
        //view 的高度
        float height = startView.getHeight();

        //默认 view 的 x 方向偏移量
        float startTranslateX = startView.getTranslationX();
        //默认 view 的 y 方向偏移量
        float startTranslateY = startView.getTranslationY();
        //按下 view 的 x 方向偏移量
        float touchTranslateX = touchView.getTranslationX();
        //按下 view 的 y 方向偏移量
        float touchTranslateY = touchView.getTranslationY();

        //打印默认 view 的左上角坐标
        //Log.i(TAG, "1: start = (" + (startX) + ", " + (startY) + ")");
        //打印按下 view 的左上角坐标
        //Log.i(TAG, "1: touch = (" + (touchX) + ", " + (touchY) + ")");

        //判断按下的 view 是否在默认 view 上下左右， 并且宽度不超过一个 view 的宽度
        if (((Math.abs(startX - touchX) > width + 5 || Math.abs(startY - touchY) > height + 5))
                || startX != touchX && startY != touchY) {
            return false;
        }

        //动画设置
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                //默认 view 轴位移动画
                //startTranslateX | startTranslateY 为原点，以 view 偏移位置作为原点， 后面位移距离必须加上偏移量
                ObjectAnimator.ofFloat(startView, "translationX", startTranslateX, startTranslateX + touchX - startX),
                ObjectAnimator.ofFloat(startView, "translationY", startTranslateY, startTranslateY + touchY - startY),
                //按下 view 轴位移动画
                //touchTranslateX | touchTranslateY 为原点，以 view 偏移位置作为原点， 后面位移距离必须加上偏移量
                ObjectAnimator.ofFloat(touchView, "translationX", touchTranslateX, touchTranslateX + startX - touchX),
                ObjectAnimator.ofFloat(touchView, "translationY", touchTranslateY, touchTranslateY + startY - touchY)
        );
        animatorSet.setDuration(50)
                .start();

        //动画监听
        //动画开始时改变 okChange 的值为 false， 防止该动画未完成又按下下一个 view 导致动画中途停止
        //动画结束后恢复 okChange 的值为 true, 表示可以接着按下下一个 view
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                okChange = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                okChange = true;
            }
        });
        //无需动画时用此来使view交换位置
        //startView.layout(touchX, touchY, touchX + width, touchY + height);
        //touchView.layout(startX, startY, startX + width, startY + height);
        return true;
    }
}
