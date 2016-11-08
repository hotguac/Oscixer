package com.jkokosa.oscixer;

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

    public abstract class FeedbackChannel {
        private String name;
        private int id;
        private int status;
        private int mute;
        private int solo;

        public void FeedbackChannel(int trackID) {
            setId(id);
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

        void AudioTrack(int trackID) {
            super.FeedbackChannel(trackID);
        }
    }
}
