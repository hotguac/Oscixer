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

    public void setSelected(int strip, Float yn) {
        if (yn == 0.0f) {
            selected_track = -1;
        } else {
            selected_track = strip;
        }
    }

    public int getSelected() {
        return selected_track;
    }

    public void setMute(int strip, Float mute) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setMute(mute);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setMute(mute);
            audio_tracks.add(trk);
        }
    }

    public void setSolo(int strip, Float solo) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setSolo(solo);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setSolo(solo);
            audio_tracks.add(trk);
        }
    }

    public void setMonitorInput(int strip, Float yn) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setMonitorInput(yn);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setMonitorInput(yn);
            audio_tracks.add(trk);
        }
    }

    public void setMonitorDisk(int strip, Float yn) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setMonitorDisk(yn);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setMonitorDisk(yn);
            audio_tracks.add(trk);
        }
    }

    public void setExpand(int strip, Float yn) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setExpand(yn);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setExpand(yn);
            audio_tracks.add(trk);
        }
    }

    public void setRecordEnable(int strip, Float yn) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setRecordEnable(yn);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setRecordEnable(yn);
            audio_tracks.add(trk);
        }
    }

    public void setRecordSafe(int strip, Float yn) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setRecordSafe(yn);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setRecordSafe(yn);
            audio_tracks.add(trk);
        }
    }

    public void setTrim(int strip, Float trim) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setTrim(trim);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setTrim(trim);
            audio_tracks.add(trk);
        }
    }

    public void setPSP(int strip, Float psp) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setPSP(psp);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setPSP(psp);
            audio_tracks.add(trk);
        }
    }

    public void setNumInputs(int strip, Float n_inputs) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setNumInputs(n_inputs);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setNumInputs(n_inputs);
            audio_tracks.add(trk);
        }
    }

    public void setNumOutputs(int strip, Float n_outputs) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setNumOutputs(n_outputs);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setNumOutputs(n_outputs);
            audio_tracks.add(trk);
        }
    }

    public void setSoloIso(int strip, Float solo_iso) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setSoloIso(solo_iso);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setSoloIso(solo_iso);
            audio_tracks.add(trk);
        }
    }

    public void setSoloSafe(int strip, Float solo_safe) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setSoloSafe(solo_safe);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setSoloSafe(solo_safe);
            audio_tracks.add(trk);
        }
    }

    public void setComment(int strip, String comment) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setComment(comment);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setComment(comment);
            audio_tracks.add(trk);
        }
    }

    public void setPolarity(int strip, Float polarity) {
        boolean found = false;
        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == strip) {
                track.setPolarity(polarity);
                found = true;
            }
        }
        if (!found) {
            ChannelStrip trk = new ChannelStrip(strip);
            trk.setPolarity(polarity);
            audio_tracks.add(trk);
        }
    }
}

class ChannelStrip {
    private static final String CS_ID = "id";
    private static final String CS_NAME = "name";
    private static final String CS_FADER = "fader";
    private static final String CS_MUTE = "mute";
    private static final String CS_SOLO = "solo";
    private static final String CS_SOLO_ISO = "solo_iso";
    private static final String CS_SOLO_SAFE = "solo_safe";
    private static final String CS_COMMENT = "comment";

    private static final String CS_POLARITY = "polarity";
    private static final String CS_MONITOR_INPUT = "monitor_input";
    private static final String CS_MONITOR_DISK = "monitor_disk";
    private static final String CS_RECENABLE = "rec_enable";
    private static final String CS_RECSAFE = "rec_safe";
    private static final String CS_EXPANDED = "expanded";
    private static final String CS_TRIM = "trim";
    private static final String CS_PAN_STERO_POSITION = "pan_stero_position";
    private static final String CS_PAN_STERO_WIDTH = "pan_stero_width";
    private static final String CS_SEND_GAIN = "send_gain";
    private static final String CS_SEND_FADER = "send_fader";
    private static final String CS_SEND_ENABLE = "send_enable";
    private static final String CS_NUM_INPUTS = "num_inputs";
    private static final String CS_NUM_OUTPUTS = "num_outputs";

    private final ConcurrentHashMap data;

    public ChannelStrip(int trackID) {
        data = new ConcurrentHashMap<>(32);

        data.put(CS_ID, trackID);
    }

    public String getName() {
        if (data.containsKey(CS_NAME)) {
            return (String) data.get(CS_NAME);
        } else {
            return "not found";
        }
    }

