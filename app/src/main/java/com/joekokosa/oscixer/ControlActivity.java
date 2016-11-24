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
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.DatagramSocket;

import static com.joekokosa.oscixer.FeedbackTracker.CS_FADER;
import static com.joekokosa.oscixer.FeedbackTracker.CS_ID;
import static com.joekokosa.oscixer.FeedbackTracker.CS_NAME;
import static com.joekokosa.oscixer.MainActivity.EXTRA_MESSAGE;

class ControlActivity extends AppCompatActivity {
    static final public int MODE_FADER = 0;
    static final public int MODE_PAN = 1;
    static final public int MODE_TRIM = 2;
    static protected DawController controller;
    static private DatagramSocket sock;
    protected Float fader;
    protected Float trim;
    protected Float pan_stereo_position;
    protected Float last_fader;
    protected int selected_strip;
    private Messenger mMessenger;
    private TextView textView;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private boolean mBound = false;
    private int strip;
    private float rec_enable;
    private int current_mode = MODE_FADER;

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

        setContentView(R.layout.control_surface2);
        // https://developer.android.com/reference/android/support/constraint/ConstraintLayout.html

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().openOptionsMenu();

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

        mMessenger = new Messenger(new ResponseHandler(this));

        AppCompatImageView touch_area = (AppCompatImageView) findViewById(R.id.touch_area);
        touch_area.setOnTouchListener(new TouchArea(this));
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

        int height;

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        height = metrics.heightPixels;

        ViewGroup.LayoutParams taParams = findViewById(R.id.touch_area).getLayoutParams();
        int newHeight = Math.round(height * 0.4f); // TODO: this is a kludge, fix layout
        taParams.height = newHeight;
        Log.d("TouchArea", taParams.toString());
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

