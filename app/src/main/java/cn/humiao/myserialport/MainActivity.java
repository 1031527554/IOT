package cn.humiao.myserialport;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

import android_usb.Serial;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Serial serial ;
    private String TAG = "MainActivity";
    private Button button,openButton,closeButton;
    private ImageButton imageBt1,imageBt2,imageBt3;
    private TextView tv1,tv2,tvname,tv4,tvT,tvH,time3,time2,time1;
    private SerialPortUtil serialPortUtil;
    private int userID;
    private int t_signal = 1;
    private Handler handler;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userID = intent.getIntExtra("key",0);

        dbManager = new DBManager(this);
        dbManager.openDatabase();
        SQLiteDatabase db = dbManager.getDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM person WHERE ID = ?",
                new String[]{String.valueOf(userID)});
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex("name"));
        dbManager.closeDatabase();

        context = getApplicationContext();

        imageBt1 = findViewById(R.id.imageBt1);
        imageBt1.setOnClickListener(this);
        imageBt2 = findViewById(R.id.imageBt2);
        imageBt2.setOnClickListener(this);
        imageBt3 = findViewById(R.id.imageBt3);
        imageBt3.setOnClickListener(this);

        button = findViewById(R.id.btn1);
        button.setOnClickListener(this);
        openButton = findViewById(R.id.bt2);
        openButton.setOnClickListener(this);
        closeButton = findViewById(R.id.bt3);
        closeButton.setOnClickListener(this);

        tv1 =  findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv4 = findViewById(R.id.tv4);

        tvT = findViewById(R.id.tvT);    //温度
        tvH = findViewById(R.id.tvH);    //湿度

        tvname = findViewById(R.id.name);
        tvname.setText(name);

        time1 = findViewById(R.id.time1);
        time2 = findViewById(R.id.time2);
        time3 = findViewById(R.id.time3);
        time();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if (msg.what ==1){
                    SimpleDateFormat simpleDateFormat1 =new SimpleDateFormat("HH:mm");
                    SimpleDateFormat simpleDateFormat2 =new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date(System.currentTimeMillis());
                    time1.setText(simpleDateFormat1.format(date));
                    time2.setText(simpleDateFormat2.format(date));
                }
            }
        };

        initViews();//  绘制界面


        //注册EventBus
        EventBus.getDefault().register(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt2 :
                // serialPortUtil = new SerialPortUtil();
                // serialPortUtil.openSerialPort();
                serial = new Serial(context,"!",115200);
                break;
            case R.id.bt3:
                //serialPortUtil.closeSerialPort();
                serial.disconnect();
                break;
            case R.id.btn1:
                // serialPortUtil.sendSerialPort(Cmd.left);
                serial.send("AA");
                break;
            case R.id.imageBt1:

                break;
            case R.id.imageBt2:

                break;
            case R.id.imageBt3:

                break;
            default:
                break;
        }

    }

    private void time(){
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("HH:mm/MM-dd");
        Date date = new Date(System.currentTimeMillis());
        time3.setText(simpleDateFormat.format(date));
        dbManager.openDatabase();
        SQLiteDatabase db = dbManager.getDatabase();
        db.execSQL("INSERT INTO person(enterTime) values(?)",
                new String[]{simpleDateFormat.format(date)});
        dbManager.closeDatabase();

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (t_signal == 1){
                    handler.sendEmptyMessage(1);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }



    private void initViews() {
        ImageView image1 = findViewById(R.id.img1);
        ImageView imageT = findViewById(R.id.imgT);
        ImageView imageH = findViewById(R.id.imgH);
        ImageView imageC = findViewById(R.id.imgL);
        ImageView imageL = findViewById(R.id.imgC);



        Glide.with(this)
                .load(R.mipmap.picture)  //将来使用网络图片
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(image1);

        Glide.with(this)
                .load(R.mipmap.wendu)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))//圆角半径
                .into(imageT);

        Glide.with(this)
                .load(R.mipmap.shidu)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))//圆角半径
                .into(imageH);

        Glide.with(this)
                .load(R.mipmap.guangzhao)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))//圆角半径
                .into(imageL);
        Glide.with(this)
                .load(R.mipmap.eryanghuatan)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))//圆角半径
                .into(imageC);



    }



    /**
     * 用EventBus进行线程间通信，也可以使用Handler
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(String string){
        String signal,message;
        Log.d(TAG,"获取到了从传感器发送到Android主板的串口数据");
        tv4.setText(string);
        Date1 dateCollation = new Date1();
        dateCollation.setDate(string);
        dateCollation.collation();
        signal = dateCollation.getSignal();
        message = dateCollation.getDate();
        tv1.setText(signal);
        switch (signal){
            case "!":
                tv2.setText(message);
            case "02":
                tv2.setText(message);
                break;
            case "0A":
                tv2.setText(message);
                tvT.setText(dateCollation.temperature());
                tvH.setText(dateCollation.humidity());
                break;
        }
    }

}
