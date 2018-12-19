package top.ratil.puzzlepie.helper;

public class LevelHelper {

    public final static String LEVEL_SIMPLE = "简单";
    public final static String LEVEL_MIDDLE = "中等";
    public final static String LEVEL_DIFFICULT = "困难";

    public final static int PADDING = 3;

    public static String[] getAllLevels() {
        return new String[]{LEVEL_SIMPLE, LEVEL_MIDDLE, LEVEL_DIFFICULT};
    }

    public static int[] getAllLevelsInt() {
        return new int[]{3, 4, 5};
    }

    public static String int2String(int level) {
        for (int i : getAllLevelsInt()) {
            if (i == level) {
                return getAllLevels()[i - 3];
            }
        }
        return getAllLevels()[0];
    }

    public static int string2Int(String level) {
        int num = 0;
        for (String i : getAllLevels()) {
            if (i.equals(level)) {
                return getAllLevelsInt()[num];
            }
            num++;
        }
        return getAllLevelsInt()[0];
    }
}
