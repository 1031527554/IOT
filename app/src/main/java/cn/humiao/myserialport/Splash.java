package cn.humiao.myserialport;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.hoho.android.usbserial.BuildConfig;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;

import java.util.ArrayList;
import java.util.List;

public class Splash extends AppCompatActivity {

    private static final int WHAT_DELAY = 0x11;// 启动页的延时跳转
    private static final int DELAY_TIME = 3000;// 延时时间
    private Button button_skip;
    private UsbSerialPort sUsbPort = null;

    // 创建Handler对象，处理接收的消息
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_DELAY:// 延时3秒跳转
                    goHome();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

       // 权限获取
   /*     UsbManager usbManager = (UsbManager)getApplicationContext().getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> usbSerialDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        //全部端口
        List<UsbSerialPort> usbSerialPorts = new ArrayList<UsbSerialPort>();
        for (UsbSerialDriver driver : usbSerialDrivers) {
            List<UsbSerialPort> ports = driver.getPorts();
            usbSerialPorts.addAll(ports);
        }
        for (UsbSerialPort port : usbSerialPorts) {
            sUsbPort = port;
        }

        UsbDeviceConnection usbConnection = usbManager.openDevice(sUsbPort.getDriver().getDevice());
        if(usbConnection == null ) {
            if (!usbManager.hasPermission(sUsbPort.getDriver().getDevice())) {
                PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(BuildConfig.APPLICATION_ID + ".GRANT_USB"), 0);
                usbManager.requestPermission(sUsbPort.getDriver().getDevice(), usbPermissionIntent);
            }
        }*/



        //延时

        handler.sendEmptyMessageDelayed(WHAT_DELAY, DELAY_TIME);

        button_skip = findViewById(R.id.skip);
        button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });
    }


    private void goHome() {
        startActivity(new Intent(Splash.this, Rfid.class));
        finish();// 销毁当前活动界面
    }
}
