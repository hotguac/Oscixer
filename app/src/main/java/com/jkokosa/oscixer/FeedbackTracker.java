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
    static final String CS_RECENABLE = "rec_enable";
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

    private final ArrayList<ChannelStrip> audio_tracks;
    private int selected_track;

    public FeedbackTracker() {
        audio_tracks = new ArrayList<>(256);
    }

    float getTrackGain(int stripID) {
        float gain = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                gain = track.getGain();
            }
        }
        return gain;
    }

    String getTrackName(int stripID) {
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

    void setTrackName(int trackID, String name) {
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

    void setMute(int strip, Float mute) {
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

    void setSolo(int strip, Float solo) {
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

    void setMonitorInput(int strip, Float yn) {
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

    void setMonitorDisk(int strip, Float yn) {
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

    void setExpand(int strip, Float yn) {
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

    void setRecordEnable(int strip, Float yn) {
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

    void setRecordSafe(int strip, Float yn) {
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

    void setTrim(int strip, Float trim) {
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

    public float getMute(int stripID) {
        float mute = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                mute = track.getMute();
            }
        }
        return mute;
    }

    public float getSolo(int stripID) {
        float solo = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                solo = track.getSolo();
            }
        }
        return solo;
    }

    public String getComment(int stripID) {
        String name = "unknown";

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                name = track.getComment();
            }
        }
        return name;
    }

    public float getSoloIso(int stripID) {
        float solo = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                solo = track.getSoloIso();
            }
        }
        return solo;
    }

    public float getSoloSafe(int stripID) {
        float solo = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                solo = track.getSoloSafe();
            }
        }
        return solo;
    }

    public float getPolarity(int stripID) {
        float result = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                result = track.getPolarity();
            }
        }
        return result;
    }

    public float getMonitorInput(int stripID) {
        float result = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                result = track.getMonitorInput();
            }
        }
        return result;
    }

    public float getMonitorDisk(int stripID) {
        float result = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                result = track.getMonitorDisk();
            }
        }
        return result;
    }

    public float getRecEnable(int stripID) {
        float result = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                result = track.getRecEnable();
            }
        }
        return result;
    }

    public float getRecSafe(int stripID) {
        float result = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                result = track.getRecSafe();
            }
        }
        return result;
    }

    public float getExpanded(int stripID) {
        float result = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                result = track.getExpanded();
            }
        }
        return result;
    }

    public float getTrim(int stripID) {
        float result = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                result = track.getTrim();
            }
        }
        return result;
    }

    public float getPanStereoPosition(int stripID) {
        float result = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                result = track.getPanStereoPosition();
            }
        }
        return result;
    }

    public float getNumInputs(int stripID) {
        float result = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                result = track.getNumInputs();
            }
        }
        return result;
    }

    public float getNumOutputs(int stripID) {
        float result = -999.99f;

        for (ChannelStrip track : audio_tracks) {
            if (track.getId() == stripID) {
                result = track.getNumOutputs();
            }
        }
        return result;
    }
}

class ChannelStrip {

    private final ConcurrentHashMap data;

    public ChannelStrip(int trackID) {
        data = new ConcurrentHashMap<>(32);

        data.put(FeedbackTracker.CS_ID, trackID);
    }

    public String getName() {
        if (data.containsKey(FeedbackTracker.CS_NAME)) {
            return (String) data.get(FeedbackTracker.CS_NAME);
        } else {
            return "not found";
        }
    }

    public void setName(String name) {
        if (data.containsKey(FeedbackTracker.CS_NAME)) {
            data.replace(FeedbackTracker.CS_NAME, name);
        } else {
            data.put(FeedbackTracker.CS_NAME, name);
        }
    }

    int getId() {
        if (data.containsKey(FeedbackTracker.CS_ID)) {
            return (int) data.get(FeedbackTracker.CS_ID);
        } else {
            return -1;
        }
    }

    float getGain() {
        if (data.containsKey(FeedbackTracker.CS_FADER)) {
            return (Float) data.get(FeedbackTracker.CS_FADER);
        } else {
            return -999.0f;
        }
    }

