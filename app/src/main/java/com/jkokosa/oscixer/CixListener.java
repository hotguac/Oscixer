package com.jkokosa.oscixer;

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

    public static final int FB_SELECT = 0;
    public static final int FB_GAIN = 1;

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

    private void attachListeners() {
        OSCListener listener = new OSCListener() {
            @Override
            public void acceptMessage(Date time, OSCMessage message) {

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
                            fbTracker.setTrackGain(strip, (float) message.getArguments().get(argnum));
                            break;
                        case "/strip/name":
                        case "/select/name":
                            fbTracker.setTrackName(strip, (String) message.getArguments().get(argnum));
                            break;
                        case "/strip/select":
                            Float select = (Float) message.getArguments().get(argnum);
                            fbTracker.setSelected(strip, select);
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
                            fbTracker.setExpand(strip, expand);
                            break;
                        case "/strip/monitor_input":
                            yn = (int) message.getArguments().get(argnum);
                            fbTracker.setMonitorInput(strip, yn * 1.0f);
                            break;
                        case "/select/monitor_input":
                            Float mon_in = (Float) message.getArguments().get(argnum);
                            fbTracker.setMonitorInput(strip, mon_in);
                            break;
                        case "/strip/monitor_disk":
                            yn = (int) message.getArguments().get(argnum);
                            fbTracker.setMonitorDisk(strip, yn * 1.0f);
                            break;
                        case "/select/monitor_disk":
                            Float mon_disk = (Float) message.getArguments().get(argnum);
                            fbTracker.setMonitorDisk(strip, mon_disk);
                            break;
                        case "/strip/recenable":
                        case "/select/recenable":
                            rec_enable = (Float) message.getArguments().get(argnum);
                            fbTracker.setRecordEnable(strip, rec_enable);
                            break;
                        case "/strip/record_safe":
                        case "/select/record_safe":
                            rec_safe = (Float) message.getArguments().get(argnum);
                            fbTracker.setRecordSafe(strip, rec_safe);
                            break;
                        case "/strip/trimdB":
                        case "/select/trimdB":
                            trim = (Float) message.getArguments().get(argnum);
                            fbTracker.setTrim(strip, trim);
                            break;
                        case "/strip/pan_stereo_position":
                        case "/select/pan_stereo_position":
                            psp = (Float) message.getArguments().get(argnum);
                            fbTracker.setPSP(strip, psp);
                            break;
                        case "/select/n_inputs":
                            Float n_inputs = (Float) message.getArguments().get(argnum);
                            fbTracker.setNumInputs(strip, n_inputs);
                            break;
                        case "/select/n_outputs":
                            Float n_outputs = (Float) message.getArguments().get(argnum);
                            fbTracker.setNumOutputs(strip, n_outputs);
                            break;
                        case "/select/solo_iso":
                            Float solo_iso = (Float) message.getArguments().get(argnum);
                            fbTracker.setSoloIso(strip, solo_iso);
                            break;
                        case "/select/solo_safe":
                            Float solo_safe = (Float) message.getArguments().get(argnum);
                            fbTracker.setSoloSafe(strip, solo_safe);
                            break;
                        case "/select/comment":
                            String comment = (String) message.getArguments().get(argnum);
                            fbTracker.setComment(strip, comment);
                            break;
                        case "/select/polarity":
                            Float polarity = (Float) message.getArguments().get(argnum);
                            fbTracker.setPolarity(strip, polarity);
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
                } finally {
                    if (strip != -1) {
                        updateStrip(strip);
                    }
                }
            }
        };

        oscportin.addListener("/bank_up", listener);
        oscportin.addListener("/bank_down", listener);
        oscportin.addListener("loop_toggle", listener);
        oscportin.addListener("transport_play", listener);
        oscportin.addListener("transport_stop", listener);
        oscportin.addListener("/rewind", listener);
        oscportin.addListener("/ffwd", listener);
        oscportin.addListener("/session_name", listener);
        oscportin.addListener("/record_tally", listener);
        oscportin.addListener("/cancel_all_solos", listener);
        oscportin.addListener("/rec_enable_toggle", listener);
        oscportin.addListener("/strip/*", listener);
        oscportin.addListener("/select/*", listener);
        oscportin.addListener("/master/*", listener);
        oscportin.addListener("/monitor/*", listener);
        oscportin.startListening();
    }

    private void updateStrip(final int stripID) { // TODO: need to communicate back to UI
        Message msg = Message.obtain(null, FB_GAIN);
        Bundle bundle = new Bundle();
        bundle.putInt("strip", stripID);
        bundle.putFloat("gain", fbTracker.getTrackGain(stripID));
        bundle.putString("name", fbTracker.getTrackName(stripID));

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
        bundle.putInt("strip", stripID);
        bundle.putString("name", fbTracker.getTrackName(stripID));

        msg.setData(bundle);
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
                    cix.updateStrip(msg.arg1);
                    break;
            }
        }
    }

}