package cn.humiao.myserialport;

import com.zhangke.websocket.SimpleListener;
import com.zhangke.websocket.SocketListener;
import com.zhangke.websocket.WebSocketHandler;
import com.zhangke.websocket.WebSocketManager;
import com.zhangke.websocket.WebSocketSetting;
import com.zhangke.websocket.response.ErrorResponse;

public class Websocket {
    private WebSocketSetting setting = new WebSocketSetting();
    private WebSocketManager manager ;
    private SocketListener socketListener = new SimpleListener() {
        @Override
        public void onConnected() {
            System.out.println("连接成功！！！！！！！！！！！");
        }

        @Override
        public void onConnectFailed(Throwable e) {
            super.onConnectFailed(e);
        }

        @Override
        public void onDisconnect() {
            System.out.println("断开连接！！！！！！！！！！！");
        }

        @Override
        public <T> void onMessage(String message, T data) {
            System.out.println(message);
        }

        @Override
        public void onSendDataError(ErrorResponse errorResponse) {
            super.onSendDataError(errorResponse);
        }
    };

    public void conect(){
        setting.setConnectUrl("ws://121.40.165.18:8800");
        setting.setConnectionLostTimeout(60);
        setting.setConnectTimeout(15 * 1000);
        setting.setReconnectWithNetworkChanged(true);
        manager = WebSocketHandler.init(setting);
        manager.start();
        manager.addListener(socketListener);
    }
    public void sendMessage(String data){
        manager.send(data);
    }
}
