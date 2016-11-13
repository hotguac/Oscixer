package com.jkokosa.oscixer;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by jkokosa on 11/9/16.
 */

class DawController {
    private DatagramSocket mySocket;
    private OSCPortOut oscPortOut;

    public void startTransport() {
        try {
            // Creating the message
            OSCMessage message = new OSCMessage("/transport_play");
            oscPortOut.send(message);
        } catch (Exception e) {
            //return;
        }
    }

    public void stopTransport() {
        try {
            OSCMessage message2 = new OSCMessage("/transport_stop");

            oscPortOut.send(message2);
        } catch (Exception e) {
            // Error handling for some error
        }
    }

    public void toggle_loop() {
        try {
            OSCMessage message2 = new OSCMessage("/loop_toggle");

            oscPortOut.send(message2);
        } catch (Exception e) {
            // Error handling for some error
        }
    }

    public void goHome() {
        try {
            OSCMessage message2 = new OSCMessage("/goto_start");

            oscPortOut.send(message2);
        } catch (Exception e) {
            // Error handling for some error
        }
    }

    public void goEnd() {
        try {
            OSCMessage message2 = new OSCMessage("/goto_end");

            oscPortOut.send(message2);
        } catch (Exception e) {
            // Error handling for some error
        }
    }

    public void prevMark() {
        try {
            OSCMessage message2 = new OSCMessage("/prev_marker");

            oscPortOut.send(message2);
        } catch (Exception e) {
            // Error handling for some error
        }
    }

    public void nextMark() {
        try {
            OSCMessage message2 = new OSCMessage("/next_marker");

            oscPortOut.send(message2);
        } catch (Exception e) {
            // Error handling for some error
        }
    }

    DatagramSocket attachPorts(String myIP, int port) {

        try {
            InetAddress target = InetAddress.getByName(myIP);
            mySocket = new DatagramSocket();
            oscPortOut = new OSCPortOut(target, port, mySocket);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }

        return mySocket;
    }

    void connectSurface() {
        // Creating the message
        ArrayList<Object> moreThingsToSend = new ArrayList<>();
        moreThingsToSend.add(0); // bank size 0 = all on one
        moreThingsToSend.add(895); // strips
        moreThingsToSend.add(8211); // feed back
        moreThingsToSend.add(0); // gain mode

        //OSCMessage message = new OSCMessage("/transport_play", Arrays.asList(thingsToSend));
        OSCMessage message = new OSCMessage("/set_surface", moreThingsToSend);

        try {
            oscPortOut.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
