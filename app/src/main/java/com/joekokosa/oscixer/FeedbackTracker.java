package com.joekokosa.oscixer;

/**
 * Created by com.joekokosa on 11/8/16.
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
    static final String CS_ID = "id";
    static final String CS_NAME = "name";
    static final String CS_FADER = "fader";
    static final String CS_MUTE = "mute";
    static final String CS_SOLO = "solo";
    static final String CS_SOLO_ISO = "solo_iso";
    static final String CS_SOLO_SAFE = "solo_safe";
    static final String CS_COMMENT = "comment";

    static final String CS_POLARITY = "polarity";
    static final String CS_MONITOR_INPUT = "monitor_input";
    static final String CS_MONITOR_DISK = "monitor_disk";
    static final String CS_TRACK_RECENABLE = "rec_enable";
    static final String CS_GLOBAL_RECENABLE = "rec_enable";
    static final String CS_RECSAFE = "rec_safe";
    static final String CS_EXPANDED = "expanded";
    static final String CS_TRIM = "trim";
    static final String CS_PAN_STERO_POSITION = "pan_stero_position";
    static final String CS_PAN_STERO_WIDTH = "pan_stero_width";
    static final String CS_SEND_GAIN = "send_gain";
    static final String CS_SEND_FADER = "send_fader";
    static final String CS_SEND_ENABLE = "send_enable";
    static final String CS_NUM_INPUTS = "num_inputs";
    static final String CS_NUM_OUTPUTS = "num_outputs";

    //private final ArrayList<ChannelStrip> audio_tracks;
    private final ChannelStrip[] audio_tracks;

    private int selected_track;

    public FeedbackTracker() {
        //audio_tracks = new ArrayList<>(256);
        audio_tracks = new ChannelStrip[512];
    }

    float getFader(int stripID) {
        if (audio_tracks[stripID] != null) {
            return audio_tracks[stripID].fader;
        } else {
            return -999.99f;
        }
    }

    String getTrackName(int stripID) {
        if (audio_tracks[stripID] != null) {
            return audio_tracks[stripID].name;
        } else {
            return "not found";
        }
    }

    void setFader(int trackID, float gain) {
        if (trackID > 0) {
            if (audio_tracks[trackID] == null) {
                audio_tracks[trackID] = new ChannelStrip();
            }

            audio_tracks[trackID].fader = gain;
        }
    }

    void setTrackName(int trackID, String name) {
        if (audio_tracks[trackID] == null) {
            audio_tracks[trackID] = new ChannelStrip();
        }

        audio_tracks[trackID].name = name;
    }

    void setSelected(int strip, Float yn) {
        if (yn == 0.0f) {
            selected_track = -1;
        } else {
            selected_track = strip;
        }
    }

    int getSelected() {
        return selected_track;
    }

    void setMute(int trackID, Float mute) {
        if (audio_tracks[trackID] == null) {
            audio_tracks[trackID] = new ChannelStrip();
        }

        audio_tracks[trackID].mute = mute;
    }

    void setSolo(int trackID, Float solo) {
        if (audio_tracks[trackID] == null) {
            audio_tracks[trackID] = new ChannelStrip();
        }

        audio_tracks[trackID].solo = solo;
    }

    void setRecordEnable(int strip, Float yn) {
        if (audio_tracks[strip] == null) {
            audio_tracks[strip] = new ChannelStrip();
        }

        audio_tracks[strip].recordEnable = yn;
    }

    public void setTrim(int strip, Float trim) {
        if (audio_tracks[strip] == null) {
            audio_tracks[strip] = new ChannelStrip();
        }

        audio_tracks[strip].fader = trim;
    }
}

class ChannelStrip {

    //private final HashMap data;
    Float fader;
    Float trim;
    String name;
    Float expanded;
    Float recordEnable;
    Float recordSafe;
    Float panStereoPosition;
    Float mute;
    Float solo;
}
