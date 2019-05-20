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
    private ImageButton imageBt1,imageBt2,imageBt3,imageBt4,imageBt5,imageBt6;
    private TextView tv1,tv2,tvname,tv4,tvT,tvH,time3,time2,time1,tvL;
    private SerialPortUtil serialPortUtil;
    private int userID;
    private int t_signal = 1;
    private Handler handler;
    private DBManager dbManager;
    private SenseDate senseDate = new SenseDate();
    private SenseDate stateDate = new SenseDate();
    private Websocket websocket = new Websocket();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userID = intent.getIntExtra("key",0);  //得到用户ID

        websocket.conect();
        websocket.sendMessage("wwwwwwwwwwwwwwwwwwww");



        //初始化状态数据
        stateDate.setTemp(30);
        stateDate.setHumidity(60);

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
        imageBt4 = findViewById(R.id.imageBt4);
        imageBt4.setOnClickListener(this);
        imageBt5 = findViewById(R.id.imageBt5);
        imageBt5.setOnClickListener(this);
        imageBt6 = findViewById(R.id.imageBt6);
        imageBt6.setOnClickListener(this);

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
        tvL =findViewById(R.id.tvL);

        tvname = findViewById(R.id.name);
        tvname.setText(name);

        time1 = findViewById(R.id.time1);
        time2 = findViewById(R.id.time2);
        time3 = findViewById(R.id.time3);

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
        time();
        initViews();//  绘制界面


        //注册EventBus
        EventBus.getDefault().register(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt2 :
                serialPortUtil = new SerialPortUtil();
                serialPortUtil.openSerialPort();
                serial = new Serial(context,"!",115200);
                break;
            case R.id.bt3:
                serialPortUtil.closeSerialPort();
                serial.disconnect();
                break;
            case R.id.btn1:
                serial.send("AA");
                break;
            case R.id.imageBt1:
                startActivity(new Intent(MainActivity.this,ModeActivity.class));
                break;
            case R.id.imageBt2:
                startActivity(new Intent(MainActivity.this, WoringActivity.class));
                break;
            case R.id.imageBt3:
                Intent intent =  new Intent(MainActivity.this, StateActivity.class);
                intent.putExtra("temp",stateDate.getTemp());
                intent.putExtra("humidity",stateDate.getHumidity());
                startActivity(intent);
                break;
            case R.id.imageBt4:
                startActivity(new Intent(MainActivity.this, HistaryDateActivity.class));
                break;
            case R.id.imageBt5:
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
                break;
            case R.id.imageBt6:
                startActivity(new Intent(MainActivity.this, SolveActivity.class));
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
        db.execSQL("UPDATE person SET enterTime = ? WHERE ID = ?",
                new String[]{simpleDateFormat.format(date), String.valueOf(userID)});
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


    private void saveDate(){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (t_signal == 1){
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("HH:mm/MM-dd");
                    Date date = new Date(System.currentTimeMillis());
                    dbManager.openDatabase();
                    SQLiteDatabase db = dbManager.getDatabase();
                    db.execSQL("INSERT INTO sense(temps,humidity,light,co2,time) values(?,?,?,?,?)",
                            new String[]{String.valueOf(senseDate.getTemp()), String.valueOf(senseDate.getHumidity()), String.valueOf(senseDate.getLight()), String.valueOf(senseDate.getCo2()),simpleDateFormat.format(date)});

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
        ImageView imageDT = findViewById(R.id.imgDT);
        ImageView imageDH = findViewById(R.id.imgDH);



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
        Glide.with(this)
                .load(R.mipmap.dishi)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))//圆角半径
                .into(imageDH);
        Glide.with(this)
                .load(R.mipmap.diwen)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))//圆角半径
                .into(imageDT);
    }



    /**
     * 用EventBus进行线程间通信，也可以使用Handler
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SenseDate senseDate){
        stateDate =senseDate;
        System.out.println("111111111111111111111111111111111111");
    }

    public void onEventMainThread(String string){
        String signal,message;
        Log.d(TAG,"获取到了从传感器发送到Android主板的串口数据");
        tv4.setText(string);
        Data dateCollation = new Data();
        dateCollation.setDate(string);
        dateCollation.collation();
        signal = dateCollation.getSignal();
        message = dateCollation.getDate();
        tv1.setText(signal);
        switch (signal){
            case "!":
                tv2.setText(message);
            case "02":
                if (dateCollation.light()==1){
                    tvL.setText("有光");
                    serial.send(Cmd.left);
                }else {
                    tvL.setText("无光");
                }
                senseDate.setLight(dateCollation.light());
                break;
            case "0A":
                tv2.setText(message);
                tvT.setText(dateCollation.temperature());
                senseDate.setTemp(Double.parseDouble(dateCollation.temperature()));
                if(Double.valueOf(dateCollation.temperature()) > stateDate.getTemp()){

                }
                tvH.setText(dateCollation.humidity());
                senseDate.setHumidity(Double.parseDouble(dateCollation.humidity()));
                if (Double.valueOf(dateCollation.humidity()) > stateDate.getHumidity()){

                }
                break;
            case "OD":
                tv2.setText( dateCollation.light());
                if (dateCollation.light()==1){
                    tvL.setText("正常");
                }else {
                    tvL.setText("超标");
                }
                senseDate.setCo2(dateCollation.light());
        }
    }

}
