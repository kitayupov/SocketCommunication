package com.skbnt.socketserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.skbnt.socketconnection.SocketConnection;

public class ServerActivity extends AppCompatActivity implements View.OnClickListener {

    private SocketConnection server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        findViewById(R.id.buttonOk).setOnClickListener(this);
        findViewById(R.id.buttonWait).setOnClickListener(this);
        findViewById(R.id.buttonFail).setOnClickListener(this);

        server = SocketConnection.createServer(this);
        server.registerCallback(new SocketConnection.OnResponseCallback() {
            @Override
            public void onResponse(String response) {
                printResponse(response);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        server.close();
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button) {
            server.sendCommand(((Button) view).getText().toString());
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
