package android_usb;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.widget.Toast;
import com.hoho.android.usbserial.BuildConfig;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.humiao.myserialport.DataUtils;

public  class Serial  {
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;

    private UsbSerialPort sUsbPort = null;
    private enum Connected { False, Pending, True }
    private Context mContext;
    public static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    private String TEMPERATURE_USB_VENDOR_ID="0403";
    private String TEMPERATURE_USB_PRODUCT_ID ="6001";
    private int deviceId, portNum, baudRate=9600;
    private String  receiveText ;
    private SerialSocket socket;
    private Connected connected = Connected.False;
    private String signal;

    public Serial(Context context ,String TEMPERATURE_USB_VENDOR_ID , String TEMPERATURE_USB_PRODUCT_ID,int baudRate) {
        this.TEMPERATURE_USB_VENDOR_ID  =TEMPERATURE_USB_VENDOR_ID;
        this.TEMPERATURE_USB_PRODUCT_ID = TEMPERATURE_USB_PRODUCT_ID;
        mContext = context;
        this.baudRate = baudRate;

        connect();
    }

    public Serial(Context context ,String signal,int baudRate){
        mContext = context;
        this.signal = signal;
        this.baudRate = baudRate;
        connect();
    }

    private void connect() {

        UsbManager usbManager = (UsbManager)mContext.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> usbSerialDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        //全部端口
        List<UsbSerialPort> usbSerialPorts = new ArrayList<UsbSerialPort>();
        for (UsbSerialDriver driver : usbSerialDrivers) {
            List<UsbSerialPort> ports = driver.getPorts();
            usbSerialPorts.addAll(ports);
        }
        String vendorId;
        String productId;

        for (UsbSerialPort port : usbSerialPorts) {
            UsbSerialDriver driver = port.getDriver();
            UsbDevice device = driver.getDevice();
            vendorId = HexDump.toHexString((short) device.getVendorId());
            productId = HexDump.toHexString((short) device.getProductId());
            if (vendorId.equals(TEMPERATURE_USB_VENDOR_ID) && productId.equals(TEMPERATURE_USB_PRODUCT_ID)) {
                sUsbPort = port;

            }
        }
        UsbDeviceConnection usbConnection = usbManager.openDevice(sUsbPort.getDriver().getDevice());
        if(usbConnection == null ) {
            if (!usbManager.hasPermission(sUsbPort.getDriver().getDevice())) {
                PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
                usbManager.requestPermission(sUsbPort.getDriver().getDevice(), usbPermissionIntent);
                return;
            }
        }
        if(usbConnection == null) {
            if (!usbManager.hasPermission(sUsbPort.getDriver().getDevice()))
                System.out.println("connection failed: permission denied");
            else
                System.out.println("connection failed: open failed");
            return;
        }

        connected = Connected.True;
        try {
            socket = new SerialSocket();
            socket.connect(mContext,usbConnection,sUsbPort, baudRate);
            // usb connect is not asynchronous. connect-success and connect-error are returned immediately from socket.connect
            // for consistency to bluetooth/bluetooth-LE app use same SerialListener and SerialService classes
            onSerialConnect();

           // mSerialIoManager = new SerialInputOutputManager(sUsbPort, socket);
           // mExecutor.submit(mSerialIoManager);


            ReceiveThread receiveThread = new ReceiveThread();
            receiveThread.start();
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    public void disconnect() {
        connected = Connected.False;
        socket.disconnect();
        socket = null;
    }

    public void send(String str) {
        if(connected != Connected.True) {
            Toast.makeText(mContext, "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            byte[] data = DataUtils.HexToByteArr(str);
            socket.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }


    public void onSerialConnect() {
        System.out.print("connected");
        connected = Connected.True;
    }

    public void onSerialConnectError(Exception e) {
        System.out.print("connection failed: " + e.getMessage());
        disconnect();
    }


    public void onSerialIoError(Exception e) {
        System.out.print("connection lost: " + e.getMessage());
        disconnect();
    }

    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            super.run();
            //条件判断，只要条件为true，则一直执行这个线程
            while (connected == Connected.True) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    receiveText=signal+socket.read();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                EventBus.getDefault().post(receiveText);

            }


        }
    }

}
