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

    public static final int FB_GAIN = 0;
    static final int MSG_REGISTER = 0;
    static final int MSG_GET_STRIP = 1;
    private OSCPortIn oscportin;
    private FeedbackTracker fbTracker = new FeedbackTracker();
    private Messenger mMessenger = new Messenger((new IncomingHandler(this)));
    private Messenger mActivity = null;

    public void setSocket(DatagramSocket sock) {
        oscportin = new OSCPortIn(sock);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    /**
     * method for clients
     */

    public void attachListeners() {
        OSCListener listener = new OSCListener() {
            @Override
            public void acceptMessage(Date time, OSCMessage message) {
                // do something
                try {
                    String myAddress = message.getAddress();

                    switch (myAddress) {
                        case "/strip/gain":
                            int strip = (int) message.getArguments().get(0);
                            fbTracker.setTrackGain(strip, (float) message.getArguments().get(1));
                            updateStrip(strip);
                            break;
                        case "/strip/mute":
                            break;
                        case "/strip/solo":
                            break;
                        case "/strip/expand":
                            break;
                        case "/strip/name":
                            break;
                        case "/strip/monitor_input":
                            break;
                        case "/strip/monitor_disk":
                            break;
                        case "/strip/recenable":
                            break;
                        case "/strip/record_safe":
                            break;
                        case "/strip/select":
                            break;
                        case "/strip/trimdB":
                            break;
                        case "/strip/pan_stereo_position":
                            break;

                        default:
                            Log.v("listener", message.getAddress());
                            break;
                    }

                    // update the UI TODO: move to class and only update if the ui is showing the appropriate control

                } catch (Exception e) {
                    Log.v("listenerError", message.getAddress());
                    //e.printStackTrace();
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

        msg.setData(bundle);
        try {
            mActivity.send(msg);
        } catch (Exception e) {
            //
        }

    }

    static class IncomingHandler extends Handler {
        CixListener cix;

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