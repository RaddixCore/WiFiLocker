package com.raddixcore.android.wifilocker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;
    TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = (TextView) findViewById(R.id.textview_status);

        boolean isFirstRun = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("key_is_first_run", true);
        if (isFirstRun) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Initial Setup");
            builder.setMessage("Welcome to Wifi Locker. Please Enter A Numeric Password For Protecting Your Wifi Switch");
            final View view = View.inflate(this, R.layout.input_alert_layout, null);
            final EditText etPassword = (EditText) view.findViewById(R.id.edittext_1);
            etPassword.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD|InputType.TYPE_CLASS_NUMBER);
            etPassword.setHint("Enter PIN");
            builder.setView(view);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String password = etPassword.getText().toString();
                    if (!password.trim().isEmpty()) {
                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit()
                                .putString("key_password", password)
                                .putBoolean("key_is_first_run", false)
                                .apply();
                    } else {
                        Toast.makeText(MainActivity.this, "You did not enter a password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.show();
        }

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean("wifiStatus", true)
                    .apply();
        } else {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean("wifiStatus", false)
                    .apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("key_wifi_lock", true)){
            tvStatus.setText("Relax! Your wifi switch is protected :-)");
        } else {
            tvStatus.setText("Your wifi switch is not protected :-(");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            final String password = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("key_password", null);
            if (password == null) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Security Check");
            builder.setMessage("Enter your password to settings");
            View view = View.inflate(MainActivity.this, R.layout.input_alert_layout, null);
            builder.setView(view);
            builder.setPositiveButton("Ok", null);
            builder.setNegativeButton("Cancel", null);
            final EditText etPassword = (EditText) view.findViewById(R.id.edittext_1);
            etPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            etPassword.setHint("Enter PIN");
            final AlertDialog alert = builder.create();
            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button b = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (etPassword.getText().toString().equals(password)) {
                                alert.dismiss();
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                            } else {
                                alert.setMessage("Invalid PIN. Try again");
                                alert.show();
                            }

                        }
                    });

                }
            });
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
