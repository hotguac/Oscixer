package com.joekokosa.oscixer;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.com.joekokosa.oscixer.MESSAGE";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, com.joekokosa.oscixer.R.xml.preferences, false);
        setContentView(com.joekokosa.oscixer.R.layout.activity_main);
    }

    /**
     * Called when the user clicks the Send button
     */
    public void displayControls(View view) {
        Intent intent = new Intent(this, ControlActivity.class);
        String message = ""; //editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }

}
