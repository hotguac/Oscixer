package com.jkokosa.oscixer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.DatagramSocket;

import static com.jkokosa.oscixer.MainActivity.EXTRA_MESSAGE;

public class ControlActivity extends AppCompatActivity {
    static private DawController controller;
    static private DatagramSocket sock;
    public TextView textView;
    private Messenger mMessenger = new Messenger(new ResponseHandler(this));
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            Messenger mService = new Messenger(service);

            try {
                Message msg = Message.obtain(null, CixListener.MSG_REGISTER);
                msg.replyTo = mMessenger;
                msg.obj = sock;

                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            controller.connectSurface();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String target_host = sharedPref.getString("target_host", "192.168.0.16");
        String target_port = sharedPref.getString("target_port", "3819");

        int port = 3819;

        try {
            port = Integer.parseInt(target_port);
        } catch (Exception e) {
            // TODO: update port in the shared prefs to be default value
            //EditTextPreference editText = (EditTextPreference) manager.findPreference("target_host");
            //editText.setSummary(editText.getText());
            port = 3819;
        } finally {
            sock = controller.attachPorts(target_host, port);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, CixListener.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    static class ResponseHandler extends Handler {
        ControlActivity activity;

        ResponseHandler(ControlActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case CixListener.FB_GAIN:
                    int strip = message.getData().getInt("strip", 0);
                    float gain = message.getData().getFloat("gain", -999.0f);
                    activity.textView.setText(String.format("Strip %d = %f", strip, gain));
                    break;
            }
        }
    }
}