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

    static final int MSG_REGISTER = 0;
    private static final int MSG_GET_STRIP = 1;

    private final FeedbackTracker fbTracker = new FeedbackTracker();
    private final Messenger mMessenger = new Messenger((new IncomingHandler(this)));
    private OSCPortIn oscportin;
    private Messenger mActivity = null;

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
        int yn; // 0-disable / 1-enable
        Float rec_enable;
        Float rec_safe;
        Float trim;
        Float psp;

        //Log.v("Message: ", message.getAddress() + " " + message.getArguments().toString());
        try {
            String myAddress = message.getAddress();
            //Log.v("Incoming Message", message.getAddress() + " " + message.getArguments().toString());
            if (myAddress.startsWith("/strip/")) {
                strip = (int) message.getArguments().get(0);
                argnum = 1;
            } else if (myAddress.startsWith("/select/")) {
                strip = fbTracker.getSelected();
                argnum = 0;
            }

            switch (myAddress) {
                case "/strip/gain":
                case "/select/gain":
                    fbTracker.setFader(strip, (float) message.getArguments().get(argnum));
                    updateFader(strip);
                    break;
                case "/strip/name":
                    // TODO: Select form seems to pass in single space for name - add to Ardour mantis?
                case "/select/name":
                    String temp_name = (String) message.getArguments().get(argnum);
                    if (!temp_name.equals("")) {
                        fbTracker.setTrackName(strip, temp_name);
                    }
                    //updateFader(strip);
                    break;
                case "/strip/select":
                    Float select = (Float) message.getArguments().get(argnum);
                    fbTracker.setSelected(strip, select);
                    if (select == 1.0f) {
                        selectStrip(strip);
                    }
                    //updateFader(strip);
                    break;
                case "/strip/mute":
                case "/select/mute":
                    Float mute = (Float) message.getArguments().get(argnum);
                    fbTracker.setMute(strip, mute);
                    //updateFader(strip);
                    break;
                case "/strip/solo":
                case "/select/solo":
                    Float solo = (Float) message.getArguments().get(argnum);
                    fbTracker.setSolo(strip, solo);
                    //updateFader(strip);
                    break;
                case "/strip/expand":
                case "/select/expand":
                    Float expand = (Float) message.getArguments().get(argnum);
                    //fbTracker.setExpand(strip, expand);
                    //updateFader(strip);
                    break;
                        /*
                        V/listenerError: /strip/monitor_input [1, 0.0]
E/Exception: java.lang.ClassCastException: java.lang.Float cannot be cast to java.lang.Integer
E/Exception: java.lang.ClassCastException: java.lang.Float cannot be cast to java.lang.Integer
V/Message:: /strip/gui_select [1, 0.0]
V/listenerError: /strip/monitor_input [2, 0.0]
E/Exception: java.lang.ClassCastException: java.lang.Float cannot be cast to java.lang.Integer
V/listenerError: /strip/monitor_disk [2, 0.0]
E/Exception: java.lang.ClassCastException: java.lang.Float cannot be cast to java.lang.Integer

                         */
                case "/strip/monitor_input":
                    yn = (int) message.getArguments().get(argnum);
                    //fbTracker.setMonitorInput(strip, yn * 1.0f);
                    //updateFader(strip);
                    break;
                case "/select/monitor_input":
                    Float mon_in = (Float) message.getArguments().get(argnum);
                    //updateFader(strip);
                    break;
                case "/strip/monitor_disk":
                    yn = (int) message.getArguments().get(argnum);
                    //fbTracker.setMonitorDisk(strip, yn * 1.0f);
                    //updateFader(strip);
                    break;
                case "/select/monitor_disk":
                    Float mon_disk = (Float) message.getArguments().get(argnum);
                    //fbTracker.setMonitorDisk(strip, mon_disk);
                    //updateFader(strip);
                    break;
                case "/strip/recenable":
                case "/select/recenable":
                    rec_enable = (Float) message.getArguments().get(argnum);
                    fbTracker.setRecordEnable(strip, rec_enable);
                    recEnableStrip(strip, rec_enable);
                    break;
                case "/strip/record_safe":
                case "/select/record_safe":
                    rec_safe = (Float) message.getArguments().get(argnum);
                    //fbTracker.setRecordSafe(strip, rec_safe);
                    //updateFader(strip);
                    break;
                case "/strip/trimdB":
                case "/select/trimdB":
                    trim = (Float) message.getArguments().get(argnum);
                    //fbTracker.setTrim(strip, trim);
                    //updateFader(strip);
                    break;
                case "/strip/pan_stereo_position":
                case "/select/pan_stereo_position":
                    psp = (Float) message.getArguments().get(argnum);
                    //fbTracker.setPSP(strip, psp);
                    //updateFader(strip);
                    break;
                case "/select/n_inputs":
                    Float n_inputs = (Float) message.getArguments().get(argnum);
                    //fbTracker.setNumInputs(strip, n_inputs);
                    //updateFader(strip);
                    break;
                case "/select/n_outputs":
                    Float n_outputs = (Float) message.getArguments().get(argnum);
                    //fbTracker.setNumOutputs(strip, n_outputs);
                    //updateFader(strip);
                    break;
                case "/select/solo_iso":
                    Float solo_iso = (Float) message.getArguments().get(argnum);
                    //fbTracker.setSoloIso(strip, solo_iso);
                    //updateFader(strip);
                    break;
                case "/select/solo_safe":
                    Float solo_safe = (Float) message.getArguments().get(argnum);
                    //fbTracker.setSoloSafe(strip, solo_safe);
                    //updateFader(strip);
                    break;
                case "/select/comment":
                    String comment = (String) message.getArguments().get(argnum);
                    //fbTracker.setComment(strip, comment);
                    //updateFader(strip);
                    break;
                case "/select/polarity":
                    Float polarity = (Float) message.getArguments().get(argnum);
                    //fbTracker.setPolarity(strip, polarity);
                    //updateFader(strip);
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
                    Log.v("Message: ", message.getAddress() + " " + message.getArguments().toString());
                    break;
            }

            // update the UI TODO: move to class and only update if the ui is showing the appropriate control

        } catch (Exception e) {
            Log.v("listenerError", message.getAddress() + " " + message.getArguments().toString());
            Log.e("Exception", e.toString());
            //e.printStackTrace();
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

    private void updateFader(final int stripID) { // TODO: need to communicate back to UI
        Message msg = Message.obtain(null, FB_STRIP);
        Bundle bundle = new Bundle();
        bundle.putInt(FeedbackTracker.CS_ID, stripID);
        bundle.putString(FeedbackTracker.CS_NAME, fbTracker.getTrackName(stripID));
        bundle.putFloat(FeedbackTracker.CS_FADER, fbTracker.getFader(stripID));
        /*
        bundle.putFloat(FeedbackTracker.CS_MUTE, fbTracker.getMute(stripID));
        bundle.putFloat(FeedbackTracker.CS_SOLO, fbTracker.getSolo(stripID));
        bundle.putFloat(FeedbackTracker.CS_SOLO_ISO, fbTracker.getSoloIso(stripID));
        bundle.putFloat(FeedbackTracker.CS_SOLO_SAFE, fbTracker.getSoloSafe(stripID));
        bundle.putString(FeedbackTracker.CS_COMMENT, fbTracker.getComment(stripID));

        bundle.putFloat(FeedbackTracker.CS_POLARITY, fbTracker.getPolarity(stripID));
        bundle.putFloat(FeedbackTracker.CS_MONITOR_INPUT, fbTracker.getMonitorInput(stripID));
        bundle.putFloat(FeedbackTracker.CS_MONITOR_DISK, fbTracker.getMonitorDisk(stripID));
        bundle.putFloat(FeedbackTracker.CS_TRACK_RECENABLE, fbTracker.getRecEnable(stripID));
        bundle.putFloat(FeedbackTracker.CS_RECSAFE, fbTracker.getRecSafe(stripID));
        bundle.putFloat(FeedbackTracker.CS_EXPANDED, fbTracker.getExpanded(stripID));
        bundle.putFloat(FeedbackTracker.CS_TRIM, fbTracker.getTrim(stripID));
        bundle.putFloat(FeedbackTracker.CS_PAN_STERO_POSITION, fbTracker.getPanStereoPosition(stripID));
        bundle.putFloat(FeedbackTracker.CS_NUM_INPUTS, fbTracker.getNumInputs(stripID));
        bundle.putFloat(FeedbackTracker.CS_NUM_OUTPUTS, fbTracker.getNumOutputs(stripID));
        */

        msg.setData(bundle);
        try {
            mActivity.send(msg);
        } catch (Exception e) {
            //
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
            //
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
            //
        }

    }

    private void recEnableGlobal(final int rec_enable) { // TODO: need to communicate back to UI
        Message msg = Message.obtain(null, FB_GLOBAL_RECENABLE);
        msg.arg1 = rec_enable;

        try {
            mActivity.send(msg);
        } catch (Exception e) {
            //
        }

    }

    static class IncomingHandler extends Handler {
        final CixListener cix;

        IncomingHandler(CixListener cixL) {
            this.cix = cixL;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER:
                    cix.mActivity = msg.replyTo;
                    DatagramSocket sock = (DatagramSocket) msg.obj;
                    cix.setSocket(sock);
                    cix.attachListeners();
                    break;

                case MSG_GET_STRIP:
                    cix.updateFader(msg.arg1);
                    break;
            }
        }
    }

}