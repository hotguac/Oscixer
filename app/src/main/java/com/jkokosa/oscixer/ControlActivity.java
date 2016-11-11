package com.jkokosa.oscixer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.DatagramSocket;

import static com.jkokosa.oscixer.MainActivity.EXTRA_MESSAGE;

public class ControlActivity extends AppCompatActivity {
    /* These two variables hold the IP address and port number.
     * You should change them to the appropriate address and port.
     */
    static private CixListener listener;
    static private DawController controller;
    static private DatagramSocket sock;

    public TextView textView;
    /**
     * Messenger for communicating with service.
     */
    boolean mBound = false;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CixListener.LocalBinder binder = (CixListener.LocalBinder) service;
            listener = binder.getService();
            mBound = true;
            listener.setSocket(sock);
            listener.attachListeners();
            controller.connectSurface();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            listener.unbindService(mConnection);
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String msg = intent.getStringExtra(EXTRA_MESSAGE);
        textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(msg);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);

        controller = new DawController();
        sock = controller.attachPorts("192.168.0.16", 3819);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, CixListener.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

}