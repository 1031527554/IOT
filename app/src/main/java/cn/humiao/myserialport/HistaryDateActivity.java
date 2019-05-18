package cn.humiao.myserialport;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class HistaryDateActivity extends AppCompatActivity {
    private LineChart mlineChart;
    private LineDataSet set1,set2;
    private DBManager dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histary_date);

        mlineChart = (LineChart)findViewById(R.id.line1);
        //后台绘制
        mlineChart.setDrawGridBackground(false);
        //设置描述文本
        mlineChart.getDescription().setEnabled(false);
        //设置支持触控手势
        mlineChart.setTouchEnabled(true);
        //设置缩放
        mlineChart.setDragEnabled(true);
        //设置推动
        mlineChart.setScaleEnabled(true);
        //如果禁用,扩展可以在x轴和y轴分别完成
        mlineChart.setPinchZoom(true);

        ArrayList<Entry> values = new ArrayList<Entry>();
        ArrayList<Entry> values1 = new ArrayList<Entry>();

        dbManager = new DBManager(this);
        dbManager.openDatabase();
        SQLiteDatabase db = dbManager.getDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM sense ",
                new String[]{});
        if(cursor.moveToFirst()){
            do {
                int x = cursor.getInt(cursor.getColumnIndex("ID"));
                float T =cursor.getFloat(cursor.getColumnIndex("temps"));
                float H =cursor.getFloat(cursor.getColumnIndex("humidity"));
                values.add(new Entry(x,T));
                values1.add(new Entry(x,H));
            }while(cursor.moveToNext());
        }
        dbManager.closeDatabase();
        //设置数据
        set1 = setData(values,"温度",-65536);
        set2 = setData(values1,"湿度",-16776961);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
        LineData data = new LineData(dataSets);
        mlineChart.setData(data);

        //默认动画
        mlineChart.animateX(2500);
        //刷新
        mlineChart.invalidate();
        // 得到这个文字
        Legend l = mlineChart.getLegend();
        // 修改文字 ...
        l.setForm(Legend.LegendForm.LINE);
    }

    private LineDataSet setData(ArrayList<Entry> values,String name ,int color){
        LineDataSet set1;
        set1 = new LineDataSet(values, name);
        // 在这里设置线
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(color);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);

        if (Utils.getSDKInt() >= 18) {
            // 填充背景只支持18以上
            //Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
            //set1.setFillDrawable(drawable);
            set1.setFillColor(Color.YELLOW);
        } else {
            set1.setFillColor(Color.BLACK);
        }

        //添加数据集
        set1.setDrawFilled(false);
        return set1;
    }

 /*   private void setData(ArrayList<Entry> values) {
        if (mlineChart.getData() != null && mlineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mlineChart.getData().getDataSetByIndex(0);
            set1 = (LineDataSet) mlineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mlineChart.getData().notifyDataChanged();
            mlineChart.notifyDataSetChanged();
        } else {
            // 创建一个数据集,并给它一个类型
            set1 = new LineDataSet(values, "年度总结报告");

            // 在这里设置线
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // 填充背景只支持18以上
                //Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
                //set1.setFillDrawable(drawable);
                set1.setFillColor(Color.YELLOW);
            } else {
                set1.setFillColor(Color.BLACK);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            //添加数据集
            set1.setDrawFilled(false);
            dataSets.add(set1);

            //创建一个数据集的数据对象
            LineData data = new LineData(dataSets);

            //谁知数据
            mlineChart.setData(data);
        }
    }*/
}
