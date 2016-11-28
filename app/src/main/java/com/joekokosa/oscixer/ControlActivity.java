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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.DatagramSocket;

import static com.joekokosa.oscixer.FeedbackTracker.CS_ID;
import static com.joekokosa.oscixer.MainActivity.EXTRA_MESSAGE;

class ControlActivity extends AppCompatActivity {
    static final public int MODE_FADER = 0;
    static final public int MODE_PAN = 1;
    static final public int MODE_TRIM = 2;
    static DawController controller;
    static int selected_strip;
    static private DatagramSocket sock;
    Float fader;
    Float trim;
    Float pan_stereo_position;
    int current_mode = MODE_FADER;
    private Messenger mService;
    private Messenger mMessenger;
    private final ServiceConnection mConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            mService = new Messenger(service);

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
    private TextView textView;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private boolean mBound = false;
    private int strip;
    private float rec_enable;
    private ImageView touch_area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.control_surface2);
        // https://developer.android.com/reference/android/support/constraint/ConstraintLayout.html

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        try {
            getSupportActionBar().openOptionsMenu();
        } catch (NullPointerException e) {
            Log.e("ControlActivity", e.getMessage());
            e.printStackTrace();
        }

        Intent intent = getIntent();
        String msg = intent.getStringExtra(EXTRA_MESSAGE);

        textView = (TextView) findViewById(R.id.feedback_text);
        textView.setText(msg);

        touch_area = (ImageView) findViewById(R.id.touch_area);

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
            e.printStackTrace();
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
        selected_strip = 0;
        FeedbackTracker.selected_track = 0;
        controller.connectSurface();
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
                controller.nextMark();
                break;
            case R.id.transport_loop:
                controller.toggle_loop();
                break;
            case R.id.strip_up:
                switch (current_mode) {
                    case MODE_FADER:
                        current_mode = MODE_PAN;
                        touch_area.setImageResource(R.drawable.pan_stereo_pos);
                        break;
                    case MODE_PAN:
                        current_mode = MODE_TRIM;

                        touch_area.setImageResource(R.drawable.ta_trim);
                        break;
                    case MODE_TRIM:
                        break;
                    default:
                        break;
                }
                try {
                    Message msg = Message.obtain(null, CixListener.MSG_SETMODE);
                    msg.arg1 = current_mode;

                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.strip_down:
                switch (current_mode) {
                    case MODE_FADER:
                        break;
                    case MODE_PAN:
                        current_mode = MODE_FADER;
                        touch_area.setImageResource(R.drawable.ta_fader);
                        break;
                    case MODE_TRIM:
                        current_mode = MODE_PAN;
                        touch_area.setImageResource(R.drawable.pan_stereo_pos);
                        break;
                    default:
                        break;
                }
                try {
                    Message msg = Message.obtain(null, CixListener.MSG_SETMODE);
                    msg.arg1 = current_mode;

                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.next_strip:
                controller.selectTrack(selected_strip + 1);
                break;
            case R.id.prev_strip:
                controller.selectTrack(selected_strip - 1);
                break;
        }
    }

    public void menuClick(MenuItem item) {
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
        String name;

        ResponseHandler(ControlActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case CixListener.FB_SELECT:
                    selected_strip = message.getData().getInt(CS_ID, 0);
                    break;
                case CixListener.FB_NAME:
                    temp_strip = message.getData().getInt(CS_ID, 0);
                    if (selected_strip == temp_strip) {
                        name = message.getData().getString(FeedbackTracker.CS_NAME, "not found");
                        try {
                            Toolbar toolbar = (Toolbar) this.activity.findViewById(R.id.my_toolbar);
                            toolbar.setTitle("Oscixer  -  " + name);
                        } catch (Exception e) {
                            Log.e("FB_NAME", e.getMessage());
                            e.printStackTrace();
                        }
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
                                e.printStackTrace();
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
                            e.printStackTrace();
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
                                try {
                                    float tmp = fader * 10;
                                    Float temp = (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / 10;
                                    activity.textView.setText(Float.toString(temp));
                                } catch (Exception e) {
                                    Log.e("STRIP", e.getMessage());
                                    e.printStackTrace();
                                }
                                break;
                            case MODE_TRIM:
                                trim = message.getData().getFloat(FeedbackTracker.CS_TRIM, 0.0f);
                                try {
                                    float tmp = trim * 10;
                                    Float temp = (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / 10;
                                    activity.textView.setText(Float.toString(temp));
                                } catch (Exception e) {
                                    Log.e("STRIP", e.getMessage());
                                    e.printStackTrace();
                                }
                                break;
                            case MODE_PAN:
                                pan_stereo_position = message.getData().getFloat(FeedbackTracker.CS_PAN_STERO_POSITION, 0.0f);
                                try {
                                    float tmp = pan_stereo_position * 100;
                                    int right = (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp);
                                    activity.textView.setText("L: " + Integer.toString(100 - right) + "  R: " + Integer.toString(right));
                                } catch (Exception e) {
                                    Log.e("STRIP", e.getMessage());
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                Log.d("CIX", "Unhandled FB_STRIP mode");
                        }
                    }
                    break;
            }
        }
    }
}