    void setGain(float gain) {
        if (data.containsKey(FeedbackTracker.CS_FADER)) {
            data.replace(FeedbackTracker.CS_FADER, gain);
        } else {
            data.put(FeedbackTracker.CS_FADER, gain);
        }
    }

    public void setExpand(Float yn) {
        if (data.containsKey(FeedbackTracker.CS_EXPANDED)) {
            data.replace(FeedbackTracker.CS_EXPANDED, yn);
        } else {
            data.put(FeedbackTracker.CS_EXPANDED, yn);
        }
    }

    public void setRecordEnable(Float yn) {
        if (data.containsKey(FeedbackTracker.CS_RECENABLE)) {
            data.replace(FeedbackTracker.CS_RECENABLE, yn);
        } else {
            data.put(FeedbackTracker.CS_RECENABLE, yn);
        }
    }

    public void setRecordSafe(Float yn) {
        if (data.containsKey(FeedbackTracker.CS_RECSAFE)) {
            data.replace(FeedbackTracker.CS_RECSAFE, yn);
        } else {
            data.put(FeedbackTracker.CS_RECSAFE, yn);
        }
    }

    public void setPSP(Float psp) {
        if (data.containsKey(FeedbackTracker.CS_PAN_STERO_POSITION)) {
            data.replace(FeedbackTracker.CS_PAN_STERO_POSITION, psp);
        } else {
            data.put(FeedbackTracker.CS_PAN_STERO_POSITION, psp);
        }
    }

    public float getMute() {
        if (data.containsKey(FeedbackTracker.CS_MUTE)) {
            return (Float) data.get(FeedbackTracker.CS_MUTE);
        } else {
            return -999.0f;
        }
    }

    public void setMute(Float mute) {
        if (data.containsKey(FeedbackTracker.CS_MUTE)) {
            data.replace(FeedbackTracker.CS_MUTE, mute);
        } else {
            data.put(FeedbackTracker.CS_MUTE, mute);
        }
    }

    public float getSolo() {
        if (data.containsKey(FeedbackTracker.CS_SOLO)) {
            return (Float) data.get(FeedbackTracker.CS_SOLO);
        } else {
            return -999.0f;
        }
    }

    public void setSolo(Float solo) {
        if (data.containsKey(FeedbackTracker.CS_SOLO)) {
            data.replace(FeedbackTracker.CS_SOLO, solo);
        } else {
            data.put(FeedbackTracker.CS_SOLO, solo);
        }
    }

    public String getComment() {
        if (data.containsKey(FeedbackTracker.CS_COMMENT)) {
            return (String) data.get(FeedbackTracker.CS_COMMENT);
        } else {
            return "";
        }
    }

    public void setComment(String comment) {
        if (data.containsKey(FeedbackTracker.CS_COMMENT)) {
            data.replace(FeedbackTracker.CS_COMMENT, comment);
        } else {
            data.put(FeedbackTracker.CS_COMMENT, comment);
        }
    }

    public float getSoloIso() {
        if (data.containsKey(FeedbackTracker.CS_SOLO_ISO)) {
            return (Float) data.get(FeedbackTracker.CS_SOLO_ISO);
        } else {
            return -999.0f;
        }
    }

    public void setSoloIso(Float solo_iso) {
        if (data.containsKey(FeedbackTracker.CS_SOLO_ISO)) {
            data.replace(FeedbackTracker.CS_SOLO_ISO, solo_iso);
        } else {
            data.put(FeedbackTracker.CS_SOLO_ISO, solo_iso);
        }
    }

    public float getSoloSafe() {
        if (data.containsKey(FeedbackTracker.CS_SOLO_SAFE)) {
            return (Float) data.get(FeedbackTracker.CS_SOLO_SAFE);
        } else {
            return -999.0f;
        }
    }

    public void setSoloSafe(Float solo_safe) {
        if (data.containsKey(FeedbackTracker.CS_SOLO_SAFE)) {
            data.replace(FeedbackTracker.CS_SOLO_SAFE, solo_safe);
        } else {
            data.put(FeedbackTracker.CS_SOLO_SAFE, solo_safe);
        }
    }

    public float getPolarity() {
        if (data.containsKey(FeedbackTracker.CS_POLARITY)) {
            return (Float) data.get(FeedbackTracker.CS_POLARITY);
        } else {
            return -999.0f;
        }
    }

