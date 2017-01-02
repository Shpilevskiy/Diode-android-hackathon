package com.example.shpilevskiy.wifidiode.LEDClient;

/**
 * Created by anders-lokans on 02.01.17.
 */

public interface LEDClientInterface {
    void toggleLED();
    Boolean isOn();
    void setBrightnessLevel(int level);
}
