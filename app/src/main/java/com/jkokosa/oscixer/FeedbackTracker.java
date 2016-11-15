package com.jkokosa.oscixer;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jkokosa on 11/8/16.
 */

class FeedbackTracker {
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

    private final ArrayList<ChannelStrip> audio_tracks;
    private int selected_track;

    public FeedbackTracker() {
        audio_tracks = new ArrayList<>(256);
    }

    public float getTrackGain(int stripID) {
        float gain = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                gain = track.getGain();
            }
        }
        return gain;
    }

    public String getTrackName(int stripID) {
        String name = "not found";

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                name = track.getName();
            }
        }
        return name;
    }

    void setTrackGain(int trackID, float gain) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == trackID) {
                track.setGain(gain);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(trackID);
            trk.setGain(gain);
            audio_tracks.add(trk);
        }
    }

    public void setTrackName(int trackID, String name) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == trackID) {
                track.setName(name);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(trackID);
            trk.setName(name);
            audio_tracks.add(trk);
        }
    }

    public void setSelected(int strip, int yn) {
        if (yn == 1) {
            selected_track = strip;
        } else {
            selected_track = -1;
        }
    }

    public int getSelected() {
        return selected_track;
    }

    /*/strip/name
V/listener: /strip/mute
V/listener: /strip/solo
V/listener: /strip/monitor_input
V/listener: /strip/monitor_disk
V/listener: /strip/recenable
V/listener: /strip/record_safe
V/listener: /strip/select
V/listener: /strip/trimdB
V/listener: /strip/pan_stereo_position*/

}

class ChannelStrip {
    private final ConcurrentHashMap data;

        /*
        private int mute;
        private int solo;
        private int solo_iso;
        private int solo_safe;
        private int ploarity;
        private int monitor_input;
        private int monitor_disk;
        private int recenable;
        private int record_safe;
        private int select;
        private int expand;
        private float trimdB;
        private float fader;
        private float pan_stero_position;
        private float pan_stero_width;
        private float send_gain;
        private float send_fader;
        private float send_enable;
        */

    public ChannelStrip(int trackID) {
        data = new ConcurrentHashMap<>(32);

        data.put("id", trackID);
    }

    public String getName() {
        if (data.containsKey("name")) {
            return (String) data.get("name");
        } else {
            return "not found";
        }
    }

    public void setName(String name) {
        if (data.containsKey("name")) {
            data.replace("name", name);
        } else {
            data.put("name", name);
        }
    }

    int getId() {
        return (int) data.get("id");
    }

    float getGain() {
        if (data.containsKey("gain")) {
            return (Float) data.get("gain");
        } else {
            return -999.0f;
        }
    }

    void setGain(float gain) {
        if (data.containsKey("gain")) {
            data.replace("gain", gain);
        } else {
            data.put("gain", gain);
        }
    }

}
