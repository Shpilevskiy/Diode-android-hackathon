package com.example.shpilevskiy.wifidiode.LEDClient;


/**
 * Created by anders-lokans on 02.01.17.
 */

public class LEDClient implements LEDClientInterface {

    private static String TOGGLE_LED_URL = "toggle";
    private static String SET_BRIGHTNESS_URL = "set";
    private static String STATUS_URL = "status";


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