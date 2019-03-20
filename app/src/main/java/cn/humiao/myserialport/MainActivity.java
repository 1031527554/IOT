package cn.humiao.myserialport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private Button button,button2;
    private TextView tv1,tv2;
    private SerialPortUtil serialPortUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn1);
        button2 = findViewById(R.id.bt2);
        tv1 =  findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialPortUtil = new SerialPortUtil();
                serialPortUtil.openSerialPort();
            }
        });

        //注册EventBus
        EventBus.getDefault().register(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialPortUtil.sendSerialPort(Cmd.OPEN_DOOR);
            }
        });
    }

    /**
     * 用EventBus进行线程间通信，也可以使用Handler
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(String string){
        String signal,message;
        Log.d(TAG,"获取到了从传感器发送到Android主板的串口数据");
        Date1 dateCollation = new Date1();
        dateCollation.setDate(string);
        dateCollation.collation();
        signal = dateCollation.getSignal();
        message = dateCollation.getDate();
        tv1.setText(signal);
        switch (signal){
            case "02":
                tv2.setText(message);
                break;
            case "0A":
                tv2.setText(message);
                break;
        }
    }
}
