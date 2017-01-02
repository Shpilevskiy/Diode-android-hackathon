package com.example.shpilevskiy.wifidiode;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity {

    private static final String domain = "http://192.168.100.16";
    private static final String lightON = "/toggle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        Switch lightSwitcher = (Switch) findViewById(R.id.lightSwitcher);


        final EditText ssidText = (EditText) findViewById(R.id.SsidEditText);
        final EditText passwordText = (EditText) findViewById(R.id.PasswordEditText);




        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    createWifiConnection(ssidText.getText().toString(), passwordText.getText().toString());
                }
            }
        });

        lightSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toggleLightViaHttp();
                    System.out.println("1");
                } else {
                    System.out.println("bye bye");
                }
            }
        });
    }


    public void createWifiConnection(String ssid, String password) {

        Log.d("Wifi conf", "ssid: " + ssid);
        Log.d("Wifi conf", "password: " + password);

        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration conf = new WifiConfiguration();

        conf.SSID = "\"" + ssid + "\"";
        conf.preSharedKey = "\""+ password +"\"";
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
        Log.d("WifiPreference", "enableNetwork returned " + b );
        System.out.println(wifi.getConnectionInfo());
    }

    public void toggleLightViaHttp() {

    }
}
