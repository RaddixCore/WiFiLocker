package com.raddixcore.android.wifilocker;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LockActivity extends AppCompatActivity {

    EditText etPin;
    Button btnUnlock;
    WifiManager wifiManager;
    boolean authenticated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        etPin = (EditText) findViewById(R.id.edittext_pin);
        btnUnlock = (Button) findViewById(R.id.button_unlock);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = PreferenceManager.getDefaultSharedPreferences(LockActivity.this).getString("key_password", "1234");
                if (etPin.getText().toString().equals(pin)) {
                    //unlock
                    PreferenceManager.getDefaultSharedPreferences(LockActivity.this)
                            .edit()
                            .putBoolean("wifiStatus", wifiManager.isWifiEnabled())
                            .apply();
                    Toast.makeText(LockActivity.this, "PIN accepted", Toast.LENGTH_SHORT).show();
                    authenticated = true;
                    finish();
                } else {
                    etPin.setText("");
                    Toast.makeText(LockActivity.this, "Invalid PIN. Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!authenticated)
            exit();
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        if (getIntent().getBooleanExtra("prevState", false)) {
            wifiManager.setWifiEnabled(true);
        } else {
            wifiManager.setWifiEnabled(false);
        }
        Intent startHomescreen = new Intent(Intent.ACTION_MAIN);
        startHomescreen.addCategory(Intent.CATEGORY_HOME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            startHomescreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(startHomescreen);
        finish();
    }
}