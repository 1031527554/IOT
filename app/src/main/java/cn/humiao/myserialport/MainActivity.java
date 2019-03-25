package cn.humiao.myserialport;

import android.content.Intent;
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
    private Button button,openButton,closeButton,modeButton;
    private TextView tv1,tv2,tv3;
    private SerialPortUtil serialPortUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn1);
        openButton = findViewById(R.id.bt2);
        closeButton = findViewById(R.id.bt3);
        modeButton = findViewById(R.id.bt4);
        tv1 =  findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                        //测试
                serialPortUtil.sendSerialPort(Cmd.left);
            }
        });

        openButton.setOnClickListener(new View.OnClickListener() {    //打开串口
            @Override
            public void onClick(View v) {
                serialPortUtil = new SerialPortUtil();
                serialPortUtil.openSerialPort();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {   //关闭串口
            @Override
            public void onClick(View v) {
                serialPortUtil.closeSerialPort();
            }
        });

        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,ModeActivity.class);
                startActivityForResult(intent,1);
            }

            protected  void  onActivityResult(int requestCode,int returnCode,Intent date){
                MainActivity.super.onActivityResult(requestCode,returnCode,date);
                if(requestCode==1&&returnCode==2) {
                    String content = date.getStringExtra("date");
                    tv3.setText(content);
                }
            }
        });

        //注册EventBus
        EventBus.getDefault().register(this);

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
