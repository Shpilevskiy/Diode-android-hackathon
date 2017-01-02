package com.example.shpilevskiy.wifidiode;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.example.shpilevskiy.wifidiode.LEDClient.LEDClient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private static final String HOST = "http://192.168.100.16";

    final ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
    final Switch lightSwitcher = (Switch) findViewById(R.id.lightSwitcher);
    final EditText ssidText = (EditText) findViewById(R.id.SsidEditText);
    final EditText passwordText = (EditText) findViewById(R.id.PasswordEditText);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);

        final LEDClient ledClient = new LEDClient(HOST);

        if (ledClient.isOn()) {
            lightSwitcher.setChecked(true);
        }

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    createWifiConnection(ssidText.getText().toString(), passwordText.getText().toString());
                }
            }
        });

        lightSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ledClient.toggleLED();
            }
        });
    }


    public void createWifiConnection(String ssid, String password) {

        Log.d("Wifi conf", "ssid: " + ssid);
        Log.d("Wifi conf", "password: " + password);

        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration conf = new WifiConfiguration();

        conf.SSID = "\"" + ssid + "\"";
        conf.preSharedKey = "\"" + password + "\"";
        conf.status = WifiConfiguration.Status.ENABLED;
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        wifi.disconnect();
        int network = wifi.addNetwork(conf);
        Log.d("WifiPreference", "add Network returned " + network);
        boolean b = wifi.enableNetwork(network, true);
        Log.d("WifiPreference", "enableNetwork returned " + b);
        System.out.println(wifi.getConnectionInfo());
    }
}
