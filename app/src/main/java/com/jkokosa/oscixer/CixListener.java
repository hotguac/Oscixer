package com.jkokosa.oscixer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;


public class CixListener extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private DatagramSocket mySocket;
    private int targetPort = 3819;
    private String targetIP = "192.168.0.16";
    private OSCPortOut oscPortOut;
    private OSCPortIn oscportin;
    private String lastMessage;

    public CixListener() {
        attachPorts(targetIP, targetPort);
    }

    public CixListener(String ip, int port) {
        targetPort = port;
        targetIP = ip;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        attachListeners();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    // Random number generator
    //private final Random mGenerator = new Random();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * method for clients
     */
    //public int getRandomNumber() {
    //    return mGenerator.nextInt(100);
    //}
    public String getLastMessage() {
        return lastMessage;
    }

    public void startTransport() {
        try {
            // Creating the message
            OSCMessage message = new OSCMessage("/transport_play");
            oscPortOut.send(message);
        } catch (Exception e) {
            return;
        }

    }

    public void stopTransport() {
        try {
            OSCMessage message2 = new OSCMessage("/transport_stop");

            // Pause for half a second
            //sleep(1500);
            oscPortOut.send(message2);
        } catch (Exception e) {
            // Error handling for some error
        }
    }

    private void attachPorts(String myIP, int port) {
        //String myIP = "192.168.0.16";
        try {
            InetAddress target = InetAddress.getByName(myIP);
            mySocket = new DatagramSocket();
            oscPortOut = new OSCPortOut(target, targetPort, mySocket);
            oscportin = new OSCPortIn(mySocket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    private void attachListeners() {
        final Context context = this;
        OSCListener listener = new OSCListener() {
            @Override
            public void acceptMessage(Date time, OSCMessage message) {
                // do something
                OSCMessage myMessage = message;
                try {
                    lastMessage = myMessage.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        oscportin.addListener("/strip/gain", listener);
        oscportin.startListening();
    }

    private void connectSurface() {
        // Creating the message
        ArrayList<Object> moreThingsToSend = new ArrayList<Object>();
        moreThingsToSend.add(0); // bank size 0 = all on one
        moreThingsToSend.add(21); // strips
        moreThingsToSend.add(3); // feed back
        moreThingsToSend.add(0); // gain mode

        //OSCMessage message = new OSCMessage("/transport_play", Arrays.asList(thingsToSend));
        OSCMessage message = new OSCMessage("/set_surface", moreThingsToSend);

        try {
            oscPortOut.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
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