    void onClicks(View view) {
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
                controller.nextMark();
                break;
            case R.id.transport_loop:
                controller.toggle_loop();
                break;
            case R.id.strip_up:
                switch (current_mode) {
                    case MODE_FADER:
                        current_mode = MODE_PAN;

                        break;
                    case MODE_PAN:
                        break;
                    case MODE_TRIM:
                        break;
                }
                break;
            case R.id.strip_down:
                break;
            case R.id.next_strip:
                controller.selectTrack(selected_strip + 1);
                break;
            case R.id.prev_strip:
                controller.selectTrack(selected_strip - 1);
                break;
        }
    }

    void menuClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.globalRecEnable:
                controller.globalRecordEnable();
                break;
            case R.id.trackRecEnable:
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
        int saved_global_rec_enable;
        int temp_strip;

        ResponseHandler(ControlActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case CixListener.FB_GAIN:
                    strip = message.getData().getInt(CS_ID, 0);
                    fader = message.getData().getFloat(CS_FADER, -999.0f);
                    String name = message.getData().getString(CS_NAME, "not found");
                    activity.textView.setText(Float.toString(fader));
                    break;
                case CixListener.FB_SELECT:
                    selected_strip = message.getData().getInt(CS_ID, 0);
                    break;
                case CixListener.FB_NAME:
                    temp_strip = message.getData().getInt(CS_ID, 0);
                    name = message.getData().getString(FeedbackTracker.CS_NAME, "not found");
                    try {
                        Toolbar toolbar = (Toolbar) this.activity.findViewById(R.id.my_toolbar);
                        toolbar.setTitle("Oscixer  -  " + name);
                    } catch (Exception e) {
                        Log.e("FB_NAME", e.getMessage());
                    }
                    break;
                case CixListener.FB_TRACK_RECENABLE:
                    temp_strip = message.getData().getInt(CS_ID, 0);
                    if (selected_strip == temp_strip) {
                        float temp_rec_enable = message.getData().getFloat(FeedbackTracker.CS_TRACK_RECENABLE, 0.0f);
                        if (temp_rec_enable != rec_enable) {
                            try {
                                ActionMenuItemView trk_enable = (ActionMenuItemView) findViewById(R.id.trackRecEnable);
                                if (temp_rec_enable == 0.0f) {
                                    trk_enable.getItemData().setIcon(R.drawable.track_rec_disabled);
                                } else {
                                    trk_enable.getItemData().setIcon(R.drawable.track_rec_enabled);
                                }
                            } catch (Exception e) {
                                Log.e("Recenable", e.getMessage());
                            }
                        }
                        rec_enable = temp_rec_enable;
                    }
                    break;
                case CixListener.FB_GLOBAL_RECENABLE:
                    int temp_global_rec_enable = message.arg1;
                    if (temp_global_rec_enable != saved_global_rec_enable) {
                        try {
                            ActionMenuItemView global_enable = (ActionMenuItemView) findViewById(R.id.globalRecEnable);
                            if (temp_global_rec_enable == 0) {
                                global_enable.getItemData().setIcon(R.drawable.global_rec_disabled);
                            } else {
                                global_enable.getItemData().setIcon(R.drawable.global_rec_enabled);
                            }
                        } catch (Exception e) {
                            Log.e("Recenable", e.getMessage());
                        }
                    }
                    saved_global_rec_enable = temp_global_rec_enable;

                    break;
                case CixListener.FB_STRIP:
                    temp_strip = message.getData().getInt(CS_ID, 0);
                    if (selected_strip == 0) {
                        selected_strip = temp_strip;
                        controller.selectTrack(selected_strip);
                    }

                    if (selected_strip == temp_strip) {
                        strip = temp_strip;

                        switch (current_mode) {
                            case MODE_FADER:
                                fader = message.getData().getFloat(FeedbackTracker.CS_FADER, 0.0f);
                                break;
                            case MODE_TRIM:
                                trim = message.getData().getFloat(FeedbackTracker.CS_TRIM, 0.0f);
                                break;
                            case MODE_PAN:
                                pan_stereo_position = message.getData().getFloat(FeedbackTracker.CS_PAN_STERO_POSITION, 0.0f);
                                break;
                            default:
                                Log.d("CIX", "Unhandled FB_STRIP mode");
                        }

                        /*
                        String comment = message.getData().getString(FeedbackTracker.CS_COMMENT, "");
                        float mute = message.getData().getFloat(FeedbackTracker.CS_MUTE, 0.0f);
                        float solo = message.getData().getFloat(FeedbackTracker.CS_SOLO, 0.0f);
                        float solo_iso = message.getData().getFloat(FeedbackTracker.CS_SOLO_ISO, 0.0f);
                        float solo_safe = message.getData().getFloat(FeedbackTracker.CS_SOLO_SAFE, 0.0f);
                        float polarity = message.getData().getFloat(FeedbackTracker.CS_POLARITY, 0.0f);
                        float monitor_input = message.getData().getFloat(FeedbackTracker.CS_MONITOR_INPUT, 0.0f);
                        float monitor_disk = message.getData().getFloat(FeedbackTracker.CS_MONITOR_DISK, 0.0f);
                        rec_enable = message.getData().getFloat(FeedbackTracker.CS_TRACK_RECENABLE, 0.0f);
                        float rec_safe = message.getData().getFloat(FeedbackTracker.CS_RECSAFE, 0.0f);
                        // TODO: findout what expanded is??
                        float expanded = message.getData().getFloat(FeedbackTracker.CS_EXPANDED, 0.0f);
                        float
                        float pan_stereo_width = message.getData().getFloat(FeedbackTracker.CS_PAN_STERO_WIDTH, 0.0f);
                        float num_inputs = message.getData().getFloat(FeedbackTracker.CS_NUM_INPUTS, 0.0f);
                        float num_outputs = message.getData().getFloat(FeedbackTracker.CS_NUM_OUTPUTS, 0.0f);

                         */

                        try {
                            activity.textView.setText(Float.toString(fader));
                        } catch (Exception e) {
                            Log.e("STRIP", e.getMessage());
                        }
                    }
                    break;
            }
        }
    }
}