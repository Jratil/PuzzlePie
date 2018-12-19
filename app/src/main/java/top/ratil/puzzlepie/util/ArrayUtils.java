package top.ratil.puzzlepie.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {

    final static int LEFT = 1;
    final static int TOP = 2;
    final static int RIGHT = 3;
    final static int BOTTOM = 4;

    /**
     * 获取num在数组中的下标
     *
     * @param num   要操作的数
     * @param array 要操作的数组
     * @return 获取的数的下表，-1表示数组不存在该数
     */
    public static int arrayIndex(int num, int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (num == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 交换数组中的两个位置数
     * 如果交换步数大于1，必须一步一步先和在矩阵上距离为1的数交换
     * 如果步数大于1，使用递归交换
     *
     * @param startPosition 最初的数的位置
     * @param swapPosition  要交换的数的位置
     * @param array         要操作的数组
     * @param direction     要移动的方向
     * @return 返回交换后的新数组
     */
    public static int[] swapNum(int startPosition, int swapPosition, int[] array, int direction) {
        int temp;
        int[] newArray = array;
        if ((direction == LEFT || direction == RIGHT)
                && Math.abs(startPosition - swapPosition) != 1) {
            if (direction == LEFT) {
                //先和左边距离为1的交换，再继续递归知道步数走完，下面都是相同方法
                temp = newArray[startPosition];
                newArray[startPosition] = newArray[startPosition - 1];
                newArray[startPosition - 1] = temp;
                startPosition--;
                newArray = swapNum(startPosition, swapPosition, newArray, direction);
                return newArray;
            } else {
                temp = array[startPosition];
                array[startPosition] = array[startPosition + 1];
                array[startPosition + 1] = temp;
                startPosition++;
                newArray = swapNum(startPosition, swapPosition, newArray, direction);
                return newArray;
            }
        }
        if ((direction == TOP || direction == BOTTOM)
                && Math.abs((startPosition - swapPosition)) / Math.sqrt(array.length) != 1) {
            if (direction == TOP) {
                temp = newArray[startPosition];
                newArray[startPosition] = newArray[startPosition - (int) Math.sqrt(array.length)];
                newArray[startPosition - (int) Math.sqrt(array.length)] = temp;
                startPosition = startPosition - (int) Math.sqrt(array.length);
                newArray = swapNum(startPosition, swapPosition, newArray, direction);
                return newArray;
            } else {
                temp = newArray[startPosition];
                newArray[startPosition] = newArray[startPosition + (int) Math.sqrt(array.length)];
                newArray[startPosition + (int) Math.sqrt(array.length)] = temp;
                startPosition = startPosition + (int) Math.sqrt(array.length);
                newArray = swapNum(startPosition, swapPosition, newArray, direction);
                return newArray;
            }
        }
        temp = array[startPosition];
        array[startPosition] = array[swapPosition];
        array[swapPosition] = temp;
        return array;
    }

    /**
     * 生成从0开始的顺序数组
     *
     * @param length 数组的长度
     * @return 生成的数组
     */
    public static int[] buildArray(int length, int startNum) {
        int[] array = new int[length];
        for (int i = startNum; i < length + startNum; i++) {
            array[i] = i;
        }
        return array;
    }

    /**
     * 生成一个新的从startNum开始的顺序的list
     *
     * @param length   生成的长度
     * @param startNum 开始的数
     * @return 生成的list
     */
    public static List<Integer> buildList(int length, int startNum) {
        List<Integer> list = new ArrayList<>(length);

        for (int i = startNum; i < length + startNum; i++) {
            list.add(i);
        }
        return list;
    }
}
