package com.joekokosa.oscixer;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.net.DatagramSocket;

import static com.joekokosa.oscixer.MainActivity.EXTRA_MESSAGE;

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
    private int selected_strip;
    private float rec_enable;

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
        setContentView(com.joekokosa.oscixer.R.layout.control_surface);

        Toolbar myToolbar = (Toolbar) findViewById(com.joekokosa.oscixer.R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //myToolbar.inflateMenu(R.menu.cs_menu);
        getSupportActionBar().openOptionsMenu();

        Intent intent = getIntent();
        String msg = intent.getStringExtra(EXTRA_MESSAGE);

        textView = (TextView) findViewById(com.joekokosa.oscixer.R.id.feedback_text);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(com.joekokosa.oscixer.R.menu.cs_menu, menu);
        return true;
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
            case com.joekokosa.oscixer.R.id.transport_play:
                controller.startTransport();
                break;
            case com.joekokosa.oscixer.R.id.transport_stop:
                controller.stopTransport();
                break;
            case com.joekokosa.oscixer.R.id.transport_home:
                controller.goHome();
                break;
            case com.joekokosa.oscixer.R.id.transport_end:
                controller.goEnd();
                break;
            case com.joekokosa.oscixer.R.id.transport_prev_mark:
                controller.prevMark();
                break;
            case com.joekokosa.oscixer.R.id.transport_next_mark:
                Log.v("Click", "Before");
                controller.nextMark();
                Log.v("Click", "After");
                //controller.moveFader(3, -6.6f);
                //controller.nextMark();
                break;
            case com.joekokosa.oscixer.R.id.transport_loop:
                controller.toggle_loop();
                break;
            case com.joekokosa.oscixer.R.id.next_strip:
                controller.selectTrack(selected_strip + 1);
                break;
            case com.joekokosa.oscixer.R.id.prev_strip:
                controller.selectTrack(selected_strip - 1);
                break;
        }
    }

    public void menuClick(MenuItem item) {
        switch (item.getItemId()) {
            case com.joekokosa.oscixer.R.id.globalRecEnable:
                controller.globalRecordEnable();
                break;
            case com.joekokosa.oscixer.R.id.trackRecEnable:
                if (rec_enable == 0.0f) {
                    controller.stripRecordEnable(strip);
                } else {
                    controller.stripRecordDisable(strip);
                }
                break;
            default:
                Log.v("Menu", "Unknown resource id");
        }
    }


    class ResponseHandler extends Handler {
        final ControlActivity activity;

        ResponseHandler(ControlActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message message) {
/*
Before enable record on Gtr when not the selected track

V/Incoming Message: /rec_enable_toggle [0]
V/Message:: /rec_enable_toggle [0]
V/Incoming Message: /rec_enable_toggle [0]
V/Message:: /rec_enable_toggle [0]
V/Incoming Message: /record_tally [1]
V/Message:: /record_tally [1]
V/Incoming Message: /record_tally [1]
V/Message:: /record_tally [1]
V/Incoming Message: /strip/recenable [4, 1.0]
V/Incoming Message: /strip/recenable [4, 1.0]

After enable before disable record on Gtr when not the selected track

V/Incoming Message: /rec_enable_toggle [0]
V/Message:: /rec_enable_toggle [0]
V/Incoming Message: /rec_enable_toggle [0]
V/Message:: /rec_enable_toggle [0]
V/Incoming Message: /record_tally [0]
V/Message:: /record_tally [0]
V/Incoming Message: /record_tally [0]
V/Message:: /record_tally [0]
V/Incoming Message: /strip/recenable [4, 0.0]
V/Incoming Message: /strip/recenable [4, 0.0]

After disable

 */

            switch (message.what) {
                case CixListener.FB_GAIN:
                    strip = message.getData().getInt("strip", 0);
                    float fader = message.getData().getFloat("gain", -999.0f);
                    String name = message.getData().getString("name", "not found");
                    activity.textView.setText(String.format("Strip %d-%s = %f", strip, name, fader));
                    break;
                case CixListener.FB_SELECT:
                    selected_strip = message.getData().getInt("strip", 0);
                    break;
                case CixListener.FB_STRIP:
                    int temp_strip = message.getData().getInt(FeedbackTracker.CS_ID, 0);
                    if (selected_strip == temp_strip) {
                        strip = temp_strip;
                        name = message.getData().getString(FeedbackTracker.CS_NAME, "not found");
                        String comment = message.getData().getString(FeedbackTracker.CS_COMMENT, "");
                        float mute = message.getData().getFloat(FeedbackTracker.CS_MUTE, 0.0f);
                        float solo = message.getData().getFloat(FeedbackTracker.CS_SOLO, 0.0f);
                        fader = message.getData().getFloat(FeedbackTracker.CS_FADER, 0.0f);
                        float trim = message.getData().getFloat(FeedbackTracker.CS_TRIM, 0.0f);
                        float solo_iso = message.getData().getFloat(FeedbackTracker.CS_SOLO_ISO, 0.0f);
                        float solo_safe = message.getData().getFloat(FeedbackTracker.CS_SOLO_SAFE, 0.0f);
                        float polarity = message.getData().getFloat(FeedbackTracker.CS_POLARITY, 0.0f);
                        float monitor_input = message.getData().getFloat(FeedbackTracker.CS_MONITOR_INPUT, 0.0f);
                        float monitor_disk = message.getData().getFloat(FeedbackTracker.CS_MONITOR_DISK, 0.0f);
                        rec_enable = message.getData().getFloat(FeedbackTracker.CS_RECENABLE, 0.0f);
                        float rec_safe = message.getData().getFloat(FeedbackTracker.CS_RECSAFE, 0.0f);
                        // TODO: findout what expanded is??
                        float expanded = message.getData().getFloat(FeedbackTracker.CS_EXPANDED, 0.0f);
                        float pan_stereo_position = message.getData().getFloat(FeedbackTracker.CS_PAN_STERO_POSITION, 0.0f);
                        float pan_stereo_width = message.getData().getFloat(FeedbackTracker.CS_PAN_STERO_WIDTH, 0.0f);
                        float num_inputs = message.getData().getFloat(FeedbackTracker.CS_NUM_INPUTS, 0.0f);
                        float num_outputs = message.getData().getFloat(FeedbackTracker.CS_NUM_OUTPUTS, 0.0f);

                        String state = String.format("Id = %d\tName = %s\nMute = %f\tTrim = %f\nFader = %f\tComment = '%s'\n" +
                                        "Solo = %f\tSolo_Iso = %f\nSolo_Safe = %f\tPolarity = %f\nMonitor_input = %f\t" +
                                        "Monitor_disk = %f\nRec_enable = %f\tRec_Safe = %f\nExpanded = %f\tPan_Position = %f\n" +
                                        "Pan_Width = %f\tNum_Inputs = %f\nNum_Outputs = %f\n",
                                strip, name, mute, trim, fader, comment,
                                solo, solo_iso, solo_safe, polarity, monitor_input,
                                monitor_disk, rec_enable, rec_safe, expanded, pan_stereo_position, pan_stereo_width,
                                num_inputs, num_outputs);
                        activity.textView.setText(state);

                        Toolbar toolbar = (Toolbar) this.activity.findViewById(com.joekokosa.oscixer.R.id.my_toolbar);
                        toolbar.setSubtitle(name);
                    }
                    break;
            }
        }
    }
}