    public void setPolarity(Float polarity) {
        if (data.containsKey(FeedbackTracker.CS_POLARITY)) {
            data.replace(FeedbackTracker.CS_POLARITY, polarity);
        } else {
            data.put(FeedbackTracker.CS_POLARITY, polarity);
        }
    }

    public float getMonitorInput() {
        if (data.containsKey(FeedbackTracker.CS_MONITOR_INPUT)) {
            return (Float) data.get(FeedbackTracker.CS_MONITOR_INPUT);
        } else {
            return -999.0f;
        }
    }

    public void setMonitorInput(Float yn) {
        if (data.containsKey(FeedbackTracker.CS_MONITOR_INPUT)) {
            data.replace(FeedbackTracker.CS_MONITOR_INPUT, yn);
        } else {
            data.put(FeedbackTracker.CS_MONITOR_INPUT, yn);
        }
    }

    public float getMonitorDisk() {
        if (data.containsKey(FeedbackTracker.CS_MONITOR_DISK)) {
            return (Float) data.get(FeedbackTracker.CS_MONITOR_DISK);
        } else {
            return -999.0f;
        }
    }

    public void setMonitorDisk(Float yn) {
        if (data.containsKey(FeedbackTracker.CS_MONITOR_DISK)) {
            data.replace(FeedbackTracker.CS_MONITOR_DISK, yn);
        } else {
            data.put(FeedbackTracker.CS_MONITOR_DISK, yn);
        }
    }

    public float getRecEnable() {
        if (data.containsKey(FeedbackTracker.CS_RECENABLE)) {
            return (Float) data.get(FeedbackTracker.CS_RECENABLE);
        } else {
            return -999.0f;
        }
    }

    public float getRecSafe() {
        if (data.containsKey(FeedbackTracker.CS_RECSAFE)) {
            return (Float) data.get(FeedbackTracker.CS_RECSAFE);
        } else {
            return -999.0f;
        }
    }

    public float getExpanded() {
        if (data.containsKey(FeedbackTracker.CS_EXPANDED)) {
            return (Float) data.get(FeedbackTracker.CS_EXPANDED);
        } else {
            return -999.0f;
        }
    }

    public float getTrim() {
        if (data.containsKey(FeedbackTracker.CS_TRIM)) {
            return (Float) data.get(FeedbackTracker.CS_TRIM);
        } else {
            return -999.0f;
        }
    }

    public void setTrim(Float trim) {
        if (data.containsKey(FeedbackTracker.CS_TRIM)) {
            data.replace(FeedbackTracker.CS_TRIM, trim);
        } else {
            data.put(FeedbackTracker.CS_TRIM, trim);
        }
    }

    public float getPanStereoPosition() {
        if (data.containsKey(FeedbackTracker.CS_PAN_STERO_POSITION)) {
            return (Float) data.get(FeedbackTracker.CS_PAN_STERO_POSITION);
        } else {
            return -999.0f;
        }
    }

    public float getNumInputs() {
        if (data.containsKey(FeedbackTracker.CS_NUM_INPUTS)) {
            return (Float) data.get(FeedbackTracker.CS_NUM_INPUTS);
        } else {
            return -999.0f;
        }
    }

    public void setNumInputs(Float n_inputs) {
        if (data.containsKey(FeedbackTracker.CS_NUM_INPUTS)) {
            data.replace(FeedbackTracker.CS_NUM_INPUTS, n_inputs);
        } else {
            data.put(FeedbackTracker.CS_NUM_INPUTS, n_inputs);
        }
    }

    public float getNumOutputs() {
        if (data.containsKey(FeedbackTracker.CS_NUM_OUTPUTS)) {
            return (Float) data.get(FeedbackTracker.CS_NUM_OUTPUTS);
        } else {
            return -999.0f;
        }
    }

    public void setNumOutputs(Float n_outputs) {
        if (data.containsKey(FeedbackTracker.CS_NUM_OUTPUTS)) {
            data.replace(FeedbackTracker.CS_NUM_OUTPUTS, n_outputs);
        } else {
            data.put(FeedbackTracker.CS_NUM_OUTPUTS, n_outputs);
        }
    }
}
