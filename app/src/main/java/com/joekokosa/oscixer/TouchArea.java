package com.joekokosa.oscixer;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

/**
 * Created by jkokosa on 11/23/16.
 */

class TouchArea implements View.OnTouchListener {

    private final ControlActivity controlActivity;
    private boolean first_point = true;
    private float lastX = 0.0f;
    private float lastY = 0.0f;
    private float last_value;
    private float next_value;
    private float valueChange = 0.0f;
    private float controlScale = 1.0f;
    private float minAdjust = 0.1f;
    private VelocityTracker mVelocityTracker = null;

    public TouchArea(ControlActivity controlActivity) {
        this.controlActivity = controlActivity;
    }

    // TODO: handle multi-touch
    /*
       Skip the positions in the event historical functions to keep the UI responsive.
    */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();
        float velocity_scale;

        float posX;
        float posY;

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
                switch (controlActivity.current_mode) {
                    case ControlActivity.MODE_FADER:
                        last_value = controlActivity.fader;
                        controlScale = 1.0f;
                        minAdjust = 0.1f;
                        break;
                    case ControlActivity.MODE_PAN:
                        last_value = controlActivity.pan_stereo_position;
                        controlScale = 0.08f;
                        minAdjust = 0.01f;
                        break;
                    case ControlActivity.MODE_TRIM:
                        last_value = controlActivity.trim;
                        controlScale = 0.8f;
                        minAdjust = 0.1f;
                        break;
                }

                next_value = last_value;
                break;
            case MotionEvent.ACTION_MOVE:

                mVelocityTracker.addMovement(event);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocity = mVelocityTracker.getXVelocity();
                //Log.d("--velocity--", Float.toString(velocity));

                velocity_scale = getVelocity_scale(velocity);

                //Log.d("--scale--", Float.toString(velocity_scale));

                for (int i = 0; i < event.getHistorySize(); i++) {
                    posX = event.getHistoricalAxisValue(0, i);
                    posY = event.getHistoricalAxisValue(1, i);
                    calcFaderChange(posX, posY, velocity_scale, v);
                }

                posX = event.getAxisValue(0);
                posY = event.getAxisValue(1);
                calcFaderChange(posX, posY, velocity_scale, v);

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

    private void calcFaderChange(float posX, float posY, float velocity_scale, View v) {
        float avgY;
        float y_scale = 1.0f;

        int myWidth = 1000;
        int left;
        int right;
        float deltaX = 0.0f;

        float maxY = v.getTop();
        float minY = v.getBottom();

        if (first_point) {
            first_point = false;
            lastX = posX;
            lastY = posY;
            valueChange = 0.0f;
        } else {
            deltaX = posX - lastX;
            avgY = (lastY + posY) / 2.0f;
            y_scale = (((avgY - minY) / (maxY - minY)) * (4.0f - 0.04f)) + 0.04f;
            lastX = posX;
            lastY = posY;

            left = v.getLeft();
            right = v.getRight();

            myWidth = right - left;

            valueChange += velocity_scale * y_scale * (deltaX / myWidth) * controlScale;

        }
        next_value += valueChange;

        if (Math.abs(next_value - last_value) > minAdjust) {
            //Log.d("--Touch Change--", Float.toString(valueChange) + " " + Float.toString(velocity_scale) + " " + Float.toString(y_scale) + " " + Float.toString(deltaX) + " " + Integer.toString(myWidth) + " " + Float.toString(controlScale));

            switch (controlActivity.current_mode) {
                case ControlActivity.MODE_FADER:
                    ControlActivity.controller.moveFader(controlActivity.selected_strip, next_value);
                    break;
                case ControlActivity.MODE_PAN:
                    ControlActivity.controller.movePan(controlActivity.selected_strip, next_value);
                    break;
                case ControlActivity.MODE_TRIM:
                    ControlActivity.controller.moveTrim(controlActivity.selected_strip, next_value);
                    break;
            }

            last_value = next_value;
            valueChange = 0.0f;
        }
    }

    private float getVelocity_scale(float velocity) {
        float velocity_scale;
        float v_sq = velocity * velocity;

        if (v_sq < 5000) {
            velocity_scale = 0.125f;
        } else if (v_sq < 80000) {
            velocity_scale = 0.25f;
        } else if (v_sq < 800000) {
            velocity_scale = 1.0f;
        } else if (v_sq < 1000000) {
            velocity_scale = 2.0f;
        } else {
            velocity_scale = 4.0f;
        }

        return velocity_scale;
    }
}
