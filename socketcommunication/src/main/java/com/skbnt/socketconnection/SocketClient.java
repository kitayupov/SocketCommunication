package com.skbnt.socketconnection;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class SocketClient extends AsyncTask<String, Void, String> {

    private String destinationIp;

    SocketClient(String destinationIp) {
        this.destinationIp = destinationIp;
    }

    @Override
    protected String doInBackground(String... strings) {
        try (Socket socket = new Socket(destinationIp, 1234);
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
             InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
             BufferedReader reader = new BufferedReader(streamReader)) {
            outputStream.writeBytes(strings[0] + "\n");
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
