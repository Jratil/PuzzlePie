package top.ratil.puzzlepie.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CustomView extends ViewGroup {

    private Context context;

    public CustomView(Context context) {
        super(context);
        this.context = context;
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected boolean addViewInLayout(View child, int index, LayoutParams params) {
        return super.addViewInLayout(child, index, params);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, LayoutParams params, boolean preventRequestLayout) {
        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }

    /**
     * 因为是循环将view添加， 所以调用addViewInLayout已节省资源
     *
     * @param child  添加的view
     * @param index  下标
     * @param params 布局
     */
    public void addViewToLayout(View child, int index, LayoutParams params) {
        addViewInLayout(child, index, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        //设置一下view的宽高，无论怎么都设成width和height一样高
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        Log.i(TAG, "view中测得的width----" + widthSize + "; 和高height----" + widthSize);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        int childMeasureWidth = 0;
        int childMeasureHeight = 0;
        int layoutWidth = 0;
        int layoutHeight = 0;
        int maxChildHeight = 0;
        int padding = getPaddingStart();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childMarginLeft = lp.leftMargin;
            int childMarginTop = lp.topMargin;
            int childMarginRight = lp.rightMargin;
            int childMarginBottom = lp.bottomMargin;

            childMeasureWidth = child.getMeasuredWidth();
            childMeasureHeight = child.getMeasuredHeight();

            //判断已经添加的子iew加上即将要添加的子view的宽度是否大于父view的宽度
            //如果是则把父view已使用的宽度layoutWidth=0
            //父view已使用的高度layoutHeight=已使用的高度加当前层最高的view的高度加内边距
            //将当前行最高view的高度设为0
            if (layoutWidth + childMeasureHeight + childMarginLeft + childMarginRight + padding * 2 >= getWidth()) {
                layoutWidth = 0;
                layoutHeight += maxChildHeight;
                maxChildHeight = 0;
            }

            //左上角顶点x，父view已使用的宽度加上内边距加外边距
            //左上角顶点y，父view已使用的高度加上内边距加外边距
            //右上角顶点x，左上角顶点x加上子view的宽度加外边距
            //左下角顶点y，左上角顶点y加上子view的高度加外边距
            l = layoutWidth + padding + childMarginLeft;
            t = layoutHeight + padding + childMarginTop;
            r = l + childMeasureWidth + padding;
            b = t + childMeasureHeight + padding;

            //父view已使用宽度加子view的宽度加内边距加外边距
            //因为要设置四周padding相同，所以直接乘2
            layoutWidth += childMeasureWidth + padding * 2 + childMarginLeft + childMarginRight;
            if (childMeasureHeight + childMarginLeft + childMarginRight + padding * 2 > maxChildHeight) {
                maxChildHeight = childMeasureHeight + padding * 2 + childMarginTop + childMarginBottom;
            }

            child.layout(l, t, r, b);
        }
    }
}
