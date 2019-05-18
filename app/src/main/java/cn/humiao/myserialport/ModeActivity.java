package cn.humiao.myserialport;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

public class ModeActivity extends AppCompatActivity {
    private Spinner spinner;
    private DBManager dbManager;
    private TextView  temp,humidity,co2,light,soilTemp,soilHumidity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        temp =findViewById(R.id.temp);
        humidity = findViewById(R.id.humidity);
        co2 = findViewById(R.id.co2);
        light = findViewById(R.id.light);
        soilTemp =findViewById(R.id.soilTemp);
        soilHumidity = findViewById(R.id.soilHumidity);

        dbManager = new DBManager(this);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dbManager.openDatabase();
                SQLiteDatabase db = dbManager.getDatabase();
                Cursor cursor =  db.rawQuery("SELECT * FROM mod WHERE ID = ?",
                        new String[]{String.valueOf(i+1)});
                if(cursor.moveToFirst())
                {
                    String humidity_date,co2_data,light_data,soilTemp_data,soilHumidity_data,temp_date;
                    temp_date = cursor.getString(cursor.getColumnIndex("temp"));
                    humidity_date = cursor.getString(cursor.getColumnIndex("humidity"));
                    co2_data = cursor.getString(cursor.getColumnIndex("co2"));
                    light_data = cursor.getString(cursor.getColumnIndex("light"));
                    soilTemp_data = cursor.getString(cursor.getColumnIndex("soilTemp"));
                    soilHumidity_data = cursor.getString(cursor.getColumnIndex("soilHumidity"));

                    temp.setText("空气温度: "+temp_date+"℃");
                    humidity.setText("空气湿度: "+humidity_date+"％");
                    co2.setText("co2: "+co2_data+"ppm");
                    light.setText("光照强度： "+light_data+"lux");
                    soilTemp.setText("土壤温度: "+soilTemp_data+"℃");
                    soilHumidity.setText("土壤湿度: "+soilHumidity_data+"％");
                }
                dbManager.closeDatabase();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

            }
        });
    }
}
