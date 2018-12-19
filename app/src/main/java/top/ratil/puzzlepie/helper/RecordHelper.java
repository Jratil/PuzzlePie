package top.ratil.puzzlepie.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import top.ratil.puzzlepie.constant.RecordDbConstant.RecordTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "record.db";

    public RecordHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + RecordTable.TABLE_NAME + "(" +
                "record_id integer primary key autoincrement," +
                RecordTable.Cols.GAME_LEVEL + " integer not null default 3," +
                RecordTable.Cols.GAME_RECORD + " real not null)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * 向数据库添加数据
     *
     * @param db       db
     * @param level    等级
     * @param fraction 分数
     */
    public void addRecord(SQLiteDatabase db, int level, double fraction) {
        insert(db, level, fraction);

        //查询数据，因为只显示10条数据，所以多余的数据全部删除
        Cursor cursor = select(db, level);
        if (cursor.getCount() > 10) {
            //遍历删除多出来的数据
            if (cursor.move(10)) {
                while (cursor.moveToNext()) {
                    double lastRecord = Double.parseDouble(cursor.getString(cursor.getColumnIndex(RecordTable.Cols.GAME_RECORD)));
                    deleteRecord(db, level, lastRecord);
                }
            }
        }
    }

    /**
     * 返回每个等级的所有记录
     *
     * @param db db
     * @return 所有等级的所有记录
     */
    public Map<Integer, List<Double>> selectRecord(SQLiteDatabase db) {
        int[] levels = LevelHelper.getAllLevelsInt();
        Map<Integer, List<Double>> allRecords = new HashMap<>();

        for (int level : levels) {
            Cursor cursor = select(db, level);
            List<Double> recordList = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    double record = Double.parseDouble(cursor.getString(cursor.getColumnIndex(RecordTable.Cols.GAME_RECORD)));
                    recordList.add(record);
                } while (cursor.moveToNext());
                allRecords.put(level, recordList);
            }
        }
        return allRecords;
    }

    /**
     * 升序查找记录
     *
     * @param db    db
     * @param level 等级
     * @return 查找到的数据
     */
    private Cursor select(SQLiteDatabase db, int level) {
        Cursor cursor = db.query(
                RecordTable.TABLE_NAME,
                null,
                RecordTable.Cols.GAME_LEVEL + " = ?",
                new String[]{String.valueOf(level)},
                null, null,
                RecordTable.Cols.GAME_RECORD + " ASC"
        );
        return cursor;
    }

    /**
     * 插入数据
     *
     * @param db       db
     * @param level    等级
     * @param fraction 分数
     */
    private void insert(SQLiteDatabase db, int level, double fraction) {
        ContentValues values = new ContentValues();
        values.put(RecordTable.Cols.GAME_LEVEL, level);
        values.put(RecordTable.Cols.GAME_RECORD, fraction);
        db.insert(RecordTable.TABLE_NAME, null, values);
    }

    /**
     * 删除数据
     *
     * @param db       db
     * @param level    要删除的等就
     * @param fraction 要删除的分数
     */
    private void deleteRecord(SQLiteDatabase db, int level, double fraction) {
        db.delete(RecordTable.TABLE_NAME,
                RecordTable.Cols.GAME_LEVEL + " = ? AND " + RecordTable.Cols.GAME_RECORD + " = ?",
                new String[]{String.valueOf(level), String.valueOf(fraction)});
    }
}
