package cn.humiao.myserialport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

public class StateActivity extends AppCompatActivity implements View.OnClickListener{
    private Button temp_add,temp_sub,soilTemp_add,soilTemp_sub,co2_add,co2_sub,humidity_add,humidity_sub,soilHumidity_add,soilHumidity_sub,light_add,light_sub;
    private TextView temp,soilTemp,co2,humidity,soilHumidity,light;
    private int temp_data=30,soilTemp_data=20,co2_data=620,humidity_data=40,soilHumidity_data=60,light_data=108;
    private SenseDate senseDate = new SenseDate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        Intent intent = getIntent();
        temp_data =(int)intent.getDoubleExtra("temp",30);
        humidity_data = (int)intent.getDoubleExtra("humidity",60);

        senseDate.setTemp(temp_data);
        senseDate.setHumidity(humidity_data);

        temp = findViewById(R.id.temp);
        temp.setText("空气温度: "+temp_data+"℃");
        soilTemp = findViewById(R.id.soilTemp);
        soilTemp.setText("土壤温度: "+soilTemp_data+"℃");
        co2 = findViewById(R.id.co2);
        co2.setText("co2: "+co2_data+"ppm");
        humidity = findViewById(R.id.humidity);
        humidity.setText("空气湿度: "+ humidity_data+"％");
        soilHumidity = findViewById(R.id.soilHumidity);
        soilHumidity.setText("土壤湿度: "+soilHumidity_data+"％");
        light = findViewById(R.id.light);
        light.setText("光照强度： "+light_data+"lux");

        temp_add =findViewById(R.id.temp_add);
        temp_add.setOnClickListener(this);
        temp_sub =findViewById(R.id.temp_sub);
        temp_sub.setOnClickListener(this);
        soilTemp_add =findViewById(R.id.soilTemp_add);
        soilTemp_add.setOnClickListener(this);
        soilTemp_sub =findViewById(R.id.soilTemp_sub);
        soilTemp_sub.setOnClickListener(this);
        co2_sub =findViewById(R.id.co2_sub);
        co2_sub.setOnClickListener(this);
        co2_add =findViewById(R.id.co2_add);
        co2_add.setOnClickListener(this);
        humidity_add =findViewById(R.id.humidity_add);
        humidity_add.setOnClickListener(this);
        humidity_sub =findViewById(R.id.humidity_sub);
        humidity_sub.setOnClickListener(this);
        soilHumidity_add =findViewById(R.id.soilHumidity_add);
        soilHumidity_add.setOnClickListener(this);
        soilHumidity_sub =findViewById(R.id.soilHumidity_sub);
        soilHumidity_sub.setOnClickListener(this);
        light_add =findViewById(R.id.light_add);
        light_add.setOnClickListener(this);
        light_sub =findViewById(R.id.light_sub);
        light_sub.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.temp_add:
                temp_data = temp_data+1;
                temp.setText("空气温度: "+temp_data+"℃");
                senseDate.setTemp(temp_data);
                EventBus.getDefault().post(senseDate);
                break;
            case R.id.temp_sub:
                temp_data = temp_data-1;
                temp.setText("空气温度: "+temp_data+"℃");
                senseDate.setTemp(temp_data);
                EventBus.getDefault().post(senseDate);
                break;
            case R.id.soilTemp_add:
                soilTemp_data =soilTemp_data+1;
                soilTemp.setText("土壤温度: "+soilTemp_data+"℃");
                break;
            case R.id.soilTemp_sub:
                soilTemp_data =soilTemp_data-1;
                soilTemp.setText("土壤温度: "+soilTemp_data+"℃");
                break;
            case R.id.light_add:
                light_data = light_data+1;
                light.setText("光照强度： "+light_data+"lux");
                break;
            case R.id.light_sub:
                light_data = light_data-1;
                light.setText("光照强度： "+light_data+"lux");
                break;
            case R.id.humidity_add:
                humidity_data = humidity_data+1;
                humidity.setText("空气湿度: "+ humidity_data+"％");
                senseDate.setHumidity(humidity_data);
                EventBus.getDefault().post(senseDate);
                break;
            case R.id.humidity_sub:
                humidity_data = humidity_data-1;
                humidity.setText("空气湿度: "+ humidity_data+"％");
                senseDate.setHumidity(humidity_data);
                EventBus.getDefault().post(senseDate);
                break;
            case R.id.soilHumidity_add:
                soilHumidity_data = soilHumidity_data+1;
                soilHumidity.setText("土壤湿度: "+soilHumidity_data+"％");
                break;
            case R.id.soilHumidity_sub:
                soilHumidity_data = soilHumidity_data-1;
                soilHumidity.setText("土壤湿度: "+soilHumidity_data+"％");
                break;
            case R.id.co2_add:
                co2_data = co2_data+1;
                co2.setText("co2: "+co2_data+"ppm");
                break;
            case R.id.co2_sub:
                co2_data = co2_data-1;
                co2.setText("co2: "+co2_data+"ppm");
                break;
        }
    }
}
