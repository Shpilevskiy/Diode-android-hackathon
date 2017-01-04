package com.example.shpilevskiy.wifidiode.LEDHttp.LEDClient;

/**
 * Created by anders-lokans on 02.01.17.
 */

interface LEDClientInterface {
    void toggleLED() throws LEDClientException;
    Boolean isOn() throws LEDClientException;
    void setBrightnessLevel(int level) throws LEDClientException;
    void setHost(String host);
}
