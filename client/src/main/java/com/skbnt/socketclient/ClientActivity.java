package com.skbnt.socketclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.skbnt.socketconnection.SocketConnection;

public class ClientActivity extends AppCompatActivity implements View.OnClickListener {

    private SocketConnection client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        findViewById(R.id.buttonOk).setOnClickListener(this);
        findViewById(R.id.buttonWait).setOnClickListener(this);
        findViewById(R.id.buttonFail).setOnClickListener(this);

        client = SocketConnection.createClient(this);
        client.registerCallback(new SocketConnection.OnResponseCallback() {
            @Override
            public void onResponse(String response) {
                printResponse(response);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        client.close();
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button) {
            client.sendCommand(((Button) view).getText().toString());
        }
    }

    private void printResponse(final String response) {
        if (response != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.append(response);
                    textView.append("   ");

                    final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            });
        }
    }
}
