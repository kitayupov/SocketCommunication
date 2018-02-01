package com.skbnt.socketconnection;

import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

class SocketServer extends Thread {

    private Context context;
    private String hostAddress;

    SocketServer(Context context) {
        this.context = context;
    }

    String getHostAddress() throws UnknownHostException {
        if (hostAddress != null) {
            return hostAddress;
        } else {
            throw new UnknownHostException();
        }
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            while (true) {
                Socket socket = serverSocket.accept();
                hostAddress = socket.getInetAddress().getHostAddress();
                InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);

                String message = reader.readLine();
                context.sendBroadcast(new Intent(SocketConnection.INTENT_FILTER).
                        putExtra(SocketConnection.STRING_EXTRA, message));

                new SocketServerReplyThread(socket, message).run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SocketServerReplyThread extends Thread {

        private Socket socket;
        private String message;

        SocketServerReplyThread(Socket socket, String message) {
            this.socket = socket;
            this.message = message;
        }

        @Override
        public void run() {
            assert socket != null;
            try (OutputStream outputStream = socket.getOutputStream();
                 PrintStream printStream = new PrintStream(outputStream)) {
                printStream.print(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
