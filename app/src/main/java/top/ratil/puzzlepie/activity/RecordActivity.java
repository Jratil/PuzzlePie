package top.ratil.puzzlepie.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import top.ratil.puzzlepie.R;
import top.ratil.puzzlepie.helper.RecordHelper;
import top.ratil.puzzlepie.util.DisplayUtils;

import java.util.List;
import java.util.Map;

public class RecordActivity extends AppCompatActivity {

    @BindView(R.id.simple_record)
    LinearLayout simpleRecordLayout;
    @BindView(R.id.medium_record)
    LinearLayout mediumRecordLayout;
    @BindView(R.id.difficult_record)
    LinearLayout difficultRecordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highest_record);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.translucent(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);

        init();
    }

    private void init() {
        RecordHelper recordHelper = new RecordHelper(this);
        SQLiteDatabase db = recordHelper.getWritableDatabase();
        Map<Integer, List<Double>> allRecords = recordHelper.selectRecord(db);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, DisplayUtils.dip2px(this, 12), 0, 0);

        for (Map.Entry<Integer, List<Double>> entry : allRecords.entrySet()) {
            List<Double> recordList = entry.getValue();
            for (Double record : recordList) {
                TextView textView = new TextView(this);
                textView.setLayoutParams(params);
                textView.setTextSize(20);
                textView.setText(record.toString() + " s");
                switch (entry.getKey()) {
                    case 3:
                        simpleRecordLayout.addView(textView);
                        break;
                    case 4:
                        mediumRecordLayout.addView(textView);
                        break;
                    case 5:
                        difficultRecordLayout.addView(textView);
                        break;
                }
            }
        }
    }
}
