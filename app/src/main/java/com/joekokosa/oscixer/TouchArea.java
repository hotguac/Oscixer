package com.joekokosa.oscixer;

import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

/**
 * Created by jkokosa on 11/23/16.
 */

class TouchArea implements View.OnTouchListener {

    private ControlActivity controlActivity;
    private VelocityTracker mVelocityTracker = null;

    public TouchArea(ControlActivity controlActivity) {
        this.controlActivity = controlActivity;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // ... Respond to touch events
        int index = event.getActionIndex();
        int action = event.getActionMasked();

        int mWidth = v.getLeft() - v.getRight();
        int mHeight = v.getTop() - v.getBottom();
        boolean first_point = true;
        float deltaX;
        float lastX = 0.0f;
        float lastY = 0.0f;
        float posX;
        float posY;
        float avgY;
        float faderChange = 0.0f;
        float velocity_scale = 1.0f;
        float y_scale = 1.0f;
        float maxY = v.getTop();
        float minY = v.getBottom();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);
                first_point = true;
                controlActivity.last_fader = controlActivity.fader;
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mVelocityTracker.computeCurrentVelocity(1000);
                // Log velocity of pixels per second
                // Best practice to use VelocityTrackerCompat where possible.
                // TODO: handle multi-touch
                int nhist = event.getHistorySize();

                //Log.d("Fader change", String.format("num hist = %d", nhist));
                for (int idx = 0; idx < nhist; idx++) {
                    posX = event.getHistoricalAxisValue(0, idx);
                    posY = event.getHistoricalAxisValue(1, idx);

                    if (first_point) {
                        first_point = false;
                        lastX = posX;
                        lastY = posY;
                    } else {
                        deltaX = posX - lastX;
                        avgY = (lastY + posY) / 2.0f;
                        y_scale = (avgY - minY) / (maxY - minY) * (20.0f - 0.5f) + 0.5f;
                        faderChange += velocity_scale * y_scale * (deltaX / mWidth);
                        if ((faderChange > 0.05f) || (faderChange < -0.05f)) {
                            Log.d("Fader change", String.format("%f deltaX =%f avgY = %f y_scale = %f lastfader = %f", faderChange, deltaX, avgY, y_scale, controlActivity.last_fader));
                            controlActivity.last_fader += faderChange;
                            controlActivity.controller.moveFader(controlActivity.selected_strip, controlActivity.last_fader);
                            faderChange = 0.0f;
                        }
                        lastX = posX;
                        lastY = posY;
                    }
                }

                posX = event.getAxisValue(0);
                posY = event.getAxisValue(1);

                if (first_point) {
                    first_point = false;
                    lastX = posX;
                    lastY = posY;
                } else {
                    deltaX = posX - lastX;
                    avgY = (lastY + posY) / 2.0f;
                    y_scale = (avgY - minY) / (maxY - minY) * (20.0f - 0.5f) + 0.5f;
                    faderChange += velocity_scale * y_scale * (deltaX / mWidth);
                    if ((faderChange > 0.05f) || (faderChange < -0.05f)) {
                        Log.d("Fader change", String.format("%f deltaX =%f avgY = %f y_scale = %f lastfader = %f", faderChange, deltaX, avgY, y_scale, controlActivity.last_fader));
                        controlActivity.last_fader += faderChange;
                        controlActivity.controller.moveFader(controlActivity.selected_strip, controlActivity.last_fader);
                        faderChange = 0.0f;
                    }
                    lastX = posX;
                    lastY = posY;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                mVelocityTracker.recycle();
                mVelocityTracker = null;

                first_point = false;
                break;
        }

        return true;
    }
}
