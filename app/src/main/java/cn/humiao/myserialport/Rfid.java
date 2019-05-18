package cn.humiao.myserialport;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import android_usb.Serial;

public class Rfid extends AppCompatActivity {
    private DBManager dbManager;
    private int userID =3;
    private Serial serial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfid);

        dbManager = new DBManager(this);
        dbManager.openDatabase();



        serial = new Serial(Rfid.this, "",9600);
        serial.send("AA");
        //注册 evenbus
        EventBus.getDefault().register(this);


        ImageButton button = findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goHome();
                SQLiteDatabase db = dbManager.getDatabase();
                Cursor cursor =  db.rawQuery("SELECT * FROM person WHERE cardID = ?",
                        new String[]{"0100B24AB54C"});

                if(cursor.moveToFirst())
                {
                    userID = cursor.getInt(cursor.getColumnIndex("ID"));
                    goHome();
                }

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(String string){
        Log.d("MainActivity","获取到了从传感器发送到Android主板的串口数据");
        System.out.println(string);
        SQLiteDatabase db = dbManager.getDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM person WHERE cardID = ?",
                new String[]{string});

        if(cursor.moveToFirst())
        {
            userID = cursor.getInt(cursor.getColumnIndex("ID"));
            dbManager.closeDatabase();
            goHome();
        }

    }

    private void goHome() {
        serial.disconnect();
        EventBus.getDefault().unregister(this);
        Intent intent =  new Intent(Rfid.this,MainActivity.class);
        intent.putExtra("key",userID);
        startActivity(intent);
        finish();// 销毁当前活动界面
    }


    //解除注册
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
