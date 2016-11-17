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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.net.DatagramSocket;

import static com.jkokosa.oscixer.MainActivity.EXTRA_MESSAGE;

public class ControlActivity extends AppCompatActivity {
    static private DawController controller;
    static private DatagramSocket sock;
    private final Messenger mMessenger = new Messenger(new ResponseHandler(this));
    private TextView textView;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private boolean mBound = false;
    private int strip;
    private String name;
    private float fader;
    private float mute;
    private float solo;
    private String comment;
    private float solo_iso;
    private float solo_safe;
    private float polarity;
    private float monitor_input;
    private float monitor_disk;
    private float rec_enable;
    private float rec_safe;
    private float expanded;
    private float trim;
    private float pan_stereo_position;
    private float pan_stereo_width;
    private float num_inputs;
    private float num_outputs;

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

        //setContentView(R.layout.control_surface);
        setContentView(R.layout.control_surface);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        String msg = intent.getStringExtra(EXTRA_MESSAGE);

        textView = (TextView) findViewById(R.id.feedback_text);
        textView.setText(msg);

        controller = new DawController();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String target_host = sharedPref.getString("target_host", "192.168.0.16");
        String target_port = sharedPref.getString("target_port", "3819");

        int port = 3819;

        try {
            port = Integer.parseInt(target_port);
        } catch (Exception e) {
            // TODO: update port in the shared prefs to be default value
            port = 3819;
        } finally {
            sock = controller.attachPorts(target_host, port);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBound) {
            Intent intent = new Intent(this, CixListener.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mBound = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, CixListener.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void onClicks(View view) {
        switch (view.getId()) {
            case R.id.transport_play:
                controller.startTransport();
                break;
            case R.id.transport_stop:
                controller.stopTransport();
                break;
            case R.id.transport_home:
                controller.goHome();
                break;
            case R.id.transport_end:
                controller.goEnd();
                break;
            case R.id.transport_prev_mark:
                controller.prevMark();
                break;
            case R.id.transport_next_mark:
                Log.v("Click", "Before");
                controller.selectTrack(3);
                Log.v("Click", "After");
                //controller.moveFader(3, -6.6f);
                //controller.nextMark();
                break;
            case R.id.transport_loop:
                controller.toggle_loop();
                break;
        }
    }

    class ResponseHandler extends Handler {
        final ControlActivity activity;

        ResponseHandler(ControlActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case CixListener.FB_GAIN:
                    strip = message.getData().getInt("strip", 0);
                    fader = message.getData().getFloat("gain", -999.0f);
                    name = message.getData().getString("name", "not found");
                    activity.textView.setText(String.format("Strip %d-%s = %f", strip, name, fader));
                    break;
                case CixListener.FB_SELECT:
                    strip = message.getData().getInt("strip", 0);
                    name = message.getData().getString("name", "not found");
                    activity.textView.setText(String.format("Strip %d-%s selected", strip, name));
                    break;
                case CixListener.FB_STRIP:
                    strip = message.getData().getInt(FeedbackTracker.CS_ID, 0);
                    name = message.getData().getString(FeedbackTracker.CS_NAME, "not found");
                    comment = message.getData().getString(FeedbackTracker.CS_COMMENT, "");
                    mute = message.getData().getFloat(FeedbackTracker.CS_MUTE, 0.0f);
                    solo = message.getData().getFloat(FeedbackTracker.CS_SOLO, 0.0f);
                    fader = message.getData().getFloat(FeedbackTracker.CS_FADER, 0.0f);
                    trim = message.getData().getFloat(FeedbackTracker.CS_TRIM, 0.0f);
                    solo_iso = message.getData().getFloat(FeedbackTracker.CS_SOLO_ISO, 0.0f);
                    solo_safe = message.getData().getFloat(FeedbackTracker.CS_SOLO_SAFE, 0.0f);
                    polarity = message.getData().getFloat(FeedbackTracker.CS_POLARITY, 0.0f);
                    monitor_input = message.getData().getFloat(FeedbackTracker.CS_MONITOR_INPUT, 0.0f);
                    monitor_disk = message.getData().getFloat(FeedbackTracker.CS_MONITOR_DISK, 0.0f);
                    rec_enable = message.getData().getFloat(FeedbackTracker.CS_RECENABLE, 0.0f);
                    rec_safe = message.getData().getFloat(FeedbackTracker.CS_RECSAFE, 0.0f);
                    // TODO: findout what expanded is??
                    expanded = message.getData().getFloat(FeedbackTracker.CS_EXPANDED, 0.0f);
                    pan_stereo_position = message.getData().getFloat(FeedbackTracker.CS_PAN_STERO_POSITION, 0.0f);
                    pan_stereo_width = message.getData().getFloat(FeedbackTracker.CS_PAN_STERO_WIDTH, 0.0f);
                    num_inputs = message.getData().getFloat(FeedbackTracker.CS_NUM_INPUTS, 0.0f);
                    num_outputs = message.getData().getFloat(FeedbackTracker.CS_NUM_OUTPUTS, 0.0f);

                    String state = String.format("Id = %d\tName = %s\nMute = %f\tTrim = %f\nFader = %f\tComment = '%s'\n" +
                                    "Solo = %f\tSolo_Iso = %f\nSolo_Safe = %f\tPolarity = %f\nMonitor_input = %f\t" +
                                    "Monitor_disk = %f\nRec_enable = %f\tRec_Safe = %f\nExpanded = %f\tPan_Position = %f\n" +
                                    "Pan_Width = %f\tNum_Inputs = %f\nNum_Outputs = %f\n",
                            strip, name, mute, trim, fader, comment,
                            solo, solo_iso, solo_safe, polarity, monitor_input,
                            monitor_disk, rec_enable, rec_safe, expanded, pan_stereo_position, pan_stereo_width,
                            num_inputs, num_outputs);
                    activity.textView.setText(state);
                    break;
            }
        }
    }
}