    public void setName(String name) {
        if (data.containsKey(CS_NAME)) {
            data.replace(CS_NAME, name);
        } else {
            data.put(CS_NAME, name);
        }
    }

    int getId() {
        if (data.containsKey(CS_ID)) {
            return (int) data.get(CS_ID);
        } else {
            return -1;
        }
    }

    float getGain() {
        if (data.containsKey(CS_FADER)) {
            return (Float) data.get(CS_FADER);
        } else {
            return -999.0f;
        }
    }

    void setGain(float gain) {
        if (data.containsKey(CS_FADER)) {
            data.replace(CS_FADER, gain);
        } else {
            data.put(CS_FADER, gain);
        }
    }

    public void setMute(Float mute) {
        if (data.containsKey(CS_MUTE)) {
            data.replace(CS_MUTE, mute);
        } else {
            data.put(CS_MUTE, mute);
        }
    }

    public void setSolo(Float solo) {
        if (data.containsKey(CS_SOLO)) {
            data.replace(CS_SOLO, solo);
        } else {
            data.put(CS_SOLO, solo);
        }
    }

    public void setMonitorInput(Float yn) {
        if (data.containsKey(CS_MONITOR_INPUT)) {
            data.replace(CS_MONITOR_INPUT, yn);
        } else {
            data.put(CS_MONITOR_INPUT, yn);
        }
    }

    public void setMonitorDisk(Float yn) {
        if (data.containsKey(CS_MONITOR_DISK)) {
            data.replace(CS_MONITOR_DISK, yn);
        } else {
            data.put(CS_MONITOR_DISK, yn);
        }
    }

    public void setExpand(Float yn) {
        if (data.containsKey(CS_EXPANDED)) {
            data.replace(CS_EXPANDED, yn);
        } else {
            data.put(CS_EXPANDED, yn);
        }
    }

    public void setRecordEnable(Float yn) {
        if (data.containsKey(CS_RECENABLE)) {
            data.replace(CS_RECENABLE, yn);
        } else {
            data.put(CS_RECENABLE, yn);
        }
    }

    public void setRecordSafe(Float yn) {
        if (data.containsKey(CS_RECSAFE)) {
            data.replace(CS_RECSAFE, yn);
        } else {
            data.put(CS_RECSAFE, yn);
        }
    }

    public void setTrim(Float trim) {
        if (data.containsKey(CS_TRIM)) {
            data.replace(CS_TRIM, trim);
        } else {
            data.put(CS_TRIM, trim);
        }
    }

    public void setPSP(Float psp) {
        if (data.containsKey(CS_PAN_STERO_POSITION)) {
            data.replace(CS_PAN_STERO_POSITION, psp);
        } else {
            data.put(CS_PAN_STERO_POSITION, psp);
        }
    }

    public void setNumInputs(Float n_inputs) {
        if (data.containsKey(CS_NUM_INPUTS)) {
            data.replace(CS_NUM_INPUTS, n_inputs);
        } else {
            data.put(CS_NUM_INPUTS, n_inputs);
        }
    }

    public void setNumOutputs(Float n_outputs) {
        if (data.containsKey(CS_NUM_OUTPUTS)) {
            data.replace(CS_NUM_OUTPUTS, n_outputs);
        } else {
            data.put(CS_NUM_OUTPUTS, n_outputs);
        }
    }

    public void setSoloIso(Float solo_iso) {
        if (data.containsKey(CS_SOLO_ISO)) {
            data.replace(CS_SOLO_ISO, solo_iso);
        } else {
            data.put(CS_SOLO_ISO, solo_iso);
        }
    }

    public void setSoloSafe(Float solo_safe) {
        if (data.containsKey(CS_SOLO_SAFE)) {
            data.replace(CS_SOLO_SAFE, solo_safe);
        } else {
            data.put(CS_SOLO_SAFE, solo_safe);
        }
    }

    public void setComment(String comment) {
        if (data.containsKey(CS_COMMENT)) {
            data.replace(CS_COMMENT, comment);
        } else {
            data.put(CS_COMMENT, comment);
        }
    }

    public void setPolarity(Float polarity) {
        if (data.containsKey(CS_POLARITY)) {
            data.replace(CS_POLARITY, polarity);
        } else {
            data.put(CS_POLARITY, polarity);
        }
    }
}
