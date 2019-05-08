package android_usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.concurrent.Executors;

import cn.humiao.myserialport.DataUtils;

public class SerialSocket implements SerialInputOutputManager.Listener {

    private static final int WRITE_WAIT_MILLIS = 2000; // 0 blocked infinitely on unprogrammed arduino

    private final BroadcastReceiver disconnectBroadcastReceiver;

    private Context context ;
    private UsbDeviceConnection connection;
    private UsbSerialPort serialPort;
    private SerialInputOutputManager ioManager;
    private String readDate = "";
    private StringBuilder sb = new StringBuilder();
    private int a = -1;

    SerialSocket() {
        disconnectBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                disconnect(); // disconnect now, else would be queued until UI re-attached
            }
        };
    }

    void connect(Context context,UsbDeviceConnection connection, UsbSerialPort serialPort, int baudRate) throws IOException {
        if(this.serialPort != null)
            throw new IOException("already connected");
        this.connection = connection;
        this.context=context;
        this.serialPort = serialPort;
        serialPort.open(connection);
        serialPort.setParameters(baudRate, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);   //打开串口
        serialPort.setDTR(true); // for arduino, ...
        serialPort.setRTS(true);

        ioManager = new SerialInputOutputManager(serialPort, this);
        Executors.newSingleThreadExecutor().submit(ioManager);
    }

    void disconnect() {
        if (ioManager != null) {
            ioManager.setListener(null);
            ioManager.stop();
            ioManager = null;
        }
        if (serialPort != null) {
            try {
                serialPort.setDTR(false);
                serialPort.setRTS(false);
            } catch (Exception ignored) {
            }
            try {
                serialPort.close();
            } catch (Exception ignored) {
            }
            serialPort = null;
        }
        if(connection != null) {
            connection.close();
            connection = null;
        }
        try {
            context.unregisterReceiver(disconnectBroadcastReceiver);
        } catch (Exception ignored) {
        }
    }

    void write(byte[] data) throws IOException {
        if(serialPort == null)
            throw new IOException("not connected");
        serialPort.write(data,WRITE_WAIT_MILLIS);
    }

    String read(){
        return readDate;
    }

    void test(byte[] date){
        if(readDate.length() >= 28){
            readDate ="";
        }
        else {
            readDate = readDate+DataUtils.ByteArrToHex(date);
        }

    }
    @Override
    public void onNewData(byte[] data) {
        test(data);
    }

    @Override
    public void onRunError(Exception e) {

    }
}
