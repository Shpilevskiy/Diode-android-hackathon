package com.example.shpilevskiy.wifidiode;

/**
 * Created by anders-lokans on 02.01.17.
 */

public class LEDClient implements LEDClientInterface {
    private static String host;

    public LEDClient(String host){
        this.host = host;
    }

    public void toggleLED(){

    }

    public void setBrightnessLevel(int level){

    }

    public Boolean isOn(){
        return false;
    }
}