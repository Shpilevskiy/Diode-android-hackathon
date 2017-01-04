package com.example.shpilevskiy.wifidiode;
import com.example.shpilevskiy.wifidiode.LEDClient.LEDClient;
import com.example.shpilevskiy.wifidiode.LEDClient.LEDClientException;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private int BRIGHTNESS_STEP = 15;

    private static final String HOST = "http://192.168.100.16";
    private static int brightnessCounter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);

        final ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        final Switch lightSwitcher = (Switch) findViewById(R.id.lightSwitcher);
        final EditText ssidText = (EditText) findViewById(R.id.SsidEditText);
        final EditText passwordText = (EditText) findViewById(R.id.PasswordEditText);
        final SeekBar brightnessLevelBar = (SeekBar) findViewById(R.id.brightnessLevelBar);

        final LEDClient ledClient = new LEDClient(HOST);


        try {
            if (ledClient.isOn()) {
                lightSwitcher.setChecked(true);
            }
        } catch (LEDClientException e) {
            // TODO (mrlokans) handle exception appropriately
            e.printStackTrace();
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
                try {
                    ledClient.toggleLED();
                } catch (LEDClientException e) {
                    Log.e("Toggle method", e.toString());
                }
            }
        });

        brightnessLevelBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    if (progress > brightnessCounter + BRIGHTNESS_STEP || progress < brightnessCounter - BRIGHTNESS_STEP) {
                        System.out.println(progress);
                        ledClient.setBrightnessLevel(progress);
                        brightnessCounter = progress;
                    }
                } catch (LEDClientException e) {
                    // TODO (mrlokans) handle exception appropriately
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
