package com.jkokosa.oscixer;

import java.util.ArrayList;

/**
 * Created by jkokosa on 11/8/16.
 */

public class FeedbackTracker {
    /*
    Audio Tracks
    Midi Tracks
    Audio + Midi Tracks
    Audio Busses
    Midi Busses
    VCA Masters
    Master 2-Bus
    Monitor Section
     */

    private static int STATUS_UNKNOWN = 0;
    private static int STATUS_DISPLAYED = 1;
    private static int STATUS_CACHED = 2;

    protected ArrayList<AudioTrack> audio_tracks;

    public FeedbackTracker() {
        audio_tracks = new ArrayList<>(256);
    }

    public float getTrackGain(int stripID) {
        float gain = -999.99f;

        for (AudioTrack track : audio_tracks) {
            if (track.getId() == stripID) {
                gain = track.getGain();
            }
        }
        return gain;
    }

    private abstract class FeedbackChannel {
        private String name;
        private int id;
        private int status;
        private int mute;
        private int solo;

        public FeedbackChannel() {

        }

        public FeedbackChannel(int trackID) {
            setId(trackID);
            setStatus(STATUS_UNKNOWN);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        protected int getId() {
            return id;
        }

        protected void setId(int id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }


    public class AudioTrack extends FeedbackChannel {

        private float gain;

        public AudioTrack(int trackID) {
            setId(trackID);
            setStatus(STATUS_UNKNOWN);
        }

        public float getGain() {
            return gain;
        }

        public void setGain(float gain) {
            this.gain = gain;
        }
    }

    public void setTrackGain(int trackID, float gain) {
        boolean found = false;
        for (AudioTrack track : audio_tracks) {
            if (track.getId() == trackID) {
                track.setGain(gain);
                found = true;
            }
        }
        if (!found) {
            AudioTrack trk = new AudioTrack(trackID);
            trk.setGain(gain);
            audio_tracks.add(trk);
        }
    }


}
