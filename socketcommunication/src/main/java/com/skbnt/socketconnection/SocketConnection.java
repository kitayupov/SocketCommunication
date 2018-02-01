package com.skbnt.socketconnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class SocketConnection {

    static final String INTENT_FILTER = "IntentFilter";
    static final String STRING_EXTRA = "StringExtra";

    private SocketServer socketServer;
    private static boolean isServer;

    private OnResponseCallback onResponseCallback;

    private Context context;

    private SocketConnection(Context context) {
        this.context = context;
        if (socketServer == null) {
            socketServer = new SocketServer(context);
            socketServer.start();
        }
        context.registerReceiver(receiver, new IntentFilter(INTENT_FILTER));
    }

    public interface OnResponseCallback {
        void onResponse(String response);
    }

    public void registerCallback(OnResponseCallback callback) {
        this.onResponseCallback = callback;
    }

    public static SocketConnection createServer(Context context) {
        isServer = true;
        return new SocketConnection(context);
    }

    public static SocketConnection createClient(Context context) {
        isServer = false;
        return new SocketConnection(context);
    }

    public void sendCommand(final String command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SocketClient socketClient = new SocketClient(getAddress());
                    socketClient.execute(command);
                    if (onResponseCallback != null) {
                        onResponseCallback.onResponse(socketClient.get());
                    }
                } catch (UnknownHostException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String getAddress() throws UnknownHostException {
        if (isServer) {
            if (socketServer != null) {
                return socketServer.getHostAddress();
            } else {
                throw new UnknownHostException();
            }
        } else {
            return "192.168.1.57";
        }
    }

    public void close() {
        if (socketServer != null) {
            socketServer.interrupt();
            socketServer = null;
        }
        context.unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (onResponseCallback != null) {
                onResponseCallback.onResponse(intent.getStringExtra(STRING_EXTRA));
            }
        }
    };
}
