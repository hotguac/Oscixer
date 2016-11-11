package com.jkokosa.oscixer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;

import java.net.DatagramSocket;
import java.util.Date;


public class CixListener extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private OSCPortIn oscportin;
    private FeedbackTracker fbTracker = new FeedbackTracker();

    public void setSocket(DatagramSocket sock) {
        oscportin = new OSCPortIn(sock);
    }

    @Override
    public void onCreate() {
        //TODO: move attach listeners to bind called routine
        super.onCreate();
        //attachListeners();
    }
    // Random number generator
    //private final Random mGenerator = new Random();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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

                    //strip_gains[strip] = (float) myMessage.getArguments().get(1);
                    /*if (myAddress.equals("/strip/gain")) {
                        fbTracker.setTrackGain(strip, (float) myMessage.getArguments().get(1));
                    } else {
                        Log.v("listener",myMessage.getAddress());
                    }*/

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
        /*((ControlActivity) ui).textView.post(new Runnable() {
            public void run() {
                ((ControlActivity) ui).textView.setText(stripID + " : " + fbTracker.getTrackGain(stripID));
                ((ControlActivity) ui).refresh();
            }
        });
*/
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        CixListener getService() {
            // Return this instance of LocalService so clients can call public methods
            return CixListener.this;
        }
    }

}