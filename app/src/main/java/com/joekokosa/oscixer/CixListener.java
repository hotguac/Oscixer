package com.joekokosa.oscixer;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;

import java.net.DatagramSocket;
import java.util.Date;

public class CixListener extends Service {

    public static final int FB_STRIP = 0;
    public static final int FB_SELECT = 1;
    public static final int FB_GAIN = 2;
    public static final int FB_TRACK_RECENABLE = 3;
    public static final int FB_GLOBAL_RECENABLE = 4;
    public static final int FB_NAME = 5;
    public static final int FB_FULL = 6;

    static final int MSG_REGISTER = 0;
    static final int MSG_SETMODE = 1;
    static private final FeedbackTracker fbTracker = new FeedbackTracker();
    static private Messenger mActivity = null;
    private final Messenger mMessenger = new Messenger((new IncomingHandler(this)));
    private int current_mode = 0;
    private OSCPortIn oscportin;

    private void setSocket(DatagramSocket sock) {
        oscportin = new OSCPortIn(sock);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    /**
     * method for clients
     */

    private void process_message(Date time, OSCMessage message) {
        int strip = -1;
        int argnum = 0;
        Float yn; // 0-disable / 1-enable
        Float rec_enable;
        Float rec_safe;
        Float trim;
        Float psp;

        try {
            String myAddress = message.getAddress();

            if (myAddress.startsWith("/strip/")) {
                strip = (int) message.getArguments().get(0);
                argnum = 1;
            } else if (myAddress.startsWith("/select/")) {
                strip = fbTracker.getSelected();
                if (strip == 0) {
                    int temp = fbTracker.getValidTrackID();
                    if (temp != 0) {
                        ControlActivity.controller.selectTrack(temp);
                    }
                }
                argnum = 0;
            }

            switch (myAddress) {
                case "/strip/gain":
                case "/select/gain":
                    if (strip > 0) {
                        Float fader = (Float) message.getArguments().get(argnum);
                        fbTracker.setFader(strip, fader);
                        if (current_mode == ControlActivity.MODE_FADER) {
                            updateStripFloat(strip, FeedbackTracker.CS_FADER, fader);
                        }
                    }
                    break;
                case "/strip/trimdB":
                case "/select/trimdB":
                    if (strip > 0) {
                        trim = (Float) message.getArguments().get(argnum);
                        fbTracker.setTrim(strip, trim);
                        if (current_mode == ControlActivity.MODE_TRIM) {
                            updateStripFloat(strip, FeedbackTracker.CS_TRIM, trim);
                        }
                    }
                    break;
                case "/strip/pan_stereo_position":
                case "/select/pan_stereo_position":
                    // 0 = full left; 0.5 = center; 1.0 = full right
                    if (strip > 0) {
                        psp = (Float) message.getArguments().get(argnum);
                        fbTracker.setPanStereoPosition(strip, psp);
                        if (current_mode == ControlActivity.MODE_PAN) {
                            updateStripFloat(strip, FeedbackTracker.CS_PAN_STERO_POSITION, psp);
                        }
                    }
                    break;
                case "/strip/name":
                    // TODO: Select form seems to pass in single space for name - add to Ardour mantis?
                case "/select/name":
                    String temp_name = (String) message.getArguments().get(argnum);
                    if (!temp_name.equals("")) {
                        fbTracker.setTrackName(strip, temp_name);
                        updateStripName(strip, temp_name);
                    }
                    break;
                case "/strip/select":
                    Float select = (Float) message.getArguments().get(argnum);
                    fbTracker.setSelected(strip, select);
                    if (select == 1.0f) {
                        selectStrip(strip);
                    }
                    break;
                case "/strip/mute":
                case "/select/mute":
                    Float mute = (Float) message.getArguments().get(argnum);
                    fbTracker.setMute(strip, mute);
                    break;
                case "/strip/solo":
                case "/select/solo":
                    Float solo = (Float) message.getArguments().get(argnum);
                    fbTracker.setSolo(strip, solo);
                    break;
                case "/strip/expand":
                case "/select/expand":
                    Float expand = (Float) message.getArguments().get(argnum);
                    break;
                case "/strip/monitor_input":
                    if (Integer.class.isInstance(message.getArguments().get(argnum))) {
                        yn = (int) message.getArguments().get(argnum) * 1.0f;
                    } else {
                        yn = (Float) message.getArguments().get(argnum);
                    }
                    break;
                case "/select/monitor_input":
                    Float mon_in = (Float) message.getArguments().get(argnum);
                    break;
                case "/strip/monitor_disk":
                    if (Integer.class.isInstance(message.getArguments().get(argnum))) {
                        yn = (int) message.getArguments().get(argnum) * 1.0f;
                    } else {
                        yn = (Float) message.getArguments().get(argnum);
                    }
                    break;
                case "/select/monitor_disk":
                    Float mon_disk = (Float) message.getArguments().get(argnum);
                    break;
                case "/strip/recenable":
                case "/select/recenable":
                    if (strip > 0) {
                        rec_enable = (Float) message.getArguments().get(argnum);
                        fbTracker.setRecordEnable(strip, rec_enable);
                        recEnableStrip(strip, rec_enable);
                    }
                    break;
                case "/strip/record_safe":
                case "/select/record_safe":
                    rec_safe = (Float) message.getArguments().get(argnum);
                    break;
                case "/select/n_inputs":
                    Float n_inputs = (Float) message.getArguments().get(argnum);
                    break;
                case "/select/n_outputs":
                    Float n_outputs = (Float) message.getArguments().get(argnum);
                    break;
                case "/select/solo_iso":
                    Float solo_iso = (Float) message.getArguments().get(argnum);
                    break;
                case "/select/solo_safe":
                    Float solo_safe = (Float) message.getArguments().get(argnum);
                    break;
                case "/select/comment":
                    String comment = (String) message.getArguments().get(argnum);
                    break;
                case "/select/polarity":
                    Float polarity = (Float) message.getArguments().get(argnum);
                    break;
                case "/rec_enable_toggle":
                    int global_rec_enable = (int) message.getArguments().get(0);
                    recEnableGlobal(global_rec_enable);
                    break;
                case "/select/comp_speed":
                case "/select/comp_mode":
                case "/select/comp_mode_name":
                case "/select/comp_speed_name":
                case "/select/comp_makeup":
                case "/select/eq_hpf":
                case "/select/eq_enable":
                case "/select/pan_stereo_width":
                case "/select/pan_elevation_position":
                case "/select/pan_frontback_position":
                case "/select/pan_lfe_control":
                case "/select/comp_enable":
                case "/select/comp_threshold":
                    break;
                default:
                    Log.i("Message: ", message.getAddress() + " " + message.getArguments().toString());
                    break;
            }

            // update the UI TODO: move to class and only update if the ui is showing the appropriate control

        } catch (Exception e) {
            Log.e("listenerError", message.getAddress() + " " + message.getArguments().toString());
            Log.e("Exception", e.toString());
            e.printStackTrace();
        }
    }


    private void attachListeners() {
        //Thread p = new Thread(new Runnable() {
        //    public void run() {

        OSCListener listener = new OSCListener() {
            @Override
            public void acceptMessage(Date time, OSCMessage message) {
                process_message(time, message);
            }
        };

        //oscportin.addListener("/bank_up", listener);
        //oscportin.addListener("/bank_down", listener);
        //oscportin.addListener("/loop_toggle", listener);
        //oscportin.addListener("/transport_play", listener);
        //oscportin.addListener("/transport_stop", listener);
        //oscportin.addListener("/rewind", listener);
        //oscportin.addListener("/ffwd", listener);
        oscportin.addListener("/session_name", listener);
        //oscportin.addListener("/record_tally", listener);
        oscportin.addListener("/record_enabled", listener);
        oscportin.addListener("/rec_enable_toggle", listener);
        oscportin.addListener("/cancel_all_solos", listener);
        oscportin.addListener("/rec_enable_toggle", listener);
        oscportin.addListener("/strip/*", listener);
        oscportin.addListener("/select/*", listener);
        oscportin.addListener("/master/*", listener);
        oscportin.addListener("/monitor/*", listener);
        oscportin.startListening();

        //    }
        //});
        //p.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        //p.start();
    }

    private void updateStripFloat(final int stripID, String key, Float value) {
        Message msg = Message.obtain(null, FB_STRIP);
        Bundle bundle = new Bundle();
        bundle.putInt(FeedbackTracker.CS_ID, stripID);
        bundle.putFloat(key, value);

        msg.setData(bundle);
        try {
            mActivity.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStripName(final int stripID, String name) {
        Message msg = Message.obtain(null, FB_NAME);
        Bundle bundle = new Bundle();
        bundle.putInt(FeedbackTracker.CS_ID, stripID);
        bundle.putString(FeedbackTracker.CS_NAME, name);

        msg.setData(bundle);
        try {
            mActivity.send(msg);
        } catch (Exception e) {
            Log.e("updateStripName", e.getMessage());
            e.printStackTrace();
        }
    }

    private void selectStrip(final int stripID) { // TODO: need to communicate back to UI
        Message msg = Message.obtain(null, FB_SELECT);
        Bundle bundle = new Bundle();
        bundle.putInt(FeedbackTracker.CS_ID, stripID);

        msg.setData(bundle);
        try {
            mActivity.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recEnableStrip(final int stripID, final float rec_enable) { // TODO: need to communicate back to UI
        Message msg = Message.obtain(null, FB_TRACK_RECENABLE);
        Bundle bundle = new Bundle();
        bundle.putInt(FeedbackTracker.CS_ID, stripID);
        bundle.putFloat(FeedbackTracker.CS_TRACK_RECENABLE, rec_enable);

        msg.setData(bundle);
        try {
            mActivity.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void recEnableGlobal(final int rec_enable) { // TODO: need to communicate back to UI
        Message msg = Message.obtain(null, FB_GLOBAL_RECENABLE);
        msg.arg1 = rec_enable;

        try {
            mActivity.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Receives messages coming from ControlActivity
    static class IncomingHandler extends Handler {
        final CixListener cix;

        IncomingHandler(CixListener cixL) {
            this.cix = cixL;
        }

        @Override
        public void handleMessage(Message msg) {
            Message outmsg;
            Bundle bundle;

            switch (msg.what) {
                case MSG_REGISTER:
                    // Provides target to send messages back to Control Activity
                    mActivity = msg.replyTo;
                    // The socket created by DawController. Ardour will reply to this socket.
                    DatagramSocket sock = (DatagramSocket) msg.obj;

                    cix.setSocket(sock);
                    cix.attachListeners();
                    break;
                case MSG_SETMODE:
                    cix.current_mode = msg.arg1;
                    switch (cix.current_mode) {
                        case ControlActivity.MODE_FADER:
                            outmsg = Message.obtain(null, FB_STRIP);
                            bundle = new Bundle();
                            bundle.putInt(FeedbackTracker.CS_ID, ControlActivity.selected_strip);
                            bundle.putFloat(FeedbackTracker.CS_FADER, fbTracker.getFader(ControlActivity.selected_strip));
                            outmsg.setData(bundle);
                            try {
                                mActivity.send(outmsg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case ControlActivity.MODE_PAN:
                            outmsg = Message.obtain(null, FB_STRIP);
                            bundle = new Bundle();
                            bundle.putInt(FeedbackTracker.CS_ID, ControlActivity.selected_strip);
                            bundle.putFloat(FeedbackTracker.CS_PAN_STERO_POSITION, fbTracker.getPanStereoPosition(ControlActivity.selected_strip));
                            outmsg.setData(bundle);
                            try {
                                mActivity.send(outmsg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case ControlActivity.MODE_TRIM:
                            outmsg = Message.obtain(null, FB_STRIP);
                            bundle = new Bundle();
                            bundle.putInt(FeedbackTracker.CS_ID, ControlActivity.selected_strip);
                            bundle.putFloat(FeedbackTracker.CS_TRIM, fbTracker.getTrim(ControlActivity.selected_strip));
                            outmsg.setData(bundle);
                            try {
                                mActivity.send(outmsg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }

                default:
                    break;
            }
        }
    }

}